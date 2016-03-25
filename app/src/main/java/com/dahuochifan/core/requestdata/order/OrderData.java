package com.dahuochifan.core.requestdata.order;

import android.app.Activity;

import com.dahuochifan.core.model.order.OrderAll;
import com.dahuochifan.model.cookbook.OrderDetailInfo;
import com.google.gson.Gson;

public class OrderData {
	private OrderAll orderall;
	public int getstatus(String jsonStr, Activity activity,Gson gson){
		orderall=gson.fromJson(jsonStr, OrderAll.class);
		return orderall.getResultcode();
	}
	public OrderAll getObj(){
		return orderall;
	}
	private OrderDetailInfo detailInfo;
	public int getDetail(String jsonStr, Activity activity,Gson gson){
		detailInfo=gson.fromJson(jsonStr, OrderDetailInfo.class);
		return detailInfo.getResultcode();
	}

}
