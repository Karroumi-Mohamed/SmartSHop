package com.smartshop.Service.impl;

import com.smartshop.Entity.PromoCode;
import com.smartshop.Exception.BusinessException;
import com.smartshop.Exception.DuplicateResourceException;
import com.smartshop.Exception.ResourceNotFoundException;
import com.smartshop.Repository.PromoCodeRepository;
import com.smartshop.Service.PromoCodeService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;

    @Override
    public PromoCode create(String code) {
        if (!code.matches("^PROMO-[A-Z0-9]{4}$")) {
            throw new BusinessException("Promo code format is invalid. It should match 'PROMO-XXXX'.");
        }

        if (promoCodeRepository.existsByCode(code)) {
            throw new DuplicateResourceException("PromoCode", "code", code);
        }

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .discountPercentage(new BigDecimal("5.00"))
                .used(false)
                .active(true)
                .build();
        return promoCodeRepository.save(promoCode);
    }

    @Override
    @Transactional(readOnly = true)
    public PromoCode findByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode not found: " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCode> findAllActive() {
        return promoCodeRepository.findByActiveTrue();
    }

    @Override
    public List<PromoCode> findAll() {
        return promoCodeRepository.findAll();
    }

    @Override
    public void delete(String code) {
        PromoCode promoCode = findByCode(code);
        promoCodeRepository.delete(promoCode);
    }

    @Override
    public PromoCode validate(String code) {
        PromoCode promoCode = findByCode(code);
        if (!promoCode.getActive()) {
            throw new BusinessException("Promo code is not active: " + code);
        }

        if (!promoCode.getUsed()) {
            throw new BusinessException("Promo code has already been used: " + code);
        }

        if (promoCode.getExpiryDate() != null && promoCode.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Promo code has expired: " + code);
        }
        return promoCode;
    }

}
