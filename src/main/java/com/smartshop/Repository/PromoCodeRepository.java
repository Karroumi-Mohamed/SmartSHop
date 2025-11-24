package com.smartshop.Repository;

import com.smartshop.Entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCode(String code);

    Boolean existsByCode(String code);

    List<PromoCode> findByActiveTrue();

    List<PromoCode> findByUsedFalseAndActiveTrue();
}
