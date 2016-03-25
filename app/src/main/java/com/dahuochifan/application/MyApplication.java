package com.dahuochifan.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.bean.User;
import com.dahuochifan.dao.DaoMaster.DevOpenHelper;
import com.dahuochifan.service.MorelJPushService;
import com.dahuochifan.utils.Tools;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication application;
    private MorelJPushService countService;

    public static DisplayImageOptions mNormalImageOptions;
    public static DisplayImageOptions mRoundImageOptions;

    private List<OnLowMemoryListener> mLowMemoryListeners = new ArrayList<OnLowMemoryListener>();
    final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final static int cacheSize = maxMemory / 8;

    /**
     * 低内存监听接口
     */
    public interface OnLowMemoryListener {
        public void onLowMemoryReceived();
    }

    public static MyApplication getInstance() {
        if (application == null)
            application = new MyApplication();
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
//		CrashHandler.getInstance().init(this);
//		JPushInterface.init(this); // 初始化 JPush
        init();
        DevOpenHelper helper = new DevOpenHelper(getApplicationContext(), "dhcf-db", null);
        MyConstant.db = helper.getWritableDatabase();
        helper.onUpgrade(MyConstant.db, 1000, 1001);
        if (MyConstant.user == null) {
            SharedPreferences spf = getSharedPreferences(MyConstant.APP_SPF_NAME, MODE_PRIVATE);
            AppManager.getAppManager();
            MyConstant.user = new User();
            Tools.updateInfo(spf);
        }
        try {
            MyConstant.APP_VERSION_NAME = getPackageManager().getPackageInfo("com.dahuochifan", 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void bindMyService() {
        Intent intent = new Intent(getApplicationContext(), MorelJPushService.class);
        startService(intent);
        /** 进入Activity开始服务 */
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
//	public void bindMyService2() {
//		Intent intent = new Intent(getApplicationContext(), MorelJPushService.class);
//		/** 进入Activity开始服务 */
//		bindService(intent, conn, Context.BIND_AUTO_CREATE);
//	}

//	public PackageInfo getPackageInfo(Activity acttivity) {
//		PackageInfo info = null;
//		try {
//			info = acttivity.getPackageManager().getPackageInfo(acttivity.getPackageName(), 0);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return info;
//	}

//	public String getVersion(Activity acttivity) {
//		PackageInfo info = getPackageInfo(acttivity);
//		// 当前应用的版本名称
//		String versionName = info.versionName;
//		// 当前版本的版本号
//		int versionCode = info.versionCode;
//		// 当前版本的包名
//		String packageNames = info.packageName;
//		return versionName;
//	}

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            countService = ((MorelJPushService.MyBinder) service).getService();
        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            countService = null;
        }
    };

    public void init() {
        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 5);
        MemoryCache memoryCache;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            memoryCache = new LruMemoryCache(memoryCacheSize);
        } else {
            memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
        }

        mNormalImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).delayBeforeLoading(2000)
                .resetViewBeforeLoading(true).showImageForEmptyUri(R.mipmap.ic_launcher).showImageOnFail(R.mipmap.ic_launcher).build();

        mRoundImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(50)).resetViewBeforeLoading(false).showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(200 * 1024 * 1024)
                .diskCache(new UnlimitedDiskCache(StorageUtils.getOwnCacheDirectory(this, "dahuochifan/tupian_d")))
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().defaultDisplayImageOptions(mNormalImageOptions) // Remove
                .memoryCache((MemoryCache) memoryCache).build();
        ImageLoader.getInstance().init(config);
    }

}
