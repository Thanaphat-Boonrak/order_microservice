package com.thanaphat2005.food.ordering.system.order.service.domain;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderPaidEvent;

import java.util.List;


public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent> OrderCreatedEventDomainEventPublisher);

    OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> OrderPaidEventDomainEventPublisher);


    void approvedOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> OrderCancelledEventDomainEventPublisher);


    void cancelOrder(Order order,List<String> failureMessages);
}
