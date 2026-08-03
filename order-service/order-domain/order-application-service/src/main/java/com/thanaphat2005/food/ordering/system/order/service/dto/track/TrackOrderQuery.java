package com.thanaphat2005.food.ordering.system.order.service.dto.track;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TrackOrderQuery {

    @NotNull
    private final UUID orderTrackingId;
}
