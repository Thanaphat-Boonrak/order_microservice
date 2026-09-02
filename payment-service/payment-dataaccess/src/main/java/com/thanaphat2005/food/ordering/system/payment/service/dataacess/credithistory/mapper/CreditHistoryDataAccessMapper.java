package com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.mapper;

import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.entity.CreditHistoryEntity;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditHistory;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {


    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .customerId(creditHistory.getCustomerId().getValue())
                .id(creditHistory.getId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

    public CreditHistory CreditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder().amount(new Money(creditHistoryEntity.getAmount()))
                .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }
}
