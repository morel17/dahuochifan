package com.dahuochifan.core.requestdata.custom;

import android.app.Activity;

import com.dahuochifan.core.model.custom.ShikeAll;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;

public class ShikeData {
	private ShikeAll shikeall;
	public int getstatus(String jsonStr, Activity activity,Gson gson){
		shikeall=gson.fromJson(jsonStr, ShikeAll.class);
		return shikeall.getResultcode();
	}
	public ShikeAll getObj(){
		return shikeall;
	}
}
