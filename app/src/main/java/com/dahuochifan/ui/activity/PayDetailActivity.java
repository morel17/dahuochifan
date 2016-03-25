package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bear.risenumbertest.lib.RiseNumberTextView;
import com.dahuochifan.R;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.views.CircleImageView;

public class PayDetailActivity extends AppCompatActivity {
	private CircleImageView customer_iv;
	private TextView customer_tv, account_tv, tag_tv, level_tv, pay_tv, create_tv2, order_tv2;
	private RiseNumberTextView price_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paydetail);
		AppManager.getAppManager().addActivity(this);
		initViews();
	}
	private void initViews() {
		customer_iv = (CircleImageView) findViewById(R.id.customer_iv);
		customer_tv = (TextView) findViewById(R.id.customer_tv);
		account_tv = (TextView) findViewById(R.id.account_tv);
		tag_tv = (TextView) findViewById(R.id.tag_tv);
		level_tv = (TextView) findViewById(R.id.level_tv);
		pay_tv = (TextView) findViewById(R.id.pay_tv);
		create_tv2 = (TextView) findViewById(R.id.create_tv2);
		order_tv2 = (TextView) findViewById(R.id.order_tv2);
		price_tv = (RiseNumberTextView) findViewById(R.id.price_tv);
		price_tv.withNumber(200);
		price_tv.setDuration(666);
		price_tv.start();
	}
}
