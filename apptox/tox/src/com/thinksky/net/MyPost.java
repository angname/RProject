package com.thinksky.net;

import android.util.Log;

import com.tox.Url;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 我的post请求方式工具类
 */

public class MyPost {


    public String doPost(String url, String img, String value) {
        String result = null;
        HttpResponse httpResponse = null;
        HttpPost post = new HttpPost(Url.HTTPURL + url);
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
                30000); // 超时设置
        client.getParams().setIntParameter(
                HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // Json字符串拼接
        nameValuePairs.add(new BasicNameValuePair("value", value));
        nameValuePairs.add(new BasicNameValuePair("img", img));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            httpResponse = client.execute(post);
            Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils
                        .toString(httpResponse.getEntity(), "utf-8");
                Log.e("HTTP", "result:" + result);
            } else {
                result = null;
            }
        } catch (UnsupportedEncodingException e) {
            result = null;
        } catch (ClientProtocolException e) {
            result = null;
        } catch (IOException e) {
            result = null;
        }
        return result;
    }

    public String doPost(String url, String value) {
        String result = null;
        HttpResponse httpResponse = null;
        HttpPost post = new HttpPost(Url.HTTPURL + url);
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
                30000); // 超时设置
        client.getParams().setIntParameter(
                HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // Json字符串拼接
        nameValuePairs.add(new BasicNameValuePair("value", value));
        //测试代码
       /* nameValuePairs.add(new BasicNameValuePair("username","小泽玛丽罗"));
        nameValuePairs.add(new BasicNameValuePair("password","gqb101112"));*/
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            httpResponse = client.execute(post);
            Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils
                        .toString(httpResponse.getEntity(), "utf-8");
                Log.e("HTTP", "result:" + result);
            } else {
                result = null;
            }
        } catch (UnsupportedEncodingException e) {
            result = null;
        } catch (ClientProtocolException e) {
            result = null;
        } catch (IOException e) {
            result = null;
        }
        return result;
    }

    public String doPost(String url, Map mapValue) {
        String result = null;
        HttpResponse httpResponse = null;
        HttpPost post = new HttpPost(url);
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
                30000); // 超时设置
        client.getParams().setIntParameter(
                HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // Json字符串拼接
        //nameValuePairs.add(new BasicNameValuePair("value", value));

        if (mapValue != null) {
            try {
                Set keys = mapValue.entrySet();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Entry) it.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    Log.e(">>>>>>>>>>>>>>>>" + name, value);
                    nameValuePairs.add(new BasicNameValuePair(name, value));
                }
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        //测试代码
       /* nameValuePairs.add(new BasicNameValuePair("username","小泽玛丽罗"));
        nameValuePairs.add(new BasicNameValuePair("password","gqb101112"));*/
        try {
            httpResponse = client.execute(post);
            // Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils
                        .toString(httpResponse.getEntity(), "utf-8");
//                Log.e("HTTP>>>>>>>>>>>>>>", "result:" + result);
            } else {
                result = null;
            }
        } catch (UnsupportedEncodingException e) {
            result = null;
        } catch (ClientProtocolException e) {
            result = null;
        } catch (IOException e) {
            result = null;
        }
        return result;
    }
}
