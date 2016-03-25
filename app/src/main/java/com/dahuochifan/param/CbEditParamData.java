package com.dahuochifan.param;

import android.content.Context;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.utils.ToolDes;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.payment.alipay.demo.SignUtils;

import java.net.URLEncoder;

import cn.jpush.android.api.JPushInterface;

public class CbEditParamData {
	private String mids;
	private String token;
	private String platform;// 平台
	private String open;
	private String tags;
	private String maxnum;
	private String minspending;
	private String step;
	private String price;
	private String cbids;// 菜谱ids
	private String platform_version;
	private String imei;
	
	public RequestParams cbeditObj(String mids, String token,String cbids,String price,String tags,String maxnum,String minspending,String step,Context context){
		data.setMids(mids);
		data.setToken(token);
		data.setPlatform("Android");
		data.setPlatform_version(MyConstant.PLATFORM_VERSION);
//		if(!TextUtils.isEmpty(MyConstant.user.getRegisterId())){
//			data.setImei(MyConstant.user.getRegisterId());
//		}else{
//			SharedPreferences spf= SharedPreferenceUtil.initSharedPerence().init(MyApplication.getInstance(),MyConstant.APP_SPF_NAME);
//			if(!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))){
//				data.setImei(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf));
//			}else{
//				data.setImei("Android");
//			}
//		}
		data.setImei(JPushInterface.getRegistrationID(context));
		data.setCbids(cbids);
		data.setTags(tags);
		data.setPrice(price);
		data.setMaxnum(maxnum);
		data.setMinspending(minspending);
		data.setStep(step);
		return desParam(data);
	}
	public static CbEditParamData data = new CbEditParamData();

	public static CbEditParamData getInstance() {
		data.init();
		return data;
	}

	private void init() {
		mids=null;
		token=null;
		platform=null;
		open=null;
		tags=null;
		maxnum=null;
		minspending=null;
		step=null;
		price=null;
		cbids=null;
		platform_version=null;
		imei=null;
	}

	public String getMids() {
		return mids;
	}


	public RequestParams desParam(CbEditParamData data) {
		RequestParams params = new RequestParams();
		try {
			Gson gson = new Gson();
			String param = gson.toJson(data, ParamData.class);
			String sign_json = param;
//            L.e("param===" + param);
			if (MyConstant.NOTTEST) {
				ToolDes des;
				des = new ToolDes(MyConstant.user.getToken());
				param = URLEncoder.encode(param, "UTF-8");
				String json = des.encrypt(param);
				json = URLEncoder.encode(json, "UTF-8");
				params.put("param", json);
				params.put("sign", URLEncoder.encode(SignUtils.sign(sign_json, MyConstant.PUCLIC_KEY), "UTF-8"));
			} else {
				params.put("param", "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}
	public void setMids(String mids) {
		this.mids = mids;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getMaxnum() {
		return maxnum;
	}
	public void setMaxnum(String maxnum) {
		this.maxnum = maxnum;
	}
	public String getMinspending() {
		return minspending;
	}
	public void setMinspending(String minspending) {
		this.minspending = minspending;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCbids() {
		return cbids;
	}
	public void setCbids(String cbids) {
		this.cbids = cbids;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getPlatform_version() {
		return platform_version;
	}

	public void setPlatform_version(String platform_version) {
		this.platform_version = platform_version;
	}
}
