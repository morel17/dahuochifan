package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.bean.DhComments;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.comment.CommentPic;
import com.dahuochifan.model.comment.CommentsInfo;
import com.dahuochifan.ui.activity.PhotoViewVPActivity;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.CustomerHead;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morel on 2015/11/30.
 * 主厨详情评论列表
 */
public class ChefDetailCommentAdapter extends RecyclerView.Adapter<ChefDetailCommentAdapter.ViewHolder> {
    private LayoutInflater inflate;
    private Context context;
    List<CommentsInfo> commentsInfos;
    int height = 0;
    private RecyclerView comment_cyc;

    public ChefDetailCommentAdapter(Context context, RecyclerView comment_cyc) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        commentsInfos = new ArrayList<>();
        this.comment_cyc = comment_cyc;
    }

    @Override
    public ChefDetailCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(inflate.inflate(R.layout.adapter_chefdetail_comments, parent, false));
        height = 0;
        return holder;
    }

    @Override
    public void onBindViewHolder(final ChefDetailCommentAdapter.ViewHolder holder, final int position) {
        final CommentsInfo object = commentsInfos.get(position);
        CustomerHead.loadImage(object.getAvatar(), holder.circle_iv1);
        holder.nickname_tv1.setText(object.getNickname());
        holder.content_tv1.setText(object.getContent());
        if (object.getPic() != null && object.getPic().size() > 0) {
            holder.pic_rl1.setVisibility(View.VISIBLE);
            if (object.getPic().size() <= 3) {
                initCyc(object.getPic().size(), object.getPic(), holder);
            } else {
                initCyc(3, object.getPic(), holder);
            }
        } else {
            holder.pic_rl1.setVisibility(View.GONE);
        }

        holder.content_tv1.post(new Runnable() {
            @Override
            public void run() {
                int heightTemp;
                if (object.getPic() != null && object.getPic().size() > 0) {
                    heightTemp = context.getResources().getDimensionPixelOffset(R.dimen.height_16_80);
                } else {
                    heightTemp = context.getResources().getDimensionPixelOffset(R.dimen.height_8_80);
                }
                height += (holder.content_tv1.getLineCount() - 1) * holder.content_tv1.getLineHeight() + heightTemp;
                if (position == commentsInfos.size() - 1) {
                    height += context.getResources().getDimensionPixelOffset(R.dimen.dh_10);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_80_80),
                            height);
                    comment_cyc.setLayoutParams(params);
                    height=0;
                }
            }
        });
    }

    private void initCyc(int size, List<CommentPic> picL, ViewHolder holder) {
        final List<DhComments> listT = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DhComments dhComments = new DhComments();
            dhComments.setImgPath(picL.get(i).getUrl());
            dhComments.setImgPathBig(picL.get(i).getUrl());
            listT.add(dhComments);
        }
        holder.picAdapter.setPicList(listT);
        holder.picAdapter.notifyDataSetChanged();
        holder.picAdapter.setOnItemClickListener(new TopCuisineItemListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Intent intent = new Intent(context, PhotoViewVPActivity.class);
                ArrayList<String> listStr = new ArrayList<>();
                for (int i = 0; i < listT.size(); i++) {
                    if (!TextUtils.isEmpty(listT.get(i).getImgPathBig())) {
                        listStr.add(listT.get(i).getImgPathBig());
                    }
                }
                intent.putStringArrayListExtra("imgList", listStr);
                intent.putExtra("item", postion);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circle_iv1;
        private TextView nickname_tv1, content_tv1;
        private PercentRelativeLayout pic_rl1;
        private RecyclerView comment_pic_cyc;
        private ChefDetailComPicAdapter picAdapter;
        private GridLayoutManager layoutManager;

        public ViewHolder(View itemView) {
            super(itemView);
            circle_iv1 = (CircleImageView) itemView.findViewById(R.id.circle_iv1);
            nickname_tv1 = (TextView) itemView.findViewById(R.id.nickname_tv1);
            content_tv1 = (TextView) itemView.findViewById(R.id.content_tv1);
            pic_rl1 = (PercentRelativeLayout) itemView.findViewById(R.id.pic_rl1);
            comment_pic_cyc = (RecyclerView) itemView.findViewById(R.id.comment_pic_cyc);
            picAdapter = new ChefDetailComPicAdapter(inflate, context);
            layoutManager = new GridLayoutManager(context, 3);
            comment_pic_cyc.setLayoutManager(layoutManager);
            comment_pic_cyc.setItemAnimator(new DefaultItemAnimator());
            comment_pic_cyc.setAdapter(picAdapter);
        }
    }

    public void setCommentsInfos(List<CommentsInfo> commentsInfos) {
        this.commentsInfos = commentsInfos;
    }

    public List<CommentsInfo> getCommentsInfos() {
        return commentsInfos;
    }

    public int getHeight() {
        return height;
    }
}
