package com.dahuochifan.model.cookbook;

import java.util.List;

/**
 * Created by admin on 2016/1/5.
 */
public class CBeatentimeMap {
    private boolean ischeck;
    private List<CBTime> lunchtimes,dinnertimes;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public List<CBTime> getLunchtimes() {
        return lunchtimes;
    }

    public void setLunchtimes(List<CBTime> lunchtimes) {
        this.lunchtimes = lunchtimes;
    }

    public List<CBTime> getDinnertimes() {
        return dinnertimes;
    }

    public void setDinnertimes(List<CBTime> dinnertimes) {
        this.dinnertimes = dinnertimes;
    }
}
