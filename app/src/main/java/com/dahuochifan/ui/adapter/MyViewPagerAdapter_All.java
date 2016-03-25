package com.dahuochifan.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morel on 2015/7/12 0012.
 */
public class MyViewPagerAdapter_All extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();//添加的Fragment的集�?
    private final List<String> mFragmentsTitles = new ArrayList<>();//每个Fragment对应的title的集�?
    public MyViewPagerAdapter_All(FragmentManager fm) {
        super(fm);
    }
    /**
     * @param fragment      添加Fragment
     * @param fragmentTitle Fragment的标题，即TabLayout中对应Tab的标�?
     */
    public void addFragment(Fragment fragment, String fragmentTitle) {
        mFragments.add(fragment);
        mFragmentsTitles.add(fragmentTitle);
    }

    @Override
    public Fragment getItem(int position) {
        //得到对应position的Fragment
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        //返回Fragment的数�?
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //得到对应position的Fragment的title
        return mFragmentsTitles.get(position);
    }
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
//        container.removeView((View) object);// 删除页卡
//    }
}
