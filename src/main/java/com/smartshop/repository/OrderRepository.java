package com.smartshop.repository;

import com.smartshop.entities.Order;
import com.smartshop.entities.Client;
import com.smartshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClient(Client client);
    List<Order> findByClientAndStatus(Client client, OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.client = :client AND o.status = 'CONFIRMED'")
    int countConfirmedOrdersByClient(Client client);
}
