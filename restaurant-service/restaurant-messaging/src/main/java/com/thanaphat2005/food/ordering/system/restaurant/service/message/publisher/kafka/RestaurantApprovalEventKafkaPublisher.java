package com.thanaphat2005.food.ordering.system.restaurant.service.message.publisher.kafka;

import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.thanaphat2005.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.thanaphat2005.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.restaurant.service.config.RestaurantServiceConfigData;
import com.thanaphat2005.food.ordering.system.restaurant.service.message.mapper.RestaurantMessagingDataMapper;
import com.thanaphat2005.food.ordering.system.restaurant.service.outbox.model.OrderEventPayload;
import com.thanaphat2005.food.ordering.system.restaurant.service.outbox.model.OrderOutboxMessage;
import com.thanaphat2005.food.ordering.system.restaurant.service.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@Slf4j
public class RestaurantApprovalEventKafkaPublisher implements RestaurantApprovalResponseMessagePublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfigData restaurantServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public RestaurantApprovalEventKafkaPublisher(RestaurantMessagingDataMapper dataMapper,
                                                 KafkaProducer<String, RestaurantApprovalResponseAvroModel>
                                                         kafkaProducer,
                                                 RestaurantServiceConfigData restaurantServiceConfigData,
                                                 KafkaMessageHelper kafkaMessageHelper) {
        this.restaurantMessagingDataMapper = dataMapper;
        this.kafkaProducer = kafkaProducer;
        this.restaurantServiceConfigData = restaurantServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }


    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(),
                        OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);
        try {
            RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
                    restaurantMessagingDataMapper
                            .orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, orderEventPayload);

            kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                    sagaId,
                    restaurantApprovalResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallBack(restaurantServiceConfigData
                                    .getRestaurantApprovalResponseTopicName(),
                            restaurantApprovalResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback));

            log.info("RestaurantApprovalResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    restaurantApprovalResponseAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponseAvroModel message" +
                            " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }

}