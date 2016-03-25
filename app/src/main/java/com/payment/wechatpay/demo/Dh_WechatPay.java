package com.payment.wechatpay.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.utils.MainTools;
import com.nostra13.universalimageloader.utils.L;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Dh_WechatPay {
	Map<String, String> resultunifiedorder;
	StringBuffer sb;
	PayReq req;
	IWXAPI msgApi;
	private Activity activity;
	private String bodyStr,detailStr,priceStr,orderStr;
	
	public Dh_WechatPay(Activity activity) {
		this.activity = activity;
		msgApi = WXAPIFactory.createWXAPI(activity, null);
		req = new PayReq();
		sb=new StringBuffer();
	}
	public void payFunction(String bodyStr, String detailStr, String priceStr, String orderStr) {
		this.bodyStr=bodyStr;
		this.detailStr=detailStr;
		this.priceStr=priceStr;
		this.orderStr=orderStr;
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}
	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		private SweetAlertDialog pDialog;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if(result!=null){
				if(result.get("result_code").equals("FAIL")){
					MainTools.ShowToast(activity, result.get("err_code_des"));
				}else{
					sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
					resultunifiedorder = result;
					genPayReq();
				}
			}

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();//

			Log.e("orion5", entity);
			byte[] buf = Util.httpPost(url, entity);
			if (buf == null) {
				return decodeXml("");
			} else {
				String content = new String(buf);
				Log.e("orion6", content);
				Map<String, String> xml = decodeXml(content);
				Log.e("orionx", xml.toString());
				return xml;
			}
		}
	}
	/**
	 * 配置支付参数
	 */
	private void genPayReq() {
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();// 生成随机字符串
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));// APP ID
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));// 商户ID
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genAppSign(signParams);
		sb.append("sign\n" + req.sign + "\n\n");
		// Log.e("orion4", signParams.toString());
		
		//调用支付
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 生成随机字符串
	 * 
	 * @return
	 */
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion2", appSign);
		return appSign;
	}

	/**
	 * 设置订单参数
	 * 
	 * @return
	 */
	private String genProductArgs() {

		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body", bodyStr));
			packageParams.add(new BasicNameValuePair("detail", detailStr));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", MyConstant.APP_DH_WX_URL));
			packageParams.add(new BasicNameValuePair("out_trade_no", orderStr));
			// Log.e("hehe", genOutTradNo()+"");
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			L.e("wechatInvald===="+priceStr);
			packageParams.add(new BasicNameValuePair("total_fee", priceStr));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);
			return new String(xmlstring.toString().getBytes(), "ISO8859-1");
			// return xmlstring;

		} catch (Exception e) {
			Log.e("tag", "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion1", packageSign);
		return packageSign;
	}
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion3", sb.toString());
		return sb.toString();
	}
	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT :

						break;
					case XmlPullParser.START_TAG :

						if ("xml".equals(nodeName) == false) {
							// 实例化student对象
							xml.put(nodeName, parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG :
						break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion7", e.toString());
		}
		return null;

	}

}
