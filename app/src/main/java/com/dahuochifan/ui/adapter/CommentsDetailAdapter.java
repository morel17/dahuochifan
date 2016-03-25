package com.dahuochifan.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.PhotoViewVPActivity;
import com.dahuochifan.bean.DhComments;
import com.dahuochifan.model.comment.CommentsInfo;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.PreviewLoader;
import com.dahuochifan.ui.views.CircleImageView;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class CommentsDetailAdapter extends RecyclerView.Adapter<CommentsDetailAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<CommentsInfo> list;
    private static final int TYPE_INFO = 0x000;
    private static final int TYPE_IV = 0x0001;

    private String nickname, avatar;
    private String content_num;
    private float star;

    public CommentsDetailAdapter(Activity commentsDetailActivity, List<CommentsInfo> listx) {
        this.context = commentsDetailActivity;
        this.list = listx;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getItemViewType(viewType) == TYPE_IV) {
            View itemLayout = inflater.inflate(R.layout.item_comments_detail, null);
            return new ViewHolder(itemLayout, TYPE_IV);
        } else {
            View itemLayout = inflater.inflate(R.layout.adapter_comments, null);
            return new ViewHolder(itemLayout, TYPE_INFO);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_INFO) {
            final List<DhComments> commentsList = new ArrayList<>();
            CustomerHead.loadImage(list.get(position - 1).getAvatar(), holder.userimg);
            holder.nickname_tv.setText(list.get(position - 1).getNickname());
            holder.time_tv.setText(list.get(position - 1).getCreatetime().substring(0, list.get(position - 1).getCreatetime().length() - 3));
            holder.content_tv.setText(list.get(position - 1).getContent());
            holder.myratingbar.setRating(list.get(position - 1).getScore() * 0.5f);
            if (list.get(position - 1).getPic() != null && list.get(position - 1).getPic().size() > 0) {
                holder.pic_rl.setVisibility(View.VISIBLE);
                for (int i = 0; i < list.get(position - 1).getPic().size(); i++) {
                    DhComments comments = new DhComments();
                    comments.setImgPathBig(list.get(position - 1).getPic().get(i).getUrl());
                    comments.setImgPath(list.get(position - 1).getPic().get(i).getUrl());
                    commentsList.add(comments);
                }
                switch (commentsList.size()) {
                    case 1:
                        loadView(commentsList, 0, holder.comments_iv1);
                        holder.comments_iv1.setVisibility(View.VISIBLE);
                        holder.comments_iv2.setVisibility(View.GONE);
                        holder.comments_iv3.setVisibility(View.GONE);
                        break;
                    case 2:
                        loadView(commentsList, 0, holder.comments_iv1);
                        loadView(commentsList, 1, holder.comments_iv2);
                        holder.comments_iv1.setVisibility(View.VISIBLE);
                        holder.comments_iv2.setVisibility(View.VISIBLE);
                        holder.comments_iv3.setVisibility(View.GONE);
                        break;
                    default:
                        loadView(commentsList, 0, holder.comments_iv1);
                        loadView(commentsList, 1, holder.comments_iv2);
                        loadView(commentsList, 2, holder.comments_iv3);
                        holder.comments_iv1.setVisibility(View.VISIBLE);
                        holder.comments_iv2.setVisibility(View.VISIBLE);
                        holder.comments_iv3.setVisibility(View.VISIBLE);
                        break;
                }

            } else {
                holder.pic_rl.setVisibility(View.GONE);
            }
            holder.comments_iv1.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    showPic(commentsList, 0);
                }
            });
            holder.comments_iv2.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    showPic(commentsList, 1);
                }
            });
            holder.comments_iv3.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    showPic(commentsList, 2);
                }
            });
        } else {
            CookerHead.loadImage(avatar, holder.chef_head);
            holder.chef_tv.setText(nickname);
            holder.chef_bar.setRating(star);
            holder.chef_comments_tv.setText(content_num);
        }
    }

    private void loadView(List<DhComments> commentsList, int position, ImageView comments_iv) {
        String urlImg = commentsList.get(position).getImgPath() + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_19_80)
                + "/h/" + context.getResources().getDimensionPixelOffset(R.dimen.width_12_80) + "/q/" + 65;
        if (comments_iv.getTag() == null || !comments_iv.getTag().equals(urlImg)) {
            PreviewLoader.loadImage(urlImg, comments_iv);
            comments_iv.setTag(urlImg);
        }
    }

    private void showPic(List<DhComments> commentsList, int index) {
        if (!TextUtils.isEmpty(commentsList.get(index).getImgPath())) {
            Intent intent = new Intent(context, PhotoViewVPActivity.class);
            ArrayList<String> listStr = new ArrayList<String>();
            for (int i = 0; i < commentsList.size(); i++) {
                if (!TextUtils.isEmpty(commentsList.get(i).getImgPathBig())) {
                    listStr.add(commentsList.get(i).getImgPathBig());
                }
            }
            intent.putExtra("item", index);
            intent.putStringArrayListExtra("imgList", listStr);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_IV;
        } else {
            return TYPE_INFO;
        }

    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public void setList(List<CommentsInfo> list) {
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userimg;
        private TextView nickname_tv;
        private TextView time_tv;
        private RatingBar myratingbar;
        private TextView content_tv;
        private PercentRelativeLayout pic_rl;
        private ImageView comments_iv1, comments_iv2, comments_iv3;
        private CircleImageView chef_head;
        private RatingBar chef_bar;
        private TextView chef_tv, chef_comments_tv;

        public ViewHolder(View itemView, int itemType) {
            super(itemView);
            if (itemType == TYPE_IV) {
                userimg = (CircleImageView) itemView.findViewById(R.id.userimg);
                nickname_tv = (TextView) itemView.findViewById(R.id.nickname_tv);
                time_tv = (TextView) itemView.findViewById(R.id.time_tv);
                content_tv = (TextView) itemView.findViewById(R.id.content_tvme);
                myratingbar = (RatingBar) itemView.findViewById(R.id.myratingbar);
                pic_rl = (PercentRelativeLayout) itemView.findViewById(R.id.pic_rl);
                comments_iv1 = (ImageView) itemView.findViewById(R.id.comments_iv1);
                comments_iv2 = (ImageView) itemView.findViewById(R.id.comments_iv2);
                comments_iv3 = (ImageView) itemView.findViewById(R.id.comments_iv3);
            } else {
                chef_head = (CircleImageView) itemView.findViewById(R.id.chef_head);
                chef_bar = (RatingBar) itemView.findViewById(R.id.myratingbar);
                chef_tv = (TextView) itemView.findViewById(R.id.chef_tv);
                chef_comments_tv = (TextView) itemView.findViewById(R.id.chef_comment);
            }
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setContent_num(String content_num) {
        this.content_num = content_num;
    }

    public void setStar(float star) {
        this.star = star;
    }
}
