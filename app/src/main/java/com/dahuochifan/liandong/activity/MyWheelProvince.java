package com.dahuochifan.liandong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.liandong.adapters.ArrayWheelAdapter;
import com.dahuochifan.liandong.widget.OnWheelChangedListener;
import com.dahuochifan.liandong.widget.WheelView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

public class MyWheelProvince extends BaseLiandongActivity implements OnWheelChangedListener {
	@Bind(R.id.id_province)
	WheelView mViewProvince;
	@Bind(R.id.cancel_tv)
	TextView cancel_tv;
	@Bind(R.id.ok_tv)
	TextView ok_tv;
	@Bind(R.id.mainrl)
	RelativeLayout main_rl;
	private int code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mywheel_province);
		ButterKnife.bind(this);
		Intent intent = getIntent();
		code = intent.getIntExtra("code", 0);
		setUpListener();
		setUpData();
	}
	@OnClick(R.id.cancel_tv)
	public void cancelFunction() {
		Intent intent = new Intent();
		intent.putExtra("provice", "");
		setResult(code, intent);
		finish();
		overridePendingTransition(0, R.anim.fadeout);
	}
	@OnClick(R.id.ok_tv)
	public void okFunction() {
		Intent intent = new Intent();
		intent.putExtra("provice", mProvinceDatas[mViewProvince.getCurrentItem()]);
		setResult(code, intent);
		finish();
		overridePendingTransition(0, R.anim.fadeout);
	}
	@OnClick(R.id.mainrl)
	public void mainFunction() {
//		Intent intent = new Intent();
//		intent.putExtra("provice", "");
//		intent.putExtra("city", "");
//		setResult(code, intent);
//		finish();
//		overridePendingTransition(0, R.anim.fadeout);
	}
	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		// mViewDistrict.addChangingListener(this);
	}
	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(MyWheelProvince.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(5);
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra("provice", "");
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
