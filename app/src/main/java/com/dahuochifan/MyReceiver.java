package com.dahuochifan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.service.DhNotiService;
import com.dahuochifan.service.MorelJPushService;
import com.dahuochifan.ui.activity.DhDiscountActivity;
import com.dahuochifan.ui.activity.MessageDetailActivityHome;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    //	public static  MediaPlayer mediaPlayer;
//	private AssetFileDescriptor afd;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (BuildConfig.LEO_DEBUG) L.e("dykai");
            Intent itnentx = new Intent(context, MorelJPushService.class);
            context.startService(itnentx);
        }
        if ("com.dahuochifan.wjj.morelservice.destroy".equals(intent.getAction())) {
            if (BuildConfig.LEO_DEBUG) L.e("dyxiao");
            Intent itnentx = new Intent(context, MorelJPushService.class);
            context.startService(itnentx);
        }
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            if (BuildConfig.LEO_DEBUG) Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            SharedPreferences spf = SharedPreferenceUtil.initSharedPerence().init(context, MyConstant.APP_SPF_NAME);
            SharedPreferences.Editor editor = spf.edit();
            SharedPreferenceUtil.initSharedPerence().putRegisterId(editor, regId);
            editor.commit();
            // send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            if (BuildConfig.LEO_DEBUG)
                Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            Intent serviceIntent = new Intent(context, DhNotiService.class);
            serviceIntent.putExtras(bundle);
            // serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(serviceIntent);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//			afd = context.getResources().openRawResourceFd(R.raw.sb);
//			if(mediaPlayer==null){
//				mediaPlayer= new MediaPlayer();
//			}else{
//				mediaPlayer.stop();
//				mediaPlayer.reset();
//			}
//			try {
//				mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//				mediaPlayer.prepare();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			mediaPlayer.start();
            String codeStr = null;
            String extraStr = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                JSONObject jsonObject = new JSONObject(extraStr);
                String obj = jsonObject.getString("data");
                L.e("obj===" + obj);
                JSONObject jsonObject1 = new JSONObject(obj);
                codeStr = jsonObject1.getString("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(codeStr)) {
                if (codeStr.equals("10003")) {//优惠券通知
                    EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_MESSAGE));
                } else if (codeStr.equals("10002")) {//系统通知
                    EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                    EventBus.getDefault().post(new FirstEvent("AnchorOrderShow"));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_MESSAGE));
                } else if ("10001".equals(codeStr)) {//订单通知
                    EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                    EventBus.getDefault().post(new FirstEvent("AnchorOrderShow"));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_ONE));
                    EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_MESSAGE));
                }
            }
            if (BuildConfig.LEO_DEBUG) Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            if (BuildConfig.LEO_DEBUG) Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            if (BuildConfig.LEO_DEBUG) Log.e(TAG, "[MyReceiver] 用户点击打开了通知");
//			if (AppManager.getAppManager() != null) {
//				AppManager.getAppManager().finishAllActivity();
//			}
            // 打开自定义的Activity
//			if(mediaPlayer!=null){
//				mediaPlayer.stop();
//				mediaPlayer.release();
//			}
            String codeStr = null;
            String extraStr = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                JSONObject jsonObject = new JSONObject(extraStr);
                String obj = jsonObject.getString("data");
                JSONObject jsonObject1 = new JSONObject(obj);
                codeStr = jsonObject1.getString("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent i = null;
            if (!TextUtils.isEmpty(codeStr)) {
                if (codeStr.equals("10003")) {//优惠券通知
                    i = new Intent(context, DhDiscountActivity.class);
                } else if (codeStr.equals("10002")) {//系统通知
                    i = new Intent(context, MessageDetailActivityHome.class);
                } else if (codeStr.equals("10001")) { //订单通知
                    i = new Intent(context, MessageDetailActivityHome.class);
                } else {
                    i = new Intent(context, MessageDetailActivityHome.class);
                }
            } else {
                i = new Intent(context, MessageDetailActivityHome.class);
            }
            bundle.putInt("code", 0);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            if (BuildConfig.LEO_DEBUG)
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            if (BuildConfig.LEO_DEBUG)
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            if (BuildConfig.LEO_DEBUG)
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
//		if(mediaPlayer!=null){
//			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//				@Override
//				public void onCompletion(MediaPlayer mp) {
//					mp.stop();
//					mp.release();
//				}
//			});
//		}
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}
