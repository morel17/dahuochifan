package com.dahuochifan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.dahuochifan.R;
import com.dahuochifan.utils.AbFileUtil;
import com.dahuochifan.utils.AbImageUtil;
import com.dahuochifan.utils.GetFileSizeUtil;
import com.dahuochifan.ui.views.ClipView;
import com.dahuochifan.ui.views.ClipView.OnDrawListenerComplete;

public class ClipPictureActivity extends AppCompatActivity implements OnTouchListener, OnClickListener {
    private ImageView srcPic;
    private ClipView clipview;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;
    private String mPath = "CropImageActivity";
    private String type;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTitle("");
        } else {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clippicture);
        mPath = getIntent().getStringExtra("PATH");
        type = getIntent().getStringExtra("type");
        srcPic = (ImageView) this.findViewById(R.id.src_pic);
        srcPic.setOnTouchListener(this);

        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(srcPic.getTop());
            }
        });
        View sure = findViewById(R.id.sure);
        sure.setOnClickListener(this);
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top top
     */
    private void initClipView(int top) {
        /*
		 * bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pic);
		 */
        Log.e("path", mPath);
        bitmap = BitmapFactory.decodeFile(mPath, getBitmapOption(2));
        clipview = new ClipView(ClipPictureActivity.this);
        clipview.setCustomTopBarHeight(top);
        clipview.addOnDrawCompleteListener(new OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                // int clipHeight = clipview.getClipHeight();
                // int clipWidth = clipview.getClipWidth();
                int clipHeight = ClipPictureActivity.this.getResources().getDimensionPixelSize(R.dimen.height_60_80);
                int clipWidth = ClipPictureActivity.this.getResources().getDimensionPixelSize(R.dimen.width_60_80);
                int midX = ClipPictureActivity.this.getResources().getDimensionPixelSize(R.dimen.height_22_80);
                int midY = ClipPictureActivity.this.getResources().getDimensionPixelSize(R.dimen.height_40_80);
                if (bitmap != null) {
                    int imageWidth = bitmap.getWidth();
                    int imageHeight = bitmap.getHeight();
                    // 按裁剪框求缩放比例
                    float scale = (clipWidth * 1.0f) / imageWidth;
                    if (imageWidth > imageHeight) {
                        scale = (clipHeight * 1.0f) / imageHeight;
                    }

                    // 起始中心点
                    float imageMidX = imageWidth * scale / 2;
                    float imageMidY = clipview.getCustomTopBarHeight() + imageHeight * scale / 2;
                    srcPic.setScaleType(ScaleType.MATRIX);

                    // 缩放
                    matrix.postScale(scale, scale);
                    // 平移
                    matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                    srcPic.setImageMatrix(matrix);
                    srcPic.setImageBitmap(bitmap);
                } else {
                    finish();
                }
            }
        });

        this.addContentView(clipview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private Options getBitmapOption(int inSampleSize, String mPath) {
        System.gc();
        Options options = new Options();
//		options.inPurgeable = true;
        Bitmap inBitmap = BitmapFactory.decodeFile(mPath);
        options.inBitmap = inBitmap;
        options.inSampleSize = inSampleSize;
        options.inMutable = true;
        return options;
    }

    private Options getBitmapOption(int inSampleSize) {
        System.gc();
        Options options = new Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        options.inMutable = true;
        return options;
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event event
     * @return float
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point point
     * @param event event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void onClick(View v) {
        Bitmap clipBitmap = getBitmap();
        byte[] bitmapByte = AbImageUtil.bitmap2Bytes(clipBitmap, Bitmap.CompressFormat.JPEG, true);
        Intent intent = new Intent();
        String mFileName = System.currentTimeMillis() + ".jpg";
        String mFilepath = GetFileSizeUtil.getMainSD(context) + "/dahuochifan/cropimg/" + mFileName;
        AbFileUtil.writeByteArrayToSD(mFilepath, bitmapByte, true);
        intent.putExtra("PATH", mFilepath);
        intent.putExtra("NAME", mFileName);
        intent.putExtra("type", type);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 获取裁剪框内截图
     *
     * @return bitmap
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(), clipview.getClipLeftMargin(), clipview.getClipTopMargin() + statusBarHeight,
                clipview.getClipWidth(), clipview.getClipHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap = null;
        }
    }
}