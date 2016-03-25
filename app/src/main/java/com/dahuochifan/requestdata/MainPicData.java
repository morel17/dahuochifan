package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.MainPicAll;
import com.dahuochifan.model.TagAll;
import com.google.gson.Gson;

public class MainPicData {
	private MainPicAll Tagyall;
	public int dealData(String jsonStr, Activity activity, Gson gson) {
		Tagyall = gson.fromJson(jsonStr, MainPicAll.class);
		return Tagyall.getResultcode();
	}
	public MainPicAll getAll() {
		return Tagyall;
	}
}
