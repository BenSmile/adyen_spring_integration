package cd.bensmile.checkout;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;

public class Config {

    @Bean
    public LayoutDialect layoutDialect(){
        return new LayoutDialect();
    }
}
