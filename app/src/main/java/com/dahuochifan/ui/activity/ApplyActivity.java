package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.liandong.activity.AgeApplyWheelActivity;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.views.dialog.MorelDialog;
import com.dahuochifan.ui.views.dialog.MorelDialog.MorelDialogListener;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ApplyActivity extends BaseActivity {
    private MyAsyncHttpClient client;
    private RequestParams params;

    private RelativeLayout name_rl, phone_rl, address_rl, age_rl;
    private TextView name_tv, phone_tv, address_tv, age_tv;
    private MorelDialog mDialog;
    private boolean canage;
    private RippleView apply_rp;
    private RadioButton man_rb, woman_rb;
    private String genderStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        canage = true;
        name_rl = (RelativeLayout) findViewById(R.id.name_rl);
        phone_rl = (RelativeLayout) findViewById(R.id.phone_rl);
        address_rl = (RelativeLayout) findViewById(R.id.address_rl);
        name_tv = (TextView) findViewById(R.id.name_tv);
        address_tv = (TextView) findViewById(R.id.address_tv);
        phone_tv = (TextView) findViewById(R.id.phone_tv);
        age_rl = (RelativeLayout) findViewById(R.id.age_rl);
        age_tv = (TextView) findViewById(R.id.age_tv);
        apply_rp = (RippleView) findViewById(R.id.apply_rp);
        man_rb = (RadioButton) findViewById(R.id.man_rb);
        woman_rb = (RadioButton) findViewById(R.id.woman_rb);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        btn_listener();
    }

    private void btn_listener() {
        apply_rp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String nameStr = name_tv.getText().toString();
                String ageStr = age_tv.getText().toString();
                String phoneStr = phone_tv.getText().toString();
                String addStr = address_tv.getText().toString();
                if (TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(phoneStr) || TextUtils.isEmpty(addStr)) {
                    MainTools.ShowToast(ApplyActivity.this, "请先填写完整");
                    return;
                }
                if (man_rb.isChecked()) {
                    genderStr = "1";
                }
                if (woman_rb.isChecked()) {
                    genderStr = "0";
                }
                params = ParamData.getInstance().applyNetObj(phoneStr, nameStr, ageStr, genderStr, addStr);
                client.post(MyConstant.URL_APPLY, params, new TextHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        try {
                            JSONObject jobj = new JSONObject(arg2);
                            int request = jobj.getInt("resultcode");
                            String tag = jobj.getString("tag");
                            if (request == 1) {
                                MainTools.ShowToast(ApplyActivity.this, jobj.getString("tag"));
                                finish();
                            } else {
                                MainTools.ShowToast(ApplyActivity.this, jobj.getString("tag"));
                                if (!TextUtils.isEmpty(tag)) {
                                    showTipDialog(ApplyActivity.this, tag, request);
                                } else {
                                    showTipDialog(ApplyActivity.this, "重新登录", request);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                        MainTools.ShowToast(ApplyActivity.this, R.string.interneterror);
                    }
                });
            }
        });

        name_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new MorelDialog(ApplyActivity.this, R.style.mylogoutstyle, "主厨姓名", "", 15, new MorelDialogListener() {

                    @Override
                    public void onClick(View view, EditText et) {
                        switch (view.getId()) {
                            case R.id.ok_tv:
                                if (TextUtils.isEmpty(et.getText().toString())) {
                                    MainTools.ShowToast(ApplyActivity.this, "输入内容不能为空");
                                    break;
                                } else {
                                    mDialog.dismiss();
                                    String content = et.getText().toString();
                                    // 设置昵称
                                    name_tv.setText(content);
                                }
                                break;
                            case R.id.cancel_tv:
                                mDialog.dismiss();
                                break;
                        }
                    }
                });
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (mDialog != null)
                    mDialog.show();
            }
        });
        phone_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog = new MorelDialog(ApplyActivity.this, R.style.mylogoutstyle, "手机号", phone_tv.getText().toString(), 11, new MorelDialogListener() {
                    @Override
                    public void onClick(View view, EditText et) {
                        switch (view.getId()) {
                            case R.id.ok_tv:
                                if (TextUtils.isEmpty(et.getText().toString())) {
                                    MainTools.ShowToast(ApplyActivity.this, "输入内容不能为空");
                                    break;
                                } else {
                                    if (mDialog != null)
                                        mDialog.dismiss();
                                    final String content = et.getText().toString();
                                    if (!MainTools.isMobile(content)) {
                                        MainTools.ShowToast(ApplyActivity.this, "手机格式不正确");
                                        break;
                                    } else {
                                        phone_tv.setText(content);
                                    }
                                }
                                break;
                            case R.id.cancel_tv:
                                if (mDialog != null)
                                    mDialog.dismiss();
                                break;
                        }
                    }
                });
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (mDialog != null)
                    mDialog.show();
            }
        });
        address_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new MorelDialog(ApplyActivity.this, R.style.mylogoutstyle, "详细地址", address_tv.getText().toString(), 0, new MorelDialogListener() {
                    @Override
                    public void onClick(View view, EditText et) {
                        switch (view.getId()) {
                            case R.id.ok_tv:
                                if (TextUtils.isEmpty(et.getText().toString())) {
                                    MainTools.ShowToast(ApplyActivity.this, "输入内容不能为空");
                                    break;
                                } else {
                                    if (mDialog != null)
                                        mDialog.dismiss();
                                    final String content = et.getText().toString();
                                    address_tv.setText(content);
                                }
                                break;
                            case R.id.cancel_tv:
                                if (mDialog != null)
                                    mDialog.dismiss();
                                break;
                        }
                    }
                });
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (mDialog != null)
                    mDialog.show();
            }
        });
        age_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (canage) {
                    canage = false;
                    Intent intent = new Intent(ApplyActivity.this, AgeApplyWheelActivity.class);
                    intent.putExtra("code", MyConstant.REQUESTCODEQ_AGE);
                    startActivityForResult(intent, MyConstant.REQUESTCODEQ_AGE);
                    overridePendingTransition(R.anim.fadein, 0);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyConstant.REQUESTCODEQ_AGE:
                canage = true;
                if (data != null) {
                    if (!TextUtils.isEmpty(data.getExtras().getString("age"))) {
                        String age = data.getExtras().getString("age");
                        age_tv.setText(age);
                    }
                }
                break;
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_apply;
    }

    @Override
    protected String initToolbarTitle() {
        return "网络申请";
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
    protected void onDestroy() {
        super.onDestroy();
        mDialog = null;
    }
}
