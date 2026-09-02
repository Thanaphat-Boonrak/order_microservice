package com.thanaphat2005.food.ordering.system.order.service;

import com.thanaphat2005.food.ordering.system.order.service.dto.message.PaymentResponse;
import com.thanaphat2005.food.ordering.system.order.service.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;


@Validated
@Service
@Slf4j
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSaga orderPaymentSaga;

    public PaymentResponseMessageListenerImpl(OrderPaymentSaga orderPaymentSaga) {
        this.orderPaymentSaga = orderPaymentSaga;
    }

    @Override
    public void paymentComplete(PaymentResponse paymentResponse) {
        orderPaymentSaga.process(paymentResponse);
        log.info("Order Payment Saga process operation is completed for order Id : {}",paymentResponse.getOrderId());
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
    orderPaymentSaga.rollback(paymentResponse);
    log.info("Payment cancelled for order Id : {}", String.join(FAILURE_MESSAGE_DELIMITER,paymentResponse.getFailureMessages()));
    }
}
