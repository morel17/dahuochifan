package com.dahuochifan.ui.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

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
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.dahuochifan.ui.views.CircleImageView;

public class DhShikeLocationActivity extends BaseActivity implements LocationSource, AMapLocationListener, RouteSearch.OnRouteSearchListener {
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;

    private LocationSource.OnLocationChangedListener mListener;
    private SharedPreferences spf;
    private double lat, longti;
    private int busMode;// 公交默认模式
    private int drivingMode;// 驾车默认模式
    private int walkMode;// 步行默认模式
    private ProgressDialog pd;

    /**
     * 关于定位
     */
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        AppManager.getAppManager().addActivity(this);
        mapView = (MapView) findViewById(R.id.map);
        pd=new ProgressDialog(this);
        spf = SharedPreferenceUtil.initSharedPerence().init(DhShikeLocationActivity.this, MyConstant.APP_SPF_NAME);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    private void initData() {
        lat = getIntent().getDoubleExtra("lat", 0);
        longti = getIntent().getDoubleExtra("long", 0);
        busMode = RouteSearch.BusDefault;// 公交默认模式
        drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
        walkMode = RouteSearch.WalkDefault;// 步行默认模式
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
    }

    private void setUpMap() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconfont));
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(1);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aMap.getCameraPosition().target,15));
        CircleImageView circleImageView = new CircleImageView(DhShikeLocationActivity.this);
        circleImageView.setBorderWidth(4);
        circleImageView.setBorderColor(getResources().getColor(R.color.maincolor_new));
        circleImageView.setImageResource(R.mipmap.dh_info_backwhtie);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longti), 18.785133f));// 设置指定的可视区域地图
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f)
                .position(new LatLng(lat, longti)).title("食客位置").icon(BitmapDescriptorFactory
                        .fromView(circleImageView))
                .draggable(true));
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
        if(pd!=null){
            pd.dismiss();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()), 18.785133f));
                LatLonPoint startPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                LatLonPoint endPoint = new LatLonPoint(lat, longti);
                RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
                RouteSearch routeSearch = new RouteSearch(this);//初始化routeSearch 对象
                routeSearch.setRouteSearchListener(this);//设置数据回调监听器

                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, walkMode);//初始化query对象，fromAndTo是包含起终点信息，walkMode是不行路径规划的模式
//        if(GDLat!=0&&GDLong!=0){
//            routeSearch.calculateWalkRouteAsyn(query);//开始算路
//        }else{
//            MainTools.ShowToast(DhShikeLocationActivity.this,"定位数据有误");
//        }
                routeSearch.calculateWalkRouteAsyn(query);//开始算路
            }else{
                if(pd!=null){
                    pd.dismiss();
                }
            }
        }else{
            if(pd!=null){
                pd.dismiss();
            }
        }
        if(mlocationClient!=null){
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
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
    protected int getLayoutView() {
        return R.layout.activity_dh_shike_location;
    }

    @Override
    protected String initToolbarTitle() {
        return "配送线路";
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
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int rCode) {
        // 路径规划中驾车模式
        if (rCode == 0) {

            if (driveRouteResult != null && driveRouteResult.getPaths() !=
                    null && driveRouteResult.getPaths().size() > 0) {
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();//清理之前的图标
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        this, aMap, drivePath, driveRouteResult.getStartPos(),
                        driveRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
                MainTools.ShowToast(DhShikeLocationActivity.this, "没有搜索到结果");
            }
        } else {
            MainTools.ShowToast(DhShikeLocationActivity.this, "网络错误");
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int rCode) {
        if(pd!=null){
            pd.dismiss();
        }
        if (rCode == 0) {
            if (walkRouteResult != null && walkRouteResult.getPaths() !=
                    null && walkRouteResult.getPaths().size() > 0) {
                WalkPath drivePath = walkRouteResult.getPaths().get(0);
                aMap.clear();//清理之前的图标
                WalkRouteOverlay drivingRouteOverlay = new WalkRouteOverlay(
                        this, aMap, drivePath, walkRouteResult.getStartPos(),
                        walkRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
//                Toast.makeText(RoutePlanningActivity.this, "没有搜索到结果", Toast.LENGTH_SHORT).show();
                MainTools.ShowToast(DhShikeLocationActivity.this, "没有搜索到结果");
            }
        } else {
            MainTools.ShowToast(DhShikeLocationActivity.this, "网络错误");
        }
    }

}
