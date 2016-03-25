package com.dahuochifan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dahuochifan.application.MyApplication;
import com.dahuochifan.api.MyConstant;

public class SharedPreferenceUtil {
	private static SharedPreferenceUtil util = null;

	private SharedPreferenceUtil() {
		super();
	}
	public static SharedPreferenceUtil initSharedPerence() {
		if (util == null) {
			synchronized (SharedPreferenceUtil.class) {
				if (util == null) {
					util = new SharedPreferenceUtil();
				}
			}
		}
		return util;

	}
	public SharedPreferences init(Activity activity,String name){
		SharedPreferences spf;
		if(activity!=null){
			spf=activity.getSharedPreferences(name, Context.MODE_PRIVATE);
		}else{
			spf=MyApplication.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
		}
		return spf;
	}
	public SharedPreferences init(Context activity,String name){
		SharedPreferences spf;
		if(activity!=null){
			spf=activity.getSharedPreferences(name, Context.MODE_PRIVATE);
		}else{
			spf=MyApplication.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
		}
		return spf;
	}
	
	public void putPayType(Editor editor,int type){
		editor.putInt(MyConstant.APP_SPF_PAYTYPE,type);
	}
	public int getPayType(SharedPreferences spf){
		return spf.getInt(MyConstant.APP_SPF_PAYTYPE,-1);
	}
	public void putWxLogin(Editor editor,boolean isWx){
		editor.putBoolean(MyConstant.APP_SPF_ISWX, isWx);
	}
	public boolean getWxLogin(SharedPreferences spf){
		return spf.getBoolean(MyConstant.APP_SPF_ISWX, false);
	}
	public void putAge(Editor editor,String age){
		editor.putString(MyConstant.APP_SPF_AGE, age);
	}
	public String getAge(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_AGE, "");
	}
	public void putInstallInfo(Editor editor,String installinfo){
		editor.putString(MyConstant.APP_SPF_INSTALL, installinfo);
	}
	public String getinstallInfo(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_INSTALL, "");
		
	}
	public void putGDLongitude(Editor editor,String longitude){
		editor.putString(MyConstant.APP_SPF_LONGITUDE, longitude);
	}
	public String getGDLongitude(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_LONGITUDE, "");
	}
	public void putGDLatitude(Editor editor,String latitude){
		editor.putString(MyConstant.APP_SPF_LATITUDE, latitude);
	}
	public String getGDLatitude(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_LATITUDE, "");
	}
	public void putOtherProv(Editor editor,String otherprov){
		editor.putString(MyConstant.APP_SPF_OTHERPROV, otherprov);
	}
	public String getOtherProv(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_OTHERPROV, "");
	}
	public void putToken(Editor editor,String toke){
		editor.putString(MyConstant.APP_SPF_USERTOKEN, toke);
	}
	public String getToken(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_USERTOKEN, "");
	}
	public void putUserId(Editor editor,String userId){
		editor.putString(MyConstant.APP_SPF_USERID, userId);
	}
	public String getUserId(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_USERID, "");
	}
	
	public void putUserRole(Editor editor,int userRole){
		editor.putInt(MyConstant.APP_SPF_ROLE, userRole);
	}
	public int getUserRole(SharedPreferences spf){
		return spf.getInt(MyConstant.APP_SPF_ROLE, -99);
	}
	public void putLoginPhone(Editor editor,String loginphone){
		editor.putString(MyConstant.APP_SPF_LOGINPHONE, loginphone);
	}
	public String getLoginPhone(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_LOGINPHONE, "");
	}
	public void putTempUser(Editor editor,String tempuser){
		editor.putString(MyConstant.APP_SPF_TEMPUSER, tempuser);
	}
	public String getTempUser(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_TEMPUSER, "");
	}
	
	public void putChefIds(Editor editor,String chefids){
		editor.putString(MyConstant.APP_SPF_CHEFIDS, chefids);
	}
	public String getChefIds(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_CHEFIDS, "");
	}
	public void putAddressInfo(Editor editor,String addressinfo){
		editor.putString(MyConstant.APP_SPF_ADDRESSINFO, addressinfo);
	}
	public String getAddressInfo(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_ADDRESSINFO, "");
	}
	public void putHomeProv(Editor editor,String homeprov){
		editor.putString(MyConstant.APP_SPF_HOMEPROV, homeprov);
	}
	public String getHomeProv(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_HOMEPROV, "");
	}
	public void putHomeCity(Editor editor,String homecity){
		editor.putString(MyConstant.APP_SPF_HOMECITY, homecity);
	}
	public String getHomeCity(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_HOMECITY, "");
	}
	public void putCurProv(Editor editor,String cruprov){
		editor.putString(MyConstant.APP_SPF_CURPROV, cruprov);
	}
	public String getCurProv(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_CURPROV, "");
	}
	public void putCurCity(Editor editor,String curcity){
		editor.putString(MyConstant.APP_SPF_CURCITY, curcity);
	}
	public String getCurCity(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_CURCITY, "");
	}
	public void putNickname(Editor editor,String nickname){
		editor.putString(MyConstant.APP_SPF_NICKNAME, nickname);
	}
	public String getNickname(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_NICKNAME, "");
	}
	public void putStatus(Editor editor,String status){
		editor.putString(MyConstant.APP_SPF_STATUS, status);
	}
	public String getStatus(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_STATUS, "");
	}
	public void putIds(Editor editor,String ids){
		editor.putString(MyConstant.APP_SPF_IDS, ids);
	}
	public String getIds(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_IDS, "");
	}
	public void putBg(Editor editor,String bg){
		editor.putString(MyConstant.APP_SPF_BG, bg);
	}
	public String getBg(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_BG, "");
	}
	public void putRole(Editor editor,int role){
		editor.putInt(MyConstant.APP_SPF_ROLE, role);
	}
	public int getRole(SharedPreferences spf){
		return spf.getInt(MyConstant.APP_SPF_ROLE, 0);
	}
	public void putAvatar(Editor editor,String avatar){
		editor.putString(MyConstant.APP_SPF_AVATAR, avatar);
	}
	public String getAvatar(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_AVATAR, "");
	}
	public void putMobile(Editor editor,String mobile){
		editor.putString(MyConstant.APP_SPF_MOBILE, mobile);
	}
	public String getMobile(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_MOBILE, "");
	}
	public void putLogin(Editor editor,boolean islogin){
		editor.putBoolean(MyConstant.APP_SPF_ISLOGIN, islogin);
	}
	public boolean getLogin(SharedPreferences spf){
		return spf.getBoolean(MyConstant.APP_SPF_ISLOGIN, false);
	}
	
	public void putGDProvince(Editor editor,String addr){
		editor.putString(MyConstant.APP_SPF_LOCATION, addr);
	}
	public String getGDProvince(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_LOCATION, "上海");
	}
	public void putGDCity(Editor editor,String city){
		editor.putString(MyConstant.APP_SPF_GDCITY, city);
	}
	public String getGDCity(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_GDCITY, "");
	}
	public void putGDDistrict(Editor editor,String district){
		editor.putString(MyConstant.APP_SPF_GDDISTRICT, district);
	}
	public String getGDDistrict(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_GDDISTRICT, "");
	}

	public void putRegisterId(Editor editor,String registerId){
		editor.putString(MyConstant.APP_SPF_REGISTERID,registerId);
	}
	public String getRegisterId(SharedPreferences spf){
		return spf.getString(MyConstant.APP_SPF_REGISTERID,"");
	}
	public void putPoiName(Editor editor,String poiStr){
		editor.putString(MyConstant.APP_SPF_POINAME,poiStr);
	}
	public String getPoiName(SharedPreferences spf){
		return  spf.getString(MyConstant.APP_SPF_POINAME,"");
	}
}
