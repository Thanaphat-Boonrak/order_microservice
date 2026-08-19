package com.thanaphat2005.food.ordering.system.order.service.messaging.publish.publisher.kafka;

import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.thanaphat2005.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.thanaphat2005.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.thanaphat2005.food.ordering.system.order.service.messaging.publish.config.OrderServiceConfigData;
import com.thanaphat2005.food.ordering.system.order.service.messaging.publish.mapper.OrderMessagingDataMapper;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {


    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper, OrderServiceConfigData orderServiceConfigData, KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer, OrderKafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }


    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();


        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel = orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName()
                    , orderId, restaurantApprovalRequestAvroModel, orderKafkaMessageHelper.<String, RestaurantApprovalRequestAvroModel>getKafkaCallBack(orderServiceConfigData.getRestaurantApprovalRequestTopicName(), restaurantApprovalRequestAvroModel));
            log.info("RestaurantApprovalRequestAvroModel sent to Kafka for orderId={}", restaurantApprovalRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel to Kafka for orderId={}", orderId, e);
        }
    }
}
