package com.dahuochifan.core.model.order;

import java.io.Serializable;

/**
 * Created by admin on 2015/9/17.
 */
public class OrderTrack implements Serializable {
    private String status,url;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
