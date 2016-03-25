package com.dahuochifan.bean;

import java.io.Serializable;

/**
 * Created by admin on 2015/10/29.
 */
public class DhComments implements Serializable {
    private String imgPath;
    private String imgPathBig;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgPathBig() {
        return imgPathBig;
    }

    public void setImgPathBig(String imgPathBig) {
        this.imgPathBig = imgPathBig;
    }
}
