package com.dahuochifan.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.address.AddressAll;
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.core.order.OrderListV11;
import com.dahuochifan.core.order.OrderParamV11;
import com.dahuochifan.core.requestdata.address.AddressData;
import com.dahuochifan.event.AddressEvent;
import com.dahuochifan.event.DiscountEvent;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.interfaces.UnitPriceItemListener;
import com.dahuochifan.model.cookbook.CBActs;
import com.dahuochifan.model.cookbook.CBAll;
import com.dahuochifan.model.cookbook.CBChef;
import com.dahuochifan.model.cookbook.CBCookBook;
import com.dahuochifan.model.cookbook.CBeatentimeMap;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.cookbook.CBData;
import com.dahuochifan.ui.adapter.UnitPriceAdapter;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.ui.views.popwindow.ActPopWindow;
import com.dahuochifan.ui.views.popwindow.ConfirmPayPopWindow;
import com.dahuochifan.ui.views.popwindow.TagPopWindow;
import com.dahuochifan.ui.views.popwindow.TimePopWindow2;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.PreviewLoader;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;
import com.payment.alipay.demo.Dh_Alipay;
import com.payment.alipay.demo.PayResult;
import com.payment.wechatpay.demo.Dh_WechatPay;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.wechat.friends.Wechat;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2015/11/19.
 * order
 */
public class ConfirmOrderActivity extends BaseActivity implements AMapLocationListener {
    /**
     * 加载失败后的预览界面
     */
    @Bind(R.id.preview_rl)
    PercentRelativeLayout preview_rl;
    @Bind(R.id.detail_tvs)
    TextView detail_tvs;
    @Bind(R.id.refresh_tv)
    TextView refresh_tv;
    @Bind(R.id.back_bg)
    ImageView back_bg;
    private boolean isFirst;

    @Bind(R.id.order_date_tv)
    TextView order_date_tv;//条目日期
    @Bind(R.id.order_price_tv)
    TextView order_price_tv;//条目单价
    @Bind(R.id.order_num_tv)
    TextView order_num_tv;//条目点餐份数
    @Bind(R.id.tableware_remark_tv)
    TextView tableware_remark_tv;//条目餐具费
    @Bind(R.id.trans_number_remark_tv)
    TextView trans_number_remark_tv;//配送费条目
    @Bind(R.id.order_tags_tv)
    TextView order_tags_tv;//条目口味
    @Bind(R.id.addr_remark_tv)
    TextView addr_remark_tv;//条目地址
    @Bind(R.id.all_remark_tv)
    TextView all_remark_tv;//条目总价格
    @Bind(R.id.order_time_tv)
    TextView order_time_tv;//条目用餐时间、
    @Bind(R.id.order_time_tv2)
    TextView order_time_tv2;//条目用餐时间
    @Bind(R.id.order_nickname_tv)
    TextView order_nickname_tv;//条目主厨姓名
    @Bind(R.id.order_remark_tv)
    TextView order_remark_tv;//条目备注
    @Bind(R.id.name_remark_tv)
    TextView name_remark_tv;//条目姓名
    @Bind(R.id.phone_remark_tv)
    TextView phone_remark_tv;//条目手机号
    /**
     * 地址栏
     */
    @Bind(R.id.nickname_tv)
    TextView nickname_tv;
    @Bind(R.id.mobile_tv)
    TextView mobile_tv;
    @Bind(R.id.address_tv)
    TextView address_tv;
    @Bind(R.id.addr_tv)
    TextView addr_tv;//添加地址的文字
    @Bind(R.id.address_content)
    RelativeLayout top_info_rl;//包含地址电话和昵称的rl
    private UnitPriceAdapter unitAdapter;
    /**
     * 属性栏
     */
    @Bind(R.id.tag_iv)
    ImageView tag_iv;//口味的箭头
    @Bind(R.id.tag_tv)
    TextView tag_tv;//口味的文字
    @Bind(R.id.time_pop_choose_tv)
    TextView time_pop_choose_tv;//用餐时间tv
    @Bind(R.id.time_pop_rl)
    RelativeLayout time_pop_rl;//时间rl
    @Bind(R.id.dotview)
    View dot_view;//用于固定popwindow
    @Bind(R.id.attr_rl2)
    RelativeLayout attr_rl2;//用于弹出口味的rl
    /**
     * 价格栏
     */
    @Bind(R.id.today_relay_tv)
    TextView today_relay_tv;//今日剩余
    @Bind(R.id.tomorrow_relay_tv)
    TextView tomorrow_relay_tv;//明日剩余
    @Bind(R.id.person_tv)
    TextView person_tv;//用餐人数
    @Bind(R.id.add_iv2)
    ImageView add_iv2;//增加人数
    @Bind(R.id.delete_iv2)
    ImageView delete_iv2;//减少人数
    @Bind(R.id.tableware_tv)
    TextView tableware_tv;//餐具费tv
    @Bind(R.id.tableware_other_tv)
    TextView tableware_other_tv;//餐具费单位
    @Bind(R.id.tableware_tip)
    TextView tableware_tip;//免费提供的文字
    @Bind(R.id.trans_number_tv)
    TextView trans_number_tv;//配送费tv
    @Bind(R.id.trans_other_tv)
    TextView trans_other_tv;//配送费单位
    @Bind(R.id.tans_tip)
    TextView tans_tip;//免费配送的文字
    @Bind(R.id.exp_rb)
    RadioButton exp_rb;//配送方式
    @Bind(R.id.ali_rb)
    RadioButton ali_rb;//支付方式ali
    @Bind(R.id.wechat_rb)
    RadioButton wechat_rb;//支付方式wechat
    @Bind(R.id.activity_tv)
    TextView activity_tv;//显示减免优惠的信息
    @Bind(R.id.discount_tv)
    TextView discount_tv;//优惠券张数
    @Bind(R.id.more_discount_rl)
    RelativeLayout more_discount_rl;//查看更多优惠券的rl
    @Bind(R.id.remark_et)
    EditText remark_et;//备注et
    @Bind(R.id.act_type_rl)
    RelativeLayout act_type_rl;//获取活动类型的rl
    @Bind(R.id.act_type_tv)
    TextView act_type_tv;//活动的描述
    @Bind(R.id.act_type_tv2)
    TextView act_type_tv2;//活动的描述
    @Bind(R.id.act_type_iv)
    ImageView act_type_iv;//活动的图标
    @Bind(R.id.red_arrow2)
    ImageView red_arrow2;
    @Bind(R.id.red_arrow)
    ImageView red_arrow;
    @Bind(R.id.type_gp)
    RadioGroup type_gp;
    @Bind(R.id.discount_other_tv)
    TextView discount_other_tv;
    @Bind(R.id.acitivity_tip)
    TextView acitivity_tip;
    @Bind(R.id.price_cyc)
    RecyclerView price_cyc;
    List<CBCookBook> unitList = new ArrayList<>();
    //单价的RecyclerView 的 Adapter


    /**
     * 主体布局
     */
    @Bind(R.id.main_rl)
    RelativeLayout main_rl;
    @Bind(R.id.swipe)
    SwipeRefreshLayout swipe;
    /**
     * 底部提示
     */
    @Bind(R.id.bottom_tip_tv)
    TextView bottom_tip_tv;
    @Bind(R.id.priceall_tv)
    TextView priceall_tv;//底部显示总价格
    @Bind(R.id.confirm_tv)
    TextView confirm_tv;//确认用餐
    private ConfirmPayPopWindow payPopWindow;//确认用餐的pop
    @Bind(R.id.bottom_achor_view)
    View bottom_achor_view;
    /**
     * 初始化数据
     */
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private CBCookBook cbBook;
    private CBChef cbChef;
    private CBActs cbActs;
    private CBAll cbAll;
    private AddressInfo info;
    private TagPopWindow finalPopWindow;
    private ActPopWindow actPop;
    private String unitPrice;//所选择的单价
    /**
     * 传递过来的参数
     */
    private int whenIndexint;
    private String chefids;

    private String[] pricesZon;//价格数组
    private int priceIndex;//价格下标
    private int maxPeople;//订餐人数上限
    private double priceAll;//结算后的总价

    private boolean hasAddr;//是否拥有正确的地址
    private boolean canTijiao;//是否可以提交
    private int addressNum;//地址的总个数
    private int discountLimit;//优惠券的满额
    private int discountPrice;//优惠券的减免额度
    private String discountId;//优惠券的id
    private double discount;
    private float addressDistance;

    private AlertDialog dialog;
    private SweetAlertDialog pDialog;
    private double fee;
    private String myOlids;
    private OrderParamV11 op;
    private OrderListV11 order;

    private final static int SDK_PAY_FLAG = 101;
    private final static int SDK_CHECK_FLAG = 102;
    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;
    private String geoLat, geoLng;
    private SharedPreferences.Editor editor;
//    /**
//     * 关于优惠券的list
//     */
//    List<DiscountDetail> detailList;
    /**
     * popwindow
     */
    private TimePopWindow2 timePop;

    /**
     * refreshveiw嵌套de scrollview
     */

    @Bind(R.id.dhscroll)
    ScrollView dhscroll;
    private int deliFee;

    public void onEventMainThread(AddressEvent event) {
        if (event != null && event.getType() != 0) {
            switch (event.getType()) {
                case MyConstant.EVENTBUS_CHOOSE_ADDR:
                    showTips(false, "添加地址");
                    info = event.getAddressInfo();
                    L.e(info.getLoc_detail());
                    addrFunction(info);
                    break;
            }
        }
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && event.getMsg() != null) {
            if (event.getMsg().equals("Add")) {
                getDefaultAddress();
            } else if (event.getMsg().equals("Edit")) {
                getDefaultAddress();
            }
        }
        if (event != null && event.getType() == MyConstant.EVENTBUS_PAY) {
            if (event.getPosition() == 0) {
                jumpFunction(1);//代表微信支付成功
                postSuccess();
            } else {
                if (pDialog != null) {
                    pDialog.dismiss();
                    MainTools.ShowToast(this, event.getPosition() == -1 ? "微信支付被取消！" : "微信支付失败！");
                }
                jumpFunction(0);
            }
        }
    }

    public void onEventMainThread(DiscountEvent event) {
        if (event != null && event.getType() == MyConstant.EVENTBUS_DISCOUNT) {
            discountLimit = event.getDiscountLimit();
            discountPrice = event.getDiscountPrice();
            discount_tv.setText("-" + discountPrice + "￥");
            discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.text_gold));
            red_arrow.setVisibility(View.VISIBLE);
            changePrice();
            discountId = event.getDiscountId();
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_confirmorder;
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (location.getErrorCode() == 0) {
                geoLat = location.getLatitude() + "";
                geoLng = location.getLongitude() + "";
                if (!TextUtils.isEmpty(geoLng) && !TextUtils.isEmpty(geoLat)) {
//                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, geoLng);
//                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, geoLat);
//                    editor.commit();
                    getCB();
                } else {
                    if (isFirst) {
                        dismissAlertDialog();
                        preview_rl.setVisibility(View.VISIBLE);
                        detail_tvs.setVisibility(View.VISIBLE);
                        back_bg.setVisibility(View.VISIBLE);
                        refresh_tv.setVisibility(View.VISIBLE);
                        detail_tvs.setText("网络不给力");
                        back_bg.setBackgroundResource(R.mipmap.internet_failed);
                    }
                }
            } else {
                if (isFirst) {
                    dismissAlertDialog();
                    preview_rl.setVisibility(View.VISIBLE);
                    detail_tvs.setVisibility(View.VISIBLE);
                    back_bg.setVisibility(View.VISIBLE);
                    refresh_tv.setVisibility(View.VISIBLE);
                    detail_tvs.setText("网络不给力");
                    back_bg.setBackgroundResource(R.mipmap.internet_failed);
                }
                MainTools.ShowToast(ConfirmOrderActivity.this, location.getErrorInfo());
                swipe.setRefreshing(false);
            }
        }
    }

    static class MyHandler extends Handler {
        WeakReference<ConfirmOrderActivity> wrReferences;

        MyHandler(ConfirmOrderActivity wrReferences) {
            this.wrReferences = new WeakReference<>(wrReferences);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE4:
                    wrReferences.get().jumpFunction(1);
                    break;
                case MyConstant.MYHANDLER_CODE6:
                    wrReferences.get().jumpFunction(0);
                    break;
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(wrReferences.get(), "支付成功", Toast.LENGTH_SHORT).show();
                        this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1000);
                        //支付宝调用成功
                        wrReferences.get().postSuccess();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(wrReferences.get(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                            this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1000);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(wrReferences.get(), "订单尚未被支付", Toast.LENGTH_SHORT).show();
                            this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE6, 1000);
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    if (BuildConfig.LEO_DEBUG)
                        L.e("8000");
                    Toast.makeText(wrReferences.get(), "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    }


    public void jumpFunction(int core) {
        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_REFRESH_ALL));
        Intent intent_zer = new Intent(ConfirmOrderActivity.this, OrderSuccessActivity.class);
        Bundle bundle2 = new Bundle();
        intent_zer.putExtra("price", fee);
        intent_zer.putExtra("obj", op);
        intent_zer.putExtra("type", core);
        if (cbActs != null) {
            if (cbActs.getType_status().equals("TYPE_PRICE")) {
                bundle2.putDouble("discount", discount);
            } else {
                bundle2.putDouble("discount", discount);
            }
        } else {
            bundle2.putDouble("discount", 0);
        }
        bundle2.putString("olids", myOlids);
        intent_zer.putExtras(bundle2);
        startActivity(intent_zer);
        pDialog.dismiss();
        finish();
    }


    @Override
    protected String initToolbarTitle() {
        initData();
        if (whenIndexint == 0) {
            return "今日订单";
        } else if (whenIndexint == 1) {
            return "明日订单";
        } else {
            return "明日订单";
        }
    }

    private void initData() {
        AppManager.getAppManager().addActivity(this);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        canTijiao = true;

        Intent intent = getIntent();
        whenIndexint = intent.getIntExtra("whenindex", -1);
        chefids = intent.getStringExtra("chefids");

        order_date_tv.setText(MainTools.getTimeStr(whenIndexint));
        priceIndex = 0;
        EventBus.getDefault().register(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        unitAdapter = new UnitPriceAdapter(inflater);
        LinearLayoutManager managerL = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        price_cyc.setLayoutManager(managerL);
        price_cyc.setItemAnimator(new DefaultItemAnimator());
        price_cyc.setAdapter(unitAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
        btnListener();
        initLocation();
    }

    /**
     * 初始化高德定位
     */

    private void initLocation() {
        editor = baseSpf.edit();
        editor.apply();
        // 初始化定位，
        mlocationClient = new AMapLocationClient(getApplicationContext());
        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        // 设置定位模式为低功耗定位
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位回调监听
        mlocationClient.setLocationListener(this);
        // 设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
        if (isFirst)
            showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中", false);
    }

    private void btnListener() {
        refresh_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlocationClient != null) {
                    mlocationClient.startLocation();
                    if (isFirst)
                        showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中", false);
                }
            }
        });
        dhscroll.setOnTouchListener(new View.OnTouchListener() {
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
                            swipe.setEnabled(false);
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        type_gp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ali_rb:
                        SharedPreferenceUtil.initSharedPerence().putPayType(editor, 0);
                        break;
                    case R.id.wechat_rb:
                        SharedPreferenceUtil.initSharedPerence().putPayType(editor, 1);
                        break;
                    default:
                        break;
                }
                editor.commit();
            }
        });
        swipe.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mlocationClient != null) {
                    mlocationClient.startLocation();
                } else {
                    swipe.setRefreshing(false);
                }
            }
        });
//        add_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addPrice();
//            }
//        });
//        delete_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePeice();
//            }
//        });
        add_iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPeople();
            }
        });
        delete_iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePeople();
            }
        });
        addr_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                addAddr();
            }
        });
        top_info_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmOrderActivity.this, AddressChooseActivity.class);
                startActivity(intent);
            }
        });
        time_pop_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePop != null) {
                    timePop.showPopupWindow(dot_view);
                }
            }
        });
        attr_rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbChef != null) {
                    String tagStr = cbChef.getTags();
                    if (!TextUtils.isEmpty(tagStr)) {
                        final String[] tagZon = tagStr.split(",");
                        if (tagZon.length > 1) {
                            finalPopWindow = new TagPopWindow(ConfirmOrderActivity.this, tagZon, new TagPopWindow.MorelPopListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    tag_tv.setText(tagZon[position]);
                                    order_tags_tv.setText(tag_tv.getText().toString());
                                    finalPopWindow.dismiss();
                                }
                            });
                            finalPopWindow.showPopupWindow(main_rl);
                        }
                    }
                }
            }
        });
        remark_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                order_remark_tv.setText(remark_et.getText().toString());
            }
        });
        more_discount_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                String disountStr = discount_tv.getText().toString();
                if (!TextUtils.isEmpty(disountStr) && !disountStr.equals("无") && !disountStr.equals("不可使用")
                        && !disountStr.equals("点击重新获取") && !disountStr.equals("正在获取")) {
                    int curEatnum = Tools.toInteger(person_tv.getText().toString());
                    double curPrice = Tools.toDouble(unitPrice);
                    double dhAll = Arith.mul(curEatnum, curPrice);
                    Intent intent = new Intent(ConfirmOrderActivity.this, DhDiscountValActivity.class);
                    intent.putExtra("price", dhAll + "");
                    startActivity(intent);
                }
            }
        });
        act_type_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (actPop != null) {
                    actPop.showPopupWindow(main_rl);
                }
            }
        });
        confirm_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                confirm_Order();
            }
        });
    }

    /**
     * 确认用餐
     */
    private void confirm_Order() {
        if (cbChef == null || cbBook == null) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "请刷新界面");
            return;
        }
        if (!canTijiao) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "请勿重复提交");
            return;
        }
        if (time_pop_choose_tv.getText().toString().equals("请选择用餐时间")) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "未选择用餐时间");
            if (timePop != null) {
                timePop.showPopupWindow(dot_view);
            }
            return;
        }
        if (!hasAddr || info == null) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "请先选择地址");
            return;
        }
        if (TextUtils.isEmpty(address_tv.getText().toString())) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "请先选择地址");
            return;
        }
        if (cbBook != null && whenIndexint == 0 && (Tools.toInteger(person_tv.getText().toString()) > cbChef.getToday_num())) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "今天只剩" + cbChef.getToday_num() + "份啦");
            return;
        }
        if (cbBook != null && whenIndexint == 1 && (Tools.toInteger(person_tv.getText().toString()) > cbChef.getTomorrow_num())) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "明天只剩" + cbChef.getTomorrow_num() + "份啦");
            return;
        }
        if (priceAll < Tools.toDouble(cbBook.getMinspending())) {
            MainTools.ShowToast(ConfirmOrderActivity.this, cbBook.getMinspending() + "元起送");
            return;
        }
        if (!ali_rb.isChecked() && !wechat_rb.isChecked()) {
            MainTools.ShowToast(ConfirmOrderActivity.this, "请先选择支付方式");
            return;
        }
        order = new OrderListV11();
        order.setCbids(cbBook.getCbids());
        order.setEatennum(Tools.toInteger(person_tv.getText().toString()));
        order.setName(cbBook.getName());
        order.setPrice(unitPrice);
        order.setRemark(remark_et.getText().toString());
        order.setTag(tag_tv.getText().toString());
        List<OrderListV11> orderListx = new ArrayList<>();
        orderListx.add(order);
        op = new OrderParamV11();
        String locationInfo;
        if (!TextUtils.isEmpty(geoLat) && !TextUtils.isEmpty(geoLng)) {
            locationInfo = geoLng + "," + geoLat;
        } else {
            locationInfo = "";
        }
        op.setCoordinate(locationInfo);
        op.setDineraddr(address_tv.getText().toString());
        op.setDinermobile(mobile_tv.getText().toString());
        op.setDinername(nickname_tv.getText().toString());
        if (order_time_tv.getText().equals("午餐")) {
            op.setDinnertime("");
            op.setLunchtime(order_time_tv2.getText().toString());
        } else {
            op.setDinnertime(order_time_tv2.getText().toString());
            op.setLunchtime("");
        }
        op.setDistance(((int) addressDistance) + "");
        op.setEatenway("配送");
        op.setDaids(info.getDaids());
        op.setChefids(chefids);
        if (cbActs != null) {
            op.setActids(cbActs.getActids());
        }
        //这里要处理优惠券id啦
        if (!TextUtils.isEmpty(discount_tv.getText().toString()) && discount_tv.getText().toString().contains("-")) {
            L.e("diaoyong===" + discountId + "ssss" + !TextUtils.isEmpty(discount_tv.getText().toString()) + "***" + discount_tv.getText().toString().contains("-"));
            op.setCuids(discountId);
        }
        op.setList(orderListx);
        if (TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getRegisterId(baseSpf))) {
            if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(ConfirmOrderActivity.this) + "")) {
                op.setImei(JPushInterface.getRegistrationID(ConfirmOrderActivity.this) + "");
            } else {
//                MainTools.ShowToast(ConfirmOrderActivity.this, "获取不到注册id，无法提交订单");
//                return;
                op.setImei("");
            }
        } else {
            op.setImei(SharedPreferenceUtil.initSharedPerence().getRegisterId(baseSpf));
        }
        if (!TextUtils.isEmpty(MyConstant.user.getUserids())) {
            op.setMids(MyConstant.user.getUserids());
        } else {
            op.setMids(SharedPreferenceUtil.initSharedPerence().getUserId(baseSpf));
        }
        if (!TextUtils.isEmpty(MyConstant.user.getToken())) {
            op.setToken(MyConstant.user.getToken());
        } else {
            op.setToken(SharedPreferenceUtil.initSharedPerence().getToken(baseSpf));
        }
        op.setPlatform(Build.MODEL.replace(" ", ""));
        op.setTotal(priceAll + "");
        op.setWhenindex(whenIndexint + "");
        String popPriceStr=order_price_tv.getText().toString();
        String popNumStr=order_num_tv.getText().toString();
        String popAllStr=all_remark_tv.getText().toString();
        String popDiscountStr=activity_tv.getText().toString();
        String popPayStr=priceall_tv.getText().toString();
        boolean isAli=ali_rb.isChecked();
        payPopWindow = new ConfirmPayPopWindow(this,popPriceStr,popNumStr,popAllStr,popDiscountStr,popPayStr,isAli,new ConfirmPayPopWindow.PayClickListener() {
            @Override
            public void OnJJClick(View view) {
                String json = gson.toJson(op);
                if (ali_rb.isChecked()) {
                    postOrder(json, true);
                } else {
                    if (new Wechat(ConfirmOrderActivity.this).isClientValid()) {
                        postOrder2(json, true);
                    } else {
                        MainTools.ShowToast(ConfirmOrderActivity.this, R.string.wechat_client_inavailable);
                    }
                }
            }
        });
        if (!payPopWindow.isShowing()) {
            payPopWindow.showPopupWindow(bottom_achor_view);
        }
    }

    /**
     * 获取菜单信息
     */
    private void getCB() {
        params = ParamData.getInstance().getCookBookListObj(this, chefids, "", whenIndexint + "");
        client.post(MyConstant.URL_GETCUISINE, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(ConfirmOrderActivity.this, R.string.interneterror);
                if (isFirst) {
                    preview_rl.setVisibility(View.VISIBLE);
                    detail_tvs.setVisibility(View.VISIBLE);
                    back_bg.setVisibility(View.VISIBLE);
                    refresh_tv.setVisibility(View.VISIBLE);
                    detail_tvs.setText("网络不给力");
                    back_bg.setBackgroundResource(R.mipmap.internet_failed);
                }
            }

            @Override
            public void onFinish() {
                dismissAlertDialog();
                swipe.setRefreshing(false);
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                L.e("responseString====" + responseString);
                CBData data = new CBData();
                if (data.dealData(responseString, ConfirmOrderActivity.this, gson) == 1) {
                    isFirst = false;
                    preview_rl.setVisibility(View.GONE);
                    person_tv.setText("1");
                    order_num_tv.setText("1份");
                    exp_rb.setChecked(true);
                    if (SharedPreferenceUtil.initSharedPerence().getPayType(baseSpf) == 0) {
                        ali_rb.setChecked(true);
                    } else if (SharedPreferenceUtil.initSharedPerence().getPayType(baseSpf) == 1) {
                        wechat_rb.setChecked(true);
                    }
                    cbAll = data.getCbAll();
                    if (cbAll != null && cbAll.getMap() != null && cbAll.getMap().getCookbooks() != null && cbAll.getMap().getCookbooks().size() > 0) {
                        priceIndex = 0;
                        cbBook = cbAll.getMap().getCookbooks().get(0);
//                        String pricesStr = cbAll.getMap().getCookbook().get(0).getPrices();
//                        if (!TextUtils.isEmpty(pricesStr)) {
//                            pricesZon = pricesStr.split(",");
//                        }
                        unitList = cbAll.getMap().getCookbooks();
                        if (unitList != null && unitList.size() > 0) {
                            unitList.get(0).setSelected(true);
                            maxPeople = unitList.get(0).getMaxnum();
                            int trueSize = unitList.size();
                            for (int i = 0; i < unitList.size(); i++) {
                                if (!unitList.get(i).isopen()) {
                                    trueSize--;
                                }
                            }
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.width_80_80), trueSize
                                    * getResources().getDimensionPixelOffset(R.dimen.dh_48));
                            price_cyc.setLayoutParams(params);
                            unitAdapter.setActs(unitList);
                            unitAdapter.notifyDataSetChanged();
                            unitPrice = MainTools.getDoubleValue(Tools.toDouble(unitList.get(0).getPrices()), 1);
                            order_price_tv.setText(unitPrice + "元");
                            changePrice();
                            unitAdapter.setOnItemClickListener(new UnitPriceItemListener() {
                                @Override
                                public void onItemClick(View view, int postion) {
                                    for (CBCookBook priceObj : unitList) {
                                        priceObj.setSelected(false);
                                    }
                                    unitList.get(postion).setSelected(true);
                                    cbBook = unitList.get(postion);
                                    unitAdapter.notifyDataSetChanged();
                                    unitPrice = MainTools.getDoubleValue(Tools.toDouble(unitList.get(postion).getPrices()), 1);
                                    order_price_tv.setText(unitPrice + "元");
                                    String personStr = person_tv.getText().toString();
                                    int curPe = Tools.toInteger(personStr);
                                    maxPeople = unitList.get(postion).getMaxnum();
                                    if (curPe > maxPeople) {
                                        curPe = maxPeople;
                                        person_tv.setText(curPe + "");
                                        order_num_tv.setText(person_tv.getText().toString() + "份");
                                    }
                                    changePrice();
                                }
                            });
                        }
                        String tagStr = cbAll.getMap().getChef().getTags();
                        if (!TextUtils.isEmpty(tagStr)) {
                            String[] tagZon = tagStr.split(",");
                            if (tagZon.length > 0) {
                                tag_tv.setText(tagZon[0]);
                                order_tags_tv.setText(tag_tv.getText().toString());
                                if (tagZon.length > 1) {
                                    tag_iv.setVisibility(View.VISIBLE);
                                } else {
                                    tag_iv.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    if (cbAll != null && cbAll.getMap() != null && cbAll.getMap().getChef() != null) {
                        if (cbAll.getMap().getChef() != null)
                            cbChef = cbAll.getMap().getChef();
                        String today_remain_str = cbChef.getToday_num() + "份";
                        String tomorrow_remain_str = cbChef.getTomorrow_num() + "份";
                        today_relay_tv.setText(today_remain_str);
                        tomorrow_relay_tv.setText(tomorrow_remain_str);
                        if (cbAll.getMap().getEatentimeMap() != null)
                            initTimePop(cbAll.getMap().getEatentimeMap());
                        int tablewarefee = cbAll.getMap().getChef().getTablewarefee();
                        order_nickname_tv.setText(cbChef.getNickname());
                        if (tablewarefee != 0) {
                            tableware_remark_tv.setText(tablewarefee + "");
                            tableware_tv.setText(tablewarefee + "");
                            tableware_tv.setVisibility(View.VISIBLE);
                            tableware_other_tv.setVisibility(View.VISIBLE);
                            tableware_tip.setVisibility(View.GONE);
                        } else {
                            tableware_remark_tv.setText("免费提供");
                            tableware_tv.setText("0");
                            tableware_tv.setVisibility(View.GONE);
                            tableware_other_tv.setVisibility(View.GONE);
                            tableware_tip.setVisibility(View.VISIBLE);
                        }
                    }
                    if (cbAll != null && cbAll.getMap() != null && cbAll.getMap().getActs() != null) {
                        if (cbAll.getMap().getActs().size() > 0) {
                            cbActs = cbAll.getMap().getActs().get(0);
                        } else {
                            cbActs = null;
                        }
                        if (cbActs != null) {
                            act_type_tv.setText(cbActs.getRemark());
                            act_type_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.text_gold));
                            act_type_iv.setVisibility(View.VISIBLE);
                            PreviewLoader.loadImage(cbActs.getIconurl(), act_type_iv);
                            if (cbAll.getMap().getActs().size() >= 1) {
                                red_arrow2.setVisibility(View.VISIBLE);
                                actPop = new ActPopWindow(ConfirmOrderActivity.this, cbAll.getMap().getActs(), new ActPopWindow.MorelPopListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if (position == cbAll.getMap().getActs().size() - 1) {
                                            act_type_iv.setVisibility(View.GONE);
                                            act_type_tv.setVisibility(View.GONE);
                                            act_type_tv2.setVisibility(View.VISIBLE);
                                            act_type_tv2.setText("不享受优惠");
                                            cbActs = null;
                                            initCoupon(cbAll);
                                        } else {
                                            act_type_iv.setVisibility(View.VISIBLE);
                                            CBActs actTemp = cbAll.getMap().getActs().get(position);
                                            act_type_tv.setText(actTemp.getRemark());
                                            act_type_tv.setVisibility(View.VISIBLE);
                                            act_type_tv2.setVisibility(View.GONE);
                                            PreviewLoader.loadImage(actTemp.getIconurl(), act_type_iv);
                                            cbActs = actTemp;
                                            initCoupon(cbAll);
                                        }
//                                        changePrice();
                                        actPop.dismiss();
                                    }
                                });
                            } else {
                                changePrice();
                                red_arrow2.setVisibility(View.GONE);
                            }
                            changePrice();
                        } else {
                            act_type_tv.setVisibility(View.GONE);
                            act_type_tv2.setVisibility(View.VISIBLE);
                            act_type_tv2.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
                            act_type_tv2.setText("无可选优惠");
                            act_type_iv.setVisibility(View.GONE);
                            red_arrow2.setVisibility(View.GONE);
                            changePrice();
                        }
                    }
                    getDefaultAddress();
                    initCoupon(cbAll);
                } else if (data.dealData(responseString, ConfirmOrderActivity.this, gson) == -1) {
                    back_bg.setVisibility(View.VISIBLE);
                    cbAll = data.getCbAll();
                    if (!TextUtils.isEmpty(cbAll.getTag())) {
                        showTipDialog(ConfirmOrderActivity.this, cbAll.getTag(), cbAll.getResultcode());
                    } else {
                        showTipDialog(ConfirmOrderActivity.this, "重新登录", cbAll.getResultcode());
                    }
                } else {
                    cbAll = data.getCbAll();
                    if (cbAll != null && !TextUtils.isEmpty(cbAll.getTag()))
                        MainTools.ShowToast(ConfirmOrderActivity.this, cbAll.getTag());
                }
            }
        });
    }

    /**
     * 更改优惠券状态
     */
    private void initCoupon(CBAll cbAll) {
        boolean canUse = (cbActs != null && cbActs.isusecoupon()) || cbActs == null;
        if (cbAll.getMap() != null) {
            if (cbAll.getMap().getCouponqty() == 0) {
                if (canUse) {
                    discount_tv.setText("无");
                    red_arrow.setVisibility(View.GONE);
                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
                } else {
                    discount_tv.setText("不可使用");
                    red_arrow.setVisibility(View.GONE);
                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
                }
            } else if (cbAll.getMap().getCouponqty() > 0) {
                if (canUse) {
                    discount_tv.setText(cbAll.getMap().getCouponqty() + "张");
                    red_arrow.setVisibility(View.VISIBLE);
                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.text_gold));
                } else {
                    discount_tv.setText("不可使用");
                    red_arrow.setVisibility(View.GONE);
                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
                }
            }
        } else {
            discount_tv.setText("无");
            red_arrow.setVisibility(View.GONE);
            discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
        }
        changePrice();
    }

    /**
     * 初始化popwindow
     *
     * @param timeMap Time
     */
    private void initTimePop(CBeatentimeMap timeMap) {
        if (cbBook != null)
            timePop = new TimePopWindow2(this, time_pop_choose_tv, order_time_tv, order_time_tv2, timeMap.getLunchtimes(), timeMap.getDinnertimes());
    }

//    /**
//     * 根据价格获得优惠券
//     */
//    private void  getCoupon() {
//        client.post(MyConstant.GET_DISCOUNT_ENABLE, params, new TextHttpResponseHandler() {
//
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                L.e("discount====" + responseString + "sbsbsb" + dhAll);
//                DiscountData data = new DiscountData();
//                DiscountVal discountVal;
//                if (data.dealValData(responseString, ConfirmOrderActivity.this, gson) == 1) {
//                    discountVal = data.getDiscountVal();
//                    if (discountVal != null) {
//                        if (discountVal.getList() != null && discountVal.getList().size() > 0) {
//                            detailList = discountVal.getList();
//                            boolean canUse = (cbActs != null && cbActs.isusecoupon()) || cbActs == null;
//                            if (detailList != null && detailList.size() > 0) {
//                                if (canUse) {
//                                    discount_tv.setText(detailList.size() + "张");
//                                    red_arrow.setVisibility(View.VISIBLE);
//                                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.text_gold));
//                                } else {
//                                    discount_tv.setText("不可使用");
//                                    red_arrow.setVisibility(View.GONE);
//                                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
//                                }
//                            } else {
//                                if (canUse) {
//                                    discount_tv.setText("无");
//                                    red_arrow.setVisibility(View.GONE);
//                                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
//                                } else {
//                                    discount_tv.setText("不可使用");
//                                    red_arrow.setVisibility(View.GONE);
//                                    discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
//                                }
//                            }
//                        } else {
//                            discount_tv.setText("无");
//                            red_arrow.setVisibility(View.GONE);
//                            discount_tv.setTextColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.gray));
//                        }
//                    }
//                }
//            }
//        });
//    }

    /**
     * 获取地址
     */
    private void getDefaultAddress() {
        params = ParamData.getInstance().getAddressObj();
        client.setTimeout(5000);
        client.post(MyConstant.URL_GETMYADDRESS, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                AddressData data = new AddressData();
                if (data.getstatus(responseString, ConfirmOrderActivity.this, gson) == 1) {
                    info = null;
                    AddressAll addressall = data.getObj();
                    SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(ConfirmOrderActivity.this, MyConstant.APP_SPF_NAME);
                    String chooseAddStr = SharedPreferenceUtil.initSharedPerence().getPoiName(spf);
                    if (addressall != null && addressall.getList() != null) {
                        List<AddressInfo> listAdd;
                        listAdd = addressall.getList();
                        if (listAdd != null) {
                            addressNum = listAdd.size();
                        }
                        if (listAdd != null && listAdd.size() > 0) {
                            for (int i = 0; i < listAdd.size(); i++) {
                                if (Tools.toString(listAdd.get(i).getLoc_detail()).equals(chooseAddStr) || listAdd.get(i).getLoc_simple().equals(chooseAddStr)) {
                                    info = listAdd.get(i);
                                    break;
                                }
                            }
                        }
                        if (info == null) {
                            if (listAdd != null && listAdd.size() > 0)
                                for (int i = 0; i < listAdd.size(); i++) {
                                    if (listAdd.get(i).isdefault()) {
                                        info = listAdd.get(i);
                                        break;
                                    }
                                }
                            if (info == null) {
                                if (listAdd != null && listAdd.size() > 0) {
                                    info = listAdd.get(0);
                                    addrFunction(info);
                                } else {
                                    showTips(true, "请添加地址");
                                }
                            } else {
                                addrFunction(info);
                            }
                        } else {
                            addrFunction(info);
                        }
                    } else {
                        showTips(true, "请添加地址");
                    }
                } else {
                    showTips(true, "添加地址");
                    if (data.getDefaultObj() != null && !TextUtils.isEmpty(data.getDefaultObj().getTag())) {
                        showTipDialog(ConfirmOrderActivity.this, data.getDefaultObj().getTag(), data.getDefaultObj().getResultcode());
                    } else {
                        showTipDialog(ConfirmOrderActivity.this, "重新登录", data.getDefaultObj().getResultcode());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    /**
     * 增加份数
     */
    private void addPeople() {
        if (cbBook == null || cbChef == null) return;
        String personStr = person_tv.getText().toString();
        int curPe = Tools.toInteger(personStr);
        if (curPe < maxPeople) {
            if (curPe < cbChef.getTomorrow_num()) {
                curPe++;
                person_tv.setText(curPe + "");
                order_num_tv.setText(person_tv.getText().toString() + "份");
            } else {
                String remainStr = whenIndexint == 0 ? "今日还剩" + cbChef.getToday_num() : "明日还剩" + cbChef.getTomorrow_num();
                MainTools.ShowToast(ConfirmOrderActivity.this, remainStr + "份");
            }
            changePrice();
        } else {
            MainTools.ShowToast(ConfirmOrderActivity.this, "最大份数" + maxPeople);
        }
    }

    /**
     * 减少份数
     */
    private void deletePeople() {
        String personStr = person_tv.getText().toString();
        int curPe = Tools.toInteger(personStr);
        if (curPe > 1) {
            curPe--;
        } else {
            return;
        }
        person_tv.setText(curPe + "");
        order_num_tv.setText(person_tv.getText().toString() + "份");
        changePrice();
    }

    /**
     * 根据优惠调整总价
     */
    private void changePrice() {
        int curEatnum = Tools.toInteger(person_tv.getText().toString());
        double curPrice = Tools.toDouble(unitPrice);
        double priceTemp = Arith.mul(curEatnum, curPrice);
        if (cbActs != null) {
            discount_other_tv.setVisibility(View.VISIBLE);//￥显示
            activity_tv.setVisibility(View.VISIBLE);//优惠价格显示
            acitivity_tip.setVisibility(View.GONE);
            String[] limitPriceStr = new String[0];
            String[] discountsStr = new String[0];
            String limitPrices = cbActs.getLimitprices();
            if (!TextUtils.isEmpty(limitPrices)) {
                limitPriceStr = limitPrices.split(",");
            }
            String discounts = cbActs.getDiscounts();
            if (!TextUtils.isEmpty(discounts)) {
                discountsStr = discounts.split(",");
            }
            int eatenNum = cbActs.getEatennum();
            String actType = cbActs.getType_status();
//      <!-------------------价格减免--------------------->
            if (actType.equals(MyConstant.PRICE_TYPE)) {
                //如果是单价减
                if (priceTemp != 0) {
                    int priceDot = -1;
                    if (limitPriceStr.length > 0) {
                        for (int i = 0; i < limitPriceStr.length; i++) {
                            double dhPrice = Tools.toDouble(limitPriceStr[i]);
                            if (curPrice == dhPrice) {
                                priceDot = i;
                                break;
                            }
                        }
                    }
                    if (priceDot != -1 && discountsStr.length > 0) {
                        double priceDiscount = Tools.toDouble(discountsStr[priceDot]);
                        if (curEatnum >= eatenNum) {
                            priceDiscount = Arith.mul(priceDiscount, eatenNum);
                        } else {
                            priceDiscount = Arith.mul(priceDiscount, curEatnum);
                        }
                        priceAll = Arith.sub(priceTemp, priceDiscount);
                        activity_tv.setText("-" + MainTools.getDoubleValue(priceDiscount, 1) + "");
                        discount = priceDiscount;
                    } else {
                        priceAll = Arith.sub(priceTemp, 0);
                        activity_tv.setText("" + 0 + "");
                        discount = 0;
                    }
                }
            } else if (actType.equals(MyConstant.TOTAL_TYPE)) {
                //如果是满减活动
                if (priceTemp != 0) {
                    int totalDot = -1;
                    if (limitPriceStr.length > 0) {
                        for (int i = 0; i < limitPriceStr.length; i++) {
                            double dhPrice = Tools.toDouble(limitPriceStr[i]);
                            if (dhPrice > priceTemp) {
                                totalDot = i - 1;
                                break;
                            }
                            if (priceTemp >= Tools.toDouble(limitPriceStr[limitPriceStr.length - 1])) {
                                totalDot = limitPriceStr.length - 1;
                            }
                        }
                    }
                    if (totalDot != -1 && discountsStr.length > 0) {
                        double totalDiscount = Tools.toDouble(discountsStr[totalDot]);
                        priceAll = Arith.sub(priceTemp, totalDiscount);
                        activity_tv.setText("-" + MainTools.getDoubleValue(totalDiscount, 1) + "");
                        discount = totalDiscount;
                    } else {
                        priceAll = Arith.sub(priceTemp, 0);
                        activity_tv.setText("" + 0 + "");
                        discount = 0;
                    }
                }
            }
        } else {
            //在没有活动的情况下
            discount_other_tv.setVisibility(View.GONE);//￥不显示
            activity_tv.setVisibility(View.GONE);//优惠价格不显示
            acitivity_tip.setVisibility(View.VISIBLE);
            priceAll = priceTemp;
        }
        boolean canUse = (cbActs != null && cbActs.isusecoupon()) || cbActs == null;
        if (discountPrice > 0 && canUse) {
            if (priceTemp < discountLimit) {//当前的优惠券已经无法满足了
                if (cbAll != null && cbAll.getMap() != null) {
                    discount_tv.setText(cbAll.getMap().getCouponqty() + "张");
                } else {
                    discount_tv.setText("点击查看");
                }
            } else {//当前的优惠券又可以重新满足了
                discount_tv.setText("-" + discountPrice + "￥");
            }
        }
        //此时的priceall已经减去了活动优惠
        //      <!-------------------优惠券减免--------------------->
        if (!discount_tv.getText().toString().contains("-")) { //不减免优惠券
            double transD = Tools.toDouble(trans_number_tv.getText().toString());
            double tableD = Tools.toDouble(tableware_tv.getText().toString());
            double priceAdd = Arith.add(transD, tableD);
            priceAll = Arith.add(priceAll, priceAdd);
            discount = Arith.sub(discount, priceAdd);
        } else { //减免优惠券
            double transD = Tools.toDouble(trans_number_tv.getText().toString());
            double tableD = Tools.toDouble(tableware_tv.getText().toString());
            double priceAdd = Arith.add(transD, tableD);
            priceAll = Arith.add(Arith.sub(priceAll, discountPrice), priceAdd);
            discount = Arith.sub(Arith.add(discount, discountPrice), priceAdd);
        }
        //      <!-------------------总价格显示--------------------->
        priceall_tv.setText("￥" + MainTools.getDoubleValue(priceAll, 1));
        all_remark_tv.setText(MainTools.getDoubleValue(priceAll, 1) + "元");
    }

    /**
     * 地址栏显示控制
     *
     * @param showState   地址是否显示
     * @param contenttips 提示文字
     */
    private void showTips(boolean showState, String contenttips) {
        if (showState) {
            addr_tv.setVisibility(View.VISIBLE);
            top_info_rl.setVisibility(View.GONE);
            bottom_tip_tv.setVisibility(View.GONE);
        } else {
            addr_tv.setVisibility(View.GONE);
            top_info_rl.setVisibility(View.VISIBLE);
        }
        addr_tv.setText(contenttips);
        hasAddr = !showState;
    }

    /**
     * 地址信息
     *
     * @param info AddressInfo
     */
    private void addrFunction(AddressInfo info) {
        if (info == null)
            return;
        nickname_tv.setText(info.getName());
        mobile_tv.setText(info.getMobile());
        name_remark_tv.setText(info.getName());
        phone_remark_tv.setText(info.getMobile());
        if (!TextUtils.isEmpty(info.getLoc_detail())) {
            if (!TextUtils.isEmpty(info.getAddrdetail())) {
                if (!TextUtils.isEmpty(info.getLoc_simple())) {
                    address_tv.setText(info.getLoc_simple() + info.getAddrdetail());
                    addr_remark_tv.setText(info.getLoc_simple() + info.getAddrdetail());
                    showTips(false, "");
                    if (TextUtils.isEmpty(info.getLatitude()) || TextUtils.isEmpty(info.getLongitude()) || info.getLatitude().equals("0") || info.getLongitude().equals("0")) {
                        L.e("dy4");
                        showTips(true, "请完善您的地址");
                    } else {
                        LatLng startLat = new LatLng(cbChef.getLatitude(), cbChef.getLongitude());
                        LatLng endLat = new LatLng(Tools.toFloat(info.getLatitude()), Tools.toFloat(info.getLongitude()));
                        addressDistance = AMapUtils.calculateLineDistance(startLat, endLat);
                        L.e("addressDistance===" + addressDistance);
                        if (cbChef.getLimitdistance() == 0 || (addressDistance > cbChef.getLimitdistance())) {
                            bottom_tip_tv.setVisibility(View.VISIBLE);
                            bottom_tip_tv.setText("地址超出距离" + Tools.getDistanc((int) addressDistance - cbChef.getLimitdistance()) + "不能点餐");
                            if (cbChef.getDeliveryfee() > 0 && (addressDistance > cbChef.getDeliverydistance())) {
                                trans_number_tv.setText(cbChef.getDeliveryfee() + "");
                                trans_number_remark_tv.setText(cbChef.getDeliveryfee() + "￥");
                                deliFee = cbChef.getDeliveryfee();
                                trans_number_tv.setVisibility(View.VISIBLE);
                                trans_other_tv.setVisibility(View.VISIBLE);
                                tans_tip.setVisibility(View.GONE);
                            } else {
                                trans_number_remark_tv.setText("免费配送");
                                trans_number_tv.setText("0");
                                trans_number_tv.setVisibility(View.GONE);
                                trans_other_tv.setVisibility(View.GONE);
                                tans_tip.setVisibility(View.VISIBLE);
                                deliFee = 0;
                            }
                        } else {
                            if (cbChef.getDeliveryfee() > 0 && (addressDistance > cbChef.getDeliverydistance())) {
                                trans_number_tv.setText(cbChef.getDeliveryfee() + "");
                                trans_number_remark_tv.setText(cbChef.getDeliveryfee() + "￥");
                                trans_number_tv.setVisibility(View.VISIBLE);
                                trans_other_tv.setVisibility(View.VISIBLE);
                                tans_tip.setVisibility(View.GONE);
                                deliFee = cbChef.getDeliveryfee();
                            } else {
                                trans_number_remark_tv.setText("免费配送");
                                trans_number_tv.setText("0");
                                trans_number_tv.setVisibility(View.GONE);
                                trans_other_tv.setVisibility(View.GONE);
                                tans_tip.setVisibility(View.VISIBLE);
                                deliFee = 0;
                            }
                            bottom_tip_tv.setVisibility(View.GONE);
                        }
                    }
                } else {
                    showTips(true, "请完善您的地址");
                }
            } else {
                showTips(true, "请完善您的地址");
            }
        } else {
            showTips(true, "请完善您的地址");
        }
        changePrice();
    }

    /**
     * 完善地址的跳转
     */
    private void addAddr() {
        if (addr_tv.getText().toString().equals("请选择地址")) {
            if (addressNum > 0) {
                Intent intent = new Intent(ConfirmOrderActivity.this, AddressChooseActivity.class);
                intent.putExtra("code", MyConstant.REQUESTCODEQ_CHOOSE);
                startActivityForResult(intent, MyConstant.REQUESTCODEQ_CHOOSE);
            }
        } else if (addr_tv.getText().toString().equals("请添加地址")) {
            if (addressNum == 0) {
                Intent intent = new Intent(ConfirmOrderActivity.this, AddressAddActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        } else if (addr_tv.getText().toString().equals("重新加载")) {
            getDefaultAddress();
        } else if (addr_tv.getText().toString().equals("请完善您的地址")) {
            Intent intent = new Intent(ConfirmOrderActivity.this, AddressEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("obj", info);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * 通知后台同步支付成功
     */
    private void postSuccess() {
        //myOlids
        //10
        //
        if (!TextUtils.isEmpty(myOlids)) {
            params = ParamData.getInstance().postStatusObj(myOlids, "10", 0l);
            client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                }
            });

        }

    }

    private void postOrder2(String json, final boolean isActivity) {

        params = ParamData.getInstance().desParam(json);
        client.post(MyConstant.URL_POSTORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                canTijiao = false;
                pDialog = new SweetAlertDialog(ConfirmOrderActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在使用微信支付");
                pDialog.setCancelable(false);
                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.blue_btn_bg_color));
                pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG)
                    L.e("responseStringB" + responseString);
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    if (request == 0) {
                        MainTools.ShowToast(ConfirmOrderActivity.this, jobj.getString("tag"));
                        if (pDialog != null)
                            pDialog.dismiss();
                    }
                    String objStr = jobj.getString("obj");
                    JSONObject jobj2 = new JSONObject(objStr);
                    fee = Tools.toDouble(jobj2.getString("total"));
                    String orderStr = jobj2.getString("number");
                    myOlids = jobj2.getString("olids");
                    if (request == 1) {
                        MainTools.ShowToast(ConfirmOrderActivity.this, jobj.getString("tag"));
                        Dh_WechatPay wepay = new Dh_WechatPay(ConfirmOrderActivity.this);
                        double hereDiscount;
                        if (isActivity) {
                            if (cbActs != null) {
                                if (cbActs.getType_status().equals("TYPE_PRICE")) {
                                    // intent_zero.putExtra("discount", discount);
                                    hereDiscount = discount; //单减*最低份数
                                } else {
                                    // intent_zero.putExtra("discount", dhDiscount);
                                    hereDiscount = discount; //单减
                                }
                            } else {
                                hereDiscount = 0;
                            }
                        } else {
                            hereDiscount = 0;//如果是根据服务器数据支付，不需要添加折扣
                        }
                        String detail_Str = "单价" + order.getPrice() + "元-" + "订餐人数" + order.getEatennum() + "人-" + "总价" + fee + "元-" + "优惠"
                                + hereDiscount + "元";
                        wepay.payFunction(cbChef.getNickname() + "-" + cbBook.getName() + "-" + order.getTag(), detail_Str, (int) (fee * 100) + "", orderStr);
                    } else if (request == 2) {
                        dialog = new AlertDialog.Builder(ConfirmOrderActivity.this).setCancelable(false).setTitle("支付结果").setMessage(jobj.getString("tag"))
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        op.setTotal(fee + "");
                                        String json = gson.toJson(op);
                                        postOrder2(json, false);
                                    }
                                }).create();
                        dialog.show();
                        if (pDialog != null)
                            pDialog.dismiss();
                    } else if (request == 3) {
                        discountLimit = 0;
                        discountId = "";
                        discountPrice = 0;
                        changePrice();
                        dialog = new AlertDialog.Builder(ConfirmOrderActivity.this).setCancelable(false).setTitle("支付结果").setMessage(jobj.getString("tag"))
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        discount_tv.setText("点击重新获取");
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        op.setTotal(fee + "");
                                        String json = gson.toJson(op);
                                        postOrder2(json, false);
                                    }
                                }).create();
                        dialog.show();
                        if (pDialog != null)
                            pDialog.dismiss();
                    } else {
                        MainTools.ShowToast(ConfirmOrderActivity.this, jobj.getString("tag"));
                        if (pDialog != null)
                            pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(ConfirmOrderActivity.this, R.string.interneterror);
                if (pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                canTijiao = true;
            }
        });

    }

    private void postOrder(String json, final boolean isActivity) {
        params = ParamData.getInstance().desParam(json);
        client.post(MyConstant.URL_POSTORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                canTijiao = false;
                pDialog = new SweetAlertDialog(ConfirmOrderActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在使用支付宝支付");
                pDialog.setCancelable(false);
                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor(ConfirmOrderActivity.this, R.color.blue_btn_bg_color));
                pDialog.setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG)
                    L.e("responseStringA" + responseString);
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    if (request == 0) {
                        MainTools.ShowToast(ConfirmOrderActivity.this, jobj.getString("tag"));
                        if (pDialog != null)
                            pDialog.dismiss();
                    }
                    String objStr = jobj.getString("obj");
                    JSONObject jobj2 = new JSONObject(objStr);
                    fee = Tools.toDouble(jobj2.getString("total"));
                    String orderStr = jobj2.getString("number");
                    myOlids = jobj2.getString("olids");
                    if (request == 1) {
                        MainTools.ShowToast(ConfirmOrderActivity.this, jobj.getString("tag"));
                        MyHandler handlerx = new MyHandler(ConfirmOrderActivity.this);
                        Dh_Alipay mypay = new Dh_Alipay(ConfirmOrderActivity.this, handlerx);
                        double hereDiscount;
                        if (isActivity) {
                            if (cbActs != null) {
                                if (cbActs.getType_status().equals("TYPE_PRICE")) {
                                    // intent_zero.putExtra("discount", discount);
                                    hereDiscount = discount;//单减*最低份数
                                } else {
                                    // intent_zero.putExtra("discount", dhDiscount);
                                    hereDiscount = discount;//单减
                                }
                            } else {
                                hereDiscount = 0;
                            }
                        } else {
                            hereDiscount = 0;
                        }
                        String detail_Str = "单价" + order.getPrice() + "元-" + "订餐人数" + order.getEatennum() + "人-" + "总价" + fee + "元-" + "优惠"
                                + hereDiscount + "元";
                        mypay.payFunction(cbChef.getNickname() + "-" + cbBook.getName() + "-" + order.getTag(), detail_Str, fee + "", orderStr);
                    } else if (request == 2) {
                        dialog = new AlertDialog.Builder(ConfirmOrderActivity.this).setCancelable(false).setTitle("支付结果").setMessage(jobj.getString("tag"))
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        op.setTotal(fee + "");
                                        String json = gson.toJson(op);
                                        postOrder(json, false);
                                    }
                                }).create();
                        dialog.show();
                        if (pDialog != null)
                            pDialog.dismiss();
                    } else if (request == 3) {//优惠券失效的情况
                        discountLimit = 0;
                        discountId = "";
                        discountPrice = 0;
                        changePrice();
                        dialog = new AlertDialog.Builder(ConfirmOrderActivity.this).setCancelable(false).setTitle("支付结果").setMessage(jobj.getString("tag"))
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        discount_tv.setText("点击重新获取");
                                    }
                                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        op.setTotal(fee + "");
                                        String json = gson.toJson(op);
                                        postOrder(json, false);
                                    }
                                }).create();
                        dialog.show();
                        if (pDialog != null)
                            pDialog.dismiss();
                    } else {
                        MainTools.ShowToast(ConfirmOrderActivity.this, jobj.getString("tag"));
                        if (pDialog != null)
                            pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(ConfirmOrderActivity.this, R.string.interneterror);
                if (pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                canTijiao = true;
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
        dismissAlertDialog();
    }
}
