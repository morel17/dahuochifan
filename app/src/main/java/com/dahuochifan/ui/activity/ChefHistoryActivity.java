package com.dahuochifan.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.dahuochifan.R;
import com.dahuochifan.ui.fragment.MyShikeFragmentThi;

public class ChefHistoryActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		init();
	}
	private void init() {
		MyShikeFragmentThi settingFragment=new MyShikeFragmentThi();
		settingFragment= settingFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, settingFragment).commit();
	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_fragment_collect;
	}

	@Override
	protected String initToolbarTitle() {
		return "历史订单";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				finish();
			default :
				return super.onOptionsItemSelected(item);
		}
	}
}
