package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.model.cookbook.CBActs;
import com.dahuochifan.utils.PreviewLoader;

public class Pop2Adapter extends RootAdapter<CBActs> {

    public Pop2Adapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_pop_tag2, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.pop_tag_tv = (TextView) v.findViewById(R.id.pop_tag_tv);
        viewHolder.pop_tag_iv = (ImageView) v.findViewById(R.id.pop_tag_iv);
        viewHolder.pop_tag_icon = (ImageView) v.findViewById(R.id.pop_tag_icon);
        viewHolder.act_rl = (RelativeLayout) v.findViewById(R.id.act_rl);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, int position, CBActs object) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.pop_tag_tv.setText(object.getRemark());
        if (!TextUtils.isEmpty(object.getIconurl())) {
            PreviewLoader.loadImage(object.getIconurl(), viewHolder.pop_tag_icon);
        } else {
            viewHolder.pop_tag_icon.setImageResource(R.drawable.white_press_selector);
        }
        if (position == getCount() - 1) {
            viewHolder.pop_tag_iv.setVisibility(View.GONE);
        } else {
            viewHolder.pop_tag_iv.setVisibility(View.VISIBLE);
        }
    }

    public class ViewHolder {
        private TextView pop_tag_tv;
        private ImageView pop_tag_iv;
        private ImageView pop_tag_icon;
        private RelativeLayout act_rl;
    }

}
