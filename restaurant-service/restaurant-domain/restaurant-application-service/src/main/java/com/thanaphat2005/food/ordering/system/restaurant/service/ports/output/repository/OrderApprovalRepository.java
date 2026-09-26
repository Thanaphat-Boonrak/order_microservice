package com.thanaphat2005.food.ordering.system.restaurant.service.ports.output.repository;

import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
