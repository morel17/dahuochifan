package com.dahuochifan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyEatenFragment extends BaseFragment implements OnRefreshListener2<ListView> {
	private View view;
	private PullToRefreshListView myEatenListView;
	private MyAsyncHttpClient client;
	private RequestParams param;
	private Gson gson;
	private SharedPreferences spf;
	// 初始页数
	private int page = 1;
	// 总页数
	// private int totalPage = 1;
	// 页数size
	private int pageSize = 4;
//	private CookListAdapter cooklistadapter;
	private double longitude = 0;
	private double latitude = 0;
	private List<ChefList> list = new ArrayList<ChefList>();
	private ImageView bgiv;
	private ProgressBar pb;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE1 :
					myEatenListView.onRefreshComplete();
					break;

				default :
					break;
			}
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_tab_mycollect, container, false);
		spf = SharedPreferenceUtil.initSharedPerence().init(mActivity, MyConstant.APP_SPF_NAME);
		init();
		initView();
//		cooklistadapter = new CookListAdapter(mActivity, 1, "");
//		myEatenListView.setAdapter(cooklistadapter);
		return view;
	}
	private void init() {
		pb = (ProgressBar) view.findViewById(R.id.pb);
		bgiv = (ImageView) view.findViewById(R.id.bgiv);
		bgiv.setBackgroundResource(R.drawable.eaten_back);
		bgiv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String curProv;
				curProv = SharedPreferenceUtil.initSharedPerence().getGDProvince(spf);
//				Intent intent = new Intent(mActivity, CookListActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("curprov", curProv);
//				bundle.putString("location", "");
////				bundle.putString("longitude", SharedPreferenceUtil.initSharedPerence().getLongitude(spf));
////				bundle.putString("latitude", SharedPreferenceUtil.initSharedPerence().getLatitude(spf));
//				intent.putExtras(bundle);
//				mActivity.startActivity(intent);
//				mActivity.finish();
			}
		});
		gson = new Gson();
		client = new MyAsyncHttpClient();
		param = new RequestParams();
		// param.prepare(mActivity, param);
//		if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getLongitude(spf))) {
//			longitude = Double.parseDouble(SharedPreferenceUtil.initSharedPerence().getLongitude(spf));
//		} else {
//			longitude = 121.371927;
//		}
//		if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getLatitude(spf))) {
//			latitude = Double.parseDouble(SharedPreferenceUtil.initSharedPerence().getLatitude(spf));
//		} else {
//			latitude = 31.105911;
//		}
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		page = 1;
		getMainData(true);
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(mActivity)) {
			page = 1;
			getMainData(true);
		} else {
			doInNetUnuseful();
		}
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(mActivity)) {
			if (page > 1) {
				getMainData(false);
			} else {
				doInTheEnd();
			}
		} else {
			doInNetUnuseful();
		}
	}
	private void getMainData(boolean isRefresh) {
		param = ParamData.getInstance().getCollectListObj(mActivity, page, pageSize, 3, longitude, latitude);
		post_refresh(param, isRefresh);
	}
	private void post_refresh(RequestParams param2, final boolean isRefresh) {
		client.post(MyConstant.URL_MYEATEN, param2, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				if (list != null && list.size() == 0) {
					pb.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				CookData cookdata = new CookData();
				if (cookdata.dealData(responseString, mActivity, gson) == 1) {

					if (!isRefresh) {
						if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
							list.addAll(cookdata.getAll().getPage().getList());
						}
					} else {
						if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
							list = cookdata.getAll().getPage().getList();
						} else {
							bgiv.setVisibility(View.VISIBLE);
						}
					}
//					cooklistadapter.clear();
//					cooklistadapter.addAll(list);
//					cooklistadapter.notifyDataSetChanged();
//					page++;
				} else {
					MainTools.ShowToast(mActivity, cookdata.getAll().getTag());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				MainTools.ShowToast(mActivity, R.string.interneterror);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				myEatenListView.onRefreshComplete();
				pb.setVisibility(View.GONE);
			}
		});
	}
	public void doInNetUnuseful() {
		MainTools.ShowToast(mActivity, R.string.check_internet);
		doCloseRefresh();
	}
	public void doInTheEnd() {
		MainTools.ShowToast(mActivity, "已经到底啦");
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
}
