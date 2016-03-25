package com.dahuochifan.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.RelativeLayout;

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
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.cheflist.ChefList;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.requestdata.CookData;
import com.dahuochifan.ui.adapter.LocationSearchAdapter;
import com.dahuochifan.ui.views.CircleImageView;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MapLoader;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.utils.Tools;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.BuildConfig;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

//import com.amap.api.location.LocationManagerProxy;
//import com.amap.api.location.LocationProviderProxy;

//import com.amap.api.maps2d.AMap;
//import com.amap.api.maps2d.LocationSource;
//import com.amap.api.maps2d.MapView;

public class ChefSearchActivity extends BaseActivity implements LocationSource, AMapLocationListener
        , AMap.OnCameraChangeListener, View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener
        , TextWatcher, AMap.OnMarkerClickListener {
    private AMap aMap;
    private MapView mapView;

    private SharedPreferences spf;

    private RequestParams params;
    private Gson gson;

    private AutoCompleteTextView search_et;

    private boolean isMarkClick;
    private int locateNum;
    private ChefList chef;
    List<ChefList> listChef;
    private double lati, longti;
    private ImageView mark_iv;

    private List<Tip> tipsList = new ArrayList<>();
    private OnLocationChangedListener mListener;
    ObjectAnimator animatorObj;
    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;

    PropertyValuesHolder pvhY, pvhSX, pvhSY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mapView = (MapView) findViewById(R.id.map);
        mark_iv = (ImageView) findViewById(R.id.mark_iv);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
        mark_iv.post(new Runnable() {
            @Override
            public void run() {
                pvhY = PropertyValuesHolder.ofFloat("y", mark_iv.getTop(), mark_iv.getTop() - 66, mark_iv.getTop());
                if (pvhY != null) {
                    animatorObj = ObjectAnimator.ofPropertyValuesHolder(mark_iv, pvhY, pvhSX, pvhSY).setDuration(1000);
                }
            }
        });
        params = new RequestParams();
        gson = new Gson();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        ImageView btn = (ImageView) findViewById(R.id.search_btn);
        PercentRelativeLayout search_rl = (PercentRelativeLayout) findViewById(R.id.search_rl);
        btn.setOnClickListener(this);
        search_rl.setOnClickListener(this);
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        search_et = (AutoCompleteTextView) findViewById(R.id.search_et);
        search_et.addTextChangedListener(this);
        search_et.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                    String search_str = search_et.getText().toString();
                    if (TextUtils.isEmpty(search_str)) {
                        MainTools.ShowToast(ChefSearchActivity.this, "搜索内容不能为空");
                        return true;
                    } else {
                        String newText = search_et.getText().toString().trim();
                        Inputtips inputTips = new Inputtips(ChefSearchActivity.this,
                                new Inputtips.InputtipsListener() {

                                    @Override
                                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                                        if (rCode == 0) {// 正确返回
                                            tipsList = tipList;
                                            LocationSearchAdapter aAdapter = new LocationSearchAdapter(ChefSearchActivity.this);
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
        search_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
                Tip myTip = tipsList.get(position);
                if (myTip.getPoint() != null) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertToLatLng(myTip.getPoint()), 16.785133f));
                }
                search_et.setText(myTip.getName());
//                getLatlon(myTip.getName())

            }
        });
    }

    private void init() {
        spf = SharedPreferenceUtil.initSharedPerence().init(ChefSearchActivity.this, MyConstant.APP_SPF_NAME);
        if (aMap == null) {
            aMap = mapView.getMap();
            UiSettings mUiSettings = aMap.getUiSettings();
            /**
             * 设置地图默认的比例尺是否显示
             */
            mUiSettings.setScaleControlsEnabled(false);
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
            setUpMap();
        }
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
//        aMap.setMyLocationStyle(AMap.MAP_TYPE_NORMAL);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        if (!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf)) && !TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf))) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Tools.toFloat(SharedPreferenceUtil.initSharedPerence().getGDLatitude(spf)), Tools.toFloat(SharedPreferenceUtil.initSharedPerence().getGDLongitude(spf))), 16.785133f));// 设置指定的可视区域地图
        } else {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.238068, 121.501654), 16.785133f));// 设置指定的可视区域地图
        }

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
                    MainTools.ShowToast(ChefSearchActivity.this, "搜索内容不能为空");
                    return;
                } else {
                    if (tipsList != null && tipsList.size() > 0) {
                        Tip myTip = tipsList.get(0);
                        if (myTip.getPoint() != null) {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertToLatLng(myTip.getPoint()), 16.785133f));
                        }
                        search_et.setText(myTip.getName());
                    } else {
                        MainTools.ShowToast(ChefSearchActivity.this, "没有搜索结果");
                    }
                }
                break;
        }
    }

//    /**
//     * 响应地理编码
//     */
//    public void getLatlon(final String name) {
//        query = new PoiSearch.Query(name, "", "021");
//        query.setPageSize(10);// 设置每页最多返回多少条poiitem
//        query.setPageNum(1);//设置查询页码
//        poiSearch = new PoiSearch(this, query);
//        poiSearch.setOnPoiSearchListener(this);
//        poiSearch.searchPOIAsyn();// 异步搜索
//    }

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
                MainTools.ShowToast(ChefSearchActivity.this, "您要搜索的地址在上海不存在");
            }

        } else if (rCode == 27) {
            MainTools.ShowToast(ChefSearchActivity.this, "error_network");
        } else if (rCode == 32) {
            MainTools.ShowToast(ChefSearchActivity.this, "error_key");
        } else {
            MainTools.ShowToast(ChefSearchActivity.this, "error_other");
        }
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    private LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_chesearch;
    }

    @Override
    protected String initToolbarTitle() {
        return "搜索主厨";
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
            if (amapLocation.getErrorCode() == 0) {
                if (locateNum > 0) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()), 16.785133f));
                    lati = amapLocation.getLatitude();
                    longti = amapLocation.getLongitude();
                }
                locateNum++;
            } else {
                String errText = "定位失败," + amapLocation.getErrorInfo();
                MainTools.ShowToast(ChefSearchActivity.this, errText);
            }
        }
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void onCameraChange(CameraPosition posi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCameraChangeFinish(CameraPosition posi) {
        playWithAfter(mark_iv);
        if (!isMarkClick) {
            double zoomLong = -3000 * posi.zoom + 50000;
            if (zoomLong <= 1000) {
                zoomLong = 1000;
            }
            getAroundChef(posi.target.latitude + "", posi.target.longitude + "", zoomLong + "");
            if (BuildConfig.DEBUG) L.e(posi.zoom + "zoomLong===" + zoomLong);
        } else {
            isMarkClick = false;
        }
    }

    public void playWithAfter(ImageView view) {
        if (animatorObj != null) {
            animatorObj.cancel();
            animatorObj = ObjectAnimator.ofPropertyValuesHolder(view, pvhY, pvhSX, pvhSY).setDuration(1000);
            animatorObj.start();
        }
    }

    private void getAroundChef(String latitude, String longitude, String distance) {
        params = ParamData.getInstance().getChefFromMap("", latitude, longitude, distance);
        baseClient.post(MyConstant.APP_CHEF_MAP_URL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                CookData data = new CookData();
                if (data.dealMapData(responseString, ChefSearchActivity.this, gson) == 1) {
                    if (aMap != null) {
                        aMap.clear();
                    }
                    listChef = data.getChef();
                    if (listChef != null && listChef.size() > 0)
                        if (listChef.size() > 9) {
                            for (int i = 0; i < 9; i++) {
                                chef = listChef.get(i);
                                final LatLng lat = new LatLng(Tools.toFloat(chef.getLatitude()), Tools.toFloat(chef.getLongitude()));
                                int mywidth = MainTools.getwidth(ChefSearchActivity.this) / 10;
                                View view = LayoutInflater.from(ChefSearchActivity.this).inflate(R.layout.map_headview, null);
                                RelativeLayout linearLayout = (RelativeLayout) view.findViewById(R.id.head_rl);
//                            CircleImageView circleImageView = new CircleImageView(ChefSearchActivity.this);
                                CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.head_cir);
//                            circleImageView.setBorderColor(getResources().getColor(R.color.cook_color));
                                MapLoader.loadImage(chef.getAvatar() + "?imageView2/1/w/" + mywidth + "/h/" + mywidth + "/q/" + MyConstant.QUALITY, circleImageView, aMap, chef, lat, linearLayout);
                                if (lati != 0 && longti != 0) {
                                    LatLng latx = new LatLng(lati, longti);
                                    aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f)
                                            .position(latx).title("当前位置").icon(BitmapDescriptorFactory.fromResource(R.mipmap.dh_location_point_map))
                                            .draggable(true));
                                }
                            }
                        } else {
                            for (int i = 0; i < listChef.size(); i++) {
                                chef = listChef.get(i);
                                final LatLng lat = new LatLng(Tools.toFloat(chef.getLatitude()), Tools.toFloat(chef.getLongitude()));
                                int mywidth = MainTools.getwidth(ChefSearchActivity.this) / 10;
                                View view = LayoutInflater.from(ChefSearchActivity.this).inflate(R.layout.map_headview, null);
                                RelativeLayout linearLayout = (RelativeLayout) view.findViewById(R.id.head_rl);
//                            CircleImageView circleImageView = new CircleImageView(ChefSearchActivity.this);
                                CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.head_cir);
//                            circleImageView.setBorderColor(getResources().getColor(R.color.cook_color));
                                MapLoader.loadImage(chef.getAvatar() + "?imageView2/1/w/" + mywidth + "/h/" + mywidth + "/q/" + MyConstant.QUALITY, circleImageView, aMap, chef, lat, linearLayout);
                                if (lati != 0 && longti != 0) {
                                    LatLng latx = new LatLng(lati, longti);
                                    aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f)
                                            .position(latx).title("当前位置").icon(BitmapDescriptorFactory.fromResource(R.mipmap.dh_location_point_map))
                                            .draggable(true));
                                }
                            }
                        }
                }
            }

        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        Inputtips inputTips = new Inputtips(ChefSearchActivity.this,
                new Inputtips.InputtipsListener() {

                    @Override
                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                        if (rCode == 0) {// 正确返回
                            tipsList = tipList;
                            LocationSearchAdapter aAdapter = new LocationSearchAdapter(ChefSearchActivity.this);
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
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        isMarkClick = true;
        if (listChef != null) {
            Intent intent = new Intent(ChefSearchActivity.this, ChefDetailActivity.class);
            for (int i = 0; i < listChef.size(); i++) {
                if (listChef.get(i).getNickname().equals(marker.getTitle())) {
                    intent.putExtra("myids", listChef.get(i).getChefids());
                    intent.putExtra("tag", "");
                    startActivity(intent);
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
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
    protected void onStop() {
        super.onStop();
        mark_iv.clearAnimation();
    }
    //    @Override
//    public View getInfoWindow(Marker marker) {
//        View infoContent = getLayoutInflater().inflate(
//                R.layout.custominfo_window, null);
//        render(marker, infoContent);
//        return infoContent;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        View infoContent = getLayoutInflater().inflate(
//                R.layout.custominfo_window, null);
//        render(marker, infoContent);
//        return infoContent;
//    }
//    /**
//     * 自定义infowinfow窗口
//     */
//    public void render(Marker marker, View view) {
//        String title = marker.getTitle();
//       ImageView img= ((ImageView) view.findViewById(R.id.badge));
//
//        TextView titleUi = ((TextView) view.findViewById(R.id.title));
//        if (title != null) {
//            SpannableString titleText = new SpannableString(title);
//            titleUi.setText(titleText);
//
//        } else {
//            titleUi.setText("");
//        }
//        String snippet = marker.getSnippet();
//		TextView snippetUi = ((TextView) view.findViewById(R.id.snippetx));
//		if (snippet != null) {
//			SpannableString snippetText = new SpannableString(snippet);
//            snippetUi.setVisibility(View.VISIBLE);
//            L.e(snippetText.toString());
//			snippetUi.setText(snippetText.toString());
//            snippetUi.setTextColor(getResources().getColor(R.color.maincolor_new));
//		} else {
//			snippetUi.setText("");
//            snippetUi.setVisibility(View.GONE);
//            img.setVisibility(View.GONE);
//		}
//    }
}
