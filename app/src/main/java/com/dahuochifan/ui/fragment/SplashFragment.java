package com.dahuochifan.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dahuochifan.R;
import com.dahuochifan.utils.MainPicLoader;

public class SplashFragment extends Fragment {

	public static SplashFragment newInstance(String picUrl) {
		Bundle arguments = new Bundle();
		arguments.putString("Url", picUrl);
		SplashFragment fragment = new SplashFragment();
		fragment.setArguments(arguments);
		return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_splash, container, false);

		ImageView view = (ImageView) rootView.findViewById(R.id.splash_iv);

		Bundle arguments = getArguments();
		if (arguments != null) {
			String url = arguments.getString("Url");
			MainPicLoader.loadImage(url,view);
		}
		return rootView;
	}
}
