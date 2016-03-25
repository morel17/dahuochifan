package com.dahuochifan.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.activity.LoginActivity;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;

import java.lang.ref.WeakReference;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

public class MyLogOutDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView ok_tv;
    private TextView cancel_tv;
    private TextView tip_tv;
    private SharedPreferences spf;
    private Editor editor;

    static class MyHandler extends Handler {
        WeakReference<Context> wrReferences;

        MyHandler(Context wrReferences) {
            this.wrReferences = new WeakReference<>(wrReferences);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    AppManager.getAppManager().finishAllActivity();
                    wrReferences.get().startActivity(new Intent(wrReferences.get(), LoginActivity.class));
                    break;

                default:
                    break;
            }
        }
    }

    public MyLogOutDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        spf = context.getSharedPreferences(MyConstant.APP_SPF_NAME, context.MODE_PRIVATE);
        editor = spf.edit();
        ShareSDK.initSDK(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialoglogout);
        ok_tv = (TextView) findViewById(R.id.ok_tv);
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);
        tip_tv = (TextView) findViewById(R.id.title_tv);
        ok_tv.setOnClickListener(this);
        cancel_tv.setOnClickListener(this);
        tip_tv.setText("是否退出账号");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_tv:
                SharedPreferenceUtil.initSharedPerence().putLogin(editor, false);
                SharedPreferenceUtil.initSharedPerence().putUserId(editor, "");
                SharedPreferenceUtil.initSharedPerence().putUserRole(editor, -99);
                SharedPreferenceUtil.initSharedPerence().putHomeProv(editor, "");
                SharedPreferenceUtil.initSharedPerence().putLoginPhone(editor,"");
                SharedPreferenceUtil.initSharedPerence().putCurProv(editor, "");
                SharedPreferenceUtil.initSharedPerence().putToken(editor, "");
                SharedPreferenceUtil.initSharedPerence().putChefIds(editor, "");
                SharedPreferenceUtil.initSharedPerence().putOtherProv(editor, "");
                SharedPreferenceUtil.initSharedPerence().putAvatar(editor, "");
                SharedPreferenceUtil.initSharedPerence().putNickname(editor, "");
                SharedPreferenceUtil.initSharedPerence().putPoiName(editor,"");
                if (SharedPreferenceUtil.initSharedPerence().getWxLogin(spf)) {
                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                    if (wechat.isValid()) {
                        wechat.removeAccount();
                    }
                }
                editor.commit();
                MyConstant.user.clear();
                MainTools.ShowToast(context, "退出成功");
                dismiss();
                final MyHandler handler = new MyHandler(context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }).start();
                break;
            case R.id.cancel_tv:
                this.dismiss();
                break;

            default:
                break;
        }
    }
}
