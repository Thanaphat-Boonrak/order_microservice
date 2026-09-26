package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderStatus;
import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentStatus;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.dto.message.PaymentResponse;
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


@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService, OrderSagaHelper orderSagaHelper, PaymentOutboxHelper paymentOutboxHelper, ApprovalOutboxHelper approvalOutboxHelper, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }


    @Override
    @Transactional
    public void process(PaymentResponse data) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(data.getSagaId()), SagaStatus.STARTED);
        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id : {} is already processed", data.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        log.info("Completing payment for order with id {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderPaidEvent.getOrder().getOrderStatus());
        paymentOutboxHelper.save(getUpdatePaymentOutboxMessage(orderPaymentOutboxMessage, orderPaidEvent.getOrder().getOrderStatus(), sagaStatus));
        approvalOutboxHelper
                .saveApprovalOutboxMessage(orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent),
                        orderPaidEvent.getOrder().getOrderStatus(),
                        sagaStatus,
                        OutboxStatus.STARTED,
                        UUID.fromString(data.getSagaId()));
        log.info("Order with id: {} is paid", order.getId().getValue());
    }


    @Override
    @Transactional
    public void rollback(PaymentResponse data) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(data.getSagaId()), getCurrentSagaStatus(data.getPaymentStatus()));
        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id : {} is already roll backed", data.getSagaId());
            return;
        }


        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        Order order = rollbackPaymentForOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
        paymentOutboxHelper.save(getUpdatePaymentOutboxMessage(orderPaymentOutboxMessage, order.getOrderStatus(), sagaStatus));
        if (data.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(data.getSagaId(), order.getOrderStatus(), sagaStatus));
        }

    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(String sagaId,
                                                                       OrderStatus orderStatus,
                                                                       SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(sagaId),
                        SagaStatus.COMPENSATING);
        if (orderApprovalOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException("Approval outbox message could not be found in " +
                    SagaStatus.COMPENSATING.name() + " status!");
        }
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }


    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[]{SagaStatus.PROCESSING, SagaStatus.STARTED};
        };
    }

    private OrderPaymentOutboxMessage getUpdatePaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private Order rollbackPaymentForOrder(PaymentResponse data) {
        log.info("Cancelling order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderDomainService.cancelOrder(order, data.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }


}
