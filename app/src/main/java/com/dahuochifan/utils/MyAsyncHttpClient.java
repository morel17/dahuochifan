package com.dahuochifan.utils;

import android.text.TextUtils;

import com.dahuochifan.application.MyApplication;
import com.dahuochifan.api.MyConstant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import cn.jpush.android.api.JPushInterface;

public class MyAsyncHttpClient extends AsyncHttpClient {

	@Override
	public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
		setTimeout(5000);setMaxRetriesAndTimeout(2,4000);
		if(MyConstant.user==null){
			Tools.updateInfo(SharedPreferenceUtil.initSharedPerence().init(MyApplication.getInstance().getApplicationContext(), MyConstant.APP_SPF_NAME));
		}
//		L.e(MyConstant.user.getUserids()+(MyConstant.user==null)+"**"+(TextUtils.isEmpty(MyConstant.user.getUserids())));
		if(TextUtils.isEmpty(JPushInterface.getRegistrationID(MyApplication.getInstance()))){
			this.addHeader("Authorization", "DHCF " + MyConstant.user.getUserids() + "@#" + MyConstant.user.getToken() + "@#" + "Android");
		}else{
			this.addHeader("Authorization", "DHCF " + MyConstant.user.getUserids() + "@#" + MyConstant.user.getToken() + "@#" +"Android_"+JPushInterface.getRegistrationID(MyApplication.getInstance()));
		}
		this.addHeader("Referer","m.dahuochifan.com");
		return super.post(url, params, responseHandler);
	}
}
