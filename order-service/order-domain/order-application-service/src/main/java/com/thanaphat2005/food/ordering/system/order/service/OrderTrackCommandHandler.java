package com.thanaphat2005.food.ordering.system.order.service;


import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.dto.track.TrackOrderQuery;
import com.thanaphat2005.food.ordering.system.order.service.dto.track.TrackOrderResponse;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTrackCommandHandler {




    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery){
        return null;
    }
}
