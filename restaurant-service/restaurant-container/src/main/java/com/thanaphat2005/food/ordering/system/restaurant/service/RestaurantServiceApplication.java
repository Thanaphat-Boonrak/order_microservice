package com.thanaphat2005.food.ordering.system.restaurant.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.thanaphat2005.food.ordering.system.restaurant.service.dataaccess.restaurant" , "com.thanaphat2005.food.ordering.system.dataaccess.restaurant"})
@EnableJpaRepositories(basePackages = {"com.thanaphat2005.food.ordering.system.restaurant.service.dataaccess.restaurant" , "com.thanaphat2005.food.ordering.system.dataaccess.restaurant" })
@SpringBootApplication(scanBasePackages = "com.thanaphat2005.food.ordering.system")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
