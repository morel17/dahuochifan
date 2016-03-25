package com.dahuochifan.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.activity.LoginActivity;
import com.dahuochifan.ui.views.dialog.MorelAlertDialog;
import com.dahuochifan.utils.MyAsyncHttpClient;
import com.dahuochifan.utils.SharedPreferenceUtil;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

public class BaseFragment extends Fragment {
    protected AppCompatActivity mActivity;
    protected BaseFragment basefragment;
    protected MyAsyncHttpClient mBaseClient;
    private AlertDialog dialogF;
    private SharedPreferences spfF;
    private SharedPreferences.Editor editorF;
    private MorelAlertDialog morelAlertDialog;

    @Override
    public void onAttach(Context activity) {
        mActivity = (AppCompatActivity) activity;
        if (mBaseClient == null) {
            mBaseClient = new MyAsyncHttpClient();
        }
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void showAlertDialog(int Type, String title, boolean cancelOutSide) {
        if (morelAlertDialog != null && morelAlertDialog.isShowing()) {
            morelAlertDialog.dismissWithAnimation();
            morelAlertDialog = null;
        }
        morelAlertDialog = new MorelAlertDialog(mActivity, Type);
        morelAlertDialog.setTitleText(title);
        morelAlertDialog.setCanceledOnTouchOutside(cancelOutSide);
        morelAlertDialog.getProgressHelper().setBarColor(ContextCompat.getColor(mActivity, R.color.white));
        morelAlertDialog.getProgressHelper().setBarWidth(3);
        morelAlertDialog.show();
    }

    protected void changeAlertDialog(int Type, String title) {
        if (morelAlertDialog != null) {
            morelAlertDialog.setTitleText(title);
            morelAlertDialog.changeAlertType(Type);
        }
    }

    protected void dismissAlertDialog() {
        if (morelAlertDialog != null) {
            morelAlertDialog.dismiss();
        }
    }

    protected void dismissAlertDialogWithOutAnimation() {
        if (morelAlertDialog != null) {
            morelAlertDialog.dismiss();
        }
    }

    protected boolean isAlertShowing() {
        return morelAlertDialog != null && morelAlertDialog.isShowing();
    }

    protected void showTipDialog(String title, int resultcode) {
        if (dialogF != null && dialogF.isShowing()) {
            dialogF.dismiss();
        }
        if (spfF == null) {
            spfF = SharedPreferenceUtil.initSharedPerence().init(mActivity, MyConstant.APP_SPF_NAME);
        }
        if (editorF == null) {
            editorF = spfF.edit();
        }
        if (resultcode == -1) {
            dialogF = new AlertDialog.Builder(mActivity).setCancelable(false).setTitle(title).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferenceUtil.initSharedPerence().putUserId(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putUserRole(editorF, -99);
                    SharedPreferenceUtil.initSharedPerence().putLogin(editorF, false);
                    SharedPreferenceUtil.initSharedPerence().putHomeProv(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putLoginPhone(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putCurProv(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putToken(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putChefIds(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putOtherProv(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putAvatar(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putNickname(editorF, "");
                    SharedPreferenceUtil.initSharedPerence().putPoiName(editorF, "");
                    if (SharedPreferenceUtil.initSharedPerence().getWxLogin(spfF)) {
                        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                        if (wechat.isValid()) {
                            wechat.removeAccount();
                        }
                    }
                    editorF.commit();
                    dialog.dismiss();
                    MyConstant.user.clear();
                    AppManager.getAppManager().finishAllActivity();
//                    JPushInterface.stopPush(mActivity.getApplicationContext());
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
            }).create();
            dialogF.show();
        }
    }

    @Override
    public void onStop() {
        if (morelAlertDialog != null)
            morelAlertDialog.dismiss();
        morelAlertDialog = null;
        super.onStop();
    }
}
