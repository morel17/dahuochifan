package com.dahuochifan.model.cookbookself;

/**
 * Created by Morel on 2015/12/7.
 * 厨师查询自己的菜谱
 */
public class ChefCBAll{
    private String tag;
    private int resultcode;
    private ChefCBMap map;

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

    public ChefCBMap getMap() {
        return map;
    }

    public void setMap(ChefCBMap map) {
        this.map = map;
    }
}
