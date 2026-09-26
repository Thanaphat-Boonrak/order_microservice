package com.thanaphat2005.food.ordering.system.restaurant.service.domain.event;

import com.thanaphat2005.food.ordering.system.domain.valueobject.RestaurantId;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {


    public OrderApprovedEvent(OrderApproval orderApproval, RestaurantId restaurantId, List<String> failureMessages, ZonedDateTime createdAt) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
    }

}
