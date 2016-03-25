package com.dahuochifan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.order.OrderAll;
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.core.requestdata.order.OrderData;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.adapter.MyOrderAdapterTemp2;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyOrderFragmentSec extends LazyFragment implements OnRefreshListener2<ListView> {
    private MyOrderFragmentSec instance = null;
    private View view;
    private PullToRefreshListView listView;
    private List<OrderInfo> list = new ArrayList<OrderInfo>();
    // 初始页数
    private int page = 1;
    // 总页数
    private int totalPage = 1;
    // 页数size
    private int pageSize = 12;
    private Gson gson;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private OrderAll orderall;
    private ImageView bgiv;
    private MyOrderAdapterTemp2 adapter;
    private SharedPreferences spf;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;

    static class MyHandler extends Handler {
        WeakReference<PullToRefreshListView> puReference;
        WeakReference<MyOrderFragmentSec> wrReferences;

        MyHandler(PullToRefreshListView puReference, MyOrderFragmentSec wrReferences) {
            this.puReference = new WeakReference<>(puReference);
            this.wrReferences = new WeakReference<>(wrReferences);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE3:
                    puReference.get().onRefreshComplete();
                    break;
                case MyConstant.MYHANDLER_ERROR:
                    wrReferences.get().changeAlertDialog(MorelAlertDialog.ERROR_TYPE, "网络不给力");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_WARN:
                    wrReferences.get().changeAlertDialog(MorelAlertDialog.WARNING_TYPE, "请求出错");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_SUCCESS:
                    wrReferences.get().changeAlertDialog(MorelAlertDialog.SUCCESS_TYPE, "加载成功");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_WAIT:
                    wrReferences.get().dismissAlertDialog();
                    break;
                default:
                    break;
            }
        }
    }

    public MyOrderFragmentSec newInstance() {
        if (instance == null) {
            instance = new MyOrderFragmentSec();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_mycollect, container, false);
        spf = SharedPreferenceUtil.initSharedPerence().init(mActivity, MyConstant.APP_SPF_NAME);
        initViews();
        initData();
        isPrepared = true;
        lazyLoad();
        return view;
    }

    //	@Override
//	public void onResume() {
//		super.onResume();
//		page = 1;
//		getOrderLoad(true);
//	}
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            page = 1;
            getOrderLoad(true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            if (page > 1 && page <= totalPage) {
                getOrderLoad(false);
            } else {
                doInTheEnd();
            }
        } else {
            doInNetUnuseful();
        }
    }

    private void initData() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        // params.prepare(mActivity, params);
        gson = new Gson();
    }

    private void initViews() {
        bgiv = (ImageView) view.findViewById(R.id.bgiv);
        bgiv.setBackgroundResource(R.drawable.comment_bg);
        listView = (PullToRefreshListView) view.findViewById(R.id.collectTablistview);
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
        adapter = new MyOrderAdapterTemp2(mActivity, 1);
        listView.setAdapter(adapter);

    }

    public void getOrderLoad(final boolean isRefresh) {

        if (isRefresh) page = 1;
        params = ParamData.getInstance().getShikeObj(page, pageSize, "1");
        getDataLoad(params, isRefresh);
    }

    private void getDataLoad(RequestParams params2, final boolean isRefresh) {
        client.setTimeout(5000);
        final MyHandler handler = new MyHandler(listView, MyOrderFragmentSec.this);
        client.post(MyConstant.URL_GETORDERLIST, params2, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (list != null && list.size() == 0) {
                    showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中，请稍等", true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                OrderData data = new OrderData();
                if (data.getstatus(responseString, mActivity, gson) == 1) {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_SUCCESS);
                    totalPage = data.getObj().getPage().getTotalPage();
                    if (!isRefresh) {
                        if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
                            list.addAll(data.getObj().getPage().getList());
                            bgiv.setVisibility(View.GONE);
                        } else {
                            MainTools.ShowToast(mActivity, "已经到底啦");
                        }
                    } else {
                        mHasLoadedOnce = true;
                        if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
                            list = data.getObj().getPage().getList();
                            bgiv.setVisibility(View.GONE);
                        } else {
                            list.clear();
                            bgiv.setVisibility(View.VISIBLE);
                        }
                    }
                    mHasLoadedOnce = true;
                } else {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_WARN);
                    if (data.getObj() != null && !TextUtils.isEmpty(data.getObj().getTag())) {
                        showTipDialog(data.getObj().getTag(), data.getObj().getResultcode());
                    } else {
                        showTipDialog(data.getObj().getTag(), data.getObj().getResultcode());
                    }
                }
                adapter.clear();
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
                page++;
                listView.onRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (!isAlertShowing())
                    MainTools.ShowToast(mActivity, R.string.interneterror);
                handler.sendEmptyMessage(MyConstant.MYHANDLER_ERROR);
                doCloseRefresh();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listView.onRefreshComplete();
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
        final MyHandler handler = new MyHandler(listView, MyOrderFragmentSec.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE3, 1000);
            }
        }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        isPrepared = false;
        mHasLoadedOnce = false;
        super.onDestroy();
    }

    @Override
    protected void lazyLoad() {
//		L.e(isPrepared + "**2*" + isVisible + "***2*" + mHasLoadedOnce);
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        page = 1;
        getOrderLoad(true);
    }
}
