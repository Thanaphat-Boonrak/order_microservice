package com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.payment;

import com.thanaphat2005.food.ordering.system.order.service.outbox.model.payment.OrderPaymentOutboxMessage;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface PaymentRequestMessagePublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage, BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> biConsumer);
}
