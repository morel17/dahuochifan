package com.dahuochifan.model;

import java.util.List;

public class NotifyAll {
    private String tag;
    private int resultcode;
    private List<NotifyObj> list;

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

    public List<NotifyObj> getList() {
        return list;
    }

    public void setList(List<NotifyObj> list) {
        this.list = list;
    }

}
