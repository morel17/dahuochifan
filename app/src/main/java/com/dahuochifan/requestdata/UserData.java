package com.dahuochifan.requestdata;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.LoginModel;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;

public class UserData {
	private SharedPreferences spf;
	private SharedPreferenceUtil spf_util;
	private Editor editor;
	private LoginModel info2;
	public UserData() {
		super();
	}
	public boolean dealData(String jsonString, Activity activity) {
		spf_util = SharedPreferenceUtil.initSharedPerence();
		spf = spf_util.init(activity, MyConstant.APP_SPF_NAME);
		editor = spf.edit();
		Gson gson=new Gson();
		info2 = gson.fromJson(jsonString, LoginModel.class);
		if (info2!=null&&info2.getResultCode()==1) {
			MyConstant.user=info2.getObj();
			MyConstant.user.setWechat(false);
			if(!TextUtils.isEmpty(MyConstant.user.getUserids())){
				spf_util.putUserId(editor, MyConstant.user.getUserids());
			}
			spf_util.putLogin(editor, true);
			MyConstant.user.setLogin(true);
			spf_util.putWxLogin(editor, false);
			spf_util.putChefIds(editor, MyConstant.user.getChefids());
			spf_util.putUserRole(editor, MyConstant.user.getRole());
			spf_util.putHomeProv(editor, MyConstant.user.getHomeprov());
			spf_util.putCurProv(editor, MyConstant.user.getCurprov());
			spf_util.putToken(editor, MyConstant.user.getToken());
			spf_util.putTempUser(editor, MyConstant.user.getUsername());
			spf_util.putOtherProv(editor, MyConstant.user.getOtherprovs());
			spf_util.putAvatar(editor, MyConstant.user.getAvatar());
			spf_util.putNickname(editor, MyConstant.user.getNickname());
			spf_util.putHomeCity(editor, MyConstant.user.getHomecity());
			spf_util.putCurCity(editor, MyConstant.user.getCurcity());
			spf_util.putMobile(editor, MyConstant.user.getMobile());
			spf_util.putAddressInfo(editor, MyConstant.user.getAddressinfo());
			spf_util.putAge(editor, MyConstant.user.getAge());
			spf_util.putAvatar(editor,  MyConstant.user.getAvatar());
			editor.commit();
			if(!TextUtils.isEmpty(info2.getTag())){
				MainTools.ShowToast(activity,info2.getTag());
			}
			return true;
		}else{
			MainTools.ShowToast(activity, info2.getTag());
			return false;
		}
	}

	public LoginModel getInfo2() {
		return info2;
	}

	public boolean dealData_register(String jsonString, Activity activity) {
		spf_util = SharedPreferenceUtil.initSharedPerence();
		spf = spf_util.init(activity, MyConstant.APP_SPF_NAME);
		editor = spf.edit();
		Gson gson=new Gson();
		LoginModel info2 = gson.fromJson(jsonString, LoginModel.class);
		if (info2!=null&&info2.getResultCode()==1) {
			spf_util.putTempUser(editor, info2.getObj().getUsername());
			editor.commit();
			return true;
		}else{
			MainTools.ShowToast(activity, info2.getTag());
			return false;
		}
	}
	
}
