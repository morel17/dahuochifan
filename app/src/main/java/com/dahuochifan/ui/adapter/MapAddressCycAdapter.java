package com.dahuochifan.ui.adapter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.AddressEvent;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.nostra13.universalimageloader.utils.L;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2015/8/27.
 */
public class MapAddressCycAdapter extends RecyclerView.Adapter<MapAddressCycAdapter.ViewHolder> {
    private LayoutInflater inflate;
    private List<PoiItem> mData;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private AppCompatActivity context;
    public MapAddressCycAdapter(AppCompatActivity context) {
        this.inflate = LayoutInflater.from(context);
        mData = new ArrayList<>();
        this.context=context;
        spf = SharedPreferenceUtil.initSharedPerence().init(context, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_mapitem, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.map_tv.setText(mData.get(position).toString());
        holder.map_tv2.setText(mData.get(position).getSnippet());
        holder.address_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData.get(position).getCityName().equals(mData.get(position).getProvinceName())){
                    EventBus.getDefault().post(new AddressEvent(MyConstant.EVENTBUS_ADD_ADDR,mData.get(position).getCityName()+mData.get(position).getAdName()+mData.get(position).getTitle(),mData.get(position).getTitle(),mData.get(position).getLatLonPoint().getLongitude()+"",mData.get(position).getLatLonPoint().getLatitude()+""));
                    context.finish();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView map_tv;
        private TextView map_tv2;
        private PercentLinearLayout address_rl;

        public ViewHolder(View itemView) {
            super(itemView);
            map_tv = (TextView) itemView.findViewById(R.id.map_tv);
            map_tv2 = (TextView) itemView.findViewById(R.id.map_tv2);
            address_rl = (PercentLinearLayout) itemView.findViewById(R.id.map_address_rl);
        }
    }

    public void setmData(List<PoiItem> mData) {
        this.mData = mData;
    }
}
