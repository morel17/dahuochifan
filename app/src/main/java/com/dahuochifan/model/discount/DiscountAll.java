package com.dahuochifan.model.discount;

/**
 * Created by admin on 2015/11/10.
 */
public class DiscountAll {
    private String tag;
    private int resultcode;
    private DiscountPage page;

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

    public DiscountPage getPage() {
        return page;
    }

    public void setPage(DiscountPage page) {
        this.page = page;
    }
}
