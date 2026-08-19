package com.thanaphat2005.food.ordering.system.restaurant.service;

import com.thanaphat2005.food.ordering.system.payment.service.domain.event.OrderApprovalEvent;
import com.thanaphat2005.food.ordering.system.restaurant.service.dto.RestaurantApprovalRequest;
import com.thanaphat2005.food.ordering.system.restaurant.service.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {

    private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    public RestaurantApprovalRequestMessageListenerImpl(RestaurantApprovalRequestHelper restaurantApprovalRequestHelper) {
        this.restaurantApprovalRequestHelper = restaurantApprovalRequestHelper;
    }

    @Override
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        OrderApprovalEvent approvalEvent = restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);
        approvalEvent.fire();
    }
}
