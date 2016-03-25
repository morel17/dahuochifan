package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.widget.ListView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.model.comment.CommentsInfo;
import com.dahuochifan.requestdata.CommentsData;
import com.dahuochifan.ui.adapter.CommentsDetailAdapter;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommentsChefDetailActivity extends BaseActivity implements OnRefreshListener2<RecyclerView> {
	private PullToRefreshRecyclerView listview;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private List<CommentsInfo> listx = new ArrayList<>();
	private CommentsDetailAdapter adapter;
	private CookBookLevel obj;
	// 初始页数
	private int page = 1;
	// 总页数
	private int totalPage = 1;
	// 页数size
	private int pageSize = 8;
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
		obj = (CookBookLevel) getIntent().getSerializableExtra("obj");
		initviews();
		page = 1;
		getData(true);
	}
	private void initviews() {

//		String[] tagstr = obj.getTags().split(",");
//		for (int i = 0; i < tagstr.length; i++) {
//			if (i <= 5) {
//				tag_tvs[i].setVisibility(View.VISIBLE);
//				tag_tvs[i].setText(tagstr[i].replace("非常", ""));
//			}
//		}
//		min_price_tv = (TextView) findViewById(R.id.min_price_tv);
//		min_person_tv = (TextView) findViewById(R.id.min_person_tv);
//		min_spend_tv = (TextView) findViewById(R.id.min_spend_tv);
//		step_size_tv = (TextView) findViewById(R.id.step_size_tv);
//		min_price_tv.setText((int) obj.getPrice() + "");
//		min_person_tv.setText(obj.getMaxnum() + "");
//		min_spend_tv.setText((int) obj.getMinspending() + "");
//		step_size_tv.setText(obj.getStep() + "");
//		comments_number_tv = (TextView) findViewById(R.id.comments_number_tv);
//		comments_number_tv.setText(obj.getCommentnum() + "评论");
		adapter = new CommentsDetailAdapter(CommentsChefDetailActivity.this, listx);
		if (obj.getCommentnum() != 0) {
			adapter.setStar(obj.getTotalscore() / obj.getCommentnum() * 0.5f);
		} else {
			adapter.setStar(0f);
		}
		adapter.setAvatar(MyConstant.user.getAvatar());
		adapter.setNickname(MyConstant.user.getNickname());
		adapter.setContent_num(obj.getCommentnum()+"评论");
		listview = (PullToRefreshRecyclerView) findViewById(R.id.recycler);
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		// params.prepare(this, params);
		gson = new Gson();


		listview.setMode(Mode.BOTH);
		listview.setOnRefreshListener(this);


		// 设置下拉刷新文本
		listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
		listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
		listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		// 设置上拉刷新文本
		listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
		listview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
		listview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		LinearLayoutManager manager = new LinearLayoutManager(CommentsChefDetailActivity.this, LinearLayoutManager.VERTICAL, false);
		listview.setLayoutManager(manager);
		listview.setAdapter(adapter);
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
		if (MainTools.isNetworkAvailable(CommentsChefDetailActivity.this)) {
			page = 1;
			getData(true);
		} else {
			doInNetUnuseful();
		}
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
		if (MainTools.isNetworkAvailable(CommentsChefDetailActivity.this)) {
			if (page > 1 && page <= totalPage) {
				getData(false);
			} else {
				doInTheEnd();
			}
		} else {
			doInNetUnuseful();
		}
	}

	private void getData(final boolean isrefresh) {
//		params = ParamData.getInstance().getCommentObj(obj.getCbids(), page, pageSize);
		client.post(MyConstant.URL_GETCOMMENTSLIST, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (BuildConfig.LEO_DEBUG)
					L.e(responseString);
				CommentsData data = new CommentsData();
				if (data.dealData(responseString, CommentsChefDetailActivity.this, gson) == 1) {
					totalPage = data.getAll().getPage().getTotalPage();
					if (!isrefresh) {
						if (data.getAll().getPage().getList() != null && data.getAll().getPage().getList().size() > 0) {
							listx.addAll(data.getAll().getPage().getList());
						}
					} else {
						if (data.getAll().getPage().getList() != null && data.getAll().getPage().getList().size() > 0) {
							listx = data.getAll().getPage().getList();
						}
					}
					adapter.setList(listx);
					adapter.notifyDataSetChanged();
					page++;
				}else{
					if(data.getAll()!=null&&!TextUtils.isEmpty(data.getAll().getTag())){
						showTipDialog(CommentsChefDetailActivity.this,data.getAll().getTag(),data.getAll().getResultcode());
					}else{
						showTipDialog(CommentsChefDetailActivity.this,"重新登录",data.getAll().getResultcode());
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(CommentsChefDetailActivity.this, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				listview.onRefreshComplete();
			}
		});
	}

	public void doInNetUnuseful() {
		MainTools.ShowToast(CommentsChefDetailActivity.this, R.string.check_internet);
		doCloseRefresh();
	}
	public void doInTheEnd() {
		MainTools.ShowToast(CommentsChefDetailActivity.this, "已经到底啦");
		doCloseRefresh();
	}
	public void doCloseRefresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE1, 1000);
			}
		}).start();
	}
	public void doPrepareRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(CommentsChefDetailActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
				| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_comments_chef_detail;
	}

	@Override
	protected String initToolbarTitle() {
		return "评论";
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
