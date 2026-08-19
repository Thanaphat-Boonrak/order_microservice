package com.thanaphat2005.food.ordering.system.payment.service.domain;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.thanaphat2005.food.ordering.system.payment.service.dataacess")
@EntityScan(basePackages = "com.thanaphat2005.food.ordering.system.payment.service.dataacess")
@SpringBootApplication(scanBasePackages = "com.thanaphat2005.food.ordering.system")

public class PaymentServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PaymentServiceApplication.class)
                .run(args);
    }
}
