package com.dahuochifan.ui.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;

import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

public abstract class BaseFullScreenActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private Editor baseeditor;
    protected SharedPreferences baseSpf;
    protected MyAsyncHttpClient baseClient;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseSpf = SharedPreferenceUtil.initSharedPerence().init(this, MyConstant.APP_SPF_NAME);
        baseClient = new MyAsyncHttpClient();
        baseeditor = baseSpf.edit();
        baseeditor.apply();
        initWindow();
        setContentView(getLayoutView());
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
    }


    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

//	private int getColorPrimary() {
//		TypedValue typedValue = new TypedValue();
//		getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//		return typedValue.data;
//	}

    public void showTipDialog(final AppCompatActivity activity, String title, int resultcode) {
        if (resultcode == -1) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = new AlertDialog.Builder(this).setCancelable(false).setTitle(title).setNegativeButton("取消", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("确定", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferenceUtil.initSharedPerence().putUserId(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putUserRole(baseeditor, -99);
                    SharedPreferenceUtil.initSharedPerence().putLogin(baseeditor, false);
                    SharedPreferenceUtil.initSharedPerence().putLoginPhone(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putHomeProv(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putCurProv(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putToken(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putChefIds(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putOtherProv(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putAvatar(baseeditor, "");
                    SharedPreferenceUtil.initSharedPerence().putNickname(baseeditor, "");
                    if (SharedPreferenceUtil.initSharedPerence().getWxLogin(baseSpf)) {
                        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                        if (wechat.isValid()) {
                            wechat.removeAccount();
                        }
                    }
                    baseeditor.commit();
                    dialog.dismiss();
                    MyConstant.user.clear();
                    AppManager.getAppManager().finishAllActivity();
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            }).create();
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    protected int getDhColor(int res) {
        if (res <= 0)
            throw new IllegalArgumentException("resource id can not be less 0");
        return ContextCompat.getColor(BaseFullScreenActivity.this, res);
    }

    protected abstract int getLayoutView();

    protected abstract void initToolbar();
}
