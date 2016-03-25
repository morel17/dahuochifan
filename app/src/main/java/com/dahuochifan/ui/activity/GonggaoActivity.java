package com.dahuochifan.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.GonggaoAdapter;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.NotifyAll;
import com.dahuochifan.model.NotifyObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.NotifyData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.ui.views.dialog.MorelDialog;
import com.dahuochifan.ui.views.dialog.MorelDialog.MorelDialogListener;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class GonggaoActivity extends BaseActivity {
	private ListView listview;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private MorelDialog dialog;
	private Context context;
	private SweetAlertDialog pDialog;
	private GonggaoAdapter adapter;
	private SwipeRefreshLayout swipe;
	private FloatingActionButton add_iv;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		gson = new Gson();
		context = this;
		swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
		listview = (ListView) findViewById(R.id.gonggao_listview);
		add_iv = (FloatingActionButton) findViewById(R.id.add_iv);
		adapter = new GonggaoAdapter(GonggaoActivity.this);
		listview.setAdapter(adapter);
		swipe.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		swipe.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getMainData();
			}
		});
		add_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog = new MorelDialog(GonggaoActivity.this, R.style.mylogoutstyle, "添加公告", "", 20, new MorelDialogListener() {

					@Override
					public void onClick(View view, EditText et) {
						switch (view.getId()) {
							case R.id.ok_tv :
								if (TextUtils.isEmpty(et.getText().toString())) {
									MainTools.ShowToast(context, "输入内容不能为空");
									break;
								} else {
									String content = et.getText().toString();
									params = ParamData.getInstance().addNofifyObj(content);
									if (MainTools.isNetworkAvailable(context)) {
										AddNotifyFunction();
									} else {
										MainTools.ShowToast(context, R.string.interneterror);
									}
								}

								break;
							case R.id.cancel_tv :
								dialog.dismiss();
								break;
							default :
								break;
						}
					}
				});
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.show();
			}
		});
		getMainData();
	}
	public void getMainData() {
		params = ParamData.getInstance().getNofifyObj();
		client.post(MyConstant.URL_CHEFNOTIFY_LIST, params, new TextHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(GonggaoActivity.this, R.string.interneterror);

			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				if(BuildConfig.LEO_DEBUG)L.e("arg2" + arg2);
				NotifyData data = new NotifyData();
				if (data.dealData(arg2, GonggaoActivity.this, gson) == 1) {
					NotifyAll all = data.getAll();
					if (all != null && all.getList() != null && all.getList().size() > 0) {
						List<NotifyObj> list = all.getList();
						adapter.clear();
						adapter.addAll(list);
						adapter.notifyDataSetChanged();
					}
				}else{
					if(data.getAll()!=null&&!TextUtils.isEmpty(data.getAll().getTag())){
						showTipDialog(GonggaoActivity.this,data.getAll().getTag(),data.getAll().getResultcode());
					}else{
						showTipDialog(GonggaoActivity.this,"重新登录",data.getAll().getResultcode());
					}

				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				swipe.setRefreshing(false);
			}

		});
	}
	private void AddNotifyFunction() {
		client.post(MyConstant.URL_CHEFNOTIFY_ADD, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在提交");
				pDialog.setCancelable(false);
				pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
				pDialog.show();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj = new JSONObject(arg2);
					int resultcode = jobj.getInt("resultcode");
					String tag = jobj.getString("tag");
					if (resultcode == 1) {
						dialog.dismiss();
						getMainData();
						pDialog.setTitleText(tag).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
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
				pDialog.setTitleText("网络不给力").changeAlertType(SweetAlertDialog.ERROR_TYPE);
				mHandler.sendEmptyMessageDelayed(0, 1500);
			}

		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_gonggao;
	}

	@Override
	protected String initToolbarTitle() {
		return "公告";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				finish();
			default :
				return super.onOptionsItemSelected(item);
		}
	}

}
