package com.thanaphat2005.food.ordering.system.restaurant.service.ports.input.message.listener;

import com.thanaphat2005.food.ordering.system.restaurant.service.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {

    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
