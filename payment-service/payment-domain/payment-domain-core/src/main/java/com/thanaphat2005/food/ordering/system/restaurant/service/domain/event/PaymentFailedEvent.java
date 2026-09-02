package com.thanaphat2005.food.ordering.system.restaurant.service.domain.event;

import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {

    public PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages) {
        super(payment, createdAt, failureMessages);
    }

}
