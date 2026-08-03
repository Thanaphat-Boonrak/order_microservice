package com.thanaphat2005.food.ordering.system.order.service.ports.input.message.listener.restaurantapproval;

import com.thanaphat2005.food.ordering.system.order.service.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseMessageListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);
    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
