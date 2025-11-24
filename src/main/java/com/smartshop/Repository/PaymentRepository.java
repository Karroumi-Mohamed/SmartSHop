package com.smartshop.Repository;

import com.smartshop.Entity.Order;
import com.smartshop.Entity.Payment;
import com.smartshop.Enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByOrderIdOrderByPaymentNumberAsc(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByOrderAndStatus(Order order, PaymentStatus status);

    Boolean existsByOrderAndStatus(Order order, PaymentStatus status);

    Optional<Payment> findTopByOrderOrderByPaymentNumberDesc(Order order);

    Integer countByOrderId(Long orderId);
}
