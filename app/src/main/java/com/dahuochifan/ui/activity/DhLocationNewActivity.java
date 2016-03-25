package com.dahuochifan.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.core.model.address.AddressAll;
import com.dahuochifan.core.model.address.AddressInfo;
import com.dahuochifan.core.requestdata.address.AddressData;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.adapter.LocationSearchAdapter;
import com.dahuochifan.ui.adapter.RecyclerViewLocationAdapter;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class DhLocationNewActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<RecyclerView>, TextWatcher, AMapLocationListener, Runnable {
    private PullToRefreshRecyclerView mRecyclerView;
    private MyAsyncHttpClient client;
    private RequestParams params;
    private Gson gson;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private LayoutInflater inflater;
    private RecyclerViewLocationAdapter adapter;
    private List<AddressInfo> list;
    private ProgressBar pb;
    private ImageView back_bg;
    private boolean hasDefault;
    private AutoCompleteTextView search_et;
    private List<Tip> myList;
    private ListView recycler_locaiton;
    private ImageView delete_tv;
    private RelativeLayout add_address_rl;
    private AMapLocation aMapLocation;// 用于判断定位超时
    private ProgressDialog pd;
    private RelativeLayout search_rl;

    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initLocation();
        initRecyclerView();
        initSearch();
        btnListener();
    }

    public void onEventMainThread(FirstEvent event) {
        if (event != null && !TextUtils.isEmpty(event.getMsg())) {
            if (event.getMsg().equals("AddNew")) {
                getAddressList();
            }
        }
    }

    /**
     * 初始化高德定位
     */

    private void initLocation() {
        // 初始化定位，
        mlocationClient = new AMapLocationClient(getApplicationContext());
        // 初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        // 设置定位模式为低功耗定位
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位回调监听
        mlocationClient.setLocationListener(this);
        // 设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
    }

    private void btnListener() {
        delete_tv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                search_et.setText("");
            }
        });
        add_address_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                startLoc();
            }
        });
        recycler_locaiton.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                Tip myTip = myList.get(position);
                if (!TextUtils.isEmpty(myTip.getDistrict()) && myTip.getPoint() != null && myTip.getDistrict().contains("上海市")) {
                    SharedPreferenceUtil.initSharedPerence().putGDProvince(editor, "上海");
                    SharedPreferenceUtil.initSharedPerence().putPoiName(editor, myTip.getName());
                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, myTip.getPoint().getLatitude() + "");
                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, myTip.getPoint().getLongitude() + "");
                    editor.commit();
                    EventBus.getDefault().post(new FirstEvent("DhLocation", myTip.getName()));
                    finish();
                } else {
                    MainTools.ShowToast(DhLocationNewActivity.this, "请选择详细的上海地址");
                }
            }
        });

        search_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                String search_str = search_et.getText().toString();
                if (TextUtils.isEmpty(search_str)) {
                    MainTools.ShowToast(DhLocationNewActivity.this, "搜索内容不能为空");
                    return;
                } else {
                    if (myList != null && myList.size() > 0) {
                        Tip myTip = myList.get(0);
                        if (myTip.getPoint() != null) {
                            if (!TextUtils.isEmpty(myTip.getDistrict()) && myTip.getPoint() != null && myTip.getDistrict().contains("上海市")) {
                                SharedPreferenceUtil.initSharedPerence().putGDProvince(editor, "上海");
                                SharedPreferenceUtil.initSharedPerence().putPoiName(editor, myTip.getName());
                                SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, myTip.getPoint().getLatitude() + "");
                                SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, myTip.getPoint().getLongitude() + "");
                                editor.commit();
                                EventBus.getDefault().post(new FirstEvent("DhLocation", myTip.getName()));
                                finish();
                            } else {
                                MainTools.ShowToast(DhLocationNewActivity.this, "请选择详细的上海地址");
                            }
                        }
                    } else {
                        MainTools.ShowToast(DhLocationNewActivity.this, "没有搜索结果");
                    }
                }
            }
        });
    }

    private void startLoc() {
        pd.show();
        mlocationClient.startLocation();
    }

    private void initViews() {
        client = new MyAsyncHttpClient();
        gson = new Gson();
        spf = SharedPreferenceUtil.initSharedPerence().init(this, MyConstant.APP_SPF_NAME);
        editor = spf.edit();
        mRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.recycler);
        pb = (ProgressBar) findViewById(R.id.pb);
        back_bg = (ImageView) findViewById(R.id.back_bg);
        recycler_locaiton = (ListView) findViewById(R.id.recycler_locaiton);
        inflater = LayoutInflater.from(this);
        list = new ArrayList<>();
        adapter = new RecyclerViewLocationAdapter(this, inflater, list);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        search_et = (AutoCompleteTextView) findViewById(R.id.search_et);
        myList = new ArrayList<>();
        delete_tv = (ImageView) findViewById(R.id.delete_tv);
        add_address_rl = (RelativeLayout) findViewById(R.id.add_address_rl);
        search_rl = (PercentRelativeLayout) findViewById(R.id.search_rl);
        pd = new ProgressDialog(DhLocationNewActivity.this);
        pd.setMessage("正在定位中，请稍等....");
        pd.setCancelable(false);
        EventBus.getDefault().register(this);
    }

    private void initRecyclerView() {
        mRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mRecyclerView.setOnRefreshListener(this);
        // 设置下拉刷新文本
        mRecyclerView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        mRecyclerView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        mRecyclerView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        mRecyclerView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        mRecyclerView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        mRecyclerView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        getAddressList();
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
                if (data.getstatus(responseString, DhLocationNewActivity.this, gson) == 1) {
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
                        showTipDialog(DhLocationNewActivity.this, data.getObj().getTag(), data.getObj().getResultcode());
                    } else {
                        showTipDialog(DhLocationNewActivity.this, "重新登录", data.getObj().getResultcode());
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MainTools.ShowToast(DhLocationNewActivity.this, R.string.interneterror);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRecyclerView.onRefreshComplete();
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void initSearch() {
        search_et.addTextChangedListener(this);
        search_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Tip tip = myList.get(position);

            }
        });
        search_et.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_dh_location_new2;
    }

    @Override
    protected String initToolbarTitle() {
        return "设置用餐地址";
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
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (MainTools.isNetworkAvailable(DhLocationNewActivity.this)) {
            getAddressList();
        } else {
            doInNetUnuseful();
        }
    }

    public void doInNetUnuseful() {
        MainTools.ShowToast(DhLocationNewActivity.this, R.string.check_internet);
        mRecyclerView.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        Inputtips inputTips = new Inputtips(DhLocationNewActivity.this,
                new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                        if (rCode == 0) {// 正确返回
                            myList = tipList;
                            LocationSearchAdapter aAdapter = new LocationSearchAdapter(DhLocationNewActivity.this);
                            aAdapter.setmObjects(tipList);
//                            search_et.setAdapter(aAdapter);
                            recycler_locaiton.setAdapter(aAdapter);
                            aAdapter.notifyDataSetChanged();
                        }
                    }
                });
        try {
            inputTips.requestInputtips(newText, "上海");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
        } catch (com.amap.api.services.core.AMapException e) {
            e.printStackTrace();
        }
        if (s.length() >= 1) {
            mRecyclerView.setVisibility(View.GONE);
            recycler_locaiton.setVisibility(View.VISIBLE);
            delete_tv.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            recycler_locaiton.setVisibility(View.GONE);
            delete_tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            stopLocation();
            mlocationClient.onDestroy();
        }
    }

    /**
     * 销毁定位
     */
    private void stopLocation() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void run() {
        if (aMapLocation == null) {
            if (pd != null) {
                pd.dismiss();
                MainTools.ShowToast(DhLocationNewActivity.this, "定位超时");
            }
            stopLocation();// 销毁掉定位
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (pd != null) {
                pd.dismiss();
            }
            if (location.getErrorCode() == 0) {
                this.aMapLocation = location;// 判断超时机制
                double geoLat = location.getLatitude();
                double geoLng = location.getLongitude();
                if (!TextUtils.isEmpty(geoLng + "") && !TextUtils.isEmpty(geoLat + "")) {
                    SharedPreferenceUtil.initSharedPerence().putGDLongitude(editor, geoLng + "");
                    SharedPreferenceUtil.initSharedPerence().putGDLatitude(editor, geoLat + "");
                    String curP, curCity, curDistrict;
                    curP = location.getProvince().replace("省", "").replace("市", "");
                    curCity = location.getCity();
                    curDistrict = location.getDistrict();
                    SharedPreferenceUtil.initSharedPerence().putCurProv(editor, curP);
                    if (!TextUtils.isEmpty(location.getPoiName())) {
                        SharedPreferenceUtil.initSharedPerence().putPoiName(editor, location.getPoiName());
                    }
                    if (!TextUtils.isEmpty(curP) && !curP.equals(MyConstant.user.getCurprov())) {
                        SharedPreferenceUtil.initSharedPerence().putGDProvince(editor, curP);
                        SharedPreferenceUtil.initSharedPerence().putGDCity(editor, curCity);
                        SharedPreferenceUtil.initSharedPerence().putGDDistrict(editor, curDistrict);
                    }
                    if (TextUtils.isEmpty(MyConstant.user.getCurprov())) {
                        if (curP.equals(curCity)) {
                            if (!TextUtils.isEmpty(curDistrict)) {
                                SharedPreferenceUtil.initSharedPerence().putCurCity(editor, curDistrict);
                                MyConstant.user.setCurcity(curDistrict);
                            }
                        } else {
                            if (!TextUtils.isEmpty(curCity)) {
                                SharedPreferenceUtil.initSharedPerence().putCurCity(editor, curCity);
                                MyConstant.user.setCurcity(curCity);
                            }
                        }
                        MyConstant.user.setCurprov(curP);
//                    postLocation(MyConstant.user.getCurprov(), MyConstant.user.getCurcity());
                    }
                    editor.commit();
                    EventBus.getDefault().post(new FirstEvent("DhLocation", location.getPoiName()));
                    stopLocation();// 停止定位
                    finish();
                }
            } else {
                MainTools.ShowToast(DhLocationNewActivity.this, location.getErrorInfo());
                stopLocation();// 停止定位
            }
        }
    }
}
