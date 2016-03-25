package com.dahuochifan.model.cookbook;

import com.dahuochifan.core.model.order.OrderInfo;

/**
 * Created by admin on 2015/9/29.
 */
public class OrderDetailInfo {
    private String tag;
    private int resultcode;
    private OrderInfo obj;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getResultcode() {
        return resultcode;
    }

    public void setResultcode(int resultcode) {
        this.resultcode = resultcode;
    }

    public OrderInfo getObj() {
        return obj;
    }

    public void setObj(OrderInfo obj) {
        this.obj = obj;
    }
}