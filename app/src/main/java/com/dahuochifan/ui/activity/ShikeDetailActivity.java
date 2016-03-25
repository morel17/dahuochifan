package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.custom.ShikeInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class ShikeDetailActivity extends BaseActivity {
    private ShikeInfo shikeinfo;
    private TextView chef_nickname_tv, price_tv, number_tv, name_tv, phone_number_tv, addressinfo_tv;
    private TextView tag_tv, level_tv;
    private TextView type_tv, status_card, liuyan_et;
    private ImageView chef_call_iv;
    private CircleImageView chef_avatar_iv;
    private TextView time_mid;
    private RippleView left_tv, right_tv;
    private ImageView center_line;
    private Gson gson;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private int position;
    private TextView mid_tv, night_tv;
    private TextView all_price_tv;
    private TextView address_tv;
    private RelativeLayout bottom_rl;
    private TextView order_date;
    private TextView order_no_tv;
    private int index;
    private SweetAlertDialog pd;
    private TextView day_tv;
    private TextView mid_tag_tv;
    private TextView mid_time_tv;
    private RelativeLayout location_rl;
    private CardView cardView_location;
    private TextView distance_tv;
    private float addressDistance;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pd.dismiss();
                    break;
                case 1:
                    pd.dismiss();
                    finish();
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
        AppManager.getAppManager().addActivity(this);
        initData();
        initViews();
        initStatus();
        btn_listner();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        shikeinfo = (ShikeInfo) bundle.getSerializable("obj");
        gson = new Gson();
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        // params.prepare(this, params);
        position = bundle.getInt("position");
        index = bundle.getInt("index");
        if (shikeinfo != null) {
            if (shikeinfo.getChlat() != 0 && shikeinfo.getChlng() != 0 && shikeinfo.getLatitude() != 0 && shikeinfo.getLongitude() != 0) {
                LatLng startLat = new LatLng(shikeinfo.getLatitude(), shikeinfo.getLongitude());
                LatLng endLat = new LatLng(shikeinfo.getChlat(), shikeinfo.getChlng());
                addressDistance = AMapUtils.calculateLineDistance(startLat, endLat);
            } else {
                addressDistance = 0;
            }
        } else {
            addressDistance = 0;
        }
    }

    private void initViews() {
        chef_call_iv = (ImageView) findViewById(R.id.cook_call_img);
        chef_nickname_tv = (TextView) findViewById(R.id.chef_nickname_tv);
        tag_tv = (TextView) findViewById(R.id.tag_tv);
        level_tv = (TextView) findViewById(R.id.level_tv);
        level_tv.setText(shikeinfo.getCbname());

        price_tv = (TextView) findViewById(R.id.price_tv);
        number_tv = (TextView) findViewById(R.id.person_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);

        phone_number_tv = (TextView) findViewById(R.id.phone_number_tv);
        addressinfo_tv = (TextView) findViewById(R.id.addressinfo_tv);

        type_tv = (TextView) findViewById(R.id.type_tvme);
        status_card = (TextView) findViewById(R.id.status_card);
        liuyan_et = (TextView) findViewById(R.id.liuyan_et);

        chef_avatar_iv = (CircleImageView) findViewById(R.id.chef_avatar_iv);
        time_mid = (TextView) findViewById(R.id.mid_time_tv);
        left_tv = (RippleView) findViewById(R.id.left_tv);
        right_tv = (RippleView) findViewById(R.id.right_tv);
        center_line = (ImageView) findViewById(R.id.center_line);
        mid_tv = (TextView) findViewById(R.id.mid_tv);
        night_tv = (TextView) findViewById(R.id.night_tv);
        all_price_tv = (TextView) findViewById(R.id.all_price_tv);
        address_tv = (TextView) findViewById(R.id.address_tv);
        bottom_rl = (RelativeLayout) findViewById(R.id.bottom_rl);
        order_date = (TextView) findViewById(R.id.order_date);
        order_no_tv = (TextView) findViewById(R.id.order_no_tv);
        day_tv = (TextView) findViewById(R.id.day_number_tv);
        mid_tag_tv = (TextView) findViewById(R.id.mid_tag_tv);
        mid_time_tv = (TextView) findViewById(R.id.mid_time_tv);
        location_rl = (RelativeLayout) findViewById(R.id.location_rl);
        cardView_location = (CardView) findViewById(R.id.cardView_location);
        distance_tv = (TextView) findViewById(R.id.distance_tv);
        if (addressDistance != 0) {
            distance_tv.setText("(" + Tools.getDistanc((int) addressDistance) + ")");
        }
    }

    private void initStatus() {
        day_tv.setText(MainTools.changeMD(shikeinfo.getEatenwhen()));
        tag_tv.setText(shikeinfo.getTag());
        order_date.setText(shikeinfo.getCreatetime());
        chef_nickname_tv.setText(shikeinfo.getNickname());
        CustomerHead.loadImage(shikeinfo.getAvatar(), chef_avatar_iv);
        order_no_tv.setText(shikeinfo.getNumber());
        // if (index == 2) {
        // delete_tv.setText("删除订单");
        // delete_tv.setVisibility(View.VISIBLE);
        // }
        if (shikeinfo.getOlstatus().equals("1")) {
            left_tv.setVisibility(View.GONE);
            center_line.setVisibility(View.GONE);
            right_tv.setText("已完成订单");
            right_tv.setBackgroundResource(R.color.gray);
            right_tv.setEnabled(false);
            chef_call_iv.setBackgroundResource(R.drawable.call_green);
            // status_card.setText("订单已付款");
            if (shikeinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            }
            cardView_location.setVisibility(View.GONE);
        } else if (shikeinfo.getOlstatus().equals("3")) {
            center_line.setVisibility(View.GONE);
            right_tv.setVisibility(View.GONE);
            if (shikeinfo.isDelivery()) {
                left_tv.setText("等待确认");
            } else {
                left_tv.setText("确认配送");
            }
            left_tv.setVisibility(View.VISIBLE);
            chef_call_iv.setBackgroundResource(R.drawable.cook_call);
            // status_card.setText("订单已付款");
            if (shikeinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            }
        } else if (shikeinfo.getOlstatus().equals("2")) {
            left_tv.setText("接单");
            right_tv.setText("拒接");
            chef_call_iv.setBackgroundResource(R.drawable.cook_call);
            if (shikeinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {// 已付款，未接单
                status_card.setText("已付款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            }
        } else if (shikeinfo.getOlstatus().equals("4")) {
            left_tv.setVisibility(View.GONE);
            center_line.setVisibility(View.GONE);
            right_tv.setText("已拒接订单");
            right_tv.setBackgroundResource(R.color.gray);
            right_tv.setEnabled(false);
            chef_call_iv.setBackgroundResource(R.drawable.call_green);
            // status_card.setText("订单已拒接");
            // status_card.setBackgroundResource(R.drawable.card_gray);
            if (shikeinfo.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                status_card.setText("退款中");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                status_card.setText("已退款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            }
        } else if (shikeinfo.getOlstatus().equals("5")) {
            left_tv.setVisibility(View.GONE);
            center_line.setVisibility(View.GONE);
            right_tv.setText("已取消订单");
            right_tv.setBackgroundResource(R.color.gray);
            right_tv.setEnabled(false);
            chef_call_iv.setBackgroundResource(R.drawable.call_green);
            // status_card.setText("订单已取消");
            // status_card.setBackgroundResource(R.drawable.card_gray);
            if (shikeinfo.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                status_card.setText("退款中");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                status_card.setText("已退款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_NO)) {
                status_card.setText("未付款");
            } else if (shikeinfo.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                status_card.setText("已付款");
            }
        }
        if (shikeinfo.getEatenway().equals("自提")) {
            address_tv.setText("自提地址：");
            addressinfo_tv.setText(shikeinfo.getZitiaddr());
        } else {
            addressinfo_tv.setText(shikeinfo.getDineraddr());
        }
        name_tv.setText(shikeinfo.getDinername());
        type_tv.setText(shikeinfo.getEatenway());
        price_tv.setText("￥" + MainTools.getDoubleValue(shikeinfo.getPrice(), 1));
        number_tv.setText(shikeinfo.getEatennum() + "");
        phone_number_tv.setText(shikeinfo.getDinermobile());
        time_mid.setText(shikeinfo.getLunchtime());
        if (TextUtils.isEmpty(shikeinfo.getRemark())) {
            liuyan_et.setText("无");
        } else {
            liuyan_et.setText(shikeinfo.getRemark());
        }

        if (TextUtils.isEmpty(shikeinfo.getLunchtime()) && TextUtils.isEmpty(shikeinfo.getDinnertime())) {
            night_tv.setVisibility(View.GONE);
            mid_tv.setVisibility(View.GONE);
        } else {
            if (TextUtils.isEmpty(shikeinfo.getLunchtime()) || TextUtils.isEmpty(shikeinfo.getDinnertime())) {
                mid_tv.setVisibility(View.VISIBLE);
                night_tv.setVisibility(View.GONE);
                if (TextUtils.isEmpty(shikeinfo.getLunchtime())) {
                    mid_tv.setText("晚");
                    mid_tag_tv.setText("晚");
                    mid_time_tv.setText(shikeinfo.getDinnertime());
                } else {
                    mid_tv.setText("午");
                    mid_tag_tv.setText("午");
                    mid_time_tv.setText(shikeinfo.getLunchtime());
                }
            } else {
                mid_tv.setVisibility(View.VISIBLE);
                night_tv.setVisibility(View.VISIBLE);
                mid_tv.setText("午");
                night_tv.setText("晚");
            }
        }
        double allResult = Arith.add(shikeinfo.getTotal(), shikeinfo.getDiscount());
        all_price_tv.setText("￥" + MainTools.getDoubleValue(allResult, 1));
    }

    public void setDateView(TextView view1, String week) {
        view1.setText(week);
    }

    private void btn_listner() {
        location_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent = new Intent(ShikeDetailActivity.this, DhShikeLocationActivity.class);
                intent.putExtra("lat", shikeinfo.getLatitude());
                intent.putExtra("long", shikeinfo.getLongitude());
                if (shikeinfo.getLatitude() != 0 && shikeinfo.getLongitude() != 0) {
                    startActivity(intent);
                } else {
                    MainTools.ShowToast(ShikeDetailActivity.this, "地址可能有误");
                }

            }
        });
        left_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shikeinfo.getOlstatus().equals("2")) {
                    new SweetAlertDialog(ShikeDetailActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否确认当前操作").setCancelText("取消")
                            .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            if (MainTools.isNetworkAvailable(ShikeDetailActivity.this)) {
                                postStatus(shikeinfo.getOlids(), "3", position);
                            } else {
                                MainTools.ShowToast(ShikeDetailActivity.this, R.string.interneterror);
                            }
                        }
                    }).show();
                } else if (shikeinfo.getOlstatus().equals("3")) {
                    new SweetAlertDialog(ShikeDetailActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否确认当前操作").setCancelText("取消")
                            .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            if (MainTools.isNetworkAvailable(ShikeDetailActivity.this)) {
                                if (!shikeinfo.isDelivery()) {
                                    postStatus(shikeinfo.getOlids(), "9", position);
                                }
                            } else {
                                MainTools.ShowToast(ShikeDetailActivity.this, R.string.interneterror);
                            }
                        }
                    }).show();
                }
            }
        });

        right_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (right_tv.getText().toString().equals("拒接")) {
                    new SweetAlertDialog(ShikeDetailActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否拒接该订单")
                            .setCancelText("取消").setConfirmText("确定").showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            if (MainTools.isNetworkAvailable(ShikeDetailActivity.this)) {
                                postStatus(shikeinfo.getOlids(), "4", position);
                            } else {
                                MainTools.ShowToast(ShikeDetailActivity.this, R.string.interneterror);
                            }
                        }
                    }).show();
                }
            }
        });
        chef_call_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + shikeinfo.getDinermobile());
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    private void postStatus(final String olids, final String status, final int position) {
        params = ParamData.getInstance().postStatusObj(olids, status, shikeinfo.getOlversion());
        client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pd = new SweetAlertDialog(ShikeDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("请稍等");
                pd.setCancelable(false);
                pd.getProgressHelper().setBarColor(ShikeDetailActivity.this.getResources().getColor(R.color.blue_btn_bg_color));
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    if (request == 1) {
                        if (status.equals("3")) {
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_ONE));
                            // EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_TWO));
                        } else if (status.equals("1")) {
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_TWO));
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_THREE));
                        } else if (status.equals("4")) {
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_ONE));
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_THREE));
                        } else if (status.equals("7")) {
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_THREE));
                        } else if (status.equals("9")) {
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_TWO));
                        }
                        pd.setTitleText(jobj.getString("tag")).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        mHandler.sendEmptyMessageDelayed(1, 1500);
                    } else {
                        pd.setTitleText(jobj.getString("tag")).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_shikedetail_new;
    }

    @Override
    protected String initToolbarTitle() {
        return "食客详情";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//			case R.id.delete :
//				deleteOrder();
//				return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteOrder() {
        new SweetAlertDialog(ShikeDetailActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否删除该条订单").setCancelText("取消")
                .setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
            }
        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                if (MainTools.isNetworkAvailable(ShikeDetailActivity.this)) {
                    postStatus(shikeinfo.getOlids(), "7", position);
                } else {
                    MainTools.ShowToast(ShikeDetailActivity.this, R.string.interneterror);
                }
            }
        }).show();

    }
}
