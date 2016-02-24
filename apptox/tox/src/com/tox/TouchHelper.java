package com.tox;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.thinksky.tox.R;

import java.util.List;

/**
 * Created by Administrator on 2014/8/13.
 */
public class TouchHelper implements View.OnTouchListener {

    private Context context;
    private String first;
    private String second;
    private String type;

    public TouchHelper(Context context, String first, String second, String type) {
        this.context = context;
        this.first = first;
        this.second = second;
        this.type = type;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (type.equals("color")) {
                    v.setBackgroundColor(Color.parseColor(first));
                } else {
                    v.setBackgroundDrawable(context.getResources().getDrawable(Integer.parseInt(first)));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (type.equals("color")) {
                    v.setBackgroundColor(Color.parseColor(second));
                } else {
                    v.setBackgroundDrawable(context.getResources().getDrawable(Integer.parseInt(second)));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (type.equals("color")) {
                    v.setBackgroundColor(Color.parseColor(second));
                } else {
                    v.setBackgroundDrawable(context.getResources().getDrawable(Integer.parseInt(second)));
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (type.equals("color")) {
                    v.setBackgroundColor(Color.parseColor(second));
                } else {
                    v.setBackgroundDrawable(context.getResources().getDrawable(Integer.parseInt(second)));
                }
                break;
            default:
                break;
        }
        return false;
    }
}
