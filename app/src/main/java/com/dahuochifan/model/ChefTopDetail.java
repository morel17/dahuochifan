package com.dahuochifan.model;

import com.dahuochifan.model.cheflist.ChefList;

import java.io.Serializable;

public class ChefTopDetail implements Serializable {
    private String tag, resultcode;
    private ChefList obj;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public ChefList getObj() {
        return obj;
    }

    public void setObj(ChefList obj) {
        this.obj = obj;
    }

}
