package com.dahuochifan.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dahuochifan.R;

/**
 * <p>
 * Title: CustomDialog
 * </p>
 * <p>
 * Description:自定义Dialog（参数传入Dialog样式文件，Dialog布局文件）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * @author morel
 * @version 1.01
 */
public class CustomDialog extends Dialog implements
		View.OnClickListener {

	/** 布局文件 **/
	int layoutRes;
	/** 上下文对象 **/
	Context context;
	/** 确定按钮 **/
	private Button confirmBtn;
	/** 取消按钮 **/
	private Button cancelBtn;
	/** 离线消息按钮 **/
	private RadioButton myRadioButton;
	/** 点击次数 **/
	private int check_count = 0;
	/** Toast时间 **/
	public static final int TOAST_TIME = 1000;
	private TextView title;
	private String tittle;
	private OnClickListener  mCancelClickListener;
	private OnClickListener  mConfirmClickListener;
	

	public CustomDialog(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 自定义布局的构造方法
	 * 
	 * @param context
	 * @param resLayout
	 */
	public CustomDialog(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialog(Context context, int theme, int resLayout,String tittle) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.tittle=tittle;
	}
	public void SetTitleText(String tittle){
		title.setText(tittle);
	}
	
	public CustomDialog setCancelClickListener(OnClickListener listener) {
		mCancelClickListener = listener;
        return this;
	}
	public CustomDialog setConfirmClickListener(OnClickListener listener) {
		mConfirmClickListener = listener;
        return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 指定布局
		this.setContentView(layoutRes);

		// 根据id在布局中找到控件对象
		title=(TextView)findViewById(R.id.title);
		confirmBtn = (Button) findViewById(R.id.confirm_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		//myRadioButton = (RadioButton) findViewById(R.id.my_rbtn);

		// 设置按钮的文本颜色
		confirmBtn.setTextColor(0xffe95504);
		cancelBtn.setTextColor(0xffe95504);

		// 为按钮绑定点击事件监听器
		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		//myRadioButton.setOnClickListener(this);
		setCancelable(false);
		SetTitleText(tittle);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.confirm_btn:
			// 点击了确认按钮
			 if (mConfirmClickListener != null) {
	                mConfirmClickListener.onClick(CustomDialog.this, R.id.confirm_btn);
	            } else {
	                dismiss();
	            }
			break;

		case R.id.cancel_btn:
			// 点击了取消按钮
			 if (mCancelClickListener != null) {
	                mCancelClickListener.onClick(CustomDialog.this,R.id.cancel_btn);
	            } else {
	                dismiss();
	            }
			break;

		default:
			break;
		}
	}
}