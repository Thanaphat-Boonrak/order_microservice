package com.thanaphat2005.food.ordering.system.payment.service.domain;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderApprovalEvent;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderApprovedEvent;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {


    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages, DomainEventPublisher<OrderApprovedEvent> orderApprovalEventPublisher, DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher);


}
