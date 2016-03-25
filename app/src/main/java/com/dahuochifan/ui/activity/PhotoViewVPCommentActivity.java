package com.dahuochifan.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dahuochifan.R;
import com.dahuochifan.utils.PreviewLoader;
import com.dahuochifan.ui.views.photoview.PinchImageView;
import com.dahuochifan.ui.views.photoview.PinchImageViewPager;

import java.util.ArrayList;
import java.util.LinkedList;


public class PhotoViewVPCommentActivity extends BaseActivity {
    private PinchImageViewPager viewPager;
    private ArrayList<String> imgList;
    private ProgressBar pb;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        Intent intent=getIntent();
        imgList=intent.getStringArrayListExtra("imglist");
        index=intent.getIntExtra("index",0);
    }

    private void initView() {
        viewPager=(PinchImageViewPager)findViewById(R.id.view_pager);
        pb=(ProgressBar)findViewById(R.id.pb);
        viewPager.setAdapter(new SamplePagerAdapter());
        viewPager.setCurrentItem(index);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_photo_view_vp;
    }


    @Override
    protected String initToolbarTitle() {
        return "查看图片";
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
    class SamplePagerAdapter extends PagerAdapter {

        final LinkedList<PinchImageView> viewCache = new LinkedList<PinchImageView>();

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PinchImageView piv;
            if (viewCache.size() > 0) {
                piv = viewCache.remove();
                piv.reset();
            } else {
                piv = new PinchImageView(PhotoViewVPCommentActivity.this);
            }
            PreviewLoader.loadImagex("file://" +imgList.get(position), piv, pb);
            container.addView(piv);
            return piv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            PinchImageView piv = (PinchImageView) object;
            container.removeView(piv);
            viewCache.add(piv);
        }
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            viewPager.setMainPinchImageView((PinchImageView) object);
        }
    }
}
