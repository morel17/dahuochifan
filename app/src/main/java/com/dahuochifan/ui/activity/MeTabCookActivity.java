package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dahuochifan.R;
import com.dahuochifan.application.AppManager;
import com.dahuochifan.ui.fragment.MyShikeFragmentThiOthers;

public class MeTabCookActivity extends BaseActivity {
    //	private ViewPager mViewPager;
//	private TabLayout mTabLayout;
//	private MyShikeFragmentFir orderFragment;
//	private MyShikeFragmentYijie orderFragmentthi;
//	private int curIndex;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        AppManager.getAppManager().addActivity(this);
        init();
//		initData();
//		initViews();
//		initFragments();
//		EventBus.getDefault().register(this);
    }

    private void init() {
        MyShikeFragmentThiOthers settingFragment = MyShikeFragmentThiOthers.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, settingFragment).commit();
    }

    //	private void initFragments() {
//		MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
//		orderFragment = MyShikeFragmentFir.newInstance();
//		orderFragmentthi = MyShikeFragmentYijie.newInstance();
//		mViewPager.removeAllViews();
//		viewPagerAdapter.addFragment(orderFragment, "未完成订单");
//		viewPagerAdapter.addFragment(orderFragmentthi, "已接订单");
//		mViewPager.setAdapter(viewPagerAdapter);
//		mTabLayout.addTab(mTabLayout.newTab().setText("未完成订单"));
//		mTabLayout.addTab(mTabLayout.newTab().setText("已接订单"));
//		mTabLayout.setupWithViewPager(mViewPager);
//		mViewPager.setCurrentItem(curIndex);
//	}
//	private void initData() {
//		Intent intent = getIntent();
//		curIndex = intent.getIntExtra("index", 0);
//	}
//	private void initViews() {
//		mViewPager = (ViewPager) findViewById(R.id.viewpager);
//		mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
//		mViewPager.setOffscreenPageLimit(2);
//	}
//
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		EventBus.getDefault().unregister(this);
//	}
//
//	public void onEventMainThread(FirstEvent event) {
//		if (event.getType() == MyConstant.EVENTBUS_CHEF_ONE) {
//			if (orderFragment != null)
//				orderFragment.getShikeOne(true);
//		}
//		if (event.getType() == MyConstant.EVENTBUS_CHEF_THREE) {
//			if (orderFragmentthi != null)
//				orderFragmentthi.getShikeOne(true);
//		}
//		if (event.getType() == MyConstant.EVENTBUS_CHEF_MOVEONE) {
//			mViewPager.setCurrentItem(1);
//		}
//	}
    @Override
    protected int getLayoutView() {
        return R.layout.activity_fragment_collect;
    }

    @Override
    protected String initToolbarTitle() {
        return "订单详情";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.history:
                startActivity(new Intent(MeTabCookActivity.this, ChefHistoryActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
