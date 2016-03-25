package com.dahuochifan.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.model.LoginModel;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.UserData;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.payment.alipay.demo.SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends BaseActivity implements AMapLocationListener {
    @Bind(R.id.phoneInput)
    TextInputLayout phoneInput;
    @Bind(R.id.phone_edit)
    EditText phone_edit;
    @Bind(R.id.icInput)
    TextInputLayout icInput;
    @Bind(R.id.ic_edit)
    EditText ic_edit;
    @Bind(R.id.passInput)
    TextInputLayout passInput;
    @Bind(R.id.pass_edit)
    EditText pass_edit;
    @Bind(R.id.pass2Input)
    TextInputLayout pass2Input;
    @Bind(R.id.pass2_edit)
    EditText pass2_edit;
    @Bind(R.id.inviteInput)
    TextInputLayout inviteInput;
    @Bind(R.id.ic_tv)
    TextView ic_tv;
    @Bind(R.id.invite_tv)
    TextView invite_tv;
    @Bind(R.id.register_tv)
    RippleView register_tv;
    @Bind(R.id.invite_edit)
    EditText invite_edit;
    private Context context;
    private TimeCount time;
    private Gson gson;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Uri SMS_INBOX;
    private ProgressDialog pd;
    private String geoLat, geoLng;
    private SharedPreferences.Editor editor;
    private TelephonyManager tm;
    /**
     * 极光推送
     */
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;

    static class MyHandler extends Handler {
        WeakReference<Context> wrReferences;

        MyHandler(Context wrReferences) {
            this.wrReferences = new WeakReference<>(wrReferences);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(MyApplication.getInstance(), (String) msg.obj, null, new TagAliasCallback() {
                        @Override
                        public void gotResult(int code, String alias, Set<String> tags) {
                            String logs;
                            switch (code) {
                                case 0:
                                    logs = "Set tag and alias success";
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    if (MainTools.isNetworkAvailable(MyApplication.getInstance())) {
                                        sendMessageDelayed(obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                                    }
                                    break;

                                default:
                            }
                        }
                    });
                    break;
                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(MyApplication.getInstance(), null, (Set<String>) msg.obj, new TagAliasCallback() {
                        @Override
                        public void gotResult(int code, String alias, Set<String> tags) {
                            String logs;
                            switch (code) {
                                case 0:
                                    logs = "Set tag  success";
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    if (MainTools.isNetworkAvailable(MyApplication.getInstance())) {
                                        sendMessageDelayed(obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                                    }
                                    break;

                                default:
                                    logs = "Failed with errorCode = " + code;
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SMS_INBOX == null) {
            SMS_INBOX = Uri.parse("content://sms/");
        }
        super.onCreate(savedInstanceState);
        initData();
        btnListener();
        initLocation();
    }

    /**
     * 初始化高德定位
     */

    private void initLocation() {
        // 初始化定位，
        mlocationClient = new AMapLocationClient(getApplicationContext());
        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        // 设置定位模式为低功耗定位
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位回调监听
        mlocationClient.setLocationListener(this);
        // 设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        AppManager.getAppManager().addActivity(this);
        Handler smsHandler = new Handler();
        SmsObserver smsObserver = new SmsObserver(smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
        gson = new Gson();
        context = this;
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        pd = new ProgressDialog(context);
        pd.setMessage(getResources().getString(R.string.registing));
        phoneInput.setHint("请输入手机号");
        icInput.setHint("请输入验证码");
        passInput.setHint("密码长度为6到20位");
        passInput.setCounterMaxLength(20);
        passInput.setCounterEnabled(true);
        pass2Input.setHint("请再次输入密码");
        pass2Input.setCounterMaxLength(20);
        pass2Input.setCounterEnabled(true);
        inviteInput.setPivotY(0);
        inviteInput.setHint("请输入邀请码");
        ic_tv.setText("获取验证码");
        editor = baseSpf.edit();
        editor.apply();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 按钮的点击监听
     */
    private void btnListener() {
        final PropertyValuesHolder pvhA1 = PropertyValuesHolder.ofFloat("alpha", 0.2f, 1f);
        final PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        ic_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                getIndentifycode();
            }
        });
        register_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                registerFunction();
            }
        });
        invite_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (!inviteInput.isShown()) {
                    inviteInput.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofPropertyValuesHolder(inviteInput, pvhA1, pvhY1).setDuration(666).start();
                }
            }
        });
    }

    /**
     * 实现注册
     */
    private void registerFunction() {
        if (TextUtils.isEmpty(phone_edit.getText().toString()) || TextUtils.isEmpty(ic_edit.getText().toString())
                || TextUtils.isEmpty(pass_edit.getText().toString()) || TextUtils.isEmpty(pass2_edit.getText().toString())) {
            MainTools.ShowToast(this, "请填写完整");
            return;
        }
        if (!MainTools.isMobile(phone_edit.getText().toString())) {
            MainTools.ShowToast(context, "手机号码格式错误");
            return;
        }
        String passfirstStr = pass_edit.getText().toString();
        String passsecondStr = pass2_edit.getText().toString();
        if (passfirstStr.length() < 6 || passsecondStr.length() < 6 || passfirstStr.length() > 20 || passsecondStr.length() > 20) {
            MainTools.ShowToast(context, R.string.login_size3);

            return;
        }
        if (!passfirstStr.equals(passsecondStr)) {
            MainTools.ShowToast(this, "密码输入不一致");
            return;
        }
        yanZhen(ic_edit.getText().toString());

    }

    /**
     * 获取验证码
     */
    public void getIndentifycode() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        ic_edit.setText("");
        String register_phone_incodestr = phone_edit.getText().toString();
        if (TextUtils.isEmpty(register_phone_incodestr)) {
            MainTools.ShowToast(context, R.string.register_phonenumber_first);
            return;
        }
        if (!MainTools.isMobile(register_phone_incodestr)) {
            MainTools.ShowToast(context, "手机号码格式错误");
            return;
        }

        String json = gson.toJson(ParamData.data.getYzmObj("Android", register_phone_incodestr));
        params.put("param", json);
        try {
            params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(MyConstant.URL_GETYZM, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                time.start();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    if (request == 1) {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                    } else if (request == 2) {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setMessage(jobj.getString("tag"))
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setMessage(jobj.getString("tag"))
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        time.cancel();
                                        ic_tv.setText("重新获取");
                                        ic_tv.setClickable(true);
                                        ic_tv.setEnabled(true);
                                    }
                                }).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setMessage("网络不给力,请检查网络连接后重试")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                time.cancel();
                                ic_tv.setText("重新获取");
                                ic_tv.setClickable(true);
                                ic_tv.setEnabled(true);
                            }
                        }).create().show();
            }
        });
    }

    /**
     * @param incodestr 验证码
     */
    private void yanZhen(final String incodestr) {
        String json = gson.toJson(ParamData.data.indentifyObj(incodestr));
        params.put("param", json);
        try {
            if (SignUtils.sign(json, MyConstant.PUCLIC_KEY) != null) {
                params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
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
                        postRegister(incodestr);
                    } else {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(RegisterActivity.this, R.string.interneterror);
            }
        });

    }

    /**
     * 注册请求
     *
     * @param incodestr 验证码
     */
    private void postRegister(String incodestr) {
        String myImei;
        SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(RegisterActivity.this, MyConstant.APP_SPF_NAME);
        SharedPreferences.Editor editor = spf.edit();
        if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))) {
            myImei = SharedPreferenceUtil.initSharedPerence().getRegisterId(spf);
        } else {
            if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(RegisterActivity.this))) {
                myImei = JPushInterface.getRegistrationID(RegisterActivity.this);
                SharedPreferenceUtil.initSharedPerence().putRegisterId(editor, myImei);
                editor.commit();
            } else {
                myImei = "";
            }
        }
        String locationInfo;
        if (!TextUtils.isEmpty(geoLat) && !TextUtils.isEmpty(geoLng)) {
            locationInfo = geoLng + "," + geoLat;
        } else {
            locationInfo = "";
        }
        String inviteStr = invite_edit.getText().toString();
        String json = gson.toJson(ParamData.getInstance().getRegisterObj("Android", phone_edit.getText().toString(), pass_edit.getText().toString(), incodestr, myImei, locationInfo, inviteStr));
        params.put("param", json);
        try {
            params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(MyConstant.URL_REGISTER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (pd != null && !pd.isShowing()) {
                    pd.show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                UserData userdata = new UserData();
                if (userdata.dealData(responseString, RegisterActivity.this)) {
                    LoginModel model = userdata.getInfo2();
                    if (model != null) {
                        if (!TextUtils.isEmpty(model.getObj().getUserids())) {
                            MyHandler handlerx = new MyHandler(RegisterActivity.this);
                            handlerx.sendMessage(handlerx.obtainMessage(MSG_SET_ALIAS, model.getObj().getUserids()));
                            // 调用JPush API设置Tag
                            Set<String> tagSet = new LinkedHashSet<String>();
                            if (!TextUtils.isEmpty(tm.getDeviceId())) {
                                tagSet.add(tm.getDeviceId());
                            }
                            if (!TextUtils.isEmpty(MyConstant.device)) {
                                tagSet.add(MyConstant.device);
                            }
                            if (!TextUtils.isEmpty(MyConstant.user.getUsername()) && MyConstant.user.getUsername().length() == 11) {
                                tagSet.add(MyConstant.user.getUsername());
                            } else {
                                tagSet.add("weixin");
                            }
                            tagSet.add("Android");
                            String regId;
                            SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(RegisterActivity.this, MyConstant.APP_SPF_NAME);
                            SharedPreferences.Editor editor = spf.edit();
                            if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))) {
                                regId = SharedPreferenceUtil.initSharedPerence().getRegisterId(spf);
                            } else {
                                if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(RegisterActivity.this))) {
                                    regId = JPushInterface.getRegistrationID(RegisterActivity.this);
                                    SharedPreferenceUtil.initSharedPerence().putRegisterId(editor, regId);
                                    editor.commit();
                                } else {
                                    regId = "";
                                }
                            }
                            tagSet.add(regId);
                            handlerx.sendMessage(handlerx.obtainMessage(MSG_SET_TAGS, tagSet));
                        }
                    }
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    AppManager.getAppManager().finishAllActivity();
                    if (WelcomeActivity.instance != null) {
                        WelcomeActivity.instance.finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
        }
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (location.getErrorCode() == 0) {
                geoLat = location.getLatitude() + "";
                geoLng = location.getLongitude() + "";
                if (!TextUtils.isEmpty(geoLng) && !TextUtils.isEmpty(geoLat)) {
                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, geoLng);
                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, geoLat);
                    editor.commit();
                }
            }
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            ic_tv.setText("重新获取");
            ic_tv.setTypeface(Typeface.DEFAULT);
            ic_tv.setClickable(true);
            ic_tv.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            ic_tv.setClickable(false);
            String icStr = millisUntilFinished / 1000 + "";
            ic_tv.setText(icStr);
            ic_tv.setTypeface(Typeface.DEFAULT_BOLD);
            ic_tv.setEnabled(false);
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
                ic_edit.setText(res);
            }
        }
        cur.close();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_register;
    }

    @Override
    protected String initToolbarTitle() {
        return "注册";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
