package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.message.MessageDetail;
import com.dahuochifan.utils.LunboLoader;
import com.dahuochifan.utils.MainTools;

/**
 * Created by admin on 2015/11/3.
 */
public class DhNotiAdapter extends RootAdapter<MessageDetail> {
    private Context context;
    private int myType, sysType;

    /**
     * Instantiates a new root adapter.
     *
     * @param context the context
     */
    public DhNotiAdapter(Context context) {
        super(context);
        this.context = context;
        sysType = 0;
        myType = 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        MessageDetail detail = getItem(position);
        if (detail.getType().equals("2")) {
            return myType;
        } else {
            if (TextUtils.isEmpty(detail.getImgurl())) {
                return myType;
            } else {
                return sysType;
            }
        }
    }

    public class ViewHolderSys {
        private TextView type_tvSys, time_tvSys, content_tvSys;
        private ImageView content_ivSys;
        private RelativeLayout more_rlSys;

        public ViewHolderSys(View itemView) {
            type_tvSys = (TextView) itemView.findViewById(R.id.type_tvSys);
            time_tvSys = (TextView) itemView.findViewById(R.id.time_tvSys);
            content_tvSys = (TextView) itemView.findViewById(R.id.content_tvSys);
            content_ivSys = (ImageView) itemView.findViewById(R.id.content_ivSys);
            more_rlSys = (RelativeLayout) itemView.findViewById(R.id.more_rlSys);
        }
    }

    public class ViewHolderMe {
        private ImageView floatbtnMe;
        private TextView type_tvMe, time_tvMe, content_tvMe, type_left_tv_short;
        private RelativeLayout more_rlMe;

        public ViewHolderMe(View itemView) {
            floatbtnMe = (ImageView) itemView.findViewById(R.id.floatbtnMe);
            type_tvMe = (TextView) itemView.findViewById(R.id.type_tvMe);
            time_tvMe = (TextView) itemView.findViewById(R.id.time_tvMe);
            content_tvMe = (TextView) itemView.findViewById(R.id.content_tvMe);
            type_left_tv_short = (TextView) itemView.findViewById(R.id.type_left_tv_short);
            more_rlMe = (RelativeLayout) itemView.findViewById(R.id.more_rlMe);
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == myType) {
            View v = inflater.inflate(R.layout.item_notime, parent, false);
            ViewHolderMe viewHolderMe = new ViewHolderMe(v);
            v.setTag(viewHolderMe);
            return v;
        } else {
            View v = inflater.inflate(R.layout.item_notisys, parent, false);
            ViewHolderSys viewHolderSys = new ViewHolderSys(v);
            v.setTag(viewHolderSys);
            return v;
        }
    }

    @Override
    protected void bindView(View view, int position, MessageDetail object) {
        if (getItemViewType(position) == myType) {
            ViewHolderMe viewHolder = (ViewHolderMe) view.getTag();
            if (MainTools.currentdaydistance(object.getCreatetime()) == 0) {
                viewHolder.time_tvMe.setText(MainTools.dayForTime2(object.getCreatetime()));
            } else if (MainTools.currentdaydistance(object.getCreatetime()) == 1) {
                viewHolder.time_tvMe.setText("昨天");
            } else {
                viewHolder.time_tvMe.setText(MainTools.dayForDay(object.getCreatetime()));
            }
            viewHolder.content_tvMe.setText(Html.fromHtml(object.getContent()));
            viewHolder.type_tvMe.setText(object.getTitle());
            if (object.getType().equals("2")) {
                viewHolder.type_left_tv_short.setText("我的");
                if (object.is_read()) {
                    viewHolder.floatbtnMe.setBackgroundResource(R.drawable.circle_order_gray);
                } else {
                    viewHolder.floatbtnMe.setBackgroundResource(R.drawable.circle_order_red);
                }
            } else {
                viewHolder.type_left_tv_short.setText("系统");
                viewHolder.floatbtnMe.setBackgroundResource(R.drawable.circle_blue);
            }
        } else {
            ViewHolderSys viewHolderSys = (ViewHolderSys) view.getTag();
            if (MainTools.currentdaydistance(object.getCreatetime()) == 0) {
                viewHolderSys.time_tvSys.setText(MainTools.dayForTime2(object.getCreatetime()));
            } else if (MainTools.currentdaydistance(object.getCreatetime()) == 1) {
                viewHolderSys.time_tvSys.setText("昨天");
            } else {
                viewHolderSys.time_tvSys.setText(MainTools.dayForDay(object.getCreatetime()));
            }
            viewHolderSys.content_tvSys.setText(Html.fromHtml(object.getContent()));
            viewHolderSys.type_tvSys.setText(object.getTitle());
            if (TextUtils.isEmpty(object.getImgurl())) {
                viewHolderSys.content_ivSys.setVisibility(View.GONE);
            } else {
                viewHolderSys.content_ivSys.setVisibility(View.VISIBLE);
                String imgStr = object.getImgurl();
                String[] imgsStr = imgStr.split(",");
                if (imgsStr != null && imgsStr.length > 0) {
                    if (viewHolderSys.content_ivSys.getTag() == null || !viewHolderSys.content_ivSys.getTag().equals(imgsStr[0])) {
                        LunboLoader.loadImage(imgsStr[0] + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
                                + context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, viewHolderSys.content_ivSys);
                        viewHolderSys.content_ivSys.setTag(imgsStr[0]);
                    }
                }
            }
        }
    }
}
