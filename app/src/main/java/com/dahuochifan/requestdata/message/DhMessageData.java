package com.dahuochifan.requestdata.message;

import android.app.Activity;

import com.dahuochifan.model.message.MessageAll;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;

public class DhMessageData {
	private MessageAll msgAll;
	public int dealData(String jsonStr, Activity activity, Gson gson) {
		msgAll = gson.fromJson(jsonStr, MessageAll.class);
		return msgAll.getResultcode();
	}
	public MessageAll getAll(){
		return msgAll;
	}
}
