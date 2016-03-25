package com.dahuochifan.ui.views.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.dahuochifan.R;
import com.zhy.view.HorizontalProgressBarWithNumber;

public class UpdateDialog extends AlertDialog implements View.OnClickListener{
	public interface MyUpdateListener {
		public void onClick(View view);
	}
	private Context context;
	private MyUpdateListener listener;
	private HorizontalProgressBarWithNumber progress;
	private TextView cancel_tv;
	
	public UpdateDialog(Context context,MyUpdateListener listener) {
		super(context);
		this.context=context;
		this.listener=listener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress);
		progress=(HorizontalProgressBarWithNumber)findViewById(R.id.progress);
		cancel_tv=(TextView)findViewById(R.id.cancel_tv);
		cancel_tv.setOnClickListener(this);
	}
	public HorizontalProgressBarWithNumber getPb(){
		return progress;
	}
	@Override
	public void onClick(View v) {
		listener.onClick(v);
	}

}
