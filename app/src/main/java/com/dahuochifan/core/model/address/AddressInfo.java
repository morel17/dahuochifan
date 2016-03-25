package com.dahuochifan.core.model.address;

import java.io.Serializable;

public class AddressInfo implements Serializable {
    private String status, addrdetail, name, daids, userids, mobile, loc_detail;
    private boolean isdefault;
    private String longitude, latitude,loc_simple;

    public String getLoc_simple() {
        return loc_simple;
    }

    public void setLoc_simple(String loc_simple) {
        this.loc_simple = loc_simple;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLoc_detail() {
        return loc_detail;
    }

    public void setLoc_detail(String loc_detail) {
        this.loc_detail = loc_detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddrdetail() {
        return addrdetail;
    }

    public void setAddrdetail(String addrdetail) {
        this.addrdetail = addrdetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDaids() {
        return daids;
    }

    public void setDaids(String daids) {
        this.daids = daids;
    }

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }
}
