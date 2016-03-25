package com.dahuochifan.event;

/**
 * Created by morel on 2015/11/13.
 */
public class DiscountEvent {
    private int type,discountLimit,discountPrice;
    private String discountId;

    public DiscountEvent(int type, int discountLimit, int discountPrice, String discountId) {
        this.type = type;
        this.discountLimit = discountLimit;
        this.discountPrice = discountPrice;
        this.discountId = discountId;
    }

    public int getType() {
        return type;
    }

    public int getDiscountLimit() {
        return discountLimit;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public String getDiscountId() {
        return discountId;
    }
}
