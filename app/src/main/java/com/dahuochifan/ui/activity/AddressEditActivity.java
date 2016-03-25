package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.event.AddressEvent;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class AddressEditActivity extends BaseActivity {
    private FloatingActionButton confrim_tv;
    private EditText name_et;
    private EditText phone_number_et;
    private EditText twon_et;
    private TextView prov_et;
    private RelativeLayout moren_rl;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private AddressInfo info;
    private boolean canAdd;
    private ProgressBar pb;
    private SweetAlertDialog dialog;
    private SweetAlertDialog pdialog;
    private String lontitude;
    private String latitude;
    private String locSimple;
    private String locdetail;
    private RelativeLayout province_rl;
    AddressInfo infox;

    static class MyHandler extends Handler {
        WeakReference<AddressEditActivity> wrReferences;
        WeakReference<SweetAlertDialog> swReferences;

        MyHandler(AddressEditActivity wrReferences, SweetAlertDialog dialog) {
            this.wrReferences = new WeakReference<>(wrReferences);
            this.swReferences = new WeakReference<>(dialog);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE6:
                    swReferences.get().dismiss();
                    wrReferences.get().finish();
                    break;
                case MyConstant.MYHANDLER_CODE4:
                    swReferences.get().dismiss();
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        // //params.prepare(AddressEditActivity.this, params);
        canAdd = true;
        confrim_tv = (FloatingActionButton) findViewById(R.id.confrim_tv);
        name_et = (EditText) findViewById(R.id.name_et);
        phone_number_et = (EditText) findViewById(R.id.phone_number_et);
        twon_et = (EditText) findViewById(R.id.twon_et);
        moren_rl = (RelativeLayout) findViewById(R.id.moren_rl);
        prov_et = (TextView) findViewById(R.id.prov_et);
        pb = (ProgressBar) findViewById(R.id.pb);
        province_rl = (RelativeLayout) findViewById(R.id.province_rl);
        initData();
        listener();
    }

    private void initData() {
        info = (AddressInfo) getIntent().getExtras().getSerializable("obj");
        if (info != null) {
            name_et.setText(info.getName());
            phone_number_et.setText(info.getMobile());
            twon_et.setText(info.getAddrdetail());
            if (TextUtils.isEmpty(info.getLoc_simple())) {
                prov_et.setText("");
            } else {
                prov_et.setText(info.getLoc_simple());
            }

            moren_rl.setSelected(info.isdefault());
            lontitude = info.getLongitude();
            latitude = info.getLatitude();
            if (info.isdefault()) {
                moren_rl.setEnabled(false);
            }
        }
    }

    public void onEventMainThread(AddressEvent event) {
        if (event != null && event.getType() != 0) {
            switch (event.getType()) {
                case MyConstant.EVENTBUS_ADD_ADDR:
                    locSimple = event.getLocSimple();
                    String provStr = event.getLocSimple() + "";
                    prov_et.setText(provStr);
                    lontitude = event.getLongtitude();
                    latitude = event.getLatitude();
                    locdetail = event.getLocAddr();
                    infox = new AddressInfo();
                    infox.setLatitude(latitude);
                    infox.setLoc_detail(locdetail);
                    infox.setLoc_simple(locSimple);
                    infox.setLongitude(lontitude);
                    break;
            }
        }

    }

    private void listener() {
        confrim_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress();
            }
        });
        moren_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moren_rl.isSelected()) {
                    moren_rl.setSelected(false);
                } else {
                    moren_rl.setSelected(true);
                    SnackbarManager.show(Snackbar.with(AddressEditActivity.this).position(Snackbar.SnackbarPosition.BOTTOM).text("请先提交")
                            .textColor(Color.parseColor("#ffffffff")).color(Color.parseColor("#fc7e00")).actionLabel("知道了")
                            .actionColor(Color.parseColor("#ffffffff")).actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                }
                            }).duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                }
            }
        });
        province_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressEditActivity.this, DhAddressMapActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longtitude", lontitude);
                startActivity(intent);
            }
        });
    }

    public void postAddr() {
        if (canAdd) {
            String namestr = name_et.getText().toString();
            String phonestr = phone_number_et.getText().toString();
            String detstr = twon_et.getText().toString();

            String locstr = prov_et.getText().toString();

            if (infox == null) {
                infox = new AddressInfo();
                infox.setLatitude(info.getLatitude());
                infox.setLoc_detail(info.getLoc_detail());
                infox.setLoc_simple(info.getLoc_simple());
                infox.setLongitude(info.getLongitude());
            }
            infox.setAddrdetail(detstr);
            infox.setDaids(info.getDaids());
            infox.setIsdefault(moren_rl.isSelected());
            infox.setMobile(phonestr);
            infox.setName(namestr);
            infox.setStatus("1");
            if (!MainTools.isMobile(phonestr)) {
                MainTools.ShowToast(AddressEditActivity.this, "手机格式不对");
                return;
            }
            if (TextUtils.isEmpty(namestr) || TextUtils.isEmpty(phonestr) || TextUtils.isEmpty(detstr) || TextUtils.isEmpty(locstr)) {
                MainTools.ShowToast(AddressEditActivity.this, "请完整填写");
                return;
            }
            params = ParamData.getInstance().editAdrObj(info.getDaids(), namestr, phonestr, locdetail, locSimple, detstr,
                    moren_rl.isSelected(), latitude, lontitude);

            client.post(MyConstant.URL_EDITADDR, params, new TextHttpResponseHandler() {
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
                            EventBus.getDefault().post(new AddressEvent(MyConstant.EVENTBUS_CHOOSE_ADDR, infox));
                            EventBus.getDefault().post(new FirstEvent("Edit"));
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(tag)) {
                                showTipDialog(AddressEditActivity.this, tag, resultcode);
                            } else {
                                showTipDialog(AddressEditActivity.this, "重新登录", resultcode);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    MainTools.ShowToast(AddressEditActivity.this, R.string.interneterror);
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

    private void deleteFunction() {
        pdialog = new SweetAlertDialog(AddressEditActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在删除");
        pdialog.setCancelable(false);
        pdialog.getProgressHelper().setBarColor(ContextCompat.getColor(AddressEditActivity.this, R.color.blue_btn_bg_color));
        final MyHandler handler = new MyHandler(AddressEditActivity.this, pdialog);
        params = ParamData.getInstance().deleteAddrObj(info.getDaids());
        client.setTimeout(5000);
        client.post(MyConstant.URL_ADDR_DEL, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pdialog.show();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                try {
                    JSONObject jobj = new JSONObject(arg2);
//                    String tag = jobj.getString("tag");
                    int resultcode = jobj.getInt("resultcode");
                    if (resultcode == 1) {
                        EventBus.getDefault().post(new FirstEvent("Edit"));
//                        if (AddressManagerActivity.instance != null) {
//                            AddressManagerActivity.instance.finish();
//                        }
                        pdialog.setTitleText(jobj.getString("tag")).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        Message msg = Message.obtain();
                        msg.what = MyConstant.MYHANDLER_CODE6;
                        handler.sendMessageDelayed(msg, 1500);
                    } else {
                        pdialog.setTitleText(jobj.getString("tag")).changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        Message msg = Message.obtain();
                        msg.what = MyConstant.MYHANDLER_CODE4;
                        handler.sendMessageDelayed(msg, 1500);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                pdialog.setTitleText("网络不给力").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                Message msg = Message.obtain();
                msg.what = MyConstant.MYHANDLER_CODE4;
                handler.sendMessageDelayed(msg, 1500);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismissWithAnimation();
        }
        if (pdialog != null) {
            pdialog.dismissWithAnimation();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_addressadd;
    }

    @Override
    protected String initToolbarTitle() {
        return "编辑地址";
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

    private void deleteAddress() {
        dialog = new SweetAlertDialog(AddressEditActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("是否删除该地址？").setContentText("").setCancelText("取消")
                .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.dismissWithAnimation();
                    }
                }).setConfirmClickListener(new OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.dismissWithAnimation();
                        deleteFunction();

                    }
                });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dh_addr, menu);
        return true;
    }

}
