package com.dahuochifan.ui.views.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.zhy.android.percent.support.PercentFrameLayout;

import java.util.List;

import cn.pedant.SweetAlert.OptAnimationLoader;
import cn.pedant.SweetAlert.ProgressHelper;

/**
 * Created by Morel on 2015/12/31.
 * MorelAlertDialog
 */
public class MorelAlertDialog extends Dialog{
    private View mDialogView;
    private AppCompatActivity context;
    /**
     * Views Fragments
     */
    private FrameLayout mErrorFrame;
    private FrameLayout mSuccessFrame;
    private FrameLayout mProgressFrame;
    private PercentFrameLayout mWarningFrame;
    /**
     * Views Errors
     */
    private ImageView mErrorX;
    /**
     * Views Success
     */
    private View mSuccessLeftMask;
    private View mSuccessRightMask;
    private SuccessTickViewWhite mSuccessTick;
    /**
     * Views TextView
     */
    private TextView mTitleTextView;
    /**
     * Progress Helper
     */
    private ProgressHelper mProgressHelper;
    private ProgressWheel progressWheel;
    /**
     * isCloseFromCancel
     */
    private boolean mCloseFromCancel;
    /**
     * Animation
     */
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private Animation mOverlayOutAnim;
    private Animation mErrorInAnim;
    private AnimationSet mErrorXInAnim;
    private AnimationSet mSuccessLayoutAnimSet;
    private Animation mSuccessBowAnim;

    /**
     * Types
     */
    private int mAlertType;
    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;

    /**
     * Strings
     */
    private String mTitleText;

    public MorelAlertDialog(AppCompatActivity context) {
        this(context, NORMAL_TYPE);
    }

    public MorelAlertDialog(AppCompatActivity context, int alertType) {
        super(context, R.style.alert_dialog2);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mProgressHelper = new ProgressHelper(context);
        mAlertType = alertType;
        this.context=context;
        /**
         * Error Animation
         */
        mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.error_frame_in);
        mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.error_x_in);
        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            List<Animation> childAnims = mErrorXInAnim.getAnimations();
            int idx = 0;
            for (; idx < childAnims.size(); idx++) {
                if (childAnims.get(idx) instanceof AlphaAnimation) {
                    break;
                }
            }
            if (idx < childAnims.size()) {
                childAnims.remove(idx);
            }
        }
        /**
         * Success Animation
         */
        mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.success_bow_roate);
        mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.success_mask_layout);
        /**
         * InAndOut Animation
         */
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            MorelAlertDialog.super.cancel();
                        } else {
                            MorelAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(cn.pedant.SweetAlert.R.id.title_text);
        mErrorFrame = (FrameLayout) findViewById(cn.pedant.SweetAlert.R.id.error_frame);
        mErrorX = (ImageView) mErrorFrame.findViewById(cn.pedant.SweetAlert.R.id.error_x);
        mSuccessFrame = (FrameLayout) findViewById(cn.pedant.SweetAlert.R.id.success_frame);
        mProgressFrame = (FrameLayout) findViewById(cn.pedant.SweetAlert.R.id.progress_dialog);
        mSuccessTick = (SuccessTickViewWhite) mSuccessFrame.findViewById(cn.pedant.SweetAlert.R.id.success_tick);
        mSuccessLeftMask = mSuccessFrame.findViewById(cn.pedant.SweetAlert.R.id.mask_left);
        mSuccessRightMask = mSuccessFrame.findViewById(cn.pedant.SweetAlert.R.id.mask_right);
        mWarningFrame = (PercentFrameLayout) findViewById(cn.pedant.SweetAlert.R.id.warning_frame);
        progressWheel=(ProgressWheel) findViewById(cn.pedant.SweetAlert.R.id.progressWheel);
        mProgressHelper.setProgressWheel(progressWheel);
        setTitleText(mTitleText);
        changeAlertType(mAlertType, true);
    }

    /**
     * @param text TipsStrings
     * @return MorelProgressDialog
     * SetTitleText
     */
    public MorelAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    /**
     * @param alertType  TheAlertDialog's Type
     * @param fromCreate IsChangedFromCreate
     *                   ChangeTheAlertDialog's Type
     */
    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case ERROR_TYPE:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TYPE:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    // initial rotate layout of success mask
                    mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                    mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                    break;
                case WARNING_TYPE:
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case PROGRESS_TYPE:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    break;
            }
            if (!fromCreate) {
                playAnimation();
            }
        }
    }

    /**
     * If AlertDialog IsNotFromCreate Restore Them
     */
    private void restore() {
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);

        mErrorFrame.clearAnimation();
        mErrorX.clearAnimation();
        mSuccessTick.clearAnimation();
        mSuccessLeftMask.clearAnimation();
        mSuccessRightMask.clearAnimation();
    }

    /**
     * PlayAnimation
     */
    private void playAnimation() {
        if (mAlertType == ERROR_TYPE) {
            mErrorFrame.startAnimation(mErrorInAnim);
            mErrorX.startAnimation(mErrorXInAnim);
        } else if (mAlertType == SUCCESS_TYPE) {
            mSuccessTick.startTickAnim();
            mSuccessRightMask.startAnimation(mSuccessBowAnim);
        }
    }

    /**
     * @param alertType AlertType
     */
    public void changeAlertType(int alertType) {
        changeAlertType(alertType, false);
    }

    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mDialogView.startAnimation(mModalOutAnim);
    }

    public ProgressHelper getProgressHelper() {
        return mProgressHelper;
    }

//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        backgroundAlpha(1f);
//    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }

//    @Override
//    public void onShow(DialogInterface dialog) {
//        backgroundAlpha(0f);
//    }
}
