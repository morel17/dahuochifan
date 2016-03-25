package com.dahuochifan.model;

import java.util.List;

/**
 * Created by admin on 2015/8/28.
 */
public class CookBookPartAll {
    private String tag;
    private int resultcode;
    private List<CookBookLevel> list;

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

    public List<CookBookLevel> getList() {
        return list;
    }

    public void setList(List<CookBookLevel> list) {
        this.list = list;
    }
}
