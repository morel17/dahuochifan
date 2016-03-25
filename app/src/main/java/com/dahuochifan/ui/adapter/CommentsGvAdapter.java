package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dahuochifan.R;
import com.dahuochifan.bean.DhComments;
import com.dahuochifan.utils.PreviewLoader;

/**
 * Created by admin on 2015/10/29.
 */
public class CommentsGvAdapter extends RootAdapter<DhComments>{
    private Context context;

    public CommentsGvAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_comments, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.comments_iv=(ImageView)v.findViewById(R.id.comments_iv);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, int position, DhComments object) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(!TextUtils.isEmpty(object.getImgPath())){
            PreviewLoader.loadImage("file://"+object.getImgPath(),viewHolder.comments_iv);
        }else{
            viewHolder.comments_iv.setImageResource(R.mipmap.comment_bg);
        }
    }
    public class ViewHolder {
        private ImageView comments_iv;
    }
}
