package com.dahuochifan.ui.views;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Morel on 2015/12/4.
 * 可拖动的iamgeView
 */
public class DragImageView extends ImageView implements View.OnTouchListener {
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private float initialX,initialY;
    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.setScaleType(ScaleType.MATRIX);
        ImageView view=(ImageView)v;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                matrix.set(getImageMatrix());
                savedMatrix.set(matrix);
                initialX=event.getX();
                initialY=event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX()-initialX,event.getY()-initialY);
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }
}
