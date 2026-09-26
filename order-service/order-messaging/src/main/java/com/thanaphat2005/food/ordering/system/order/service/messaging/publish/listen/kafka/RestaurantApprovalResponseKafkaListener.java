package com.thanaphat2005.food.ordering.system.order.service.messaging.publish.listen.kafka;

import com.thanaphat2005.food.ordering.system.domain.event.payload.RestaurantOrderEventPayload;
import com.thanaphat2005.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.thanaphat2005.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.thanaphat2005.food.ordering.system.messaging.DebeziumOp;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.thanaphat2005.food.ordering.system.order.service.messaging.publish.mapper.OrderMessagingDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import debezium.restaurant.order_outbox.Envelope;
import debezium.restaurant.order_outbox.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;


@Component
@Slf4j
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<Envelope> {


    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    private final KafkaMessageHelper kafkaMessageHelper;

    public RestaurantApprovalResponseKafkaListener(RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener, OrderMessagingDataMapper orderMessagingDataMapper, KafkaMessageHelper kafkaMessageHelper) {
        this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }


    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}", containerFactory = "kafkaListenerContainerFactory"
    )
    public void receive(
            @Payload List<Envelope> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("{} number of restaurant approval responses received!", messages.stream().filter(message -> message.getBefore() == null && DebeziumOp.CREATE.getOp().equals(message.getOp())).toList().size());


        messages.forEach(avroModel -> {

            if(avroModel.getBefore() == null && DebeziumOp.CREATE.getOp().equals(avroModel.getOp())) {
                Value restaurantApprovalResponseAvroModel = avroModel.getAfter();
                RestaurantOrderEventPayload restaurantOrderEventPayload = kafkaMessageHelper.getOrderEventPayload(restaurantApprovalResponseAvroModel.getPayload(), RestaurantOrderEventPayload.class);
                try {
                    if (OrderApprovalStatus.APPROVED.name().equals(restaurantOrderEventPayload.getOrderApprovalStatus())) {
                        log.info("Processing approved order for order id: {}",
                                restaurantOrderEventPayload.getOrderId());
                        restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper
                                .approvalResponseAvroModelToApprovalResponse(restaurantOrderEventPayload,restaurantApprovalResponseAvroModel));
                    } else if (OrderApprovalStatus.REJECTED.name().equals(restaurantOrderEventPayload.getOrderApprovalStatus())) {
                        log.info("Processing rejected order for order id: {}, with failure messages: {}",
                                restaurantOrderEventPayload.getOrderId(),
                                String.join(FAILURE_MESSAGE_DELIMITER,
                                        restaurantOrderEventPayload.getFailureMessages()));
                        restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper
                                .approvalResponseAvroModelToApprovalResponse(restaurantOrderEventPayload,restaurantApprovalResponseAvroModel));
                    }
                } catch (OptimisticLockingFailureException e) {
                    log.error("Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                            restaurantOrderEventPayload.getOrderId());
                } catch (OrderNotFoundException e) {
                    log.error("No order found for order id: {}", restaurantOrderEventPayload.getOrderId());
                }
            }

        });
    }
}
