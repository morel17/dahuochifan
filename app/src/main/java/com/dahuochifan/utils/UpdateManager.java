package com.dahuochifan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.model.UpdateAll;
import com.dahuochifan.ui.activity.WelcomeActivity;
import com.dahuochifan.ui.views.dialog.UpdateDialog;
import com.dahuochifan.ui.views.dialog.UpdateDialog.MyUpdateListener;
import com.nostra13.universalimageloader.utils.L;
import com.zhy.view.HorizontalProgressBarWithNumber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 软件版本更新
 *
 * @author Morel
 */
public class UpdateManager {

    private static Context mContext;


    // 返回的安装包url
    private static String apkUrl;


    /* 下载包安装路径 */
    private static String savePath;
    public static String saveFileName;

    /* 进度条与通知ui刷新的handler和msg常量 */
    private HorizontalProgressBarWithNumber mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;
    private static UpdateDialog pdialog;

    private boolean interceptFlag = false;
    public static AlertDialog dialog;
    public static PackageInfo info;
    static UpdateAll updateAll;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (mProgress == null) {
                        mProgress = pdialog.getPb();
                    }
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    public UpdateManager(Context context) {
        this.mContext = context;
        savePath = GetFileSizeUtil.getMainSD(mContext) + "/dahuochifan/apks";
        saveFileName = savePath + "/Dahuochifan.apk";

    }

    // 外部接口让主Activity调用
    public void checkUpdateInfo() {
        showNoticeDialog();
    }

    private void showNoticeDialog() {
        if (updateAll != null && updateAll.getObj() != null && updateAll.getObj().isIsinstall()) {
//            dialog = new CustomDialog(mContext, R.style.mylogoutstyle, R.layout.customdialog, "App有重要更新，请立即升级").setCancelClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    AppManager.getAppManager().finishAllActivity();
//                }
//            }).setConfirmClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    showDownloadDialog();
//                }
//            });
            dialog = new AlertDialog.Builder(mContext).setCancelable(false).setTitle("更新通知").setMessage(TextUtils.isEmpty(updateAll.getObj().getRemark()) ? "App有重要更新，请立即升级" : updateAll.getObj().getRemark())
                    .setPositiveButton("确定", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showDownloadDialog();
                        }
                    }).setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AppManager.getAppManager().finishAllActivity();
                        }
                    }).create();
            dialog.show();
        } else {
            dialog = new AlertDialog.Builder(mContext).setCancelable(false).setTitle("更新通知").setMessage(TextUtils.isEmpty(updateAll.getObj().getRemark()) ? "请安装新版本" : updateAll.getObj().getRemark())
                    .setPositiveButton("确定", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showDownloadDialog();
                        }
                    }).setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }
    }

    public static void dismissDialog2() {
        if (pdialog != null) {
            pdialog.dismiss();
        }
    }

    private void showDownloadDialog() {
        pdialog = new UpdateDialog(mContext,new MyUpdateListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cancel_tv:
                        pdialog.dismiss();
                        AppManager.getAppManager().finishAllActivity();
                        interceptFlag = true;
                        break;
                    default:
                        break;
                }
            }
        });
        pdialog.setCancelable(false);
//        pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pdialog.show();
        downloadApk();
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
                ApkFile.createNewFile();
                System.out.println("ApkFile" + ApkFile.exists());
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.s

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
        // 安装的时候也关闭 程序的界面 免得在安装的时候点击了取消 后出现程序中断 无法进行下步操作
        Activity ac = (Activity) mContext;
        ac.finish();
        if (WelcomeActivity.instance != null) {
            WelcomeActivity.instance.finish();
        }
        // SaveParam.saveVersion(this.mContext, version);
        // CommonOper.exitApp(mContext);
    }

    /**
     * 检查软件版本是否有更新
     *
     * @param mHandler    处理消息的handle
     * @param isneedtoast 如果没有发现新版本，是否需要提醒
     * @param name2
     * @param code2
     * @param versionName
     * @param versionCode
     */
    public static void checkVersion(Handler mHandler, boolean isneedtoast, String code2, String name2, int versionCode, String versionName) {
        Message msg1 = new Message();
        msg1.what = MyConstant.msg_checkingversion;
        mHandler.sendMessage(msg1);
        Message msg = new Message();
        msg.what = 0;
        /*
         * if(null!=lastVersion&&!"".equals(lastVersion)&&!Content.SOFTVERSION.equals(lastVersion)){ msg.what = Content.msg_findnewversion; }else{
		 * if(isneedtoast) msg.what = Content.msg_notfindnewversion; }
		 */
        // Log.e("UpdateManager", code2+"***"+name2);
        StringBuffer strb2 = new StringBuffer(name2);
        if (strb2.length() > 3) {
            strb2.deleteCharAt(3);
        }
        float newName = Tools.toFloat(strb2.toString());
        StringBuffer strb = new StringBuffer(versionName);
        if (strb.length() > 3) {
            strb.deleteCharAt(3);
        }
        float myName = Tools.toFloat(strb.toString());

        if (newName > myName) {
            msg.what = MyConstant.msg_findnewversion;
        } else {
            msg.what = MyConstant.msg_notfindnewversion;
        }

        mHandler.sendMessage(msg);
        // Message msg2 = new Message();
        // msg2.what = 678;
        // mHandler.sendMessage(msg2);
    }

    public static void checkVersion(Handler mHandler, PackageInfo info, UpdateAll updateall) {
        updateAll = updateall;
        Message msg1 = new Message();
        msg1.what = MyConstant.msg_checkingversion;
        mHandler.sendMessage(msg1);
        Message msg = new Message();
        msg.what = 0;
        String versionname_old = info.versionName.replace(".", "");
        String versionname_new = updateall.getObj().getVersionname().replace(".", "");
        int vo = Integer.parseInt(versionname_old);
        int vn = Integer.parseInt(versionname_new);
        // int vn = 2;
        if (BuildConfig.LEO_DEBUG)
            L.e(vo + "**" + vn);
        apkUrl = updateall.getObj().getUrl();
        if (BuildConfig.LEO_DEBUG)
            L.e(info.versionCode + "*&&&*" + updateall.getObj().getVersioncode());
        // if (true) {
        if ((info.versionCode != updateall.getObj().getVersioncode()) || (vo != vn)) {
            msg.what = MyConstant.msg_findnewversion;
        } else {
            msg.what = MyConstant.msg_notfindnewversion;
        }
        mHandler.sendMessage(msg);
    }
//    public static void checkVersion(Handler mHandler,int)

}