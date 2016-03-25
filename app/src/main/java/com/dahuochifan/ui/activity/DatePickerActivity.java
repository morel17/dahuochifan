package com.dahuochifan.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.liandong.adapters.ArrayWheelAdapter;
import com.dahuochifan.liandong.widget.OnWheelChangedListener;
import com.dahuochifan.liandong.widget.WheelView;
import com.dahuochifan.utils.Tools;
import com.nostra13.universalimageloader.utils.L;

import java.util.Calendar;

public class DatePickerActivity extends Activity implements OnWheelChangedListener{
	private WheelView wheel_year,wheel_month,wheel_day;
	private TextView ok_tv,cancel_tv;
	private Calendar ca;
	private String[] yearstr;
	private String[] monthstr=new String[12];
	private String[] day28=new String[28];
	private String[] day29=new String[29];
	private String[] day30=new String[30];
	private String[] day31=new String[31];
	private int code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		setContentView(R.layout.activity_datapicker);
		code=getIntent().getIntExtra("code", 0);
		initViews();
		setUpListener();
		setYear_Month();
		int month=ca.get(ca.MONTH);
		if(BuildConfig.LEO_DEBUG)L.e("month"+month);
		setDay(month+1);
		wheel_day.setCurrentItem(ca.get(ca.DATE)-1);
		ok_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("date", yearstr[0]+"-"+monthstr[wheel_month.getCurrentItem()]+"-"+day31[wheel_day.getCurrentItem()]);
				setResult(code, intent);
				finish();
				overridePendingTransition(0, R.anim.fadeout);
			}
		});
		cancel_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("date", "");
				setResult(code, intent);
				finish();
				overridePendingTransition(0, R.anim.fadeout);
			}
		});
	}
	private void initViews() {
		wheel_year=(WheelView)findViewById(R.id.id_year);
		wheel_month=(WheelView)findViewById(R.id.id_month);
		wheel_day=(WheelView)findViewById(R.id.id_day);
		ok_tv=(TextView)findViewById(R.id.ok_tv);
		cancel_tv=(TextView)findViewById(R.id.cancel_tv);
		ca=Calendar.getInstance();
		String curyear=ca.get(ca.YEAR)+"";
		yearstr=new String[]{curyear};
		for(int i=0;i<12;i++){
			if(i<9){
				monthstr[i]="0"+(i+1);
			}else{
				monthstr[i]=i+1+"";
			}
		}
		for(int i=0;i<28;i++){
			if(i<9){
				day28[i]="0"+(i+1);
			}else{
				day28[i]=""+(i+1);
			}
		}
		for(int i=0;i<29;i++){
			if(i<9){
				day29[i]="0"+(i+1);
			}else{
				day29[i]=""+(i+1);
			}
		}
		for(int i=0;i<30;i++){
			if(i<9){
				day30[i]="0"+(i+1);
			}else{
				day30[i]=""+(i+1);
			}
		}
		for(int i=0;i<31;i++){
			if(i<9){
				day31[i]="0"+(i+1);
			}else{
				day31[i]=""+(i+1);
			}
		}
		
	}
	private void setUpListener() {
		wheel_year.addChangingListener(this);
		wheel_month.addChangingListener(this);
		wheel_day.addChangingListener(this);
	}
	private void setYear_Month() {
		wheel_year.setVisibleItems(7);
		wheel_month.setVisibleItems(7);
		wheel_day.setVisibleItems(7);
		wheel_year.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, yearstr));
		wheel_month.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, monthstr));
		wheel_month.setCurrentItem(ca.get(ca.MONTH));
	}
	private void setDay(int month) {
		int yearnum=Tools.toInteger(yearstr[0]);
		if(isLeapYear(yearnum)){
			if(month==2){
				wheel_day.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, day29));
			}else if(month==4||month==6||month==9||month==11){
				wheel_day.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, day30));
			}else{
				wheel_day.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, day31));
			}
		}else{
			if(month==2){
				wheel_day.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, day28));
			}else if(month==4||month==6||month==9||month==11){
				wheel_day.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, day30));
			}else{
				wheel_day.setViewAdapter(new ArrayWheelAdapter<String>(DatePickerActivity.this, day31));
			}
		}
	}
	public boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
			return true;
		else
			return false;
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if(wheel == wheel_month){
			int month= Tools.toInteger(monthstr[wheel_month.getCurrentItem()]);
			setDay(month);
			wheel_day.setCurrentItem(0);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra("date", "");
			setResult(code, intent);
			finish();
			overridePendingTransition(0, R.anim.fadeout);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
