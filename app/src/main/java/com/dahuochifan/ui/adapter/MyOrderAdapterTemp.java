package com.dahuochifan.ui.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.bean.MaterialSimpleListItem;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.activity.CommentsNewActivity;
import com.dahuochifan.ui.activity.OrderDetailActivity;
import com.dahuochifan.ui.activity.TimeLineActivity;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;
import com.payment.alipay.demo.Dh_Alipay;
import com.payment.alipay.demo.PayResult;
import com.payment.wechatpay.demo.Dh_WechatPay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class MyOrderAdapterTemp extends RootAdapter<OrderInfo> {
    private FragmentActivity context;
    private RequestParams params;
    private SweetAlertDialog pd;
    private int index;
    private String olidTemp;
    static class MyHandler extends Handler {
        WeakReference<SweetAlertDialog> wrReferences;
        WeakReference<FragmentActivity> contextReferences;
        WeakReference<MyOrderAdapterTemp> adapterReferences;
        MyHandler(SweetAlertDialog wrReference,FragmentActivity context,MyOrderAdapterTemp adapter) {
            this.wrReferences = new WeakReference<>(wrReference);
            this.contextReferences=new WeakReference<>(context);
            this.adapterReferences=new WeakReference<>(adapter);
        }
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE6:

                    break;
                case MyConstant.MYHANDLER_CODE4:
                    wrReferences.get().dismiss();
                    break;
                case MyConstant.SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(contextReferences.get(), "支付成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                        adapterReferences.get().postSuccess();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(contextReferences.get(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(contextReferences.get(), "订单尚未被支付", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
    public MyOrderAdapterTemp(FragmentActivity fragmentActivity, int index) {
        super(fragmentActivity);
        this.context = fragmentActivity;
        params = new RequestParams();
        this.index = index;
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && event.getType() == MyConstant.EVENTBUS_PAY) {
            if (event.getPosition() == 0) {
                //代表微信支付成功
            }
        }
    }
    public void postSuccess(){
        if(!TextUtils.isEmpty(olidTemp)){
            params = ParamData.getInstance().postStatusObj(olidTemp, "10", 0L);
            client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                }

                @Override
                public void onFinish() {
                    olidTemp="";
                    super.onFinish();
                }
            });
        }
    }
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_myorder_view, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.chef_avatar_iv = (CircleImageView) v.findViewById(R.id.chef_avatar_iv);
        viewHolder.chef_nickname_tv = (TextView) v.findViewById(R.id.chef_nickname_tv);
        viewHolder.tag_tv = (TextView) v.findViewById(R.id.tag_tv);
        viewHolder.person_tv = (TextView) v.findViewById(R.id.person_tv);
        viewHolder.day_number_tv = (TextView) v.findViewById(R.id.day_number_tv);
        viewHolder.mid_tv = (TextView) v.findViewById(R.id.mid_tv);
        viewHolder.night_tv = (TextView) v.findViewById(R.id.night_tv);
        viewHolder.all_price_tv = (TextView) v.findViewById(R.id.all_price_tv);
        viewHolder.comment_tv = (TextView) v.findViewById(R.id.comment_tv);
        viewHolder.status_tv = (TextView) v.findViewById(R.id.status_tv);
        viewHolder.top_rl = (RelativeLayout) v.findViewById(R.id.top_rl);
        viewHolder.level_tv = (TextView) v.findViewById(R.id.level_tv);
        viewHolder.timeline_tv = (TextView) v.findViewById(R.id.timeline_tv);
        viewHolder.detail_tv = (TextView) v.findViewById(R.id.detail_tv);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, final int position, final OrderInfo object) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        CookerHead.loadImage(object.getAvatar(), viewHolder.chef_avatar_iv);
        viewHolder.chef_nickname_tv.setText(object.getNickname());
        viewHolder.person_tv.setText(object.getEatennum() + "份");
        viewHolder.level_tv.setText(object.getCbname());
        if (object.isgrapcoupon() && object.getGrapcoupon() != null) {
            viewHolder.detail_tv.setText("发红包");
            viewHolder.detail_tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
        } else {
            viewHolder.detail_tv.setText("详情");
            viewHolder.detail_tv.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
        }
        if (object.getTrack() != null) {
            if (TextUtils.isEmpty(object.getTrack().getStatus()) || !object.getTrack().getStatus().equals("0")) {
                viewHolder.timeline_tv.setVisibility(View.GONE);
            } else {
                viewHolder.timeline_tv.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.timeline_tv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(object.getTag())) {
            viewHolder.tag_tv.setText(object.getTag());
        }
        if (TextUtils.isEmpty(object.getLunchtime())) {
            viewHolder.mid_tv.setVisibility(View.GONE);
        } else {
            viewHolder.mid_tv.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(object.getDinnertime())) {
            viewHolder.night_tv.setVisibility(View.GONE);
        } else {
            viewHolder.night_tv.setVisibility(View.VISIBLE);
        }
        viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()));
        final double allprice_temp = object.getTotal();
        viewHolder.all_price_tv.setText("￥" + MainTools.getDoubleValue(allprice_temp, 1));
        switch (object.getOlstatus()) {
            case "2":// 等待接单
                if (MyConstant.PAY_SUCCESS.equals(object.getPaystatus())) {
                    viewHolder.status_tv.setText("已付款");
                    viewHolder.comment_tv.setText("等待接单");
                    viewHolder.comment_tv.setVisibility(View.VISIBLE);
                    viewHolder.comment_tv.setBackgroundResource(R.drawable.card_main2);
                } else if (MyConstant.PAY_NO.equals(object.getPaystatus())) {
                    viewHolder.status_tv.setText("未付款");
                    viewHolder.comment_tv.setText("付款");
                    viewHolder.comment_tv.setVisibility(View.VISIBLE);
                    viewHolder.comment_tv.setBackgroundResource(R.drawable.card_main2);
                } else if (MyConstant.PAY_WATING.equals(object.getPaystatus())) {
                    viewHolder.status_tv.setText(" ");
                    viewHolder.comment_tv.setText("支付处理中");
                    viewHolder.comment_tv.setVisibility(View.VISIBLE);
                    viewHolder.comment_tv.setBackgroundResource(R.drawable.card_main2);
                }
                viewHolder.comment_tv.setTextColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.status_tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
                break;
            case "3":// 已经接单
                if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                    viewHolder.status_tv.setText("已付款");
                    viewHolder.comment_tv.setText("确认用餐");
                    viewHolder.comment_tv.setVisibility(View.VISIBLE);
                } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                    viewHolder.status_tv.setText("交易状态异常");
                    viewHolder.comment_tv.setText("异常");
                    viewHolder.comment_tv.setVisibility(View.VISIBLE);
                }
                viewHolder.comment_tv.setTextColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.comment_tv.setBackgroundResource(R.drawable.card_main2);
                viewHolder.status_tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
                break;
            case "4":
                if (object.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                    viewHolder.status_tv.setText("退款中");
                } else if (object.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                    viewHolder.status_tv.setText("已退款");
                } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                    viewHolder.status_tv.setText("未付款");
                } else if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                    viewHolder.status_tv.setText("已付款");
                }
                viewHolder.comment_tv.setText("主厨已拒单");
                viewHolder.comment_tv.setTextColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.comment_tv.setBackgroundResource(R.drawable.card_gray);
                viewHolder.status_tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
                break;
            case "5":// 取消订单
                if (object.getPaystatus().equals(MyConstant.REFUND_APPLY)) {
                    viewHolder.status_tv.setText("退款中");
                } else if (object.getPaystatus().equals(MyConstant.REFUND_SUCCESS)) {
                    viewHolder.status_tv.setText("已退款");
                } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                    viewHolder.status_tv.setText("未付款");
                } else if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                    viewHolder.status_tv.setText("已付款");
                }
                viewHolder.comment_tv.setText("已取消订单");
                viewHolder.comment_tv.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.comment_tv.setBackgroundResource(R.drawable.card_gray);
                viewHolder.status_tv.setTextColor(context.getResources().getColor(R.color.maincolor_new));
                break;
            default:

                break;
        }
        viewHolder.top_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("obj", object);
                bundle.putInt("index", index);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        viewHolder.comment_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.comment_tv.getText().toString().equals("付款")) {
                    postStatus(object.getOlids(), "8", position, object);
                } else if (viewHolder.comment_tv.getText().toString().equals("确认用餐")) {
                    postStatus(object.getOlids(), "1", position, object);
                } else if (viewHolder.comment_tv.getText().toString().equals("支付处理中")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).setCancelable(false).setTitle("支付提示").setMessage("当前支付正在处理中，请耐心等待")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                                    dialog.dismiss();
                                }
                            }).create();
                    alertDialog.show();
                }
            }
        });
        viewHolder.timeline_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (object.getTrack().getStatus().equals("0")) {
                    Intent intent = new Intent(context, TimeLineActivity.class);
                    if (!TextUtils.isEmpty(object.getTrack().getUrl())) {
                        intent.putExtra("trackurl", object.getTrack().getUrl());
                        context.startActivity(intent);
                    }
                }
            }
        });
    }

    private void showPayDialog(final OrderInfo object) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("支付方式");
        builder.setCancelable(true);
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(context);
        String[] array = {"支付宝支付", "微信支付"};
        adapter.add(new MaterialSimpleListItem.Builder(context).content(array[0]).icon(R.drawable.ic_alipay_logo).build());
        adapter.add(new MaterialSimpleListItem.Builder(context).content(array[1]).icon(R.drawable.ic_wx_logo).build());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        olidTemp=object.getOlids();
                        dialog.dismiss();
                        MyHandler handler=new MyHandler(pd,context,MyOrderAdapterTemp.this);
                        Dh_Alipay mypay = new Dh_Alipay(context, handler);
                        String detail_Str = "单价" + object.getPrice() + "元-" + "订餐人数" + object.getEatennum() + "人-" + "总价" + object.getTotal() + "元-"
                                + "优惠" + "0" + "元";
                        if (BuildConfig.LEO_DEBUG)
                            L.e("all_price===" + object.getTotal());
                        mypay.payFunction(object.getNickname() + "-" + object.getCbname() + "-" + object.getTag(), detail_Str, object.getTotal()
                                + "", object.getNumber());
                        break;
                    case 1:
                        olidTemp=object.getOlids();
                        dialog.dismiss();
                        if (object.getTotal() != 0) {
                            Dh_WechatPay wepay = new Dh_WechatPay(context);
                            String deString = "单价" + object.getPrice() + "元-" + "订餐人数" + object.getEatennum() + "人-" + "总价" + object.getTotal() + "元-"
                                    + "优惠" + "0" + "元";
                            wepay.payFunction(object.getNickname() + "-" + object.getCbname() + "-" + object.getTag(), deString,
                                    (int) (object.getTotal() * 100) + "", object.getNumber());
                        }
                        break;
                }
            }
        });
        builder.show();
    }

    private void postStatus(final String olids, final String status, final int position, final OrderInfo object) {
        params = ParamData.getInstance().postStatusObj(olids, status, object.getOlversion());
        client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                switch (status) {
                    case "5":
                        pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在取消订单");
                        break;
                    case "6":
                        pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在删除订单");
                        break;
                    case "1":
                        pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在确认用餐");
                        break;
                    case "8":
                        pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在完成付款");
                        break;
                }
                pd.setCancelable(false);
                pd.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG)
                    L.e("delete" + responseString);
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    String tagStr = jobj.getString("tag");
                    if (!status.equals("8")) {
                        if (request == 1) {
                            pd.setTitleText(jobj.getString("tag")).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//							Message msg = Message.obtain();
//							msg.arg1 = position;
//							msg.what = MyConstant.MYHANDLER_CODE6;
//							handler.sendMessageDelayed(msg, 1500);
                            pd.dismiss();
                            new AlertDialog.Builder(context).setCancelable(true).setTitle("快速评论").setMessage("是否要去评论")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(context, CommentsNewActivity.class);
                                            intent.putExtra("obj", (Serializable) object);
                                            context.startActivity(intent);
                                        }
                                    }).create().show();
                        } else {
                            pd.setContentText(jobj.getString("tag")).setTitleText("错误提示").setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        }
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
//						EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_MOVEONE));
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_TWO));
                        EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_THREE));
                    } else {
                        if (request == 1) {
                            showPayDialog(object);
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            new AlertDialog.Builder(context).setCancelable(false).setTitle("支付异常").setMessage(tagStr)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                            L.e("olids===" + olids);
                            EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyHandler handler=new MyHandler(pd,context,MyOrderAdapterTemp.this);
                pd.setTitleText("网络不给力").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
            }
        });
    }

    private class ViewHolder {
        private CircleImageView chef_avatar_iv;
        private TextView chef_nickname_tv;
        private TextView tag_tv;
        private TextView person_tv;
        private TextView day_number_tv;
        private TextView mid_tv;
        private TextView night_tv;
        private TextView all_price_tv;
        private TextView comment_tv;
        private TextView status_tv;
        private RelativeLayout top_rl;
        private TextView level_tv;
        private TextView timeline_tv;
        private TextView detail_tv;
    }

}
