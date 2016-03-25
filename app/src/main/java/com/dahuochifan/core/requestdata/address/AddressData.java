package com.dahuochifan.core.requestdata.address;

import android.app.Activity;
import android.location.Address;
import android.provider.Telephony;

import com.dahuochifan.core.model.address.AddressAll;
import com.dahuochifan.core.model.address.AddressDefault;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;

public class AddressData {
	private AddressAll addressall;
	private AddressDefault addressDefault;

	public int getstatus(String jsonStr, Activity activity,Gson gson){
		addressall=gson.fromJson(jsonStr, AddressAll.class);
		return addressall.getResultcode();
	}
	public int getstatusDefault(String jsonStr, Activity activity,Gson gson){
		addressDefault=gson.fromJson(jsonStr, AddressDefault.class);
		return addressDefault.getResultcode();
	}
	public AddressAll getObj(){
		return addressall;
	}
	public AddressDefault getDefaultObj(){
		return  addressDefault;
	}
}
