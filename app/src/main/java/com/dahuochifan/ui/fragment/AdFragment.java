package com.dahuochifan.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dahuochifan.R;
import com.dahuochifan.ui.activity.DhActivitiesDetailActivity;
import com.dahuochifan.utils.AdLoader;
import com.nostra13.universalimageloader.utils.L;

public class AdFragment extends  BaseFragment{
	private String detail;
	private String url;
	public static AdFragment newInstance(String picUrl, String detailStr) {
		Bundle arguments = new Bundle();
		arguments.putString("Url", picUrl);
		arguments.putString("detail", detailStr);
		AdFragment fragment = new AdFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
		ImageView view = (ImageView) rootView.findViewById(R.id.splash_iv);
		Bundle arguments = getArguments();
		if (arguments != null) {
			url = arguments.getString("Url");
			AdLoader.loadImage(url, view);
			detail = arguments.getString("detail");
		}
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(detail)) {
					Intent intent = new Intent(mActivity, DhActivitiesDetailActivity.class);
					intent.putExtra("detail", detail);
					intent.putExtra("url", url);
					L.e("url=====" + url);
					startActivity(intent);
				}
			}
		});
		return rootView;
	}
}
