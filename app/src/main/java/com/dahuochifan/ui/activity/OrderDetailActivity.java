package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.cookbook.OrderDetailInfo;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.views.ActionSheet;
import com.dahuochifan.ui.views.ActionSheet.ActionSheetListener;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.PreviewLoader;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.utils.L;
import com.payment.alipay.demo.Dh_Alipay;
import com.payment.alipay.demo.PayResult;
import com.payment.wechatpay.demo.Dh_WechatPay;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.iwgang.countdownview.CountdownView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class OrderDetailActivity extends BaseActivity implements ActionSheetListener, PlatformActionListener {
    private OrderInfo orderinfo;
    private TextView chef_nickname_tv, level_tv, price_tv, number_tv, name_tv, phone_number_tv, addressinfo_tv;
    private TextView type_tv, status_card, liuyan_et;
    private CircleImageView chef_avatar_iv;
    private RippleView bottom_tv;
    private Context context;
    private TextView address_tv;
    private TextView all_price_tv;
    private TextView tag_tv;
    private TextView order_date;
    private TextView order_no_tv;
    private ImageView coupon_iv;
    private TextView time_tv;
    private PercentRelativeLayout cancel_rl;
    private PercentRelativeLayout preview_rl;
    private TextView detail_tv, refresh_tv;
    private ImageView back_bg;
    private SweetAlertDialog pDialog;
    private TextView actdel_tv, coudel_tv;
    private TextView distribution_tv, tableware_tv;
    // private TextView delete_tv;

    public static OrderDetailActivity instance;

    private int index;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private SweetAlertDialog pd;
    private RelativeLayout top_rl;
    private double all_price;
    private TextView discount_tv;
    private RelativeLayout discount_rl;
    private TextView orderpay_tv;
    private TextView orderpay;
    private SwipeRefreshLayout swipe;
    private ScrollView order_detail_sc;
    private boolean canRefresh;
    private RelativeLayout main_rl;
    private int containerWidth;
    private int containerHeight;
    private CountdownView countView;
    float lastX, lastY;
    float myStartX = 0, myStartY = 0;

    static class MyHandler extends Handler {
        WeakReference<SweetAlertDialog> puReferences;
        WeakReference<OrderDetailActivity> wrReferences;

        MyHandler(SweetAlertDialog wrReferences, OrderDetailActivity wrReference) {
            this.puReferences = new WeakReference<>(wrReferences);
            this.wrReferences = new WeakReference<>(wrReference);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (puReferences.get() != null)
                        puReferences.get().dismiss();
                    break;
                case 1:
                    if (puReferences.get() != null)
                        puReferences.get().dismiss();
                    wrReferences.get().finish();
                    break;
                case MyConstant.SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(wrReferences.get(), "支付成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                        wrReferences.get().finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(wrReferences.get(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                            wrReferences.get().finish();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(wrReferences.get(), "订单尚未被支付", Toast.LENGTH_SHORT).show();
                        }
                    }
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
        initData();
        initViews();
//        initStatus();
        getMainData(false);
        btn_listner();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        orderinfo = (OrderInfo) bundle.getSerializable("obj");
        index = bundle.getInt("index");
        context = this;
        instance = this;
        ShareSDK.initSDK(this);
        canRefresh = true;
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        pDialog = new SweetAlertDialog(OrderDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在加载中");
        pDialog.setCancelable(false);
        pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(OrderDetailActivity.this, R.color.blue_btn_bg_color));
        pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        main_rl = (RelativeLayout) findViewById(R.id.main_rl);
        chef_nickname_tv = (TextView) findViewById(R.id.chef_nickname_tv);
        level_tv = (TextView) findViewById(R.id.level_tv);
        price_tv = (TextView) findViewById(R.id.price_tv);
        number_tv = (TextView) findViewById(R.id.person_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);
        tag_tv = (TextView) findViewById(R.id.tag_tv);
        phone_number_tv = (TextView) findViewById(R.id.phone_number_tv);
        addressinfo_tv = (TextView) findViewById(R.id.addressinfo_tv);

        type_tv = (TextView) findViewById(R.id.type_tvme);
        status_card = (TextView) findViewById(R.id.status_card);
        liuyan_et = (TextView) findViewById(R.id.liuyan_et);

        chef_avatar_iv = (CircleImageView) findViewById(R.id.chef_avatar_iv);
        bottom_tv = (RippleView) findViewById(R.id.bottom_tv);
        address_tv = (TextView) findViewById(R.id.address_tv);
        all_price_tv = (TextView) findViewById(R.id.all_price_tv);
        order_date = (TextView) findViewById(R.id.order_date);
        order_no_tv = (TextView) findViewById(R.id.order_no_tv);
        top_rl = (RelativeLayout) findViewById(R.id.top_rl);
        discount_tv = (TextView) findViewById(R.id.discount_tv);
        discount_rl = (RelativeLayout) findViewById(R.id.discount_rl);
        orderpay_tv = (TextView) findViewById(R.id.orderpay_tv);
        orderpay = (TextView) findViewById(R.id.orderpay);
        coupon_iv = (ImageView) findViewById(R.id.coupon_iv);
        time_tv = (TextView) findViewById(R.id.time_tv);
        cancel_rl = (PercentRelativeLayout) findViewById(R.id.cancel_rl);
        preview_rl = (PercentRelativeLayout) findViewById(R.id.preview_rl);
        detail_tv = (TextView) findViewById(R.id.detail_tv);
        refresh_tv = (TextView) findViewById(R.id.refresh_tv);
        back_bg = (ImageView) findViewById(R.id.back_bg);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        distribution_tv = (TextView) findViewById(R.id.distribution_tv);
        tableware_tv = (TextView) findViewById(R.id.tableware_tv);
        swipe.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        order_detail_sc = (ScrollView) findViewById(R.id.order_detail_sc);
        countView = (CountdownView) findViewById(R.id.countView);
        actdel_tv = (TextView) findViewById(R.id.actdel_tv);
        coudel_tv = (TextView) findViewById(R.id.coudel_tv);
        countView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                //计时结束处理
                EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                new AlertDialog.Builder(OrderDetailActivity.this).setCancelable(false).setTitle("提示").setMessage("该订单将自动取消，请退出重新下单")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    private void initStatus() {
        if (!TextUtils.isEmpty(orderinfo.getLunchtime())) {
            time_tv.setText(MainTools.changeMD2(orderinfo.getEatenwhen()) + " " + "中午" + " " + orderinfo.getLunchtime());
        } else if (!TextUtils.isEmpty(orderinfo.getDinnertime())) {
            time_tv.setText(MainTools.changeMD2(orderinfo.getEatenwhen()) + " " + "晚上" + " " + orderinfo.getDinnertime());
        } else {
            time_tv.setText(MainTools.changeMD2(orderinfo.getEatenwhen()));
        }
        if (!TextUtils.isEmpty(orderinfo.getTag())) {
            tag_tv.setText(orderinfo.getTag());
        }
        chef_nickname_tv.setText(orderinfo.getNickname());
        CookerHead.loadImage(orderinfo.getAvatar(), chef_avatar_iv);
        order_date.setText(orderinfo.getCreatetime());

        if (orderinfo.isgrapcoupon() && orderinfo.getGrapcoupon() != null) {
            coupon_iv.setVisibility(View.VISIBLE);
            PreviewLoader.loadImage(orderinfo.getGrapcoupon().getIcon(), coupon_iv);
        } else {
            coupon_iv.setVisibility(View.GONE);
        }
        if (orderinfo.getOlstatus().equals("1")) {
            cancel_rl.setVisibility(View.GONE);
            if (orderinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            }
            if (orderinfo.isIscomment()) {
                bottom_tv.setText("已完成订单");
                bottom_tv.setVisibility(View.VISIBLE);
                bottom_tv.setBackgroundResource(R.color.gray);
                bottom_tv.setEnabled(false);
            } else {
                bottom_tv.setText("给个评价");
                bottom_tv.setVisibility(View.VISIBLE);
                bottom_tv.setBackgroundResource(R.color.maincolor_new);
                bottom_tv.setEnabled(true);
            }
        } else if (orderinfo.getOlstatus().equals("3")) { //表示待确认
            cancel_rl.setVisibility(View.GONE);
            if (orderinfo.getConfirmtime() > 0) {
                orderpay_tv.setVisibility(View.VISIBLE);
                orderpay.setVisibility(View.VISIBLE);
                orderpay_tv.setText(MainTools.getDateStr(orderinfo.getConfirmtime()));
            }
            if (MainTools.timeCompare3(orderinfo.getConfirmtime())) {
                postStatus(orderinfo.getOlids(), "1", orderinfo);
            }
            if (orderinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            }
            bottom_tv.setEnabled(true);
            bottom_tv.setText("确认用餐");
            bottom_tv.setVisibility(View.VISIBLE);
        } else if (orderinfo.getOlstatus().equals("2")) { //表示带接单
            if (orderinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {// 已付款，未接单
                status_card.setText("已付款");
                bottom_tv.setText("等待接单");
                cancel_rl.setVisibility(View.VISIBLE);
                countView.setVisibility(View.GONE);
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_NO)) {//已付款，未接单
                cancel_rl.setVisibility(View.VISIBLE);
                status_card.setText("未付款");
                bottom_tv.setText("付款");
                long timeRemain = (long) orderinfo.getCountdown() * 1000;
                countView.setVisibility(View.VISIBLE);
                countView.stop();
                countView.start(timeRemain);
            } else if (MyConstant.PAY_WATING.equals(orderinfo.getPaystatus())) {
                cancel_rl.setVisibility(View.VISIBLE);
                status_card.setText("");
                bottom_tv.setText("支付处理中");
                cancel_rl.setVisibility(View.GONE);
                countView.setVisibility(View.GONE);
            }
            bottom_tv.setVisibility(View.VISIBLE);
        } else if (orderinfo.getOlstatus().equals("4")) { //表示已拒接
            cancel_rl.setVisibility(View.GONE);
            if (orderinfo.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                status_card.setText("退款中");
            } else if (orderinfo.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                status_card.setText("已退款");
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            }
            bottom_tv.setText("主厨已拒单");
            bottom_tv.setBackgroundResource(R.color.gray);
            bottom_tv.setEnabled(false);
            bottom_tv.setVisibility(View.VISIBLE);
        } else if (orderinfo.getOlstatus().equals("5")) { //表示已取消
            cancel_rl.setVisibility(View.GONE);
            if (orderinfo.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                status_card.setText("退款中");
            } else if (orderinfo.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                status_card.setText("已退款");
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            }
            bottom_tv.setText("已取消订单");
            bottom_tv.setBackgroundResource(R.color.gray);
            bottom_tv.setEnabled(false);
            bottom_tv.setVisibility(View.VISIBLE);
        }
        name_tv.setText(orderinfo.getDinername());
        type_tv.setText(orderinfo.getEatenway());
        level_tv.setText(orderinfo.getCbname());
        price_tv.setText("￥ " + MainTools.getDoubleValue(orderinfo.getPrice(), 1));
        number_tv.setText("x  " + orderinfo.getEatennum());
        phone_number_tv.setText(orderinfo.getDinermobile());
        distribution_tv.setText(orderinfo.getDeliveryfee() > 0 ? "￥ " + orderinfo.getDeliveryfee() : "免费配送");
        tableware_tv.setText(orderinfo.getTablewarefee() > 0 ? "￥ " + orderinfo.getTablewarefee() : "免费配送");
        if (TextUtils.isEmpty(orderinfo.getRemark())) {
            liuyan_et.setText("无");
        } else {
            liuyan_et.setText(orderinfo.getRemark());
        }
        order_no_tv.setText(orderinfo.getNumber());

        if (orderinfo.getEatenway().equals("自提")) {
            address_tv.setText("自提地址：");
            addressinfo_tv.setText(orderinfo.getZitiaddr());
        } else {
            addressinfo_tv.setText(orderinfo.getDineraddr());
        }
        if (BuildConfig.LEO_DEBUG)
            L.e(orderinfo.getDiscount() + "discount");
        if (orderinfo.getDiscount() > 0 || orderinfo.getCoupondiscount() > 0) {
            discount_rl.setVisibility(View.VISIBLE);
            double discountResult = Arith.mul(orderinfo.getPrice(), orderinfo.getEatennum());
            double feeAll = orderinfo.getTablewarefee() + orderinfo.getDeliveryfee();
            discountResult = Arith.add(discountResult, feeAll);
            discount_tv.setText("￥ " + MainTools.getDoubleValue(discountResult, 1));
        } else {
            discount_rl.setVisibility(View.GONE);
        }
        if (orderinfo.getDiscount() > 0) {
            actdel_tv.setText("-￥ " + MainTools.getDoubleValue(orderinfo.getDiscount(), 1));
        } else {
            actdel_tv.setText("￥ 0");
        }
        if (orderinfo.getCoupondiscount() > 0) {
            coudel_tv.setText("-￥" + orderinfo.getCoupondiscount());
        } else {
            coudel_tv.setText("￥ 0");
        }
        all_price = orderinfo.getTotal();
        all_price_tv.setText("￥" + MainTools.getDoubleValue(all_price, 1));
    }

    public void setDateView(TextView view1, String week) {
        view1.setText(week);
    }

    private void btn_listner() {
        refresh_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainData(false);
            }
        });
        top_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, ChefDetailActivity.class);
                intent.putExtra("tag", orderinfo.getTag());
                intent.putExtra("myids", orderinfo.getChefids());
                startActivity(intent);
            }
        });
        bottom_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottom_tv.getText().equals("给个评价")) {
                    Intent intent = new Intent(context, CommentsNewActivity.class);
                    intent.putExtra("obj", orderinfo);
                    context.startActivity(intent);
                } else if (bottom_tv.getText().equals("付款")) {
                    if (cancel_rl.isShown() && countView.getRemainTime() < 60000) {
                        new AlertDialog.Builder(OrderDetailActivity.this).setCancelable(false).setTitle("提示").setMessage("由于您在15分钟内未操作该订单，您无法支付该订单")
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        postStatus(orderinfo.getOlids(), "8", orderinfo);
                    }
                } else if (bottom_tv.getText().equals("确认用餐")) {
                    postStatus(orderinfo.getOlids(), "1", orderinfo);
                }
            }
        });
        cancel_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (orderinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                    if (countView.getRemainTime() < 60000) {
                        new AlertDialog.Builder(OrderDetailActivity.this).setCancelable(false).setTitle("提示").setMessage("由于您在15分钟内未操作该订单，该订单将在1分钟内自动取消")
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } else {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否取消该条订单").setCancelText("取消")
                                .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                if (MainTools.isNetworkAvailable(context)) {
                                    postStatus(orderinfo.getOlids(), "5", orderinfo);
                                } else {
                                    MainTools.ShowToast(context, R.string.interneterror);
                                }
                            }
                        }).show();
                    }
                } else if (orderinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否取消该条订单").setCancelText("取消")
                            .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            if (MainTools.isNetworkAvailable(context)) {
                                postStatus(orderinfo.getOlids(), "5", orderinfo);
                            } else {
                                MainTools.ShowToast(context, R.string.interneterror);
                            }
                        }
                    }).show();
                }

            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (orderinfo != null && orderinfo.getOlids() != null)
                    getMainData(true);
            }
        });
        order_detail_sc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int scrollY = view.getScrollY();
                        if (scrollY == 0) {
                            swipe.setEnabled(true);
                        } else {
                            if (swipe.isEnabled()) {
                                swipe.setEnabled(false);
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        coupon_iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View iv, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        myStartX = lastX;
                        myStartY = lastY;
                        swipe.setEnabled(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //  不要直接用getX和getY,这两个获取的数据已经是经过处理的,容易出现图片抖动的情况
                        float distanceX = lastX - event.getRawX();
                        float distanceY = lastY - event.getRawY();

                        float nextY = iv.getY() - distanceY;
                        float nextX = iv.getX() - distanceX;

                        // 不能移出屏幕
                        if (nextY < 0) {
                            nextY = 0;
                        } else if (nextY > containerHeight - iv.getHeight()) {
                            nextY = containerHeight - iv.getHeight();
                        }
                        if (nextX < 0)
                            nextX = 0;
                        else if (nextX > containerWidth - iv.getWidth())
                            nextX = containerWidth - iv.getWidth();

                        // 属性动画移动
                        ObjectAnimator y = ObjectAnimator.ofFloat(iv, "y", iv.getY(), nextY);
                        ObjectAnimator x = ObjectAnimator.ofFloat(iv, "x", iv.getX(), nextX);

                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(x, y);
                        animatorSet.setDuration(0);
                        animatorSet.start();

                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_UP:
                        double nextXs = Math.abs(Arith.sub(myStartX, event.getRawX()));
                        double nextYs = Math.abs(Arith.sub(myStartY, event.getRawY()));
                        double allD = Math.sqrt(Arith.mul(nextXs, nextXs) + Arith.mul(nextYs, nextYs));
                        if (allD < 20) {
                            setTheme(R.style.ActionSheetStyleIOS7);
                            showActionSheet();
                        }
                }
                return false;
            }
        });
    }

    /**
     * 重新加载当前订单
     */
    private void getMainData(final boolean isRecyc) {
        params = ParamData.getInstance().getOrderDetail(orderinfo.getOlids(), "0");
        client.post(MyConstant.GET_ORDER_DETAIL, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                canRefresh = false;
                if (!isRecyc) {
                    if (pDialog != null)
                        pDialog.show();
                }
                super.onStart();
            }

            @Override
            public void onFinish() {
                canRefresh = true;
                super.onFinish();
                if (swipe != null)
                    swipe.setRefreshing(false);
                if (pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(OrderDetailActivity.this, R.string.interneterror);
                if (!isRecyc) {
                    preview_rl.setVisibility(View.VISIBLE);
                    back_bg.setVisibility(View.VISIBLE);
                    refresh_tv.setVisibility(View.VISIBLE);
                    detail_tv.setVisibility(View.VISIBLE);
                    detail_tv.setText("网络不给力");
                }
                back_bg.setBackgroundResource(R.mipmap.internet_failed);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                OrderDetailInfo info = gson.fromJson(responseString, OrderDetailInfo.class);
                if (info.getResultcode() == 1) {
                    preview_rl.setVisibility(View.GONE);
                    orderinfo = info.getObj();
                    initStatus();
                } else {
                    if (info != null && info.getTag() != null) {
                        showTipDialog(OrderDetailActivity.this, info.getTag(), info.getResultcode());
                    } else {
                        showTipDialog(OrderDetailActivity.this, "未知错误", -1);
                    }
                }
            }
        });
    }

    /**
     * 分享优惠券的方法
     */
    private void showActionSheet() {
        ActionSheet.createBuilder(this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("分享给好友", "分享到朋友圈")
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    private void showPayDialog() {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet.createBuilder(OrderDetailActivity.this, getSupportFragmentManager()).setCancelButtonTitle("取消")
                .setOtherButtonTitles("支付宝支付", "微信支付").setCancelableOnTouchOutside(true).setListener(new ActionSheetListener() {

            @Override
            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                switch (index) {
                    case 0:
                        MyHandler handler = new MyHandler(pd, OrderDetailActivity.this);
                        Dh_Alipay mypay = new Dh_Alipay(OrderDetailActivity.this, handler);
                        String detail_Str = "单价" + orderinfo.getPrice() + "元-" + "订餐人数" + orderinfo.getEatennum() + "人-" + "总价" + all_price
                                + "元-" + "优惠" + "0" + "元";
                        if (BuildConfig.LEO_DEBUG)
                            L.e("all_price===" + all_price);
                        mypay.payFunction(orderinfo.getNickname() + "-" + orderinfo.getCbname() + "-" + orderinfo.getTag(), detail_Str,
                                all_price + "", orderinfo.getNumber());
                        break;
                    case 1:
                        Dh_WechatPay wepay = new Dh_WechatPay(OrderDetailActivity.this);
                        String detail_Str2 = "单价" + orderinfo.getPrice() + "元-" + "订餐人数" + orderinfo.getEatennum() + "人-" + "总价"
                                + all_price + "元-" + "优惠" + "0" + "元";
                        wepay.payFunction(orderinfo.getNickname() + "-" + orderinfo.getCbname() + "-" + orderinfo.getTag(), detail_Str2,
                                (int) (all_price * 100) + "", orderinfo.getNumber());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

            }
        }).show();
    }

    private void deleteOrder() {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否删除该条订单").setCancelText("取消").setConfirmText("确定")
                .showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
            }
        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                if (MainTools.isNetworkAvailable(context)) {
                    postStatus(orderinfo.getOlids(), "6", orderinfo);
                } else {
                    MainTools.ShowToast(context, R.string.interneterror);
                }
            }
        }).show();
    }

    private void postStatus(final String olids, final String status, OrderInfo orderinfo) {
        params = ParamData.getInstance().postStatusObj(olids, status, orderinfo.getOlversion());
        client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (status.equals("5")) {
                    pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在取消订单");
                } else if (status.equals("6")) {
                    pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在删除订单");
                } else if (status.equals("1")) {
                    pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在确认用餐");
                } else if (status.equals("8")) {
                    pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在完成付款");
                }
                pd.setCancelable(false);
                pd.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    String tag = jobj.getString("tag");
                    if (!status.equals("8")) {
                        if (request == 1) {
                            if (status.equals("5")) {
                                EventBus.getDefault().post(new FirstEvent("MyOrder"));
                            }
                            pd.setTitleText(jobj.getString("tag")).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            MyHandler handler = new MyHandler(pd, OrderDetailActivity.this);
                            handler.sendEmptyMessageDelayed(1, 1500);
                        } else {
                            pd.setContentText(jobj.getString("tag")).setTitleText("错误提示").setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            getMainData(true);
                        }
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                        if (status.equals("1")) {
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_MOVEONE));
                        }
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_TWO));
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_THREE));
                    } else {
                        if (request == 1) {
                            showPayDialog();
                            pd.dismiss();
                        } else {
                            getMainData(true);
                            pd.dismiss();
                            new AlertDialog.Builder(context).setCancelable(false).setTitle("支付异常").setMessage(jobj.getString("tag"))
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                            L.e("olids====" + olids);
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                // mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_orderdetail;
    }

    @Override
    protected String initToolbarTitle() {
        return "订单详情";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//			case R.id.share :
//				shareFunction();
//				return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                MainTools.ShowToast(this, "正在分享请稍等");
                Platform plat = ShareSDK.getPlatform(OrderDetailActivity.this, Wechat.NAME);
                plat.setPlatformActionListener(this);
                Wechat.ShareParams sp = new Wechat.ShareParams();
                // 任何分享类型都需要title和text
                sp.title = orderinfo.getGrapcoupon().getTitle();
                sp.text = orderinfo.getGrapcoupon().getDesc();
                sp.shareType = Platform.SHARE_WEBPAGE;
                sp.url = orderinfo.getGrapcoupon().getUrl();
                sp.imageUrl = orderinfo.getGrapcoupon().getIcon();
                plat.share(sp);
                break;
            case 1:
                MainTools.ShowToast(this, "正在分享请稍等");
                Platform plat2 = ShareSDK.getPlatform(OrderDetailActivity.this, WechatMoments.NAME);
                plat2.setPlatformActionListener(this);
                Wechat.ShareParams sp2 = new Wechat.ShareParams();
                sp2.title = orderinfo.getGrapcoupon().getTitle();
                sp2.text = orderinfo.getGrapcoupon().getDesc();
                sp2.shareType = Platform.SHARE_WEBPAGE;
                sp2.url = orderinfo.getGrapcoupon().getUrl();
                sp2.imageUrl = orderinfo.getGrapcoupon().getIcon();
                plat2.share(sp2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        MainTools.ShowToast(this, "分享完成");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        MainTools.ShowToast(this, "分享失败");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        MainTools.ShowToast(this, "分享被取消");
    }

    /**
     * @param event EventBus事件
     *              接收eventbus监听
     */
    public void onEventMainThread(FirstEvent event) {
        if (event != null && event.getType() == MyConstant.EVENTBUS_ORDER_ONE) {
            getMainData(true);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (countView != null) {
            countView.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 这里来获取容器的宽和高
        if (hasFocus) {
            containerHeight = main_rl.getHeight();
            containerWidth = main_rl.getWidth();
        }
    }
}
