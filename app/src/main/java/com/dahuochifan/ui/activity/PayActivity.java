package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bear.risenumbertest.lib.RiseNumberTextView;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SystemBarTintManager;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PayActivity extends AppCompatActivity {
	private TextView tx_tv, pay_tv;
	private TextView title_tv;
	private RelativeLayout back_rl;
	private RiseNumberTextView price_tv;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private SwipeRefreshLayout swipe;
	private RelativeLayout title_rl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWindow();
		setContentView(R.layout.activity_pay);
		AppManager.getAppManager().addActivity(this);
		tx_tv = (TextView) findViewById(R.id.tixian_tv);
		pay_tv = (TextView) findViewById(R.id.zhangdan_tv);
		title_tv = (TextView) findViewById(R.id.title_tv);
		back_rl = (RelativeLayout) findViewById(R.id.back_rl);
		price_tv = (RiseNumberTextView) findViewById(R.id.price_tv);
		swipe=(SwipeRefreshLayout)findViewById(R.id.swipe);
		title_rl=(RelativeLayout)findViewById(R.id.title_rl);
		swipe.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		title_tv.setText("收入");
		client=new MyAsyncHttpClient();
		params=new RequestParams();
//		price_tv.withNumber(0.00f);
//		price_tv.setDuration(1500);
		getPayNumber();
		btn_listener();
		swipe.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getPayNumber();
			}
		});
	}
	private void initWindow() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.transparent));
			tintManager.setStatusBarTintEnabled(true);
		}
	}
	private void getPayNumber() {
		params=ParamData.getInstance().getNormalObj();
		client.post(MyConstant.URL_GETPAY, params,new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				price_tv.withNumber(0.00f);
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj=new JSONObject(arg2);
					int resultcode=jobj.getInt("resultcode");
					String tagStr=jobj.getString("tag");
					if(resultcode==0){
						MainTools.ShowToast(PayActivity.this, tagStr);
						price_tv.withNumber(0.00f);
					}else if(resultcode==1){
						String objStr=jobj.getString("obj");
						if(!TextUtils.isEmpty(objStr)){
							JSONObject jobj2=new JSONObject(objStr);
							double total=jobj2.getDouble("total");
							double discount=jobj2.getDouble("discount");
							double pay_fee=total+discount;
							if(pay_fee>0){
								price_tv.withNumber((float)pay_fee);
							}else{
								price_tv.withNumber(0.00f);
							}
						}else{
							price_tv.withNumber(0.00f);
						}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(PayActivity.this, R.string.interneterror);
				price_tv.withNumber(0.00f);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				price_tv.setDuration(1500);
				price_tv.start();
				swipe.setRefreshing(false);
			}
		});
	}
	private void btn_listener() {
		title_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		back_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tx_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainTools.ShowToast(PayActivity.this, "暂不支持提现功能");
			}
		});
		pay_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent_zero = new Intent(PayActivity.this, MeTabCookActivity.class);
				intent_zero.putExtra("index", 1);
				startActivity(intent_zero);
			}
		});
	}
}
