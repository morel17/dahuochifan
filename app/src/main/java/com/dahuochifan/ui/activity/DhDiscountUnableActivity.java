package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.discount.DiscountAll;
import com.dahuochifan.model.discount.DiscountDetail;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.discount.DiscountData;
import com.dahuochifan.ui.adapter.DiscountUnableAdapter;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DhDiscountUnableActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private PullToRefreshListView discount_listview;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private ImageView bgiv;
    private ProgressBar pb;
    // 初始页数
    private int page = 1;
    // 每页的个数
    private int pageSize = 15;
    private List<DiscountDetail> list;
    private DiscountUnableAdapter adapter;
    private String discountStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initListView();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE1:
                    discount_listview.onRefreshComplete();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    private void initListView() {
        discount_listview.setMode(PullToRefreshBase.Mode.BOTH);
        discount_listview.setOnRefreshListener(this);
        ILoadingLayout startLabels = discount_listview.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = discount_listview.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        // 设置下拉刷新文本
        discount_listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        discount_listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        discount_listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        discount_listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        discount_listview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        discount_listview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
    }

    private void init() {
        discount_listview = (PullToRefreshListView) findViewById(R.id.discount_listview);
        client = new MyAsyncHttpClient();
        gson = new Gson();
        bgiv = (ImageView) findViewById(R.id.back_bg);
        bgiv.setBackgroundResource(R.mipmap.discount_empty);
        pb = (ProgressBar) findViewById(R.id.pb);
        list = new ArrayList<>();
        adapter = new DiscountUnableAdapter(this);
        discount_listview.setAdapter(adapter);
        discountStatus = "0";//参数表示不可用或者已过期
        getMainData(true);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_dh_discount;
    }

    @Override
    protected String initToolbarTitle() {
        return "红包";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(this)) {
            getMainData(true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(this)) {
            getMainData(false);
        } else {
            doInNetUnuseful();
        }
    }

    private void getMainData(final boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        }
        params = ParamData.getInstance().getDiscountAllObj(discountStatus, page + "", pageSize + "");
        client.post(MyConstant.GET_DISCOUNT_ALL_LIST, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(DhDiscountUnableActivity.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                discount_listview.onRefreshComplete();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                DiscountData data = new DiscountData();
                DiscountAll discountAll;
                if (data.dealData(responseString, DhDiscountUnableActivity.this, gson) == 1) {
                    discountAll = data.getDiscountAll();
                    if (discountAll != null) {
                        list = discountAll.getPage().getList();
                        if (list != null && list.size() > 0) {
                            bgiv.setVisibility(View.GONE);
                            if (isRefresh) {
                                adapter.setmObjects(list);
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.addAll(list);
                                adapter.notifyDataSetChanged();
                            }
                            page++;
                        } else {
                            if (isRefresh) {
                                bgiv.setVisibility(View.VISIBLE);
                            } else {
                                if (adapter.getCount() > 0 && !TextUtils.isEmpty(adapter.getmObjects().get(adapter.getmObjects().size() - 1).getCuids())) {
                                } else {
                                    if (adapter.getCount() == 0) {
                                        bgiv.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }

                } else if (data.dealData(responseString, DhDiscountUnableActivity.this, gson) == -1) {
                    discountAll = data.getDiscountAll();
                    if (discountAll != null) {
                        String tag = discountAll.getTag();
                        int resultcode = discountAll.getResultcode();
                        if (!TextUtils.isEmpty(tag)) {
                            showTipDialog(DhDiscountUnableActivity.this, tag, resultcode);
                        } else {
                            showTipDialog(DhDiscountUnableActivity.this, "重新登录", resultcode);
                        }
                    }
                } else {
                    discountAll = data.getDiscountAll();
                    if (discountAll != null) {
                        String tag = discountAll.getTag();
                        if (!TextUtils.isEmpty(tag)) {
                            MainTools.ShowToast(DhDiscountUnableActivity.this, tag);
                        }
                    }
                }
            }
        });
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(this, R.string.check_internet);
        doCloseRefresh();
    }

    public void doInTheEnd() {
        MainTools.ShowToast(this, "已经到底啦");
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
