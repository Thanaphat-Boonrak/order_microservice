package com.thanaphat2005.food.ordering.system.service.message.listener.kafka;

import com.thanaphat2005.food.ordering.system.domain.event.payload.OrderPaymentEventPayload;
import com.thanaphat2005.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.thanaphat2005.food.ordering.system.kafka.consumer.KafkaSingleItemConsumer;
import com.thanaphat2005.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.thanaphat2005.food.ordering.system.messaging.DebeziumOp;
import com.thanaphat2005.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.input.PaymentRequestMessageListener;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.exception.PaymentNotFoundException;
import com.thanaphat2005.food.ordering.system.service.message.mapper.PaymentMessageDataMapper;
import debezium.order.payment_outbox.Envelope;
import debezium.order.payment_outbox.Value;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaSingleItemConsumer<Envelope> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessageDataMapper paymentMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentMessageDataMapper paymentMessagingDataMapper,
                                       KafkaMessageHelper kafkaMessageHelper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload Envelope messages, @Header(KafkaHeaders.RECEIVED_KEY) String key, @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partitions, @Header(KafkaHeaders.OFFSET) Long offsets) {
        log.info("Receive payment request message for key: {}", key);
        if (messages.getBefore() == null && DebeziumOp.CREATE.getOp().equals(messages.getOp())) {
            log.info("Incoming Message in com.thanaphat2005.food.ordering.system.service.message.listener.kafka.PaymentRequestKafkaListener: {} with key: {}, partition: {} and offset: {} ",
                    messages, key, partitions, offsets);
            Value paymentRequestAvroModel = messages.getAfter();
            OrderPaymentEventPayload orderPaymentEventPayload =
                    kafkaMessageHelper.getOrderEventPayload(paymentRequestAvroModel.getPayload(), OrderPaymentEventPayload.class);
            try {
                if (PaymentOrderStatus.PENDING.name().equals(orderPaymentEventPayload.getPaymentOrderStatus())) {
                    log.info("Processing payment for order id: {}", orderPaymentEventPayload.getOrderId());
                    paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                            .paymentRequestAvroModelToPaymentRequest(orderPaymentEventPayload, paymentRequestAvroModel));
                } else if (PaymentOrderStatus.CANCELLED.name().equals(orderPaymentEventPayload.getPaymentOrderStatus())) {
                    log.info("Cancelling payment for order id: {}", orderPaymentEventPayload.getOrderId());
                    paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper
                            .paymentRequestAvroModelToPaymentRequest(orderPaymentEventPayload, paymentRequestAvroModel));
                }
            } catch (DataAccessException e) {
                Throwable rootCause = NestedExceptionUtils.getRootCause(e);
                if (rootCause instanceof SQLException sqlException &&
                        sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    log.error("Caught unique constraint exception with sql state: {} " +
                                    "in com.thanaphat2005.food.ordering.system.service.message.listener.kafka.PaymentRequestKafkaListener for order id: {}",
                            sqlException.getSQLState(), orderPaymentEventPayload.getOrderId());
                } else {
                    throw new PaymentApplicationServiceException("Throwing DataAccessException in" +
                            " com.thanaphat2005.food.ordering.system.service.message.listener.kafka.PaymentRequestKafkaListener: " + e.getMessage(), e);
                }
            } catch (PaymentNotFoundException e) {
                log.error("No payment found for order id: {}", orderPaymentEventPayload.getOrderId());
            }
        }
    }
}