package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.cookbook.CBTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morel on 2015/12/10.
 * LunchTimeAdapter
 */
public class LunchPopAdapter extends RecyclerView.Adapter<LunchPopAdapter.ViewHolder> {
    private List<CBTime> mData;
    private LayoutInflater inflate;
    private Context context;
    private TopCuisineItemListener listenerZ;

    public LunchPopAdapter(Context context) {
        this.context = context;
        inflate = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    @Override
    public LunchPopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_time_pop, parent, false), listenerZ);
        return holder;
    }

    @Override
    public void onBindViewHolder(LunchPopAdapter.ViewHolder holder, int position) {
        holder.tv.setText(mData.get(position).getTime());
        if (position != 0) {
            holder.tv.setEnabled(mData.get(position).isValid());
//            holder.tv.setTextColor(ContextCompat.getColor(context, R.color.pop_item_tv_color_selsector));
            holder.tv.setBackgroundResource(R.drawable.pop_item_color_selsector);
        } else {
            holder.tv.setEnabled(false);
            holder.tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
            holder.tv.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv;
        TopCuisineItemListener listenerx;

        public ViewHolder(View itemView, TopCuisineItemListener listener) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.time_tv);
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

    public void setOnItemClickListener(TopCuisineItemListener listenerZ) {
        this.listenerZ = listenerZ;
    }

    public void setmData(List<CBTime> mData) {
        this.mData = mData;
    }

    public List<CBTime> getmData() {
        return mData;
    }
}
