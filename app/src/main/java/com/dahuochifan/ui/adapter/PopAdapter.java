package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;

public class PopAdapter extends RootAdapter<String>{

	public PopAdapter(Context context) {
		super(context);
	}
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v=inflater.inflate(R.layout.item_pop_tag, parent,false);
		ViewHolder viewHolder=new ViewHolder();
		viewHolder.pop_tag_tv=(TextView)v.findViewById(R.id.pop_tag_tv);
		viewHolder.pop_tag_iv=(ImageView)v.findViewById(R.id.pop_tag_iv);
		v.setTag(viewHolder);
		return v;
	}

	@Override
	protected void bindView(View view, int position, String object) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.pop_tag_tv.setText(object);
		if(position==getCount()-1){
			viewHolder.pop_tag_iv.setVisibility(View.GONE);
		}else{
			viewHolder.pop_tag_iv.setVisibility(View.VISIBLE);
		}
	}
	public class ViewHolder{
		private TextView pop_tag_tv;
		private ImageView pop_tag_iv;
	}

}
