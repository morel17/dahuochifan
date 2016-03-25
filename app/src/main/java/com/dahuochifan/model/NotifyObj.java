package com.dahuochifan.model;

import java.io.Serializable;

public class NotifyObj implements Serializable {
    private String content, cnids;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCnids() {
        return cnids;
    }

    public void setCnids(String cnids) {
        this.cnids = cnids;
    }

}
