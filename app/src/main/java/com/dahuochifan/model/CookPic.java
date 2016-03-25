package com.dahuochifan.model;

import java.io.Serializable;

public class CookPic implements Serializable {
    private String thumb, url;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
