package com.thanaphat2005.food.ordering.system.order.service;


import com.thanaphat2005.food.ordering.system.domain.exception.DomainException;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderCommand;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;


    private final RestaurantRepository restaurantRepository;

    private final OrderDataMapper orderDataMapper;

    public OrderCreateHelper(OrderDomainService orderDomainService, OrderRepository orderRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public OrderCreatedEvent persisOrder(CreateOrderCommand createOrderCommand){
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order,restaurant);
        saveOrder(order);
        log.info("Order is created with id: {}" , orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        return restaurantRepository.findRestaurantInformation(restaurant).orElseThrow(() -> {
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
            return new DomainException("Could not find restaurant with customer id: " + createOrderCommand.getRestaurantId());
        });
    }

    private void checkCustomer(UUID customerId) {
        customerRepository.findCustomer(customerId).orElseThrow(() -> {
            log.warn("Could not find customer with customer id: {}",customerId);
            return new DomainException("Could not find customer with customer id: " + customerId);
        });
    }



    private Order saveOrder(Order order){
        Order orderResult =  orderRepository.save(order);
        if(order == null){
            log.error("Cound not safe order!");
            throw new OrderDomainException("Cound not save order!");
        }
        log.info("Order is saved with id: {}",orderResult.getId().getValue());
        return orderResult;
    }

}
