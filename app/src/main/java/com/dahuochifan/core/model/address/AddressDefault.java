package com.dahuochifan.core.model.address;

/**
 * Created by Morel on 2015/9/1.
 */
public class AddressDefault {
    private String tag;
    private int resultcode;
    private AddressMap map ;

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

    public AddressMap getMap() {
        return map;
    }

    public void setMap(AddressMap map) {
        this.map = map;
    }
}
