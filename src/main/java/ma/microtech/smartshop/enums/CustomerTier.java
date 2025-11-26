package ma.microtech.smartshop.enums;

import java.math.BigDecimal;

public enum CustomerTier {
    BASIC(0),
    SILVER(5),
    GOLD(10),
    PLATINUM(15);

    private final BigDecimal discountPercentage;

    CustomerTier(int discountPercentage){
        this.discountPercentage = BigDecimal.valueOf(discountPercentage);
    }

    public BigDecimal getDiscountPercentage(){
        return discountPercentage;
    }
}
