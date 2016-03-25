package com.dahuochifan.ui.activity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.model.MainPicAll;
import com.dahuochifan.model.UpdateAll;
import com.dahuochifan.model.wechat.MainPicObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.MainPicData;
import com.dahuochifan.requestdata.UpdateData;
import com.dahuochifan.ui.adapter.WelcomeAdapter;
import com.dahuochifan.ui.view.indicator.CirclePageIndicator;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.SystemBarTintManager;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.utils.UpdateManager;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cz.msebera.android.httpclient.Header;

public class WelcomeActivity extends AppCompatActivity {
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.indicator)
    CirclePageIndicator indicator;
    private ArrayList<String> ImgList;
    private WelcomeAdapter adapter;
    private int curPage;
    private int preState;
    private MyApplication myApplication;
    public static WelcomeActivity instance;
    private PackageInfo pinfo;
    private PackageManager manager;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private TelephonyManager tm;
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    private static final String TAG = "JPush";
    private SharedPreferences spf;
    private boolean canShow;

    static class MyHandler extends Handler {
        WeakReference<Context> wrReferences;
        WeakReference<Boolean> boolReferences;

        MyHandler(Context wrReferences, boolean canShowx) {
            this.wrReferences = new WeakReference<>(wrReferences);
            this.boolReferences = new WeakReference<>(canShowx);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                case MyConstant.msg_findnewversion:
                    if (this.boolReferences != null && this.boolReferences.get()) {
                        UpdateManager mUpdateManager = new UpdateManager(wrReferences.get());
                        mUpdateManager.checkUpdateInfo();
                    }
                    break;
                case MyConstant.msg_notfindnewversion:
                    break;

                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(MyApplication.getInstance(), (String) msg.obj, null, new TagAliasCallback() {
                        @Override
                        public void gotResult(int code, String alias, Set<String> tags) {
                            String logs;
                            switch (code) {
                                case 0:
                                    logs = "Set tag and alias success";
                                    Log.i(TAG, logs);
                                    break;

                                case 6002:
                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    Log.i(TAG, logs);
                                    if (MainTools.isNetworkAvailable(MyApplication.getInstance())) {
                                        sendMessageDelayed(obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                                    } else {
                                        Log.i(TAG, "No network");
                                    }
                                    break;

                                default:
                                    logs = "Failed with errorCode = " + code;
                                    Log.e(TAG, logs);
                            }
                        }
                    });
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(MyApplication.getInstance(), null, (Set<String>) msg.obj, new TagAliasCallback() {
                        @Override
                        public void gotResult(int code, String alias, Set<String> tags) {
                            switch (code) {
                                case 0:
//                                    logs = "Set tag and alias success";
                                    break;

                                case 6002:
//                                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                                    if (MainTools.isNetworkAvailable(MyApplication.getInstance())) {
                                        sendMessageDelayed(obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                                    }
                                    break;

                                default:
//                                    logs = "Failed with errorCode = " + code;
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
        canShow = true;
        initJpush();
        myApplication = (MyApplication) this.getApplication();
        myApplication.bindMyService();
        spf = getSharedPreferences(MyConstant.APP_SPF_NAME, MODE_PRIVATE);
        MyConstant.user.setUserids(SharedPreferenceUtil.initSharedPerence().getUserId(spf));
        initTopViews();
        init();
        Tools.updateInfo(spf);
        if (SharedPreferenceUtil.initSharedPerence().getLogin(spf)) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            update();
//            initTopViews();
            setContentView(R.layout.activity_welcome);
            ButterKnife.bind(this);
            instance = this;
            getPic();
//            initViewPager();
        }
    }

    private void getPic() {
        params = ParamData.getInstance().getPicObj();
        client.post(MyConstant.APP_MAIN_PIC, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (ImgList == null) {
                    ImgList = new ArrayList<>();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(WelcomeActivity.this, "图片加载失败");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                MainPicData data = new MainPicData();
                if (data.dealData(responseString, WelcomeActivity.this, gson) == 1) {
                    MainPicAll picAll = data.getAll();
                    List<MainPicObj> list = picAll.getList();
                    int w = getResources().getDimensionPixelOffset(R.dimen.width_80_80) / 10 * 8;
                    int h = getResources().getDimensionPixelOffset(R.dimen.height_80_80) / 10 * 8;
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            String imgUrl = list.get(i).getUrl() + "?imageView2/1/w/" + w + "/h/"
                                    + h + "/q/" + MyConstant.QUALITY;
                            ImgList.add(i, imgUrl);
                        }

                        initViewPager();
                    }
                }
            }
        });
    }

    public void initTopViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(ContextCompat.getColor(this, R.color.black));
            // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void init() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();

        final Set<String> tagSet = new LinkedHashSet<>();
        if (!TextUtils.isEmpty(tm.getDeviceId())) {
            tagSet.add(tm.getDeviceId());
        }
        if (!TextUtils.isEmpty(MyConstant.device)) {
            tagSet.add(MyConstant.device);
        }
        final MyHandler handlerx = new MyHandler(WelcomeActivity.this, canShow);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handlerx.sendMessage(handlerx.obtainMessage(MSG_SET_TAGS, tagSet));
            }
        }).start();

    }

    private void initViewPager() {
        adapter = new WelcomeAdapter(getSupportFragmentManager());
        adapter.addAll(ImgList);
        pager.removeAllViews();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setIndicatorType(CirclePageIndicator.IndicatorType.CIRCLE);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                indicator.updateIndicator(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                curPage = arg0;
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (preState == 1 && arg0 == 0 && curPage == ImgList.size() - 1) {
                    Intent intent = new Intent(WelcomeActivity.this, JustLookActivity.class);
                    startActivity(intent);
                }
                preState = arg0;
            }
        });
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        canShow = false;
        if (UpdateManager.dialog != null && !SharedPreferenceUtil.initSharedPerence().getLogin(spf)) {
            UpdateManager.dialog.dismiss();
        }
    }

    @OnClick(R.id.login_btn)
    void login_function() {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    @OnClick(R.id.register_btn)
    void register_function() {
        startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.look_btn)
    void look_function() {
        Intent intent = new Intent(WelcomeActivity.this, JustLookActivity.class);
        startActivity(intent);
    }

    private void update() {
        manager = getPackageManager();
        try {
            pinfo = manager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        client.setTimeout(5000);
        client.post(MyConstant.URL_UPDATE, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final UpdateData data = new UpdateData();
                if (data.dealData(responseString, gson, WelcomeActivity.this) == 1) {
                    final MyHandler handlerx = new MyHandler(WelcomeActivity.this, canShow);
                    new Thread() {
                        UpdateAll updateall = data.getObj();

                        public void run() {
                            UpdateManager.checkVersion(handlerx, pinfo, updateall);
                        }
                    }.start();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(WelcomeActivity.this, R.string.interneterror);
            }
        });
    }

    private void initJpush() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        spf = SharedPreferenceUtil.initSharedPerence().init(WelcomeActivity.this, MyConstant.APP_SPF_NAME);
        SharedPreferences.Editor editor = spf.edit();
        JPushInterface.init(MyApplication.getInstance());
        // 调用JPush API设置Tag
        MyHandler handlerx = new MyHandler(this, canShow);
        if (!TextUtils.isEmpty(MyConstant.user.getUserids())) {
            handlerx.sendMessage(handlerx.obtainMessage(MSG_SET_ALIAS, MyConstant.user.getUserids()));
        }
        // 调用JPush API设置Tag
        Set<String> tagSet = new LinkedHashSet<>();
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
        tagSet.add(JPushInterface.getRegistrationID(WelcomeActivity.this));
        handlerx.sendMessage(handlerx.obtainMessage(MSG_SET_TAGS, tagSet));
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(WelcomeActivity.this, R.layout.customer_notitfication_layout, R.id.icon,
                R.id.title, R.id.text);
        builder.layoutIconDrawable = R.mipmap.ic_launcher;
        builder.developerArg0 = "developerArg2";
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(2, builder);
        JPushInterface.resumePush(MyApplication.getInstance());
        if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(WelcomeActivity.this))) {
            SharedPreferenceUtil.initSharedPerence().putRegisterId(editor, JPushInterface.getRegistrationID(WelcomeActivity.this));
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
