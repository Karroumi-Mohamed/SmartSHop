package com.smartshop.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartshop.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalse();

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByStockLessThanAndDeletedFalse(Integer stockIsLessThan);
}
