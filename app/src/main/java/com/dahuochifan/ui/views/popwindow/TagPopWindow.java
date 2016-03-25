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
import com.dahuochifan.ui.adapter.PopAdapter;

import java.util.ArrayList;

public class TagPopWindow extends PopupWindow implements OnItemClickListener,PopupWindow.OnDismissListener{
	public interface MorelPopListener{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id);
	}
	private View conentView;
	private ArrayList<String> tagstr=new ArrayList<String>();
	private MorelPopListener listener;
	private AppCompatActivity context;
	
	public TagPopWindow(final AppCompatActivity context,String[] tag,MorelPopListener listener) {
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.pop_tag, null);
		this.listener=listener;
		for(int i=0;i<tag.length;i++){
			tagstr.add(tag[i]);
		}
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
		PopAdapter adapter=new PopAdapter(context);
		adapter.addAll(tagstr);
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
