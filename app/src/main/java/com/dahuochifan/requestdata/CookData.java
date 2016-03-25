package com.dahuochifan.requestdata;

import android.app.Activity;

import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.model.ChefMap;
import com.dahuochifan.model.cheflist.ChefAll;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;

import java.util.List;

public class CookData {
	private ChefAll cookall;
	private ChefMap chefMap;


	public int dealData(String jsonStr, Activity activity,Gson gson){
		cookall=gson.fromJson(jsonStr, ChefAll.class);
		return Tools.toInteger(cookall.getResultcode());
		
	}
	public ChefAll getAll(){
		return cookall;
	}

	public int dealMapData(String jsonStr,Activity activity,Gson gson){
		chefMap=gson.fromJson(jsonStr,ChefMap.class);
		return  Tools.toInteger(chefMap.getResultcode());
	}
	public List<ChefList> getChef(){
		return chefMap.getList();
	}
	
}
