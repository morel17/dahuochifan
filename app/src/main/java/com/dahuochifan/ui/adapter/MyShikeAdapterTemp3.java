package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.dahuochifan.ui.activity.ShikeDetailActivity;
import com.dahuochifan.ui.activity.TimeLineActivity;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.custom.ShikeInfo;
import com.dahuochifan.utils.Arith;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.ui.views.CircleImageView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.utils.L;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyShikeAdapterTemp3 extends RootAdapter<ShikeInfo> {
    private Context context;
    private RequestParams params;
    private Gson gson;
    private SweetAlertDialog pd;

    public MyShikeAdapterTemp3(Context context) {
        super(context);
        this.context = context;
        params = new RequestParams();
        gson = new Gson();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE6:
                    pd.dismiss();
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
        viewHolder.stamp_iv = (ImageView) v.findViewById(R.id.stamp_iv);
        viewHolder.right_iv = (ImageView) v.findViewById(R.id.right_iv);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, final int position, final ShikeInfo object) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        CustomerHead.loadImage(object.getAvatar(), viewHolder.chef_avatar_iv);
        viewHolder.chef_nickname_tv.setText(object.getNickname());
        viewHolder.person_tv.setText(object.getEatennum() + "份");
        viewHolder.tag_tv.setText(object.getTag());
        if (!TextUtils.isEmpty(object.getLunchtime())) {
            viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()) + object.getLunchtime());
        } else {
            viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()) + object.getDinnertime());
        }
        viewHolder.address_tv.setText(object.getDinername() + " (" + object.getDineraddr() + ")");
        viewHolder.right_iv.setVisibility(View.GONE);
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
        if (object.isclearing()) {
            viewHolder.stamp_iv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.stamp_iv.setVisibility(View.GONE);
        }
        double allResult = Arith.add(object.getTotal(), object.getDiscount());
        viewHolder.all_price_tv.setText("￥" + MainTools.getDoubleValue(allResult, 1));
        switch (object.getOlstatus()) {
            case "1":
                if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
                    viewHolder.pay_tv.setText("已付款");
                } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
                    viewHolder.pay_tv.setText("未付款");
                } else {
                    viewHolder.pay_tv.setText("异常");
                    if (BuildConfig.LEO_DEBUG) L.e("error=====" + object.getPaystatus());
                }
                viewHolder.status_tv.setText("订单已完成");
                break;
            case "4":
                viewHolder.status_tv.setText("订单已拒绝");
                break;
            case "5":
                viewHolder.status_tv.setText("订单已取消");
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
                bundle.putInt("index", 2);
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
        private ImageView stamp_iv;
        private ImageView right_iv;
    }

}
