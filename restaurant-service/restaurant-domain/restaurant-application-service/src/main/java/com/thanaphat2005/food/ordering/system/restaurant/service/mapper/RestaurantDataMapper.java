package com.thanaphat2005.food.ordering.system.restaurant.service.mapper;

import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderStatus;
import com.thanaphat2005.food.ordering.system.domain.valueobject.RestaurantId;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Product;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.thanaphat2005.food.ordering.system.restaurant.service.dto.RestaurantApprovalRequest;
import com.thanaphat2005.food.ordering.system.restaurant.service.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {

    public Restaurant restaurantApprovalRequestAvroModelToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(OrderDetail.builder()
                        .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                        .products(restaurantApprovalRequest.getProducts().stream().map(
                                        product -> Product.builder()
                                                .productId(product.getId())
                                                .quantity(product.getQuantity())
                                                .build())
                                .collect(Collectors.toList()))
                        .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }

    public OrderEventPayload
    orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
        return OrderEventPayload.builder()
                .orderId(orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString())
                .restaurantId(orderApprovalEvent.getRestaurantId().getValue().toString())
                .orderApprovalStatus(orderApprovalEvent.getOrderApproval().getApprovalStatus().name())
                .createdAt(orderApprovalEvent.getCreatedAt())
                .failureMessages(orderApprovalEvent.getFailureMessages())
                .build();
    }
}
