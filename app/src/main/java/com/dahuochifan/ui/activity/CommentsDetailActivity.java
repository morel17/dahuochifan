package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.widget.ListView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.comment.CommentsInfo;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CommentsData;
import com.dahuochifan.ui.adapter.CommentsDetailAdapter;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommentsDetailActivity extends BaseActivity implements OnRefreshListener2<RecyclerView> {


    private PullToRefreshRecyclerView listview;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private String nickname, naviland, avatar;
    // 初始页数
    private int page = 1;
    // 总页数
    private int totalPage = 1;
    // 页数size
    private int pageSize = 8;
    private int score;
    private int commentnum;
    private String cbids, chefids;
    private List<CommentsInfo> listx = new ArrayList<CommentsInfo>();
    private CommentsDetailAdapter adapter;
    private boolean canUp;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE1:
                    listview.onRefreshComplete();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        initData();
        initViews();
        page = 1;
        getData(true);
    }

    private void initData() {
        nickname = getIntent().getStringExtra("nickname");
        naviland = getIntent().getStringExtra("naviland");
        avatar = getIntent().getStringExtra("avatar");
        score = getIntent().getIntExtra("score", 0);
        commentnum = getIntent().getIntExtra("commentnum", 0);
        cbids = getIntent().getStringExtra("cbids");
        chefids = getIntent().getStringExtra("chefids");
        canUp = true;
    }

    private void initViews() {
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        // params.prepare(this, params);
        gson = new Gson();
        listview = (PullToRefreshRecyclerView) findViewById(R.id.recycler);
        listview.setMode(Mode.PULL_FROM_START);
        listview.setOnRefreshListener(this);
        // 设置下拉刷新文本
        listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        listview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        listview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        adapter = new CommentsDetailAdapter(CommentsDetailActivity.this, listx);
        if (commentnum != 0) {
            adapter.setStar(score / commentnum * 0.5f);
        } else {
            adapter.setStar(0f);
        }
        adapter.setAvatar(avatar);
        adapter.setNickname(nickname);
        adapter.setContent_num(commentnum + "评论");
        LinearLayoutManager manager = new LinearLayoutManager(CommentsDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        listview.setLayoutManager(manager);
        listview.setAdapter(adapter);
        listview.getRefreshableView().setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动，既是否向右滑动或向下滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
//                        Ln.e("howes right="+manager.findLastCompletelyVisibleItemPosition());
//                        Toast.makeText(getActivityContext(),"加载更多",0).show();
                        if (canUp) {
                            if (MainTools.isNetworkAvailable(CommentsDetailActivity.this)) {
                                if (page > 1 && page <= totalPage) {
                                    getData(false);
                                } else {
                                    doInTheEnd();
                                }
                            } else {
                                doInNetUnuseful();
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                L.e("dx===" + dx + "sss" + "dy====" + dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0) {
                    //大于0表示，正在向上滚动
                    isSlidingToLast = true;
                } else {
                    //小于等于0 表示停止或向下滚动
                    isSlidingToLast = false;
                }
            }
        });
    }

    private void getData(final boolean isrefresh) {
        params = ParamData.getInstance().getCommentObj(cbids, page, pageSize, chefids);
        client.post(MyConstant.URL_GETCOMMENTSLIST, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!isrefresh)
                    canUp = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (BuildConfig.LEO_DEBUG) L.e(responseString);
                CommentsData data = new CommentsData();
                if (data.dealData(responseString, CommentsDetailActivity.this, gson) == 1) {
                    totalPage = data.getAll().getPage().getTotalPage();
                    if (!isrefresh) {
                        canUp = true;
                        if (data.getAll().getPage().getList() != null && data.getAll().getPage().getList().size() > 0) {
                            listx.addAll(data.getAll().getPage().getList());
                        }
                    } else {
                        if (data.getAll().getPage().getList() != null && data.getAll().getPage().getList().size() > 0) {
                            listx = data.getAll().getPage().getList();
                        }
                    }
                    adapter.setList(listx);
                    adapter.notifyDataSetChanged();
                    page++;
                } else {
                    if (data.getAll() != null && !TextUtils.isEmpty(data.getAll().getTag())) {
                        showTipDialog(CommentsDetailActivity.this, data.getAll().getTag(), data.getAll().getResultcode());
                    } else {
                        showTipDialog(CommentsDetailActivity.this, "重新登录", data.getAll().getResultcode());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (!isrefresh)
                    canUp = true;
                MainTools.ShowToast(CommentsDetailActivity.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(CommentsDetailActivity.this)) {
            page = 1;
            getData(true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(CommentsDetailActivity.this)) {
            if (page > 1 && page <= totalPage) {
                getData(false);
            } else {
                doInTheEnd();
            }
        } else {
            doInNetUnuseful();
        }
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(CommentsDetailActivity.this, R.string.check_internet);
        doCloseRefresh();
    }

    public void doInTheEnd() {
        MainTools.ShowToast(CommentsDetailActivity.this, "已经到底啦");
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

    public void doPrepareRefresh(PullToRefreshBase<ListView> refreshView) {
        String label = DateUtils.formatDateTime(CommentsDetailActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_commentsdetail;
    }

    @Override
    protected String initToolbarTitle() {
        return "评论";
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

}
