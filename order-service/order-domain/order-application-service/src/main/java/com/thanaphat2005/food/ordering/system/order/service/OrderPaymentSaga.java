package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.domain.event.EmptyEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.PaymentResponse;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.thanaphat2005.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;
    private final  OrderSagaHelper orderSagaHelper;

    public OrderPaymentSaga(OrderDomainService orderDomainService,OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher, OrderSagaHelper orderSagaHelper) {
        this.orderDomainService = orderDomainService;
        this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
        this.orderSagaHelper = orderSagaHelper;
    }


    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse data) {
        log.info("Completing payment for order with id {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is paid", order.getId().getValue());
        return orderPaidEvent;
    }


    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse data) {
        log.info("Cancelling order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderSagaHelper.saveOrder(order);
        orderDomainService.cancelOrder(order, data.getFailureMessages());
        return EmptyEvent.INSTANCE;
    }






}
