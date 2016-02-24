package com.thinksky.thread;

import android.os.Handler;
import android.os.Message;

import com.thinksky.net.MyPost;
import com.thinksky.utils.MyJson;

import java.util.Map;

/**
 * 网络Post请求的线程
 */

public class HttpPostThread implements Runnable {

    private Handler hand;
    private String url;
    private String value = "0";
    private String img = "";
    private MyPost myGet = new MyPost();
    private Map<String, String> mapValue = null;
    private MyJson myJson = new MyJson();
    private Boolean Isauto = false;

    public HttpPostThread(Handler hand, String endParamerse, String value,
                          String img) {
        this.hand = hand;
        // 拼接访问服务器完整的地址
        url = endParamerse;
        this.value = value;
        this.img = img;
    }

    public HttpPostThread(Handler hand, String endParamerse, String value) {
        this.hand = hand;
        // 拼接访问服务器完整的地址
        url = endParamerse;
        this.value = value;
    }

    public HttpPostThread(Handler hand, String url, Map value) {
        this.hand = hand;
        this.url = url;
        this.mapValue = value;
    }

    public HttpPostThread(Handler hand, String url) {
        this.hand = hand;
        this.url = url;
        this.mapValue = null;
    }


    public HttpPostThread(Boolean autoLogin, Map mapValue) {
        this.Isauto = autoLogin;
        this.mapValue = mapValue;
    }

    @Override
    public void run() {
        // 获取我们回调主ui的message
        Message msg = hand.obtainMessage();
        String result = null;
        if (value != "0") {
//            Log.e("STRATWEIBO>>>>>>>>>1", "value");
            result = myGet.doPost(url, value);
        } else {
//            Log.e("STRATWEIBO>>>>>>>>>1", "mapValue");
            result = myGet.doPost(url, mapValue);
        }
        if (myJson.isJSON(result)) {
            //因为接口返回不统一，在success为true时设置msg.what为1,在success为false 时 msg的值为ErrorCode的值
            if (myJson.getSuccess(result)) {
                msg.what = 0;
            } else {
                msg.what = myJson.getErrorCode(result);
            }
        } else {
            msg.what = 404;

        }
        msg.obj = result;
        // 给主ui发送消息传递数据
        hand.sendMessage(msg);
    }
}
