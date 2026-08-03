package com.thanaphat2005.food.ordering.system.order.service.ports.output.repository;

import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);


    Optional<Order> findByTrackingId(TrackingId trackingId);
}
