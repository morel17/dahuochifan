package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dahuochifan.R;

public class TimeLineActivity extends BaseActivity {
    private WebView webView;
    private String trackUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        webView=(WebView)findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.loadUrl(trackUrl);
        webView.loadUrl("http://www.baidu.com/");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    private void initData() {
        Intent intent=getIntent();
        trackUrl=intent.getStringExtra("trackurl");

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_time_line;
    }

    @Override
    protected String initToolbarTitle() {
        return "订单跟踪";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                if(webView.canGoBack()){
                    webView.goBack(); // goBack()表示返回WebView的上一页面
                }else{
                    finish();
                }
            default :
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }else{
            finish();
        }
        return super.onKeyDown(keyCode,event);
    }
}
