package com.dahuochifan.requestdata.activities;

import android.app.Activity;

import com.dahuochifan.model.activities.DhAdAllList;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;

public class DhActivitiesListData {
	private DhAdAllList adAll;
	public int dealData(String jsonStr, Activity activity, Gson gson) {
		adAll = gson.fromJson(jsonStr, DhAdAllList.class);
		return adAll.getResultcode();
	}
	public DhAdAllList getAll(){
		return adAll;
	}
}
