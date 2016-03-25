package com.dahuochifan.ui.fragment;

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
import com.dahuochifan.model.activities.DhAdAllList;
import com.dahuochifan.model.activities.DhAdDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.activities.DhActivitiesListData;
import com.dahuochifan.ui.adapter.MainAdAdapter;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
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

public class MainAdFragment extends LazyFragment implements OnRefreshListener2<ListView> {
    private View view;
    private PullToRefreshListView myEatenListView;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private ImageView bgiv;

    // 初始页数
    private int page = 1;
    // 页数size
    private int pageSize = 8;
    List<DhAdDetail> list_detail = new ArrayList<DhAdDetail>();
    private MainAdAdapter adapter;
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
        WeakReference<MainAdFragment> wrReferences;

        MyHandler(PullToRefreshListView puReference, MainAdFragment wrReferences) {
            this.puReference = new WeakReference<>(puReference);
            this.wrReferences = new WeakReference<>(wrReferences);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE2:
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mainad, container, false);
        init();
        initViews();
        isPrepared = true;
        lazyLoad();
        return view;
    }

    private void getAd(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        }
        params = ParamData.getInstance().getAdObj();
        getDataOne(params, isRefresh);
    }

    private void getDataOne(RequestParams params2, final boolean isRefresh) {
        final MyHandler handler = new MyHandler(myEatenListView, MainAdFragment.this);
        client.post(MyConstant.URL_AD, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (list_detail != null && list_detail.size() == 0) {
                    showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中，请稍等", true);
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                DhActivitiesListData data = new DhActivitiesListData();
                if (data.dealData(arg2, mActivity, gson) == 1) {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_SUCCESS);
                    DhAdAllList dhAll = data.getAll();
                    List<DhAdDetail> list = dhAll.getList();
                    if (!isRefresh) {
                        if (list != null && list.size() > 0) {
                            list_detail.addAll(list);
                            bgiv.setVisibility(View.GONE);
                        } else {
                            MainTools.ShowToast(mActivity, "已经到底啦");
                        }
                    } else {
                        mHasLoadedOnce = true;
                        if (list != null && list.size() > 0) {
                            list_detail = list;
                            bgiv.setVisibility(View.GONE);
                        } else {
                            list.clear();
                            bgiv.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter.setmObjects(list_detail);
                    page++;
                    adapter.notifyDataSetChanged();
                    mHasLoadedOnce = true;
                } else {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_WARN);
                    if (data.getAll() != null && !TextUtils.isEmpty(data.getAll().getTag()) && data.getAll().getResultcode() == -1) {
                        showTipDialog(data.getAll().getTag(), data.getAll().getResultcode());
                    }

                }
                myEatenListView.onRefreshComplete();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(mActivity, R.string.interneterror);
                handler.sendEmptyMessage(MyConstant.MYHANDLER_ERROR);
                doCloseRefresh();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void init() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        adapter = new MainAdAdapter(mActivity);
    }

    private void initViews() {
        myEatenListView = (PullToRefreshListView) view.findViewById(R.id.collectTablistview);
        bgiv = (ImageView) view.findViewById(R.id.bgiv);
        myEatenListView.setMode(Mode.PULL_FROM_START);
        myEatenListView.setOnRefreshListener(this);

        // 设置下拉刷新文本
        myEatenListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        myEatenListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        myEatenListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        myEatenListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        myEatenListView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        myEatenListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        adapter = new MainAdAdapter(mActivity);
        myEatenListView.setAdapter(adapter);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            page = 1;
            getAd(true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            getAd(false);
        } else {
            doInNetUnuseful();
        }
    }

    public void doCloseRefresh() {
        final MyHandler handler = new MyHandler(myEatenListView, MainAdFragment.this);
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
    public void onDestroy() {
        isPrepared = false;
        mHasLoadedOnce = false;
        super.onDestroy();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        page = 1;
        getAd(true);
    }
}
