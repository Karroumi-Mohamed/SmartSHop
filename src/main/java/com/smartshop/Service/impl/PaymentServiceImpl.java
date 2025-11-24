package com.smartshop.Service.impl;

import com.smartshop.DTO.Request.PaymentCreateRequest;
import com.smartshop.DTO.Response.PaymentResponse;
import com.smartshop.Entity.Order;
import com.smartshop.Entity.Payment;
import com.smartshop.Enums.OrderStatus;
import com.smartshop.Enums.PaymentMethod;
import com.smartshop.Enums.PaymentStatus;
import com.smartshop.Exception.BusinessException;
import com.smartshop.Exception.ResourceNotFoundException;
import com.smartshop.Mapper.PaymentMapper;
import com.smartshop.Repository.OrderRepository;
import com.smartshop.Repository.PaymentRepository;
import com.smartshop.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    private static final BigDecimal CASH_LIMIT = new BigDecimal("20000.00");

    @Override
    public PaymentResponse create(PaymentCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Can only add payments to PENDING orders");
        }

        if (request.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessException(
                    "Payment amount exceeds remaining amount. Remaining: " + order.getRemainingAmount() + " DH");
        }

        if (request.getPaymentMethod() == PaymentMethod.ESPECES &&
                request.getAmount().compareTo(CASH_LIMIT) > 0) {
            throw new BusinessException("Cash payments cannot exceed " + CASH_LIMIT + " DH");
        }

        validatePaymentMethodFields(request);

        Integer paymentNumber = paymentRepository.countByOrderId(order.getId()) + 1;

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setPaymentNumber(paymentNumber);

        if (request.getPaymentMethod() == PaymentMethod.ESPECES) {
            payment.setStatus(PaymentStatus.CASHED);
            payment.setEncashmentDate(LocalDate.now());
        } else {
            payment.setStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);

        order.setRemainingAmount(order.getRemainingAmount().subtract(request.getAmount()));
        orderRepository.save(order);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentMapper.toResponseList(paymentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByOrderId(Long orderId) {
        return paymentMapper.toResponseList(
                paymentRepository.findByOrderIdOrderByPaymentNumberAsc(orderId));
    }

    @Override
    public PaymentResponse markAsCashed(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Only PENDING payments can be marked as cashed");
        }

        payment.setStatus(PaymentStatus.CASHED);
        payment.setEncashmentDate(LocalDate.now());

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    public PaymentResponse markAsRejected(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Only PENDING payments can be rejected");
        }

        Order order = payment.getOrder();
        order.setRemainingAmount(order.getRemainingAmount().add(payment.getAmount()));
        orderRepository.save(order);

        payment.setStatus(PaymentStatus.REJECTED);

        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(updatedPayment);
    }

    private void validatePaymentMethodFields(PaymentCreateRequest request) {
        switch (request.getPaymentMethod()) {
            case ESPECES -> {
                if (request.getReceiptNumber() == null || request.getReceiptNumber().isEmpty()) {
                    throw new BusinessException("Receipt number is required for cash payments");
                }
            }
            case CHEQUE -> {
                if (request.getChequeNumber() == null || request.getChequeNumber().isEmpty()) {
                    throw new BusinessException("Cheque number is required");
                }
                if (request.getBankName() == null || request.getBankName().isEmpty()) {
                    throw new BusinessException("Bank name is required for cheque");
                }
                if (request.getChequeDueDate() == null) {
                    throw new BusinessException("Cheque due date is required");
                }
            }
            case VIREMENT -> {
                if (request.getTransferReference() == null || request.getTransferReference().isEmpty()) {
                    throw new BusinessException("Transfer reference is required");
                }
                if (request.getBankName() == null || request.getBankName().isEmpty()) {
                    throw new BusinessException("Bank name is required for transfer");
                }
            }
        }
    }
}
