package com.dahuochifan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.bean.User;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/*
 * ϵͳ������
 * */
public class Tools {
	public static long lastClickTime;

	// �ж���û��sd��
	public boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	// ��ø�Ŀ¼·��
	public String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:
																				// /data/data/
		}
	}

	// �������viewװ���� bitmap��ʽ
	public Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	/**
	 * ��pxֵת��Ϊdip��dpֵ����֤�ߴ��С����
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 
	 * ��dip��dpֵת��Ϊpxֵ����֤�ߴ��С����
	 * 
	 * @param dipValue
	 * 
	 * @param
	 * 
	 * @return
	 */

	public static int dip2px(Context context, float dipValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * scale + 0.5f);

	}

	/**
	 * 
	 * ��pxֵת��Ϊspֵ����֤���ִ�С����
	 * 
	 * @param pxValue
	 * 
	 * @param
	 * 
	 * @return
	 */

	public int px2sp(Context context, float pxValue) {

		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pxValue / fontScale + 0.5f);

	}

	/**
	 * 
	 * ��pxֵת��Ϊspֵ����֤���ִ�С����
	 * 
	 * @param spValue
	 * 
	 * @param
	 * 
	 * @return
	 */

	public int sp2px(Context context, float spValue) {

		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (spValue * fontScale + 0.5f);

	}

	public boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
	}

	public boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
	}

	/**
	 * ����Ƿ����SDCard
	 * 
	 * @return
	 */
	public boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private final int STROKE_WIDTH = 4;

	// ��assets��Դ�л�ȡͼƬ
	public Bitmap getBitmap(Context context, String filename) {

		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {

			InputStream is = am.open(filename);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public Bitmap toRoundBitmap(Context context, String filename) {
		Bitmap bitmap = getBitmap(context, filename);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			left = 0;
			bottom = width;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(4);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		// ����ɫԲȦ
		paint.reset();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
		paint.setAntiAlias(true);
		canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2, paint);
		return output;
	}
	/**
	 * ���ư�ť�����Ƶ��
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
	public static String getDistanc(int distance) {
		String dist = "";
		if (distance < 1000) {
			dist = distance + "m";
		} else {
			double distance2 = distance;
			double newdistance = distance2 / 1000;
			BigDecimal b = new BigDecimal(newdistance);
			DecimalFormat decimalFormat = new DecimalFormat("######0.00");
			dist = decimalFormat.format(newdistance) + "km";
		}
		return dist;
	}
	public static String getDistanc2(int distance) {
		String dist = "";
		if (distance < 1000) {
			dist = distance + "m";
		} else {
			double distance2 = distance;
			double newdistance = distance2 / 1000;
			BigDecimal b = new BigDecimal(newdistance);
			DecimalFormat decimalFormat = new DecimalFormat("######0.0");
			dist = decimalFormat.format(newdistance) + "km";
		}
		return dist;
	}

	public static void blur(Bitmap bkg, ImageView view, Context context) {
		if(BuildConfig.LEO_DEBUG)L.e(bkg.getWidth() + "**%%1" + bkg.getHeight());
		long startMs = System.currentTimeMillis();
		float scaleFactor = 1;
		float radius = 20;
		scaleFactor = 8;
		radius = 2;

		Bitmap overlay = Bitmap.createBitmap((int) (context.getResources().getDimensionPixelSize(R.dimen.width_80_80) / scaleFactor), (int) (context
				.getResources().getDimensionPixelSize(R.dimen.width_53_80) / scaleFactor), Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bkg, 0, 0, paint);

		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		view.setImageDrawable(null);
		if(BuildConfig.LEO_DEBUG)L.e(overlay.getWidth() + "**%%" + overlay.getHeight());
		overlay = Bitmap.createScaledBitmap(overlay, px2dip(context, context.getResources().getDimensionPixelSize(R.dimen.width_80_80)),
				px2dip(context, context.getResources().getDimensionPixelSize(R.dimen.width_53_80)), true);
		if(BuildConfig.LEO_DEBUG)L.e(overlay.getWidth() + "**%%2" + overlay.getHeight());
		view.setImageDrawable(new BitmapDrawable(context.getResources(), overlay));
	}

	public static String getEncoding(String str) {

		String encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}
	
	public static void updateInfo(SharedPreferences spf){
		if(MyConstant.user==null){
			MyConstant.user=new User();
		}
		MyConstant.user.setUserids(SharedPreferenceUtil.initSharedPerence().getUserId(spf));
		if(!TextUtils.isEmpty(SharedPreferenceUtil.initSharedPerence().getToken(spf))){
			MyConstant.user.setToken(SharedPreferenceUtil.initSharedPerence().getToken(spf));
		}else{
			MyConstant.user.setToken("dahuochifan");
		}
		MyConstant.user.setUsername(SharedPreferenceUtil.initSharedPerence().getTempUser(spf));
		MyConstant.user.setHomeprov(SharedPreferenceUtil.initSharedPerence().getHomeProv(spf));
		MyConstant.user.setCurprov(SharedPreferenceUtil.initSharedPerence().getCurProv(spf));
		MyConstant.user.setRole(SharedPreferenceUtil.initSharedPerence().getRole(spf));
		MyConstant.user.setAvatar(SharedPreferenceUtil.initSharedPerence().getAvatar(spf));
		MyConstant.user.setChefids(SharedPreferenceUtil.initSharedPerence().getChefIds(spf));
		MyConstant.user.setLogin(SharedPreferenceUtil.initSharedPerence().getLogin(spf));
		MyConstant.user.setOtherprovs(SharedPreferenceUtil.initSharedPerence().getOtherProv(spf));
		MyConstant.user.setNickname(SharedPreferenceUtil.initSharedPerence().getNickname(spf));
		MyConstant.user.setCurcity(SharedPreferenceUtil.initSharedPerence().getCurCity(spf));
		MyConstant.user.setHomecity(SharedPreferenceUtil.initSharedPerence().getHomeCity(spf));
		MyConstant.user.setMobile(SharedPreferenceUtil.initSharedPerence().getMobile(spf));
		MyConstant.user.setAddressinfo(SharedPreferenceUtil.initSharedPerence().getAddressInfo(spf));
		MyConstant.user.setAge(SharedPreferenceUtil.initSharedPerence().getAge(spf));
	}

	public static String getDouble2String(double d, int digit) {
		try {
		String str = d + "";
		if (digit >= 0) {
			str = String.format("%." + digit + "f", d);
		}
		return str;
		}catch (Exception e){

		}
		return  "";
	}
	public static String getDouble2String(String str, int digit) {
		try {
			if (digit >= 0) {
				str = String.format("%." + digit + "f", str);
			}
			return str;
		}catch (Exception e){

		}
		return  "";
	}
	public static double toDouble(String str){
		double dhDouble=0d;
		try {
			dhDouble=Double.parseDouble(str);
		}catch (Exception e){
		}
		return dhDouble;
	}
	public static int toInteger(String str){
		int dhInteger=0;
		try {
			dhInteger=Integer.parseInt(str);
		}catch (Exception e){
		}
		return dhInteger;
	}
	public static float toFloat(String str){
		float dhFloat=0;
		try {
			dhFloat=Float.parseFloat(str);
		}catch (Exception e){
		}
		return dhFloat;
	}
	public static String toString(String str){
		if(TextUtils.isEmpty(str)){
			return "";
		}else{
			return str;
		}
	}
}
