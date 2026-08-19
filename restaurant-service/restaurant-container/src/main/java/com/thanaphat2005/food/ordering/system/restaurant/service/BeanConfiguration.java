package com.thanaphat2005.food.ordering.system.restaurant.service;


import com.thanaphat2005.food.ordering.system.payment.service.domain.RestaurantDomainService;
import com.thanaphat2005.food.ordering.system.payment.service.domain.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {


    @Bean
    public RestaurantDomainService restaurantDomainService(){
        return new RestaurantDomainServiceImpl();
    }
}
