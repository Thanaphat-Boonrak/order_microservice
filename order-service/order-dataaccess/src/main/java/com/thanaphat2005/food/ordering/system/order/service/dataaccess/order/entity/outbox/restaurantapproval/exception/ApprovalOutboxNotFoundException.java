package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.outbox.restaurantapproval.exception;

public class ApprovalOutboxNotFoundException extends RuntimeException {

    public ApprovalOutboxNotFoundException(String message) {
        super(message);
    }
}
