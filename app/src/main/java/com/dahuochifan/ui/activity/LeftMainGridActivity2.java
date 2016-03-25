package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.TagObj;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.TagData;
import com.dahuochifan.ui.adapter.MainGvAdapter3;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class LeftMainGridActivity2 extends BaseActivity {
	private GridView main_gv;
	private MainGvAdapter3 adapter;
	private Context context;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private SharedPreferences spf;
	private Editor eidtor;
	private ProgressBar pb;
	private List<TagObj> taglist = new ArrayList<TagObj>();
	private SwipeRefreshLayout swipe;
	private SweetAlertDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initView();
		getData();
	}
	private void getData() {
		params = ParamData.getInstance().postTagObj_new();
		client.post(MyConstant.URL_CBTAG_LIST, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				TagData data = new TagData();
				if (data.dealData(arg2, LeftMainGridActivity2.this, gson) == 1) {
					if (data.getAll().getList() != null && data.getAll().getList().size() > 0) {
						taglist = data.getAll().getList();
						adapter.setmObjects(taglist);
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
		spf = SharedPreferenceUtil.initSharedPerence().init(LeftMainGridActivity2.this, MyConstant.APP_SPF_NAME);
		eidtor = spf.edit();
	}
	private void initView() {
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		// params.prepare(LeftMainGridActivity.this, params);
		gson = new Gson();
		main_gv = (GridView) findViewById(R.id.main_gv);
		adapter = new MainGvAdapter3(LeftMainGridActivity2.this);
		pb = (ProgressBar) findViewById(R.id.pb);
		main_gv.setAdapter(adapter);
		context = this;
		swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
		swipe.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		swipe.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getData();
			}
		});
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_leftmaingrid;
	}

	@Override
	protected String initToolbarTitle() {
		return "我喜欢的口味";
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
