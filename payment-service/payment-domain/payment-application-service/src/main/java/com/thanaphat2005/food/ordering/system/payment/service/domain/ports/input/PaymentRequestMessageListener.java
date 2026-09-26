package com.thanaphat2005.food.ordering.system.payment.service.domain.ports.input;

import com.thanaphat2005.food.ordering.system.payment.service.domain.dto.PaymentRequest;

public interface PaymentRequestMessageListener {

    void completePayment(PaymentRequest paymentRequest);

    void cancelPayment(PaymentRequest paymentRequest);
}
