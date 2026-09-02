package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.order.service.dto.message.RestaurantApprovalResponse;
import com.thanaphat2005.food.ordering.system.order.service.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;


@Validated
@Service
@Slf4j
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {


    private final OrderApprovalSaga orderApprovalSaga;

    public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
        this.orderApprovalSaga = orderApprovalSaga;
    }


    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order id: {}",restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Order Approval  Saga rollback event for order id: {} with failure messages: {}", restaurantApprovalResponse.getOrderId(), String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponse.getFailureMessages()));
    }
}
