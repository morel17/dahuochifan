package com.dahuochifan.ui.activity;

import android.app.Activity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatePickerActivity2 extends Activity implements OnWheelChangedListener{
	private WheelView wheel_year;
	private TextView ok_tv,cancel_tv;
	private Calendar ca;
	private String[] yearstr;
	private int code;
	private String[] str=new String[7];
	private ArrayList<String>list=new ArrayList<String>();
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_datapicker2);
			code=getIntent().getIntExtra("code", 0);
			initViews();
			setUpListener();
			ok_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("date", list.get(wheel_year.getCurrentItem()));
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
			ok_tv=(TextView)findViewById(R.id.ok_tv);
			cancel_tv=(TextView)findViewById(R.id.cancel_tv);
			ca=Calendar.getInstance();
			list.clear();
			for(int i=0;i<7;i++){
				str[i]=returnDate(i);
			}
		}
		public String returnDate(int count) {
			Calendar strDate = Calendar.getInstance();
			strDate.add(strDate.DATE, count);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			list.add(sdf2.format(strDate.getTime()));
			return sdf.format(strDate.getTime());
		}
		private void setUpListener() {
			wheel_year.addChangingListener(this);
			wheel_year.setViewAdapter(new ArrayWheelAdapter<>(DatePickerActivity2.this, str));
			wheel_year.setCurrentItem(0);
		}
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			
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
