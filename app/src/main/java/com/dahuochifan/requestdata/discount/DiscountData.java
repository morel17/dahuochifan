package com.dahuochifan.requestdata.discount;

import android.app.Activity;

import com.dahuochifan.model.discount.DiscountAll;
import com.dahuochifan.model.discount.DiscountVal;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;

/**
 * Created by morel on 2015/11/10.
 */
public class DiscountData {
    private DiscountAll discountAll;
    private DiscountVal discountVal;

    public int dealData(String jsonStr, Activity activity, Gson gson) {
        discountAll = gson.fromJson(jsonStr, DiscountAll.class);
        if (discountAll.getResultcode() != 1) {
            MainTools.ShowToast(activity, discountAll.getTag());
        }
        return discountAll.getResultcode();
    }
    public int dealValData(String jsonStr, Activity activity, Gson gson) {
        discountVal = gson.fromJson(jsonStr, DiscountVal.class);
        if (discountVal.getResultcode() != 1) {
            MainTools.ShowToast(activity, discountVal.getTag());
        }
        return discountVal.getResultcode();
    }

    public DiscountVal getDiscountVal() {
        return discountVal;
    }

    public DiscountAll getDiscountAll() {
        return discountAll;
    }
}
