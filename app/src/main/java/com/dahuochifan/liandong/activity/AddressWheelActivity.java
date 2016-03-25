package com.dahuochifan.liandong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.liandong.adapters.ArrayWheelAdapter;
import com.dahuochifan.liandong.widget.OnWheelChangedListener;
import com.dahuochifan.liandong.widget.WheelView;
import com.dahuochifan.utils.MainTools;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

public class AddressWheelActivity extends BaseLiandongActivity implements OnWheelChangedListener {
	@Bind(R.id.id_province)
	WheelView mViewProvince;
	@Bind(R.id.id_city)
	WheelView mViewCity;
	@Bind(R.id.cancel_tv)
	TextView cancel_tv;
	@Bind(R.id.ok_tv)
	TextView ok_tv;
	@Bind(R.id.mainrl)
	RelativeLayout main_rl;
	private int code;
	private String provinceStr;
	private String cityStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mywheel);
		ButterKnife.bind(this);
		Intent intent = getIntent();
		code = intent.getIntExtra("code", 0);
		provinceStr=intent.getStringExtra("province");
		cityStr=intent.getStringExtra("city");
		setUpListener();
		setUpData();
	}
	@OnClick(R.id.cancel_tv)
	public void cancelFunction() {
		Intent intent = new Intent();
		intent.putExtra("provice", "");
		intent.putExtra("city", "");
		setResult(code, intent);
		finish();
		overridePendingTransition(0, R.anim.fadeout);
	}
	@OnClick(R.id.ok_tv)
	public void okFunction() {
		Intent intent = new Intent();
		intent.putExtra("provice", mCurrentProviceName);
		if(mCurrentProviceName.equals("香港")||mCurrentProviceName.equals("澳门")||mCurrentProviceName.equals("台湾")){
			intent.putExtra("city", "");
		}else{
			if(!TextUtils.isEmpty(mCurrentCityName)&&!(mCurrentCityName.equals("----"))&&!(mCurrentCityName.equals("null"))){
				intent.putExtra("city", mCurrentCityName);
			}else{
				MainTools.ShowToast(AddressWheelActivity.this, "请选择市区");
				intent.putExtra("city", "");
				return;
			}
		}
		setResult(code, intent);
		finish();
		overridePendingTransition(0, R.anim.fadeout);
	}
	@OnClick(R.id.mainrl)
	public void mainFunction() {
	}
	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		// mViewDistrict.addChangingListener(this);
	}
	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(AddressWheelActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(5);
		mViewCity.setVisibleItems(5);
		updateProvince();
		updateCitiesFirst();
		updateAreas();
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[]{""};
		}
	}

	private void updateProvince() {
		for(int i=0;i<mProvinceDatas.length;i++){
			if(provinceStr.equals(mProvinceDatas[i])){
				mViewProvince.setCurrentItem(i);
			}
		}
	}
	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCitiesFirst() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[]{""};
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		for(int i=0;i<cities.length;i++){
			if(cityStr.equals(cities[i])){
				mViewCity.setCurrentItem(i);
			}
		}
	}
	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[]{""};
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra("provice", "");
			intent.putExtra("city", "");
			setResult(code, intent);
			finish();
			overridePendingTransition(0, R.anim.fadeout);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}
}
