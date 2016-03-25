package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.core.order.OrderParamV11;
import com.dahuochifan.event.FirstEvent;
import com.dahuochifan.utils.MainTools;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.friends.Wechat.ShareParams;
import de.greenrobot.event.EventBus;

public class OrderSuccessActivity extends BaseActivity {
	private TextView agin_tv;
	private double price;
	private TextView price_tv;
	private TextView share_tv;
	private TextView pay_tv;
	ShareParams sp;
	private float mDisplayDensity;
	private OrderParamV11 op;
	private int type;
	private TextView price_tv_all, price_tv_discount;
//	private TextView success_tv;
	private double discount;
	private String olids;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		price = getIntent().getDoubleExtra("price", 0);
		op = (OrderParamV11) getIntent().getSerializableExtra("obj");
		type = getIntent().getIntExtra("type", -1);
		discount = getIntent().getExtras().getDouble("discount");
		olids=getIntent().getExtras().getString("olids");
		pay_tv = (TextView) findViewById(R.id.pay_tv);
		price_tv_all = (TextView) findViewById(R.id.price_tv_all);
		price_tv_discount = (TextView) findViewById(R.id.price_tv_discount);
		price_tv_all.setText(MainTools.getDoubleValue(price + discount,1) + "￥");
		price_tv_discount.setText(MainTools.getDoubleValue(discount,1) + "￥");
		agin_tv = (TextView) findViewById(R.id.agin_tv);
		if (type == 0) {
			toolbar.setTitle("继续付款");
			pay_tv.setText("继续付款");
		} else if (type == 1) {
			toolbar.setTitle("订单完成");
			pay_tv.setText("查看详情");
		}
		price_tv = (TextView) findViewById(R.id.price_tv);
		share_tv = (TextView) findViewById(R.id.share_tv);
		price_tv.setText(MainTools.getDoubleValue(price,1));
		mDisplayDensity = getResources().getDisplayMetrics().density;
		btn_listener();
		ShareSDK.initSDK(this);
		sp = new ShareParams();
	}
	private void btn_listener() {
		pay_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
				Intent intent_zero = new Intent(OrderSuccessActivity.this, MainActivity.class);
				startActivity(intent_zero);
				if (MainActivity.mTabIndicator != null && MainActivity.mViewPager != null) {
					for (int i = 0; i < MainActivity.mTabIndicator.size(); i++) {
						MainActivity.mTabIndicator.get(i).setIconAlpha(0);
					}
					MainActivity.mTabIndicator.get(1).setIconAlpha(1.0f);
					MainActivity.mViewPager.setCurrentItem(1, false);
				}
				if (ChefDetailActivity.instance != null) {
					ChefDetailActivity.instance.finish();
				}
//				op.getChefids();
//				op.getList().get(0).getCbids();
				if(type==0){
					Intent intent = new Intent(OrderSuccessActivity.this, OrderDetailActivity2.class);
					Bundle bundle = new Bundle();
					bundle.putString("olids",olids);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				finish();
			}
		});
		agin_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
				Intent intent_zero = new Intent(OrderSuccessActivity.this, MainActivity.class);
				startActivity(intent_zero);
				if (MainActivity.mTabIndicator != null && MainActivity.mViewPager != null) {
					for (int i = 0; i < MainActivity.mTabIndicator.size(); i++) {
						MainActivity.mTabIndicator.get(i).setIconAlpha(0);
					}
					MainActivity.mTabIndicator.get(0).setIconAlpha(1.0f);
					MainActivity.mViewPager.setCurrentItem(0, false);
				}
				if (ChefDetailActivity.instance != null) {
					ChefDetailActivity.instance.finish();
				}

				finish();
			}
		});
		share_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Platform plat = ShareSDK.getPlatform(OrderSuccessActivity.this, Wechat.NAME);
				plat.setPlatformActionListener(new PlatformActionListener() {

					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onCancel(Platform arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});
				Wechat.ShareParams sp = new Wechat.ShareParams();
				sp.title = "喊你吃饭啦";
				String content = "";
				if (op != null) {
					if (TextUtils.isEmpty(op.getLunchtime())) {
						if (op.getWhenindex().equals("0")) {
							content = "今天晚上跟我一起搭伙!";
						} else {
							content = "明天晚上跟我一起搭伙!";
						}
					} else if (TextUtils.isEmpty(op.getDinnertime())) {
						if (op.getWhenindex().equals("0")) {
							content = "今天中午跟我一起搭伙!";
						} else {
							content = "明天中午跟我一起搭伙!";
						}
					} else {
						if (op.getWhenindex().equals("0")) {
							content = "今天中午和晚上跟我一起搭伙!";
						} else {
							content = "明天中午和晚上跟我一起搭伙!";
						}
					}
					sp.text = content + "\nwww.dahuochifan.com/download/app";
				} else {
					sp.text = "搭伙吃饭APP下载/n正宗家庭小灶，纯正老家味";
				}
				sp.shareType = Platform.SHARE_TEXT;
				plat.share(sp);
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (ChefDetailActivity.instance != null) {
				ChefDetailActivity.instance.finish();
			}
			finish();
		}
		return false;

	}
	@Override
	protected int getLayoutView() {
		return R.layout.activity_ordersuccess_new;
	}

	@Override
	protected String initToolbarTitle() {
		return "";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				EventBus.getDefault().post(new FirstEvent(MyConstant.EVENTBUS_ORDER_ONE));
				Intent intent_zero = new Intent(OrderSuccessActivity.this, MainActivity.class);
				startActivity(intent_zero);
				if (MainActivity.mTabIndicator != null && MainActivity.mViewPager != null) {
					for (int i = 0; i < MainActivity.mTabIndicator.size(); i++) {
						MainActivity.mTabIndicator.get(i).setIconAlpha(0);
					}
					MainActivity.mTabIndicator.get(0).setIconAlpha(1.0f);
					MainActivity.mViewPager.setCurrentItem(0, false);
				}
				if (ChefDetailActivity.instance != null) {
					ChefDetailActivity.instance.finish();
				}
				finish();
			default :
				return super.onOptionsItemSelected(item);
		}
	}
}
