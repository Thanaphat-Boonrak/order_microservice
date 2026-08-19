package com.thanaphat2005.food.ordering.system.payment.service.domain.entity;

import com.thanaphat2005.food.ordering.system.domain.entity.BaseEntity;
import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.payment.service.domain.valueobject.TransactionType;
import com.thanaphat2005.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;

public class CreditHistory extends BaseEntity<CreditHistoryId> {

    private final CustomerId customerId;

    private final Money amount;

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    private final TransactionType transactionType;

    private CreditHistory(Builder builder) {
        super.setId(builder.creditHistoryId);
        customerId = builder.customerId;
        amount = builder.amount;
        transactionType = builder.transactionType;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private CreditHistoryId creditHistoryId;
        private CustomerId customerId;
        private Money amount;
        private TransactionType transactionType;

        private Builder() {
        }


        public Builder creditHistoryId(CreditHistoryId val) {
            creditHistoryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder amount(Money val) {
            amount = val;
            return this;
        }

        public Builder transactionType(TransactionType val) {
            transactionType = val;
            return this;
        }

        public CreditHistory build() {
            return new CreditHistory(this);
        }
    }
}
