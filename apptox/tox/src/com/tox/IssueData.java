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
 * Created by huJiaYu on 2015/3/24 0024.
 */
public class IssueData extends BaseApi{

    //获取专辑评论Json数据
    public ArrayList<JSONObject> getIssueReplyJson(String url,int id) {

        int count = 1;
        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList2;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList2 = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL+url)+"&page=" + count +"&row_id=" + id);
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
    public JSONArray getIssueDetailJson(String url,int id) {

        String result;
        JSONArray jsonObjFist;
        HttpClient httpClient = new DefaultHttpClient();
        try {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL+url) + "&id=" + id);
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
    //获取Web端json数据
    public ArrayList<JSONObject> getJson(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        String result;
        JSONArray jsonObjFist;
        int count = 1;
        ArrayList<JSONObject> jsonList;
        try {
            jsonList = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL+url)+"&page=" + count);
                count = count + 1;
                HttpResponse httpResponse = httpClient.execute(get);
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObjFist = new JSONObject(result).getJSONArray("list");
                }
                catch (JSONException e)
                {
                    break;
                }
                if (jsonObjFist.length() > 0) {
                    for (int i = 0; i < jsonObjFist.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonObjFist.opt(i);
                        jsonList.add(jsonObject);
                    }
                }
                else {
                    break;
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }
}
