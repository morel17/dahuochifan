package com.dahuochifan.requestdata.message;

import android.app.Activity;

import com.dahuochifan.model.message.MsgDetAll;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;

public class DhMsgDet {
	private MsgDetAll msgAll;
	public int dealData(String jsonStr, Activity activity, Gson gson) {
		msgAll = gson.fromJson(jsonStr, MsgDetAll.class);
		if (msgAll.getResultcode() != 1) {
			MainTools.ShowToast(activity, msgAll.getTag());
		}
		return msgAll.getResultcode();
	}
	public MsgDetAll getAll(){
		return msgAll;
	}
}
