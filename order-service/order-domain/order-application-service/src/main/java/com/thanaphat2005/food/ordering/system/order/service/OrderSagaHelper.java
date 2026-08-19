package com.thanaphat2005.food.ordering.system.order.service;


import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderId;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Order findOrder(String orderId) {
        Optional<Order> order = orderRepository.findByOrderId(new OrderId(UUID.fromString(orderId)));
        if (order.isEmpty()) {
            log.error("Order with id {} not found", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + "could not found");
        }
        return order.get();
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }


}
