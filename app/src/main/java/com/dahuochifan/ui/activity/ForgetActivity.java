package com.dahuochifan.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 *
 */
public class ForgetActivity extends BaseActivity {
    private TextInputLayout phoneInput, icInput, passInput, pass2Input;
    private EditText phone_edit, ic_edit, pass_edit, pass2_edit;
    private TextView forget_complete_tv, ic_tv;
    private TimeCount time;

    private Gson gson;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private String phone_str;
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private SmsObserver smsObserver;
    public static ForgetActivity instance;
    public Handler smsHandler = new Handler() {
        // 这里可以进行回调的操作
        // TODO

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        smsObserver = new SmsObserver(this, smsHandler);
        instance = this;
        getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
        initViews();
        init();
        listener();
    }

    private void initViews() {
        phone_edit = (EditText) findViewById(R.id.phone_edit);
        ic_edit = (EditText) findViewById(R.id.ic_edit);
        pass_edit = (EditText) findViewById(R.id.pass_edit);
        pass2_edit = (EditText) findViewById(R.id.pass2_edit);
        forget_complete_tv = (TextView) findViewById(R.id.forget_complete_tv);
        ic_tv = (TextView) findViewById(R.id.ic_tv);
        phoneInput = (TextInputLayout) findViewById(R.id.phoneInput);
        icInput = (TextInputLayout) findViewById(R.id.icInput);
        passInput = (TextInputLayout) findViewById(R.id.passInput);
        pass2Input = (TextInputLayout) findViewById(R.id.pass2Input);
        phoneInput.setHint("请输入手机号");
        icInput.setHint("请输入验证码");
        passInput.setHint("密码长度为6到20位");
        passInput.setCounterMaxLength(20);
        passInput.setCounterEnabled(true);
        pass2Input.setHint("请再次输入密码");
        pass2Input.setCounterMaxLength(20);
        pass2Input.setCounterEnabled(true);
    }

    private void init() {
        time = new TimeCount(60000, 1000);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
    }

    private void listener() {
        ic_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndentifycode();
            }
        });
        forget_complete_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetFunction();
            }
        });
    }

    /**
     * 完成忘记密码
     */
    private void forgetFunction() {
        if (TextUtils.isEmpty(phone_edit.getText().toString()) || TextUtils.isEmpty(ic_edit.getText().toString())
                || TextUtils.isEmpty(pass_edit.getText().toString()) || TextUtils.isEmpty(pass2_edit.getText().toString())) {
            MainTools.ShowToast(this, "请填写完整");
            return;
        }
        if (!MainTools.isMobile(phone_edit.getText().toString())) {
            MainTools.ShowToast(this, "手机号码格式错误");
            return;
        }
        String passfirstStr = pass_edit.getText().toString();
        String passsecondStr = pass2_edit.getText().toString();
        if (passfirstStr.length() < 6 || passsecondStr.length() < 6 || passfirstStr.length() > 20 || passsecondStr.length() > 20) {
            MainTools.ShowToast(this, R.string.login_size3);
            return;
        }
        if (!passfirstStr.equals(passsecondStr)) {
            MainTools.ShowToast(this, "密码输入不一致");
            return;
        }
        yanZhen(ic_edit.getText().toString(), phone_edit.getText().toString(), pass_edit.getText().toString());
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
            ic_tv.setText(millisUntilFinished / 1000 + "");
            ic_tv.setTypeface(Typeface.DEFAULT);
            ic_tv.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
        }
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
            MainTools.ShowToast(this, R.string.register_phonenumber_first);
            return;
        }
        if (!MainTools.isMobile(register_phone_incodestr)) {
            MainTools.ShowToast(this, "手机号码格式错误");
            return;
        }
        String json = gson.toJson(ParamData.data.getYzmObj("Android", register_phone_incodestr));
        params.put("param", json);
        try {
            params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(MyConstant.URL_USERSMS2, params, new TextHttpResponseHandler() {
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
                        MainTools.ShowToast(ForgetActivity.this, jobj.getString("tag"));
                    } else if (request == 2) {
                        new AlertDialog.Builder(ForgetActivity.this).setCancelable(false).setTitle("提示").setMessage(jobj.getString("tag"))
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        new AlertDialog.Builder(ForgetActivity.this).setCancelable(false).setTitle("提示").setMessage(jobj.getString("tag"))
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
                new AlertDialog.Builder(ForgetActivity.this).setCancelable(false).setTitle("提示").setMessage("网络不给力,请检查网络连接后重试")
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

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
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
        String where = " address in('10690278075436','10690278405436') AND date >  " + (System.currentTimeMillis() - 1 * 60 * 1000);
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
    }

    private void yanZhen(final String incodestr, final String phoneStr, final String pass) {
        String json = gson.toJson(ParamData.data.indentifyObj(incodestr));
        params.put("param", json);
        try {
            params.put("sign", URLEncoder.encode(SignUtils.sign(json, MyConstant.PUCLIC_KEY), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(MyConstant.URL_USER_CHECK_CODE, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                L.e("arg2===" + arg2);
                try {
                    JSONObject jobj = new JSONObject(arg2);
                    int request = jobj.getInt("resultcode");
                    String tag = jobj.getString("tag");
                    L.e("tag===" + tag);
                    if (request == 1) {
                        changePwd(phoneStr, incodestr, pass);
                    } else {
                        MainTools.ShowToast(ForgetActivity.this, tag);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(ForgetActivity.this, R.string.interneterror);
            }
        });

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_forget;
    }

    @Override
    protected String initToolbarTitle() {
        return "忘记密码";
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

    /**
     * @param passfirstStr 输入的密码
     */
    private void changePwd(String phoneStr, String code, String passfirstStr) {
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
                    MainTools.ShowToast(ForgetActivity.this, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(ForgetActivity.this, R.string.interneterror);
            }
        });
    }
}
