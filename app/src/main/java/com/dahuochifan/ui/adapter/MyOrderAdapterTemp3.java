package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.ui.activity.CommentsNewActivity;
import com.dahuochifan.ui.activity.OrderDetailActivity;
import com.dahuochifan.ui.activity.TimeLineActivity;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.nostra13.universalimageloader.utils.L;

public class MyOrderAdapterTemp3 extends RootAdapter<OrderInfo> {
    private Context context;
    private int index;

    public MyOrderAdapterTemp3(FragmentActivity context, int index) {
        super(context);
        this.context = context;
        this.index = index;
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
        viewHolder.status_tv = (TextView) v.findViewById(R.id.comment_tv);
        viewHolder.top_rl = (RelativeLayout) v.findViewById(R.id.top_rl);
        viewHolder.level_tv = (TextView) v.findViewById(R.id.level_tv);
        viewHolder.pay_status_tv = (TextView) v.findViewById(R.id.status_tv);
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
        viewHolder.tag_tv.setText(object.getTag());
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
        if (object.getPaystatus().equals(MyConstant.PAY_SUCCESS)) {
            viewHolder.pay_status_tv.setText("已付款");
            viewHolder.pay_status_tv.setTextColor(context.getResources().getColor(R.color.maincolor_new));
        } else if (object.getPaystatus().equals(MyConstant.PAY_NO)) {
            viewHolder.pay_status_tv.setText("未付款");
            viewHolder.pay_status_tv.setTextColor(context.getResources().getColor(R.color.maincolor_new));
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
        viewHolder.all_price_tv.setText("￥" + MainTools.getDoubleValue(object.getTotal(), 1));
        if (BuildConfig.LEO_DEBUG)
            L.e(object.getOlstatus());
        switch (object.getOlstatus()) {
            case "1":
                if (object.isIscomment()) {
                    viewHolder.status_tv.setText("已完成");
                } else {
                    viewHolder.status_tv.setText("给个评价");
                }
                break;
            case "4":
                viewHolder.status_tv.setText("已拒绝");
                break;
            case "5":
                viewHolder.status_tv.setText("已取消");
                break;
            default:

                break;
        }
        viewHolder.top_rl.setOnClickListener(new OnClickListener() {
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
        viewHolder.status_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (viewHolder.status_tv.getText().equals("给个评价")) {
                    Intent intent = new Intent(context, CommentsNewActivity.class);
                    intent.putExtra("obj", object);
                    context.startActivity(intent);
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

    private class ViewHolder {
        private CircleImageView chef_avatar_iv;
        private TextView chef_nickname_tv;
        private TextView tag_tv;
        private TextView person_tv;
        private TextView day_number_tv;
        private TextView mid_tv;
        private TextView night_tv;
        private TextView all_price_tv;
        private TextView status_tv;
        private RelativeLayout top_rl;
        private TextView level_tv;
        private TextView pay_status_tv;
        private TextView timeline_tv;
        private TextView detail_tv;
    }

}
