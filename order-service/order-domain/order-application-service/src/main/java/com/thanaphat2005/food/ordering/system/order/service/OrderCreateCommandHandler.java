package com.thanaphat2005.food.ordering.system.order.service;


import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderCommand;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderResponse;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.outbox.scheduler.payment.PaymentOutboxHelper;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class OrderCreateCommandHandler {


    private final OrderCreateHelper orderCreateHelper;

    private final OrderDataMapper orderDataMapper;

    private final PaymentOutboxHelper paymentOutboxHelper;

    private final OrderSagaHelper orderSagaHelper;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper, PaymentOutboxHelper paymentOutboxHelper, OrderSagaHelper orderSagaHelper) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderSagaHelper = orderSagaHelper;
    }


    public CreateOrderResponse createOrderResponse(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persisOrder(createOrderCommand);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        CreateOrderResponse createOrderResponse = orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order created Successfully");
        paymentOutboxHelper.savePaymentOutboxMessage(orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent)
                , orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID());

        log.info("Returning CreateOrderResponse with order id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return createOrderResponse;
    }


}
