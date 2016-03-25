package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.AddressEditAdapter;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.address.AddressAll;
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.core.requestdata.address.AddressData;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
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
import de.greenrobot.event.EventBus;

public class AddressManagerActivity extends BaseActivity implements OnRefreshListener2<ListView> {
    private List<AddressInfo> list;
    private PullToRefreshListView listview;
    private AddressEditAdapter adapter;
    private TextView add_address;
    //	public static AddressManagerActivity instance;
    private MyAsyncHttpClient client;
    private ImageView back_bg;
    private ProgressBar pb;
    private Gson gson;
    private boolean hasDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        listview = (PullToRefreshListView) findViewById(R.id.address_listview);
        back_bg = (ImageView) findViewById(R.id.back_bg);
        pb = (ProgressBar) findViewById(R.id.pb);
        gson = new Gson();
        client = new MyAsyncHttpClient();
//		instance=this;
        list = new ArrayList<>();
        adapter = new AddressEditAdapter(this, list);
        listview.setAdapter(adapter);
        listview.setMode(Mode.PULL_FROM_START);
        listview.setOnRefreshListener(this);
        // 设置下拉刷新文本
        listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置listview拉刷新文本
        listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        listview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        listview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        add_address = (TextView) findViewById(R.id.manage_address);
        initData();
        listener();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getMsg())) {
            if (event.getMsg().equals("Edit")) {
                getAddressList();
            } else if (event.getMsg().equals("Add")) {
                getAddressList();
            }
        }
    }

    private void initData() {
        if (getIntent().getExtras() != null) {
            list = (List<AddressInfo>) getIntent().getExtras().getSerializable("obj");
            adapter.setList(list);
        } else {
            getAddressList();
        }
    }

    private void listener() {
        add_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null && list.size() >= 20) {
                    MainTools.ShowToast(AddressManagerActivity.this, "地址数量过多，请删除不必要的地址");
                } else {
                    Intent intent = new Intent(AddressManagerActivity.this, AddressAddActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("hasdefault", hasDefault);
                    startActivity(intent);
                }
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddressManagerActivity.this, AddressEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("obj", list.get(position - 1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(AddressManagerActivity.this)) {
            getAddressList();
        } else {
            doInNetUnuseful();
        }
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(AddressManagerActivity.this, R.string.check_internet);
        listview.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    private void getAddressList() {
        RequestParams params = ParamData.getInstance().getAddressObj();
        client.post(MyConstant.URL_GETMYADDRESS, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                AddressData data = new AddressData();
                if (data.getstatus(responseString, AddressManagerActivity.this, gson) == 1) {
                    AddressAll addressall = data.getObj();
                    if (addressall != null) {
                        list = addressall.getList();
                        if (list != null && list.size() > 0) {
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                            back_bg.setVisibility(View.GONE);
                            hasDefault = false;
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).isdefault()) {
                                    hasDefault = true;
                                    break;
                                }
                            }
                        } else {
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                            back_bg.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (data.getObj() != null && !TextUtils.isEmpty(data.getObj().getTag())) {
                        showTipDialog(AddressManagerActivity.this, data.getObj().getTag(), data.getObj().getResultcode());
                    } else {
                        showTipDialog(AddressManagerActivity.this, "重新登录", data.getObj().getResultcode());
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(AddressManagerActivity.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listview.onRefreshComplete();
                pb.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_addressemanage;
    }

    @Override
    protected String initToolbarTitle() {
        return "管理地址";
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
