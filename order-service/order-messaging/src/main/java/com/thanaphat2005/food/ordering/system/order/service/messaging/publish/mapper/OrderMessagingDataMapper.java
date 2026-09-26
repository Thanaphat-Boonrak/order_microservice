package com.thanaphat2005.food.ordering.system.order.service.messaging.publish.mapper;

import com.thanaphat2005.food.ordering.system.domain.event.payload.RestaurantOrderEventPayload;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentStatus;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.*;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.CustomerModel;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.PaymentResponse;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.RestaurantApprovalResponse;
import com.thanaphat2005.food.ordering.system.domain.event.payload.OrderApprovalEventPayload;
import com.thanaphat2005.food.ordering.system.domain.event.payload.PaymentOrderEventPayload;
import debezium.payment.order_outbox.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }


    public RestaurantApprovalRequestAvroModel
    orderApprovalEventToRestaurantApprovalRequestAvroModel(String sagaId, OrderApprovalEventPayload
            orderApprovalEventPayload) {
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderApprovalEventPayload.getOrderId())
                .setRestaurantId(orderApprovalEventPayload.getRestaurantId())
                .setRestaurantOrderStatus(RestaurantOrderStatus
                        .valueOf(orderApprovalEventPayload.getRestaurantOrderStatus()))
                .setProducts(orderApprovalEventPayload.getProducts().stream().map(orderApprovalEventProduct ->
                        Product.newBuilder().setId(orderApprovalEventProduct.getId())
                                .setQuantity(orderApprovalEventProduct.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(orderApprovalEventPayload.getPrice())
                .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(order.getId().getValue().toString())
                .setRestaurantId(order.getRestaurantId().getValue().toString())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .setProducts(order.getItems().stream().map(orderItem ->
                        Product.newBuilder()
                                .setId(orderItem.getProduct().getId().getValue().toString())
                                .setQuantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .build();
    }

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentOrderEventPayload paymentOrderEventPayload, Value paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .paymentId(paymentOrderEventPayload.getPaymentId())
                .customerId(paymentOrderEventPayload.getCustomerId())
                .orderId(paymentOrderEventPayload.getOrderId())
                .price(paymentOrderEventPayload.getPrice())
                .createdAt(Instant.parse(paymentResponseAvroModel.getCreatedAt()))
                .paymentStatus(PaymentStatus.valueOf(paymentOrderEventPayload.getPaymentStatus()))
                .failureMessages(paymentOrderEventPayload.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse approvalResponseAvroModelToApprovalResponse(RestaurantOrderEventPayload restaurantOrderEventPayload, debezium.restaurant.order_outbox.Value restaurantApprovalResponseAvroModel) {
        return RestaurantApprovalResponse.builder()
                .id(restaurantApprovalResponseAvroModel.getId())
                .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
                .restaurantId(restaurantOrderEventPayload.getRestaurantId())
                .orderId(restaurantOrderEventPayload.getOrderId())
                .createdAt(Instant.parse(restaurantApprovalResponseAvroModel.getCreatedAt()))
                .orderApprovalStatus(OrderApprovalStatus.valueOf(
                        restaurantOrderEventPayload.getOrderApprovalStatus()))
                .failureMessages(restaurantOrderEventPayload.getFailureMessages())
                .build();
    }

    public CustomerModel customerAvroModelToCustomerModel(CustomerAvroModel customerAvroModel) {
        return CustomerModel.builder()
                .id(customerAvroModel.getId().toString())
                .username(customerAvroModel.getUsername())
                .firstName(customerAvroModel.getFirstName())
                .lastName(customerAvroModel.getLastName())
                .build();
    }
}