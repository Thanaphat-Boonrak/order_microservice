package com.thanaphat2005.food.ordering.system.order.service.dto.create;


import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;

@Builder
@Getter
@AllArgsConstructor
public class OrderAddress {

    @NotNull
    @Max(value = 50)
    private final String street;
    @NotNull
    @Max(value = 10)
    private final String postalCode;

    @NotNull
    @Max(value = 50)
    private  final  String city;

}
