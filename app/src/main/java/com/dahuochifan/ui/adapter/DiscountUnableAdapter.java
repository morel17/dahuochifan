package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.model.discount.DiscountDetail;

/**
 * Created by admin on 2015/11/5.
 */
public class DiscountUnableAdapter extends RootAdapter<DiscountDetail> {
    private Context context;

    /**
     * Instantiates a new root adapter.
     *
     * @param context the context
     */

    public DiscountUnableAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_discount_unable, parent, false);
        ViewHolderCommon viewHolderC = new ViewHolderCommon(v);
        v.setTag(viewHolderC);
        return v;
    }

    @Override
    protected void bindView(View view, int position, DiscountDetail object) {
        ViewHolderCommon viewHolderCommon = (ViewHolderCommon) view.getTag();
        viewHolderCommon.price_tv.setText(object.getDiscount() + "");
        if (object.isuse()) {
            viewHolderCommon.remain_tv.setText("已使用");
            viewHolderCommon.discount_iv.setBackgroundResource(R.drawable.discount_used);
        } else {
            viewHolderCommon.remain_tv.setText("已过期");
            viewHolderCommon.discount_iv.setBackgroundResource(R.drawable.discount_unable);
        }
        viewHolderCommon.type_tv.setText(object.getName());
        viewHolderCommon.type_info_tv.setText("满" + object.getConsumemin() + "元可用");
        viewHolderCommon.content_tv.setText(object.getContent());
        if (object.getStartdate().equals(object.getEnddate())) {
            viewHolderCommon.remain_detail_tv.setText(object.getStartdate() + "有效");
        } else {
            viewHolderCommon.remain_detail_tv.setText(object.getStartdate() + "至" + object.getEnddate() + "有效");
        }
    }

    class ViewHolderCommon {
        private TextView price_tv, remain_tv, type_tv, type_info_tv, content_tv, remain_detail_tv;
        private ImageView discount_iv;

        public ViewHolderCommon(View itemView) {
            price_tv = (TextView) itemView.findViewById(R.id.price_tv);
            remain_tv = (TextView) itemView.findViewById(R.id.remain_tv);
            type_tv = (TextView) itemView.findViewById(R.id.type_tv);
            type_info_tv = (TextView) itemView.findViewById(R.id.type_info_tv);
            content_tv = (TextView) itemView.findViewById(R.id.content_tv);
            remain_detail_tv = (TextView) itemView.findViewById(R.id.remain_detail_tv);
            discount_iv = (ImageView) itemView.findViewById(R.id.discount_iv);
        }
    }
}
