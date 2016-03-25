package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.List;

public class MainTabAdapter extends RecyclerView.Adapter<MainTabAdapter.ViewHolder> {
    private List<TagObj> mData;
    private LayoutInflater inflate;
    private Context context;
    private SharedPreferences spf;
    private Editor editor;
    private TopCuisineItemListener listenerZ;

    public MainTabAdapter(List<TagObj> mData, Context context) {
        this.mData = mData;
        this.context = context;
        inflate = LayoutInflater.from(context);
        spf = SharedPreferenceUtil.initSharedPerence().init(context, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv;
        private ImageView iv;
        private PercentRelativeLayout layout;
        TopCuisineItemListener listenerx;

        public ViewHolder(View itemView, TopCuisineItemListener listener) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            layout = (PercentRelativeLayout) itemView.findViewById(R.id.layout);
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

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TagObj obj = mData.get(position);
        holder.tv.setText(obj.getName());
        if (obj.getChecked().equals("1")) {
            holder.iv.setVisibility(View.VISIBLE);
            switch (position) {
                case 0:
                    holder.iv.setBackgroundResource(R.drawable.tabimg1);
                    break;
                case 1:
                    holder.iv.setBackgroundResource(R.drawable.tabimg2);
                    break;
                case 2:
                    holder.iv.setBackgroundResource(R.drawable.tabimg3);
                    break;
                case 3:
                    holder.iv.setBackgroundResource(R.drawable.tabimg4);
                    break;
                case 4:
                    holder.iv.setBackgroundResource(R.drawable.tabimg5);
                    break;
                case 5:
                    holder.iv.setBackgroundResource(R.drawable.tabimg6);
                    break;

                default:
                    break;
            }
        } else {
            holder.iv.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_item_tab, arg0, false), listenerZ);
        return holder;
    }

    public void setList(List<TagObj> mData) {
        this.mData = mData;
    }

    public void setOnItemClickListener(TopCuisineItemListener listenerZ) {
        this.listenerZ = listenerZ;
    }
}
