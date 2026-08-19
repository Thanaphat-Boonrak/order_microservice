package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.domain.event.EmptyEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.RestaurantApprovalResponse;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.thanaphat2005.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Slf4j
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;
    private final  OrderSagaHelper orderSagaHelper;

    public OrderApprovalSaga(OrderDomainService orderDomainService , OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher, OrderSagaHelper orderSagaHelper) {
        this.orderDomainService = orderDomainService;
        this.orderCancelledPaymentRequestMessagePublisher = orderCancelledPaymentRequestMessagePublisher;
        this.orderSagaHelper = orderSagaHelper;
    }


    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalResponse data) {
        log.info("Approving order with Id: {}", data.getId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderDomainService.approvedOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Order with Id: {} is approved", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancelledEvent rollback(RestaurantApprovalResponse data) {
        log.info("Cancelling order with Id: {}", data.getId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order,data.getFailureMessages(),orderCancelledPaymentRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with Id: {} is cancelled", order.getId().getValue());
        return orderCancelledEvent;
    }
}
