package com.thinksky.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.thinksky.info.AshamedInfo;
import com.thinksky.info.CheckInfo;
import com.thinksky.info.Com2Com;
import com.thinksky.info.ErrorInfo;
import com.thinksky.info.ForumInfo;
import com.thinksky.info.ImageInfo;
import com.thinksky.info.NewsCategory;
import com.thinksky.info.NewsDetailInfo;
import com.thinksky.info.NewsListInfo;
import com.thinksky.info.NewsReplyInfo;
import com.thinksky.info.PostComment;
import com.thinksky.info.PostInfo;
import com.thinksky.info.UserInfo;
import com.thinksky.info.WeiboCommentInfo;
import com.thinksky.info.WeiboInfo;
import com.tox.BaseApi;
import com.tox.BaseFunction;
import com.tox.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Json字符串解析工具类
 *
 * @author 苦涩
 */

public class MyJson {

    BaseApi baseApi = new BaseApi();
    public String getWeiboWordLimits(String value){
        String wordLimit="";
        try{
            JSONObject jsonObject= new JSONObject(value);
            wordLimit=jsonObject.getString("weibo_words_limit");
        }
        catch (JSONException e){
            return "140";
        }
       return wordLimit;
    }
    public WeiboCommentInfo getWeiboComAfterCom(String value){
        WeiboCommentInfo weiboCommentInfo=new WeiboCommentInfo();
        try {
            JSONObject jsonObject=new JSONObject(value);
            //v1中的解析方法
//            JSONObject comObject=jsonObject.getJSONObject("comment");
            JSONObject comObject=jsonObject.getJSONObject("0");
            weiboCommentInfo.setId(comObject.getString("id"));
            weiboCommentInfo.setContent(comObject.getString("content"));
            weiboCommentInfo.setCtime(getStandardDate(comObject.getString("create_time")));
            UserInfo userInfo = getUserInfo(comObject.getJSONObject("user"));
            weiboCommentInfo.setUser(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  weiboCommentInfo;

    }
    public WeiboInfo getWeiboAfterRepost(String value){
        WeiboInfo weiboInfo = new WeiboInfo();
        try {
            JSONObject jsonObject=new JSONObject(value);
            JSONObject weiboObject=jsonObject.getJSONObject("detail");
//            JSONObject weiboObject=detailObject.getJSONObject("weibo");
            weiboInfo.setWid(weiboObject.getString("id"));
            weiboInfo.setComment_count(weiboObject.getString("comment_count"));
            weiboInfo.setCtime(getStandardDate(weiboObject.getString("create_time")));
            weiboInfo.setWcontent(weiboObject.getString("content"));
            weiboInfo.setRepost_count(weiboObject.getString("repost_count"));
            weiboInfo.setIs_top(Integer.parseInt(weiboObject.getString("is_top")));
            weiboInfo.setFrom(weiboObject.getString("from"));
            weiboInfo.setLikenum(weiboObject.getString("support_count"));
            UserInfo user = getUserInfo(weiboObject.getJSONObject("user"));
            weiboInfo.setUser(user);

            weiboInfo.setType(weiboObject.getString("type"));
            //Log.e("WEIBOTYPE>>>>>><<<<<<<<<<<<<<",listitem.getString("type"));
            if (weiboObject.getString("type").equals("image")) {
                weiboInfo.setImgList(getImgUrlList(weiboObject.getJSONArray("images")));
            } else {
                weiboInfo.setImgList(new ArrayList<String>());
            }
            if (BaseFunction.isLogin()) {
                weiboInfo.setIs_supported(weiboObject.getString("is_supported").equals("1") ? true : false);
            }
            if (weiboObject.getString("type").equals("repost")) {
                //TODO 转发微博JSON解析

                WeiboInfo repost = getRepostWeiboInfo(weiboObject.getString("data"));
                weiboInfo.setRepostWeiboInfo(repost);
                // Log.e("转发微博" + weiboInfo.getWid(), repost.getWcontent());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weiboInfo;
    }

    public WeiboInfo getWeiboAfterSend(String value){
        WeiboInfo weiboInfo = new WeiboInfo();
        try {
            JSONObject jsonObject=new JSONObject(value);
            JSONObject weiboObject=jsonObject.getJSONObject("detail");

            weiboInfo.setWid(weiboObject.getString("id"));
            weiboInfo.setComment_count(weiboObject.getString("comment_count"));
            weiboInfo.setCtime(getStandardDate(weiboObject.getString("create_time")));
            weiboInfo.setWcontent(weiboObject.getString("content"));
            weiboInfo.setRepost_count(weiboObject.getString("repost_count"));
            weiboInfo.setIs_top(Integer.parseInt(weiboObject.getString("is_top")));
            weiboInfo.setFrom(weiboObject.getString("from"));
            weiboInfo.setLikenum(weiboObject.getString("support_count"));
            UserInfo user = getUserInfo(weiboObject.getJSONObject("user"));
            weiboInfo.setCan_delete(weiboObject.getBoolean("can_delete"));
            weiboInfo.setUser(user);

            weiboInfo.setType(weiboObject.getString("type"));
            //Log.e("WEIBOTYPE>>>>>><<<<<<<<<<<<<<",listitem.getString("type"));
            if (weiboObject.getString("type").equals("image")) {
                weiboInfo.setImgList(getImgUrlList(weiboObject.getJSONArray("images")));
            } else {
                weiboInfo.setImgList(new ArrayList<String>());
            }
            if (BaseFunction.isLogin()) {
                weiboInfo.setIs_supported(weiboObject.getString("is_supported").equals("1") ? true : false);
            }
            if (weiboObject.getString("type").equals("repost")) {
                //TODO 转发微博JSON解析

                WeiboInfo repost = getRepostWeiboInfo(weiboObject.getString("data"));
                weiboInfo.setRepostWeiboInfo(repost);
                // Log.e("转发微博" + weiboInfo.getWid(), repost.getWcontent());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weiboInfo;
    }
    /**
     *
     * @param value
     * @return
     */
    public boolean isJSON(String value) {
        if (value != null) {
            value = value.trim();
            if (value.startsWith("{") && value.endsWith("}")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * 图片上传后从结果中获取图片的attach_id
     *
     * @param obj
     * @return
     */
    public String getAttachId(Object obj) {
        String img_attachId = "";
        String result = obj.toString();
        try {
            JSONObject jsonObject = new JSONObject(result);
            img_attachId = jsonObject.getString("id");

        } catch (JSONException e) {

        }
        return img_attachId;
    }

    // 解析糗事方法
    public List<AshamedInfo> getAshamedList(String value) {
        List<AshamedInfo> list = null;
        try {
            JSONArray jay = new JSONArray(value);
            list = new ArrayList<AshamedInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                AshamedInfo info = new AshamedInfo();
                info.setQid(job.getString("QID"));
                info.setUid(job.getString("UID"));
                info.setTid(job.getString("TID"));
                info.setQimg(job.getString("QIMG"));
                info.setQvalue(job.getString("QVALUE"));
                info.setQlike(job.getString("QLIKE"));
                info.setQunlike(job.getString("QUNLIKE"));
                info.setQshare(job.getString("QSHARE"));
                info.setUname(job.getString("UNAME"));
                info.setUhead(job.getString("UHEAD"));
                list.add(info);
            }
        } catch (JSONException e) {
        }
        return list;
    }

    public List<WeiboInfo> getWeiboList(String value) {
        List<WeiboInfo> list = null;
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            list = new ArrayList<WeiboInfo>();
            // Log.e("LIST SIZE>>>>>>>>>>>>>>>>>>",jsonArray.length()+"");
            for (int i = 0; i < jsonArray.length(); i++) {
                WeiboInfo weiboInfo = new WeiboInfo();
                JSONObject listitem = (JSONObject) jsonArray.opt(i);
                try {
                    weiboInfo.setWid(listitem.getString("id"));
                    weiboInfo.setComment_count(listitem.getString("comment_count"));
                    weiboInfo.setCtime(getStandardDate(listitem.getString("create_time")));
                    weiboInfo.setWcontent(listitem.getString("content"));
                    weiboInfo.setRepost_count(listitem.getString("repost_count"));
                    weiboInfo.setIs_top(Integer.parseInt(listitem.getString("is_top")));
                    weiboInfo.setFrom(listitem.getString("from"));
                    weiboInfo.setLikenum(listitem.getString("support_count"));
                    weiboInfo.setCan_delete(listitem.getBoolean("can_delete"));
                    UserInfo user = getUserInfo(listitem.getJSONObject("user"));
                    weiboInfo.setUser(user);

                    weiboInfo.setType(listitem.getString("type"));
                    //Log.e("WEIBOTYPE>>>>>><<<<<<<<<<<<<<",listitem.getString("type"));
                    if (listitem.getString("type").equals("image")) {
                        weiboInfo.setImgList(getImgUrlList(listitem.getJSONArray("images")));
                    } else {
                        weiboInfo.setImgList(new ArrayList<String>());
                    }
                    if (BaseFunction.isLogin()) {
                        weiboInfo.setIs_supported(listitem.getString("is_supported").equals("1"));
                    }
                    if (listitem.getString("type").equals("repost")) {
                        //TODO 转发微博JSON解析

                        WeiboInfo repost = getRepostWeiboInfo(listitem.getString("data"));
                        weiboInfo.setRepostWeiboInfo(repost);
                        // Log.e("转发微博" + weiboInfo.getWid(), repost.getWcontent());
                    }

                    //TODO 虾米音乐解析
                    if (listitem.getString("type").equals("xiami")) {
                        Log.d("虾米音乐", "这是一条虾米音乐的微博");

                    }
                }catch (JSONException e){
                    Log.e("WeiboListJSON解析出错", ">>>>>>>>>>>>>>>>>>>>");
                    continue;
                }

                //TODO
                list.add(weiboInfo);
            }
        } catch (JSONException e) {
            Log.e("WeiboListJSON解析出错", ">>>>>>>>>>>>>>>>>>>>");
        }
        return list;
    }

    public WeiboInfo getRepostWeiboInfo(String value) {
        //TODO 转发微博JSON解析
        WeiboInfo repostWeibo = new WeiboInfo();
        try {

            JSONObject jsonObject = new JSONObject(value);

            JSONObject jsonSourse = new JSONObject(jsonObject.getString("sourse"));
            repostWeibo.setWid(jsonSourse.getString("id"));
            //weiboInfo.setLikenum(jsonObject.getString(""));

            repostWeibo.setComment_count(jsonSourse.getString("comment_count"));
            repostWeibo.setCtime(getStandardDate(jsonSourse.getString("create_time")));
            repostWeibo.setRepost_count(jsonSourse.getString("repost_count"));
            repostWeibo.setWcontent(jsonSourse.getString("content"));

            if (jsonSourse.getString("user").equals("null")){
                UserInfo userInfo=new UserInfo();
                userInfo.setUid(null);
                userInfo.setAvatar(null);
                userInfo.setNickname(null);
                repostWeibo.setUser(userInfo);
            }else {

                UserInfo userInfo = getUserInfo(jsonSourse.getJSONObject("user"));
                repostWeibo.setUser(userInfo);
                repostWeibo.setType(jsonSourse.getString("type"));

                if (jsonSourse.getString("type").equals("image")) {
                    List<String> imgList = getImgUrlList(jsonSourse.getJSONArray("images"));
                    repostWeibo.setImgList(imgList);
                }
            }


        } catch (JSONException E) {

        }
        return repostWeibo;

    }

    public List<String> getImgUrlList(JSONArray jsonArray) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add((String) jsonArray.opt(i));
        }
        return list;
    }
    public List<String> getImgUrlList(com.alibaba.fastjson.JSONArray jsonArray) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add((String) jsonArray.get(i));
        }
        return list;
    }

    /**
     * @param value Json格式的String 字符串
     * @return
     * @discription 解析微博评论的方法
     */
    public List<WeiboCommentInfo> getWeiboCommentsList(String value) {
        List<WeiboCommentInfo> list = new ArrayList<WeiboCommentInfo>();
        try {
            JSONObject jsonObject = new JSONObject(value);
            //获得评论的列表
            JSONArray jsonArray = jsonObject.getJSONArray("list");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
                WeiboCommentInfo weiboCommentInfo = new WeiboCommentInfo();
                weiboCommentInfo.setId(jsonObj.getString("id"));
                weiboCommentInfo.setContent(jsonObj.getString("content"));
                weiboCommentInfo.setCtime(getStandardDate(jsonObj.getString("create_time")));
                if (jsonObj.getString("user").equals("null")){
                    UserInfo userInfo= new UserInfo();
                    userInfo.setUid(null);
                    userInfo.setAvatar(null);
                    userInfo.setNickname(null);
                    weiboCommentInfo.setUser(userInfo);
                    list.add(weiboCommentInfo);
                }else {
                    UserInfo userInfo = getUserInfo(jsonObj.getJSONObject("user"));
                    weiboCommentInfo.setUser(userInfo);
                    list.add(weiboCommentInfo);
                }
            }
            //Log.e("<<<<<<<<<<<<<<<<<<<<<<<<<<",list.toString());
        } catch (JSONException e) {

        }
        return list;
    }

    /**
     * @param value
     * @return
     */
    public String getUserID(String value) {
        String userID = "";
        try {
            JSONObject jsonObject = new JSONObject(value);
            userID = jsonObject.getString("uid");

        } catch (JSONException e) {

        }
        return userID;
    }

    public String getUserSessionID(String value) {
        String session_id = "";
        try {
            JSONObject jsonObject = new JSONObject(value);
            session_id = jsonObject.getString("session_id");
        } catch (JSONException e) {

        }
        return session_id;
    }

    public boolean getSuccess(String value) {

        try {
            JSONObject jsonObject = new JSONObject(value);
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                return false;
            }

        } catch (JSONException e) {

        }
        return true;
    }

    public int getErrorCode(String value) {
        int errorCode = -1;
        try {
            JSONObject jsonObject = new JSONObject(value);
            errorCode = Integer.parseInt(jsonObject.getString("error_code"));
        } catch (JSONException e) {

        }
        return errorCode;
    }




    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(String timeStr) {

        Calendar cd = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日 HH:mm");
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        StringBuffer time = new StringBuffer();
        long t = Long.parseLong(timeStr);
        long timeGap = System.currentTimeMillis() - (t * 1000);
        long mill = (long) Math.ceil(timeGap / 1000);//秒前

        long minute = (long) Math.ceil(timeGap / 60 / 1000.0f);// 分钟前
        long hour = (long) Math.ceil(timeGap / 60 / 60 / 1000.0f);// 小时
        long day = (long) Math.ceil(timeGap / 24 / 60 / 60 / 1000.0f);// 天前
        if (day - 1 > 0) {
            time.append(format1.format(new Date(Long.parseLong(timeStr) * 1000)));

        } else if (hour - 1 > 0) {
            if (hour >= cd.get(Calendar.HOUR_OF_DAY)) {
                time.append(format1.format(new Date(Long.parseLong(timeStr) * 1000)));
            } else {
                time.append("今天" + format2.format(new Date(Long.parseLong(timeStr) * 1000)));
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                time.append("1小时前");
            } else {
                time.append(minute + "分钟前");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                time.append("1分钟前");
            } else {
    time.append(mill + "秒前");
}
} else {
        time.append("刚刚");
        }
        return time.toString();
        }


    public UserInfo getUserInfo(JSONObject user) {
            UserInfo userInfo = new UserInfo();
            try {
            userInfo.setUid(user.getString("uid"));
            userInfo.setAvatar(Url.USERHEADURL + user.getString("avatar128"));
            userInfo.setNickname(user.getString("nickname"));

            } catch (JSONException e) {

            }
            return userInfo;
    }
    public UserInfo getUserInfo(com.alibaba.fastjson.JSONObject user) {
        UserInfo userInfo = new UserInfo();

            userInfo.setUid(user.getString("uid"));
            userInfo.setAvatar(Url.USERHEADURL + user.getString("avatar128"));
            userInfo.setNickname(user.getString("nickname"));


        return userInfo;
    }

    public UserInfo getUserAllInfo(String result) {
        UserInfo userInfo = new UserInfo();
        try {
            JSONObject jsonObject = new JSONObject(result);
            userInfo.setNickname(jsonObject.getString("nickname"));
            userInfo.setCtime(getStandardDate(jsonObject.getString("reg_time")));
            userInfo.setFollowing(jsonObject.getString("following"));
            userInfo.setEmail(jsonObject.getString("email"));
            userInfo.setScore(jsonObject.getString("score"));
            userInfo.setTitle(jsonObject.getString("title"));
            userInfo.setFans(jsonObject.getString("fans"));
            userInfo.setIsFollow(jsonObject.getString("is_follow"));
            userInfo.setSex(jsonObject.getString("sex"));
            userInfo.setSignature(jsonObject.getString("signature"));
//            userInfo.setBirth(jsonObject.getString("birthday"));
//            Log.d("myjson>>>",jsonObject.getString("birthday"));
            userInfo.setAvatar(Url.USERHEADURL + jsonObject.getString("avatar256"));
            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("expand_info"));
            userInfo.setBirth(jsonObject1.getString("生日"));
            userInfo.setQq(jsonObject1.getString("qq"));
            JSONObject jsonObject2 = new JSONObject(jsonObject.getString("pos_city"));
            userInfo.setCity(jsonObject2.getString("name"));
            JSONObject jsonObject3 = new JSONObject(jsonObject.getString("pos_district"));
            userInfo.setDistrict(jsonObject3.getString("name"));
            JSONObject jsonObject4 = new JSONObject(jsonObject.getString("pos_province"));
            userInfo.setProvince(jsonObject4.getString("name"));
        } catch (JSONException e) {

        }
        return userInfo;
    }

    public String getApkUrl(String result) {
        String downLoadUrl = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("file").startsWith("http")) {
                downLoadUrl = jsonObject.getString("file");
            } else {
                downLoadUrl = Url.USERHEADURL + "/tox/" + jsonObject.getString("file");
            }
        } catch (JSONException E) {
            E.printStackTrace();
        }
        return downLoadUrl;
    }

    // 解析糗事评论的方法
    public List<Com2Com> getAhamedCommentsList(String value) {
        List<Com2Com> list = null;
        try {
            JSONArray jay = new JSONArray(value);
            list = new ArrayList<Com2Com>();
            for (int i = 0; i < jay.length(); i++) {


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CheckInfo> getCheckRank(String result) {
        List<CheckInfo> list = new ArrayList<CheckInfo>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = (JSONObject) jsonArray.opt(i);
                CheckInfo checkInfo = new CheckInfo();
                checkInfo.setCtime(item.getString("create_time"));
                JSONObject jsonObject1= item.getJSONObject("user");
                checkInfo.setCon_num(jsonObject1.getString("con_check"));
                checkInfo.setTotal_num(jsonObject1.getString("total_check"));
                checkInfo.setUserInfo(getUserInfo(jsonObject1));
                list.add(checkInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 解析附近用户的方法
    public List<UserInfo> getNearUserList(String result) {
        List<UserInfo> list = null;
        try {
            JSONArray jay = new JSONArray(result);
            list = new ArrayList<UserInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                UserInfo info = new UserInfo();
                /*info.setUserid(job.getString("USERID"));
				info.setUname(job.getString("UNAME"));
				info.setUhead(job.getString("UHEAD"));
				info.setUage(job.getString("UAGE"));
				info.setUhobbles(job.getString("UHOBBIES"));
				info.setUplace(job.getString("UPLACE"));
				info.setUexplain(job.getString("UEXPLAIN"));
				info.setUtime(job.getString("UTIME"));
				info.setUbrand(job.getString("UBRAND"));
				info.setUsex(job.getString("USEX"));*/
                list.add(info);
            }
        } catch (JSONException e) {
            e.getMessage();
        }
        return list;
    }

    public CheckInfo getMyCheckInfo(String result) {
        CheckInfo checkInfo = new CheckInfo();
        try {

            JSONObject jsonObject = new JSONObject(result);
            checkInfo.setCon_num(jsonObject.getString("con_num"));
            checkInfo.setTotal_num(jsonObject.getString("total_num"));
            checkInfo.setOver_tare(jsonObject.getString("over_rate"));
            checkInfo.setIsChecked(Integer.parseInt(jsonObject.getString("has_checked")));

        } catch (JSONException E) {
            E.getMessage();
        }
        return checkInfo;
    }
    public PostInfo getPostInfo(String result){
        PostInfo postInfo=new PostInfo();

        com.alibaba.fastjson.JSONObject jsonObject= (com.alibaba.fastjson.JSONObject) JSON.parse(result);
        postInfo.setImgList(getImgUrlList(jsonObject.getJSONObject("content").getJSONArray("image")));
        postInfo.setPostContent(jsonObject.getJSONObject("content").getString("content"));
        postInfo.setReplyCount(jsonObject.getString("create_time"));
        postInfo.setPostTitle(jsonObject.getString("title"));
        postInfo.setReplyCount("0");
        postInfo.setSupportCount("0");
        postInfo.setIs_support(0);
        postInfo.setPostId(jsonObject.getString("id"));
        postInfo.setUserInfo(getUserInfo(jsonObject.getJSONObject("user")));
        postInfo.setViewCount(jsonObject.getString("view_count"));
        return postInfo;
    }



    public  List<PostInfo> getPostInfos(String result,String forumTitle){
        List<PostInfo> list=new ArrayList<PostInfo>();
       try {
           JSONObject object=new JSONObject(result);
           JSONArray jsonArray=object.getJSONArray("list");
           for(int i=0;i<jsonArray.length();i++){
               JSONObject listItem=(JSONObject)jsonArray.opt(i);
               PostInfo postInfo=new PostInfo();
               postInfo.setCreatTime(getStandardDate(listItem.getString("create_time")));
               postInfo.setIs_support(Integer.parseInt(listItem.getString("is_support")));
               postInfo.setIs_top(Integer.parseInt(listItem.getString("is_top")));
               postInfo.setPostContent(listItem.getString("content"));
               postInfo.setPostId(listItem.getString("id"));
               postInfo.setLastReplyTime(getStandardDate(listItem.getString("last_reply_time")));
               postInfo.setViewCount(listItem.getString("view_count"));
               postInfo.setSupportCount(listItem.getString("support_count"));
               postInfo.setReplyCount(listItem.getString("reply_count"));
               postInfo.setPostTitle(listItem.getString("title"));
               postInfo.setUserInfo(getUserInfo(listItem.getJSONObject("user")));
               postInfo.setImgList(getImgUrlList(listItem.getJSONArray("imglist")));
               postInfo.setForumTitle(forumTitle);
                list.add(postInfo);
           }
       }catch (JSONException e){
           e.printStackTrace();
       }
         return list;
    }


    public List<ForumInfo> getForumInfos(String result){
        List<ForumInfo> list=new ArrayList<ForumInfo>();
        try{

            com.alibaba.fastjson.JSONObject object=JSON.parseObject(result);
            com.alibaba.fastjson.JSONArray jsonArray=object.getJSONArray("list");
            for(int i=0;i<jsonArray.size();i++){
                com.alibaba.fastjson.JSONObject listItem=( com.alibaba.fastjson.JSONObject)jsonArray.get(i);
                Object forum=listItem.get("forums");
                if(forum==null){

                }else{
                    com.alibaba.fastjson.JSONArray forums=listItem.getJSONArray("forums");
                    for(int j=0;j<forums.size();j++){
                       com.alibaba.fastjson.JSONObject item=(com.alibaba.fastjson.JSONObject)forums.get(j);
                        ForumInfo forumInfo=new ForumInfo();
                        forumInfo.setForumId(item.getString("id"));
                        forumInfo.setLastReplyTime(getStandardDate(item.getString("last_reply_time")));
                        forumInfo.setBackground(item.getString("background"));
                        forumInfo.setLogo(item.getString("logo"));
                        forumInfo.setDescription(item.getString("description"));
                        forumInfo.setTitle(item.getString("title"));
                        forumInfo.setcTime(getStandardDate(item.getString("create_time")));
                        forumInfo.setPostCount(item.getString("total_count"));
                        forumInfo.setTopicCount(item.getString("topic_count"));
                        list.add(forumInfo);
                    }

                }




            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return list;
    }

    public  List<PostComment> getPostComments(String result){
        List<PostComment> list=new ArrayList<PostComment>();
        try{
            JSONObject object=new JSONObject(result);
            JSONArray jsonArray=object.getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item=(JSONObject)jsonArray.opt(i);
                PostComment postComment=new PostComment();
                postComment.setcTime(getStandardDate(item.getString("create_time")));
                postComment.setPostId(item.getString("post_id"));
                postComment.setPostComId(item.getString("id"));
                postComment.setuTime(getStandardDate(item.getString("update_time")));
                postComment.setUserInfo(getUserInfo(item.getJSONObject("user")));
                postComment.setImgList(getImgUrlList(item.getJSONArray("imgList")));
                postComment.setComContent(item.getString("content"));
                postComment.setCom2comList(getComments(item.getJSONArray("toReplyList")));
                list.add(postComment);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取楼中里面的评论列表
     * @param jsonArray
     * @return
     */
    public List<Com2Com> getComments(JSONArray jsonArray){
        List<Com2Com> list=new ArrayList<Com2Com>();
        if(jsonArray!=null){
            try{
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=(JSONObject)jsonArray.opt(i);
                    Com2Com com2Com=new Com2Com();
                    com2Com.setcTime(getStandardDate(jsonObject.getString("ctime")));
                    com2Com.setContent(jsonObject.getString("content"));
                    com2Com.setId(jsonObject.getString("id"));
                    com2Com.setPostId(jsonObject.getString("post_id"));
                    com2Com.setTo_f_reply_id(jsonObject.getString("to_f_reply_id"));
                    com2Com.setTo_reply_id(jsonObject.getString("to_reply_id"));
                    com2Com.setUid(jsonObject.getString("uid"));
                    com2Com.setTo_uid(jsonObject.getString("to_uid"));
                    com2Com.setUserInfo(getUserInfo(jsonObject.getJSONObject("user")));
                    if(jsonObject.getString("is_landlord").equals("0")){
                        com2Com.setLandlord(false);
                    }else {
                        com2Com.setLandlord(true);
                    }
                    list.add(com2Com);
                }
            }catch (JSONException E){
                E.printStackTrace();
            }
        }
        return list;
    }

    public Com2Com getComment(String result){
        Com2Com com=new Com2Com();
        try{
            JSONObject jsonObject=new JSONObject(result);
            com.setId(jsonObject.getString("id"));
            com.setTo_uid(jsonObject.getString("to_uid"));
            com.setUid(jsonObject.getString("uid"));
            com.setPostId(jsonObject.getString("post_id"));
            com.setContent(jsonObject.getString("content"));
            com.setcTime(jsonObject.getString("ctime"));
            com.setUserInfo(getUserInfo(jsonObject.getJSONObject("user")));

        }catch (JSONException e){
            e.printStackTrace();
        }
        return  com;
    }

    public PostComment getPostComment(String result){
        PostComment postComment=new PostComment();
        try{
            JSONObject jsonObject=new JSONObject(result);
            postComment.setcTime(result);
            postComment.setcTime(getStandardDate(jsonObject.getString("create_time")));
            postComment.setPostId(jsonObject.getString("post_id"));
            postComment.setPostComId(jsonObject.getString("id"));
            postComment.setuTime(getStandardDate(jsonObject.getString("update_time")));
            postComment.setUserInfo(getUserInfo(jsonObject.getJSONObject("user")));
            postComment.setImgList(getImgUrlList(jsonObject.getJSONArray("imgList")));
            postComment.setComContent(jsonObject.getString("content"));
//            postComment.setCom2comList(getComments(jsonObject.getJSONArray("toReplyList")));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return postComment;
    }

    /**
     * @param value  传递需要解析的Json数据
     * @return
     */
    public ArrayList<HashMap<String,String>> beforeRegisterStatus(String value){
        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(value);
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("open_invite",jsonObject.getString("open_invite_register"));
            map.put("email_verify_type",jsonObject.getString("email_verify_type"));
            map.put("mobile_verify_type",jsonObject.getString("mobile_verify_type"));
            list.add(map);
            JSONArray jsonArray = new JSONObject(value).getJSONArray("list");
            for (int i = 0;i < jsonArray.length();i++){
                HashMap<String,String> map1=new HashMap<String,String>();
                map1.put("id",jsonArray.optJSONObject(i).getString("id"));
                map1.put("title",jsonArray.optJSONObject(i).getString("title"));
                list.add(map1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("list>>><<><", String.valueOf(list));
        return list;
    }

    public String[] checkRegisterStatus(String value){
        String[] status=new String[4];
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONObject jsonObject1=jsonObject.getJSONObject("0");
            status[0]=jsonObject1.getString("id");
            status[1]=jsonObject1.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("status>>>>",status[0]);
        Log.d("status>>>>",status[1]);
        return status;
    }


    //解析注册方式
    public ArrayList<String> checkWay(String value){
        ArrayList list=new ArrayList();
        String[] arr;
        if (value != null) {
            try {
                JSONObject jsonObject = new JSONObject(value);
                JSONArray array = jsonObject.getJSONArray("list");
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //资讯导航栏获取
    public ArrayList<NewsCategory> getNewsNavigation(String result){
        ArrayList<NewsCategory> list = new ArrayList<NewsCategory>();
        String id = "0";
        try {
            JSONArray jsonArray = new JSONObject(result).getJSONArray("list");
            if (jsonArray != null){
                    for (int i = -1;i < jsonArray.length();i++) {
                        NewsCategory newsCategory = new NewsCategory();
                        if (i == -1){
                            newsCategory.setId("0");
                            newsCategory.setTitle("首页");
                            newsCategory.setPid("0");
                            newsCategory.setStatus("1");
                            newsCategory.setCan_post("1");
                            newsCategory.setNeed_audit("1");
                        }else {
                            id = jsonArray.optJSONObject(i).getString("id");
                            newsCategory.setId(id);
                            newsCategory.setTitle(jsonArray.optJSONObject(i).getString("title"));
                            newsCategory.setPid(jsonArray.optJSONObject(i).getString("pid"));
                            newsCategory.setCan_post(jsonArray.optJSONObject(i).getString("can_post"));
                            newsCategory.setNeed_audit(jsonArray.optJSONObject(i).getString("need_audit"));
                            newsCategory.setStatus(jsonArray.optJSONObject(i).getString("status"));
                        }
                        ArrayList<NewsCategory> secondList = new ArrayList<NewsCategory>();
                        NewsCategory tempNewsCategory = new NewsCategory();
                        tempNewsCategory.setId(id);
                        tempNewsCategory.setTitle("全部");
                        tempNewsCategory.setPid(id);
                        tempNewsCategory.setStatus("1");
                        tempNewsCategory.setCan_post("1");
                        tempNewsCategory.setNeed_audit("1");
                        secondList.add(tempNewsCategory);
                        if (i != -1) {
                            try {
                                JSONArray tempJsonArray = jsonArray.optJSONObject(i).getJSONArray("NewsSecond");
                                if (tempJsonArray != null) {
                                    for (int j = 0; j < tempJsonArray.length(); j++) {
                                        NewsCategory secondNewsCategory = new NewsCategory();
                                        secondNewsCategory.setId(tempJsonArray.optJSONObject(j).getString("id"));
                                        secondNewsCategory.setTitle(tempJsonArray.optJSONObject(j).getString("title"));
                                        secondNewsCategory.setPid(tempJsonArray.optJSONObject(j).getString("pid"));
                                        secondNewsCategory.setCan_post(tempJsonArray.optJSONObject(j).getString("can_post"));
                                        secondNewsCategory.setNeed_audit(tempJsonArray.optJSONObject(j).getString("need_audit"));
                                        secondNewsCategory.setStatus(tempJsonArray.optJSONObject(j).getString("status"));
                                        secondList.add(secondNewsCategory);
                                    }
                                }
                                newsCategory.setNewsSecond(secondList);
                            } catch (JSONException e) {
                                newsCategory.setNewsSecond(secondList);
                                list.add(newsCategory);
                                continue;
                            }
                        }else {
                            newsCategory.setNewsSecond(secondList);
                        }
                        list.add(newsCategory);
                    }
                }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 获取资讯大纲
     */
    public ArrayList<NewsListInfo> getNewsList(String result){
        ArrayList<NewsListInfo> list = new ArrayList<NewsListInfo>();
        try {
            JSONArray jsonArray = new JSONObject(result).getJSONArray("list");
            for (int i = 0;i < jsonArray.length();i++){
                NewsListInfo newsListInfo = new NewsListInfo();
                newsListInfo.setId(jsonArray.optJSONObject(i).getString("id"));
                newsListInfo.setUid(jsonArray.optJSONObject(i).getString("uid"));
                newsListInfo.setTitle(jsonArray.optJSONObject(i).getString("title"));
                newsListInfo.setDescription(jsonArray.optJSONObject(i).getString("description"));
                newsListInfo.setCategory(jsonArray.optJSONObject(i).getString("category"));
                newsListInfo.setStatus(jsonArray.optJSONObject(i).getString("status"));
                newsListInfo.setReason(jsonArray.optJSONObject(i).getString("reason"));
                newsListInfo.setPosition(jsonArray.optJSONObject(i).getString("position"));
                newsListInfo.setCover(baseApi.getResourcesURL(jsonArray.optJSONObject(i).getString("cover")));
                newsListInfo.setView(jsonArray.optJSONObject(i).getString("view"));
                newsListInfo.setComment(jsonArray.optJSONObject(i).getString("comment"));
                newsListInfo.setCollection(jsonArray.optJSONObject(i).getString("collection"));
                newsListInfo.setDead_line(jsonArray.optJSONObject(i).getString("dead_line"));
                newsListInfo.setSource(jsonArray.optJSONObject(i).getString("source"));
                newsListInfo.setCreate_time(jsonArray.optJSONObject(i).getString("create_time"));
                newsListInfo.setUpdate_time(jsonArray.optJSONObject(i).getString("update_time"));
                try{
                    JSONObject userJSONObj = jsonArray.optJSONObject(i).getJSONObject("user");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUid(userJSONObj.getString("uid"));
                    userInfo.setNickname(userJSONObj.getString("nickname"));
                    userInfo.setAvatar(baseApi.getResourcesURL(userJSONObj.getString("avatar128")));
                    newsListInfo.setUser(userInfo);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                list.add(newsListInfo);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    //解析资讯全部分类
    public ArrayList<HashMap<String,String>> getType(String value){
        ArrayList<HashMap<String,String>> title=new ArrayList<HashMap<String,String>>();
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray array=jsonObject.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject title_object = array.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", title_object.getString("id"));
                map.put("title", title_object.getString("title"));
                title.add(map);
                JSONArray jsonArray1 = title_object.optJSONArray("NewsSecond");
                if (jsonArray1 != null) {
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        JSONObject second = jsonArray1.getJSONObject(j);
                        map1.put("id", second.getString("id"));
                        map1.put("title", "----" + second.getString("title"));
                        title.add(map1);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return title;
    }

    /**
     * 获取我的咨询投稿信息
     */
    public ArrayList<NewsListInfo> getMyNewsList(String result){
        ArrayList<NewsListInfo> list = new ArrayList<NewsListInfo>();
        try {
            JSONArray jsonArray = new JSONObject(result).getJSONArray("list");
            for (int i = 0;i < jsonArray.length();i++){
                NewsListInfo newsListInfo = new NewsListInfo();
                newsListInfo.setId(jsonArray.optJSONObject(i).getString("id"));
                newsListInfo.setUid(jsonArray.optJSONObject(i).getString("uid"));
                newsListInfo.setTitle(jsonArray.optJSONObject(i).getString("title"));
                newsListInfo.setDescription(jsonArray.optJSONObject(i).getString("description"));
                newsListInfo.setCategory(jsonArray.optJSONObject(i).getString("category"));
                newsListInfo.setStatus(jsonArray.optJSONObject(i).getString("status"));
                newsListInfo.setReason(jsonArray.optJSONObject(i).getString("reason"));
                newsListInfo.setPosition(jsonArray.optJSONObject(i).getString("position"));
                newsListInfo.setCover(baseApi.getResourcesURL(jsonArray.optJSONObject(i).getString("cover")));
                newsListInfo.setView(jsonArray.optJSONObject(i).getString("view"));
                newsListInfo.setComment(jsonArray.optJSONObject(i).getString("comment"));
                newsListInfo.setCollection(jsonArray.optJSONObject(i).getString("collection"));
                newsListInfo.setDead_line(jsonArray.optJSONObject(i).getString("dead_line"));
                newsListInfo.setSource(jsonArray.optJSONObject(i).getString("source"));
                newsListInfo.setCreate_time(jsonArray.optJSONObject(i).getString("create_time"));
                newsListInfo.setUpdate_time(jsonArray.optJSONObject(i).getString("update_time"));
                newsListInfo.setApproval(jsonArray.optJSONObject(i).getString("approval"));
                try{
                    JSONObject userJSONObj = jsonArray.optJSONObject(i).getJSONObject("user");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUid(userJSONObj.getString("uid"));
                    userInfo.setNickname(userJSONObj.getString("nickname"));
                    userInfo.setAvatar(baseApi.getResourcesURL(userJSONObj.getString("avatar128")));
                    newsListInfo.setUser(userInfo);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                list.add(newsListInfo);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取我的咨询投稿信息
     */
    public NewsDetailInfo getNewsInfoList(String result){
        NewsDetailInfo newsDetailInfo = new NewsDetailInfo();
        UserInfo userInfo = new UserInfo();
        ArrayList<ImageInfo> imageList = new ArrayList<ImageInfo>();
        try {
            JSONObject jsonObject = new JSONObject(result).getJSONObject("list");
            newsDetailInfo.setId(jsonObject.getString("id"));
            newsDetailInfo.setUid(jsonObject.getString("uid"));
            newsDetailInfo.setTitle(jsonObject.getString("title"));
            newsDetailInfo.setDescription(jsonObject.getString("description"));
            newsDetailInfo.setContent(jsonObject.getString("content"));
            newsDetailInfo.setCategory(jsonObject.getString("category"));
            newsDetailInfo.setCategory_title(jsonObject.getString("category_title"));
            newsDetailInfo.setStatus(jsonObject.getString("status"));
            newsDetailInfo.setReason(jsonObject.getString("reason"));
            newsDetailInfo.setSort(jsonObject.getString("sort"));
            newsDetailInfo.setPosition(jsonObject.getString("position"));
            newsDetailInfo.setCover(baseApi.getResourcesURL(jsonObject.getString("cover")));
            newsDetailInfo.setView(jsonObject.getString("view"));
            newsDetailInfo.setComment(jsonObject.getString("comment"));
            newsDetailInfo.setCollection(jsonObject.getString("collection"));
            newsDetailInfo.setDead_line(jsonObject.getString("dead_line"));
            newsDetailInfo.setSource(jsonObject.getString("source"));
            newsDetailInfo.setCreate_time(jsonObject.getString("create_time"));
            newsDetailInfo.setUpdate_time(jsonObject.getString("update_time"));
            try {
                JSONObject userJSON = jsonObject.getJSONObject("user");
                userInfo.setUid(userJSON.getString("uid"));
                userInfo.setUsername(userJSON.getString("username"));
                userInfo.setNickname(userJSON.getString("nickname"));
                userInfo.setAvatar(baseApi.getResourcesURL(userJSON.getString("avatar128")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            newsDetailInfo.setUser(userInfo);
            try{
                JSONArray imageJSONArray = jsonObject.getJSONArray("imgList");
                for (int i = 0;i <imageJSONArray.length();i++){
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setPos(imageJSONArray.optJSONObject(i).getString("pos"));
                    imageInfo.setSrc(baseApi.getResourcesURL(imageJSONArray.optJSONObject(i).getString("src")));
                    imageList.add(imageInfo);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            newsDetailInfo.setImgList(imageList);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return newsDetailInfo;
    }

    /**
     * 解析资讯评论数据
     */
    public ArrayList<NewsReplyInfo> getNewsReplyInfo(String result){
        ArrayList<NewsReplyInfo> list = new ArrayList<NewsReplyInfo>();
        try {
            JSONArray jsonArray = new JSONObject(result).getJSONArray("list");
            for (int i = 0;i < jsonArray.length();i++){
                NewsReplyInfo replyInfo = new NewsReplyInfo();
                replyInfo.setId(jsonArray.optJSONObject(i).getString("id"));
                replyInfo.setUid(jsonArray.optJSONObject(i).getString("uid"));
                replyInfo.setApp(jsonArray.optJSONObject(i).getString("app"));
                replyInfo.setMod(jsonArray.optJSONObject(i).getString("mod"));
                replyInfo.setRow_id(jsonArray.optJSONObject(i).getString("row_id"));
                replyInfo.setParse(jsonArray.optJSONObject(i).getString("parse"));
                replyInfo.setContent(jsonArray.optJSONObject(i).getString("content"));
                replyInfo.setCreate_time(jsonArray.optJSONObject(i).getString("create_time"));
                replyInfo.setPid(jsonArray.optJSONObject(i).getString("pid"));
                replyInfo.setStatus(jsonArray.optJSONObject(i).getString("status"));
                replyInfo.setIp(jsonArray.optJSONObject(i).getString("ip"));
                replyInfo.setArea(jsonArray.optJSONObject(i).getString("area"));
                replyInfo.setIs_landlord(jsonArray.optJSONObject(i).getString("is_landlord"));
                try {
                    UserInfo userInfo = new UserInfo();
                    JSONObject tempJson = jsonArray.optJSONObject(i).getJSONObject("user");
                    userInfo.setUid(tempJson.getString("uid"));
                    userInfo.setUsername(tempJson.getString("username"));
                    userInfo.setNickname(tempJson.getString("nickname"));
                    userInfo.setAvatar(baseApi.getResourcesURL(tempJson.getString("avatar128")));
                    replyInfo.setUser(userInfo);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                list.add(replyInfo);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析删除评论返回信息
     */
    public ErrorInfo getReturnInfo(String result){
        ErrorInfo errorInfo = new ErrorInfo();
        try {
            JSONObject jsonObject = new JSONObject(result);
            errorInfo.setError_code(jsonObject.getInt("error_code"));
            errorInfo.setMessage(jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorInfo;
    }
}