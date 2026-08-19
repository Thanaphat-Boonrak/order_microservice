package com.thanaphat2005.food.ordering.system.payment.service.domain.event;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.domain.valueobject.RestaurantId;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent{


    private final DomainEventPublisher<OrderRejectedEvent> orderRejectedEventPublisher;

    public OrderRejectedEvent(OrderApproval orderApproval, RestaurantId restaurantId, List<String> failureMessages, ZonedDateTime createdAt, DomainEventPublisher<OrderRejectedEvent> orderRejectedEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.orderRejectedEventPublisher = orderRejectedEventPublisher;
    }

    @Override
    public void fire() {
        orderRejectedEventPublisher.publish(this);
    }
}
