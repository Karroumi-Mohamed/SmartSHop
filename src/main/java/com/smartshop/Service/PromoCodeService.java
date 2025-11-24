package com.smartshop.Service;

import java.util.List;

import com.smartshop.Entity.PromoCode;

public interface PromoCodeService {
    PromoCode create(String code);

    PromoCode findByCode(String code);

    List<PromoCode> findAll();

    List<PromoCode> findAllActive();

    void delete(String code);

    PromoCode validate(String code);
}
