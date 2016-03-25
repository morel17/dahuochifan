package com.dahuochifan.requestdata.cookbook;

import android.app.Activity;

import com.dahuochifan.model.cookbook.CBAll;
import com.google.gson.Gson;

/**
 * Created by Morel on 2015/11/19.
 *
 */
public class CBData {
    private CBAll cbAll;
    public int dealData(String jsonStr, Activity activity,Gson gson) {
        cbAll = gson.fromJson(jsonStr, CBAll.class);
        return cbAll.getResultcode();
    }

    public CBAll getCbAll() {
        return cbAll;
    }
}
