package com.thanaphat2005.food.ordering.system.order.service.domain.entity;

import com.thanaphat2005.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.thanaphat2005.food.ordering.system.domain.entity.BaseEntity;
import com.thanaphat2005.food.ordering.system.domain.valueobject.Money;
import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderId;


public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money price;
    private final Money subTotal;


    boolean isPriceValid(){
        return price.isGreaterThanZero() &&
                price.equals(product.getPrice())
                && price.multiple(quantity).equals(subTotal);
     }

    private OrderItem(Builder builder) {
        super.setId(builder.orderItemId);
        product = builder.product;
        price = builder.price;
        subTotal = builder.subTotal;
        quantity = builder.quantity;
    }

    public static Builder builder() {
        return new Builder();
    }


    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }



    public void initializedOrderItem(OrderId Id,OrderItemId orderItemId) {
        this.orderId = Id;
        super.setId(orderItemId);
    }

    public static final class Builder {
        private OrderItemId orderItemId;
        private Product product;
        private Money price;
        private Money subTotal;
        private int quantity;

        private Builder() {
        }



        public Builder orderItemId(OrderItemId val) {
            orderItemId = val;
            return this;
        }

        public Builder product(Product val) {
            product = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder subTotal(Money val) {
            subTotal = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
