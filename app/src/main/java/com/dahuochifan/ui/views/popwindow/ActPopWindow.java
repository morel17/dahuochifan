package com.dahuochifan.ui.views.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.dahuochifan.R;
import com.dahuochifan.model.cookbook.CBActs;
import com.dahuochifan.ui.adapter.Pop2Adapter;

import java.util.ArrayList;
import java.util.List;

public class ActPopWindow extends PopupWindow implements OnItemClickListener,PopupWindow.OnDismissListener{
	public interface MorelPopListener{
		void onItemClick(AdapterView<?> parent, View view, int position, long id);
	}
	private View conentView;
	private List<CBActs> cbstr=new ArrayList<>();
	private MorelPopListener listener;
	private AppCompatActivity context;

	public ActPopWindow(final AppCompatActivity context, List<CBActs> cbList, MorelPopListener listener) {
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.pop_tag2, null);
		this.listener=listener;
		this.cbstr=cbList;
		CBActs acts=new CBActs();
		acts.setRemark("不使用优惠");
		cbstr.add(acts);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
//		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setWidth(context.getResources().getDimensionPixelSize(R.dimen.width_62_80));
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		//添加pop窗口关闭事件
		setOnDismissListener(this);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);
		ListView listview=(ListView)conentView.findViewById(R.id.pop_tag_listview);
		Pop2Adapter adapter=new Pop2Adapter(context);
		adapter.setmObjects(cbstr);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	}

	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
//			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
//			this.showAsDropDown(parent,0,1);
			this.showAtLocation(parent, Gravity.CENTER,0,0);
			backgroundAlpha(0.5f);
		} else {
			this.dismiss();
		}
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
	@Override
	public void onDismiss() {
		backgroundAlpha(1f);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		listener.onItemClick(parent, view, position, id);
	}
}
