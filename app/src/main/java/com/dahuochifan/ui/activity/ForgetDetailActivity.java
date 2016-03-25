package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.payment.alipay.demo.SignUtils;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class ForgetDetailActivity extends BaseActivity {
	private TextView register_second_phone;
	private EditText pass_first_et;
	private EditText pass_second_et;
	private String phoneStr;
	private TextView register_complete_tv;
	private Context context;
	private String code;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		initData();
		initViews();
		listeners();
	}
	private void initData() {
		Intent intent = getIntent();
		phoneStr = intent.getExtras().getString("phone");
		code = intent.getExtras().getString("code");
		context = this;
	}
	private void initViews() {
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		gson = new Gson();
		register_second_phone = (TextView) findViewById(R.id.register_second_phone);
		pass_first_et = (EditText) findViewById(R.id.pass_first_et);
		pass_second_et = (EditText) findViewById(R.id.pass_second_et);
		register_second_phone.setText(phoneStr);
		register_complete_tv = (TextView) findViewById(R.id.register_complete_tv);
	}
	private void listeners() {
		register_complete_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String passfirstStr = pass_first_et.getText().toString();
				String passsecondStr = pass_second_et.getText().toString();
				if (TextUtils.isEmpty(passfirstStr) || TextUtils.isEmpty(passsecondStr)) {
					MainTools.ShowToast(context, R.string.register_complete_first);
					return;
				}
				if (passfirstStr.length() < 6 || passsecondStr.length() < 6 || passfirstStr.length() > 20 || passsecondStr.length() > 20) {
					MainTools.ShowToast(context, R.string.login_size3);
					return;
				}
				if (!passfirstStr.equals(passsecondStr)) {
					MainTools.ShowToast(context, R.string.register_password_diff);
					return;
				}
				changePwd(passfirstStr);

			}

			private void changePwd(String passfirstStr) {
				String json = gson.toJson(ParamData.data.changePwdObj(phoneStr, code, passfirstStr));
				params.put("param", json);
				try {
					params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				client.post(MyConstant.URL_CHANGEPWD, params, new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						try {
							JSONObject jobj = new JSONObject(arg2);
							int resultcode = jobj.getInt("resultcode");
							String tag = jobj.getString("tag");
							if (resultcode == 1) {
								if (ForgetActivity.instance != null) {
									ForgetActivity.instance.finish();
								}
								finish();
							}
							MainTools.ShowToast(context, tag);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						MainTools.ShowToast(context, R.string.interneterror);
					}
				});
			}
		});
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_forgetdetail;
	}

	@Override
	protected String initToolbarTitle() {
		return "忘记密码";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				finish();
			default :
				return super.onOptionsItemSelected(item);
		}
	}
}
