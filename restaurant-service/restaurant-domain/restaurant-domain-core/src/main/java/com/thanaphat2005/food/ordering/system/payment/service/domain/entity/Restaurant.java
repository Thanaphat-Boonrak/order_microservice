package com.thanaphat2005.food.ordering.system.payment.service.domain.entity;

import com.thanaphat2005.food.ordering.system.domain.entity.AggregateRoot;
import com.thanaphat2005.food.ordering.system.domain.valueobject.*;
import com.thanaphat2005.food.ordering.system.payment.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

public class Restaurant extends AggregateRoot<RestaurantId> {

    private OrderApproval orderApproval;
    private boolean active;
    private final OrderDetail orderDetail;


    public void  validateOrder(List<String> failureMessage){
        if(orderDetail.getOrderStatus() != OrderStatus.PAID){
            failureMessage.add("Payment is not completed for order: " + orderDetail.getId());
        }
        Money totalAmount = orderDetail.getProducts().stream().map(product -> {
            if(!product.isAvailable()){
                failureMessage.add("Product with id: " + orderDetail.getId().getValue() + " is not available");
            }
            return product.getPrice().multiple(product.getQuantity());
        }).reduce(Money.ZERO, Money::add);
        if(!totalAmount.equals(orderDetail.getTotalAmount()))
        {
            failureMessage.add("Total price is not correct for order: " + orderDetail.getId());
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        this.orderApproval = OrderApproval.builder().orderApprovalId(new OrderApprovalId(UUID.randomUUID()))
                .restaurantId(this.getId())
                .orderId(this.getOrderDetail().getId())
                .approvalStatus(orderApprovalStatus)
                .build();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private Restaurant(Builder builder) {
        super.setId(builder.restaurantId);
        orderApproval = builder.orderApproval;
        active = builder.active;
        orderDetail = builder.orderDetail;
    }

    public static Builder builder() {
        return new Builder();
    }


    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public boolean isActive() {
        return active;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public static final class Builder {
        private RestaurantId restaurantId;
        private OrderApproval orderApproval;
        private boolean active;
        private OrderDetail orderDetail;

        private Builder() {
        }



        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder orderApproval(OrderApproval val) {
            orderApproval = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Builder orderDetail(OrderDetail val) {
            orderDetail = val;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}
