package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.order.repository;

import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByTrackingId(UUID trackingId);

}
