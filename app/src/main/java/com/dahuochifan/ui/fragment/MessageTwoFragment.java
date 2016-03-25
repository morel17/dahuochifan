package com.dahuochifan.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.MessageDetailActivity;
import com.dahuochifan.ui.adapter.DhMessageAdapter;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.message.MessageDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.message.DhMessageData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MessageTwoFragment extends LazyFragment implements OnRefreshListener2<ListView>{
	private MessageTwoFragment instance = null;
	public  MessageTwoFragment newInstance() {
		if (instance == null) {
			instance = new MessageTwoFragment();
		}
		Bundle arguments = new Bundle();

		instance.setArguments(arguments);
		return instance;
	}

	private View view;
	private PullToRefreshListView listView;
	private ImageView bgiv;
	private ProgressBar pb;
	// 初始页数
	private static int page = 1;
	// 页数size
	private int pageSize = 12;
	private Gson gson;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private DhMessageAdapter adapter;
	private List<MessageDetail> list = new ArrayList<MessageDetail>();

	private TabLayout tabLayout;
	private ViewPager viewpager;
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE3 :
					listView.onRefreshComplete();
					break;
				default :
					break;
			}
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_messagecenter, container, false);
		initViews();
		isPrepared = true;
		lazyLoad();
		return view;
	}
	private void initViews() {
		gson = new Gson();
		client = new MyAsyncHttpClient();
		params = new RequestParams();
		adapter = new DhMessageAdapter(mActivity);
		// if(type==1){
		// delete_tv.setVisibility(View.GONE);
		// }
		bgiv = (ImageView) view.findViewById(R.id.bgiv);
		bgiv.setBackgroundResource(R.drawable.message_back);
		pb = (ProgressBar)  view.findViewById(R.id.pb);
		listView = (PullToRefreshListView)  view.findViewById(R.id.collectTablistview);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		// 设置下拉刷新文本
		listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
		listView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
		listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		// 设置上拉刷新文本
		listView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
		listView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
		listView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(mActivity, MessageDetailActivity.class);
				i.putExtra("mylist", (Serializable)list.get(position - 1));
				Bundle bundle = new Bundle();
				bundle.putInt("code", 1);
				i.putExtras(bundle);
				startActivity(i);
			}
		});
	}
	public void getMessage(final boolean isRefresh) {
		if (isRefresh)
			page = 1;
		params = ParamData.getInstance().getMessageObj(page, pageSize,"2");
		client.setTimeout(5000);
		client.post(MyConstant.URL_MESSAGE, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				if (list != null && list.size() == 0) {
					pb.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				DhMessageData msgData = new DhMessageData();
				if (msgData.dealData(arg2, mActivity, gson) == 1) {
					if (msgData.getAll() != null && msgData.getAll().getPage().getList() != null && msgData.getAll().getPage().getList().size() > 0) {
						if (isRefresh) {
							list = msgData.getAll().getPage().getList();
						} else {
							list.addAll(msgData.getAll().getPage().getList());
						}
						bgiv.setVisibility(View.GONE);
					} else {
						if (isRefresh) {
							bgiv.setVisibility(View.VISIBLE);
						}
					}
					adapter.setmObjects(list);
					adapter.notifyDataSetChanged();
					page++;
					listView.onRefreshComplete();
					mHasLoadedOnce=true;
				} else {
					if (msgData.getAll() != null && !TextUtils.isEmpty(msgData.getAll().getTag())) {
						showTipDialog(msgData.getAll().getTag(),msgData.getAll().getResultcode());
						//Integer.parseInt
					} else {
						showTipDialog(msgData.getAll().getTag(),msgData.getAll().getResultcode());
					}

				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				MainTools.ShowToast(mActivity, R.string.interneterror);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				listView.onRefreshComplete();
				pb.setVisibility(View.GONE);
			}
		});
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(mActivity)) {
			getMessage(true);
		} else {
			doInNetUnuseful();
		}
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (MainTools.isNetworkAvailable(mActivity)) {
			if (page > 1) {
				getMessage(false);
			} else {
				doCloseRefresh();
			}
		} else {
			doInNetUnuseful();
		}
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
				handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE3, 1000);
			}
		}).start();
	}
	@Override
	public void onDestroy() {
		isPrepared=false;
		mHasLoadedOnce=false;
		list.clear();
		super.onDestroy();
	}
	@Override
	protected void lazyLoad() {
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		page = 1;
		getMessage(true);
	}
}
