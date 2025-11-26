package ma.microtech.smartshop.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PromoCodeServiceImpl {
    private final Map<String, BigDecimal> promoCodes = new ConcurrentHashMap<>();
    private final Set<String> usedCodes = ConcurrentHashMap.newKeySet();

    @PostConstruct
    private void init(){
        promoCodes.put("PROMO-2025", BigDecimal.valueOf(10));
        promoCodes.put("PROMO-WOW1", BigDecimal.valueOf(10));
        promoCodes.put("PROMO-XTRA", BigDecimal.valueOf(5));
        promoCodes.put("PROMO-TEST99", BigDecimal.valueOf(15));
        promoCodes.put("PROMO-GOLD", BigDecimal.valueOf(20));
    }

    public boolean isValidFormat(String code){
        return code != null && code.matches("PROMO-[A-Z0-9]{4}");
    }

    public boolean isUnused(String code){
        return code != null && promoCodes.containsKey(code) && usedCodes.add(code);
    }

    public BigDecimal getDiscountPercent(String code){
        return promoCodes.getOrDefault(code, BigDecimal.ZERO);
    }

}
