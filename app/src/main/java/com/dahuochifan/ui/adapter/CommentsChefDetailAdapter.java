package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.model.comment.CommentsInfo;
import com.dahuochifan.utils.CustomerHead;
import com.dahuochifan.ui.views.CircleImageView;

public class CommentsChefDetailAdapter extends RootAdapter<CommentsInfo> {

	public CommentsChefDetailAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.item_comments_detail, parent, false);
		ViewHolder holder=new ViewHolder();
		holder.userimg=(CircleImageView)v.findViewById(R.id.userimg);
		holder.nickname_tv=(TextView)v.findViewById(R.id.nickname_tv);
		holder.time_tv=(TextView)v.findViewById(R.id.time_tv);
		holder.content_tv=(TextView)v.findViewById(R.id.content_tvme);
		holder.myratingbar=(RatingBar)v.findViewById(R.id.myratingbar);
		v.setTag(holder);
		return v;
	}

	@Override
	protected void bindView(View view, int position, CommentsInfo object) {
		final ViewHolder holder=(ViewHolder) view.getTag();
		CustomerHead.loadImage(object.getAvatar(), holder.userimg);
		holder.nickname_tv.setText(object.getNickname());
		holder.time_tv.setText(object.getCreatetime());
		holder.content_tv.setText(object.getContent());
//		holder.myratingbar.setRating(object.get);
	}
	public class ViewHolder {
		private CircleImageView userimg;
		private TextView nickname_tv;
		private TextView time_tv;
		private RatingBar myratingbar;
		private TextView content_tv;
	}

}
