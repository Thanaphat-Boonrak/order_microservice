package com.thanaphat2005.food.ordering.system.service.message.mapper;


import com.thanaphat2005.food.ordering.system.domain.event.payload.OrderPaymentEventPayload;
import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.thanaphat2005.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.thanaphat2005.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import debezium.order.payment_outbox.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentMessageDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(OrderPaymentEventPayload paymentOrderEventPayload, Value paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(paymentOrderEventPayload.getCustomerId())
                .orderId(paymentOrderEventPayload.getOrderId())
                .price(paymentOrderEventPayload.getPrice())
                .createdAt(Instant.parse(paymentRequestAvroModel.getCreatedAt()))
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentOrderEventPayload.getPaymentOrderStatus()))
                .build();
    }

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(String sagaId,
                                                                                OrderEventPayload orderEventPayload) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setPaymentId(orderEventPayload.getPaymentId())
                .setCustomerId(orderEventPayload.getCustomerId())
                .setOrderId(orderEventPayload.getOrderId())
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }
}