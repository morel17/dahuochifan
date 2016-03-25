package com.dahuochifan.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.ui.views.RevealBackgroundView;
import com.dahuochifan.utils.PreviewLoader;
import com.dahuochifan.utils.SystemBarTintManager;
import com.dahuochifan.ui.views.photoview.PinchImageView;

public class SimpleViewActivity extends SwipeBackActivity implements RevealBackgroundView.OnStateChangeListener {
    private PinchImageView photo_view;
    private String imgPath;
    private ProgressBar pb;
    private RevealBackgroundView vRevealBackground;
    private int[] startingLocation;
    private PropertyValuesHolder pvhA1;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTopViews();
        setContentView(R.layout.activity_simpleview);
        initData();
        initViews();
        setupRevealBackground(savedInstanceState);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    private void initData() {
        imgPath = getIntent().getStringExtra("imgpath");
        startingLocation = getIntent().getIntArrayExtra("location");
    }

    private void initViews() {
        pvhA1 = PropertyValuesHolder.ofFloat("alpha", 0.2f, 1f);
        photo_view = (PinchImageView) findViewById(R.id.iv_photo);
        photo_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        pb = (ProgressBar) findViewById(R.id.pb);
        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        PreviewLoader.loadImagex(imgPath, photo_view, pb);
    }

    public void initTopViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(ContextCompat.getColor(this, R.color.transparent));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            photo_view.setVisibility(View.VISIBLE);
//			animateUserProfileOptions();
//			animateUserProfileHeader();
//			photo_view.setTranslationY(-photo_view.getHeight());
            ObjectAnimator.ofPropertyValuesHolder(photo_view, pvhA1).setDuration(456).start();
        } else {
            photo_view.setVisibility(View.INVISIBLE);
        }
    }
}
