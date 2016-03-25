package com.dahuochifan.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.interfaces.UnitPriceItemListener;
import com.dahuochifan.model.cookbook.CBCookBook;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morel on 2016/1/6.
 */
public class UnitPriceAdapter extends RecyclerView.Adapter<UnitPriceAdapter.ViewHolder> {
    private LayoutInflater inflate;
    private UnitPriceItemListener listenerZ;
    private List<CBCookBook> acts;
    private int showType, hideType;

    public UnitPriceAdapter(LayoutInflater inflate) {
        this.inflate = inflate;
        acts = new ArrayList<>();
        showType=0;
        hideType=1;
    }
    @Override
    public int getItemViewType(int position) {
        CBCookBook obj = acts.get(position);
        if(obj.isopen()){
            return showType;
        }else{
            return hideType;
        }
    }
    @Override
    public UnitPriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == showType) {
            ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_unitprice, parent, false), listenerZ);
            return holder;
        } else {
            ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.blank_layout, parent, false), listenerZ);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(UnitPriceAdapter.ViewHolder holder, int position) {
        CBCookBook unitObj = acts.get(position);
        if (getItemViewType(position) == showType) {
            holder.price_tv.setText("￥ " + unitObj.getPrices());
            holder.price_type_tv.setText(unitObj.getName());
            holder.price_select_iv.setSelected(unitObj.isSelected());
            holder.unit_main_rl.setVisibility(unitObj.isopen()?View.VISIBLE:View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return acts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView price_type_tv, price_tv;
        private ImageView price_select_iv;
        private UnitPriceItemListener listenerx;
        private PercentRelativeLayout unit_main_rl;

        public ViewHolder(View itemView, UnitPriceItemListener listenerZ) {
            super(itemView);
            price_type_tv = (TextView) itemView.findViewById(R.id.price_type_tv);
            price_tv = (TextView) itemView.findViewById(R.id.price_tv);
            price_select_iv = (ImageView) itemView.findViewById(R.id.price_select_iv);
            unit_main_rl=(PercentRelativeLayout)itemView.findViewById(R.id.unit_main_rl);
            this.listenerx = listenerZ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenerx != null) {
                listenerx.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(UnitPriceItemListener listener) {
        this.listenerZ = listener;
    }

    public void setActs(List<CBCookBook> acts) {
        this.acts = acts;
    }
}
