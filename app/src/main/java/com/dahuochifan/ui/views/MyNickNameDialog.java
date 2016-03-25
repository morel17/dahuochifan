package com.dahuochifan.ui.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dahuochifan.R;

public class MyNickNameDialog extends Dialog implements View.OnClickListener{
//	private MyNicknameListener listener;
	private Context context;
	private TextView ok_tv;
	private TextView cancel_tv;
	private EditText et;
	private String nicknameStr;
	public interface MyNicknameListener{
		public void onClick(View view, EditText et);
	}
	private MyNicknameListener listener;
	public MyNickNameDialog(Activity context,int theme,String nicknameStr,MyNicknameListener myNicknameListener) {
		super(context,theme);
		this.context=context;
		this.listener=myNicknameListener;
		this.nicknameStr=nicknameStr;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_nickname);
		ok_tv=(TextView)findViewById(R.id.ok_tv);
		cancel_tv=(TextView)findViewById(R.id.cancel_tv);
		et=(EditText)findViewById(R.id.nickname_et);
		ok_tv.setOnClickListener(this);
		cancel_tv.setOnClickListener(this);
		et.setText(nicknameStr);
	}
	@Override
	public void onClick(View v) {
		listener.onClick(v,et);
	}
}
