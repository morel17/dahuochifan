package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.chefinfo.ChefInfo;
import com.dahuochifan.model.chefinfo.ChefInfoAll;
import com.google.gson.Gson;

public class ChefTopDetailData {
    private ChefInfoAll cookall;

    public int dealData(String jsonStr, Activity activity, Gson gson) {
        cookall = gson.fromJson(jsonStr, ChefInfoAll.class);
        return cookall.getResultcode();

    }

    public ChefInfo getAll() {
        return cookall.getObj();
    }

    public String getTag() {
        return cookall.getTag();
    }

    public int getResultCode() {
        return cookall.getResultcode();
    }


}
