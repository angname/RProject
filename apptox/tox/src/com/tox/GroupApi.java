package com.tox;

import android.os.Handler;

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
import java.util.HashMap;

/**
 * Created by Administrator on 2015/5/7 0007.
 */
public class GroupApi extends BaseApi{

    public GroupApi() {
        super();
    }

    public GroupApi(Handler handler) {
        this.handler = handler;
    }
    /**
     * 加入群组
     * @param group_id
     * @return
     */
    public void joinGroupPost(String group_id){
        putArg("group_id",group_id);
        putArg("session_id",Url.SESSIONID);
        execute(handler,Url.getApiUrl(Url.JOINGROUP),true);
    }

    /**
     * 退出群组
     * @param group_id
     * @return
     */
    public void quitGroupPost(String group_id){
        putArg("group_id",group_id);
        putArg("session_id",Url.SESSIONID);
        execute(handler,Url.getApiUrl(Url.QUITGROUP),true);
    }

    /**
     * 帖子点赞
     * @param post_id
     * @return
     */
    public void supportGroupPost(String post_id){
        putArg("post_id",post_id);
        putArg("session_id",Url.SESSIONID);
        execute(handler,Url.getApiUrl(Url.SUPPORT),true);
    }

    /**
     * 帖子回复
     * @param post_id 帖子ID
     * @param content 回复内容
     * @return
     */
    public void postComment(String post_id,String content){
        putArg("post_id",post_id);
        putArg("session_id",Url.SESSIONID);
        putArg("content",content);
        execute(handler,Url.getApiUrl(Url.POSTGROUPCOMMENT),true);
    }

    /**
     * 回复楼中楼
     * @return
     */
    public void replyComment(String post_id,String to_f_reply_id,String content){
        putArg("post_id", post_id);
        putArg("session_id",Url.SESSIONID);
        putArg("to_f_reply_id",to_f_reply_id);
        putArg("content",content);
        execute(handler, Url.getApiUrl(Url.REPLYGROUPCOMMENTS), true);
    }

    /**
     * 解散群组
     * @return
     */
    public void dismissGroup(String id){
        putArg("session_id",Url.SESSIONID);
        putArg("id",id);
        execute(handler,Url.getApiUrl(Url.DISMISSGROUP),true);
    }

    /**
     * 获取群组详细信息
     */
    public void getGroupInfo(String group_id){
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }
        putArg("group_id",group_id);
        execute(handler, Url.getApiUrl(Url.GROUPINFO), true);
    }

    /**
     *群组详细信息json解析
     */
    public HashMap<String, String> getGroupInfoMap(String result,HashMap<String, String> map){
        try{
            JSONObject jsonObjFist = new JSONObject(result).getJSONObject("list");
            map.put("id", jsonObjFist.getString("id"));
            map.put("title", jsonObjFist.getString("title"));
            map.put("uid", jsonObjFist.getString("uid"));
            map.put("post_count", jsonObjFist.getString("post_count"));
            map.put("group_logo", Url.USERHEADURL + jsonObjFist.getString("logo"));
            map.put("group_background", Url.USERHEADURL + jsonObjFist.getString("background"));
            map.put("detail", jsonObjFist.getString("detail"));
            map.put("group_type", jsonObjFist.getString("type"));
            map.put("activity", jsonObjFist.getString("activity"));
            map.put("memberCount", jsonObjFist.getString("menmberCount"));
            map.put("is_join", jsonObjFist.getString("is_join"));
            JSONObject tempObj = jsonObjFist.getJSONObject("user");
            map.put("user_uid", tempObj.getString("uid"));
            map.put("user_username", tempObj.getString("username"));
            map.put("user_nickname", tempObj.getString("nickname"));
            map.put("user_avatar128", Url.USERHEADURL + tempObj.getString("avatar128"));
            return map;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return map;
    }

    //全部群组信息
    public ArrayList<JSONObject> getAllGroupList(String url,int page,int type_id,String session_id) {

        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url)+"&page=" + page + "&type_id=" + type_id + "&width=300&height=300" + "&session_id=" + session_id);
                HttpResponse httpResponse = httpClient.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpResponse.getEntity());
                    try {
                        jsonObjFist = new JSONObject(result).getJSONArray("list");
                        for (int i = 0; i < jsonObjFist.length(); i++) {
                            JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                            jsonList.add(jsonObject);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }
    //我的群组信息
    public ArrayList<JSONObject> getMyGroupList(String url,String session_id) {

        int count = 1;
        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url)+"&page=" + count + "&session_id=" + session_id);
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
                            jsonList.add(jsonObject);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    //群组导航栏
    public ArrayList<JSONObject> getGroupType(String url,int id) {

        int count = 1;
        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
            while (true) {
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url) + "&page=" + count + "&id="+ id);
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
                            jsonList.add(jsonObject);
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    //群组分类导航
    public ArrayList<JSONObject> getGroupCategory(String url,int group_id) {

        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
                HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url) + "&group_id="+ group_id);
                HttpResponse httpResponse = httpClient.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(httpResponse.getEntity());
                    try {
                        jsonObjFist = new JSONObject(result).getJSONArray("list");
                        for (int i = 0; i < jsonObjFist.length(); i++) {
                            JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                            jsonList.add(jsonObject);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    //群组帖子信息
    public ArrayList<JSONObject> getGroupPostList(String url,int page,int group_id,int cateID,String session_id) {

        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
            HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url) + "&page=" + page + "&group_id=" + group_id + "&cate_id=" + cateID + "&session_id=" + session_id);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObjFist = new JSONObject(result).getJSONArray("list");
                    for (int i = 0; i < jsonObjFist.length(); i++) {
                        JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                        jsonList.add(jsonObject);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    //获取群组帖子回复数据
    public ArrayList<JSONObject> getPostReply(String url,int post_id,int page) {

        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
            HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url) + "&post_id="+ post_id + "&page=" + page);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObjFist = new JSONObject(result).getJSONArray("list");
                    for (int i = 0; i < jsonObjFist.length(); i++) {
                        JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                        jsonList.add(jsonObject);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    //获取帖子评论数
    public JSONObject getPostCount(String url,int post_id) {

        String result;
        JSONObject jsonObject;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url) + "&id="+ post_id);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObject = new JSONObject(result);
                    return jsonObject;
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    //获取群组帖子回复数据
    public ArrayList<JSONObject> getLzlReply(String url, int post_id, int to_f_reply_id, int page) {

        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
            HttpGet get = new HttpGet(String.valueOf(Url.HTTPURL + url) + "&post_id="+ post_id + "&to_f_reply_id=" + to_f_reply_id + "&page=" + page);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObjFist = new JSONObject(result).getJSONArray("list");
                    for (int i = 0; i < jsonObjFist.length(); i++) {
                        JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                        jsonList.add(jsonObject);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    //获取公告信息
    public JSONObject getGroupNotice(String url,String group_id) {

        String result;
        JSONObject jsonObject;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(url + "&group_id="+ group_id);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObject = new JSONObject(result);
                    return jsonObject;
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    //热帖获取函数
    public ArrayList<JSONObject> getHotPost(String url,String group_id,String session_id) {

        String result;
        JSONArray jsonObjFist;
        ArrayList<JSONObject> jsonList;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            jsonList = new ArrayList<JSONObject>();
            HttpGet get = new HttpGet(url + "&group_id="+ group_id + "&session_id=" + session_id);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
                try {
                    jsonObjFist = new JSONObject(result).getJSONArray("list");
                    for (int i = 0; i < jsonObjFist.length(); i++) {
                        JSONObject jsonObject = (JSONObject)jsonObjFist.opt(i);
                        jsonList.add(jsonObject);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }
}
