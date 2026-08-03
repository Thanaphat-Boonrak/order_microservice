package com.thanaphat2005.food.ordering.system.order.service;


import com.thanaphat2005.food.ordering.system.domain.exception.DomainException;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderCommand;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderResponse;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class OrderCreateCommandHandler {


    private final OrderCreateHelper orderCreateHelper;

    private final OrderDataMapper orderDataMapper;

    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper, OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedPaymentRequestMessagePublisher = orderCreatedPaymentRequestMessagePublisher;
    }


    public CreateOrderResponse createOrderResponse(CreateOrderCommand createOrderCommand){
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persisOrder(createOrderCommand);
        log.info("Order is created with id: {}",orderCreatedEvent.getOrder().getId().getValue());
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());
    }




}
