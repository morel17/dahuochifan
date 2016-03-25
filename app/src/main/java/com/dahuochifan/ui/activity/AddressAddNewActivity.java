package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.event.AddressEvent;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class AddressAddNewActivity extends BaseActivity {
    private FloatingActionButton confrim_tv;
    private EditText name_et;
    private EditText phone_number_et;
    private EditText twon_et;
    private TextView prov_et;
    private RelativeLayout moren_rl;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private boolean canAdd;
    private ProgressBar pb;
    private String latitude;
    private String longtitude;
    private String locsimple;
    private String locdeatil;
    private RelativeLayout province_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        canAdd = true;
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        confrim_tv = (FloatingActionButton) findViewById(R.id.confrim_tv);
        confrim_tv.setVisibility(View.GONE);
        name_et = (EditText) findViewById(R.id.name_et);
        phone_number_et = (EditText) findViewById(R.id.phone_number_et);
        twon_et = (EditText) findViewById(R.id.twon_et);
        moren_rl = (RelativeLayout) findViewById(R.id.moren_rl);
        prov_et = (TextView) findViewById(R.id.prov_et);
        pb = (ProgressBar) findViewById(R.id.pb);
        province_rl = (RelativeLayout) findViewById(R.id.province_rl);
        listener();
    }

    public void onEventMainThread(AddressEvent event) {
        if (event != null && event.getType() != 0) {
            switch (event.getType()) {
                case MyConstant.EVENTBUS_ADD_ADDR:
                    locsimple = event.getLocSimple();
                    prov_et.setText(locsimple);
                    latitude = event.getLatitude();
                    longtitude = event.getLongtitude();
                    locdeatil = event.getLocAddr();
                    break;
            }
        }

    }

    private void listener() {
        moren_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moren_rl.isSelected()) {
                    moren_rl.setSelected(false);
                } else {
                    moren_rl.setSelected(true);
                }
            }
        });
        province_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressAddNewActivity.this, DhAddressMapActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longtitude", longtitude);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_addressadd;
    }

    @Override
    protected String initToolbarTitle() {
        return "添加地址";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dhsave:
                postAddr();
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void postAddr() {
        if (canAdd) {
            String namestr = name_et.getText().toString();
            String phonestr = phone_number_et.getText().toString();
            String locstr = prov_et.getText().toString();
            String detstr = twon_et.getText().toString();
            if (TextUtils.isEmpty(namestr) || TextUtils.isEmpty(phonestr) || TextUtils.isEmpty(locstr) || TextUtils.isEmpty(detstr) || locstr.equals("地图选择")) {
                MainTools.ShowToast(AddressAddNewActivity.this, "请完整填写");
                return;
            }
            if (!MainTools.isMobile(phonestr)) {
                MainTools.ShowToast(AddressAddNewActivity.this, "手机格式不对");
                return;
            }
            params = ParamData.getInstance().addAdrObj(namestr, phonestr, locdeatil, locsimple, detstr, moren_rl.isSelected(), latitude, longtitude);
            client.post(MyConstant.URL_ADDADDR, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    canAdd = false;
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jobj = new JSONObject(responseString);
                        int resultcode = jobj.getInt("resultcode");
                        String tag = jobj.getString("tag");
                        if (resultcode == 1) {
                            MainTools.ShowToast(AddressAddNewActivity.this, tag);
                            EventBus.getDefault().post(new FirstEvent("AddNew"));
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(tag)) {
                                showTipDialog(AddressAddNewActivity.this, tag, resultcode);
                            } else {
                                showTipDialog(AddressAddNewActivity.this, "重新登录", resultcode);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    MainTools.ShowToast(AddressAddNewActivity.this, R.string.interneterror);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    pb.setVisibility(View.GONE);
                    canAdd = true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dh_addr, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
