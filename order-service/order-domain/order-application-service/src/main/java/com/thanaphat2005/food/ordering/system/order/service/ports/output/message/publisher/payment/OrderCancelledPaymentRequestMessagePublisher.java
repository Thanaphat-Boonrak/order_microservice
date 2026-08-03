package com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.payment;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCreatedEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
