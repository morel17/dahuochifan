package com.dahuochifan.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.UpdateAll;
import com.dahuochifan.model.userinfo.PersonInfoDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.plugin.main.PluginActivity;
import com.dahuochifan.requestdata.PersonInfoData;
import com.dahuochifan.requestdata.UpdateData;
import com.dahuochifan.ui.adapter.MainCycAdapter;
import com.dahuochifan.ui.fragment.CenterOrderFragment;
import com.dahuochifan.ui.fragment.CenterShikeFragment;
import com.dahuochifan.ui.fragment.DhNotificationFragment;
import com.dahuochifan.ui.fragment.MainAdFragment;
import com.dahuochifan.ui.fragment.MainFragment;
import com.dahuochifan.ui.views.ChangeColorIconWithTextView;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.ui.views.NoScrollViewPager;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.SystemBarTintManager;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.utils.UpdateManager;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener, OnClickListener, AMapLocationListener {
    public static NoScrollViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private String[] mTitles = new String[]{"First Fragment!", "Second Fragment!", "Third Fragment!", "Activity Fragment"};
    public static List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<>();
    private int userRole;
    private static Gson gson;
    private static RequestParams params;
    private static MyAsyncHttpClient client;

    private PackageInfo pinfo;
    public static PersonInfoDetail info;// 供fragment调用的实例
    private PercentRelativeLayout headview_rl;
    private DrawerLayout mDrawerLayout;
    private TextView contact_tv;
    private CircleImageView cirImg;
    private TextView cur_prov_tv;
    private TextView addrTv;
    private TextView user_tv;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private AlertDialog dialog;
    private int curPage;
    private int preState;
    private RelativeLayout manager_rl;
    private ProgressDialog pd;
    private ImageView anchor_iv;
    private ImageView anchor_iv_order;
    private Button testBtn;


    ChangeColorIconWithTextView one;
    ChangeColorIconWithTextView two;
    ChangeColorIconWithTextView three;
    ChangeColorIconWithTextView ad;
    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;

    static class MyHandler extends Handler {
        WeakReference<MainActivity> wrReferences;

        MyHandler(MainActivity wrReferences) {
            this.wrReferences = new WeakReference<>(wrReferences);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.msg_findnewversion:
                    MainActivity mainActivity = wrReferences.get();
                    UpdateManager mUpdateManager = new UpdateManager(mainActivity);
                    mUpdateManager.checkUpdateInfo();
                    break;
                case MyConstant.msg_notfindnewversion:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initWindow();
        setContentView(getLayoutView());
        init();
        initLocation();
        update();
        getUserInfo();
        if (TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getPoiName(spf))) {
            startLoc();
        } else {
            addrTv.setText(SharedPreferenceUtil.initSharedPerence().getPoiName(spf));
            initDatas();
        }

        contact_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + "4000960509");
                intent.setData(data);
                startActivity(intent);
            }
        });
        headview_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //占据焦点
            }
        });
    }

    /**
     * 初始化高德定位
     */

    private void initLocation() {
        AMapLocationClientOption mLocationOption;
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
    }

    private void startLoc() {
        pd.show();
        addrTv.setText("定位中...");
        mlocationClient.startLocation();
    }


    public void getUserInfo() {
        params = ParamData.getInstance().getPersonObj(MyConstant.user.getRole());
        client.post(MyConstant.URL_GETPERSONINFO, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                PersonInfoData persondata = new PersonInfoData();
                if (persondata.dealData2(responseString, MainActivity.this, gson) == 1) {
                    info = persondata.getPersonInfoDetail();
                    if (info != null) {
                        if (info.getMark() != null && !TextUtils.isEmpty(info.getMark().getNewsnum())) {
                            int newsMark = Tools.toInteger(info.getMark().getNewsnum());
                            if (newsMark > 0) {
                                anchor_iv.setVisibility(View.VISIBLE);
                            } else {
                                anchor_iv.setVisibility(View.GONE);
                            }
                        }
                        if (info.getMark() != null && !TextUtils.isEmpty(info.getMark().getDinerordernum())) {
                            int dinnerNum = Tools.toInteger(info.getMark().getDinerordernum());
                            if (info.getRole() != 2) {
                                if (dinnerNum > 0) {
                                    anchor_iv_order.setVisibility(View.VISIBLE);
                                } else {
                                    anchor_iv_order.setVisibility(View.GONE);
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(info.getManager())) {
                            manager_rl.setVisibility(View.VISIBLE);
                        } else {
                            manager_rl.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }


    private void init() {
        RelativeLayout center_rl;
        TextView home_prov_tv;
        ImageView search_iv;
        ImageView left_menu;
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("正在定位中，请稍等....");
        pd.setCancelable(false);
        List<String> list = new ArrayList<>();
        spf = SharedPreferenceUtil.initSharedPerence().init(MainActivity.this, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
        editor.apply();
        mTitles = new String[]{"First Fragment!", "Second Fragment!", "Third Fragment!", "Activity Fragment"};
        mTabIndicator = new ArrayList<>();
        MyApplication myApplication = (MyApplication) this.getApplication();
        myApplication.bindMyService();
        EventBus.getDefault().register(this);
        AppManager.getAppManager().addActivity(this);
        userRole = MyConstant.user.getRole();
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        testBtn = (Button) findViewById(R.id.testBtn);
        anchor_iv = (ImageView) findViewById(R.id.anchor_iv);
        anchor_iv_order = (ImageView) findViewById(R.id.anchor_iv_order);
        one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);
        two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);
        three = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_three);
        ad = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_activity);
        headview_rl = (PercentRelativeLayout) findViewById(R.id.headview_rl);
        center_rl = (RelativeLayout) findViewById(R.id.center_rl);
        user_tv = (TextView) findViewById(R.id.user_tv);
        left_menu = (ImageView) findViewById(R.id.left_menu);
        search_iv = (ImageView) findViewById(R.id.search_iv);
        home_prov_tv = (TextView) findViewById(R.id.home_prov_tv);
        cur_prov_tv = (TextView) findViewById(R.id.cur_prov_tv);
        addrTv = (TextView) findViewById(R.id.addrTv);
        manager_rl = (RelativeLayout) findViewById(R.id.manager_rl);
        String homeStr = "家乡" + MyConstant.user.getHomeprov();
        home_prov_tv.setText(homeStr);
        String curStr = "现居" + MyConstant.user.getCurprov();
        cur_prov_tv.setText(curStr);
        user_tv.setText(MyConstant.user.getNickname());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cirImg = (CircleImageView) findViewById(R.id.head_iv);
        mViewPager = (NoScrollViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setNoScroll(false);// 设置不可滑动
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        if (userRole != 2) {
            list.clear();
            list.add(getResources().getString(R.string.dh_pinfo));
            list.add("用餐地址");
            list.add(getResources().getString(R.string.dh_col));
            list.add("红包");
            list.add(getResources().getString(R.string.dh_set));
            CustomerHead.loadImage(MyConstant.user.getAvatar(), cirImg);
        } else {
            list.clear();
            list.add(getResources().getString(R.string.dh_pinfo));
            list.add(getResources().getString(R.string.dh_pay));
            list.add(getResources().getString(R.string.dh_foo));
            list.add(getResources().getString(R.string.dh_not));
            list.add(getResources().getString(R.string.dh_set));
            CookerHead.loadImage(MyConstant.user.getAvatar(), cirImg);
        }
        MainCycAdapter adapter = new MainCycAdapter(list, MainActivity.this);
        recyclerView.setAdapter(adapter);
        contact_tv = (TextView) findViewById(R.id.contact_tv);
        search_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyConstant.user.isLogin()) {
//                    startActivity(new Intent(MainActivityTemp.this, SearchActivity.class));
                    startActivity(new Intent(MainActivity.this, ChefSearchActivity.class));
                }
            }
        });
        left_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        cirImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!TextUtils.isEmpty(MyConstant.user.getAvatar())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, SimpleViewActivity.class);
                            int[] startLocation = new int[2];
                            v.getLocationOnScreen(startLocation);
                            startLocation[0] += v.getMeasuredWidth() / 2;
                            intent.putExtra("imgpath", MyConstant.user.getAvatar());
                            intent.putExtra("location", startLocation);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    }, 200);
                } else {
                    MainTools.ShowToast(MainActivity.this, "请到个人信息中去设置头像");
                }
            }
        });
        manager_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(info.getManager())) {
                    PluginActivity.getInstanceActivity(MainActivity.this);
                }
            }
        });
        center_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent itent = new Intent(MainActivity.this, DhLocationNewActivity.class);
                startActivity(itent);
            }
        });
        testBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
    }

    private void update() {
        PackageManager manager;
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
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final UpdateData data = new UpdateData();
                if (data.dealData(responseString, gson, MainActivity.this) == 1) {
                    final MyHandler myHandler = new MyHandler(MainActivity.this);
                    new Thread() {
                        UpdateAll updateall = data.getObj();

                        public void run() {
                            UpdateManager.checkVersion(myHandler, pinfo, updateall);
                        }
                    }.start();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(MainActivity.this, R.string.interneterror);
            }
        });
    }

    public void showTipDialog(String title) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(this).setCancelable(false).setTitle(title).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceUtil.initSharedPerence().putUserId(editor, "");
                SharedPreferenceUtil.initSharedPerence().putUserRole(editor, -99);
                SharedPreferenceUtil.initSharedPerence().putLogin(editor, false);
                SharedPreferenceUtil.initSharedPerence().putLoginPhone(editor, "");
                SharedPreferenceUtil.initSharedPerence().putHomeProv(editor, "");
                SharedPreferenceUtil.initSharedPerence().putCurProv(editor, "");
                SharedPreferenceUtil.initSharedPerence().putToken(editor, "");
                SharedPreferenceUtil.initSharedPerence().putChefIds(editor, "");
                SharedPreferenceUtil.initSharedPerence().putOtherProv(editor, "");
                SharedPreferenceUtil.initSharedPerence().putAvatar(editor, "");
                SharedPreferenceUtil.initSharedPerence().putNickname(editor, "");
                if (SharedPreferenceUtil.initSharedPerence().getWxLogin(spf)) {
                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                    if (wechat.isValid()) {
                        wechat.removeAccount();
                    }
                }
                editor.commit();
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }).create();
        dialog.show();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getMsg())) {
            switch (event.getMsg()) {
                case "nickname":
                    user_tv.setText(MyConstant.user.getNickname());
                    break;
                case "avatar":
                    CookerHead.loadImage(MyConstant.user.getAvatar(), cirImg);
                    break;
                case "DhLocation":
                    setTextView(event.getLocaiton());
                    break;
                case "AnchorShow":
                    anchor_iv.setVisibility(View.VISIBLE);
                    break;
                case "AnchorHide":
                    anchor_iv.setVisibility(View.GONE);
                    break;
                case MyConstant.ANCHORDERSHOW:
                    if (MyConstant.user.getRole() != 2) {
                        anchor_iv_order.setVisibility(View.VISIBLE);
                    }
                    break;
                case MyConstant.ANCHORDERHIDE:
                    if (MyConstant.user.getRole() != 2) {
                        anchor_iv_order.setVisibility(View.GONE);
                    }
                    break;
                case MyConstant.ANCHCHEFSHOW:
//                    if (MyConstant.user.getRole() == 2) {
//
//                    }
                    break;
                case MyConstant.ANCHCHEFHIDE:
//                    if (MyConstant.user.getRole() == 2) {
//
//                    }
                    break;
            }
        }

    }

    public void setTextView(String str) {
        if (addrTv != null) {
            addrTv.setText(str);
        }
    }

    private void initDatas() {

        for (String title : mTitles) {
            switch (title) {
                case "First Fragment!":
                    MainFragment fragment_left = new MainFragment();
                    mTabs.add(fragment_left);
                    break;
                case "Second Fragment!":
                    if (userRole != 2) {
                        CenterOrderFragment fragment_order = new CenterOrderFragment();
                        mTabs.add(fragment_order);
                    } else {
                        CenterShikeFragment fragment_shike = new CenterShikeFragment();
                        mTabs.add(fragment_shike);
                    }
                    break;
                case "Activity Fragment":
                    MainAdFragment fragment_ad = new MainAdFragment();
                    mTabs.add(fragment_ad);
                    break;
                case "Third Fragment!":
                    DhNotificationFragment fragment_msg = new DhNotificationFragment();
                    mTabs.add(fragment_msg);
                    break;
            }
        }
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }
        };
        mTabIndicator.clear();
        initTabIndicator();
//        mViewPager.removeAllViews();
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
        resetOtherTabs();
        mTabIndicator.get(0).setIconAlpha(1.0f);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initTabIndicator() {
        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);
        mTabIndicator.add(ad);


        one.setOnClickListener(this);
        two.setOnClickListener(this);
        ad.setOnClickListener(this);
        three.setOnClickListener(this);
        one.setIconAlpha(1.0f);
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        switch (v.getId()) {
            case R.id.id_indicator_one:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                anchor_iv_order.setVisibility(View.GONE);
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                anchor_iv.setVisibility(View.GONE);
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_activity:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
        if (preState == 1 && arg0 == 0 && curPage == 0) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        preState = arg0;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int arg2) {
        if (positionOffset > 0) {
            ChangeColorIconWithTextView left = mTabIndicator.get(position);
            ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
        curPage = position;
    }

    @Override
    public void onPageSelected(int arg0) {


    }

    private void resetOtherTabs() {

        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!Tools.isFastDoubleClick()) {
                MainTools.ShowToast(MainActivity.this, "再按一次退出");
            } else {
                moveTaskToBack(true);
                return true;
            }
        }
        return false;

    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (UpdateManager.dialog != null) {
            UpdateManager.dialog.dismiss();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        if (pd != null) {
            pd.dismiss();
        }
        if (mlocationClient != null) {
            stopLocation();
            mlocationClient.onDestroy();
        }
        super.onDestroy();
    }

    private int getLayoutView() {
        return R.layout.activity_main2;
    }


    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(ContextCompat.getColor(this, R.color.transparent));
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintColor(ContextCompat.getColor(MainActivity.this, R.color.black));
        }
    }

    @Override

    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (pd != null) {
                pd.dismiss();
            }
            if (location.getErrorCode() == 0) {
                double geoLat = location.getLatitude();
                double geoLng = location.getLongitude();
                if (!TextUtils.isEmpty(geoLng + "") && !TextUtils.isEmpty(geoLat + "")) {
                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, geoLng + "");
                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, geoLat + "");
                    String curP, curCity, curDistrict;
                    curP = location.getProvince().replace("省", "").replace("市", "");
                    curCity = location.getCity();
                    curDistrict = location.getDistrict();
                    SharedPreferenceUtil.initSharedPerence().putCurProv(editor, curP);
                    addrTv.setText(location.getPoiName());
                    String curStr1 = "现居" + curP;
                    cur_prov_tv.setText(curStr1);
                    if (!TextUtils.isEmpty(location.getPoiName())) {
                        SharedPreferenceUtil.initSharedPerence().putPoiName(editor, location.getPoiName());
                    }
                    if (!TextUtils.isEmpty(curP) && !curP.equals(MyConstant.user.getCurprov())) {
                        SharedPreferenceUtil.initSharedPerence().putGDProvince(editor, curP);
                        SharedPreferenceUtil.initSharedPerence().putGDCity(editor, curCity);
                        SharedPreferenceUtil.initSharedPerence().putGDDistrict(editor, curDistrict);
                    }
                    if (TextUtils.isEmpty(MyConstant.user.getCurprov())) {
                        if (curP.equals(curCity)) {
                            if (!TextUtils.isEmpty(curDistrict)) {
                                SharedPreferenceUtil.initSharedPerence().putCurCity(editor, curDistrict);
                                MyConstant.user.setCurcity(curDistrict);
                            }
                        } else {
                            if (!TextUtils.isEmpty(curCity)) {
                                SharedPreferenceUtil.initSharedPerence().putCurCity(editor, curCity);
                                MyConstant.user.setCurcity(curCity);
                            }
                        }
                        MyConstant.user.setCurprov(curP);
                    }
                    editor.commit();
                }
            } else {
                if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getPoiName(spf))) {
                    addrTv.setText(SharedPreferenceUtil.initSharedPerence().getPoiName(spf));
                } else {
                    addrTv.setText("重新定位");
                }
                MainTools.ShowToast(MainActivity.this, location.getErrorInfo());
            }
            stopLocation();// 停止定位
            initDatas();
        }
    }

    /**
     * 销毁定位
     */
    private void stopLocation() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
    }
}
