package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.custom.ShikeInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.activity.ShikeDetailActivity;
import com.dahuochifan.ui.activity.TimeLineActivity;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class MyShikeAdapterTempYijie extends RootAdapter<ShikeInfo> {
    private Context context;
    private RequestParams params;
    private SweetAlertDialog pd;

    public MyShikeAdapterTempYijie(Context context) {
        super(context);
        this.context = context;
        params = new RequestParams();
        pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在确认配送");
        pd.setCancelable(false);
        pd.getProgressHelper().setBarColor(ContextCompat.getColor(context, R.color.blue_btn_bg_color));
    }

    static class MyHandler extends Handler {
        WeakReference<SweetAlertDialog> swReferences;

        MyHandler(SweetAlertDialog dialog) {
            this.swReferences = new WeakReference<>(dialog);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE6:
                    swReferences.get().dismiss();
                    break;
                case MyConstant.MYHANDLER_CODE4:
                    swReferences.get().dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_my_shike_new3, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.chef_avatar_iv = (CircleImageView) v.findViewById(R.id.chef_avatar_iv);
        viewHolder.chef_nickname_tv = (TextView) v.findViewById(R.id.chef_nickname_tv);
        viewHolder.tag_tv = (TextView) v.findViewById(R.id.tag_tv);
        viewHolder.person_tv = (TextView) v.findViewById(R.id.person_tv);
        viewHolder.day_number_tv = (TextView) v.findViewById(R.id.day_number_tv);
        viewHolder.night_tv = (TextView) v.findViewById(R.id.night_tv);
        viewHolder.all_price_tv = (TextView) v.findViewById(R.id.all_price_tv);
        viewHolder.status_tv = (TextView) v.findViewById(R.id.status_tv);
        viewHolder.top_rl = (RelativeLayout) v.findViewById(R.id.top_rl);
        viewHolder.pay_tv = (TextView) v.findViewById(R.id.pay_tv);
        viewHolder.address_tv = (TextView) v.findViewById(R.id.address_tv);
        viewHolder.timeline_tv = (TextView) v.findViewById(R.id.timeline_tv);
        viewHolder.control_rl = (RelativeLayout) v.findViewById(R.id.control_rl);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, final int position, final ShikeInfo object) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        CustomerHead.loadImage(object.getAvatar(), viewHolder.chef_avatar_iv);
        viewHolder.chef_nickname_tv.setText(object.getNickname());
        String eatStr = object.getEatennum() + "份";
        viewHolder.person_tv.setText(eatStr);
        viewHolder.tag_tv.setText(object.getTag());
        if (!TextUtils.isEmpty(object.getLunchtime())) {
            viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()) + object.getLunchtime());
        } else {
            viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()) + object.getDinnertime());
        }
        viewHolder.address_tv.setText(object.getDinername() + " (" + object.getDineraddr() + ")");
        if (object.isDelivery()) {
            viewHolder.control_rl.setVisibility(View.GONE);
        } else {
            viewHolder.control_rl.setVisibility(View.VISIBLE);
        }
        if (object.getTrack() != null) {
            viewHolder.timeline_tv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.timeline_tv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(object.getLunchtime())) {
            viewHolder.night_tv.setText("午");
        }
        if (!TextUtils.isEmpty(object.getDinnertime())) {
            viewHolder.night_tv.setText("晚");
        }
        double allResult = Arith.add(object.getTotal(), object.getDiscount());
        String alPStr = "￥" + MainTools.getDoubleValue(allResult, 1);
        viewHolder.all_price_tv.setText(alPStr);
        switch (object.getOlstatus()) {
            case "2":
                switch (object.getPaystatus()) {
                    case MyConstant.PAY_SUCCESS:
                        viewHolder.pay_tv.setText("已付款");
                        viewHolder.status_tv.setText("待接单");
                        break;
                    case MyConstant.PAY_NO:
                        viewHolder.pay_tv.setText("未付款");
                        viewHolder.status_tv.setText("异常");
                        break;
                    case MyConstant.REFUND_APPLY:
                        viewHolder.pay_tv.setText("退款中");
                        viewHolder.status_tv.setText("异常");
                        break;
                    case MyConstant.REFUND_SUCCESS:
                        viewHolder.pay_tv.setText("已退款");
                        viewHolder.status_tv.setText("异常");
                        break;

                }
                break;
            case "3"://这是应该出现的
                switch (object.getPaystatus()) {
                    case MyConstant.PAY_SUCCESS:
                        viewHolder.pay_tv.setText("已付款");
                        break;
                    case MyConstant.PAY_NO:
                        viewHolder.pay_tv.setText("未付款");
                        break;
                    case MyConstant.REFUND_APPLY:
                        viewHolder.pay_tv.setText("退款中");
                        break;
                    case MyConstant.REFUND_SUCCESS:
                        viewHolder.pay_tv.setText("已退款");
                        break;
                }
                viewHolder.status_tv.setText("等待确认用餐");
                break;
            case "5":
                switch (object.getPaystatus()) {
                    case MyConstant.PAY_SUCCESS:
                        viewHolder.pay_tv.setText("已付款");
                        break;
                    case MyConstant.PAY_NO:
                        viewHolder.pay_tv.setText("未付款");
                        break;
                    case MyConstant.REFUND_APPLY:
                        viewHolder.pay_tv.setText("退款中");
                        break;
                    case MyConstant.REFUND_SUCCESS:
                        viewHolder.pay_tv.setText("已退款");
                        break;
                }
                viewHolder.status_tv.setText("已取消");
                break;
            case "4":
                switch (object.getPaystatus()) {
                    case MyConstant.PAY_SUCCESS:
                        viewHolder.pay_tv.setText("已付款");
                        break;
                    case MyConstant.PAY_NO:
                        viewHolder.pay_tv.setText("未付款");
                        break;
                    case MyConstant.REFUND_APPLY:
                        viewHolder.pay_tv.setText("退款中");
                        break;
                    case MyConstant.REFUND_SUCCESS:
                        viewHolder.pay_tv.setText("已退款");
                        break;
                }
                viewHolder.status_tv.setText("已拒单");
                break;
            default:

                break;
        }
        viewHolder.top_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShikeDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putSerializable("obj", object);
                bundle.putInt("index", 1);
                intent.putExtras(bundle);
                context.startActivity(intent);
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
        viewHolder.control_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                new AlertDialog.Builder(context).setCancelable(true).setTitle("确认配送").setMessage("是否已经配送")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                postStatus(object.getOlids(), "9", object);
                            }
                        }).create().show();
            }
        });
    }

    private void postStatus(final String olids, final String status, final ShikeInfo object) {
        final MyHandler handler = new MyHandler(pd);
        params = ParamData.getInstance().postStatusObj(olids, status, object.getOlversion());
        client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                pd.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG)
                    L.e("delete" + responseString);
                try {
                    JSONObject jobj = new JSONObject(responseString);
                    int request = jobj.getInt("resultcode");
                    String tagStr = jobj.getString("tag");
                    if (request == 1) {
                        pd.setTitleText(tagStr).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        L.e("dy===2");
                        handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE6, 1500);
                    } else {
                        pd.setContentText(jobj.getString("tag")).setTitleText("错误提示").setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    }
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_TWO));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.setTitleText("网络不给力").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
            }
        });
    }


    public class ViewHolder {
        private CircleImageView chef_avatar_iv;
        private TextView chef_nickname_tv;
        private TextView tag_tv;
        private TextView person_tv;
        private TextView day_number_tv;
        private TextView night_tv;
        private TextView all_price_tv;
        private TextView status_tv;
        private RelativeLayout top_rl;
        private TextView pay_tv;
        private TextView address_tv;
        private TextView timeline_tv;
        private RelativeLayout control_rl;
        private ImageView right_iv;
    }

}
