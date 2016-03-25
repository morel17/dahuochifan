package com.dahuochifan.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.ui.activity.DhDiscountActivity;
import com.dahuochifan.ui.activity.MessageDetailActivityHome;
import com.dahuochifan.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2015/11/10.
 */
public class DhNotiService extends Service {
    private final IBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

    }

    public class MyBinder extends Binder {
        public DhNotiService getService() {
            return DhNotiService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_NOT_STICKY;
        super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.getExtras() != null) {
            showNotification(getApplicationContext(), intent.getExtras());
        }
        return flags;
    }

    protected void showNotification(Context context, Bundle bundlex) {
        String titleStr = null;
        String codeStr=null;
        String extraStr=bundlex.getString(JPushInterface.EXTRA_EXTRA);
        try {
            JSONObject jsonObject=new JSONObject(extraStr);
            String obj=jsonObject.getString("data");
            JSONObject jsonObject1=new JSONObject(obj);
            titleStr=jsonObject1.getString("title");
            codeStr=jsonObject1.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = null;
        if(!TextUtils.isEmpty(codeStr)){
            if(codeStr.equals("10003")){//优惠券通知
                EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_MESSAGE));
                i = new Intent(context, DhDiscountActivity.class);
            }else if(codeStr.equals("10002")){//系统通知
                EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                EventBus.getDefault().post(new FirstEvent("AnchorOrderShow"));
                EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_MESSAGE));
                i = new Intent(context, MessageDetailActivityHome.class);
            }else if(codeStr.equals("10001")){ //订单通知
                EventBus.getDefault().post(new FirstEvent("AnchorShow"));
                EventBus.getDefault().post(new FirstEvent("AnchorOrderShow"));
                EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
                EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_CHEF_ONE));
                EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_MESSAGE));
                i = new Intent(context, MessageDetailActivityHome.class);
            }else{
                i = new Intent(context, MessageDetailActivityHome.class);
            }
        }else{
            i = new Intent(context, MessageDetailActivityHome.class);
        }

        bundlex.putInt("code",1);
        bundlex.putString("content", bundlex.getString(JPushInterface.EXTRA_MESSAGE));
        i.putExtras(bundlex);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(i);//这样消息一来就自动进入app了
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 22,
                i, PendingIntent.FLAG_CANCEL_CURRENT);
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持

        Notification notify2 = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                        // icon)
                .setTicker("您有搭伙吃饭的新消息")// 设置在status
                        // bar上显示的提示文字
                .setContentTitle(titleStr==null?"新消息":titleStr)// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText(""+ bundlex.getString(JPushInterface.EXTRA_MESSAGE))// TextView中显示的详细内容
                .setContentIntent(pendingIntent2) // 关联PendingIntent
//                .setNumber() // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;//点击可被删除
//        notify2.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中,不能被滑动删除
        notify2.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notify2.ledARGB = Color.BLUE;
        if (Long.parseLong(bundlex.getString(JPushInterface.EXTRA_MSG_ID)) > 1000) {
            manager.notify((int) (Long.parseLong(bundlex.getString(JPushInterface.EXTRA_MSG_ID)) / 1000), notify2);
        } else {
            manager.notify(Tools.toInteger(bundlex.getString(JPushInterface.EXTRA_MSG_ID)), notify2);
        }

    }
}
