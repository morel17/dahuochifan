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
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2015/8/27.
 */
public class MapCycAdapter extends RecyclerView.Adapter<MapCycAdapter.ViewHolder> {
    private LayoutInflater inflate;
    private List<PoiItem> mData;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private AppCompatActivity context;
    public MapCycAdapter(AppCompatActivity context) {
        this.inflate=LayoutInflater.from(context);
        mData=new ArrayList<>();
        spf= SharedPreferenceUtil.initSharedPerence().init(context, MyConstant.APP_SPF_NAME);
        editor=spf.edit();
        this.context=context;
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
                if(mData.get(position).getProvinceName().equals("上海市")){
                    SharedPreferenceUtil.initSharedPerence().putGDProvince(editor, mData.get(position).getProvinceName().replace("省", "").replace("市", ""));
                    SharedPreferenceUtil.initSharedPerence().putGDCity(editor, mData.get(position).getCityName());
                    SharedPreferenceUtil.initSharedPerence().putGDDistrict(editor, mData.get(position).getAdName());
                    SharedPreferenceUtil.initSharedPerence().putPoiName(editor, mData.get(position).toString());
                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, mData.get(position).getLatLonPoint().getLatitude() + "");
                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, mData.get(position).getLatLonPoint().getLongitude() + "");
                    editor.commit();
                    EventBus.getDefault().post(new FirstEvent("DhLocation", mData.get(position).toString()));
                    EventBus.getDefault().post(new FirstEvent("CURPROV"));
                    EventBus.getDefault().post(new FirstEvent("TAGS"));
                    EventBus.getDefault().post(new FirstEvent("HOMEPROV"));
                    context.finish();
                }else{
                    MainTools.ShowToast(context,"搭伙吃饭限上海地区");
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
            map_tv=(TextView)itemView.findViewById(R.id.map_tv);
            map_tv2=(TextView)itemView.findViewById(R.id.map_tv2);
            address_rl=(PercentLinearLayout)itemView.findViewById(R.id.map_address_rl);
        }
    }

    public void setmData(List<PoiItem> mData) {
        this.mData = mData;
    }
}
