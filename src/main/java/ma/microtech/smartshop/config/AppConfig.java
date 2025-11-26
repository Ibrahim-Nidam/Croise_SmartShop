package ma.microtech.smartshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private BigDecimal tvaRate = BigDecimal.valueOf(0.2);

    public BigDecimal getTvaRate() {
        return tvaRate;
    }
    public void setTvaRate(BigDecimal tvaRate){
        this.tvaRate = tvaRate;
    }
}
