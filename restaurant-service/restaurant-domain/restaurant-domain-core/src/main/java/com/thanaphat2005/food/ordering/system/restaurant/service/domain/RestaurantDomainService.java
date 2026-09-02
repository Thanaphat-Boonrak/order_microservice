package com.thanaphat2005.food.ordering.system.restaurant.service.domain;

import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {


    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages);


}
