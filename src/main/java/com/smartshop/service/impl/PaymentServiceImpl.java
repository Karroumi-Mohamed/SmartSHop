package com.smartshop.service.impl;

import com.smartshop.dto.request.PaymentCreateRequest;
import com.smartshop.dto.response.PaymentResponse;
import com.smartshop.entities.Order;
import com.smartshop.entities.Payment;
import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.exception.BusinessRuleException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.PaymentMapper;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    private static final BigDecimal ESPECES_LIMIT = new BigDecimal("20000");

    @Override
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        // Validate payment amount
        if (request.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessRuleException("Payment amount exceeds remaining amount. Remaining: " + order.getRemainingAmount());
        }

        // Validate ESPECES limit
        if (request.getPaymentMethod() == PaymentMethod.ESPECES && request.getAmount().compareTo(ESPECES_LIMIT) > 0) {
            throw new BusinessRuleException("Cash payment cannot exceed 20,000 DH (Legal limit: Art. 193 CGI)");
        }

        // Calculate payment number
        int paymentNumber = order.getPayments().size() + 1;

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setPaymentNumber(paymentNumber);
        payment.setPaymentDate(LocalDateTime.now());

        // Set status based on payment method
        if (request.getPaymentMethod() == PaymentMethod.ESPECES) {
            payment.setStatus(PaymentStatus.ENCAISSE);
            payment.setEncashmentDate(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.EN_ATTENTE);
        }

        Payment saved = paymentRepository.save(payment);

        // Update order remaining amount
        order.setRemainingAmount(order.getRemainingAmount().subtract(request.getAmount()));
        order.addPayment(saved);
        orderRepository.save(order);

        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return paymentRepository.findByOrder(order).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getPaymentMethod() == PaymentMethod.ESPECES) {
            throw new BusinessRuleException("Cannot modify status of ESPECES payment");
        }

        payment.setStatus(status);

        if (status == PaymentStatus.ENCAISSE) {
            payment.setEncashmentDate(LocalDateTime.now());
        }

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponse(updated);
    }
}
