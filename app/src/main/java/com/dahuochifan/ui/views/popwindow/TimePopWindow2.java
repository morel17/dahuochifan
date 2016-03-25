package com.dahuochifan.ui.views.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.interfaces.TopCuisineItemListener;
import com.dahuochifan.model.cookbook.CBTime;
import com.dahuochifan.ui.adapter.DinnerPopAdapter;
import com.dahuochifan.ui.adapter.LunchPopAdapter;
import com.dahuochifan.utils.DividerItemDecoration;

import java.util.List;

/**
 * Created by Morel on 2015/12/10.
 * 订餐时间的popwindow
 */
public class TimePopWindow2 extends PopupWindow implements PopupWindow.OnDismissListener {
    private View conentView;
    /**
     * 传递至popwindow的参数
     */
    private AppCompatActivity context;
    private TextView time_pop_choose_tv;
    private TextView order_time_tv;
    private TextView order_time_tv2;
    /**
     * lunch & dinner adapter
     */
    LunchPopAdapter lunchPopAdapter;
    DinnerPopAdapter dinnerPopAdapter;
    /**
     * timeList
     */
    private List<CBTime> lunchList, dinnerList;
    /**
     * popwindow上的控件
     */
    private ImageView lunch_left_iv, dinner_left_iv;
    private RecyclerView lunch_cyc, dinner_cyc;


    public TimePopWindow2(final AppCompatActivity context, TextView time_pop_choose_tv, TextView order_time_tv, TextView order_time_tv2, List<CBTime> lunchtimes, List<CBTime> dinnertimes) {
        super(context);
        this.context = context;
        this.time_pop_choose_tv = time_pop_choose_tv;
        this.order_time_tv = order_time_tv;
        this.order_time_tv2 = order_time_tv2;
        this.lunchList = lunchtimes;
        this.dinnerList = dinnertimes;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_time_new, null);

        lunch_left_iv = (ImageView) conentView.findViewById(R.id.lunch_left_iv);
        dinner_left_iv = (ImageView) conentView.findViewById(R.id.dinner_left_iv);
        lunch_cyc = (RecyclerView) conentView.findViewById(R.id.lunch_cyc);
        dinner_cyc = (RecyclerView) conentView.findViewById(R.id.dinner_cyc);
        lunchPopAdapter = new LunchPopAdapter(context);
        dinnerPopAdapter = new DinnerPopAdapter(context);
        GridLayoutManager lunchManager = new GridLayoutManager(context, 4);
        // layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        // 设置布局管理器
        lunch_cyc.setLayoutManager(lunchManager);
        lunch_cyc.setItemAnimator(new DefaultItemAnimator());
        lunch_cyc.addItemDecoration(new DividerItemDecoration(context, 1314520));
        lunch_cyc.setAdapter(lunchPopAdapter);
        GridLayoutManager dinnerManager = new GridLayoutManager(context, 4);
        dinner_cyc.setLayoutManager(dinnerManager);
        dinner_cyc.setItemAnimator(new DefaultItemAnimator());
        dinner_cyc.addItemDecoration(new DividerItemDecoration(context, 1314520));
        dinner_cyc.setAdapter(dinnerPopAdapter);
        dealList();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        //		this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
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
        btnListener();
    }

    private void btnListener() {
        if (lunchPopAdapter != null) {
            lunchPopAdapter.setOnItemClickListener(new TopCuisineItemListener() {
                @Override
                public void onItemClick(View view, int postion) {
                    if (lunchPopAdapter.getmData() != null && lunchPopAdapter.getmData().size() > 0) {
                        CBTime objTemp = lunchPopAdapter.getmData().get(postion);
                        if (objTemp.isValid()) {
                            String eatTimeStr = "午餐 " + objTemp.getTime();
                            time_pop_choose_tv.setText(eatTimeStr);
                            order_time_tv.setText("午餐");
                            order_time_tv2.setText(objTemp.getTime());
                            time_pop_choose_tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
                            dismiss();
                        }
                    }
                }
            });
        }
        if (dinnerPopAdapter != null) {
            dinnerPopAdapter.setOnItemClickListener(new TopCuisineItemListener() {
                @Override
                public void onItemClick(View view, int postion) {
                    if (dinnerPopAdapter.getmData() != null && dinnerPopAdapter.getmData().size() > 0) {
                        CBTime objTemp = dinnerPopAdapter.getmData().get(postion);
                        if (objTemp.isValid()) {
                            String eatTimeStr = "晚餐 " + objTemp.getTime();
                            time_pop_choose_tv.setText(eatTimeStr);
                            order_time_tv.setText("晚餐");
                            order_time_tv2.setText(objTemp.getTime());
                            time_pop_choose_tv.setTextColor(ContextCompat.getColor(context, R.color.maincolor_new));
                            dismiss();
                        }
                    }
                }
            });
        }
    }

    private CBTime putFirstObj(String typeStr) {
        CBTime obj = new CBTime();
        obj.setValid(false);
        obj.setTime(typeStr);
        return obj;
    }

    /**
     * deal with time list
     */
    private void dealList() {
        lunchList.add(0, putFirstObj("午餐"));
        dinnerList.add(0, putFirstObj("晚餐"));
        lunchPopAdapter.setmData(lunchList);
        dinnerPopAdapter.setmData(dinnerList);
        lunchPopAdapter.notifyDataSetChanged();
        dinnerPopAdapter.notifyDataSetChanged();
        RelativeLayout.LayoutParams lunchparams = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_62_80), getLineNumber(lunchList)
                * context.getResources().getDimensionPixelOffset(R.dimen.width_9_80) + 4);
        lunch_cyc.setLayoutParams(lunchparams);
        RelativeLayout.LayoutParams dinnerparams = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_62_80), getLineNumber(dinnerList)
                * context.getResources().getDimensionPixelOffset(R.dimen.width_9_80) + 4);
        dinner_cyc.setLayoutParams(dinnerparams);
        int iv1Height = getLineNumber(lunchList) > 1 ? getLineNumber(lunchList)
                * context.getResources().getDimensionPixelOffset(R.dimen.width_9_80) + 4 : getLineNumber(lunchList)
                * context.getResources().getDimensionPixelOffset(R.dimen.width_9_80);
        int iv2Height = getLineNumber(dinnerList) > 1 ? getLineNumber(dinnerList)
                * context.getResources().getDimensionPixelOffset(R.dimen.width_9_80) + 4 : getLineNumber(dinnerList)
                * context.getResources().getDimensionPixelOffset(R.dimen.width_9_80);
        RelativeLayout.LayoutParams iv1params = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_2_80), iv1Height);
        RelativeLayout.LayoutParams iv2params = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelOffset(R.dimen.width_2_80), iv2Height);
        lunch_left_iv.setLayoutParams(iv1params);
        dinner_left_iv.setLayoutParams(iv2params);
    }


    /**
     * @param tagList targetList
     * @return line number
     */
    public int getLineNumber(List<CBTime> tagList) {
        int num = tagList.size() % 4;
        int line = tagList.size() / 4;
        if (num > 0) {
            line++;
        }
        return line;
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
