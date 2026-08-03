package com.thanaphat2005.food.ordering.system.order.service.domain.entity;

import com.thanaphat2005.food.ordering.system.domain.entity.BaseEntity;
import com.thanaphat2005.food.ordering.system.domain.valueobject.BaseId;
import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId productId,String name, Money price) {
        setId(productId);
        this.name = name;
        this.price = price;
    }

    public Product(ProductId productId) {
        setId(productId);
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}
