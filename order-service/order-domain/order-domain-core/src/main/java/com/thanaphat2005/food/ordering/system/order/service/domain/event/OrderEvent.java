package com.thanaphat2005.food.ordering.system.order.service.domain.event;

import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public abstract class OrderEvent implements DomainEvent<Order> {
    private final Order order;
    private final ZonedDateTime createdAt;

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Order getOrder() {
        return order;
    }

    public OrderEvent(Order order, ZonedDateTime createdAt) {
        this.order = order;
        this.createdAt = createdAt;
    }
}
