package com.smartshop.Repository;

import com.smartshop.Entity.Order;
import com.smartshop.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByClientId(Long clientId);

    List<Order> findByClientIdOrderByOrderDateDesc(Long clientId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByOrderDateBetween(LocalDateTime orderDateAfter, LocalDateTime orderDateBefore);

    Integer countByClientId(Long clientId);
}
