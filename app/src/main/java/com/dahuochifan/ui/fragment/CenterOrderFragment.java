package com.dahuochifan.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.MyViewPagerAdapter_Order;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;

import de.greenrobot.event.EventBus;

public class CenterOrderFragment extends LazyFragment {
	private View view;
	private ViewPager mViewPager;
	private TabLayout mTabLayout;

	MyOrderFragment orderFragment;
	MyOrderFragmentSec orderFragmentSec;
	MyOrderFragmentThi orderFragmentThi;
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_centerorder_new, container, false);
		initViews(view);
		isPrepared = true;
		lazyLoad();
		EventBus.getDefault().register(this);
		return view;
	}

	private void initFragments() {
		MyViewPagerAdapter_Order viewPagerAdapter = new MyViewPagerAdapter_Order(getChildFragmentManager());
		orderFragment=new MyOrderFragment();
		orderFragment = orderFragment.newInstance();
		orderFragmentSec=new MyOrderFragmentSec();
		orderFragmentSec = orderFragmentSec.newInstance();
		orderFragmentThi=new MyOrderFragmentThi();
		orderFragmentThi = orderFragmentThi.newInstance();
//		mViewPager.removeAllViews();
		viewPagerAdapter.addFragment(orderFragment, "未接订单");
		viewPagerAdapter.addFragment(orderFragmentSec, "待评价订单");
		viewPagerAdapter.addFragment(orderFragmentThi, "历史订单");
		mViewPager.setAdapter(viewPagerAdapter);
		mTabLayout.addTab(mTabLayout.newTab().setText("未接订单"));
		mTabLayout.addTab(mTabLayout.newTab().setText("待评价订单"));
		mTabLayout.addTab(mTabLayout.newTab().setText("历史订单"));
		mTabLayout.setupWithViewPager(mViewPager);
		mViewPager.setCurrentItem(0);
	}


	private void initViews(View myView) {
		mViewPager = (ViewPager) myView.findViewById(R.id.metab_viewPager_order);
		mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout_order);
		// 0xffe95504
		// 设置滚动监听
		mViewPager.setOffscreenPageLimit(2);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isPrepared=false;
		mHasLoadedOnce=false;
		EventBus.getDefault().unregister(this);
	}
	public void onEventMainThread(FirstEvent event) {
		if (event.getType() == MyConstant.EVENTBUS_ORDER_ONE) {
			if (orderFragment != null)
				orderFragment.getOrderLoad(true);
		}
		if (event.getType() == MyConstant.EVENTBUS_ORDER_TWO) {
			orderFragmentSec.getOrderLoad(true);
		}
		if (event.getType() == MyConstant.EVENTBUS_ORDER_THREE) {
			orderFragmentThi.getOrderLoad(true);

		}
		if (event.getType() == MyConstant.EVENTBUS_ORDER_MOVEONE) {
			mViewPager.setCurrentItem(1);
		}
		if (event != null && event.getType() == MyConstant.EVENTBUS_PAY) {
			if (event.getPosition() == 0) {
				//代表微信支付成功
				if (orderFragment != null)
					orderFragment.postSuccess();
			}
		}
	}
	@Override
	protected void lazyLoad() {
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		initFragments();
		mHasLoadedOnce=true;
	}
}
