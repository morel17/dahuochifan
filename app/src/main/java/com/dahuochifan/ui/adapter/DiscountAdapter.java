package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.DhDiscountUnableActivity;
import com.dahuochifan.model.discount.DiscountDetail;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.zhy.android.percent.support.PercentRelativeLayout;

/**
 * Created by morel on 2015/11/5.
 */
public class DiscountAdapter extends RootAdapter<DiscountDetail> {
    private Context context;
    /**
     * Instantiates a new root adapter.
     */
    private int commonType = 0;
    private int lastType = 1;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        DiscountDetail detail = getItem(position);
        if (TextUtils.isEmpty(detail.getCuids())) {
            return lastType;
        } else {
            return commonType;
        }
    }

    public DiscountAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == commonType) {
            View v = inflater.inflate(R.layout.item_discount, parent, false);
            ViewHolderCommon viewHolderC = new ViewHolderCommon(v);
            v.setTag(viewHolderC);
            return v;
        } else {
            View v = inflater.inflate(R.layout.item_discount_last, parent, false);
            ViewHolderLast viewHolderL = new ViewHolderLast(v);
            v.setTag(viewHolderL);
            return v;
        }
    }

    @Override
    protected void bindView(View view, int position, DiscountDetail object) {
        if (getItemViewType(position) == commonType) {
            ViewHolderCommon viewHolderCommon = (ViewHolderCommon) view.getTag();
            viewHolderCommon.price_tv.setText(object.getDiscount() + "");
            viewHolderCommon.remain_tv.setText("还有" + MainTools.currentdaydistance2(object.getEnddate()) + "天有效");
            viewHolderCommon.type_tv.setText(object.getName());
            viewHolderCommon.type_info_tv.setText("满" + object.getConsumemin() + "元可用");
            viewHolderCommon.content_tv.setText(object.getContent());
            if (object.getStartdate().equals(object.getEnddate())) {
                viewHolderCommon.remain_detail_tv.setText(object.getStartdate() + "有效");
            } else {
                viewHolderCommon.remain_detail_tv.setText(object.getStartdate() + "至" + object.getEnddate() + "有效");
            }
        } else {
            ViewHolderLast viewHolderL = (ViewHolderLast) view.getTag();
            viewHolderL.discount_unused_rl.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    context.startActivity(new Intent(context, DhDiscountUnableActivity.class));
                }
            });
        }
    }

    class ViewHolderCommon {
        private TextView price_tv, remain_tv, type_tv, type_info_tv, content_tv, remain_detail_tv;

        public ViewHolderCommon(View itemView) {
            price_tv = (TextView) itemView.findViewById(R.id.price_tv);
            remain_tv = (TextView) itemView.findViewById(R.id.remain_tv);
            type_tv = (TextView) itemView.findViewById(R.id.type_tv);
            type_info_tv = (TextView) itemView.findViewById(R.id.type_info_tv);
            content_tv = (TextView) itemView.findViewById(R.id.content_tv);
            remain_detail_tv = (TextView) itemView.findViewById(R.id.remain_detail_tv);
        }
    }

    class ViewHolderLast {
        private PercentRelativeLayout discount_unused_rl;

        public ViewHolderLast(View itemView) {
            discount_unused_rl = (PercentRelativeLayout) itemView.findViewById(R.id.discount_unused_rl);
        }
    }
}
