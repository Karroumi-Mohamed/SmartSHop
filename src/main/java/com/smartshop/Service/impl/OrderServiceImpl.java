package com.smartshop.Service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartshop.DTO.Request.OrderCreateRequest;
import com.smartshop.DTO.Request.OrderItemRequest;
import com.smartshop.DTO.Response.OrderResponse;
import com.smartshop.Entity.Client;
import com.smartshop.Entity.Order;
import com.smartshop.Entity.OrderItem;
import com.smartshop.Entity.Payment;
import com.smartshop.Entity.Product;
import com.smartshop.Entity.PromoCode;
import com.smartshop.Enums.ClientLevel;
import com.smartshop.Enums.OrderStatus;
import com.smartshop.Enums.PaymentStatus;
import com.smartshop.Exception.BusinessException;
import com.smartshop.Exception.InsufficientStockException;
import com.smartshop.Exception.ResourceNotFoundException;
import com.smartshop.Mapper.OrderMapper;
import com.smartshop.Repository.ClientRepository;
import com.smartshop.Repository.OrderRepository;
import com.smartshop.Repository.PaymentRepository;
import com.smartshop.Repository.ProductRepository;
import com.smartshop.Service.OrderService;
import com.smartshop.Service.PromoCodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final PromoCodeService promoCodeService;
    private final OrderMapper orderMapper;

    private static final BigDecimal TVA_RATE = new BigDecimal("20.00");

    @Override
    public OrderResponse create(OrderCreateRequest request) {
        // Get client
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTvaRate(TVA_RATE);

        BigDecimal subTotal = BigDecimal.ZERO;

        // Process each item
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemRequest.getProductId()));

            // Check stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(),
                        product.getStock(),
                        itemRequest.getQuantity());
            }

            // Calculate line total
            BigDecimal lineTotal = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .lineTotal(lineTotal)
                    .build();

            order.getOrderItems().add(orderItem);
            subTotal = subTotal.add(lineTotal);
        }

        order.setSubTotal(subTotal);

        BigDecimal discountPercentage = calculateDiscount(client, subTotal);

        PromoCode promoCode = null;
        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            promoCode = promoCodeService.validate(request.getPromoCode());
            discountPercentage = discountPercentage.add(promoCode.getDiscountPercentage());
            order.setPromoCode(promoCode);
        }

        order.setDiscountPercentage(discountPercentage);

        BigDecimal discountAmount = subTotal.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        order.setDiscountAmount(discountAmount);

        BigDecimal amountAfterDiscount = subTotal.subtract(discountAmount);
        order.setAmountAfterDiscount(amountAfterDiscount);

        BigDecimal tvaAmount = amountAfterDiscount.multiply(TVA_RATE)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        order.setTvaAmount(tvaAmount);

        BigDecimal totalTTC = amountAfterDiscount.add(tvaAmount);
        order.setTotalTTC(totalTTC);
        order.setRemainingAmount(totalTTC);

        if (promoCode != null) {
            promoCode.setUsed(true);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderMapper.toResponseList(orderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByClientId(Long clientId) {
        return orderMapper.toResponseList(
                orderRepository.findByClientIdOrderByOrderDateDesc(clientId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByStatus(OrderStatus status) {
        return orderMapper.toResponseList(orderRepository.findByStatus(status));
    }

    @Override
    public OrderResponse confirm(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be confirmed");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Order not fully paid. Remaining: " + order.getRemainingAmount() + " DH");
        }

        boolean hasPendingPayments = paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PENDING);
        if (hasPendingPayments) {
            throw new BusinessException("All payments must be cashed before confirmation");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                order.setStatus(OrderStatus.REJECTED);
                orderRepository.save(order);
                throw new InsufficientStockException(
                        product.getName(),
                        product.getStock(),
                        item.getQuantity());
            }
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        Client client = order.getClient();
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(order.getTotalTTC()));
        client.setLastOrderDate(LocalDateTime.now());
        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(LocalDateTime.now());
        }

        ClientLevel newLevel = ClientLevel.calculateLevel(
                client.getTotalOrders(),
                client.getTotalSpent());
        client.setLevel(newLevel);
        clientRepository.save(client);

        order.setStatus(OrderStatus.CONFIRMED);
        Order confirmedOrder = orderRepository.save(order);
        return orderMapper.toResponse(confirmedOrder);
    }

    @Override
    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be canceled");
        }

        boolean hasCashedPayments = paymentRepository.existsByOrderAndStatus(order, PaymentStatus.CASHED);
        if (hasCashedPayments) {
            throw new BusinessException(
                    "Cannot cancel order with cashed payments. Please process refund manually.");
        }

        List<Payment> pendingPayments = paymentRepository.findByOrderAndStatus(order, PaymentStatus.PENDING);
        for (Payment payment : pendingPayments) {
            payment.setStatus(PaymentStatus.CANCELED);
            paymentRepository.save(payment);
        }

        if (order.getPromoCode() != null) {
            order.getPromoCode().setUsed(false);
        }

        order.setStatus(OrderStatus.CANCELED);
        Order canceledOrder = orderRepository.save(order);
        return orderMapper.toResponse(canceledOrder);
    }

    private BigDecimal calculateDiscount(Client client, BigDecimal subTotal) {
        ClientLevel level = client.getLevel();

        return switch (level) {
            case PLATINUM -> {
                if (subTotal.compareTo(new BigDecimal("1200")) >= 0) {
                    yield new BigDecimal("15.00");
                }
                yield BigDecimal.ZERO;
            }
            case GOLD -> {
                if (subTotal.compareTo(new BigDecimal("800")) >= 0) {
                    yield new BigDecimal("10.00");
                }
                yield BigDecimal.ZERO;
            }
            case SILVER -> {
                if (subTotal.compareTo(new BigDecimal("500")) >= 0) {
                    yield new BigDecimal("5.00");
                }
                yield BigDecimal.ZERO;
            }
            case BASIC -> BigDecimal.ZERO;
        };
    }
}
