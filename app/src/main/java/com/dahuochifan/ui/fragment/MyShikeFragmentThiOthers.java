package com.dahuochifan.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.MyShikeAdapterTemp3;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.custom.ShikeInfo;
import com.dahuochifan.core.requestdata.custom.ShikeData;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
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

public class MyShikeFragmentThiOthers extends BaseFragment implements OnRefreshListener2<ListView> {
	View view;
	private PullToRefreshListView myEatenListView;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private List<ShikeInfo> list = new ArrayList<ShikeInfo>();
	private MyShikeAdapterTemp3 adapter;
	private ImageView bgiv;
	private ProgressBar pb;
	public static MyShikeFragmentThiOthers instance;
	// 初始页数
	private int page = 1;
	// 页数size
	private int pageSize = 12;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE2 :
					myEatenListView.onRefreshComplete();
					break;
				default :
					break;
			}
		};
	};
    public static MyShikeFragmentThiOthers newInstance(){
    	if (instance == null) {
			instance = new MyShikeFragmentThiOthers();
		}
		return instance;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_shike_complete, container, false);
		initViews();
		init();
		page = 1;
		getShikeOne(true);
		return view;
	}
	private void init() {
		gson = new Gson();
		client = new MyAsyncHttpClient();
		params = new RequestParams();
	}
	private void initViews() {
		myEatenListView = (PullToRefreshListView) view.findViewById(R.id.collectTablistview);
		pb = (ProgressBar) view.findViewById(R.id.pb);
		bgiv=(ImageView)view.findViewById(R.id.bgiv);
		bgiv.setBackgroundResource(R.drawable.shike_back);
		myEatenListView.setMode(Mode.BOTH);
		myEatenListView.setOnRefreshListener(this);

		// 设置下拉刷新文本
		myEatenListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
		myEatenListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
		myEatenListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		// 设置上拉刷新文本
		myEatenListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
		myEatenListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
		myEatenListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		adapter = new MyShikeAdapterTemp3(mActivity);
		myEatenListView.setAdapter(adapter);
	}

	public void getShikeOne(boolean isRefresh) {
		if(isRefresh){
			page=1;
		}
		params = ParamData.getInstance().getShikeObj(page, pageSize, "2");
		getDataOne(params, isRefresh);
	}
	private void getDataOne(RequestParams params2, final boolean isRefresh) {
		client.setTimeout(5000);
		client.post(MyConstant.URL_GETSHIKELIST, params2, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				if (list != null && list.size() == 0) {
					pb.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if(BuildConfig.LEO_DEBUG)L.e(responseString);
				ShikeData data = new ShikeData();
				if (data.getstatus(responseString, mActivity, gson) == 1) {
					if (!isRefresh) {
						if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
							list.addAll(data.getObj().getPage().getList());
							bgiv.setVisibility(View.GONE);
						} else {
							MainTools.ShowToast(mActivity, "已经到底啦");
						}
					} else {
						if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
							list = data.getObj().getPage().getList();
							bgiv.setVisibility(View.GONE);
						}else{
							list.clear();
							bgiv.setVisibility(View.VISIBLE);
						}
					}
					adapter.clear();
					adapter.addAll(list);
					page++;
					adapter.notifyDataSetChanged();
					EventBus.getDefault().post(new FirstEvent("MyShike"));
				}else{
					if (data.getObj() != null && !TextUtils.isEmpty(data.getObj().getTag())) {
						showTipDialog(data.getObj().getTag(),data.getObj().getResultcode());
					} else {
						showTipDialog("请重新登录",data.getObj().getResultcode());
					}
				}
				myEatenListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(mActivity, R.string.interneterror);
				doCloseRefresh();
			}
			@Override
			public void onFinish() {
				super.onFinish();
				myEatenListView.onRefreshComplete();
				pb.setVisibility(View.GONE);
			}
		});
	}

	public void doCloseRefresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE2, 1000);
			}
		}).start();
	}
	public void doInNetUnuseful() {
		MainTools.ShowToast(mActivity, R.string.check_internet);
		doCloseRefresh();
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(mActivity)) {
			page = 1;
			getShikeOne(true);
		} else {
			doInNetUnuseful();
		}
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(mActivity)) {
			getShikeOne(false);
		} else {
			doInNetUnuseful();
		}
	}
}
