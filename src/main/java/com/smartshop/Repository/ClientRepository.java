package com.smartshop.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartshop.Entity.Client;
import com.smartshop.Enums.ClientLevel;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Client> findByUserId(Long userId);

    List<Client> findByLevel(ClientLevel level);
}
