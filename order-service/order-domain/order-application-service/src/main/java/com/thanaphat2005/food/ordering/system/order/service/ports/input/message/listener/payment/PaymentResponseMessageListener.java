package com.thanaphat2005.food.ordering.system.order.service.ports.input.message.listener.payment;

import com.thanaphat2005.food.ordering.system.order.service.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

    void paymentComplete(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse);
}
