package com.dahuochifan.ui.adapter;

import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import de.greenrobot.event.EventBus;

public class MainGvAdapter3 extends RootAdapter<TagObj> {
	private Context context;
	private RequestParams params;
	private boolean canChange;
	public MainGvAdapter3(Context context) {
		super(context);
		this.context = context;
		params = new RequestParams();
		canChange = true;
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
	protected void bindView(View view, final int position, final TagObj object) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		if(BuildConfig.LEO_DEBUG)L.e("object.getName()" + object.getName());
		holder.prov_tv.setText(object.getName());
		holder.prov_tv.setSelected(object.getChecked().equals("1"));
		holder.prov_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.prov_tv.isSelected()) {
					if (canChange) {
						changeTag(holder.prov_tv, object.getTagids(), position);
					} else {
						MainTools.ShowToast(context, "您的操作过频");
					}
				} else {
					if (canChange) {
						int testNum = 0;
						for (int i = 0; i < getmObjects().size(); i++) {
							if (getmObjects().get(i).getChecked().equals("1")) {
								testNum++;
							}
						}
						if (testNum < 6) {
							changeTag(holder.prov_tv, object.getTagids(), position);
						} else {
							MainTools.ShowToast(context, "您选择的标签不能超过6个");
						}
					} else {
						MainTools.ShowToast(context, "您的操作过频");
					}
				}
			}
		});
	}
	private void changeTag(final TextView prov_tv, String tagid, final int position) {
		params = ParamData.getInstance().getTagAddObj(tagid);
		client.post(MyConstant.URL_TAGS_ADD, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				canChange = false;
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject object = new JSONObject(arg2);
					String tag = object.getString("tag");
					MainTools.ShowToast(context, tag);
					int resultcode = object.getInt("resultcode");
					if (resultcode == 1) {
						getmObjects().get(position).setChecked("1");
						prov_tv.setSelected(true);
						notifyDataSetChanged();
						EventBus.getDefault().post(new FirstEvent("TAGS"));
					} else if (resultcode == 2) {
						getmObjects().get(position).setChecked("0");
						prov_tv.setSelected(false);
						notifyDataSetChanged();
						EventBus.getDefault().post(new FirstEvent("TAGS"));
					} else {

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				canChange = true;
				super.onFinish();
			}
		});
	}
	class ViewHolder {
		private TextView prov_tv;
	}
	

}
