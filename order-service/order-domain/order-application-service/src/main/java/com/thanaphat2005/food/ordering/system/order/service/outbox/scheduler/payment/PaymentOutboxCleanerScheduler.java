package com.thanaphat2005.food.ordering.system.order.service.outbox.scheduler.payment;

import com.thanaphat2005.food.ordering.system.order.service.outbox.model.payment.OrderPaymentOutboxMessage;
import com.thanaphat2005.food.ordering.system.outbox.OutboxScheduler;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {


    private final PaymentOutboxHelper paymentOutboxHelper;

    public PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
        this.paymentOutboxHelper = paymentOutboxHelper;
    }

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderPaymentOutboxMessage>> outboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus
                (OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);

        if (outboxMessageResponse.isPresent()) {
            List<OrderPaymentOutboxMessage> orderPaymentOutboxMessages = outboxMessageResponse.get();
            log.info("Received {} orderPaymentOutboxMessages for clean-up. The payload: {}", orderPaymentOutboxMessages.size(), orderPaymentOutboxMessages.stream().map(OrderPaymentOutboxMessage::getPayload)
                    .collect(Collectors.joining("\n")));
            paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED,SagaStatus.SUCCEEDED,SagaStatus.FAILED, SagaStatus.COMPENSATED);
            log.info("{} OrderPaymentOutboxMessage deleted!", orderPaymentOutboxMessages.size());
        }
    }
}
