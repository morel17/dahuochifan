package com.dahuochifan.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyWrapListView extends ListView {
	public MyWrapListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyWrapListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyWrapListView(Context context) {
		super(context);
	}
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
