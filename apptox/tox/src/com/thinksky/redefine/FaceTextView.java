package com.thinksky.redefine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.thinksky.utils.EmojiUtils;

/**
 * Created by Administrator on 2014/8/21.
 */
public class FaceTextView extends TextView {
    private Context context;

    public FaceTextView(Context context){
        super(context);
        this.context=context;
    }

    public FaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setFaceText(String text) {
        text= EmojiUtils.convertTag(text);
        CharSequence spanned = Html.fromHtml(text, imageGetter, null);
        setText(spanned);
    }

    private Html.ImageGetter imageGetter = new Html.ImageGetter()
    {
        public Drawable getDrawable(String source){

            int id = getResources().getIdentifier(source, "drawable", context.getPackageName());

            Drawable emoji = getResources().getDrawable(id);
            int w = emoji.getIntrinsicWidth() ;
            int h = emoji.getIntrinsicHeight();
            emoji.setBounds(0, 0, w, h);
            return emoji;
        }
    };
}
