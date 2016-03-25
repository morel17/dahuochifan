package com.dahuochifan.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.CommentsDetailActivity;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.CookBookLevel;

public class MyCaipuDetailAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private List<CookBookLevel> list;
	
	public MyCaipuDetailAdapter(Context context, List<CookBookLevel> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView!=null){
			holder=(ViewHolder)convertView.getTag();
		}else{
			convertView = inflater.inflate(R.layout.item_cook_mycaipu, parent, false);
			holder = new ViewHolder();
			holder.eat_num_tv=(TextView)convertView.findViewById(R.id.eat_num_tv);
			holder.comments_tv=(TextView)convertView.findViewById(R.id.comments_tv);
			holder.price_tv=(TextView)convertView.findViewById(R.id.price_tv);
			holder.level_tv=(TextView)convertView.findViewById(R.id.level_tv);
			convertView.setTag(holder);
		}
		holder.level_tv.setText(list.get(position).getName());
		holder.comments_tv.setText(list.get(position).getCommentnum() + "评论");
		holder.price_tv.setText(list.get(position).getPrice()+"");
		holder.comments_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,CommentsDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("cbids",list.get(position).getCbids());
				bundle.putString("nickname",MyConstant.user.getNickname());
				bundle.putString("naviland", "非常" +MyConstant.user.getHomeprov().replace("省", "").replace("市", ""));
				bundle.putString("avatar", MyConstant.user.getAvatar());
				bundle.putString("status", list.get(position).getStatus());
				bundle.putString("name", list.get(position).getName());
				bundle.putString("comments", list.get(position).getCommentnum()+"");
				bundle.putString("price", list.get(position).getPrice()+"");
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	class ViewHolder{
		private TextView comments_tv,price_tv,level_tv,eat_num_tv;
	}

}
