package com.dahuochifan.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.ui.adapter.MyViewPagerAdapter_Shike;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.event.FirstEvent;
import com.nostra13.universalimageloader.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by Morel on 2015/8/26.
 */
public class CenterShikeFragment extends LazyFragment{
    private View view;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private MyShikeFragmentFir orderFragment;
    private MyShikeFragmentYijie orderFragmentthi;
    private MyShikeFragmentThi settingFragment;

    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shike_all, container, false);
        initViews(view);
        isPrepared = true;
        lazyLoad();
        EventBus.getDefault().register(this);
        return view;
    }

    private void initFragments() {
        MyViewPagerAdapter_Shike viewPagerAdapter = new MyViewPagerAdapter_Shike(getChildFragmentManager());
        orderFragment=new MyShikeFragmentFir();
        orderFragment = orderFragment.newInstance();
        orderFragmentthi=new MyShikeFragmentYijie();
        orderFragmentthi = orderFragmentthi.newInstance();
        settingFragment=new MyShikeFragmentThi();
        settingFragment = settingFragment.newInstance();
//        mViewPager.removeAllViews();
        viewPagerAdapter.addFragment(orderFragment, "未完成订单");
        viewPagerAdapter.addFragment(orderFragmentthi, "已接订单");
        viewPagerAdapter.addFragment(settingFragment,"历史订单");
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setText("未完成订单"));
        mTabLayout.addTab(mTabLayout.newTab().setText("已接订单"));
        mTabLayout.addTab(mTabLayout.newTab().setText("历史订单"));
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

    private void initViews(View mView) {
        mViewPager = (ViewPager) mView.findViewById(R.id.metab_viewPager_shike);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout_shike);
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
        if (event.getType() == MyConstant.EVENTBUS_CHEF_ONE) {
            if(BuildConfig.LEO_DEBUG) L.e("hehehe1");
            if (orderFragment != null)
                if(BuildConfig.LEO_DEBUG) L.e("hehehe2");
                orderFragment.getShikeOne(true);
        }
        if (event.getType() == MyConstant.EVENTBUS_CHEF_THREE) {
            if (settingFragment != null)
                settingFragment.getShikeOne(true);
        }
        if (event.getType() == MyConstant.EVENTBUS_CHEF_TWO) {
            if (orderFragmentthi != null)
                orderFragmentthi.getShikeOne(true);
        }
        if (event.getType() == MyConstant.EVENTBUS_CHEF_MOVEONE) {
            mViewPager.setCurrentItem(1);
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
