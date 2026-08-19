package com.thanaphat2005.food.ordering.system.restaurant.service.ports.output.repository;

import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
