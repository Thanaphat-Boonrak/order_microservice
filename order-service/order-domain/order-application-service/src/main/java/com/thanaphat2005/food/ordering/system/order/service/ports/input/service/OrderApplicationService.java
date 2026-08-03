package com.thanaphat2005.food.ordering.system.order.service.ports.input.service;

import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderCommand;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderResponse;
import com.thanaphat2005.food.ordering.system.order.service.dto.track.TrackOrderQuery;
import com.thanaphat2005.food.ordering.system.order.service.dto.track.TrackOrderResponse;
import jakarta.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);

}
