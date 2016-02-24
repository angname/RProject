package com.thinksky.redefine;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2014/7/23.
 */
public class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDoubleTap(MotionEvent event) {
        return false;
    }
}
