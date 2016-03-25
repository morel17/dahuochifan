package com.dahuochifan.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dahuochifan.ui.fragment.SplashFragment;

import java.util.ArrayList;

public class SplashAdapter extends FragmentStatePagerAdapter {
	private ArrayList<String> ImgList;

	public SplashAdapter(FragmentManager fm) {
		super(fm);
		ImgList=new ArrayList<>();
	}

	@Override
	public Fragment getItem(int i) {
		return SplashFragment.newInstance(ImgList.get(i));
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
