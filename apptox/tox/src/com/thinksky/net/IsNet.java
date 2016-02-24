package com.thinksky.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.tox.Url;

/**
 * 网络判断工具类
 */
public class IsNet {

    private IsNet() {

    }

    public static boolean IsConnect() {
        if (Url.context != null) {
            ConnectivityManager manager = (ConnectivityManager) Url.context
//                .getSystemService(Url.context.CONNECTIVITY_SERVICE);
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            State stata = null;
            if (info != null) {
                stata = info.getState();
                if (stata == State.CONNECTED)
                    return true;
            }
            info = null;
            info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            stata = null;
            if (info != null) {
                stata = info.getState();
                if (stata == State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
