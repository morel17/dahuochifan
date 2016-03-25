package com.dahuochifan.model;

import com.dahuochifan.model.wechat.MainPicObj;

import java.util.List;

/**
 * Created by admin on 2015/9/8.
 */
public class MainPicAll {
    private String tag;
    private int resultcode;
    private List<MainPicObj> list;

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

    public List<MainPicObj> getList() {
        return list;
    }

    public void setList(List<MainPicObj> list) {
        this.list = list;
    }
}
