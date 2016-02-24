package com.thinksky.thread;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thinksky.net.MyGet;
import com.thinksky.utils.MyJson;

/**
 * 网络Get请求的线程
 */
public class HttpGetThread implements Runnable {

    private Handler hand;
    private String url;
    private MyGet myGet = new MyGet();
    private MyJson myJson = new MyJson();

    public HttpGetThread(Handler hand, String endParamerse) {
        this.hand = hand;
        // 拼接访问服务器完整的地址
        url = endParamerse;
    }

    @Override
    public void run() {
        // 获取我们回调主ui的message
        Message msg = hand.obtainMessage();
        Log.e("liuxiaowei", url);
        try {
            String result = myGet.doGet(url);

            if (myJson.getSuccess(result)) {
                msg.what = 0;
            } else {
                msg.what = myJson.getErrorCode(result);
            }
            msg.obj = result;
        } catch (ClientProtocolException e) {
            msg.what = 404;
        } catch (IOException e) {
            msg.what = 100;
        }
        // 给主ui发送消息传递数据
        hand.sendMessage(msg);
    }
}
