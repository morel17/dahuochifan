package com.dahuochifan.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.event.AddressEvent;
import com.dahuochifan.ui.adapter.LocationSearchAdapter;
import com.dahuochifan.ui.adapter.MapAddressCycAdapter;
import com.dahuochifan.utils.DividerItemDecoration;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DhAddressMapActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener, View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener, TextWatcher {

    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;
    private TextView tv;

    private LocationSource.OnLocationChangedListener mListener;
    private ImageView mark_ivx;

    private RecyclerView recyclerView;
    private MapAddressCycAdapter adapter;


    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据

    private SharedPreferences spf;

    private RelativeLayout map_bottom_rl;
    private ProgressBar map_bottom_pb;
    private TextView map_bottom_tv;

    private String longtitude;
    private String latitude;
    private int locateNum;
    private ImageView search_btn;
    private AutoCompleteTextView search_et;
    private PercentRelativeLayout search_rl;
    private GeocodeSearch geocoderSearch;
    ObjectAnimator animatorObj;
    private List<Tip> tipsList = new ArrayList<>();
    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    PropertyValuesHolder pvhY, pvhSX, pvhSY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mapView = (MapView) findViewById(R.id.map);
        mark_ivx = (ImageView) findViewById(R.id.mark_ivx);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
        mark_ivx.post(new Runnable() {
            @Override
            public void run() {
                pvhY = PropertyValuesHolder.ofFloat("y", mark_ivx.getTop(), mark_ivx.getTop() - 66, mark_ivx.getTop());
                if (pvhY != null) {
                    animatorObj = ObjectAnimator.ofPropertyValuesHolder(mark_ivx, pvhY, pvhSX, pvhSY).setDuration(1000);
                }
            }
        });
        tv = (TextView) findViewById(R.id.tv);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        map_bottom_rl = (RelativeLayout) findViewById(R.id.map_bottom_rl);
        map_bottom_pb = (ProgressBar) findViewById(R.id.map_bottom_pb);
        map_bottom_tv = (TextView) findViewById(R.id.map_bottom_tv);
        search_btn = (ImageView) findViewById(R.id.search_btn);
        search_et = (AutoCompleteTextView) findViewById(R.id.search_et);
        search_rl = (PercentRelativeLayout) findViewById(R.id.search_rl);
        spf = SharedPreferenceUtil.initSharedPerence().init(DhAddressMapActivity.this, MyConstant.APP_SPF_NAME);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        initData();
        init();
        search_btn.setOnClickListener(this);
        search_rl.setOnClickListener(this);
        search_et.addTextChangedListener(this);
        search_et.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                    String search_str = search_et.getText().toString();
                    if (TextUtils.isEmpty(search_str)) {
                        MainTools.ShowToast(DhAddressMapActivity.this, "搜索内容不能为空");
                        return true;
                    } else {
                        String newText = search_et.getText().toString().trim();
                        Inputtips inputTips = new Inputtips(DhAddressMapActivity.this,
                                new Inputtips.InputtipsListener() {

                                    @Override
                                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                                        if (rCode == 0) {// 正确返回
                                            tipsList = tipList;
                                            LocationSearchAdapter aAdapter = new LocationSearchAdapter(DhAddressMapActivity.this);
                                            aAdapter.setmObjects(tipList);
                                            search_et.setAdapter(aAdapter);
                                            aAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                        try {
                            inputTips.requestInputtips(newText, "上海");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
                        } catch (com.amap.api.services.core.AMapException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        /**
         * 搜索结果模块的点击事件
         */
        search_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeInput(search_et);
                Tip myTip = tipsList.get(position);
                if (myTip != null && myTip.getPoint() != null) {
                    EventBus.getDefault().post(new AddressEvent(MyConstant.EVENTBUS_ADD_ADDR, myTip.getDistrict() + myTip.getName(), myTip.getName(), myTip.getPoint().getLongitude() + "", myTip.getPoint().getLatitude() + ""));
                    finish();
                } else {
                    MainTools.ShowToast(DhAddressMapActivity.this, "请选择详细的上海地址");
                    search_et.setText(myTip.getName());
                }
//                if (myTip.getPoint() != null) {
//                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertToLatLng(myTip.getPoint()), 16.785133f));
//                }
//                search_et.setText(myTip.getName());
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        longtitude = intent.getStringExtra("longtitude");
        latitude = intent.getStringExtra("latitude");
    }

    /**
     * @param etView etView
     *               关闭软键盘
     */
    private void closeInput(View etView) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etView.getWindowToken(), 0);
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            /**
             * 设置地图默认的比例尺是否显示
             */
            mUiSettings.setScaleControlsEnabled(false);
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
            setUpMap();
        }
        adapter = new MapAddressCycAdapter(DhAddressMapActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void setUpMap() {

        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.dh_location_point_map));
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(1);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setOnCameraChangeListener(this);
        if (!TextUtils.isEmpty(longtitude) && !TextUtils.isEmpty(latitude) && !longtitude.equals("0.0") && !latitude.equals("0.0")) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Tools.toFloat(latitude), Tools.toFloat(longtitude)), 16.785133f));// 设置指定的可视区域地图
        } else {
            if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf)) && !TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf))) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Tools.toFloat(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf)), Tools.toFloat(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf))), 16.785133f));// 设置指定的可视区域地图
            } else {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.238068, 121.501654), 16.785133f));// 设置指定的可视区域地图
            }
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                if (locateNum > 0) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()), 16.785133f));
                }
                locateNum++;
            }
        }
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setOnceLocation(true);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onCameraChange(CameraPosition posi) {

    }

    public void playWithAfter(ImageView view) {
        if (animatorObj != null) {
            animatorObj.cancel();
            animatorObj = ObjectAnimator.ofPropertyValuesHolder(view, pvhY, pvhSX, pvhSY).setDuration(1000);
            animatorObj.start();
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition posi) {
        playWithAfter(mark_ivx);
        query = new PoiSearch.Query("", MyConstant.poiStr, "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        query.setLimitDiscount(false);
        query.setLimitGroupbuy(false);
        LatLonPoint lp = new LatLonPoint(posi.target.latitude, posi.target.longitude);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(lp, 500, true));//
        // 设置搜索区域为以lp点为圆心，其周围2000米范围
            /*
             * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
        poiSearch.searchPOIAsyn();// 异步搜索
        showBottomControl(true, true, "正在搜索");

    }

    private void showBottomControl(boolean isShow, boolean isrlShow, String content) {
        map_bottom_rl.setVisibility(isrlShow ? View.VISIBLE : View.GONE);
        map_bottom_pb.setVisibility(isShow ? View.VISIBLE : View.GONE);
        map_bottom_tv.setText(content);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_address_map;
    }

    @Override
    protected String initToolbarTitle() {
        return "定位";
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

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
//        showDialog();
        GeocodeQuery query = new GeocodeQuery(name, "021");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * POI搜索回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                showBottomControl(false, false, "");
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiResult.getSearchSuggestionKeywords();
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
//                        aMap.clear();// 清理之前的图标
//                        poiOverlay = new PoiOverlay(aMap, poiItems);
//                        poiOverlay.removeFromMap();
//                        poiOverlay.addToMap();
//                        poiOverlay.zoomToSpan();
                        adapter.setmData(poiItems);
                        adapter.notifyDataSetChanged();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
//                        showSuggestCity(suggestionCities);
                    } else {
//                        ToastUtil.show(PoiAroundSearchActivity.this,
//                                R.string.no_result);
                        showBottomControl(false, true, "抱歉!无法获取到您的位置");
                    }
                }
            } else {
//                ToastUtil
//                        .show(PoiAroundSearchActivity.this, R.string.no_result);
                showBottomControl(false, true, "抱歉!无法获取到您的位置");
            }
        } else if (rCode == 27) {
//            ToastUtil
//                    .show(PoiAroundSearchActivity.this, R.string.error_network);
            showBottomControl(false, true, "网络异常");
        } else if (rCode == 32) {
//            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_key);
        } else {
//            ToastUtil.show(PoiAroundSearchActivity.this,getString(R.string.error_other) + rCode);
        }

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertToLatLng(address.getLatLonPoint()), 16.785133f));
            } else {
                MainTools.ShowToast(DhAddressMapActivity.this, "您要搜索的地址在上海不存在");
            }

        } else if (rCode == 27) {
//            ToastUtil.show(GeocoderActivity.this, R.string.error_network);
            MainTools.ShowToast(DhAddressMapActivity.this, "error_network");
        } else if (rCode == 32) {
//            ToastUtil.show(GeocoderActivity.this, R.string.error_key);
            MainTools.ShowToast(DhAddressMapActivity.this, "error_key");
        } else {
//            ToastUtil.show(GeocoderActivity.this,
//                    getString(R.string.error_other) + rCode);
            MainTools.ShowToast(DhAddressMapActivity.this, "error_other");
        }
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    private LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                if (mlocationClient != null)
                    mlocationClient.startLocation();
                break;
            case R.id.search_rl:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                String search_str = search_et.getText().toString();
                if (TextUtils.isEmpty(search_str)) {
                    MainTools.ShowToast(DhAddressMapActivity.this, "搜索内容不能为空");
                } else {
                    String newText = search_et.getText().toString().trim();
                    Inputtips inputTips = new Inputtips(DhAddressMapActivity.this,
                            new Inputtips.InputtipsListener() {

                                @Override
                                public void onGetInputtips(List<Tip> tipList, int rCode) {
                                    if (rCode == 0) {// 正确返回
                                        tipsList = tipList;
                                        LocationSearchAdapter aAdapter = new LocationSearchAdapter(DhAddressMapActivity.this);
                                        aAdapter.setmObjects(tipList);
                                        search_et.setAdapter(aAdapter);
                                        aAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                    try {
                        inputTips.requestInputtips(newText, "上海");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
                    } catch (com.amap.api.services.core.AMapException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String newText = s.toString().trim();
        Inputtips inputTips = new Inputtips(DhAddressMapActivity.this,
                new Inputtips.InputtipsListener() {

                    @Override
                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                        if (rCode == 0) {// 正确返回
                            tipsList = tipList;
                            LocationSearchAdapter aAdapter = new LocationSearchAdapter(DhAddressMapActivity.this);
                            aAdapter.setmObjects(tipList);
                            search_et.setAdapter(aAdapter);
                            aAdapter.notifyDataSetChanged();
                        }
                    }
                });
        try {
            inputTips.requestInputtips(newText, "上海");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
        } catch (com.amap.api.services.core.AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mark_ivx.clearAnimation();
    }
}
