package com.dahuochifan.ui.activity;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.UpdateAll;
import com.dahuochifan.requestdata.UpdateData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.UpdateManager;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * @author morel
 */
public class AboutActivity extends BaseActivity {
    private static MyAsyncHttpClient client;
    private static Gson gson;
    private PackageInfo pinfo;
    private PackageManager manager;
    private SweetAlertDialog pDialog;

    static class MyHandler extends Handler {
        WeakReference<AboutActivity> wrReferences;
        WeakReference<SweetAlertDialog> swReferences;

        MyHandler(AboutActivity wrReferences, SweetAlertDialog dialog) {
            this.wrReferences = new WeakReference<>(wrReferences);
            this.swReferences = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstant.msg_findnewversion:
                    swReferences.get().dismiss();
                    UpdateManager mUpdateManager = new UpdateManager(wrReferences.get());
                    mUpdateManager.checkUpdateInfo();
                    break;
                case MyConstant.msg_notfindnewversion:
                    swReferences.get().dismiss();
                    MainTools.ShowToast(wrReferences.get(), "已经是最新版本");
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        client = new MyAsyncHttpClient();
        gson = new Gson();
        RelativeLayout welcome_rl = (RelativeLayout) findViewById(R.id.welcome_rl);
        RelativeLayout check_rl = (RelativeLayout) findViewById(R.id.check_rl);
        RelativeLayout point_rl = (RelativeLayout) findViewById(R.id.point_rl);
        TextView app_tv = (TextView) findViewById(R.id.app_tv);
        manager = getPackageManager();
        try {
            pinfo = manager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String apptvStr = "搭伙吃饭" + pinfo.versionName;
        app_tv.setText(apptvStr);
        welcome_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, SplashActivity.class));
            }
        });
        check_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        point_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(AboutActivity.this, "无法打开应用市场 !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void update() {
        manager = getPackageManager();
        try {
            pinfo = manager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        client.setTimeout(5000);
        client.post(MyConstant.URL_UPDATE, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pDialog = new SweetAlertDialog(AboutActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(AboutActivity.this, R.color.blue_btn_bg_color));
                pDialog.setTitleText("正在检查更新").setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final UpdateData data = new UpdateData();
                if (data.dealData(responseString, gson, AboutActivity.this) == 1) {
                    final MyHandler handler = new MyHandler(AboutActivity.this, pDialog);
                    new Thread() {
                        UpdateAll updateall = data.getObj();

                        public void run() {
                            UpdateManager.checkVersion(handler, pinfo, updateall);
                        }
                    }.start();
                } else {
                    if (data.getObj() != null && !TextUtils.isEmpty(data.getObj().getTag())) {
                        showTipDialog(AboutActivity.this, data.getObj().getTag(), data.getObj().getResultcode());
                    } else {
                        showTipDialog(AboutActivity.this, "重新登录", data.getObj().getResultcode());
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(AboutActivity.this, R.string.interneterror);
                pDialog.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_about;
    }

    @Override
    protected String initToolbarTitle() {
        return "关于产品";
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
