package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.UpdateAll;
import com.google.gson.Gson;

public class UpdateData {
	private UpdateAll updateall;
	public int dealData(String jsonStr, Gson gson, Activity activity) {
		try {
			updateall = gson.fromJson(jsonStr, UpdateAll.class);
			return updateall.getResultcode();
		} catch (Exception e) {
		}
		return 0;
	}
	public UpdateAll getObj() {
		return updateall;
	}

}
