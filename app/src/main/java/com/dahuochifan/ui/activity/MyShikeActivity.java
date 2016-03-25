package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.custom.ShikeInfo;
import com.dahuochifan.core.requestdata.custom.ShikeData;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.ui.adapter.MyShikeAdapterTemp;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class MyShikeActivity extends AppCompatActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout back_rl;
	private TextView title_tv;
	private PullToRefreshListView listView;
	// 初始页数
	private int page = 1;
	// 总页数
	private int totalPage = 1;
	// 页数size
	private int pageSize = 8;
	private Gson gson;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private MyShikeAdapterTemp adapter;
	private TextView cooklist_refresh_tv;
	private List<ShikeInfo> list = new ArrayList<ShikeInfo>();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE2 :
					listView.onRefreshComplete();
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
		setContentView(R.layout.activity_my_shike);
		EventBus.getDefault().register(this);
		initviews();
		initData();
		btn_listener();
	}
	public void onEventMainThread(FirstEvent event) {
		if (event.getMsg().equals("status")) {
			if (adapter != null) {
				page = 1;
				getShikeOne(true);
			}
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		page = 1;
		getShikeOne(true);
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		doPrepareRefresh(refreshView);
		if (MainTools.isNetworkAvailable(MyShikeActivity.this)) {
			page = 1;
			getShikeOne(true);
		} else {
			doInNetUnuseful();
		}
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(MyShikeActivity.this)) {
			if (page > 1 && page <= totalPage) {
				getShikeOne(false);
			} else {
				doInTheEnd();
			}
		} else {
			doInNetUnuseful();
		}
	}
	public void doInNetUnuseful() {
		MainTools.ShowToast(MyShikeActivity.this, R.string.check_internet);
		doCloseRefresh();
	}
	public void doInTheEnd() {
		MainTools.ShowToast(MyShikeActivity.this, "已经到底啦");
		doCloseRefresh();
	}
	public void doCloseRefresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE2, 1000);
			}
		}).start();
	}
	public void doPrepareRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(MyShikeActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}
	private void getShikeOne(boolean isRefresh) {
//		String json = gson.toJson(ParamData.data.getShikeObj(MyConstant.user.getUserids(), MyConstant.user.getToken(), "Android", page, pageSize));
//		params.put("param", json);
//		getDataOne(params, isRefresh);
	}
	private void getDataOne(RequestParams params2, final boolean isRefresh) {
		client.post(MyConstant.URL_GETSHIKELIST, params2, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if(BuildConfig.LEO_DEBUG)L.e(responseString);
				cooklist_refresh_tv.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				ShikeData data = new ShikeData();
				if (data.getstatus(responseString, MyShikeActivity.this, gson) == 1) {
					totalPage = data.getObj().getPage().getTotalPage();
					if (!isRefresh) {
						if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
							list.addAll(data.getObj().getPage().getList());
						}
					} else {
						if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
							list = data.getObj().getPage().getList();
						}
					}
					adapter.clear();
					adapter.addAll(list);
					page++;
					adapter.notifyDataSetChanged();
					EventBus.getDefault().post(new FirstEvent("MyShike"));
				}
				listView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				if (adapter == null) {
					cooklist_refresh_tv.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
				}
				MainTools.ShowToast(MyShikeActivity.this, R.string.interneterror);
				doCloseRefresh();
			}
			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}
	private void initData() {
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		// params.prepare(MyShikeActivity.this, params);
	}

	private void btn_listener() {
		back_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		cooklist_refresh_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				page = 1;
				getShikeOne(true);
			}
		});
	}
	private void initviews() {
		back_rl = (RelativeLayout) findViewById(R.id.back_rl);
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText("我的食客");
		cooklist_refresh_tv = (TextView) findViewById(R.id.cooklist_refresh_tv);
		listView = (PullToRefreshListView) findViewById(R.id.shikelistview);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		listView.setPullLabel("上拉加载", Mode.PULL_FROM_END);
		listView.setReleaseLabel("松开加载", Mode.PULL_FROM_END);
		listView.setRefreshingLabel("正在加载", Mode.PULL_FROM_END);
		gson = new Gson();
		adapter = new MyShikeAdapterTemp(MyShikeActivity.this);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
