package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookData;
import com.dahuochifan.ui.adapter.RecyclerViewPreviewAdapter;
import com.dahuochifan.utils.MainTools;
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

public class JustLookActivity extends BaseActivity implements OnRefreshListener2<RecyclerView> {
    private PullToRefreshRecyclerView listView;
    private ImageView back_bg;
    private ProgressBar pb;
    private Gson gson;
    private RequestParams params;
    private int page = 1;// 初始页数
    private int pageSize = 6;// 页数size
    private double longitude = 0;
    private double latitude = 0;
    private String locationStr = "上海";
    private String currentProv = "上海";
    private List<ChefList> list = new ArrayList<ChefList>();
    private RecyclerViewPreviewAdapter adapter;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConstant.MYHANDLER_CODE1:
                    listView.onRefreshComplete();
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
        initViews();
        init();
        listener();
        page = 1;
        getData(true);
    }

    private void listener() {
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				MainTools.ShowToast(JustLookActivity.this, position + "eheheh");
//			}
//		});
    }

    private void init() {
        gson = new Gson();
        params = new RequestParams();
        adapter = new RecyclerViewPreviewAdapter(this, list, "全部");
        LinearLayoutManager manager = new LinearLayoutManager(JustLookActivity.this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);
    }

    private void initViews() {
        pb = (ProgressBar) findViewById(R.id.pb);
        listView = (PullToRefreshRecyclerView) findViewById(R.id.myxlistview);
        back_bg = (ImageView) findViewById(R.id.back_bg);
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
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(JustLookActivity.this)) {
            page = 1;
            getData(true);
        } else {
            doInNetUnuseful();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(JustLookActivity.this)) {
            if (page > 1) {
                getData(false);
            } else {
                doInTheEnd();
            }
        } else {
            doInNetUnuseful();
        }
    }

    private void getData(final boolean isRefresh) {
        params = ParamData.getInstance().getChefListObj(JustLookActivity.this, page, pageSize, 1, longitude + "",
                latitude + "", "", false + "");
        if (BuildConfig.LEO_DEBUG) L.e(params.toString() + "***");
        baseClient.post(MyConstant.URL_MAINCOOKLIST, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                CookData cookdata = new CookData();
                if (cookdata.dealData(responseString, JustLookActivity.this, gson) == 1) {
                    if (!isRefresh) {
                        if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                            list.addAll(cookdata.getAll().getPage().getList());
                        } else {
                            pb.setVisibility(View.GONE);
                        }
                    } else {
                        if (cookdata.getAll().getPage().getList() != null && cookdata.getAll().getPage().getList().size() > 0) {
                            list = cookdata.getAll().getPage().getList();
                        } else {
                            pb.setVisibility(View.GONE);
                        }
                    }
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                    page++;
                } else {
                    MainTools.ShowToast(JustLookActivity.this, cookdata.getAll().getTag());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(JustLookActivity.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listView.onRefreshComplete();
                pb.setVisibility(View.GONE);
            }
        });

    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(JustLookActivity.this, R.string.check_internet);
        doCloseRefresh();
    }

    public void doInTheEnd() {
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

    @Override
    protected int getLayoutView() {
        return R.layout.activity_cooklist;
    }
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.dh_justlook, menu);
//		return true;
//	}

    @Override
    protected String initToolbarTitle() {
        return "搭伙";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//			case R.id.search:
//				startActivity(new Intent(JustLookActivity.this, JustSearchActivity.class));
//				return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
