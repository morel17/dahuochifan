package com.dahuochifan.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.dahuochifan.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTools {
    private static Toast toast = null;
    private static DisplayMetrics dm;
    private static double EARTH_RADIUS = 6378.137;// 地球半径
    private static final String ymd_hms = "yyyy-MM-dd HH:mm:ss";
    private static final String ymd_hm = "yyyy-MM-dd HH:mm";
    public static int SCREEN_WIDTH_PIXELS;
    public static int SCREEN_HEIGHT_PIXELS;
    public static float SCREEN_DENSITY;
    public static int SCREEN_WIDTH_DP;
    public static int SCREEN_HEIGHT_DP;
    private static boolean sInitialed;

    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static void setTopll(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.setTheme(R.style.BaseTheme_Translucent);
        } else {
        }
    }

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ShowToast(Context cont, String msg) {
        if (toast == null) {
            toast = Toast.makeText(cont, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void ShowToast(Context cont, int msg) {

        if (toast == null) {
            toast = Toast.makeText(cont, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void CancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }

    public static int getWindowWidth(Activity activity) {
        if (dm == null) {
            dm = new DisplayMetrics();
        }
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getWindowHeight(Activity activity) {
        if (dm == null) {
            dm = new DisplayMetrics();
        }
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static String dayForTime(String pTime) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /** */
        /** 将截取到的时间字符串转化为时间格式的字符串 **/
        Date startD;

        try {
            startD = sdf.parse(pTime);
            str = new SimpleDateFormat("HH:mm:ss").format(startD);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
    public static String dayForTime2(String pTime) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        SimpleDateFormat.getDateInstance().
        /** */
        /** 将截取到的时间字符串转化为时间格式的字符串 **/
        Date startD;

        try {
            startD = sdf.parse(pTime);
            str = new SimpleDateFormat("HH:mm").format(startD);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
    public static String dayForDay(String pTime) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /** */
        /** 将截取到的时间字符串转化为时间格式的字符串 **/
        Date startD;

        try {
            startD = sdf.parse(pTime);
            str = new SimpleDateFormat("yyyy-MM-dd").format(startD);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    public static String dayForWeek(String pTime) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date tmpDate;
        String week = null;
        try {
            tmpDate = format.parse(pTime);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            week = sdf.format(tmpDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    public static String getWeek(Date tmpDate) {
        String week = null;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        week = sdf.format(tmpDate);
        return week;
    }

    public static long currentdaydistance(String start) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String end = sdf.format(date);
        Date startD;
        long day = 0;
        try {
            startD = sdf.parse(start);
            Date endD = sdf.parse(end);
            day = (endD.getTime() - startD.getTime()) / 1000 / (24 * 3600);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.abs(day);
    }

    public static long currentdaydistance2(String start) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String end = sdf.format(date);
        Date startD;
        long seconds = 0;
        long day = 0;
        try {
            startD = sdf.parse(start);
            Date endD = sdf.parse(end);
            seconds = endD.getTime() - startD.getTime();
            day = seconds / 1000 / (24 * 3600);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.abs(day) + 1;
    }

    public static long daydistance(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /** */
        /** 将截取到的时间字符串转化为时间格式的字符串 **/
        Date startD;
        long day = 0;
        try {
            startD = sdf.parse(start);
            Date endD = sdf.parse(end);
            day = (endD.getTime() - startD.getTime()) / 1000 / (24 * 3600) + 1;//因为订单，当天算一天，所以要加1
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return day;
    }

    public static boolean isMore3Hours(String chooseTimeStr) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        String curTime = df.format(new Date());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(curTime));
            c2.setTime(df.parse(chooseTimeStr));
            long tiem = c2.getTimeInMillis() - c1.getTimeInMillis();
            long time_normal = 1000 * 3600 * 3;
            if (tiem > time_normal) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            System.err.println("格式不正确");
        }
        return false;
    }
//	public static String getMore3Hours(){
//		java.text.DateFormat df = new SimpleDateFormat("HH:mm");
//		String curTime = df.format(new Date());
//		Calendar c1 = Calendar.getInstance();
//		try {
//			c1.setTime(df.parse(curTime));
//			if(c1.get(Calendar.MINUTE)==0){
//				c1.add(Calendar.HOUR_OF_DAY, 3);
//				return c1.get(Calendar.HOUR_OF_DAY)+":"+"00";
//			}else if(c1.get(Calendar.MINUTE)<=30&c1.get(Calendar.MINUTE)>0 ){
//				c1.add(Calendar.HOUR_OF_DAY, 3);
//				return c1.get(Calendar.HOUR_OF_DAY)+":"+"30";
//			}else {
//				c1.add(Calendar.HOUR_OF_DAY, 4);
//				return c1.get(Calendar.HOUR_OF_DAY)+":"+"00";
//			}
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}

    public static String getMoreMinuteTime(int minuteTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, minuteTime);
        return sdf.format(nowTime.getTime());
    }
    public static String getLastTime(String lastTime, int minuteTime) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(lastTime));
            c1.add(Calendar.MINUTE, -minuteTime);
            return df.format(c1.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getTimeStr(int whenIndex) {//获取日期
        //获取当前日期
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("MM月dd日 ");
        String nowDate = sf.format(date);
        if (whenIndex == 0) {
            return nowDate + getWeek(date);
        }
        //通过日历获取下一天日期
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sf.parse(nowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_YEAR, +1);
        String nextDate_1 = sf.format(cal.getTime());
        return nextDate_1 + getWeek(cal.getTime());
    }

    /**
     * 大于设定时间
     *
     * @param endTime
     * @return
     */
    public static boolean timeCompare(String endTime) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        String curTime = df.format(new Date());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(curTime));
            c2.setTime(df.parse(endTime));
        } catch (ParseException e) {
            System.err.println("格式不正确");
        }
        int result = c1.compareTo(c2);
        if (result >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean timeCompare2(String startTime, String endTime) {
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(startTime));
            c2.setTime(df.parse(endTime));
        } catch (ParseException e) {
            System.err.println("格式不正确");
        }
        int result = c1.compareTo(c2);
        if (result >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 大于设定时间 按照yyyy-MM-dd HH:mm 格式
     *
     * @param ltime
     * @return
     */
    public static boolean timeCompare3(long ltime) {
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String curTime = df.format(new Date());
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(curTime));
        } catch (ParseException e) {
            System.err.println("格式不正确");
        }
        long result = c1.getTimeInMillis() - ltime;
        if (result >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String changeMD(String dateStr) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /** */
        /** 将截取到的时间字符串转化为时间格式的字符串 **/
        Date startD;

        try {
            startD = sdf.parse(dateStr);
            str = new SimpleDateFormat("M月d日").format(startD);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;

    }
    public static String changeMD2(String dateStr) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /** */
        /** 将截取到的时间字符串转化为时间格式的字符串 **/
        Date startD;

        try {
            startD = sdf.parse(dateStr);
            str = new SimpleDateFormat("yyyy年MM月dd日 E").format(startD);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;

    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static String divide(String v1, String v2, int scale)

    {

        return divide(v1, v2, scale, BigDecimal.ROUND_HALF_EVEN);

    }

    public static String divide(String v1, String v2, int scale, int round_mode)

    {

        if (scale < 0)

        {

            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        }

        BigDecimal b1 = new BigDecimal(v1);

        BigDecimal b2 = new BigDecimal(v2);

        return b1.divide(b2, scale, round_mode).toString();

    }

    public String getDateTime(String datetime, String format) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDateStr(long datetime) {
        Date d = new Date(datetime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
        return sdf.format(d);
    }

    public static String encoding(String src) {
        if (src == null)
            return "";
        StringBuilder result = new StringBuilder();
        if (src != null) {
            src = src.trim();
            for (int pos = 0; pos < src.length(); pos++) {
                switch (src.charAt(pos)) {
                    case '\"':
                        result.append("");
                        break;
                    case '<':
                        result.append("");
                        break;
                    case '>':
                        result.append("");
                        break;
                    case '\'':
                        result.append("");
                        break;
                    /*
                     * case '&': result.append("&amp;"); break;
					 */
                    case '%':
                        result.append("");
                        break;
                    case '_':
                        result.append("");
                        break;
                    case '#':
                        result.append("");
                        break;
                    case '?':
                        result.append("");
                        break;
                    default:
                        result.append(src.charAt(pos));
                        break;
                }
            }
        }
        return result.toString();
    }

    public static int getwidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static String getSmallBitmap(String filePath, Activity context) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 800, 480);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] bitmapByte = AbImageUtil.bitmap2Bytes(bm, Bitmap.CompressFormat.JPEG, true);
        String mFileName = System.currentTimeMillis() + ".jpg";
        String mFilepath = GetFileSizeUtil.getMainSD(context) + "/dahuochifan/cropimg/" + mFileName;
        AbFileUtil.writeByteArrayToSD(mFilepath, bitmapByte, true);
        return mFilepath;

    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * double如果是整数显示整形如果带小数点，四舍五入
     *
     * @param price
     * @param scale
     * @return
     */
    public static String getDoubleValue(double price, int scale) {
        double priced = price;
        int pricei = (int) priced;
        if (0 == priced - (double) pricei) {
            return pricei + "";
        } else {
            return new BigDecimal(price + "").setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        }
    }

    public static int dp2px(float dp) {
        final float scale = SCREEN_DENSITY;
        return (int) (dp * scale + 0.5f);
    }

    public static int designedDP2px(float designedDp) {
        if (SCREEN_WIDTH_DP != 320) {
            designedDp = designedDp * SCREEN_WIDTH_DP / 320f;
        }
        return dp2px(designedDp);
    }


    public static void init(Context context) {
        if (sInitialed || context == null) {
            return;
        }
        sInitialed = true;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH_PIXELS = dm.widthPixels;
        SCREEN_HEIGHT_PIXELS = dm.heightPixels;
        SCREEN_DENSITY = dm.density;
        SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / dm.density);
        SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / dm.density);
    }
}
