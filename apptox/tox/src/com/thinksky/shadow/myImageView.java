package com.thinksky.shadow;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.content.Context;

/**
 * Created by Administrator on 2014/7/17.
 */
public class myImageView extends ImageView {
    private String namespace = "http://com.com";
    private int color;

    public myImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        color = Color.parseColor("#966925");
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画边框
        Rect rect = canvas.getClipBounds();
        rect.bottom--;
        rect.right--;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect, paint);

    }
}
