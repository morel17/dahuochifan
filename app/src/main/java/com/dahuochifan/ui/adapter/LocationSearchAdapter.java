package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.dahuochifan.R;

/**
 * Created by Morel on 2015/10/26.
 *
 */
public class LocationSearchAdapter extends RootAdapter<Tip> {

    public LocationSearchAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_location, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.location_tv = (TextView) v.findViewById(R.id.location_tv);
        holder.district_tv=(TextView)v.findViewById(R.id.district_tv);
        v.setTag(holder);
        return v;
    }

    @Override
    protected void bindView(View view, int position, Tip object) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.location_tv.setText(object.getName());
        if(TextUtils.isEmpty(object.getDistrict())){
            holder.district_tv.setText("未知地址");
        }else{
            holder.district_tv.setText(object.getDistrict());
        }

    }

    class ViewHolder {
        private TextView location_tv,district_tv;
    }

}
