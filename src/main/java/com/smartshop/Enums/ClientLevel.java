package com.smartshop.Enums;

import java.math.BigDecimal;

public enum ClientLevel {
    BASIC,
    SILVER,
    GOLD,
    PLATINUM;

    public static ClientLevel calculateLevel(int totalOrder, BigDecimal totalSpent) {
        if (totalOrder >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return PLATINUM;
        }
        if (totalOrder >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return GOLD;
        }
        if (totalOrder >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return SILVER;
        }
        return BASIC;
    }
}
