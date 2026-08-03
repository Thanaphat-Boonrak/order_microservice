package com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.restaurantapproval;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
