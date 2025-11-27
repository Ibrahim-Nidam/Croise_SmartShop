package ma.microtech.smartshop.enums;

import java.math.BigDecimal;

public enum CustomerTier {
    BASIC(0, BigDecimal.ZERO),
    SILVER(5, BigDecimal.valueOf(500)),
    GOLD(10, BigDecimal.valueOf(800)),
    PLATINUM(15, BigDecimal.valueOf(1200));

    private final int discountPercent;
    private final BigDecimal minAmountForDiscount;

    CustomerTier(int discountPercent, BigDecimal minAmountForDiscount) {
        this.discountPercent = discountPercent;
        this.minAmountForDiscount = minAmountForDiscount;
    }

    public BigDecimal getDiscountPercent(BigDecimal orderSousTotalHT) {
        if (this == BASIC) return BigDecimal.ZERO;
        if (orderSousTotalHT.compareTo(minAmountForDiscount) >= 0) {
            return BigDecimal.valueOf(discountPercent);
        }
        return BigDecimal.ZERO;
    }
}