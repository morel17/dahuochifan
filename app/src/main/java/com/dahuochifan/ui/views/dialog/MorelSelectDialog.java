package com.dahuochifan.ui.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dahuochifan.R;
import com.zcw.togglebutton.ToggleButton;

public class MorelSelectDialog extends Dialog implements View.OnClickListener {
	private MorelSelectListener listener;
	private TextView ok_tv;
	private TextView cancel_tv;
	private String title;
	private TextView title_tv;
	private ToggleButton tg;
	public interface MorelSelectListener {
		public void onClick(View view, ToggleButton tg);
	}
	public MorelSelectDialog(Context context, int theme,String title, MorelSelectListener listener) {
		super(context);
		this.listener = listener;
		this.title = title;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select);
		ok_tv = (TextView) findViewById(R.id.ok_tv);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		tg = (ToggleButton) findViewById(R.id.mytogglebtn);
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText(title);
		ok_tv.setOnClickListener(this);
		cancel_tv.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		listener.onClick(v, tg);
	}

}
