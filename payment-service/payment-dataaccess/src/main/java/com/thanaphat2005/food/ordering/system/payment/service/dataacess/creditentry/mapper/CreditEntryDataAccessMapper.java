package com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.mapper;


import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.entity.CreditEntryEntity;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditEntry;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {


    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

    public CreditEntry CreditEntryEntityTocreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder().customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
                .build();
    }
}
