package com.dahuochifan.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class DHTextView extends TextView {
	private Context mContext;
	private String TypefaceName = "DH001";
	public String getTypefaceName() {
		return TypefaceName;
	}

	public void setTypefaceName(String typefaceName) {
		TypefaceName = typefaceName;
		Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/" + TypefaceName + ".ttf");
		this.setTypeface(typeface);
		System.gc();
	}

	public DHTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
//		this.setTypeface(MyConstant.typeface);
	}

	public DHTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
//		this.setTypeface(MyConstant.typeface);
	}

	public DHTextView(Context context) {
		super(context);
		this.mContext = context;
//		this.setTypeface(MyConstant.typeface);
	}
}
