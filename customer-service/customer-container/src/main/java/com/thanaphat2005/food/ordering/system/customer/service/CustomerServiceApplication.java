package com.thanaphat2005.food.ordering.system.customer.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.thanaphat2005.food.ordering.system.customer.service.dataaccess" , "com.thanaphat2005.food.ordering.system.dataaccess"})
@EnableJpaRepositories(basePackages = {"com.thanaphat2005.food.ordering.system.customer.service.dataaccess" , "com.thanaphat2005.food.ordering.system.dataaccess" })
@SpringBootApplication(scanBasePackages = "com.thanaphat2005.food.ordering.system")
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
