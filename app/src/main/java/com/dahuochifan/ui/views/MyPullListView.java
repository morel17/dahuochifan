package com.dahuochifan.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MyPullListView extends PullToRefreshListView {
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;
	public MyPullListView(Context context) {
		super(context);
	}

	public MyPullListView(Context context, com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode) {
		super(context, mode);
	}
	public MyPullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MyPullListView(Context context, com.handmark.pulltorefresh.library.PullToRefreshBase.Mode mode,
			com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
	}
}
