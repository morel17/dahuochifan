package com.dahuochifan.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;

public class MyPayAllFragment extends Fragment implements OnRefreshListener2<ListView>{
	private View view;
	private PullToRefreshListView myEatenListView;
	private MyAsyncHttpClient client;
	private RequestParams param;
	private Gson gson;
	// 初始页数
	private int page = 1;
	// 页数size
	private int pageSize = 4;
	private ProgressBar pb;
	private ImageView bgiv;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_payall, container, false);
		init();
		initView();
		return view;
	}
	private void init() {
		pb = (ProgressBar) view.findViewById(R.id.pb);
		bgiv = (ImageView) view.findViewById(R.id.bgiv);
		bgiv.setBackgroundResource(R.drawable.eaten_back);
		gson = new Gson();
		client = new MyAsyncHttpClient();
		param = new RequestParams();
	}
	private void initView() {
		myEatenListView = (PullToRefreshListView) view.findViewById(R.id.collectTablistview);
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
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}
}
