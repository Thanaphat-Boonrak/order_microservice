package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderStatus;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.RestaurantApprovalResponse;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.outbox.model.approval.OrderApprovalOutboxMessage;
import com.thanaphat2005.food.ordering.system.order.service.outbox.model.payment.OrderPaymentOutboxMessage;
import com.thanaphat2005.food.ordering.system.order.service.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.thanaphat2005.food.ordering.system.order.service.outbox.scheduler.payment.PaymentOutboxHelper;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.saga.SagaStatus;
import com.thanaphat2005.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;


@Component
@Slf4j
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderDomainService orderDomainService;
    private final  OrderSagaHelper orderSagaHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;

    public OrderApprovalSaga(OrderDomainService orderDomainService, OrderSagaHelper orderSagaHelper, ApprovalOutboxHelper approvalOutboxHelper, OrderDataMapper orderDataMapper, PaymentOutboxHelper paymentOutboxHelper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
        this.paymentOutboxHelper = paymentOutboxHelper;
    }


    @Override
    @Transactional
    public void process(RestaurantApprovalResponse data) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(data.getSagaId()), SagaStatus.PROCESSING);


        if (orderApprovalOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!",
                    data.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();

        Order order = approveOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage,
                order.getOrderStatus(), sagaStatus));

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(data.getSagaId(),order.getOrderStatus(),sagaStatus));

        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(String sagaId,
                                                                     OrderStatus orderStatus,
                                                                     SagaStatus sagaStatus) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse = paymentOutboxHelper
                .getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(sagaId), SagaStatus.PROCESSING);
        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException("Payment outbox message cannot be found in " +
                    SagaStatus.PROCESSING.name() + " state");
        }
        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage
                                                                               orderApprovalOutboxMessage,
                                                                       OrderStatus
                                                                               orderStatus,
                                                                       SagaStatus
                                                                               sagaStatus) {
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with Id: {}", restaurantApprovalResponse.getId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approvedOrder(order);
        orderSagaHelper.saveOrder(order);
        return  order;
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse data) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(data.getSagaId()), SagaStatus.PROCESSING);

        if (orderApprovalOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already roll backed!",
                    data.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();

        OrderCancelledEvent domainEvent = rollbackOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage,
                domainEvent.getOrder().getOrderStatus(), sagaStatus));
        paymentOutboxHelper.savePaymentOutboxMessage(orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(domainEvent),domainEvent.getOrder().getOrderStatus(),sagaStatus, OutboxStatus.STARTED,UUID.fromString(data.getSagaId()));

        log.info("Order with Id: {} is cancelled", domainEvent.getOrder().getId().getValue());
    }

    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse data) {
        log.info("Cancelling order with Id: {}", data.getId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order,data.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return orderCancelledEvent;
    }


}
