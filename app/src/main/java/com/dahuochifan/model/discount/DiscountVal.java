package com.dahuochifan.model.discount;

import java.util.List;

/**
 * Created by morel on 2015/11/11.
 */
public class DiscountVal {
    private String tag;
    private int resultcode;
    private List<DiscountDetail> list;

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

    public List<DiscountDetail> getList() {
        return list;
    }

    public void setList(List<DiscountDetail> list) {
        this.list = list;
    }
}
