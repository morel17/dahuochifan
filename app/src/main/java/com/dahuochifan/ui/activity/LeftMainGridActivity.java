package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.MainGvAdapter;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.TagData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class LeftMainGridActivity extends AppCompatActivity {
	private RelativeLayout back_rl;
	private GridView main_gv;
	private MainGvAdapter adapter;
	private Context context;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private SharedPreferences spf;
	private Editor eidtor;
	private List<String> listtemp = new ArrayList<String>();
	private TextView confirm_tv;
	private ProgressBar pb;
	private List<TagObj> taglist = new ArrayList<TagObj>();
	private SwipeRefreshLayout swipe;
	private SweetAlertDialog pDialog;
	private TextView title_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leftmaingrid);
		initData();
		initView();
		listener();
		getData();
	}
	private void getData() {
		params = ParamData.getInstance().postTagObj("1");
		client.post(MyConstant.URL_CBTAG_LIST, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				TagData data = new TagData();
				if (data.dealData(arg2, LeftMainGridActivity.this, gson) == 1) {
					if (data.getAll().getList() != null && data.getAll().getList().size() > 0) {
						taglist = data.getAll().getList();
						adapter.setList(taglist);
						adapter.notifyDataSetChanged();
					}
				} else {

				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(context, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				swipe.setRefreshing(false);
			}
		});
	}
	private void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		listtemp = (List<String>) bundle.getSerializable("obj");
		spf = SharedPreferenceUtil.initSharedPerence().init(LeftMainGridActivity.this, MyConstant.APP_SPF_NAME);
		eidtor = spf.edit();
	}
	private void listener() {
		back_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				new SweetAlertDialog(LeftMainGridActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("未确认修改是否退出").setCancelText("取消")
//						.setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//							@Override
//							public void onClick(SweetAlertDialog sDialog) {
//								sDialog.dismiss();
//							}
//						}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//							@Override
//							public void onClick(SweetAlertDialog sDialog) {
//								sDialog.dismiss();
//								finish();
//							}
//						}).show();
				finish();
			}
		});
		confirm_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (taglist.size() == 0) {
					MainTools.ShowToast(context, "请下拉重新加载");
					return;
				}
				List<String> listtemp = new ArrayList<String>();
				if (adapter != null) {
					List<TagObj> listx = adapter.getList();
					for (int i = 0; i < listx.size(); i++) {
						if (listx.get(i).isIsselect()) {
							listtemp.add(listx.get(i).getName());
						}
					}
				}
				String prov = "";
				for (int i = 0; i < listtemp.size(); i++) {
					if (i != listtemp.size() - 1) {
						if (i == 0) {
							prov = listtemp.get(i) + ",";
						} else {
							prov += listtemp.get(i) + ",";
						}
					} else {
						prov = prov + listtemp.get(i);
					}
				}
				postUpdateProv(prov, listtemp);
			}
		});
	}
	private void initView() {
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		// params.prepare(LeftMainGridActivity.this, params);
		gson = new Gson();
		back_rl = (RelativeLayout) findViewById(R.id.back_rl);
		main_gv = (GridView) findViewById(R.id.main_gv);
		adapter = new MainGvAdapter(LeftMainGridActivity.this, listtemp, taglist);
		confirm_tv = (TextView) findViewById(R.id.confirm_tv);
		pb = (ProgressBar) findViewById(R.id.pb);
		title_tv=(TextView)findViewById(R.id.title_tv);
		title_tv.setText("标签管理");
		main_gv.setAdapter(adapter);
		context = this;
		swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
		swipe.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		swipe.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getData();
			}
		});
	}
	private void postUpdateProv(final String prov, final List<String> listtemp2) {
		params = ParamData.getInstance().updateProvObj(prov);
		client.post(MyConstant.URL_UPDATEPROV, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				pb.setVisibility(View.VISIBLE);
				confirm_tv.setEnabled(false);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if(BuildConfig.LEO_DEBUG)L.e("responseString" + responseString);
				try {
					JSONObject jobj = new JSONObject(responseString);
					int request = jobj.getInt("resultcode");
					if (request == 1) {
						SharedPreferenceUtil.initSharedPerence().putOtherProv(eidtor, prov);
						eidtor.commit();
						EventBus.getDefault().post(new FirstEvent(listtemp2, "MainGV"));
						finish();
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
				pb.setVisibility(View.GONE);
				confirm_tv.setEnabled(true);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//			new SweetAlertDialog(LeftMainGridActivity.this, SweetAlertDialog.WARNING_TYPE).setContentText("").setTitleText("未确认修改是否退出").setCancelText("取消")
//					.setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//						@Override
//						public void onClick(SweetAlertDialog sDialog) {
//							sDialog.dismiss();
//						}
//					}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//						@Override
//						public void onClick(SweetAlertDialog sDialog) {
//							sDialog.dismiss();
//							finish();
//						}
//					}).show();
			finish();
		}
		return false;
	}
}
