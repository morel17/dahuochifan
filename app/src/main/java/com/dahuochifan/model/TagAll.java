package com.dahuochifan.model;

import java.util.List;

public class TagAll {
    private String tag;
    private int resultcode;
    private List<TagObj> list;

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

    public List<TagObj> getList() {
        return list;
    }

    public void setList(List<TagObj> list) {
        this.list = list;
    }

}
