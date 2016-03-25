package com.dahuochifan.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.application.MyApplication;
import com.nostra13.universalimageloader.utils.L;

import cn.jpush.android.api.JPushInterface;

public class MorelJPushService extends Service {
	private final IBinder binder = new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		
		return binder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		if(BuildConfig.LEO_DEBUG)L.e("dy------------------1");
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(BuildConfig.LEO_DEBUG)L.e("dy------------------2");
	}
	
	
	@Override
	public boolean onUnbind(Intent intent) {
		if(BuildConfig.LEO_DEBUG)L.e("dy------------------5");
		return super.onUnbind(intent);
	}
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		if(BuildConfig.LEO_DEBUG)L.e("dy------------------3");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags=START_NOT_STICKY;
		super.onStartCommand(intent, flags, startId);
		if(JPushInterface.isPushStopped(MyApplication.getInstance())){
			JPushInterface.init(MyApplication.getInstance());
			JPushInterface.resumePush(MyApplication.getInstance());
		}
		return flags;
	}
	@Override
	public void onDestroy() {
		
		if(BuildConfig.LEO_DEBUG)L.e("dy------------------4");
		stopForeground(true);
		Intent itent =new Intent("com.dahuochifan.wjj.morelservice.destroy");
		sendBroadcast(itent);
		super.onDestroy();
	}

	public class MyBinder extends Binder {
		public MorelJPushService getService() {
			return MorelJPushService.this;
		}
	}

}
