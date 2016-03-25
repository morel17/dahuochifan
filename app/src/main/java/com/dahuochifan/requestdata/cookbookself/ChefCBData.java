package com.dahuochifan.requestdata.cookbookself;

import android.app.Activity;

import com.dahuochifan.model.cookbookself.ChefCBAll;
import com.google.gson.Gson;

/**
 * Created by Morel on 2015/12/7.
 * 厨师私房菜数据解析
 */
public class ChefCBData {
    private ChefCBAll chefCbAll;
    public int dealData(String jsonStr, Activity activity, Gson gson) {
        chefCbAll = gson.fromJson(jsonStr, ChefCBAll.class);
        return chefCbAll.getResultcode();
    }

    public ChefCBAll getChefCbAll() {
        return chefCbAll;
    }
}
