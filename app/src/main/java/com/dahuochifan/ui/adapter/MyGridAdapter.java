package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dahuochifan.R;
import com.dahuochifan.utils.LunboLoader;

public class MyGridAdapter extends BaseAdapter {
	private String[] provinceStr;
	private Context context;
	private LayoutInflater inflater;
	public MyGridAdapter(Context context, String[] provinceStr) {
		this.provinceStr = provinceStr;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return provinceStr.length;
	}

	@Override
	public Object getItem(int position) {
		return provinceStr[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_mygrid, parent, false);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.iv.setScaleType(ImageView.ScaleType.FIT_XY);

			convertView.setTag(holder);
		}
		LinearLayout.LayoutParams layoutParams;
		if (provinceStr.length == 2) {
			layoutParams = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_35_80), context.getResources()
					.getDimensionPixelOffset(R.dimen.width_21_80));
			LunboLoader.loadImage(provinceStr[position] + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_35_80) + "/h/"
					+ context.getResources().getDimensionPixelOffset(R.dimen.width_21_80) + "/q/" + 65, holder.iv);
		} else if (provinceStr.length == 1) {
			layoutParams = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_70_80), context.getResources()
					.getDimensionPixelOffset(R.dimen.width_42_80));
			LunboLoader.loadImage(provinceStr[position] + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
					+ context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + 65, holder.iv);
		} else {
			layoutParams = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_22_80), context.getResources()
					.getDimensionPixelOffset(R.dimen.width_13_80));
			LunboLoader.loadImage(provinceStr[position] + "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_22_80) + "/h/"
					+ context.getResources().getDimensionPixelOffset(R.dimen.width_13_80) + "/q/" + 65, holder.iv);
		}
		
		holder.iv.setLayoutParams(layoutParams);
		return convertView;
	}
	public void setProvinceStr(String[] provinceStr) {
		this.provinceStr = provinceStr;
	}
	class ViewHolder {
		private ImageView iv;

	}

}
