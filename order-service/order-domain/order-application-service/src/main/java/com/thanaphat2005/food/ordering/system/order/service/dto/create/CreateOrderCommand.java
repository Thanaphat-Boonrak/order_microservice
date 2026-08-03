package com.thanaphat2005.food.ordering.system.order.service.dto.create;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
public class CreateOrderCommand  {

    private final UUID customerId;
    private final UUID restaurantId;
    private final BigDecimal price;
    private final List<OrderItem> items;
    private final OrderAddress address;
}
