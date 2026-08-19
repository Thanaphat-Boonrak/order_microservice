package com.thanaphat2005.food.ordering.system.payment.service.domain.valueobject;

import com.thanaphat2005.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {


    public OrderApprovalId(UUID value) {
        super(value);
    }
}
