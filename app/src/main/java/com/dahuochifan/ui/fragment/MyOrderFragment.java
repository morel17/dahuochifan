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
import com.dahuochifan.core.model.order.OrderInfo;
import com.dahuochifan.core.requestdata.order.OrderData;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.userinfo.PersonInfoDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.PersonInfoData;
import com.dahuochifan.ui.adapter.MyOrderAdapterTemp;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
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
import de.greenrobot.event.EventBus;

public class MyOrderFragment extends BaseFragment implements OnRefreshListener2<ListView> {
    private MyOrderFragment instance = null;
    private View view;
    private PullToRefreshListView listView;
    private List<OrderInfo> list = new ArrayList<>();
    // 初始页数
    private static int page = 1;
    // 页数size
    private int pageSize = 12;
    private Gson gson;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private ImageView bgiv;
    private MyOrderAdapterTemp adapter;
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
        WeakReference<MyOrderFragment> wrReferences;

        MyHandler(PullToRefreshListView puReference, MyOrderFragment wrReferences) {
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
//    private Handler handler = new Handler() {
//
//    };

    public MyOrderFragment newInstance() {
        if (instance == null) {
            instance = new MyOrderFragment();
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
        page = 1;
        getOrderLoad(true);

        return view;
    }

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
            if (page > 1) {
                getOrderLoad(false);
            } else {
                doCloseRefresh();
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
        bgiv.setBackgroundResource(R.drawable.shike_back);
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
        adapter = new MyOrderAdapterTemp(mActivity, 0);
        listView.setAdapter(adapter);
    }

    public void postSuccess() {
        if (adapter != null) {
            adapter.postSuccess();
        }
    }

    public void getOrderLoad(final boolean isRefresh) {
        if (isRefresh)
            page = 1;
        params = ParamData.getInstance().getShikeObj(page, pageSize, "0");
        getDataLoad(params, isRefresh);
    }

    private void getDataLoad(RequestParams params2, final boolean isRefresh) {
        client.setTimeout(5000);
        final MyHandler handler = new MyHandler(listView, MyOrderFragment.this);
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
                    if (!isRefresh) {
                        if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
                            list.addAll(data.getObj().getPage().getList());
                            bgiv.setVisibility(View.GONE);
                        } else {
                            MainTools.ShowToast(mActivity, "已经到底啦");
                        }
                    } else {
//                        getUserInfo();
                        mHasLoadedOnce = true;
                        if (data.getObj().getPage().getList() != null && data.getObj().getPage().getList().size() > 0) {
                            list = data.getObj().getPage().getList();
                            bgiv.setVisibility(View.GONE);
                        } else {
                            list.clear();
                            bgiv.setVisibility(View.VISIBLE);
                        }
                    }
                    // EventBus.getDefault().post(new FirstEvent("MyOrder"));
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

    /**
     * 刷新的时候更新mark状态顺带更新用户信息
     */
    private void getUserInfo() {
        params = ParamData.getInstance().getPersonObj(MyConstant.user.getRole());
        client.post(MyConstant.URL_GETPERSONINFO, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                PersonInfoData persondata = new PersonInfoData();
                if (persondata.dealData2(responseString, mActivity, gson) == 1) {
                    PersonInfoDetail info;
                    info = persondata.getPersonInfoDetail();
                    if (info != null) {
                        if (info.getMark() != null && !TextUtils.isEmpty(info.getMark().getNewsnum())) {
                            int newsMark = Tools.toInteger(info.getMark().getNewsnum());
                            if (newsMark > 0) {
                                EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                            } else {
                                EventBus.getDefault().post(new FirstEvent("AnchorHide"));
                            }
                        }
                        if (info.getMark() != null && !TextUtils.isEmpty(info.getMark().getChefordernum())) {
                            int orderNum = Tools.toInteger(info.getMark().getDinerordernum());
                            if (orderNum > 0) {
                                EventBus.getDefault().post(new FirstEvent("AnchorOrderShow"));
                            } else {
                                EventBus.getDefault().post(new FirstEvent("AnchorOrderHide"));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(mActivity, R.string.check_internet);
        doCloseRefresh();
    }

    public void doCloseRefresh() {
        final MyHandler handler = new MyHandler(listView, MyOrderFragment.this);
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
//	@Override
//	protected void lazyLoad() {
//		L.e(isPrepared + "**1*" + isVisible + "***1*" + mHasLoadedOnce);
//		if (!isPrepared || !isVisible || mHasLoadedOnce) {
//			L.e("dy++++"+isPrepared);
//			return;
//		}
//		page = 1;
//		getOrderLoad(true);
//	}
}
