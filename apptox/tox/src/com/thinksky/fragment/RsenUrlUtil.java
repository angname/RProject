package com.thinksky.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thinksky.net.IsNet1;
import com.thinksky.net.ThreadPoolUtils;
import com.thinksky.thread.HttpPostThread;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiao on 2016/2/17.
 */
public class RsenUrlUtil {

    public static String URL_BASE = "http://192.168.15.36:8081";

    //百科服务器地址
    public static String URL_BK = URL_BASE + "/opensns/api.php?s=Paper/getCategory";
    //百科二级菜单
    public static String URL_BKT = URL_BASE + "/opensns/api.phps=Paper/getPaperAll&category=";
    //专家
    public static String URL_ZJ = URL_BASE + "/opensns/api.php?s=/Issue/getIssList&session_id=&issue_id=";
    //问答
    public static String URL_WD = URL_BASE + "/opensns/api.php?s=Question/getQuestionList";
    //论坛
    public static String URL_LT = URL_BASE + "/opensns/api.php?s=/forum/getForumModules&";

    public static void execute(final OnHttpResultListener listener) {
        execute(URL_BK, listener);
    }


    public static void execute(final String url, final OnHttpResultListener listener) {
        execute(Url.context, url, listener);
    }
    public static void execute(Context context, final String url, final OnHttpResultListener listener) {
        if (IsNet1.IsConnect(context)) {
            ThreadPoolUtils.execute(new HttpPostThread(new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        String data = (String) msg.obj;
                        try {
                            Log.e(RsenUrlUtil.class.getSimpleName(), "url-->" + url);
                            Log.e(RsenUrlUtil.class.getSimpleName(), "data-->" + data);
                            JSONObject jsonObject = new JSONObject(data);
                            if (listener != null) {
                                listener.onResult(true, data, jsonObject);
                            }
                        } catch (JSONException E) {
                            if (listener != null) {
                                listener.onResult(false, data, null);
                            }
                        }
                    }
                }
            }, url));
        } else {
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }
    }

    public interface OnHttpResultListener {
        /**
         * state: 请求是否成功
         * jsonObject: 请求成功时，返回的json对象数据
         */
        void onResult(boolean state, String result, JSONObject jsonObject);
    }
}
