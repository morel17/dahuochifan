package com.dahuochifan.ui.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dahuochifan.R;

public class MorelDialog extends Dialog implements View.OnClickListener {
	public interface MorelDialogListener {
		 void onClick(View view, EditText et);
	}
	private MorelDialogListener listener;
	private TextView ok_tv;
	private TextView cancel_tv;
	private EditText et;
	private TextView title_tv;
	private String title;
	private String content;
	private int length;
	private Context context;
	public MorelDialog(Context context, int theme, String title, String content, int length, MorelDialogListener listener) {
		super(context, theme);
		this.listener = listener;
		this.title = title;
		this.content = content;
		this.length = length;
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_notify);
		ok_tv = (TextView) findViewById(R.id.ok_tv);
		cancel_tv = (TextView) findViewById(R.id.cancel_tv);
		et = (EditText) findViewById(R.id.nickname_et);
		et.setFocusable(true);
		et.setFocusableInTouchMode(true);
		et.requestFocus();
		et.setText(content);
		if (length > 0) {
			InputFilter[] filters = {new InputFilter.LengthFilter(length)};
			et.setFilters(filters);
		}
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText(title);
		ok_tv.setOnClickListener(this);
		cancel_tv.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		listener.onClick(v, et);
	}

}
