package com.dahuochifan.liandong.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.liandong.adapters.ArrayWheelAdapter;
import com.dahuochifan.liandong.widget.OnWheelChangedListener;
import com.dahuochifan.liandong.widget.WheelView;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class AgeWheelActivity extends BaseActivity implements OnWheelChangedListener {
    private WheelView age_wheel;
    private TextView ok_tv;
    private TextView cancel_tv;
    private String age[];
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agewheel);
        initviews();
        setUpListener();
        setUpData();
    }

    private void setUpListener() {
        age_wheel.addChangingListener(this);
        ok_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String agestr = age[age_wheel.getCurrentItem()];
                params = ParamData.getInstance().updateAgeObj(agestr);
                postAge(agestr);
            }
        });
        cancel_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FirstEvent(MyConstant.USER_AGE, ""));
                finish();
                overridePendingTransition(0, R.anim.fadeout);
            }
        });
    }

    private void setUpData() {
        age_wheel.setViewAdapter(new ArrayWheelAdapter<String>(AgeWheelActivity.this, age));
    }

    private void initviews() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        ok_tv = (TextView) findViewById(R.id.ok_tv);
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);
        age_wheel = (WheelView) findViewById(R.id.age_wheel);
        age = new String[]{"60后", "70后", "80后", "90后", "00后"};
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

    }

    private void postAge(final String agestr) {
        client.post(MyConstant.URL_UPDATEPROV, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pDialog = new SweetAlertDialog(AgeWheelActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交修改");
                pDialog.setCancelable(false);
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
                        pDialog.dismiss();
                        MainTools.ShowToast(AgeWheelActivity.this, tag);
                        EventBus.getDefault().post(new FirstEvent(MyConstant.USER_AGE, agestr));
                        finish();
                        overridePendingTransition(0, R.anim.fadeout);
                    } else {
                        pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            EventBus.getDefault().post(new FirstEvent(MyConstant.USER_AGE, ""));
            finish();
            overridePendingTransition(0, R.anim.fadeout);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }
}
