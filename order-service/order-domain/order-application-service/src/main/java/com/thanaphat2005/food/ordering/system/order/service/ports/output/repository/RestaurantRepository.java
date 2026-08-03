package com.thanaphat2005.food.ordering.system.order.service.ports.output.repository;

import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
