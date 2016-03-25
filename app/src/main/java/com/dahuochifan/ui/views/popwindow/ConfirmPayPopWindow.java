package com.dahuochifan.ui.views.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.ui.views.ripple.RippleView;
import com.zhy.android.percent.support.PercentRelativeLayout;

/**
 * Created by Morel on 2016/1/15.
 * Confirm pay PopWindow
 */
public class ConfirmPayPopWindow extends PopupWindow implements OnDismissListener,View.OnClickListener {
    public interface PayClickListener{
        void OnJJClick(View view);
    }

    @Override
    public void onClick(View v) {
        listenerZ.OnJJClick(v);
    }
    private View conentView;
    private AppCompatActivity context;

    private PayClickListener listenerZ;

    private TextView pop_price_tv, pop_pay_type, pop_average_tv, pop_eatnum_tv, pop_all_tv, pop_discount_tv;
    private RippleView pop_confirm_tv;
    private PercentRelativeLayout one_rl;


    public ConfirmPayPopWindow(AppCompatActivity context, String popPriceStr, String popNumStr, String popAllStr, String popDiscountStr, String popPayStr, boolean isAli, PayClickListener listener) {
        super(context);
        this.context = context;
        this.listenerZ=listener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_confirm_pay, null);

        pop_price_tv = (TextView) conentView.findViewById(R.id.pop_price_tv);
        pop_pay_type = (TextView) conentView.findViewById(R.id.pop_pay_type);
        pop_average_tv = (TextView) conentView.findViewById(R.id.pop_average_tv);
        pop_eatnum_tv = (TextView) conentView.findViewById(R.id.pop_eatnum_tv);
        pop_all_tv = (TextView) conentView.findViewById(R.id.pop_all_tv);
        pop_discount_tv = (TextView) conentView.findViewById(R.id.pop_discount_tv);
        one_rl = (PercentRelativeLayout) conentView.findViewById(R.id.one_rl);
        pop_confirm_tv=(RippleView) conentView.findViewById(R.id.pop_confirm_tv);

        pop_price_tv.setText(popPayStr);
        pop_pay_type.setText(isAli?"支付宝支付":"微信支付");
        pop_average_tv.setText(popPriceStr);
        pop_eatnum_tv.setText(popNumStr);
        pop_all_tv.setText(popAllStr);
        pop_discount_tv.setText(popDiscountStr);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        //		this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(context.getResources().getDimensionPixelSize(R.dimen.width_36_80));
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.transparent));
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //添加pop窗口关闭事件
        setOnDismissListener(this);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        btn_Listener();
    }

    private void btn_Listener() {
        one_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing())
                    dismiss();
            }
        });
        pop_confirm_tv.setOnClickListener(this);
    }

    /**
     * 显示popupWindow
     *
     * @param parent mainLayout
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 1);
            backgroundAlpha(0.5f);
        } else {
            this.dismiss();
//            backgroundAlpha(1f);
        }
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1f);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha alpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }
}
