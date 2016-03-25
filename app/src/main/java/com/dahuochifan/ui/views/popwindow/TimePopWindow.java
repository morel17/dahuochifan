package com.dahuochifan.ui.views.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.utils.MainTools;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;

/**
 * Created by Morel on 2015/8/21.
 * 关于时间选择的popwindow
 */
public class TimePopWindow extends PopupWindow implements PopupWindow.OnDismissListener {

    private View conentView;
    private AppCompatActivity context;
    private int whenIndexint;
    private TextView time_pop_choose_tv;
    private TextView order_time_tv;
    private TextView order_time_tv2;

    private ArrayList<TextView> tvList;
    private ArrayList<String> timeList,timeListTemp;
    private TextView midOne, midTwo, midThree, midFour, midFive, midSix, midSeven;
    private TextView nigOne, nigTwo, nigThree, nigFour, nigFive, nigSix, nigSeven;
    private TextView midOther, nightOther;
    private int lastIndex;
    private String lunchtimes,dinnertimes;
    private String[] lunchStr,dinnerStr;
    private int advTimes;

    public TimePopWindow(final AppCompatActivity context, int whenIndexint, TextView time_pop_choose_tv, TextView order_time_tv, TextView order_time_tv2, String lunchtimes, String dinnertimes, int advanceminute) {
        super(context);
        tvList = new ArrayList<>();
        timeList = new ArrayList<>();
        timeListTemp=new ArrayList<>();
        this.advTimes=advanceminute;
        this.time_pop_choose_tv=time_pop_choose_tv;
        this.order_time_tv2=order_time_tv2;
        this.order_time_tv=order_time_tv;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.whenIndexint = whenIndexint;
//        this.midList = midList;
//        this.nightList = nightList;
        this.context = context;
        this.lunchtimes=lunchtimes;
        this.dinnertimes=dinnertimes;
        if(!TextUtils.isEmpty(lunchtimes)){
            lunchStr=lunchtimes.split(",");
        }
        if(!TextUtils.isEmpty(dinnertimes)){
            dinnerStr=dinnertimes.split(",");
        }
        if(lunchStr!=null&&lunchStr.length>0){
            for(int i=0;i<lunchStr.length;i++){
                timeListTemp.add(lunchStr[i]);
            }
        }
        if(dinnerStr!=null&&dinnerStr.length>0){
            for(int i=0;i<dinnerStr.length;i++){
                timeListTemp.add(dinnerStr[i]);
            }
        }
        conentView = inflater.inflate(R.layout.poptime, null);
        midOther = (TextView) conentView.findViewById(R.id.midother);
        nightOther = (TextView) conentView.findViewById(R.id.nigother);

        midOne = (TextView) conentView.findViewById(R.id.midone);
        midTwo = (TextView) conentView.findViewById(R.id.midtwo);
        midThree = (TextView) conentView.findViewById(R.id.midthree);
        midFour = (TextView) conentView.findViewById(R.id.midfour);
        midFive = (TextView) conentView.findViewById(R.id.midfive);
        midSix = (TextView) conentView.findViewById(R.id.midsix);
        midSeven = (TextView) conentView.findViewById(R.id.midseven);

        nigOne = (TextView) conentView.findViewById(R.id.nigone);
        nigTwo = (TextView) conentView.findViewById(R.id.nigtwo);
        nigThree = (TextView) conentView.findViewById(R.id.nigthree);
        nigFour = (TextView) conentView.findViewById(R.id.nigfour);
        nigFive = (TextView) conentView.findViewById(R.id.nigfive);
        nigSix = (TextView) conentView.findViewById(R.id.nigsix);
        nigSeven = (TextView) conentView.findViewById(R.id.nigseven);
        initList();
        if (whenIndexint == 0) {
            falseAll();
            showDeli();
            dealInterval();
        }else{
            falseAll();
            showDeli();
        }
        btnListener();
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
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //添加pop窗口关闭事件
        setOnDismissListener(this);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
    }

    private void btnListener() {
        for(int i=0;i<tvList.size();i++){
            final int finalI = i;
            tvList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalI > 6) {
                        time_pop_choose_tv.setText("晚餐 " + tvList.get(finalI).getText().toString());
                        order_time_tv.setText("晚餐");
                        order_time_tv2.setText(tvList.get(finalI).getText().toString());
                    } else {
                        time_pop_choose_tv.setText("午餐 " + tvList.get(finalI).getText().toString());
                        order_time_tv.setText("午餐");
                        order_time_tv2.setText(tvList.get(finalI).getText().toString());
                    }
                    time_pop_choose_tv.setTextColor(context.getResources().getColor(R.color.maincolor_new));
                    dismiss();
                }
            });
        }
    }

    private void falseAll(){
        for(int j=0;j<timeList.size();j++){
                tvList.get(j).setEnabled(false);
        }
    }
    private void dealInterval() {
        String curTimeStr=MainTools.getMoreMinuteTime(advTimes);
        int startA=0;
        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).equals(MainTools.getMoreMinuteTime(advTimes))) {
                startA++;
                for (int j = 0; j < i; j++) {
                    tvList.get(j).setEnabled(false);
                }
            }
        }
        if(startA==0){
            L.e(!MainTools.timeCompare2("14:00",curTimeStr)+"**"+!MainTools.timeCompare2(curTimeStr,"17:00"));
            L.e(!MainTools.timeCompare2("20:00",curTimeStr)+"&&");
            if(!MainTools.timeCompare2("14:00",curTimeStr)&&!MainTools.timeCompare2(curTimeStr,"17:00")){
                for(int i=0;i<7;i++){
                    tvList.get(i).setEnabled(false);
                }
            }
            if(!MainTools.timeCompare2("20:00",curTimeStr)){
                falseAll();
            }
        }
    }
    private void showDeli(){
        if(timeListTemp!=null&&timeListTemp.size()>0){
            for(int i=0;i<timeListTemp.size();i++){
                for(int j=0;j<timeList.size();j++){
                    if(timeList.get(j).equals(timeListTemp.get(i))){
                        tvList.get(j).setEnabled(true);
                    }
                }
            }
        }
    }

    private void initList() {
        tvList.add(midOne);
        tvList.add(midTwo);
        tvList.add(midThree);
        tvList.add(midFour);
        tvList.add(midFive);
        tvList.add(midSix);
        tvList.add(midSeven);
        tvList.add(nigOne);
        tvList.add(nigTwo);
        tvList.add(nigThree);
        tvList.add(nigFour);
        tvList.add(nigFive);
        tvList.add(nigSix);
        tvList.add(nigSeven);

        for(int i=0;i<tvList.size();i++){
            tvList.get(i).setEnabled(false);
        }

        timeList.add("11:00");
        timeList.add("11:30");
        timeList.add("12:00");
        timeList.add("12:30");
        timeList.add("13:00");
        timeList.add("13:30");
        timeList.add("14:00");
        timeList.add("17:00");
        timeList.add("17:30");
        timeList.add("18:00");
        timeList.add("18:30");
        timeList.add("19:00");
        timeList.add("19:30");
        timeList.add("20:00");
    }

    /**
     * 显示popupWindow
     *
     * @param parent
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
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }
}
