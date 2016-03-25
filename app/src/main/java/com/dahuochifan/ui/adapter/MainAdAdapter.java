package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.activities.DhAdDetail;
import com.dahuochifan.ui.activity.DhActivitiesDetailActivity;
import com.dahuochifan.utils.LunboLoader;

public class MainAdAdapter extends RootAdapter<DhAdDetail> {
    private Context context;

    public MainAdAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.adapter_mainad, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.ad_iv = (ImageView) v.findViewById(R.id.ad_iv);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, int position, final DhAdDetail object) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        String url = object.getHead() + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
                + context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY;
        if (viewHolder.ad_iv.getTag() == null || !viewHolder.ad_iv.getTag().equals(url)) {
            LunboLoader.loadImage(url, viewHolder.ad_iv);
            viewHolder.ad_iv.setTag(url);
        }

        viewHolder.ad_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(object.getDetail())) {
                    Intent intent = new Intent(context, DhActivitiesDetailActivity.class);
                    intent.putExtra("detail", object.getDetail());
                    intent.putExtra("url", object.getHead());
                    intent.putExtra("remark",object.getRemark());
                    context.startActivity(intent);
                }
            }
        });
    }

    public class ViewHolder {
        private ImageView ad_iv;
    }
}
