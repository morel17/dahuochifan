package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.message.MessageDetail;
import com.dahuochifan.model.message.MsgDetAll;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.message.DhMsgDet;
import com.dahuochifan.utils.LunboLoader;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.views.ActionSheet;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cz.msebera.android.httpclient.Header;

public class MessageDetailActivityHome extends BaseActivity implements ActionSheet.ActionSheetListener, PlatformActionListener {
    private TextView date_Tv, time_Tv, content_Tv;
    private SwipeRefreshLayout swipe;
    private int code;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private String msgId;
    private Gson gson;
    private String timeString;
    private String contentString;
    private String urlString;
    private SharedPreferences spf;
    private Editor editor;
    private WebView webView;
    // private TextView add_address;
    private SweetAlertDialog pd;
    private MessageDetail obj;
    private String imgurlString;
    private ImageView iv1, iv2, iv3;
    private FloatingActionButton floatingActionButton;
    private MenuItem doneMenuItem;
    private String titleString;
    private boolean canShare;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE6:
                    pd.dismiss();
                    finish();
                    break;
                case MyConstant.MYHANDLER_CODE4:
                    pd.dismiss();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        AppManager.getAppManager().addActivity(this);
        init();
        initData();
        initViews();
        btn_listener();
        if (code == 0) {
            L.e("dy2");
            if (msgId == null) {
                if (!TextUtils.isEmpty(contentString)) {
                    content_Tv.setText(Html.fromHtml(contentString));
                }
                date_Tv.setText("今天");
            } else {
                if (msgId.indexOf("http") != -1) {
                    webView.setVisibility(View.VISIBLE);
                    swipe.setVisibility(View.GONE);
                    webView.loadUrl(msgId);
                    canShare = true;
                } else {
                    content_Tv.setText(Html.fromHtml(contentString));
                    date_Tv.setText("今天");
                    getMessage();
                }
            }
        } else {
            date_Tv.setText("今天");
            content_Tv.setText(Html.fromHtml(contentString));
        }
    }

    private void init() {
        spf = SharedPreferenceUtil.initSharedPerence().init(MessageDetailActivityHome.this, MyConstant.APP_SPF_NAME);
        Tools.updateInfo(spf);
    }

    private void getMessage() {
        params = ParamData.getInstance().getMsgDetObj(msgId);
        client.post(MyConstant.URL_MESSAGEDETAIL, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (BuildConfig.LEO_DEBUG) L.e("xxx1" + arg2);
                DhMsgDet data = new DhMsgDet();
                if (data.dealData(arg2, MessageDetailActivityHome.this, gson) == 1) {
                    MsgDetAll msgAll = data.getAll();
                    if (msgAll != null && msgAll.getObj() != null) {
                        String tiemStr = msgAll.getObj().getCreatetime();
                        String contentStr = msgAll.getObj().getContent();
                        String urlStr = msgAll.getObj().getUrl();
                        titleString = msgAll.getObj().getTitle();
                        String imgurlString;


                        if (msgAll.getObj().getImgurl() != null) {
                            imgurlString = msgAll.getObj().getImgurl();
                        } else {
                            imgurlString = "";
                        }
                        if (TextUtils.isEmpty(urlStr)) {
                            webView.setVisibility(View.GONE);
                            swipe.setVisibility(View.VISIBLE);
                            time_Tv.setText(MainTools.dayForTime(tiemStr));
                            if (MainTools.currentdaydistance(tiemStr) == 0) {
                                date_Tv.setText("今天");
                            } else if (MainTools.currentdaydistance(tiemStr) == 1) {
                                date_Tv.setText("昨天");
                            } else {
                                date_Tv.setText(MainTools.dayForDay(tiemStr));
                            }
                            content_Tv.setText(Html.fromHtml(contentStr));
                            if (!TextUtils.isEmpty(imgurlString)) {
                                String[] provinceStr = imgurlString.split(",");
                                if (provinceStr != null && provinceStr.length > 0) {
                                    if (provinceStr.length == 1) {
                                        iv1.setVisibility(View.VISIBLE);
                                        iv2.setVisibility(View.GONE);
                                        iv3.setVisibility(View.GONE);
                                        LunboLoader.loadImage(provinceStr[0] + "?imageView2/1/w/" + getResources().getDimensionPixelOffset(R.dimen.width_70_80)
                                                + "/h/" + getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, iv1);
                                    } else if (provinceStr.length == 2) {
                                        iv1.setVisibility(View.VISIBLE);
                                        iv2.setVisibility(View.VISIBLE);
                                        iv3.setVisibility(View.GONE);
                                        LunboLoader.loadImage(provinceStr[0] + "?imageView2/1/w/" + getResources().getDimensionPixelOffset(R.dimen.width_70_80)
                                                + "/h/" + getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, iv1);
                                        LunboLoader.loadImage(provinceStr[1] + "?imageView2/1/w/" + getResources().getDimensionPixelOffset(R.dimen.width_70_80)
                                                + "/h/" + getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, iv2);
                                    } else {
                                        iv1.setVisibility(View.VISIBLE);
                                        iv2.setVisibility(View.VISIBLE);
                                        iv3.setVisibility(View.VISIBLE);
                                        LunboLoader.loadImage(provinceStr[0] + "?imageView2/1/w/" + getResources().getDimensionPixelOffset(R.dimen.width_70_80)
                                                + "/h/" + getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, iv1);
                                        LunboLoader.loadImage(provinceStr[1] + "?imageView2/1/w/" + getResources().getDimensionPixelOffset(R.dimen.width_70_80)
                                                + "/h/" + getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, iv2);
                                        LunboLoader.loadImage(provinceStr[2] + "?imageView2/1/w/" + getResources().getDimensionPixelOffset(R.dimen.width_70_80)
                                                + "/h/" + getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, iv3);
                                    }
                                }
                            }
                        } else {
                            webView.setVisibility(View.VISIBLE);
                            swipe.setVisibility(View.GONE);
                            webView.loadUrl(urlStr);
                        }

                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(MessageDetailActivityHome.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                swipe.setRefreshing(false);
                super.onFinish();
            }
        });
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getInt("code");
            if (code == 0) {
                try {
                    floatingActionButton.setVisibility(View.VISIBLE);
                    JSONObject jobj = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    contentString = bundle.getString(JPushInterface.EXTRA_ALERT);
                    msgId = jobj.getString("extra");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                floatingActionButton.setVisibility(View.VISIBLE);
                contentString = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String josnStr = bundle.getString(JPushInterface.EXTRA_EXTRA);
                try {
                    JSONObject jsonObject = new JSONObject(josnStr);
                    String obj = jsonObject.getString("data");
                    JSONObject jsonObject1 = new JSONObject(obj);
                    msgId = jsonObject1.getString("ncids");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void initViews() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        date_Tv = (TextView) findViewById(R.id.date_tv);
        time_Tv = (TextView) findViewById(R.id.time_tv);
        content_Tv = (TextView) findViewById(R.id.content_tvme);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        webView = (WebView) findViewById(R.id.webview);
        iv1 = (ImageView) findViewById(R.id.iv_one);
        iv2 = (ImageView) findViewById(R.id.iv_two);
        iv3 = (ImageView) findViewById(R.id.iv_three);
        iv1.setScaleType(ImageView.ScaleType.FIT_XY);
        iv2.setScaleType(ImageView.ScaleType.FIT_XY);
        iv3.setScaleType(ImageView.ScaleType.FIT_XY);

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private void btn_listener() {
        floatingActionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageDetailActivityHome.this, WelcomeActivity.class));
                finish();
            }
        });
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipe.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessage();
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_messagedetail;
    }

    @Override
    protected String initToolbarTitle() {
        return "消息";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                if (canShare) {
                    share();
                } else {
                    MainTools.ShowToast(MessageDetailActivityHome.this, "只能分享系统消息");
                }
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dh_message_detail2_others, menu);
        return true;
    }

    private void share() {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet.createBuilder(MessageDetailActivityHome.this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("分享到朋友圈", "分享给好友").setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        Platform plat;
        switch (index) {
            case 0:
                plat = ShareSDK.getPlatform(MessageDetailActivityHome.this, WechatMoments.NAME);
                plat.setPlatformActionListener(this);
                WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
                // 任何分享类型都需要title和text
                // the two params of title and text are required in every share type
                sp.title = titleString;
                sp.text = contentString;
                sharefunction_1(plat, sp);
                break;
            case 1:
                plat = ShareSDK.getPlatform(MessageDetailActivityHome.this, Wechat.NAME);
                plat.setPlatformActionListener(this);
                Wechat.ShareParams sp2 = new Wechat.ShareParams();
                sp2.title = titleString;
                sp2.text = contentString;
                sharefunction_2(plat, sp2);
                break;
            default:
                break;
        }
    }

    private void sharefunction_1(Platform platx, WechatMoments.ShareParams spx) {
        String[] provinceStr = null;
        if (!TextUtils.isEmpty(imgurlString)) {
            provinceStr = imgurlString.split(",");
        }
        if (TextUtils.isEmpty(urlString)) {
            spx.shareType = Platform.SHARE_IMAGE;
            if (provinceStr != null && !TextUtils.isEmpty(provinceStr[0])) {
                spx.imageUrl = provinceStr[0] + "?imageView2/1/w/" + 200
                        + "/h/" + 200 + "/q/" + MyConstant.QUALITY;
            } else {
                spx.imageUrl = "http://www.dahuochifan.com/web/img/barcode.png";
            }
        } else {
            spx.title = contentString;
            spx.shareType = Platform.SHARE_WEBPAGE;
            spx.url = urlString;
            if (provinceStr != null && !TextUtils.isEmpty(provinceStr[0])) {
                spx.imageUrl = provinceStr[0] + "?imageView2/1/w/" + 200
                        + "/h/" + 200 + "/q/" + MyConstant.QUALITY;
            } else {
                spx.imageUrl = "http://www.dahuochifan.com/web/img/barcode.png";
            }
        }
        platx.share(spx);
        MainTools.ShowToast(MessageDetailActivityHome.this, "正在分享，请稍等...");
    }

    private void sharefunction_2(Platform platx, Wechat.ShareParams spx) {
        String[] provinceStr = null;
        if (!TextUtils.isEmpty(imgurlString)) {
            provinceStr = imgurlString.split(",");
        }
        if (TextUtils.isEmpty(urlString)) {
            spx.shareType = Platform.SHARE_IMAGE;
            if (provinceStr != null && !TextUtils.isEmpty(provinceStr[0])) {
                spx.imageUrl = provinceStr[0] + "?imageView2/1/w/" + 200
                        + "/h/" + 200 + "/q/" + MyConstant.QUALITY;
            } else {
                spx.imageUrl = "http://www.dahuochifan.com/web/img/barcode.png";
            }
        } else {
            spx.shareType = Platform.SHARE_WEBPAGE;
            spx.url = urlString;
            if (provinceStr != null && !TextUtils.isEmpty(provinceStr[0])) {
                spx.imageUrl = provinceStr[0] + "?imageView2/1/w/" + 200
                        + "/h/" + 200 + "/q/" + MyConstant.QUALITY;
            } else {
                spx.imageUrl = "http://www.dahuochifan.com/web/img/barcode.png";
            }
        }
        platx.share(spx);
        MainTools.ShowToast(MessageDetailActivityHome.this, "正在分享，请稍等...");
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        MainTools.ShowToast(MessageDetailActivityHome.this, "分享取消");
    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        MainTools.ShowToast(MessageDetailActivityHome.this, "分享成功");
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        MainTools.ShowToast(MessageDetailActivityHome.this, "分享失败");
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }
}
