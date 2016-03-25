package com.dahuochifan.liandong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.liandong.adapters.ArrayWheelAdapter;
import com.dahuochifan.liandong.widget.OnWheelChangedListener;
import com.dahuochifan.liandong.widget.WheelView;

public class AgeApplyWheelActivity extends BaseActivity implements OnWheelChangedListener {
	private WheelView age_wheel;
	private TextView ok_tv;
	private TextView cancel_tv;
	private String age[];
	private int code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agewheel);
		initviews();
		setUpListener();
		setUpData();
	}
	private void setUpListener() {
		age_wheel.addChangingListener(this);
		ok_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String agestr = age[age_wheel.getCurrentItem()];
				/*
				 * String agestr=age[age_wheel.getCurrentItem()]; Intent intent = new Intent(); intent.putExtra("age", agestr); setResult(code, intent);
				 * finish(); overridePendingTransition(0, R.anim.fadeout);
				 */
				Intent intent = new Intent();
				intent.putExtra("age", agestr);
				setResult(code, intent);
				finish();
				overridePendingTransition(0, R.anim.fadeout);
			}
		});
		cancel_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("age", "");
				setResult(code, intent);
				finish();
				overridePendingTransition(0, R.anim.fadeout);
			}
		});
	}
	private void setUpData() {
		age_wheel.setViewAdapter(new ArrayWheelAdapter<String>(AgeApplyWheelActivity.this, age));
	}
	private void initviews() {
		code = getIntent().getIntExtra("code", 0);
		ok_tv = (TextView) findViewById(R.id.ok_tv);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		age_wheel = (WheelView) findViewById(R.id.age_wheel);
		age = new String[]{"60后", "70后", "80后", "90后", "00后"};
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra("age", "");
			setResult(code, intent);
			finish();
			overridePendingTransition(0, R.anim.fadeout);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
