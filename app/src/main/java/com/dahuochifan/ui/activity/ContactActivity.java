package com.dahuochifan.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.payment.alipay.demo.SignUtils;
import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class ContactActivity extends BaseActivity {
	private EditText register_phone_et;
	private EditText indentify_et;
	private RippleView indentify_tv;
	private RippleView register_next_tv;
	private Context context;
	private Gson gson;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private TimeCount time;
	private Uri SMS_INBOX;
	private int code;
	private SmsObserver smsObserver;
	private SweetAlertDialog pDialog;
	private SharedPreferences spf;
	private Editor editor;
	private String register_phone_incodestr;
	public Handler smsHandler = new Handler() {
		// 这里可以进行回调的操作
		// TODO

	};
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE4 :
					if (pDialog != null) {
						pDialog.dismiss();
					}
					Intent intent = new Intent();
					intent.putExtra("mobile", register_phone_incodestr);
					setResult(code, intent);
					finish();
					break;
				case MyConstant.MYHANDLER_CODE3 :
					if (pDialog != null) {
						pDialog.dismiss();
					}
					break;
				default :
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		initData();
		initViews();
		initBtn();
		SnackbarManager.show(Snackbar.with(context).position(Snackbar.SnackbarPosition.BOTTOM).text("请谨慎更换您的联系方式!!!").textColor(Color.parseColor("#ffffffff"))
				.color(Color.parseColor("#fc7e00")).actionLabel("知道了").actionColor(Color.parseColor("#ffffffff")).actionListener(new ActionClickListener() {
					@Override
					public void onActionClicked(Snackbar snackbar) {
					}
				}).duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE));

	}
	private void initData() {
		time = new TimeCount(60000, 1000);
		code = getIntent().getIntExtra("code", 0);
		spf = SharedPreferenceUtil.initSharedPerence().init(this, MyConstant.APP_SPF_NAME);
		editor = spf.edit();
	}
	private void initViews() {
		context = this;
		gson = new Gson();
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		if (SMS_INBOX == null) {
			SMS_INBOX = Uri.parse("content://sms/");
		}
		smsObserver = new SmsObserver(smsHandler);
		getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
		register_phone_et = (EditText) findViewById(R.id.register_phone_et);
		indentify_et = (EditText) findViewById(R.id.indentify_et);
		indentify_tv = (RippleView) findViewById(R.id.indentify_tv);
		register_next_tv = (RippleView) findViewById(R.id.register_next_tv);
	}
	private void initBtn() {
		indentify_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				indentify_et.setText("");
				register_phone_incodestr = register_phone_et.getText().toString();
				if (TextUtils.isEmpty(register_phone_incodestr)) {
					MainTools.ShowToast(context, R.string.register_phonenumber_first);
					return;
				}
				if (!MainTools.isMobile(register_phone_incodestr)) {
					MainTools.ShowToast(context, "手机号码格式错误");
					return;
				}
				time.start();
				String json = gson.toJson(ParamData.data.getYzmObj("Android", register_phone_incodestr));
				params.put("param", json);
				try {
					params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				client.post(MyConstant.URL_USERSMS2, params, new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						if(BuildConfig.LEO_DEBUG)
							L.e("responseString" + responseString);
						try {
							JSONObject jobj = new JSONObject(responseString);
							int request = jobj.getInt("resultcode");
							if (request == 1) {
								MainTools.ShowToast(context, jobj.getString("tag"));
							} else {
								MainTools.ShowToast(context, jobj.getString("tag"));
								time.cancel();
								indentify_tv.setText("重新获取");
								indentify_tv.setClickable(true);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						MainTools.ShowToast(context, R.string.interneterror);
						time.cancel();
						indentify_tv.setText("重新获取");
						indentify_tv.setClickable(true);
					}
				});
			}
		});
		register_next_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String incodestr = indentify_et.getText().toString();
				register_phone_incodestr = register_phone_et.getText().toString();
				if (TextUtils.isEmpty(incodestr)) {
					MainTools.ShowToast(context, "请输入验证码");
					return;
				}
				if (TextUtils.isEmpty(register_phone_incodestr)) {
					MainTools.ShowToast(context, "请输入手机号");
					return;
				}
				yanZhen(incodestr, register_phone_incodestr);
			}
		});

	}
	private void yanZhen(final String incodestr, final String register_phone_incodestrx) {
		String json = gson.toJson(ParamData.data.indentifyObj(incodestr));
		params.put("param", json);
			try {
				String str=SignUtils.sign(json, MyConstant.PUCLIC_KEY);
				if(!TextUtils.isEmpty(str)){
					params.put("sign", URLEncoder.encode(str, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}

		client.post(MyConstant.URL_USER_CHECK_CODE, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj = new JSONObject(arg2);
					int request = jobj.getInt("resultcode");
					if (request == 1) {
						updateFunction(register_phone_incodestrx);
					} else {
						MainTools.ShowToast(context, jobj.getString("tag"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			private void updateFunction(final String register_phone_incodestrx) {
				params = ParamData.getInstance().updateMobileObj(register_phone_incodestrx);
				client.post(MyConstant.URL_UPDATEPROV, params, new TextHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交修改");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
						pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
						pDialog.show();
					}
					@Override
					public void onSuccess(int arg0, Header[] arg1, String arg2) {
						try {
							JSONObject jobj = new JSONObject(arg2);
							int request = jobj.getInt("resultcode");
							String tag = jobj.getString("tag");
							if (request == 1) {
								pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
								SharedPreferenceUtil.initSharedPerence().putMobile(editor, register_phone_incodestrx);
								MyConstant.user.setMobile(register_phone_incodestrx);
								editor.commit();
								handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
							} else {
								pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
								handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE3, 1500);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
						pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
						handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE3, 1500);
					}
					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
		});

	}
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {// 计时完毕时触发
			indentify_tv.setText("重新获取");
			indentify_tv.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			indentify_tv.setClickable(false);
			indentify_tv.setText("(" + millisUntilFinished / 1000 + ")秒后重新获取");
		}
	}

	class SmsObserver extends ContentObserver {

		public SmsObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			getSmsFromPhone();
		}
	}

	public void getSmsFromPhone() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[]{"body"};// "_id", "address", "person",, "date", "type
		String where = " address in('10690278075436','10690278405436') AND date >  " + (System.currentTimeMillis() - 60 * 1000);
		Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		if (null == cur)
			return;
		if (cur.moveToNext()) {
			// String number = cur.getString(cur.getColumnIndex("address"));// 手机号
			// String name = cur.getString(cur.getColumnIndex("person"));// 联系人姓名列表
			String body = cur.getString(cur.getColumnIndex("body"));
			// 这里我是要获取自己短信服务号码中的验证码~~
			Pattern pattern = Pattern.compile("[^0-9]");
			Matcher matcher = pattern.matcher(body);
			if (matcher.find()) {
				String res = matcher.replaceAll("").trim();
				// mobileText.setText(res);
				indentify_et.setText(res);
			}
		}
		cur.close();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent();
			intent.putExtra("mobile", "");
			setResult(code, intent);
			finish();
		}
		return false;

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (time != null) {
			time.cancel();
		}
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_contact;
	}

	@Override
	protected String initToolbarTitle() {
		return "个人信息";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				Intent intent = new Intent();
				intent.putExtra("mobile", "");
				setResult(code, intent);
				finish();
			default :
				return super.onOptionsItemSelected(item);
		}

	}
}
