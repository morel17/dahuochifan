package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.TagAll;
import com.google.gson.Gson;

public class TagData {
	private TagAll Tagyall;
	public int dealData(String jsonStr, Activity activity, Gson gson) {
		Tagyall = gson.fromJson(jsonStr, TagAll.class);
		return Tagyall.getResultcode();
	}
	public TagAll getAll() {
		return Tagyall;
	}
}
