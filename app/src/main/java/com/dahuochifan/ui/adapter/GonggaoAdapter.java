package com.dahuochifan.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.GonggaoActivity;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.NotifyObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * @author admin
 *
 */
public class GonggaoAdapter extends RootAdapter<NotifyObj> {
	private RequestParams params;
	private Gson gson;
	private GonggaoActivity context;
	private SweetAlertDialog pDialog;
	private List<NotifyObj> list = new ArrayList<NotifyObj>();
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1 :
					pDialog.dismiss();
					break;
				case 0 :
					pDialog.dismiss();
					break;
				default :
					break;
			}
		};
	};
	public GonggaoAdapter(GonggaoActivity context) {
		super(context);
		this.context = context;
		params = new RequestParams();
		gson = new Gson();
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.item_gonggao, parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.tv = (TextView) v.findViewById(R.id.gonggao_tv);
		viewHolder.iv = (ImageView) v.findViewById(R.id.trash_iv);
		v.setTag(viewHolder);
		return v;
	}

	@Override
	protected void bindView(View view, final int position, final NotifyObj object) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.tv.setText(object.getContent());
		viewHolder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("是否删除该条公告").setCancelText("取消")
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
									deleteFunction(object.getCnids(), position);
								} else {
									MainTools.ShowToast(context, R.string.interneterror);
								}
							}
						}).show();
			}
		});
	}
	private void deleteFunction(String cnids, final int position) {
		params = ParamData.getInstance().delNotifyObj(cnids);
		client.post(MyConstant.URL_CHEFNOTIFY_DEL, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
				pDialog.setCancelable(false);
				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
				pDialog.setTitleText("正在加载").setConfirmText("").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
				pDialog.show();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj = new JSONObject(arg2);
					String tag = jobj.getString("tag");
					int resultcode = jobj.getInt("resultcode");
					if (resultcode == 1) {
						pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
						getmObjects().remove(position);
						notifyDataSetChanged();
						mHandler.sendEmptyMessageDelayed(1, 1500);
					} else {
						pDialog.setTitleText(tag).setConfirmText("确定").changeAlertType(SweetAlertDialog.WARNING_TYPE);
						mHandler.sendEmptyMessageDelayed(1, 1500);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				pDialog.setTitleText("网络不给力").setConfirmText("确定").changeAlertType(SweetAlertDialog.ERROR_TYPE);
				mHandler.sendEmptyMessageDelayed(0, 1500);
			}
		});
	}
	private class ViewHolder {
		private TextView tv;
		private ImageView iv;
	}

}
