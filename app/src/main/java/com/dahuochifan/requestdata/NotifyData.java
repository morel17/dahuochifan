package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.NotifyAll;
import com.google.gson.Gson;

public class NotifyData {
	private NotifyAll notifyall;
	public int dealData(String jsonStr, Activity activity,Gson gson){
		notifyall=gson.fromJson(jsonStr, NotifyAll.class);
		return notifyall.getResultcode();
	}
	public NotifyAll getAll(){
		return notifyall;
	}
}
