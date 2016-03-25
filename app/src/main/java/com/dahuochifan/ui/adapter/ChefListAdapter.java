package com.dahuochifan.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.LunboLoader;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.Tools;
import com.dahuochifan.ui.views.CircleImageView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ChefListAdapter extends RootAdapter<ChefList> {

	private boolean cando;
	private boolean cancollect;
	private RequestParams params;
	private Activity context;
	public ChefListAdapter(Activity context) {
		super(context);
		cando = true;
		cancollect = true;
		params = new RequestParams();
		this.context = context;
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.adapter_cheflist, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.chef_pic = (ImageView) v.findViewById(R.id.chef_iv);
		viewHolder.chef_like_rl = (RelativeLayout) v.findViewById(R.id.like_rl);
		viewHolder.chef_collect_rl = (RelativeLayout) v.findViewById(R.id.collect_rl);
		viewHolder.chef_head = (CircleImageView) v.findViewById(R.id.chef_head_iv);
		viewHolder.chef_nickname_tv = (TextView) v.findViewById(R.id.nick_name_tv);
		viewHolder.province_tv = (TextView) v.findViewById(R.id.province_tv);
		viewHolder.chef_eatnum_tv = (TextView) v.findViewById(R.id.eat_num_tv);
		viewHolder.chef_collectnum_tv = (TextView) v.findViewById(R.id.collect_num_tv);
		viewHolder.chef_likenum_tv = (TextView) v.findViewById(R.id.like_num_tv);
		v.setTag(viewHolder);
		return v;
	}

	@Override
	protected void bindView(View view, int position, final ChefList object) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		if (object.getBgs() != null && object.getBgs().size() > 0) {
			LunboLoader.loadImage(object.getBgs().get(0).getUrl()+ "?imageView2/1/w/" + context.getResources().getDimensionPixelOffset(R.dimen.width_70_80) + "/h/"
					+ context.getResources().getDimensionPixelOffset(R.dimen.width_42_80) + "/q/" + MyConstant.QUALITY, viewHolder.chef_pic);
		}
		CookerHead.loadImage(object.getAvatar(), viewHolder.chef_head);
		viewHolder.chef_eatnum_tv.setText(object.getEatennum());
		viewHolder.chef_collectnum_tv.setText(object.getCollectnum());
		viewHolder.chef_likenum_tv.setText(object.getLikenum());
		viewHolder.province_tv.setText("非常" + object.getHomeprov().replace("省", "").replace("市", ""));
		viewHolder.chef_nickname_tv.setText(object.getNickname());

		viewHolder.chef_like_rl.setSelected(object.isIslike());
		viewHolder.chef_collect_rl.setSelected(object.isIscollect());

		viewHolder.chef_like_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isLike = object.isIslike();
				String number = object.getLikenum();
				if (cando) {
					doLike(object, isLike, number, viewHolder.chef_like_rl, viewHolder.chef_likenum_tv);
				}
			}
		});
		viewHolder.chef_collect_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCollect = object.isIscollect();
				if (cancollect) {
					doCollect(object, viewHolder.chef_collect_rl, isCollect, viewHolder.chef_collectnum_tv);
				}
			}
		});

	}

	private void doLike(final ChefList object, final boolean isLike, String number, final RelativeLayout like_ll, final TextView like_tv) {
		Gson gson = new Gson();
		String json;
		if (!object.isIslike()) {
			params = ParamData.getInstance().getLikeObj(object.getChefids(), "1");
		} else {
			params = ParamData.getInstance().getLikeObj(object.getChefids(), "0");
		}

		client.post(MyConstant.URL_MAINTOPLIKE, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				cando = false;
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					JSONObject jobj = new JSONObject(responseString);
					if (jobj.getString("resultcode").equals("1")) {
						int num = Tools.toInteger(object.getLikenum());
						if (isLike) {
							object.setIslike(!isLike);
							like_ll.setSelected(!isLike);
							num--;
						} else {
							object.setIslike(!isLike);
							like_ll.setSelected(!isLike);
							num++;
						}
						object.setLikenum(num + "");
						like_tv.setText(num + "");
					} else {
						MainTools.ShowToast(context, jobj.getString("tag"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				cando = true;
			}
		});
	}
	private void doCollect(final ChefList object, final RelativeLayout collect_ll, final boolean isCollect, final TextView collect_tv) {
		Gson gson = new Gson();
		String json = null;
		if (!isCollect) {
			params = ParamData.getInstance().getLikeObj(object.getChefids(), "1");
		} else {
			params = ParamData.getInstance().getLikeObj(object.getChefids(), "0");
		}

		client.post(MyConstant.URL_MAINTOPCOLLECT, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				cancollect = false;
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					JSONObject jobj = new JSONObject(responseString);
					if (jobj.getString("resultcode").equals("1")) {
						int num = Tools.toInteger(object.getCollectnum());
						if (isCollect) {
							object.setIscollect(!isCollect);
							collect_ll.setSelected(!isCollect);
							num--;
						} else {
							object.setIscollect(!isCollect);
							collect_ll.setSelected(!isCollect);
							num++;
						}
						object.setCollectnum(num + "");
						collect_tv.setText(num + "");
					} else {
						MainTools.ShowToast(context, jobj.getString("tag"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				cancollect = true;
			}
		});

	}
	class ViewHolder {
		private ImageView chef_pic;
		private CircleImageView chef_head;
		private RelativeLayout chef_like_rl;
		private RelativeLayout chef_collect_rl;
		private TextView chef_nickname_tv;
		private TextView province_tv;
		private TextView chef_eatnum_tv;
		private TextView chef_collectnum_tv;
		private TextView chef_likenum_tv;
	}

}
