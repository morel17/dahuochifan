package com.dahuochifan.requestdata;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.userinfo.PersonInfo;
import com.dahuochifan.model.userinfo.PersonInfoDetail;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;

public class PersonInfoData {
	private PersonInfo info;
	private SharedPreferences spf;
	private Editor editor;
	PersonInfoDetail personinfo;
	public int dealData(String jsonStr, Activity activity,Gson gson){
		spf = SharedPreferenceUtil.initSharedPerence().init(activity, MyConstant.APP_SPF_NAME);
		editor=spf.edit();
		info=gson.fromJson(jsonStr, PersonInfo.class);
		if(Tools.toInteger(info.getResultcode())==1){
			personinfo=info.getObj();
			if(personinfo!=null){
				SharedPreferenceUtil.initSharedPerence().putUserId(editor,personinfo.getUserids());
				SharedPreferenceUtil.initSharedPerence().putChefIds(editor, personinfo.getChefids());
				SharedPreferenceUtil.initSharedPerence().putUserRole(editor, personinfo.getRole());
				SharedPreferenceUtil.initSharedPerence().putHomeProv(editor, personinfo.getHomeprov());
				SharedPreferenceUtil.initSharedPerence().putCurProv(editor, personinfo.getCurprov());
				SharedPreferenceUtil.initSharedPerence().putToken(editor, personinfo.getToken());
				SharedPreferenceUtil.initSharedPerence().putTempUser(editor, personinfo.getUsername());
				SharedPreferenceUtil.initSharedPerence().putOtherProv(editor, personinfo.getOtherprovs());
				SharedPreferenceUtil.initSharedPerence().putAvatar(editor, personinfo.getAvatar());
				SharedPreferenceUtil.initSharedPerence().putNickname(editor, personinfo.getNickname());
				SharedPreferenceUtil.initSharedPerence().putHomeCity(editor, personinfo.getHomecity());
				SharedPreferenceUtil.initSharedPerence().putCurCity(editor, personinfo.getCurcity());
				SharedPreferenceUtil.initSharedPerence().putMobile(editor, personinfo.getMobile());
				SharedPreferenceUtil.initSharedPerence().putAddressInfo(editor, personinfo.getAddressinfo());
				SharedPreferenceUtil.initSharedPerence().putAvatar(editor, personinfo.getAvatar());
				SharedPreferenceUtil.initSharedPerence().putAge(editor, personinfo.getAge());
				editor.commit();
				Tools.updateInfo(spf);
			}
		}else{
			MainTools.ShowToast(activity, info.getTag());
		}
		return Tools.toInteger(info.getResultcode());
	}
	public int dealData2(String jsonStr, Activity activity,Gson gson){
		spf = SharedPreferenceUtil.initSharedPerence().init(activity, MyConstant.APP_SPF_NAME);
		editor=spf.edit();
		info=gson.fromJson(jsonStr, PersonInfo.class);
		if(Tools.toInteger(info.getResultcode())==1){
			personinfo=info.getObj();
			if(personinfo!=null){
				SharedPreferenceUtil.initSharedPerence().putUserId(editor,personinfo.getUserids());
				SharedPreferenceUtil.initSharedPerence().putChefIds(editor, personinfo.getChefids());
				SharedPreferenceUtil.initSharedPerence().putUserRole(editor, personinfo.getRole());
				SharedPreferenceUtil.initSharedPerence().putHomeProv(editor, personinfo.getHomeprov());
				SharedPreferenceUtil.initSharedPerence().putCurProv(editor, personinfo.getCurprov());
				SharedPreferenceUtil.initSharedPerence().putTempUser(editor, personinfo.getUsername());
				SharedPreferenceUtil.initSharedPerence().putOtherProv(editor, personinfo.getOtherprovs());
				SharedPreferenceUtil.initSharedPerence().putAvatar(editor, personinfo.getAvatar());
				SharedPreferenceUtil.initSharedPerence().putNickname(editor, personinfo.getNickname());
				SharedPreferenceUtil.initSharedPerence().putHomeCity(editor, personinfo.getHomecity());
				SharedPreferenceUtil.initSharedPerence().putCurCity(editor, personinfo.getCurcity());
				SharedPreferenceUtil.initSharedPerence().putMobile(editor, personinfo.getMobile());
				SharedPreferenceUtil.initSharedPerence().putAddressInfo(editor, personinfo.getAddressinfo());
				SharedPreferenceUtil.initSharedPerence().putAvatar(editor, personinfo.getAvatar());
				SharedPreferenceUtil.initSharedPerence().putAge(editor, personinfo.getAge());
				editor.commit();
				Tools.updateInfo(spf);
			}
		}else{
			MainTools.ShowToast(activity, info.getTag());
		}
		return Tools.toInteger(info.getResultcode());
	}
	public PersonInfoDetail getPersonInfoDetail(){
		personinfo=info.getObj();
		return personinfo;
	}
}
