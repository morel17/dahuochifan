package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dahuochifan.R;
import com.dahuochifan.bean.DhComments;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.utils.PreviewLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morel on 2015/11/30.
 * 评论列表的图片适配
 */
public class ChefDetailComPicAdapter extends RecyclerView.Adapter<ChefDetailComPicAdapter.ViewHolder> {
    private LayoutInflater inflate;
    private Context context;
    private List<DhComments> list;
    private TopCuisineItemListener listenerZ;

    public ChefDetailComPicAdapter(LayoutInflater inflate, Context context) {
        this.inflate = inflate;
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public ChefDetailComPicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.item_comments, parent, false), listenerZ);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChefDetailComPicAdapter.ViewHolder holder, int position) {
        DhComments object = list.get(position);
        if (!TextUtils.isEmpty(object.getImgPath())) {
            PreviewLoader.loadImage(object.getImgPath() + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_19_80)
                    + "/h/" + context.getResources().getDimensionPixelOffset(R.dimen.width_12_80) + "/q/" + 65, holder.comments_iv);
        } else {
            holder.comments_iv.setImageResource(R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView comments_iv;
        TopCuisineItemListener listenerx;

        public ViewHolder(View itemView, TopCuisineItemListener listener) {
            super(itemView);
            comments_iv = (ImageView) itemView.findViewById(R.id.comments_iv);
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

    public void setPicList(List<DhComments> list) {
        this.list = list;
    }
    public void setOnItemClickListener(TopCuisineItemListener listener) {
        this.listenerZ = listener;
    }
}
