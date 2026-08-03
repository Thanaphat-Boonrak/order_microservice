package com.thanaphat2005.food.ordering.system.order.service.dto.message;


import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String paymentId;
    private Instant createdAt;
    private OrderApprovalStatus orderApprovalStatus;
    private List<String> failureMessages;
}
