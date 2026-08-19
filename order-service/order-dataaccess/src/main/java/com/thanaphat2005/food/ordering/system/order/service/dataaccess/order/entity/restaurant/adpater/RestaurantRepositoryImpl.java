package com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.restaurant.adpater;


import com.thanaphat2005.food.ordering.system.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.thanaphat2005.food.ordering.system.order.service.dataaccess.order.entity.restaurant.mapper.RestaurantDataAccessMapper;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository, RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        return restaurantJpaRepository.
                findByRestaurantIdAndProductIdIn
                        (restaurant.getId().getValue(),
                                restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant))
                .map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
