package com.dahuochifan.model.cookbook;

import java.util.List;

/**
 * Created by admin on 2015/11/19.
 */
public class CBMap {
    private int couponqty;
    private CBeatentimeMap eatentimeMap;
    private List<CBActs> acts;
    private List<CBCookBook> cookbooks;
    private CBChef chef;

    public CBeatentimeMap getEatentimeMap() {
        return eatentimeMap;
    }

    public void setEatentimeMap(CBeatentimeMap eatentimeMap) {
        this.eatentimeMap = eatentimeMap;
    }

    public int getCouponqty() {
        return couponqty;
    }

    public void setCouponqty(int couponqty) {
        this.couponqty = couponqty;
    }

    public List<CBActs> getActs() {
        return acts;
    }

    public void setActs(List<CBActs> acts) {
        this.acts = acts;
    }

    public List<CBCookBook> getCookbooks() {
        return cookbooks;
    }

    public void setCookbooks(List<CBCookBook> cookbooks) {
        this.cookbooks = cookbooks;
    }

    public CBChef getChef() {
        return chef;
    }

    public void setChef(CBChef chef) {
        this.chef = chef;
    }
}
