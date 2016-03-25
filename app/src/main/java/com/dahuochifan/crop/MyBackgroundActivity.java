package com.dahuochifan.crop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dahuochifan.R;
import com.dahuochifan.crop.ListImageDirPopupWindow.OnImageDirSelected;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MyBackgroundActivity extends Activity implements OnImageDirSelected {
	private ProgressDialog mProgressDialog;
	private TextView Suer_tv;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;
	private List<String> mImgsNew;

	private GridView mGirdView;
	private MyBackgroundAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;
	private String type;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "相册无图片", Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list());
		mImgsNew.clear();
		for(int i=mImgs.size()-1;i>=0;i--){
			mImgsNew.add(mImgs.get(i));
		}
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		L.e("mImgs1===" + mImgsNew.size() + "**"+mImgDir.getAbsolutePath());
		mAdapter = new MyBackgroundAdapter(getApplicationContext(), mImgsNew, R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		Log.e("diaoyong", "haha");
		// mImageCount.setText("全部图片："+totalCount + "张");
		mChooseDir.setText("点击更多");
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), mImageFloders, LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybackground_main);
		Intent intent_get=getIntent();
		Bundle bundle=intent_get.getExtras();
		type=bundle.getString("type");
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		mImgsNew=new ArrayList<>();
		initView();
		getImages();
		initEvent();
		Suer_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MyBackgroundAdapter.mSelectedImage.size() <= 0) {
					Toast.makeText(MyBackgroundActivity.this, "您还没有选择图片...", Toast.LENGTH_SHORT).show();
				} else if (MyBackgroundAdapter.mSelectedImage.size() > 1) {
					Toast.makeText(MyBackgroundActivity.this, "您只能选择一张图片...", Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(MyBackgroundActivity.this,
					// MyBackgroundAdapter.mSelectedImage.get(0),
					// Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("PATH", MyBackgroundAdapter.mSelectedImage.get(0));
					intent.putExtra("type", type);
					setResult(RESULT_OK, intent);
					finish();
					MyBackgroundAdapter.mSelectedImage.clear();
				}
			}
		});
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = MyBackgroundActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE
						+ "=?", new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}
					if(parentFile.list()==null)continue;
					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		Suer_tv = (TextView) findViewById(R.id.sure);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {
		MyBackgroundAdapter.mSelectedImage.clear();
		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		L.e("mImgs2===" + mImgs.size() + "**"+mImgDir.getAbsolutePath());
		mImgsNew.clear();
		for(int i=mImgs.size()-1;i>=0;i--){
			mImgsNew.add(mImgs.get(i));
		}
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyBackgroundAdapter(getApplicationContext(), mImgsNew, R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyBackgroundAdapter.mSelectedImage.clear();
	}

}
