package com.dahuochifan.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.LoginModel;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.UserData;
import com.dahuochifan.requestdata.wechat.WechatData;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mob.tools.utils.UIHandler;
import com.nostra13.universalimageloader.utils.L;
import com.payment.alipay.demo.SignUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements PlatformActionListener, Callback, AMapLocationListener {
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;
    @Bind(R.id.phoneInput)
    TextInputLayout phoneInput;
    @Bind(R.id.phone_edit)
    EditText phone_edit;
    @Bind(R.id.passInput)
    TextInputLayout passInput;
    @Bind(R.id.pass_edit)
    EditText pass_edit;
    @Bind(R.id.login_complete)
    RippleView login_complete_tv;
    @Bind(R.id.start_wxlogin)
    PercentRelativeLayout start_wxlogin;
    @Bind(R.id.register_btn)
    TextView register_btn;
    @Bind(R.id.forget_btn)
    TextView forget_btn;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Context context;
    private ProgressDialog pd;
    private SharedPreferences spf;
    private Gson gson;
    private TelephonyManager tm;
    private String access_tokenStr;
    private SweetAlertDialog pDialog;
    private SharedPreferences.Editor editor;
    private String geoLat, geoLng;
    private boolean hasSc;

    /**
     * 极光推送
     */
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    /**
     * `
     * 关于定位
     */
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

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
                                    L.e("login===" + logs);
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    if (MainTools.isNetworkAvailable(MyApplication.getInstance())) {
                                        sendMessageDelayed(obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                                    } else {
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
                                    L.e(logs);
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    if (MainTools.isNetworkAvailable(MyApplication.getInstance())) {
                                        sendMessageDelayed(obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                                    } else {
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
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        ShareSDK.initSDK(this);
        init();
        initLocation();
    }

    /**
     * 初始化高德定位
     */

    private void initLocation() {
        // 初始化定位，
        mlocationClient = new AMapLocationClient(getApplicationContext());
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        // 设置定位模式为低功耗定位
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位回调监听
        mlocationClient.setLocationListener(this);
        // 设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    private void init() {
        context = this;
        hasSc=true;
        phoneInput.setHint("请输入手机号");
        passInput.setHint("请输入密码");
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.isloging));
        spf = SharedPreferenceUtil.initSharedPerence().init(this, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
        editor.apply();
        if (SharedPreferenceUtil.initSharedPerence().getTempUser(spf).length() > 11) {
            phone_edit.setText("");
        } else {
            phone_edit.setText(SharedPreferenceUtil.initSharedPerence().getTempUser(spf));
        }
        gson = new Gson();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phone_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if (s.length() > 0) {
//					chaone.setVisibility(View.VISIBLE);
//				} else {
//					chaone.setVisibility(View.GONE);
//				}
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pass_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (BuildConfig.LEO_DEBUG) L.e("count" + count);
//				if (s.length() > 0) {
//					chatwo.setVisibility(View.VISIBLE);
//				} else {
//					chatwo.setVisibility(View.GONE);
//				}
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        forget_btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
            }
        });
        register_btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        });
    }

    @OnClick(R.id.login_complete)
    public void login_complete_function() {
        String login_str = phone_edit.getText().toString();
        String pass_str = pass_edit.getText().toString();
        if (TextUtils.isEmpty(login_str) || TextUtils.isEmpty(pass_str)) {
            MainTools.ShowToast(context, R.string.login_request_toast);
            return;
        }
        if (login_str.length() < 6 || pass_str.length() < 6) {
            MainTools.ShowToast(context, R.string.login_size);
            return;
        }
        postLogin(login_str, pass_str);
    }

    @OnClick(R.id.start_wxlogin)
    public void wx_login() {
        if (hasSc)
            authorize(new Wechat(this));
    }

    private void postLogin(String login_str, String pass_str) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pass_edit.getWindowToken(), 0);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        String json;
        String locationInfo = "";
        if (!TextUtils.isEmpty(geoLat) && !TextUtils.isEmpty(geoLng)) {
            locationInfo = geoLng + "," + geoLat;
        } else {
            locationInfo = "";
        }
        String regId;
        if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))) {
            regId = SharedPreferenceUtil.initSharedPerence().getRegisterId(spf);
        } else {
            if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(LoginActivity.this))) {
                regId = JPushInterface.getRegistrationID(LoginActivity.this);
            } else {
                regId = "";
            }
        }
        json = gson.toJson(ParamData.data.getLoginObj(login_str, pass_str, regId, locationInfo), ParamData.class);
        params.put("param", json);
        try {
            params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(MyConstant.URL_LOGIN, params, new TextHttpResponseHandler() {
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
                if (userdata.dealData(responseString, LoginActivity.this)) {
                    if (userdata.getInfo2() != null) {
                        LoginModel model = userdata.getInfo2();
                        if (!TextUtils.isEmpty(model.getObj().getUserids())) {
                            MyHandler handlerx = new MyHandler(LoginActivity.this);
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
                            SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(LoginActivity.this, MyConstant.APP_SPF_NAME);
                            SharedPreferences.Editor editor = spf.edit();
                            if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))) {
                                regId = SharedPreferenceUtil.initSharedPerence().getRegisterId(spf);
                            } else {
                                if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(LoginActivity.this))) {
                                    regId = JPushInterface.getRegistrationID(LoginActivity.this);
                                    SharedPreferenceUtil.initSharedPerence().putRegisterId(editor, regId);
                                    editor.commit();
                                } else {
                                    regId = "";
                                }
                            }
                            tagSet.add(regId);
                            handlerx.sendMessage(handlerx.obtainMessage(MSG_SET_TAGS, tagSet));
                        }

                        if (model.getObj() != null && model.getObj().getMark() != null) {
                            if (!TextUtils.isEmpty(model.getObj().getMark().getNewsnum())) {
                                int newsMark = Tools.toInteger(model.getObj().getMark().getNewsnum());
                                if (newsMark > 0) {
                                    EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                                } else {
                                    EventBus.getDefault().post(new FirstEvent("AnchorHide"));
                                }
                            }
                            if (!TextUtils.isEmpty(model.getObj().getMark().getDinerordernum())) {
                                int dinnerNum = Tools.toInteger(model.getObj().getMark().getDinerordernum());
                                if (model.getObj().getRole() != 2) {
                                    if (dinnerNum > 0) {
                                        EventBus.getDefault().post(new FirstEvent(MyConstant.ANCHORDERSHOW));
                                    } else {
                                        EventBus.getDefault().post(new FirstEvent(MyConstant.ANCHORDERHIDE));
                                    }
                                } else {
                                    EventBus.getDefault().post(new FirstEvent(MyConstant.ANCHORDERHIDE));
                                }
                            }
                        }
                    }
                    AppManager.getAppManager().finishAllActivity();
                    startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
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

    private void authorize(Platform plat) {
        hasSc=false;
        if (!plat.isClientValid()) {
            MainTools.ShowToast(this, R.string.wechat_client_inavailable);
            return;
        }
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                // login(plat.getName(), userId, null);
                return;
            }
        }
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在使用微信登录");
        pDialog.setCancelable(false);
        pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(LoginActivity.this, R.color.blue_btn_bg_color));
        pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.show();
        plat.setPlatformActionListener(this);
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
        ShareSDK.stopSDK(this);
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
    }

    private void login(String plat, HashMap<String, Object> res) {
        // 在这个地方执行登录
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = res;
        UIHandler.sendMessage(msg, this);
    }

    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
//			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            access_tokenStr = platform.getDb().getToken();
            login(platform.getName(), res);
        }
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND: {
                Toast.makeText(this, "userid_found", Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {
                HashMap<String, Object> res = (HashMap<String, Object>) msg.obj;
                System.out.println(res + "=========");
                String openidStr = res.get("openid").toString();
                String wxuseridStr = res.get("unionid").toString();
                String nicknameStr = res.get("nickname").toString();
                String avatarStr = res.get("headimgurl").toString();
                String genderStr = res.get("sex").toString();
                WeChatLogin(access_tokenStr, openidStr, nicknameStr, wxuseridStr, genderStr, avatarStr);
            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(this, "授权取消", Toast.LENGTH_SHORT).show();
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
            hasSc=true;
            break;
            case MSG_AUTH_ERROR: {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                Toast.makeText(this, "授权错误", Toast.LENGTH_SHORT).show();
                hasSc=true;
            }
            break;
            case MSG_AUTH_COMPLETE: {
//				Toast.makeText(this, "授权完成", Toast.LENGTH_SHORT).show();
                hasSc=true;
            }
            break;
        }
        return false;
    }

    private void WeChatLogin(String access_tokenStr, String openidStr, String nicknameStr, String wxuseridStr, String genderStr, String avatarStr) {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        String locationInfo;
        if (!TextUtils.isEmpty(geoLat) && !TextUtils.isEmpty(geoLng)) {
            locationInfo = geoLng + "," + geoLat;
        } else {
            locationInfo = "";
        }
        String json = gson.toJson(ParamData.getInstance().getWechatObj(access_tokenStr, openidStr, locationInfo, JPushInterface.getRegistrationID(LoginActivity.this)),
                ParamData.class);
        params.put("param", json);
        try {
            params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(MyConstant.URL_WECHAT_LOGIN, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                System.out.println(arg2);
                WechatData userdata = new WechatData();
                // if(data.dealData(arg2, LoginActivity.this, gson)==1){
                //
                // }
                if (userdata.dealData(arg2, LoginActivity.this)) {
                    if (userdata.getInfo2() != null && userdata.getInfo2().getObj() != null) {
                        LoginModel model = userdata.getInfo2();
                        if (!TextUtils.isEmpty(model.getObj().getUserids())) {
                            MyHandler handlerx = new MyHandler(LoginActivity.this);
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
                            SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(LoginActivity.this, MyConstant.APP_SPF_NAME);
                            SharedPreferences.Editor editor = spf.edit();
                            if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(spf))) {
                                regId = SharedPreferenceUtil.initSharedPerence().getRegisterId(spf);
                            } else {
                                if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(LoginActivity.this))) {
                                    regId = JPushInterface.getRegistrationID(LoginActivity.this);
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
                    AppManager.getAppManager().finishAllActivity();
                    if (WelcomeActivity.instance != null) {
                        WelcomeActivity.instance.finish();
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                    if (wechat.isValid()) {
                        wechat.removeAccount();
                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                if (wechat.isValid()) {
                    wechat.removeAccount();
                }
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_login;
    }

    @Override
    protected String initToolbarTitle() {
        return "登录";
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

}
