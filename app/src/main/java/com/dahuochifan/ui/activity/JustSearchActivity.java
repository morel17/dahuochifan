package com.dahuochifan.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookData;
import com.dahuochifan.ui.adapter.RecyclerViewPreviewAdapter;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class JustSearchActivity extends BaseActivity implements OnRefreshListener2<RecyclerView> {
	private EditText search_et;
	private RelativeLayout search_area;
	private PullToRefreshRecyclerView listview;
	private ProgressBar pb;
	private ImageView back_bg;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private int page = 1;// 初始页数
	private int pageSize = 8;// 页数size
	private String searchstr;
	private List<ChefList> list = new ArrayList<ChefList>();
	private RecyclerViewPreviewAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE1 :
					listview.onRefreshComplete();
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
		initViews();
		init();
		listener();
	}
	private void initViews() {
		search_et = (EditText) findViewById(R.id.search_et);
		search_area = (RelativeLayout) findViewById(R.id.search_area);
		listview = (PullToRefreshRecyclerView) findViewById(R.id.myxlistview);
		pb = (ProgressBar) findViewById(R.id.pb);
		back_bg = (ImageView) findViewById(R.id.back_bg);
	}
	private void init() {
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		gson = new Gson();
		listview.setMode(Mode.BOTH);
		listview.setOnRefreshListener(this);
		// listview.setPullLabel("上拉加载", Mode.PULL_FROM_END);
		// listview.setReleaseLabel("松开加载", Mode.PULL_FROM_END);
		// listview.setRefreshingLabel("正在加载", Mode.PULL_FROM_END);
		// 设置下拉刷新文本
		listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
		listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
		listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		// 设置上拉刷新文本
		listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
		listview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
		listview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		adapter = new RecyclerViewPreviewAdapter(this, list, "");
		LinearLayoutManager manager = new LinearLayoutManager(JustSearchActivity.this, LinearLayoutManager.VERTICAL, false);
		listview.setLayoutManager(manager);
		listview.setAdapter(adapter);
	}
	private void listener() {
		search_area.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchstr = search_et.getText().toString();
				if (TextUtils.isEmpty(searchstr)) {
					MainTools.ShowToast(JustSearchActivity.this, "搜索内容不能为空");
					return;
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
				page = 1;
				getData(true);
			}
		});
		// listview.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// MainTools.ShowToast(JustSearchActivity.this, position + "eheheh");
		// }
		// });
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
		// doPrepareRefresh(refreshView);
		if (MainTools.isNetworkAvailable(JustSearchActivity.this)) {
			page = 1;
			getData(true);
		} else {
			doInNetUnuseful();
		}
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
		if (MainTools.isNetworkAvailable(JustSearchActivity.this)) {
			getData(false);
		} else {
			doInNetUnuseful();
		}
	}

	private void getData(final boolean isRefresh) {
		params = ParamData.getInstance().searchcookObj(JustSearchActivity.this, searchstr, page, pageSize,  3);

		client.post(MyConstant.URL_SEARCH, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				pb.setVisibility(View.VISIBLE);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				CookData cookdata = new CookData();
				if (cookdata.dealData(responseString, JustSearchActivity.this, gson) == 1) {
					if (!isRefresh) {
						if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
							list.addAll(cookdata.getAll().getPage().getList());
						} else {
							pb.setVisibility(View.GONE);
						}
					} else {
						if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
							list = cookdata.getAll().getPage().getList();
						} else {
							pb.setVisibility(View.GONE);
						}

					}
					adapter.setList(list);
					adapter.notifyDataSetChanged();
					page++;
				} else {
					MainTools.ShowToast(JustSearchActivity.this, cookdata.getAll().getTag());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(JustSearchActivity.this, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				listview.onRefreshComplete();
				pb.setVisibility(View.GONE);
			}
		});

	}
	public void doInNetUnuseful() {
		MainTools.ShowToast(JustSearchActivity.this, R.string.check_internet);
		doCloseRefresh();
	}
	public void doInTheEnd() {
		doCloseRefresh();
	}
	public void doCloseRefresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE1, 500);
			}
		}).start();
	}
	// public void doPrepareRefresh(PullToRefreshBase<ListView> refreshView) {
	// String label = DateUtils.formatDateTime(JustSearchActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
	// | DateUtils.FORMAT_ABBREV_ALL);
	// refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	// }
	@Override
	protected int getLayoutView() {
		return R.layout.activity_search;
	}

	@Override
	protected String initToolbarTitle() {
		return "搜索主厨";
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
