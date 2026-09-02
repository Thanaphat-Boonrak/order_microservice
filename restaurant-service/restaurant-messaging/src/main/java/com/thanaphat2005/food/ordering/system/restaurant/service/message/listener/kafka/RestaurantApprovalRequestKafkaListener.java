package com.thanaphat2005.food.ordering.system.restaurant.service.message.listener.kafka;

import com.thanaphat2005.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.thanaphat2005.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.thanaphat2005.food.ordering.system.restaurant.service.message.mapper.RestaurantMessagingDataMapper;
import com.thanaphat2005.food.ordering.system.restaurant.service.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


@Service
@Slf4j
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {

    private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    public RestaurantApprovalRequestKafkaListener(RestaurantApprovalRequestMessageListener
                                                          restaurantApprovalRequestMessageListener,
                                                  RestaurantMessagingDataMapper
                                                          restaurantMessagingDataMapper) {
        this.restaurantApprovalRequestMessageListener = restaurantApprovalRequestMessageListener;
        this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${restaurant-service.restaurant-approval-request-topic-name}")
    public void receive(@Payload List<RestaurantApprovalRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of orders approval requests received with keys {}, partitions {} and offsets {}" +
                        ", sending for restaurant approval",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalRequestAvroModel -> {
            try {
                log.info("Processing order approval for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
                restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper.
                        restaurantApprovalRequestAvroModelToRestaurantApproval(restaurantApprovalRequestAvroModel));
            } catch (DataAccessException e) {
                SQLException sqlException = (SQLException) e.getRootCause();
                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    log.error("Caught unique constraint exception with sql state: {} " +
                                    "in RestaurantApprovalRequestKafkaListener for order id: {}",
                            sqlException.getSQLState(), restaurantApprovalRequestAvroModel.getOrderId());
                } else {
                    throw new RestaurantApplicationServiceException("Throwing DataAccessException in" +
                            " RestaurantApprovalRequestKafkaListener: " + e.getMessage(), e);
                }
            } catch (RestaurantNotFoundException e) {
                log.error("No restaurant found for restaurant id: {}, and order id: {}",
                        restaurantApprovalRequestAvroModel.getRestaurantId(),
                        restaurantApprovalRequestAvroModel.getOrderId());
            }
        });
    }

}