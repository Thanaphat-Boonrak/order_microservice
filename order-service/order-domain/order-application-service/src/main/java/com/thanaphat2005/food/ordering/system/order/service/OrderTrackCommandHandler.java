package com.thanaphat2005.food.ordering.system.order.service;


import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.thanaphat2005.food.ordering.system.order.service.domain.valueobject.TrackingId;
import com.thanaphat2005.food.ordering.system.order.service.dto.track.TrackOrderQuery;
import com.thanaphat2005.food.ordering.system.order.service.dto.track.TrackOrderResponse;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;

    private final OrderRepository orderRepository;

    public OrderTrackCommandHandler(OrderDataMapper orderDataMapper, OrderRepository orderRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery){
        Order order =  orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId())).orElseThrow(() ->{
            throw new OrderNotFoundException("Could not find order with tracking id: " + trackOrderQuery.getOrderTrackingId());
        });
        return orderDataMapper.trackOrderResponse(order);
    }
}
