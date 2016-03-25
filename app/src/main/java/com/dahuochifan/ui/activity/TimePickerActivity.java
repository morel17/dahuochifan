package com.dahuochifan.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.liandong.adapters.ArrayWheelAdapter;
import com.dahuochifan.liandong.widget.OnWheelChangedListener;
import com.dahuochifan.liandong.widget.WheelView;
import com.dahuochifan.utils.MainTools;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class TimePickerActivity extends Activity implements OnWheelChangedListener {
	private WheelView wheel_hour;
	private TextView ok_tv, cancel_tv;
	private String timeStr;
	private String[] hourstr1 = new String[]{"11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00"};
	private String[] hourstr2 = new String[]{"17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"};
	private String[] midStr;
	private String[] nightStr;
	private Calendar ca;
	private int code;
	private int hour;
	private int minute;
	private int day_length;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		setContentView(R.layout.activity_timepicker);
		code=getIntent().getIntExtra("code", 0);
		timeStr=getIntent().getStringExtra("time");
		day_length=getIntent().getIntExtra("length", 0);
	
		initViews();
		setUpListener();
		setHour_Minute();
		ok_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String curStr="";
				if(code==MyConstant.REQUESTCODEQ_TIME_1){
					if(wheel_hour.getViewAdapter().getItemsCount()<7){
						curStr=midStr[wheel_hour.getCurrentItem()];
					}else{
						curStr=hourstr1[wheel_hour.getCurrentItem()];
					}
				}else{
					if(wheel_hour.getViewAdapter().getItemsCount()<7){
						curStr=nightStr[wheel_hour.getCurrentItem()];
					}else{
						curStr=hourstr2[wheel_hour.getCurrentItem()];
					}
				}
				
				if(MainTools.isMore3Hours(curStr)){
					Intent intent = new Intent();
					//intent.putExtra("time", hourstr[wheel_hour.getCurrentItem()]+":"+minutestr[wheel_minute.getCurrentItem()]);
					if(code==MyConstant.REQUESTCODEQ_TIME_1){
						intent.putExtra("time", curStr);
					}else{
						intent.putExtra("time", curStr);
					}
					setResult(code, intent);
					finish();
					overridePendingTransition(0, R.anim.fadeout);
				}else{
					if(day_length==1){
						if(wheel_hour.getCurrentItem()==wheel_hour.getViewAdapter().getItemsCount()-1){
							if(code==MyConstant.REQUESTCODEQ_TIME_1){
								MainTools.ShowToast(TimePickerActivity.this,"已经过上午11点，您已经不能点午餐啦");
								EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CONFIRM_ORDER));
							}else{
								MainTools.ShowToast(TimePickerActivity.this,"已经过下午5点，您已经不能点晚餐啦");
								EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CONFIRM_ORDER));
							}
						}else{
//							MainTools.ShowToast(TimePickerActivity.this,"当前时间段已经不能点餐了，您可以尝试选择"+MainTools.getMore3Hours());
						}
					}else if(day_length==2){
						Intent intent = new Intent();
						//intent.putExtra("time", hourstr[wheel_hour.getCurrentItem()]+":"+minutestr[wheel_minute.getCurrentItem()]);
						if(code==MyConstant.REQUESTCODEQ_TIME_1){
							intent.putExtra("time", hourstr1[wheel_hour.getCurrentItem()]);
						}else{
							intent.putExtra("time", hourstr2[wheel_hour.getCurrentItem()]);
						}
						setResult(code, intent);
						finish();
						overridePendingTransition(0, R.anim.fadeout);
					}
					
				}
			}
		});
		cancel_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("time", "");
				setResult(code, intent);
				finish();
				overridePendingTransition(0, R.anim.fadeout);
			}
		});
	}
	private void initViews() {
		ca = Calendar.getInstance();
		wheel_hour = (WheelView) findViewById(R.id.id_hour);
		ok_tv = (TextView) findViewById(R.id.ok_tv);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		hour = ca.get(ca.HOUR_OF_DAY);
		minute = ca.get(ca.MINUTE);
//		timeStr = MainTools.getMore3Hours();

	}
	private void setUpListener() {
		wheel_hour.addChangingListener(this);
	}
	private void setHour_Minute() {
		wheel_hour.setVisibleItems(5);
		if (code == MyConstant.REQUESTCODEQ_TIME_1) {
			int index = -1;
			if (day_length == 1) {
				for (int i = 0; i < hourstr1.length; i++) {
					if (timeStr.equals(hourstr1[i])) {
						if (i != 0) {
							midStr = new String[hourstr1.length - i];
							for (int j = 0; j < midStr.length; j++) {
								midStr[j] = hourstr1[j + i];
							}
							index = 0;
							wheel_hour.setViewAdapter(new ArrayWheelAdapter<String>(TimePickerActivity.this, midStr));
							wheel_hour.setCurrentItem(index);
						}
					}
				}
			}
			if (index == -1) {
				wheel_hour.setViewAdapter(new ArrayWheelAdapter<String>(TimePickerActivity.this, hourstr1));
				for (int i = 0; i < hourstr1.length; i++) {
					if (timeStr.equals(hourstr1[i])) {
						index = i;
					}
				}
				if (index != -1&&day_length==1) {
					wheel_hour.setCurrentItem(index);
				}else{
					wheel_hour.setCurrentItem(0);
				}
			}

		} else {
			int index = -1;
			if (day_length == 1) {
				for (int i = 0; i < hourstr2.length; i++) {
					if (timeStr.equals(hourstr2[i])) {
						if (i != 0) {
							nightStr = new String[hourstr2.length - i];
							for (int j = 0; j < nightStr.length; j++) {
								nightStr[j] = hourstr2[j + i];
							}
							index = 0;
							wheel_hour.setViewAdapter(new ArrayWheelAdapter<String>(TimePickerActivity.this, nightStr));
							wheel_hour.setCurrentItem(0);
						}
					}
				}
			}
			if (index == -1) {
				wheel_hour.setViewAdapter(new ArrayWheelAdapter<String>(TimePickerActivity.this, hourstr2));
				for (int i = 0; i < hourstr2.length; i++) {
					if (timeStr.equals(hourstr2[i])) {
						index = i;
					}
				}
				if (index != -1&&day_length==1) {
					wheel_hour.setCurrentItem(index);
				}else{
					wheel_hour.setCurrentItem(0);
				}
			}
		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra("time", "");
			setResult(code, intent);
			finish();
			overridePendingTransition(0, R.anim.fadeout);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

}
