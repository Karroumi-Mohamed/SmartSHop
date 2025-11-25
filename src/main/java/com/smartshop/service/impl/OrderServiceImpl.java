package com.smartshop.service.impl;

import com.smartshop.dto.request.OrderCreateRequest;
import com.smartshop.dto.request.OrderItemRequest;
import com.smartshop.dto.response.OrderResponse;
import com.smartshop.entities.*;
import com.smartshop.enums.CustomerTier;
import com.smartshop.enums.OrderStatus;
import com.smartshop.exception.BusinessRuleException;
import com.smartshop.exception.InsufficientStockException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.OrderMapper;
import com.smartshop.repository.*;
import com.smartshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final OrderMapper orderMapper;

    @Value("${smartshop.vat-rate:0.20}")
    private BigDecimal vatRate;

    @Override
    public OrderResponse createOrder(OrderCreateRequest request) {
        // Validate client
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));

        // Validate stock availability
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getDeleted()) {
                throw new BusinessRuleException("Product is no longer available: " + product.getName());
            }

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName() +
                                ". Available: " + product.getStock() + ", Requested: " + itemRequest.getQuantity());
            }

            BigDecimal lineTotal = product.getPriceExclTax()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPriceExclTax())
                    .lineTotal(lineTotal)
                    .build();

            orderItems.add(orderItem);
            subTotal = subTotal.add(lineTotal);
        }

        // Calculate discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        String promoCode = request.getPromoCode();

        // Apply loyalty discount
        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client.getTier(), subTotal);
        if (loyaltyDiscount.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = discountAmount.add(loyaltyDiscount);
        }

        // Apply promo code discount
        if (promoCode != null && !promoCode.isEmpty()) {
            PromoCode promo = promoCodeRepository.findByCode(promoCode)
                    .orElseThrow(() -> new BusinessRuleException("Invalid promo code: " + promoCode));

            if (!promo.getActive()) {
                throw new BusinessRuleException("Promo code is not active: " + promoCode);
            }

            if (promo.hasBeenUsedBy(client.getId())) {
                throw new BusinessRuleException("Promo code already used by this client");
            }

            BigDecimal promoDiscount = subTotal.multiply(promo.getDiscountPercentage().divide(BigDecimal.valueOf(100)))
                    .setScale(2, RoundingMode.HALF_UP);
            discountAmount = discountAmount.add(promoDiscount);

            promo.markAsUsedBy(client.getId());
            promoCodeRepository.save(promo);
        }

        // Calculate totals
        BigDecimal amountAfterDiscount = subTotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal vatAmount = amountAfterDiscount.multiply(vatRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInclTax = amountAfterDiscount.add(vatAmount).setScale(2, RoundingMode.HALF_UP);

        // Create order
        Order order = Order.builder()
                .client(client)
                .orderDate(LocalDateTime.now())
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .vatAmount(vatAmount)
                .totalInclTax(totalInclTax)
                .promoCode(promoCode)
                .status(OrderStatus.PENDING)
                .remainingAmount(totalInclTax)
                .build();

        // Add items to order
        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        // Decrement stock
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    private BigDecimal calculateLoyaltyDiscount(CustomerTier tier, BigDecimal subTotal) {
        BigDecimal discountPercentage = BigDecimal.ZERO;
        BigDecimal threshold = BigDecimal.ZERO;

        switch (tier) {
            case SILVER:
                threshold = new BigDecimal("500");
                discountPercentage = new BigDecimal("5");
                break;
            case GOLD:
                threshold = new BigDecimal("800");
                discountPercentage = new BigDecimal("10");
                break;
            case PLATINUM:
                threshold = new BigDecimal("1200");
                discountPercentage = new BigDecimal("15");
                break;
            default:
                return BigDecimal.ZERO;
        }

        if (subTotal.compareTo(threshold) >= 0) {
            return subTotal.multiply(discountPercentage.divide(BigDecimal.valueOf(100)))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        return orderRepository.findByClient(client).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be confirmed");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("Order must be fully paid before confirmation. Remaining: " + order.getRemainingAmount());
        }

        order.setStatus(OrderStatus.CONFIRMED);

        // Update client statistics
        Client client = order.getClient();
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(order.getTotalInclTax()));

        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(order.getOrderDate());
        }
        client.setLastOrderDate(order.getOrderDate());

        // Update tier
        CustomerTier newTier = calculateTier(client.getTotalOrders(), client.getTotalSpent());
        client.setTier(newTier);

        clientRepository.save(client);
        Order confirmed = orderRepository.save(order);
        return orderMapper.toResponse(confirmed);
    }

    private CustomerTier calculateTier(Integer totalOrders, BigDecimal totalSpent) {
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        } else if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        } else if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }
        return CustomerTier.BASIC;
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be canceled");
        }

        order.setStatus(OrderStatus.CANCELED);

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        Order canceled = orderRepository.save(order);
        return orderMapper.toResponse(canceled);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.REJECTED) {
            throw new BusinessRuleException("Cannot modify order with final status: " + order.getStatus());
        }

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }
}
