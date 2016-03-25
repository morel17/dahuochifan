package com.dahuochifan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.application.MyApplication;
import com.dahuochifan.listener.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class PreviewLoader {
	private static PreviewLoader loaderBusiness = new PreviewLoader(MyApplication.getInstance());
	public ImageLoader imageLoader;
	private DisplayImageOptions options;
	private AnimateFirstDisplayListener animateFirstListener;

	private PreviewLoader(Context context) {

		animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.transparent).showImageForEmptyUri(R.color.transparent)
				.showImageOnFail(R.color.transparent)
				.cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(0))
				.build();
	};

	public static PreviewLoader getInstance() {
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
		ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
		getInstance().getImageLoader().displayImage(url, iv, options, null);
	}
	public static void loadImagex(String url, ImageView iv,final ProgressBar pb) {
		DisplayImageOptions options = getInstance().getOptions();
		ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
		getInstance().getImageLoader().displayImage(url, iv, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				pb.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				pb.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public static void loadImageBlur(String url, final ImageView iv, final ImageView iv2, final Context context) {
		DisplayImageOptions options = getInstance().getOptions();
		ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
		getInstance().getImageLoader().displayImage(url, iv, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// Tools.blur(loadedImage, iv, context);
				iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						iv.getViewTreeObserver().removeOnPreDrawListener(this);
						iv.buildDrawingCache();

						Bitmap bmp = iv.getDrawingCache();
						blur(bmp, iv2);
						return true;
					}
					private void blur(Bitmap bkg, ImageView view) {
						long startMs = System.currentTimeMillis();
						float scaleFactor = 1;
						float radius = 20;
						scaleFactor = 8;
						radius = 5;

						Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor),
								Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(overlay);
						canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
						canvas.scale(1 / scaleFactor, 1 / scaleFactor);
						Paint paint = new Paint();
						paint.setFlags(Paint.FILTER_BITMAP_FLAG);
						canvas.drawBitmap(bkg, 0, 0, paint);

						overlay = FastBlur.doBlur(overlay, (int) radius, true);
						iv.setImageDrawable(null);
						view.setImageDrawable(new BitmapDrawable(context.getResources(), overlay));
					}

				});
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub

			}
		});
	}
	public static void loadImage(String url, ImageView iv, final int width, final int heigth) {
		DisplayImageOptions options = getInstance().getOptions();
		ImageLoadingListener animateFirstListener = getInstance().getAnimateFirstListener();
		getInstance().getImageLoader().displayImage(url, iv, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				/*
				 * int wDp = Common.px2dip(CoordesApplication.getInstance(), width); int hDp = Common.px2dip(CoordesApplication.getInstance(), height);
				 */
				LinearLayout.LayoutParams params;
				params = (LinearLayout.LayoutParams) view.getLayoutParams();
				// params.width =width;
				params.height = params.width * heigth / width;
				view.setLayoutParams(params);

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub

			}
		});
	}

	public static void loadImage(String url, ImageView iv, int drawableId) {
		getInstance().options = new DisplayImageOptions.Builder().showImageOnLoading(drawableId).showImageForEmptyUri(drawableId).showImageOnFail(drawableId)
				.cacheInMemory(false).cacheOnDisc(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(0))
				.build();
		loadImage(url, iv);
	}

}
