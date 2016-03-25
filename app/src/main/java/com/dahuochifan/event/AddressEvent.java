package com.dahuochifan.event;

import com.dahuochifan.core.model.address.AddressInfo;

/**
 * Created by admin on 2015/8/31.
 */
public class AddressEvent {
    private int type;
    private String locAddr;
    private String locSimple;
    private String longtitude;
    private String latitude;
    private AddressInfo addressInfo;

    public AddressEvent(int type, AddressInfo chefInfo) {
        this.type = type;
        this.addressInfo = chefInfo;
    }

    public AddressEvent(int type, String locAddr, String simpleStr, String longtitude, String latitude) {
        this.type = type;
        this.locAddr = locAddr;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.locSimple = simpleStr;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }
    public String getLocAddr() {
        return locAddr;
    }

    public int getType() {
        return type;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLocSimple() {
        return locSimple;
    }
}
