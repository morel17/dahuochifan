package com.dahuochifan.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.model.message.MessageDetail;
import com.dahuochifan.model.userinfo.PersonInfoDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.PersonInfoData;
import com.dahuochifan.requestdata.message.DhMessageData;
import com.dahuochifan.ui.activity.MessageDetailActivity;
import com.dahuochifan.ui.adapter.DhNotiAdapter;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2015/11/2.
 * 主界面消息Fragment
 */
public class DhNotificationFragment extends LazyFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private View view;
    private ImageView bgiv;
    private PullToRefreshListView notiListview;
    private List<MessageDetail> list;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private DhNotiAdapter adapter;

    // 初始页数
    private int page = 1;
    private Gson gson;
    private MyAsyncHttpClient client;
    private RequestParams params;

    static class MyHandler extends Handler {
        WeakReference<PullToRefreshListView> wrReferences;
        WeakReference<DhNotificationFragment> fgReference;

        MyHandler(PullToRefreshListView wrReferences, DhNotificationFragment fgReference) {
            this.wrReferences = new WeakReference<>(wrReferences);
            this.fgReference = new WeakReference<>(fgReference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE3:
                    PullToRefreshListView cycView = wrReferences.get();
                    cycView.onRefreshComplete();
                    break;
                case MyConstant.MYHANDLER_ERROR:
                    fgReference.get().changeAlertDialog(MorelAlertDialog.ERROR_TYPE, "网络不给力");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_WARN:
                    fgReference.get().changeAlertDialog(MorelAlertDialog.WARNING_TYPE, "请求出错");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_SUCCESS:
                    fgReference.get().changeAlertDialog(MorelAlertDialog.SUCCESS_TYPE, "加载成功");
                    this.sendEmptyMessageDelayed(MyConstant.MYHANDLER_WAIT, 1500);
                    break;
                case MyConstant.MYHANDLER_WAIT:
                    fgReference.get().dismissAlertDialog();
                    break;
                default:
                    break;
            }
        }
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null) {
            switch (event.getType()) {
                case MyConstant.EVENTBUS_MESSAGE:
                    getMessage(true);
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        initViews(view);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notiListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.get(position - 1).setIs_read(true);
                Intent it = new Intent(mActivity, MessageDetailActivity.class);
                it.putExtra("mylist", list.get(position - 1));
                startActivity(it);
                adapter.notifyDataSetChanged();
                int j = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i).is_read()) {
                        j++;
                    }
                    if (j > 0) {
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("notilist", (Serializable) list);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            list = new ArrayList<>();
            list = (List<MessageDetail>) savedInstanceState.getSerializable("notilist");
            if (adapter == null) {
                adapter = new DhNotiAdapter(mActivity);
            }
            adapter.setmObjects(list);
            adapter.notifyDataSetChanged();
        }
        super.onViewStateRestored(savedInstanceState);
    }

    private void initViews(View dhview) {
        bgiv = (ImageView) dhview.findViewById(R.id.bgiv);
        notiListview = (PullToRefreshListView) dhview.findViewById(R.id.notiListview);
        list = new ArrayList<>();
        gson = new Gson();
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        adapter = new DhNotiAdapter(mActivity);
        notiListview = (PullToRefreshListView) view.findViewById(R.id.notiListview);
        notiListview.setMode(PullToRefreshBase.Mode.BOTH);
        notiListview.setOnRefreshListener(this);
        // 设置下拉刷新文本
        notiListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        notiListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        notiListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        notiListview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        notiListview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        notiListview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        notiListview.setAdapter(adapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        page = 1;
        getMessage(true);
    }

    private void getMessage(final boolean isRefresh) {
        final MyHandler handler = new MyHandler(notiListview,DhNotificationFragment.this);
        if (isRefresh)
            page = 1;
        int pageSize = 20;
        params = ParamData.getInstance().getMessageObj(page, pageSize, "0");
        client.setTimeout(5000);
        client.post(MyConstant.URL_MESSAGE, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (list != null && list.size() == 0) {
                    showAlertDialog(MorelAlertDialog.PROGRESS_TYPE, "正在加载中，请稍等", true);
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                DhMessageData msgData = new DhMessageData();
                if (msgData.dealData(arg2, mActivity, gson) == 1) {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_SUCCESS);
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
                    notiListview.onRefreshComplete();
                    mHasLoadedOnce = true;
                } else {
                    handler.sendEmptyMessage(MyConstant.MYHANDLER_WARN);
                    if (msgData.getAll() != null && !TextUtils.isEmpty(msgData.getAll().getTag())) {
                        showTipDialog(msgData.getAll().getTag(), msgData.getAll().getResultcode());
                    } else {
                        showTipDialog("未知错误", -1);
                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                handler.sendEmptyMessage(MyConstant.MYHANDLER_ERROR);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                notiListview.onRefreshComplete();
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
                    getMessage(true);
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
        final MyHandler handler = new MyHandler(notiListview, DhNotificationFragment.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(MyConstant.MYHANDLER_CODE3, 1000);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        isPrepared = false;
        mHasLoadedOnce = false;
        list.clear();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
