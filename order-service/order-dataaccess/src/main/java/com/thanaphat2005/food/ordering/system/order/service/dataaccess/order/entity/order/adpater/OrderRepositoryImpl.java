package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.order.adpater;


import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderId;
import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.order.mapper.OrderDataAccessMapper;
import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.order.repository.OrderJpaRepository;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.valueobject.TrackingId;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl  implements OrderRepository   {


    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.orderEntityToOrder(orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order)));
    }

    @Override
    public Optional<Order> findByOrderId(OrderId orderId) {
        return orderJpaRepository.findById(orderId.getValue()).map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getValue()).map(orderDataAccessMapper::orderEntityToOrder);
    }
}
