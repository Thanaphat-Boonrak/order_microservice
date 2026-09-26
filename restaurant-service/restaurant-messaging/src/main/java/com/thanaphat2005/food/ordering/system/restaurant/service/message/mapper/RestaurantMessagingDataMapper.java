package com.thanaphat2005.food.ordering.system.restaurant.service.message.mapper;

import com.thanaphat2005.food.ordering.system.domain.event.payload.OrderApprovalEventPayload;
import com.thanaphat2005.food.ordering.system.domain.valueobject.ProductId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Product;
import com.thanaphat2005.food.ordering.system.restaurant.service.dto.RestaurantApprovalRequest;
import com.thanaphat2005.food.ordering.system.restaurant.service.outbox.model.OrderEventPayload;
import debezium.order.restaurant_approval_outbox.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {
    public RestaurantApprovalRequest
    restaurantApprovalRequestAvroModelToRestaurantApproval(OrderApprovalEventPayload restaurantOrderEventPayload , Value
                                                                   restaurantApprovalRequestAvroModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestAvroModel.getId())
                .sagaId(restaurantApprovalRequestAvroModel.getSagaId())
                .restaurantId(restaurantOrderEventPayload.getRestaurantId())
                .orderId(restaurantOrderEventPayload.getOrderId())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantOrderEventPayload
                        .getRestaurantOrderStatus()))
                .products(restaurantOrderEventPayload.getProducts()
                        .stream().map(avroModel ->
                                Product.builder()
                                        .productId(new ProductId(UUID.fromString(avroModel.getId())))
                                        .quantity(avroModel.getQuantity())
                                        .build())
                        .collect(Collectors.toList()))
                .price(restaurantOrderEventPayload.getPrice())
                .createdAt(Instant.parse(restaurantApprovalRequestAvroModel.getCreatedAt()))
                .build();
    }

    public RestaurantApprovalResponseAvroModel
    orderEventPayloadToRestaurantApprovalResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderEventPayload.getOrderId())
                .setRestaurantId(orderEventPayload.getRestaurantId())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderEventPayload.getOrderApprovalStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }
}