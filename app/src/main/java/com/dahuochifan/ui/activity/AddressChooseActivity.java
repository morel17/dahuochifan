package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
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
import com.dahuochifan.ui.adapter.AddressAdapter;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.model.address.AddressAll;
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.core.requestdata.address.AddressData;
import com.dahuochifan.event.AddressEvent;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class AddressChooseActivity extends BaseActivity implements OnRefreshListener2<ListView> {
    private PullToRefreshListView listview;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private List<AddressInfo> list = new ArrayList<>();
    private AddressAdapter adapter;
    private TextView manage_address;
    private ImageView back_bg;
    private ProgressBar pb;
    private boolean hasDefault;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        initviews();
        getAddressList();
        listener();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getMsg())) {
            switch (event.getMsg()) {
                case "Edit":
                    getAddressList();
                    break;
                case "Add":
                    getAddressList();
                    break;
                case "Default":
                    getAddressList();
                    break;
            }
        }
    }

    private void initviews() {
        listview = (PullToRefreshListView) findViewById(R.id.address_listview);
        back_bg = (ImageView) findViewById(R.id.back_bg);
        client = new MyAsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();
        adapter = new AddressAdapter(this, list, gson, client, params);
        listview.setAdapter(adapter);
        manage_address = (TextView) findViewById(R.id.manage_address);
        listview.setMode(Mode.PULL_FROM_START);
        listview.setOnRefreshListener(this);
        pb = (ProgressBar) findViewById(R.id.pb);
        // 设置下拉刷新文本
        listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置listview拉刷新文本
        listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        listview.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        listview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
    }

    private void getAddressList() {
        params = ParamData.getInstance().getAddressObj();
        client.post(MyConstant.URL_GETMYADDRESS, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                AddressData data = new AddressData();
                if (data.getstatus(responseString, AddressChooseActivity.this, gson) == 1) {
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
                        showTipDialog(AddressChooseActivity.this, data.getObj().getTag(), data.getObj().getResultcode());
                    } else {
                        showTipDialog(AddressChooseActivity.this, "重新登录", -1);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(AddressChooseActivity.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listview.onRefreshComplete();
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void listener() {
        manage_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    Intent intent = new Intent(AddressChooseActivity.this, AddressManagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("obj", (Serializable) list);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent();
                if (list.size() > 0 && !TextUtils.isEmpty(list.get(position - 1).getLongitude()) && !TextUtils.isEmpty(list.get(position - 1).getLatitude()) && !(list.get(position - 1).getLongitude().equals("0")) && !(list.get(position - 1).getLatitude().equals("0"))) {
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("obj", (Serializable) list.get(position - 1));
//                        intent.putExtras(bundle);
                    AddressInfo info = list.get(position - 1);
                    EventBus.getDefault().post(new AddressEvent(MyConstant.EVENTBUS_CHOOSE_ADDR, info));
                    finish();
                } else {
                    MainTools.ShowToast(AddressChooseActivity.this, "地址信息不完整，请重新编辑");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (MainTools.isNetworkAvailable(AddressChooseActivity.this)) {
            getAddressList();
        } else {
            doInNetUnuseful();
        }
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(AddressChooseActivity.this, R.string.check_internet);
        listview.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_addresschoose;
    }

    @Override
    protected String initToolbarTitle() {
        return "选择地址";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_address:
                if (list != null && list.size() >= 20) {
                    MainTools.ShowToast(AddressChooseActivity.this, "地址数量过多，请删除不必要的地址");
                } else {
                    Intent intent = new Intent(AddressChooseActivity.this, AddressAddActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("hasdefault", hasDefault);
                    startActivity(intent);
                }
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dh_address_list, menu);
        return true;
    }
}
