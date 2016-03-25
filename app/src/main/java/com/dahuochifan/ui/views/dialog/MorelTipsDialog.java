package com.dahuochifan.ui.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dahuochifan.R;

public class MorelTipsDialog extends Dialog implements View.OnClickListener {
	private MorelTipsListener listener;
	private TextView ok_tv;
	private TextView cancel_tv;
	private TextView tv;
	private String title;
	private String content;
	private TextView title_tv;

	public interface MorelTipsListener {
		public void onClick(View view, TextView tv);
	}
	public MorelTipsDialog(Context context, int theme, String title, String content, MorelTipsListener listener) {
		super(context,theme);
		this.listener = listener;
		this.title = title;
		this.content = content;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tips);
		ok_tv = (TextView) findViewById(R.id.ok_tv);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		tv = (TextView) findViewById(R.id.nickname_tv);
		title_tv = (TextView) findViewById(R.id.title_tv);
		tv.setText(content);
		title_tv.setText(title);
		ok_tv.setOnClickListener(this);
		cancel_tv.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		listener.onClick(v, tv);
	}

}
