package com.dahuochifan.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.utils.MainTools;

public class MainGvAdapter2 extends RootAdapter<TagObj> {
	private List<String> liststr = new ArrayList<String>();
	private Context context;
	public MainGvAdapter2(Context context, List<String> liststr) {
		super(context);
		this.liststr = liststr;
		this.context = context;
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.item_main_gv, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.prov_tv = (TextView) v.findViewById(R.id.prov_tv);
		v.setTag(holder);
		return v;
	}

	@Override
	protected void bindView(View view, int position, final TagObj object) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.prov_tv.setText(object.getName());
		for (int i = 0; i < liststr.size(); i++) {
			if (object.getName().equals(liststr.get(i))) {
				object.setIsselect(true);
			}
		}
		holder.prov_tv.setSelected(object.isIsselect());
		holder.prov_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.prov_tv.isSelected()) {
					holder.prov_tv.setSelected(false);
					object.setIsselect(false);
					liststr.clear();
				} else {
					liststr.clear();
					int num = 0;
					for (int i = 0; i < getmObjects().size(); i++) {
						if (getmObjects().get(i).isIsselect()) {
							num++;
						}
					}
					if (num <= 4) {
						holder.prov_tv.setSelected(true);
						object.setIsselect(true);
					} else {
						MainTools.ShowToast(context, "最多选5个标签");
					}
				}
			}
		});
	}
	class ViewHolder {
		private TextView prov_tv;
	}

}
