package com.dahuochifan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dahuochifan.R;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.listener.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class CookerHead {
	private static CookerHead loaderBusiness = new CookerHead(
			MyApplication.getInstance());
	private DisplayImageOptions options;
	private AnimateFirstDisplayListener animateFirstListener;

	private CookerHead(Context context) {

		animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cookhead)
				.showImageForEmptyUri(R.drawable.cookhead)
				.showImageOnFail(R.drawable.cookhead)
				.cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(0))
				.build();
	};

	public static CookerHead getInstance() {
		return loaderBusiness;
	}

	public ImageLoader getImageLoader() {
		return ImageLoader.getInstance();
	}

	public DisplayImageOptions getOptions() {
		return options;
	}

	public ImageLoadingListener getAnimateFirstListener() {
		return animateFirstListener;
	}

	public static void loadImage(String url, ImageView iv) {
		DisplayImageOptions options = getInstance().getOptions();
		ImageLoadingListener animateFirstListener = getInstance()
				.getAnimateFirstListener();
		getInstance().getImageLoader().displayImage(url, iv, options,
				animateFirstListener);
	}

	public static void loadImage(String url, ImageView iv, final int width,
			final int heigth) {
		DisplayImageOptions options = getInstance().getOptions();
		ImageLoadingListener animateFirstListener = getInstance()
				.getAnimateFirstListener();
		getInstance().getImageLoader().displayImage(url, iv, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						/*
						 * int wDp =
						 * Common.px2dip(CoordesApplication.getInstance(),
						 * width); int hDp =
						 * Common.px2dip(CoordesApplication.getInstance(),
						 * height);
						 */
						LinearLayout.LayoutParams params;
						params = (LinearLayout.LayoutParams) view
								.getLayoutParams();
						//params.width =width;
						params.height = params.width * heigth / width;
						view.setLayoutParams(params);

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});
	}

	public static void loadImage(String url, ImageView iv, int drawableId) {
		getInstance().options = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawableId)
				.showImageForEmptyUri(drawableId).showImageOnFail(drawableId)
				.cacheInMemory(false).cacheOnDisc(true)
				.considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(0))
				.build();
		loadImage(url, iv);
	}

}
