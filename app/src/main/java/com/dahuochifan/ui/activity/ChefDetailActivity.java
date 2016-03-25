package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.chefinfo.ChefInfo;
import com.dahuochifan.model.cheflist.ChefActs;
import com.dahuochifan.model.comment.CommentsInfo;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.ChefTopDetailData;
import com.dahuochifan.requestdata.CommentsData;
import com.dahuochifan.ui.adapter.ChefDetailCommentAdapter;
import com.dahuochifan.ui.adapter.MainChefListActAdapter;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.ui.views.popwindow.ConfirmPopWindow;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NetworkImageHolderView;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.utils.TopBgLoader;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cz.msebera.android.httpclient.Header;

//import com.bigkoo.convenientbanner.CBViewHolderCreator;
//import com.bigkoo.convenientbanner.OnItemClickListener;

public class ChefDetailActivity extends AppCompatActivity implements OnItemClickListener {
    private ChefInfo chef;
    @Bind(R.id.chef_tv)
    TextView nickname_tv;
    @Bind(R.id.address_tv)
    TextView location_tv;
    @Bind(R.id.chef_head)
    CircleImageView circle_iv;
    @Bind(R.id.chef_head_rl)
    RelativeLayout chef_head_rl;
    @Bind(R.id.like_iv)
    ImageView like_iv;
    @Bind(R.id.like_tv)
    TextView like_tv;
    @Bind(R.id.collect_iv)
    ImageView collect_iv;
    @Bind(R.id.collect_tv)
    TextView collect_tv;
    @Bind(R.id.eat_num_tv)
    TextView eat_tv;
    @Bind(R.id.type_distance_tv)
    TextView type_distance_tv;//用于显示点餐范围的tv


    @Bind(R.id.swipe)
    SwipeRefreshLayout swipe;
    @Bind(R.id.today_relay_tv)
    TextView today_relay_tv;
    @Bind(R.id.tomorrow_relay_tv)
    TextView tomorrow_relay_tv;
    @Bind(R.id.content_tvme)
    TextView content_tv;
    @Bind(R.id.tip_tv)
    TextView tip_tv;

    @Bind(R.id.backdrop)
    ImageView backdrop;
    @Bind(R.id.comments_tv)
    TextView comments_tv;
    @Bind(R.id.today_order_tv)
    TextView today_order_tv;
    @Bind(R.id.tomorrow_order_tv)
    TextView tomorrow_order_tv;
    @Bind(R.id.acor_view)
    View acor_view;
    @Bind(R.id.dh_tags_tv)
    TextView dh_tags_tv;
    @Bind(R.id.good_dish_tv2)
    TextView good_dish_tv2;
    @Bind(R.id.more_rl)
    RelativeLayout more_rl;

    @Bind(R.id.repost_tv)
    TextView repost_tv;
    @Bind(R.id.trans_discount_tv)
    TextView trans_discount_tv;
    @Bind(R.id.head_float)
    FloatingActionButton head_float;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @Bind(R.id.trans_fee)
    TextView trans_fee;
    //    @Bind(R.id.loading_rl)
//    RelativeLayout loading_rl;
//    @Bind(R.id.refresh_iv)
//    ImageView refresh_iv;
//    @Bind(R.id.loading_iv)
//    AVLoadingIndicatorView loading_iv;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.chef_home)
    TextView chef_home;
    @Bind(R.id.comment_cyc)
    RecyclerView comment_cyc;
    @Bind(R.id.activity_cyc)
    RecyclerView activity_cyc;

    /**
     * 关于重新加载
     */
    @Bind(R.id.preview_rl)
    PercentRelativeLayout preview_rl;
    @Bind(R.id.back_bg)
    ImageView back_bg;
    @Bind(R.id.detail_tvs)
    TextView detail_tv;
    @Bind(R.id.refresh_tv)
    TextView refresh_tv;
    private MorelAlertDialog pDialog;


    private boolean canlike, cancollect;
    private MyAsyncHttpClient client;
    private RequestParams param;
    private Context context;
    private Gson gson;

    private ArrayList<String> picList = new ArrayList<>();

    //    private TextView type_1, type_2, type_3;
    private String tagStr;
    public static ChefDetailActivity instance;
    private SharedPreferences spf;
    private String ids;
    ConfirmPopWindow pop;


    private SharedPreferences.Editor editor;
    private AlertDialog dialog;
    private ChefDetailCommentAdapter commentAdapter;
    private MainChefListActAdapter act_adapter;

    private static final android.view.animation.Interpolator INTERPOLATOR =
            new FastOutSlowInInterpolator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTopViews();
        setContentView(R.layout.activity_cookdetail);
        ButterKnife.bind(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsing_toolbar.setTitle("主厨详情");
        collapsing_toolbar.setExpandedTitleColor(ContextCompat.getColor(ChefDetailActivity.this, R.color.transparent));//展开后文字颜色
        collapsing_toolbar.setCollapsedTitleTextColor(ContextCompat.getColor(ChefDetailActivity.this, R.color.white));//收缩后文字颜色
        preview_rl.setVisibility(View.VISIBLE);
        preview_rl.setBackgroundResource(R.color.transparent);
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismissWithAnimation();
            pDialog = null;
        }
        pDialog = new MorelAlertDialog(this, MorelAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("正在加载中");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.white));
        pDialog.getProgressHelper().setBarWidth(3);
        initData();
        getChefInfo(false);
        btnListener();
        swipe.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChefInfo(true);
            }
        });
    }

    private void initTopViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void btnListener() {
        head_float.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom == oldBottom) {
                    //消失
                    if (!head_float.isShown()) {
                        animateOut(chef_head_rl);
                    }
                }
                if (bottom < oldBottom) {
                    //显示
                    if (head_float.isShown()) {
                        animateIn(chef_head_rl);
                    }
                }
            }
        });
        today_order_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                todayOrder();
            }
        });
        tomorrow_order_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                tomorrowOrder();
            }
        });
        refresh_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                getChefInfo(false);
            }
        });
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    swipe.setEnabled(true);
                } else {
                    swipe.setEnabled(false);
                }
            }
        });
    }

    private void getChefInfo(final boolean isSwipe) {
        if (chef != null) {
            param = ParamData.getInstance().getChefTopObj(ChefDetailActivity.this, 1, SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf),
                    SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf), chef.getChefids());
        } else {
            param = ParamData.getInstance().getChefTopObj(ChefDetailActivity.this, 1, SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf),
                    SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf), ids);
        }
        client.post(MyConstant.URL_GETCHEF, param, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!isSwipe) {
                    if (pDialog != null) {
                        pDialog.show();
                    }
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String responseString) {
                ChefTopDetailData cookdata = new ChefTopDetailData();
                if (cookdata.dealData(responseString, ChefDetailActivity.this, gson) == 1) {
                    if (cookdata.getAll() != null) {
                        chef = cookdata.getAll();
                        if (chef != null && preview_rl != null) {
                            preview_rl.setVisibility(View.GONE);
                            initViews();
                            if (!TextUtils.isEmpty(chef.getStory())) {
                                content_tv.setText(chef.getStory().replace("\n", "").replace("\r", ""));
                                content_tv.setText(Html.fromHtml("<b>主厨背后的故事：</b>" + chef.getStory().replace("\n", "").replace("\r", "")));
                            }
                            if (chef.getDeliveryfee() != 0) {
                                if (chef.getDeliverydistance() > 0) {
                                    String feeStr = "￥" + chef.getDeliveryfee() + "(" + Tools.getDistanc(chef.getDeliverydistance()) + ")";
                                    trans_fee.setText(feeStr);
                                } else {
                                    String feeStr = "￥" + chef.getDeliveryfee();
                                    trans_fee.setText(feeStr);
                                }
                                trans_fee.setTextColor(ContextCompat.getColor(ChefDetailActivity.this, R.color.text_dark));
                            } else {
                                trans_fee.setText("免费配送");
                                trans_fee.setTextColor(ContextCompat.getColor(ChefDetailActivity.this, R.color.maincolor_new));
                            }
                            if (chef.getGooddish() != null) {
                                String[] gooddishStr = chef.getGooddish().split(",");
                                StringBuilder sb = new StringBuilder();
                                for (String goodishstr : gooddishStr) {
                                    String dishStr = goodishstr + " ";
                                    sb.append(dishStr);
                                }
                                good_dish_tv2.setText(sb.toString());
                            }
                            if (chef.getActs() != null) {
                                List<ChefActs> acts = chef.getActs();
                                if (acts.size() > 0) {
                                    activity_cyc.setVisibility(View.VISIBLE);
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_80_80), acts.size()
                                            * context.getResources().getDimensionPixelOffset(R.dimen.dh_16) + acts.size() * context.getResources().getDimensionPixelOffset(R.dimen.dh_8));
                                    activity_cyc.setLayoutParams(params);
                                    act_adapter.setActs(acts);
                                    act_adapter.notifyDataSetChanged();
                                } else {
                                    activity_cyc.setVisibility(View.GONE);
                                }
                            } else {
                                activity_cyc.setVisibility(View.GONE);
                            }
                            String today_num = chef.getToday_num() + "份";
                            String tommorrow_num = chef.getTomorrow_num() + "份";
                            today_relay_tv.setText(today_num);
                            tomorrow_relay_tv.setText(tommorrow_num);
                            LatLng startLat = new LatLng(chef.getLatitude(), chef.getLongitude());
                            LatLng endLat = new LatLng(Tools.toFloat(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf)), Tools.toFloat(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf)));
                            float addressDistance = AMapUtils.calculateLineDistance(startLat, endLat);
                            if (chef.getLimitdistance() > 0) {
                                type_distance_tv.setText(Tools.getDistanc2(chef.getLimitdistance()));
                            } else {
                                type_distance_tv.setText("-");
                            }
                            if (chef.getCookbookpic() != null && chef.getCookbookpic().size() > 0) {
                                if (!chef.isopen()) {
                                    tip_tv.setText("主厨正在休息中");
                                } else {


                                    if (chef.getLimitdistance() != 0 && (addressDistance <= chef.getLimitdistance())) {
                                        tip_tv.setVisibility(View.GONE);
                                    } else {
                                        String tipsStr = "您超出点餐范围" + Tools.getDistanc((int) (addressDistance - chef.getLimitdistance())) + "不能点餐";
                                        tip_tv.setText(tipsStr);
                                    }
                                }
                                if (chef.getCommentnum() > 0) {
                                    String commentsStr = "评论  " + chef.getCommentnum();
                                    comments_tv.setText(commentsStr);
                                    getCommentFirstPage();
                                } else {
                                    comments_tv.setText("暂无评论");
                                    more_rl.setVisibility(View.GONE);
                                }
                            } else {
                                tip_tv.setText("暂无菜单");
                            }
                            initCBViewPager(chef);
                        } else {
                            if (!isSwipe && preview_rl != null) {
                                preview_rl.setBackgroundResource(R.color.white);
                                preview_rl.setVisibility(View.VISIBLE);
                                detail_tv.setVisibility(View.VISIBLE);
                                back_bg.setVisibility(View.VISIBLE);
                                refresh_tv.setVisibility(View.VISIBLE);
                                detail_tv.setText("网络不给力");
                                back_bg.setBackgroundResource(R.mipmap.internet_failed);
                            }
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(cookdata.getTag())) {
                        showTipDialog(ChefDetailActivity.this, cookdata.getTag(), cookdata.getResultCode());
                    } else {
                        showTipDialog(ChefDetailActivity.this, "重新登录", cookdata.getResultCode());
                    }

                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(context, R.string.interneterror);
                if (!isSwipe && (preview_rl != null)) {
                    preview_rl.setBackgroundResource(R.color.white);
                    preview_rl.setVisibility(View.VISIBLE);
                    detail_tv.setVisibility(View.VISIBLE);
                    back_bg.setVisibility(View.VISIBLE);
                    refresh_tv.setVisibility(View.VISIBLE);
                    detail_tv.setText("网络不给力");
                    back_bg.setBackgroundResource(R.mipmap.internet_failed);
                }
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFinish() {
//                swipe.setRefreshing(false);
                if (pDialog != null)
                    pDialog.dismiss();
                if (isSwipe && swipe != null) {
                    swipe.setRefreshing(false);
                }
                super.onFinish();
            }
        });
    }

    private void initCBViewPager(ChefInfo chefx) {
        if (chefx.getCookbookpic() != null && chefx.getCookbookpic().size() > 0) {
            picList.clear();
            for (int i = 0; i < chefx.getCookbookpic().size(); i++) {
                if (chefx.getCookbookpic().get(i).getStatus().equals("1")) {
                    picList.add(chefx.getCookbookpic().get(i).getUrl());
                }
            }
            convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, picList)
                    .setOnItemClickListener(this)
                    //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                    .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
//                    .startTurning(16888);
            Class cls;
            try {
                cls = Class.forName("com.ToxicBakery.viewpager.transforms." + DepthPageTransformer.class.getSimpleName());
                ABaseTransformer transforemer = (ABaseTransformer) cls.newInstance();
                convenientBanner.getViewPager().setPageTransformer(true, transforemer);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() {
        AppManager.getAppManager().addActivity(this);
        instance = this;
        context = this;
//        type_1 = (TextView) findViewById(R.id.type_tv_one);
//        type_2 = (TextView) findViewById(R.id.type_tv_two);
//        type_3 = (TextView) findViewById(R.id.type_tv_three);
        trans_discount_tv = (TextView) findViewById(R.id.trans_discount_tv);
        client = new MyAsyncHttpClient();
        client.setTimeout(9);
        param = new RequestParams();
        gson = new Gson();
        spf = SharedPreferenceUtil.initSharedPerence().init(this, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
        editor.apply();
        pop = new ConfirmPopWindow(this);
        commentAdapter = new ChefDetailCommentAdapter(this, comment_cyc);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        comment_cyc.setLayoutManager(manager);
        comment_cyc.setItemAnimator(new DefaultItemAnimator());
        comment_cyc.setAdapter(commentAdapter);
        Intent intent = getIntent();
        if (intent.getExtras().getString("tag") != null) {
            tagStr = intent.getExtras().getString("tag");
        } else {
            tagStr = "";
        }
        ids = intent.getExtras().getString("myids");
        canlike = true;
        cancollect = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        act_adapter = new MainChefListActAdapter(inflater, this);
        LinearLayoutManager managerL = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        activity_cyc.setLayoutManager(managerL);
        activity_cyc.setItemAnimator(new DefaultItemAnimator());
        activity_cyc.setAdapter(act_adapter);
    }

    private void initViews() {
        if (chef.getBgs() != null) {
            if (!TextUtils.isEmpty(chef.getBgs().get(0).getUrl())) {
                TopBgLoader.loadImage(chef.getBgs().get(0).getUrl() + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
                        + context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, backdrop);

            }
        }
        if (chef.getDistance() != 0) {
            if (TextUtils.isEmpty(tagStr)) {
                location_tv.setText(chef.getAddressinfo());
            } else {
                String locStr = chef.getAddressinfo();
                location_tv.setText(locStr);
                if (tagStr.equals("全部")) {
                    tagStr = "";
                }
            }
        } else {
            location_tv.setText(chef.getAddressinfo());
        }
        if (chef.getTags() != null) {
            String[] chefTagStr = chef.getTags().split(",");
            StringBuilder sbTag = new StringBuilder();
            for (String cheftagstr : chefTagStr) {
                String appendStr = cheftagstr + " ";
                sbTag.append(appendStr);
            }
            dh_tags_tv.setText(sbTag.toString());
        }
        if (chef.getGooddish() != null) {
            String[] gooddishStr = chef.getGooddish().split(",");
            StringBuilder sb = new StringBuilder();
            for (String goodishstr : gooddishStr) {
                String appendStr = goodishstr + " ";
                sb.append(appendStr);
            }
            good_dish_tv2.setText(sb.toString());
        }
        nickname_tv.setText(chef.getNickname());
        if (!TextUtils.isEmpty(chef.getHomeprov())) {
            if (!TextUtils.isEmpty(chef.getHomecity())) {
                chef_home.setText(chef.getHomeprov().replace("省", "").replace("市", "") + chef.getHomecity() + "人");
            } else {
                chef_home.setText(chef.getHomeprov().replace("省", "").replace("市", "") + "人");
            }
        }
        CookerHead.loadImage(chef.getAvatar(), circle_iv);
        head_float.setVisibility(View.GONE);
        like_tv.setText(chef.getLikenum() + "");
        like_iv.setSelected(chef.islike());
        collect_iv.setSelected(chef.iscollect());
        eat_tv.setText(chef.getEatennum() + "");
        collect_tv.setText(chef.getCollectnum() + "");
        /**
         * 关于配送方式的显示暂时先注释掉
         */
//        if (chef.getEatenway() != null) {
//            String[] cookeatenway;
//            cookeatenway = chef.getEatenway().split(",");
//            for (int i = 0; i < cookeatenway.length; i++) {
//                String eatwayStr = "";
//                if (cookeatenway[i].equals("配送")) {
//                    eatwayStr = "配送";
//                }
//                if (cookeatenway[i].equals("自提")) {
//                    eatwayStr = "自提";
//                }
//                if (cookeatenway[i].equals("堂吃")) {
//                    eatwayStr = "堂吃";
//                }
//                if (i == 0) {
//                    type_1.setVisibility(View.VISIBLE);
//                    type_1.setText(eatwayStr);
//                } else if (i == 1) {
//                    type_2.setVisibility(View.VISIBLE);
//                    type_2.setText(eatwayStr);
//                } else if (i == 2) {
//                    type_3.setVisibility(View.VISIBLE);
//                    type_3.setText(eatwayStr);
//                }
//                if (chef.getLimitdistance() != 0) {
//                    String distanceStr = Tools.getDistanc2(chef.getLimitdistance()) + "以内";
//                    trans_discount_tv.setText(distanceStr);
//                }
//            }
//        }
    }

    private void getCommentFirstPage() {
        param = ParamData.getInstance().getCommentObj(chef.getCbids(), 1, 5, chef.getChefids());
        client.post(MyConstant.URL_GETCOMMENTSLIST, param, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                repost_tv.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG) L.e(responseString);
                CommentsData data = new CommentsData();
                if (data.dealData(responseString, ChefDetailActivity.this, gson) == 1) {
                    if (data.getAll().getPage().getList() != null && data.getAll().getPage().getList().size() > 0) {
                        List<CommentsInfo> commentsInfos = data.getAll().getPage().getList();
                        if (commentsInfos.size() > 0) {
                            more_rl.setVisibility(View.VISIBLE);
                        } else {
                            more_rl.setVisibility(View.GONE);
                        }
                        if (commentsInfos.size() > 5) {
                            initCyc(5, commentsInfos);
                        } else {
                            initCyc(commentsInfos.size(), commentsInfos);
                        }
                    }
                }
            }

            private void initCyc(int size, List<CommentsInfo> commentsInfosx) {
                List<CommentsInfo> listTemp = new ArrayList<>();
                AtomicInteger count = new AtomicInteger();
                for (int i = 0; i < size; i++) {
                    listTemp.add(commentsInfosx.get(i));
                    if (commentsInfosx.get(i).getPic() != null && commentsInfosx.get(i).getPic().size() > 0) {
                        count.incrementAndGet();
                    }
                }

                int height = context.getResources().getDimensionPixelOffset(R.dimen.dh_10) + count.get() * context.getResources().getDimensionPixelOffset(R.dimen.height_16_80) + (size - count.get()) * context.getResources().getDimensionPixelOffset(R.dimen.height_8_80);
                commentAdapter.setCommentsInfos(listTemp);
                commentAdapter.notifyDataSetChanged();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_80_80),
                        height);
                comment_cyc.setLayoutParams(params);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(ChefDetailActivity.this, R.string.interneterror);
                repost_tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @OnClick(R.id.repost_tv)
    public void repost_TV() {
        getCommentFirstPage();
    }

    @OnClick(R.id.detail_tv)
    public void detail_TV() {
        pop.showPopupWindow(acor_view);
    }

    @OnClick(R.id.like_rl)
    public void LikeFunction() {
        if (canlike && chef != null) {
            int likenum = chef.getLikenum();
            boolean islike = chef.islike();
            doLike(likenum, islike);
        }
    }


    @OnClick(R.id.more_rl)
    public void getComment() {
        if (chef != null) {
            Intent intent = new Intent(context, CommentsDetailActivity.class);
            intent.putExtra("nickname", chef.getNickname());
            if (!TextUtils.isEmpty(chef.getHomecity())) {
                intent.putExtra(
                        "naviland",
                        "非常" + chef.getHomeprov().replace("省", "").replace("市", "") + "/"
                                + chef.getHomecity().replace("新区", "").replace("市", "").replace("区", ""));
            } else {
                intent.putExtra("naviland", "非常" + chef.getHomeprov().replace("省", "").replace("市", ""));
            }
            intent.putExtra("avatar", chef.getAvatar());
            intent.putExtra("score", chef.getTotalscore());
            intent.putExtra("commentnum", chef.getCommentnum());
            intent.putExtra("cbids", chef.getCbids());
            intent.putExtra("chefids",chef.getChefids());
            context.startActivity(intent);
        }
    }

    @OnClick(R.id.chef_head)
    public void getChefDetail() {
        if (chef != null && !TextUtils.isEmpty(chef.getAvatar())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ChefDetailActivity.this, SimpleViewActivity.class);
                    int[] startLocation = new int[2];
                    circle_iv.getLocationOnScreen(startLocation);
                    startLocation[0] += circle_iv.getMeasuredWidth() / 2;
                    intent.putExtra("imgpath", chef.getAvatar());
                    intent.putExtra("location", startLocation);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }, 200);
        }
    }

    private void doLike(final int likenum, final boolean islike) {
        if (islike) {
            param = ParamData.getInstance().getLikeObj(chef.getChefids(), "0");
        } else {
            param = ParamData.getInstance().getLikeObj(chef.getChefids(), "1");
        }
        client.post(MyConstant.URL_MAINTOPLIKE, param, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                canlike = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    if (jobj.getString("resultcode").equals("1")) {
                        chef.setIslike(!islike);
                        like_iv.setSelected(!islike);
                        int num = likenum;
                        if (islike) {
                            num--;
                        } else {
                            num++;
                        }
                        chef.setLikenum(num);
                        String numStr = num + "";
                        like_tv.setText(numStr);
                    } else {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                canlike = true;
            }
        });
    }

    @OnClick(R.id.collect_rl)
    public void CollectFunction() {
        boolean isCollect = chef.iscollect();
        int collectnum = chef.getCollectnum();
        if (cancollect) {
            doCollect(isCollect, collectnum);
        }
    }

    public void todayOrder() {
        if (chef == null) {
            MainTools.ShowToast(ChefDetailActivity.this, "请刷新重试");
            return;
        }

        if (chef.getCookbookpic() == null || chef.getCookbookpic().size() == 0) {
            MainTools.ShowToast(context, "当前无菜单");
            return;
        }
        if (!chef.isopen()) {
            return;
        }
        if (MyConstant.user.getRole() == 2) {
            MainTools.ShowToast(context, "您好，暂时不支持主厨下订单");
            return;
        }

        String lastTime = null;
        if (!TextUtils.isEmpty(chef.getDinnertimes())) {
            String[] timesStr = chef.getDinnertimes().split(",");
            if (!TextUtils.isEmpty(timesStr[timesStr.length - 1])) {
                lastTime = MainTools.getLastTime(timesStr[timesStr.length - 1], chef.getAdvanceminute());
            }
        }
        Intent intent = new Intent(ChefDetailActivity.this, ConfirmOrderActivity.class);
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(lastTime)) {
            if (MainTools.timeCompare("11:00") && MainTools.timeCompare(lastTime)) {
                MainTools.ShowToast(ChefDetailActivity.this, "对不起，今日已无法下单。");
                return;
            } else {
                bundle.putInt("whenindex", 0);
            }
        } else {
            if (MainTools.timeCompare("11:00") && MainTools.timeCompare("17:00")) {
                MainTools.ShowToast(ChefDetailActivity.this, "对不起，今日已无法下单。");
                return;
            } else {
                bundle.putInt("whenindex", 0);
            }
        }

        if (chef.getLimitdistance() != 0 && (chef.getDistance() <= chef.getLimitdistance())) {
            bundle.putString("chefids", chef.getChefids());
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            String tipStr = "您超出点餐范围" + Tools.getDistanc(chef.getDistance() - chef.getLimitdistance()) + "不能点餐";
            tip_tv.setText(tipStr);
        }
    }

    public void tomorrowOrder() {
        if (chef == null) {
            MainTools.ShowToast(ChefDetailActivity.this, "请刷新重试");
            return;
        }
        if (chef.getCookbookpic() == null || chef.getCookbookpic().size() == 0) {
            MainTools.ShowToast(context, "当前无菜单");
            return;
        }
        if (!chef.isopen()) {
            return;
        }
        if (MyConstant.user.getRole() == 2) {
            MainTools.ShowToast(context, "您好，暂时不支持主厨下订单");
            return;
        }
        if (chef.getLimitdistance() == 0 || (chef.getDistance() > chef.getLimitdistance())) {
            MainTools.ShowToast(ChefDetailActivity.this, "对不起,已经超出点餐范围。");
            return;
        }
        Intent intent = new Intent(ChefDetailActivity.this, ConfirmOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("whenindex", 1);
        bundle.putString("chefids", chef.getChefids());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void doCollect(final boolean isCollect, final int collectnum) {
        gson = new Gson();
        if (!isCollect) {
            param = ParamData.getInstance().getLikeObj(chef.getChefids(), "1");
        } else {
            param = ParamData.getInstance().getLikeObj(chef.getChefids(), "0");
        }
        client.post(MyConstant.URL_MAINTOPCOLLECT, param, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                cancollect = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    if (jobj.getString("resultcode").equals("1")) {
                        chef.setIscollect(!isCollect);
                        collect_iv.setSelected(!isCollect);
                        int num = collectnum;
                        if (isCollect) {
                            num--;
                        } else {
                            num++;
                        }
                        chef.setCollectnum(num);
                        String numStr = num + "";
                        collect_tv.setText(numStr);
                    } else {
                        MainTools.ShowToast(context, jobj.getString("tag"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(context, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                cancollect = true;
            }
        });
    }

    @OnClick(R.id.cardView_story)
    public void toChefDetail() {
        if (chef != null) {
            Intent intent = new Intent(ChefDetailActivity.this, CookStoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("commentnum", chef.getCommentnum() + "");
            if (chef.getCommentnum() > 0) {
                bundle.putFloat("score", chef.getTotalscore() / chef.getCommentnum() * 0.5f);
            } else {
                bundle.putFloat("score", 5f);
            }
            bundle.putString("avatar", chef.getAvatar());
            String hometownStr;
            if (!TextUtils.isEmpty(chef.getHomecity())) {
                hometownStr = chef.getHomeprov() + chef.getHomecity();
            } else {
                hometownStr = chef.getHomeprov();
            }
            bundle.putString("hometown", hometownStr);
            bundle.putString("nickname", chef.getNickname());
            if (!TextUtils.isEmpty(chef.getStory())) {
                bundle.putString("story", chef.getStory().replace("\n", "").replace("\r", ""));
            } else {
                bundle.putString("story", "");
            }
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
        }
        convenientBanner.stopTurning();
        ButterKnife.unbind(this);
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

    private void animateOut(final RelativeLayout button) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
                    .setInterpolator(INTERPOLATOR).withLayer()
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fadeout);
            anim.setInterpolator(INTERPOLATOR);
            anim.setDuration(200L);
            button.startAnimation(anim);
        }
    }

    // Same animation that FloatingActionButton.Behavior
    // uses to show the FAB when the AppBarLayout enters
    private void animateIn(RelativeLayout button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fadein);
            anim.setDuration(200L);
            anim.setInterpolator(INTERPOLATOR);
            button.startAnimation(anim);
        }
    }

    public void showTipDialog(final AppCompatActivity activity, String title, int resultcode) {
        if (resultcode == -1) {
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
                    SharedPreferenceUtil.initSharedPerence().putHomeProv(editor, "");
                    SharedPreferenceUtil.initSharedPerence().putLoginPhone(editor, "");
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
                    MyConstant.user.clear();
                    AppManager.getAppManager().finishAllActivity();
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            }).create();
            dialog.show();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ChefDetailActivity.this, PhotoViewVPActivity.class);
        intent.putExtra("imgList", picList);
        intent.putExtra("item", position);
        startActivity(intent);
    }
}
