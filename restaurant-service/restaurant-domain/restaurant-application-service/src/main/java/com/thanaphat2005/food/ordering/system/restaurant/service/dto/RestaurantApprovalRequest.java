package com.thanaphat2005.food.ordering.system.restaurant.service.dto;


import com.thanaphat2005.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Builder
@Getter
public class RestaurantApprovalRequest {

    private String id;
    private String sagaId;
    private String restaurantId;
    private String orderId;
    private RestaurantOrderStatus restaurantOrderStatus;
    private List<Product> products;
    private BigDecimal price;
    private Instant createdAt;
}
