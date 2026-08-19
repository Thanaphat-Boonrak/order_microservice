package com.thanaphat2005.food.ordering.system.payment.service.domain.event;

import com.thanaphat2005.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCancelledEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCancelledEvent> paymentCompletedEventDomainEventPublisher;

    public PaymentCancelledEvent(Payment payment, ZonedDateTime createdAt, DomainEventPublisher<PaymentCancelledEvent> paymentCompletedEventDomainEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    }


    @Override
    public void fire() {
        paymentCompletedEventDomainEventPublisher.publish(this);
    }
}
