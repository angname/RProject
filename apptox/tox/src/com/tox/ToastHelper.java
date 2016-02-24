package com.tox;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/8/13.
 */
public class ToastHelper {

    private ToastHelper() {

    }

    public static void showToast(String showTxet, Context context) {
        if (context!= null) {
            Toast.makeText(context, showTxet, Toast.LENGTH_LONG).show();
        }
    }
    public static void showToast(String showText,Context context,int imgId){
        Toast toast=Toast.makeText(context,showText,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,25,0);
        LinearLayout toastView=(LinearLayout)toast.getView();
        ImageView imageView=new ImageView(context);
        imageView.setImageResource(imgId);
        imageView.setMinimumWidth(65);
        imageView.setMinimumHeight(65);
        toastView.addView(imageView,0);
        toast.show();
    }
}
