package com.dahuochifan.requestdata;

import com.dahuochifan.model.CookBookListAll;
import com.dahuochifan.model.CookBookMap;
import com.dahuochifan.model.CookBookPartAll;
import com.google.gson.Gson;

public class CookBookListData {
	private CookBookListAll cookbookall;
	private CookBookPartAll cookbookpart;

	public CookBookListAll getCookBook(Gson gson,String jsonStr){
		cookbookall=gson.fromJson(jsonStr, CookBookListAll.class);
		return cookbookall;
	}
	public CookBookPartAll getCookbookpart(Gson gson,String jsonStr){
		cookbookpart=gson.fromJson(jsonStr,CookBookPartAll.class);
		return  cookbookpart;
	}
	public CookBookMap getMap(CookBookListAll cookbookall){
		CookBookMap map=cookbookall.getMap();
		return map;
	}
}
