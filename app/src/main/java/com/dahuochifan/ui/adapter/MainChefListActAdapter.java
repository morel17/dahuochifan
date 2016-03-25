package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.cheflist.ChefActs;
import com.dahuochifan.utils.PreviewLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morel on 2015/11/30.
 * 主界面列表内部活动列表的Adapter
 */
public class MainChefListActAdapter extends RecyclerView.Adapter<MainChefListActAdapter.ViewHolder> {
    private LayoutInflater inflate;
    private Context context;
    private List<ChefActs> acts;
    private TopCuisineItemListener listenerZ;

    public MainChefListActAdapter(LayoutInflater inflate, Context context) {
        this.inflate = inflate;
        this.context = context;
        acts = new ArrayList<>();
    }

    @Override
    public MainChefListActAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_cheflist_acts, parent, false), listenerZ);
        return holder;
    }

    @Override
    public void onBindViewHolder(MainChefListActAdapter.ViewHolder holder, int position) {
        ChefActs chefAct = acts.get(position);
        PreviewLoader.loadImage(chefAct.getIconurl(), holder.discount_others);
        holder.discount_remark.setText(chefAct.getRemark());
    }

    @Override
    public int getItemCount() {
        return acts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView discount_others;
        private TextView discount_remark;
        TopCuisineItemListener listenerx;

        public ViewHolder(View itemView, TopCuisineItemListener listener) {
            super(itemView);
            discount_others = (ImageView) itemView.findViewById(R.id.discount_others);
            discount_remark = (TextView) itemView.findViewById(R.id.discount_remark);
            this.listenerx = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenerx != null) {
                listenerx.onItemClick(v, getAdapterPosition());
            }
        }
    }
    public void setOnItemClickListener(TopCuisineItemListener listener) {
        this.listenerZ = listener;
    }
    public void setActs(List<ChefActs> acts) {
        this.acts = acts;
    }
}
