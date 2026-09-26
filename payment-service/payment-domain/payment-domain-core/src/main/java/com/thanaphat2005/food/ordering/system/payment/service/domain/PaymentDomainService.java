package com.thanaphat2005.food.ordering.system.payment.service.domain;

import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditEntry;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditHistory;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Payment;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {


    PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages);


    PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages);
}
