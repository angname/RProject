package com.tox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.thinksky.net.IsNet;
import com.thinksky.net.ThreadPoolUtils;
import com.thinksky.thread.HttpPostThread;
import com.thinksky.tox.UserInfoActivity;
import com.thinksky.utils.MyJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Api基类
 */
public class BaseApi {
    Map<String, String> mMap = new HashMap<String, String>();
    protected Handler handler;

    public BaseApi() {
        initApiMap();
    }
    /**
     *查看用户资料
     */
    public void goUserInfo(Context mContext,String userUid){
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("userUid",userUid);
        mContext.startActivity(intent);
    }

    /**
     * 初始化Api参数Map
     */
    protected void initApiMap() {
        putArg("session_id", getSeesionId());
    }

    /**
     * 获取到当前的SessionID
     *
     * @return
     */
    public String getSeesionId() {
        return Url.SESSIONID;
    }
    /**
     * 获取到当前的UID
     *
     * @return
     */
    public String getUid() {
        return Url.USERID;
    }

    /**
     * 放置参数
     *
     * @param key
     * @param value
     */
    protected void putArg(String key, String value) {
        mMap.put(key, value);
    }

    protected void execute(final Handler handler, final String url, boolean isNeed2JudeSessionID) {
        if (IsNet.IsConnect()) {
            if (isNeed2JudeSessionID) {
                if (isSessionIdExpired()) {
                    Map<String, String> mapValue = new HashMap<String, String>();
                    mapValue.put("username", Url.sp.getString("username", ""));
                    mapValue.put("password", Url.sp.getString("password", ""));
                    String loginUrl = Url.getApiUrl(Url.LOGIN);
                    ThreadPoolUtils.execute(new HttpPostThread(new Handler() {
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject((String) msg.obj);
                                    Url.VERSION = jsonObject.getString("version");
                                    Url.WEIBOWORDS =Integer.parseInt(jsonObject.getString("weibo_words_limit")) ;
                                } catch (JSONException E) {

                                }
                                Url.SESSIONID = new MyJson().getUserSessionID((String) msg.obj);
                                Url.LASTPOSTTIME = System.currentTimeMillis();
                                ThreadPoolUtils.execute(new HttpPostThread(handler, url, mMap));
                            } else {
                            }
                        }
                    }, loginUrl, mapValue));
                } else {
                    ThreadPoolUtils.execute(new HttpPostThread(handler, url, mMap));
                }
            } else {
                ThreadPoolUtils.execute(new HttpPostThread(handler, url, mMap));
            }
        } else {
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }

    }

    /**
     * 设置handler
     *
     * @param handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean isSessionIdExpired() {
        if (Url.LASTPOSTTIME == 0) {
            return false;
        }
        Log.e("session_id过期，开始自动登入", "");
        long timeNow = System.currentTimeMillis();
        long gap = (timeNow - Url.LASTPOSTTIME) / 1000;
        return gap > 400;
    }

    /**
     * 文本框自动获取输入焦点并自动打开软键盘
     */
    public void openKeyBoard(Context context, EditText editText){
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 存储用户信息
     */
    public void saveUserInfoToNative(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", Url.MYUSERINFO.getNickname());
        editor.putString("avatar", Url.MYUSERINFO.getAvatar());
        Log.e("我要保存", Url.MYUSERINFO.getAvatar());
        editor.apply();
    }

    /**
     * 判断url为远程地址或者本地地址，并进行相应组装
     */
    public String getResourcesURL(String tempURL) {
        if (!tempURL.contains("http://")) {
            return Url.USERHEADURL +tempURL;
        }
        return tempURL;
    }
}
