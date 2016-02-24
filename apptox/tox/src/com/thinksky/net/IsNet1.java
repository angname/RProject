package com.thinksky.net;

/**
 * Created by jiao on 2016/2/24.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tox.Url;

/**
 * 网络判断工具类
 */
public class IsNet1 {

    private IsNet1() {

    }

    public static boolean IsConnect() {
        return IsConnect(Url.context);
    }

    public static boolean IsConnect(Context context) {
        if (context!= null) {
            ConnectivityManager manager = (ConnectivityManager) context
//                .getSystemService(Url.context.CONNECTIVITY_SERVICE);
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo.State stata = null;
            if (info != null) {
                stata = info.getState();
                if (stata == NetworkInfo.State.CONNECTED)
                    return true;
            }
            info = null;
            info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            stata = null;
            if (info != null) {
                stata = info.getState();
                if (stata == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}

