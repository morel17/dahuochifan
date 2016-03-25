package com.dahuochifan.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dahuochifan.ui.fragment.WelcomeFragment;

import java.util.ArrayList;

public class WelcomeAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> ImgList;

    public WelcomeAdapter(FragmentManager fm) {
        super(fm);
        ImgList=new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        return WelcomeFragment.newInstance(ImgList.get(i));
    }

    @Override
    public int getCount() {
        if(ImgList==null){
            ImgList=new ArrayList<>();
        }
        return ImgList.size();
    }

    public void addAll(ArrayList<String> picList) {
        this.ImgList = picList;
    }
}
