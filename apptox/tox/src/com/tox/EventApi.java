package com.tox;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
public class EventApi extends BaseApi{

    //获取活动回复
    public ArrayList<JSONObject> getEventReplyList(String url,int row_id) {

        int count = 1;
        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList2;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList2 = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url)+"&page=" + count + "&row_id=" + row_id);
                count = count + 1;
                HttpResponse httpResponse = httpClient.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpResponse.getEntity());
                    try {
                        jsonObjFist = new JSONObject(result).getJSONArray("list");
                    }catch (JSONException e)
                    {
                        break;
                    }
                    if (jsonObjFist.length() > 0) {
                        for (int i = 0; i < jsonObjFist.length(); i++) {
                            JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                            jsonList2.add(jsonObject);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            return jsonList2;
        } catch (IOException e) {
            return null;
        }
    }

    //获取活动详情
    public JSONArray getEventDetail(String url,int id,String session_id) {

        String result;
        JSONArray jsonObjFist;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL+url) + "&id=" + id +"&session_id="+session_id);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObjFist = new JSONObject(result).getJSONArray("list");
                }catch (JSONException e)
                {
                    return null;
                }
                return jsonObjFist;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    //获取活动页list Json数据
    public ArrayList<JSONObject> getEventList(String url,int type_id) {

        int count = 1;
        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList2;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList2 = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url)+"&page=" + count + "&type_id=" + type_id + "&width=350&height=300");
                count = count + 1;
                HttpResponse httpResponse = httpClient.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpResponse.getEntity());
                    try {
                        jsonObjFist = new JSONObject(result).getJSONArray("list");
                    }catch (JSONException e)
                    {
                        break;
                    }
                    if (jsonObjFist.length() > 0) {
                        for (int i = 0; i < jsonObjFist.length(); i++) {
                            JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                            jsonList2.add(jsonObject);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            return jsonList2;
        } catch (IOException e) {
            return null;
        }
    }

    //获取活动页Json数据
    public ArrayList<JSONObject> getEventJson(String url) {

        int count = 1;
        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList2;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList2 = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url)+"&page=" + count);
                count = count + 1;
                HttpResponse httpResponse = httpClient.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpResponse.getEntity());
                    try {
                        jsonObjFist = new JSONObject(result).getJSONArray("list");
                    }catch (JSONException e)
                    {
                        break;
                    }
                    if (jsonObjFist.length() > 0) {
                        for (int i = 0; i < jsonObjFist.length(); i++) {
                            JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                            jsonList2.add(jsonObject);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            return jsonList2;
        } catch (IOException e) {
            return null;
        }
    }
}