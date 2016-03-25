package com.dahuochifan.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.views.ActionSheet;
import com.dahuochifan.ui.views.ActionSheet.ActionSheetListener;
import com.dahuochifan.ui.views.MyLogOutDialog;
import com.dahuochifan.utils.GetFileSizeUtil;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.NoDoubleClickListener;
import com.dahuochifan.utils.UpdateManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.friends.Wechat.ShareParams;
import cn.sharesdk.wechat.moments.WechatMoments;

public class SettingActivity extends BaseActivity implements ActionSheetListener, PlatformActionListener {
    private RelativeLayout feedback_rl;
    private RelativeLayout logout_rl;
    private RelativeLayout about_rl;
    private RelativeLayout share_rl;
    private RelativeLayout contact_rl;
    private RelativeLayout memory_rl;
    private RelativeLayout apply_rl;
    private TextView text_tv;
    private TextView right_tv;

    private SharedPreferences spf;
    private Editor editor;
    ShareParams sp;
    private int userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        initviews();
        listener();
    }

    private void initviews() {
        userRole = MyConstant.user.getRole();
        feedback_rl = (RelativeLayout) findViewById(R.id.feedback_rl);
        logout_rl = (RelativeLayout) findViewById(R.id.logout_rl);
        about_rl = (RelativeLayout) findViewById(R.id.about_rl);
        share_rl = (RelativeLayout) findViewById(R.id.share_rl);
        contact_rl = (RelativeLayout) findViewById(R.id.contact_rl);
        memory_rl = (RelativeLayout) findViewById(R.id.memory_rl);
        apply_rl = (RelativeLayout) findViewById(R.id.apply_rl);
        text_tv = (TextView) findViewById(R.id.right_tv);
        text_tv.setText(GetFileSizeUtil.getInstance().getFilesSize(StorageUtils.getOwnCacheDirectory(this, "dahuochifan/tupian_d")));
        spf = getSharedPreferences(MyConstant.APP_SPF_NAME, MODE_PRIVATE);
        editor = spf.edit();
        ShareSDK.initSDK(this);
        sp = new ShareParams();
        if (userRole == 2) {
            apply_rl.setVisibility(View.GONE);
        }
    }

    private void listener() {
        about_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
            }
        });
        memory_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this).setCancelable(false).setTitle("是否清除缓存").setMessage("使用缓存可节省流量")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ImageLoader.getInstance().clearMemoryCache();
                                ImageLoader.getInstance().clearDiskCache();
                                MainTools.ShowToast(SettingActivity.this, "缓存清理完毕");
                                text_tv.setText(GetFileSizeUtil.getInstance().getFilesSize(StorageUtils.getOwnCacheDirectory(SettingActivity.this, "dahuochifan/tupian_d")));
                            }
                        }).create().show();
            }
        });
        feedback_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, FeedBackActivity.class));
            }
        });
        logout_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLogOutDialog dialog = new MyLogOutDialog(SettingActivity.this, R.style.mylogoutstyle);
                dialog.show();
            }
        });
        share_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet();
            }
        });
        contact_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + "4000960509");
                intent.setData(data);
                startActivity(intent);
            }
        });
        apply_rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent_apply = new Intent(SettingActivity.this, ApplyAllActivity.class);
                startActivity(intent_apply);
            }
        });
    }

    public void showActionSheet() {
        ActionSheet.createBuilder(this, getSupportFragmentManager()).setCancelButtonTitle("取消").setOtherButtonTitles("分享给好友", "分享到朋友圈")
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                Platform plat = ShareSDK.getPlatform(SettingActivity.this, Wechat.NAME);
                plat.setPlatformActionListener(this);
                Wechat.ShareParams sp = new Wechat.ShareParams();
                // 任何分享类型都需要title和text
                // the two params of title and text are required in every share type
                sp.title = "搭伙吃饭分享";
                sp.text = "搭伙吃饭APP下载,正宗家庭小炒，纯正老家味";
                sp.shareType = Platform.SHARE_WEBPAGE;
                sp.url = "http://www.dahuochifan.com/download/app";
                sp.imageData = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                plat.share(sp);
                break;
            case 1:
                Platform plat2 = ShareSDK.getPlatform(SettingActivity.this, WechatMoments.NAME);
                plat2.setPlatformActionListener(this);
                WechatMoments.ShareParams sp2 = new WechatMoments.ShareParams();
                sp2.title = "搭伙吃饭APP下载,正宗家庭小炒，纯正老家味";
                sp2.text = "搭伙吃饭APP下载,正宗家庭小炒，纯正老家味";
                sp2.shareType = Platform.SHARE_WEBPAGE;
                sp2.url = "http://www.dahuochifan.com/download/app";
                sp2.imageData = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                plat2.share(sp2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {

    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {

    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_setting;
    }

    @Override
    protected String initToolbarTitle() {
        return "设置";
    }

    @Override
    protected void onDestroy() {
        if (UpdateManager.dialog != null) {
            UpdateManager.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
