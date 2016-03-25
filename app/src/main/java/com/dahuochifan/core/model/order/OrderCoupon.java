package com.dahuochifan.core.model.order;

import java.io.Serializable;

/**
 * Created by Morel on 2015/11/25.
 *
 */
public class OrderCoupon implements Serializable {
    private String icon,title,desc,url;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
