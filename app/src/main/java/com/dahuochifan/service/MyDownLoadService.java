package com.dahuochifan.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.requestdata.UpdateData;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.utils.L;

public class MyDownLoadService extends Service{
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;
	private PackageInfo pinfo;
	private PackageManager manager;
	private Thread downLoadThread;
	private String apkUrl;
	private int vn ;
	private String savePath=Environment.getExternalStorageDirectory() + "/" + "dahuochifan" + "/"+"/" + vn + "/";
	private String saveFileName = savePath + "Dahuochifan.apk";
	private SharedPreferences spf;
	private Editor editor;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			
		};
	};
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		client=new MyAsyncHttpClient();
		params=new RequestParams();
		gson=new Gson();
		manager = getPackageManager();
		try {
			pinfo = manager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		spf=getSharedPreferences(MyConstant.APP_SPF_NAME, MODE_PRIVATE);
		editor=spf.edit();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		doDownLoad();
		
	}
	private void doDownLoad() {
		client.post(MyConstant.URL_UPDATE, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				final UpdateData data = new UpdateData();
//				if (data.dealData(responseString,gson,getApplicationContext()) == 1) {
//					UpdateAll updateall = data.getObj();
//					String versionname_old = pinfo.versionName.replace(".", "");
//					String versionname_new = updateall.getObj().getVersionname().replace(".", "");
//					int vo = Integer.parseInt(versionname_old);
//					vn = Integer.parseInt(versionname_new);
//					apkUrl= updateall.getObj().getUrl();
//					if (pinfo.versionCode < updateall.getObj().getVersioncode() || vo < vn) {
//						if(SharedPerenceUtil.initSharedPerence().getinstallInfo(spf).equals(vn+"true")){
//							downloadApk();
//						}
//					}
//				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			}
		});
	}
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				System.out.println("ApkFile" + ApkFile.exists());
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					if(BuildConfig.LEO_DEBUG)L.e((int) (((float) count / length) * 100)+"***");
//					progress = (int) (((float) count / length) * 100);
					// 更新进度
//					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
//						mHandler.sendEmptyMessage(DOWN_OVER);
						SharedPreferenceUtil.initSharedPerence().putInstallInfo(editor, vn+"true");
						editor.commit();
						break;
					}
					fos.write(buf, 0, numread);
				} while (true);// 点击取消就停止下载.
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};


	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
