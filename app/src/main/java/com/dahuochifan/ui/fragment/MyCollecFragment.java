package com.dahuochifan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookData;
import com.dahuochifan.ui.adapter.RecyclerViewCurAdapter;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyCollecFragment extends BaseFragment implements OnRefreshListener2<RecyclerView> {
    private View view;
    private PullToRefreshRecyclerView myEatenListView;
    private MyAsyncHttpClient client;
    private RequestParams param;
    private Gson gson;
    private SharedPreferences spf;
    // 初始页数
    private static int page = 1;
    // 总页数
    // private int totalPage = 1;
    // 页数size
    private int pageSize = 8;
    private RecyclerViewCurAdapter cooklistadapter;
    private double longitude = 0;
    private double latitude = 0;
    private List<ChefList> list = new ArrayList<ChefList>();
    private ImageView bgiv;

    static class MyHandler extends Handler {
        WeakReference<PullToRefreshRecyclerView> puReferences;
        WeakReference<MyCollecFragment> wrReferences;

        MyHandler(PullToRefreshRecyclerView wrReferences, MyCollecFragment wrReference) {
            this.puReferences = new WeakReference<>(wrReferences);
            this.wrReferences = new WeakReference<>(wrReference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE1:
                    PullToRefreshRecyclerView cycView = puReferences.get();
                    cycView.onRefreshComplete();
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

    public static MyCollecFragment newInstance() {
        MyCollecFragment fragment = new MyCollecFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cheflist, container, false);
        spf = SharedPreferenceUtil.initSharedPerence().init(mActivity, MyConstant.APP_SPF_NAME);
        init();
        initView();
        cooklistadapter = new RecyclerViewCurAdapter(mActivity, list, "");
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        myEatenListView.setLayoutManager(manager);
        myEatenListView.setAdapter(cooklistadapter);
        return view;
    }

    private void init() {
        bgiv = (ImageView) view.findViewById(R.id.back_bg);
        bgiv.setBackgroundResource(R.drawable.collect_back);
        gson = new Gson();
        client = new MyAsyncHttpClient();
        param = new RequestParams();
        // param.prepare(mActivity, param);
        if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf))) {
            longitude = Tools.toDouble(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf));
        } else {
            longitude = 121.371927;
        }
        if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf))) {
            latitude = Tools.toDouble(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf));
        } else {
            latitude = 31.105911;
        }
    }

    private void initView() {
        myEatenListView = (PullToRefreshRecyclerView) view.findViewById(R.id.recycler);
        myEatenListView.setMode(Mode.BOTH);
        myEatenListView.setOnRefreshListener(this);
        ILoadingLayout startLabels = myEatenListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = myEatenListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

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
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            page = 1;
            getMainData(true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(mActivity)) {
            getMainData(false);
        } else {
            doInNetUnuseful();
        }
    }

    private void getMainData(boolean isRefresh) {
        param = ParamData.getInstance().getCollectListObj(mActivity, page, pageSize, 3, longitude, latitude);
        post_refresh(param, isRefresh);
    }

    private void post_refresh(RequestParams param2, final boolean isRefresh) {
        final MyHandler handler = new MyHandler(myEatenListView, this);
        client.setTimeout(5000);
        client.post(MyConstant.URL_GETCOLLECTLIST, param2, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (list != null && list.size() == 0) {
                    showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中", false);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                CookData cookdata = new CookData();
                if (cookdata.dealData(responseString, mActivity, gson) == 1) {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_SUCCESS);
                    if (!isRefresh) {
                        if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                            list.addAll(cookdata.getAll().getPage().getList());
                        }
                    } else {
                        if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                            list = cookdata.getAll().getPage().getList();
                        } else {
                            list.clear();
                            bgiv.setVisibility(View.VISIBLE);
                        }
                    }
                    cooklistadapter.setList(list);
                    cooklistadapter.notifyDataSetChanged();
                    page++;
                } else {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_WARN);
//					MainTools.ShowToast(mActivity, cookdata.getAll().getTag());
//					if (cookdata.getAll() != null && !TextUtils.isEmpty(cookdata.getAll().getTag())) {
//						showTipDialog(cookdata.getAll().getTag(),Integer.parseInt(cookdata.getAll().getResultcode()));
//					} else {
//						showTipDialog("请重新登录",Integer.parseInt(cookdata.getAll().getResultcode()));
//					}
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (!isAlertShowing())
                    MainTools.ShowToast(mActivity, R.string.interneterror);
                handler.sendEmptyMessage(MyConstant.MYHANDLER_ERROR);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                myEatenListView.onRefreshComplete();
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
        final MyHandler handler = new MyHandler(myEatenListView, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE1, 1000);
            }
        }).start();
    }
}
