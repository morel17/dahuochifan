package com.dahuochifan.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.utils.MainTools;

public class MainGvAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private List<TagObj> list = new ArrayList<TagObj>();
	private List<String> listtemp = new ArrayList<String>();
	public MainGvAdapter(Context context, List<String> listtemp, List<TagObj> listtag) {
		super();
		this.context = context;
		this.listtemp = listtemp;
		inflater = LayoutInflater.from(context);
		this.list = listtag;
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
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_main_gv, parent, false);
			holder.prov_tv = (TextView) convertView.findViewById(R.id.prov_tv);
			convertView.setTag(holder);
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < listtemp.size(); j++) {
					if (list.get(i).getName().equals(listtemp.get(j))) {
						list.get(i).setIsselect(true);
					}
				}
			}
		}
		holder.prov_tv.setText(list.get(position).getName());
		holder.prov_tv.setSelected(list.get(position).isIsselect());
		holder.prov_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.prov_tv.isSelected()) {
					holder.prov_tv.setSelected(false);
					list.get(position).setIsselect(false);
					listtemp.clear();
				} else {
					listtemp.clear();
					int num = 0;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).isIsselect()) {
							num++;
						}
					}
					if (num <= 5) {
						holder.prov_tv.setSelected(true);
						list.get(position).setIsselect(true);
					} else {
						MainTools.ShowToast(context, "最多显示6个");
					}
				}
			}
		});
		return convertView;
	}
	class ViewHolder {
		private TextView prov_tv;
	}
	public List<TagObj> getList() {
		return list;
	}

	public void setList(List<TagObj> list) {
		this.list = list;
	}

}
