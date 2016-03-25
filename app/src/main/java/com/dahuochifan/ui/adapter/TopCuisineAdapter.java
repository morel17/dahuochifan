package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.TagObj;

import java.util.List;

/**
 * Created by Morel on 2015/11/23.
 * 顶部菜系的RecyclerView
 */
public class TopCuisineAdapter extends RecyclerView.Adapter<TopCuisineAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<TagObj> mDatas;
    private TopCuisineItemListener listenerZ;

    public TopCuisineAdapter(Context context, List<TagObj> mDatas) {
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView cuisine_tv;
        TopCuisineItemListener listenerx;

        public ViewHolder(View view, TopCuisineItemListener listener) {
            super(view);
            this.listenerx = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listenerx != null) {
                listenerx.onItemClick(v, getAdapterPosition());
            }
        }
    }

    @Override
    public TopCuisineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_cuisine,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listenerZ);

        viewHolder.cuisine_tv = (TextView) view.findViewById(R.id.cuisine_tv);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TopCuisineAdapter.ViewHolder holder, int position) {
        holder.cuisine_tv.setText(mDatas.get(position).getName());
        holder.cuisine_tv.setSelected(mDatas.get(position).isIsselect());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(TopCuisineItemListener listener) {
        this.listenerZ = listener;
    }

    public void setmDatas(List<TagObj> mDatas) {
        this.mDatas = mDatas;
    }
}
