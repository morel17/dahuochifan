package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.activity.CommentsNewActivity;
import com.dahuochifan.ui.activity.OrderDetailActivity;
import com.dahuochifan.ui.activity.TimeLineActivity;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.CookerHead;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.ui.views.CircleImageView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class MyOrderAdapterTemp2 extends RootAdapter<OrderInfo> {
	private Context context;
	private Gson gson;
	private RequestParams params;
	private SweetAlertDialog pd;
	private int index;
	static class MyHandler extends Handler {
		WeakReference<SweetAlertDialog> wrReferences;
		MyHandler(SweetAlertDialog wrReference) {
			this.wrReferences = new WeakReference<>(wrReference);
		}
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE6 :
					wrReferences.get().dismiss();
					break;
				case MyConstant.MYHANDLER_CODE4 :
					wrReferences.get().dismiss();
					break;
				default :
					break;
			}
		}
	}
	public MyOrderAdapterTemp2(FragmentActivity context,int index) {
		super(context);
		this.context = context;
		gson = new Gson();
		params = new RequestParams();
		this.index=index;
	}
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.item_myorder_view, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.chef_avatar_iv = (CircleImageView) v.findViewById(R.id.chef_avatar_iv);
		viewHolder.chef_nickname_tv = (TextView) v.findViewById(R.id.chef_nickname_tv);
		viewHolder.tag_tv = (TextView) v.findViewById(R.id.tag_tv);
		viewHolder.person_tv = (TextView) v.findViewById(R.id.person_tv);
		viewHolder.day_number_tv = (TextView) v.findViewById(R.id.day_number_tv);
		viewHolder.mid_tv = (TextView) v.findViewById(R.id.mid_tv);
		viewHolder.night_tv = (TextView) v.findViewById(R.id.night_tv);
		viewHolder.all_price_tv = (TextView) v.findViewById(R.id.all_price_tv);
		viewHolder.comment_tv = (TextView) v.findViewById(R.id.comment_tv);
		viewHolder.status_tv = (TextView) v.findViewById(R.id.status_tv);
		viewHolder.top_rl = (RelativeLayout) v.findViewById(R.id.top_rl);
		viewHolder.level_tv = (TextView) v.findViewById(R.id.level_tv);
		viewHolder.timeline_tv=(TextView)v.findViewById(R.id.timeline_tv);
		viewHolder.detail_tv=(TextView)v.findViewById(R.id.detail_tv);
		v.setTag(viewHolder);
		return v;
	}

	@Override
	protected void bindView(View view, final int position, final OrderInfo object) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		CookerHead.loadImage(object.getAvatar(), viewHolder.chef_avatar_iv);
		viewHolder.chef_nickname_tv.setText(object.getNickname());
		viewHolder.person_tv.setText(object.getEatennum() + "份");
		viewHolder.level_tv.setText(object.getCbname());
		viewHolder.tag_tv.setText(object.getTag());
		if(object.isgrapcoupon()&&object.getGrapcoupon()!=null){
			viewHolder.detail_tv.setText("发红包");
			viewHolder.detail_tv.setTextColor(ContextCompat.getColor(context,R.color.maincolor_new));
		}else{
			viewHolder.detail_tv.setText("详情");
			viewHolder.detail_tv.setTextColor(ContextCompat.getColor(context,R.color.text_dark));
		}
		if(object.getTrack()!=null){
			if(TextUtils.isEmpty(object.getTrack().getStatus())||!object.getTrack().getStatus().equals("0")){
				viewHolder.timeline_tv.setVisibility(View.GONE);
			}else{
				viewHolder.timeline_tv.setVisibility(View.VISIBLE);
			}
		} else{
			viewHolder.timeline_tv.setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(object.getLunchtime())) {
			viewHolder.mid_tv.setVisibility(View.GONE);
		} else {
			viewHolder.mid_tv.setVisibility(View.VISIBLE);
		}
		if (TextUtils.isEmpty(object.getDinnertime())) {
			viewHolder.night_tv.setVisibility(View.GONE);
		} else {
			viewHolder.night_tv.setVisibility(View.VISIBLE);
		}
		viewHolder.day_number_tv.setText(MainTools.changeMD(object.getEatenwhen()));
		viewHolder.all_price_tv.setText("￥" + MainTools.getDoubleValue(object.getTotal(),1));
		switch (object.getOlstatus()) {
			case "1" :
				viewHolder.status_tv.setText("已付款");
				viewHolder.status_tv.setTextColor(context.getResources().getColor(R.color.maincolor_new));
				viewHolder.comment_tv.setVisibility(View.VISIBLE);
				if (object.isIscomment()) {
					viewHolder.comment_tv.setText("删除订单");
				} else {
					viewHolder.comment_tv.setText("给个评论");
				}
				break;
			default :

				break;
		}
		viewHolder.top_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, OrderDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("obj", object);
				bundle.putInt("index", index);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		viewHolder.comment_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (viewHolder.comment_tv.getText().toString().equals("给个评论")) {
					Intent intent = new Intent(context, CommentsNewActivity.class);
					intent.putExtra("obj", object);
					context.startActivity(intent);
				} else if (viewHolder.comment_tv.getText().toString().equals("取消订单")) {
					new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否取消该条订单").setCancelText("取消")
							.setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.dismiss();
								}
							}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.dismiss();
									if (MainTools.isNetworkAvailable(context)) {
										postStatus(object.getOlids(), "5", position, object);
									} else {
										MainTools.ShowToast(context, R.string.interneterror);
									}
								}
							}).show();
				} 
			}
		});
		viewHolder.timeline_tv.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View v) {
				if(object.getTrack().getStatus().equals("0")){
					Intent intent =new Intent(context,TimeLineActivity.class);
					if(!TextUtils.isEmpty(object.getTrack().getUrl())){
						intent.putExtra("trackurl",object.getTrack().getUrl());
						context.startActivity(intent);
					}
				}
			}
		});
	}
	private void postStatus(final String olids, final String status, final int position, final OrderInfo object) {
		params = ParamData.getInstance().postStatusObj(olids, status,object.getOlversion());
		client.post(MyConstant.URL_FOLLOWORDER, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				if (status.equals("5")) {
					pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在取消订单");
				} else if (status.equals("6")) {
					pd = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在删除订单");
				}
				pd.setCancelable(false);
				pd.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
				pd.show();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if(BuildConfig.LEO_DEBUG)L.e("delete"+responseString);
				try {
					JSONObject jobj = new JSONObject(responseString);
					int request = jobj.getInt("resultcode");
					if (request == 1) {
						if (status.equals("5")) {
							EventBus.getDefault().post(new FirstEvent("MyOrder"));
							getmObjects().remove(position);
							notifyDataSetChanged();
						} else if (status.equals("6")) {
							getmObjects().remove(position);
							notifyDataSetChanged();
						}
						pd.setTitleText(jobj.getString("tag")).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
						Message msg=Message.obtain();
						msg.arg1=position;
						msg.what=MyConstant.MYHANDLER_CODE6;
						MyHandler handler=new MyHandler(pd);
						handler.sendMessageDelayed(msg, 1500);
					} else {
						pd.setTitleText(jobj.getString("tag")).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
					}
					EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_THREE));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				pd.setTitleText("网络不给力").changeAlertType(SweetAlertDialog.ERROR_TYPE);
				MyHandler handler=new MyHandler(pd);
				handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE4, 1500);
			}
		});
	}
	private class ViewHolder {
		private CircleImageView chef_avatar_iv;
		private TextView chef_nickname_tv;
		private TextView tag_tv;
		private TextView person_tv;
		private TextView day_number_tv;
		private TextView mid_tv;
		private TextView night_tv;
		private TextView all_price_tv;
		private TextView comment_tv;
		private TextView status_tv;
		private RelativeLayout top_rl;
		private TextView level_tv;
		private TextView timeline_tv;
		private TextView detail_tv;
	}

}
