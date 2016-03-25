package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.comment.CommentsAll;
import com.google.gson.Gson;

public class CommentsData {
	private CommentsAll commentall;
	public int dealData(String jsonStr, Activity activity,Gson gson){
		commentall=gson.fromJson(jsonStr, CommentsAll.class);
		return commentall.getResultcode();
	}
	public CommentsAll getAll(){
		return commentall;
	}
}
