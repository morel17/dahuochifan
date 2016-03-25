package com.payment.alipay.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.dahuochifan.api.MyConstant;

public class Dh_Alipay {
	// 商户PID
	public static final String PARTNER = "2088911778744576";
	// 商户收款账号
	public static final String SELLER = "dahuochifanglobal@163.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOJA/1Xhydsstf/Ghy5c45ELSvXHLMtkkPl+w1HXmNL9YzVJwOg+UrmhOqBeuDfjzjCg8/IlTCIFKZVItuki4Laxoj4t0y4D0UU9bA7Htb35piRxUZpkgWvpy+5t0Nwxt/5rktlwReRPfUz5huxceFriFkQRgULm2RGm2Ei6rtOnAgMBAAECgYBqTa+qYlO9Lty0OEr7dZlWZL/ripF5Xt7e2rhak7myKGucdfK1K9MidKZfAyH64IXYQ0da1jtXIKXRBkEqeMCrM3R2UF17ZifqoovZAVncyFQ6HzgItFunAskPV9ImJexL3k3/bYThK77F0w290wWn3rBRgotUBWcJIM9KPqG3wQJBAP7VBJKoozcWc84sAdm2YyCcCw5yUtQRBcDECVzZkkYNN9C2eZqFiJu74HjszopvREf+gf+uXN2AfU6d52c1Vn0CQQDjSnM7F9JEwRBES/4wBNMDuyFnxqvTchmvlOFEkOEIUBtRatv7OkiKuAhjcM5Vbo9Ian7vuuZt84/SvMhNUpfzAkAbhZoW1Mu72Vrse2g/wSj3jqLLDqHPcX8zkHiKZJ0dn7PVgL+lZqKYXzQhG0I7RUUUmXIk0QoKiiP+Z6NkTChpAkB6p2SDkRlRLPZARNoIw7Gzo7olLFrxIfOmgqsFu0EUoVX8m7PWYN9grGsqbmeKRr1fkMirSaqrzvHgASv+PgHtAkEAxprQVmg76MdrTdBZgJJvwve0uA/fdiQyQ1+7r3lID0eaSCooOTGFs+2ICmzj0yDzMzXZqOnpIqXWqDAuIDQQbg==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";



	private static final int SDK_PAY_FLAG = 101;
	private static final int SDK_CHECK_FLAG = 102;

	private Activity activity;
	private Handler mHandler;
	public Dh_Alipay(Activity activity, Handler handler) {
		super();
		this.activity = activity;
		this.mHandler=handler;
	}
	public void checkFunction() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(activity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();
	}
	public void payFunction(String foodname, String fooddetail, String foodprice,String orderNumber) {

		// 订单
		String orderInfo = getOrderInfo(foodname, fooddetail, foodprice,orderNumber);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	/**
	 * create the order info. 创建订单信息
	 * @param orderNumber 
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price, String orderNumber) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNumber + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" +  MyConstant.APP_DH_ALI_URL + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
