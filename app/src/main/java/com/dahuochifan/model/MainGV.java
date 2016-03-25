package com.dahuochifan.model;

import java.io.Serializable;

public class MainGV implements Serializable {
    private String prov;
    private boolean isselect;

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public boolean isIsselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }
}
