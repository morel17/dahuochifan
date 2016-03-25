package com.dahuochifan.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.ui.views.ActionSheet;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class DhActivitiesDetailActivity extends BaseActivity implements ActionSheet.ActionSheetListener, PlatformActionListener {
    private String detailStr;
    private String headStr;
    private String remarkStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        initViews();
        ShareSDK.initSDK(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        detailStr = getIntent().getStringExtra("detail");
        headStr = getIntent().getStringExtra("url");
        remarkStr = getIntent().getStringExtra("remark");
        WebView adweb = (WebView) findViewById(R.id.adweb);
        WebSettings webSettings = adweb.getSettings();
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        if (detailStr.contains("?")) {
            detailStr = detailStr + "&userids=" + MyConstant.user.getUserids();
        } else {
            detailStr = detailStr + "?" + MyConstant.user.getUserids();
        }
        adweb.loadUrl(detailStr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dh_message_detail2, menu);
        return true;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_activitiesdetail;
    }

    @Override
    protected String initToolbarTitle() {
        return "活动详情";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                share();
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void share() {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet.createBuilder(DhActivitiesDetailActivity.this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("分享到朋友圈", "分享给好友").setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        Platform plat;
        switch (index) {
            case 0:
                plat = ShareSDK.getPlatform(DhActivitiesDetailActivity.this, WechatMoments.NAME);
                plat.setPlatformActionListener(this);
                WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
                // 任何分享类型都需要title和text
                // the two params of title and text are required in every share type
                sp.text = "活动分享";
                if (!TextUtils.isEmpty(remarkStr)) {
                    sp.title = remarkStr;
                } else {
                    sp.title = "活动分享";
                }
                sharefunction_1(plat, sp);
                break;
            case 1:
                plat = ShareSDK.getPlatform(DhActivitiesDetailActivity.this, Wechat.NAME);
                plat.setPlatformActionListener(this);
                Wechat.ShareParams sp2 = new Wechat.ShareParams();
                sp2.title = "活动分享";
                if (!TextUtils.isEmpty(remarkStr)) {
                    sp2.text = remarkStr;
                } else {
                    sp2.text = "活动分享内容";
                }
                sharefunction_2(plat, sp2);
                break;
            default:
                break;
        }
    }

    private void sharefunction_1(Platform platx, WechatMoments.ShareParams spx) {
        spx.shareType = Platform.SHARE_WEBPAGE;
        spx.imageUrl = headStr + "?imageView2/1/w/" + 200
                + "/h/" + 200 + "/q/" + MyConstant.QUALITY;
        spx.url = detailStr;

        platx.share(spx);
        MainTools.ShowToast(DhActivitiesDetailActivity.this, "正在分享，请稍等...");
    }

    private void sharefunction_2(Platform platx, Wechat.ShareParams spx) {

        spx.shareType = Platform.SHARE_WEBPAGE;
        spx.imageUrl = headStr + "?imageView2/1/w/" + 200
                + "/h/" + 200 + "/q/" + MyConstant.QUALITY;
        spx.url = detailStr;
        platx.share(spx);
        MainTools.ShowToast(DhActivitiesDetailActivity.this, "正在分享，请稍等...");
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        MainTools.ShowToast(DhActivitiesDetailActivity.this, "分享完成");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        MainTools.ShowToast(DhActivitiesDetailActivity.this, "分享失败");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        MainTools.ShowToast(DhActivitiesDetailActivity.this, "分享取消");
    }
}
