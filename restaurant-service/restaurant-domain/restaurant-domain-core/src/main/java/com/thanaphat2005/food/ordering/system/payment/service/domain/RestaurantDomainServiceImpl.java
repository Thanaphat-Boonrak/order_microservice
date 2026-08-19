package com.thanaphat2005.food.ordering.system.payment.service.domain;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderApprovalEvent;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderApprovedEvent;
import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {


    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages, DomainEventPublisher<OrderApprovedEvent> orderApprovalEventPublisher, DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher) {
        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    orderApprovalEventPublisher);
        } else {
            log.info("Order is rejected for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    orderRejectedEventDomainEventPublisher);
        }
    }

}

