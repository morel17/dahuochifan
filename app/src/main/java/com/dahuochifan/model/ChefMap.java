package com.dahuochifan.model;

import com.dahuochifan.model.cheflist.ChefList;

import java.util.List;

/**
 * Created by admin on 2015/9/2.
 */
public class ChefMap {
    private String tag, resultcode;
    private List<ChefList> list;

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

    public List<ChefList> getList() {
        return list;
    }

    public void setList(List<ChefList> list) {
        this.list = list;
    }
}
