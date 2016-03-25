package com.dahuochifan.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;

import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Looper;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 * @author way
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
	private static CrashHandler INSTANCE;// CrashHandler实例
	private Context mContext;// 程序的Context对象
	private String crashReport;
	private MyAsyncHttpClient client;
	private RequestParams params;
	private Gson gson;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;

		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器

		client = new MyAsyncHttpClient();
		params = new RequestParams();
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 *            异常信息
	 * @return true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		if (ex == null || mContext == null)
			return false;
		crashReport = getCrashReport(mContext, ex);
	
		try {
			crashReport = new String(crashReport.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				File file = save2File(crashReport);
				sendAppCrashReport(mContext, crashReport, file);
				Looper.loop();
			}

		}.start();
		return true;
	}

	private File save2File(String crashReport) {
		// TODO Auto-generated method stub
		String fileName = "crash-" + System.currentTimeMillis() + ".txt";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");
				if (!dir.exists())
					dir.mkdir();
				File file = new File(dir, fileName);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(crashReport.toString().getBytes());
				fos.close();
				return file;
			} catch (Exception e) {
				// L.i("save2File error:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	private void sendAppCrashReport(final Context context, final String crashReport, final File file) {

		// final AlertDialog dialog;
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setIcon(android.R.drawable.ic_dialog_info);
		// builder.setTitle("app_error");
		// builder.setMessage("app_error_message");
		// builder.setPositiveButton("submit_report", new OnClickListener() {
		//
		// @Override
		// public void onClick(final DialogInterface dialog, int which) {
		// client.setTimeout(5000);
		// params = ParamData.getInstance().getFeedbackObj(params, "");
		// try {
		// params.put("logfile", file);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// client.post(MyConstant.URL_FEEDBACK, params, new TextHttpResponseHandler() {
		// @Override
		// public void onStart() {
		// super.onStart();
		// // pd.show();
		// L.e("diaoyong");
		// }
		// @Override
		// public void onSuccess(int arg0, Header[] arg1, String arg2) {
		// // TODO Auto-generated method stub
		// L.e(arg2 + "***");
		// }
		//
		// @Override
		// public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
		// // TODO Auto-generated method stub
		//
		// }
		// @Override
		// public void onFinish() {
		// super.onFinish();
		// // pd.dismiss();
		// dialog.dismiss();
		// }
		// });
		// }
		// });
		// builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// // 退出
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(1);
		// }
		// });
		// dialog = builder.create();
		// dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// dialog.show();
		// dialog.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface arg0) {
		// // TODO Auto-generated method stub
		// dialog.dismiss();
		// // 退出
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(1);
		// }
		// });

		client.setTimeout(5000);
		params = ParamData.getInstance().getFeedbackObj("");
		try {
			params.put("logfile", file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.post(MyConstant.URL_ADDLOG, params, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				// pd.show();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				try {
					JSONObject jobj = new JSONObject(arg2);
					int resultcode = jobj.getInt("resultcode");
					String tag = jobj.getString("tag");
					if (resultcode == 1) {
						MainTools.ShowToast(mContext, tag);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onFinish() {
				super.onFinish();
				if (file.exists()) {
					file.delete();
				}
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
		});

	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo pinfo = getPackageInfo(context);
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
		if (MyConstant.user.isLogin()) {
			exceptionStr.append("User: (" + MyConstant.user.getUsername() + ")\n");
			exceptionStr.append("NickName: (" + MyConstant.user.getNickname() + ")\n");
		}
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	private PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// e.printStackTrace(System.err);
			// Log.i("getPackageInfo err = " + e.getMessage());
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

}