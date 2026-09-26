package com.thanaphat2005.food.ordering.system.restaurant.service;


import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderId;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.RestaurantDomainService;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.thanaphat2005.food.ordering.system.restaurant.service.dto.RestaurantApprovalRequest;
import com.thanaphat2005.food.ordering.system.restaurant.service.mapper.RestaurantDataMapper;
import com.thanaphat2005.food.ordering.system.restaurant.service.outbox.scheduler.OrderOutboxHelper;
import com.thanaphat2005.food.ordering.system.restaurant.service.ports.output.repository.OrderApprovalRepository;
import com.thanaphat2005.food.ordering.system.restaurant.service.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderOutboxHelper orderOutboxHelper;


    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService, RestaurantDataMapper restaurantDataMapper, RestaurantRepository restaurantRepository, OrderApprovalRepository orderApprovalRepository, OrderOutboxHelper orderOutboxHelper) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderOutboxHelper = orderOutboxHelper;
    }


    @Transactional
    public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent =
                restaurantDomainService.validateOrder(
                        restaurant,
                        failureMessages
                );
        orderApprovalRepository.save(restaurant.getOrderApproval());
        orderOutboxHelper
                .saveOrderOutboxMessage(restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                        orderApprovalEvent.getOrderApproval().getApprovalStatus(),
                        OutboxStatus.STARTED,
                        UUID.fromString(restaurantApprovalRequest.getSagaId()));

    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper
                .restaurantApprovalRequestAvroModelToRestaurant(restaurantApprovalRequest);
        Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);
        if (restaurantResult.isEmpty()) {
            log.error("Restaurant with id " + restaurant.getId().getValue() + " not found!");
            throw new RestaurantNotFoundException("Restaurant with id " + restaurant.getId().getValue() +
                    " not found!");
        }

        Restaurant restaurantEntity = restaurantResult.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> {
            restaurantEntity.getOrderDetail().getProducts().forEach(productEntity -> {
                if (productEntity.getId().equals(product.getId())) {
                    product.updateWithConfirmNamePriceAndAvailability(productEntity.getName(), productEntity.getPrice(), productEntity.isAvailable());
                }
            });
        });
        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString((restaurantApprovalRequest.getOrderId()))));
        return restaurant;
    }
}
