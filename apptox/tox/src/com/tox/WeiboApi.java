package com.tox;

import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * 微博Api
 */
public class WeiboApi extends BaseApi {
    Handler mHandler;

    public WeiboApi() {
        super();
    }

    public WeiboApi(Handler handler) {
        super();
        mHandler = handler;
    }

    /**
     * 删除微博
     */
    public void deleteWeiBo(String weibo_id) {
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }
        putArg("weibo_id", weibo_id);
        execute(handler, Url.getApiUrl(Url.DELETEWEIBO), true);
    }

    /**
     * 获取全站微博列表
     *
     * @param page
     * @param uid
     */
    public void listAllWeibo(int page, String uid) {
        putArg("page", page + "");
        if (!uid.equals("0")) {
            putArg("uid", uid);
        }
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }
        execute(handler, Url.getApiUrl(Url.WEIBO), true);
    }

    /**
     * 获取我的微博列表
     *
     * @param page
     * @param uid
     */
    public void listMyWeibo(int page, String uid) {
        putArg("page", page + "");
        if (!uid.equals("1")) {
            putArg("uid", uid);
        }
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }


        execute(handler, Url.getApiUrl(Url.MYFOLLOWINGWEIBO), true);
    }

    /**
     * 获取我关注的人的微博
     *
     * @param page
     */
    public void listMyFollowingWeibo(int page) {
        putArg("page", page + "");
        putArg("count", "10");
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }

        execute(handler, Url.getApiUrl(Url.MYFOLLOWINGWEIBO), true);
    }

    /**
     * 发送文字微博
     *
     * @param content
     */
    public void sendTextWeibo(String content, List<String> attach_ids, String type) {
        putArg("content", content);
        putArg("from", Url.getFROM());
        if (attach_ids.size() != 0) {
            putArg("attach_ids", getAttachIds(attach_ids));

        }
        putArg("type", type);

        execute(handler, Url.getApiUrl(Url.SENDWEIBO), true);
    }

    /**
     * 转发微博
     *
     * @param content
     * @param source_id
     * @param weibo_id
     */
    public void sendRepost(String content, String source_id, String weibo_id) {
        putArg("from", Url.getFROM());
        putArg("content", content);
        Log.d("source_id", source_id);
        Log.d("weibo_id", weibo_id);
        putArg("weiboId", weibo_id);
        putArg("sourceId", source_id);
        putArg("type", "repost");
        execute(handler, Url.getApiUrl(Url.REPOSTWEIBO), true);
    }

    public static String getAttachIds(List<String> attach_ids) {
        String ids = "";
        for (int i = 0; i < attach_ids.size(); i++) {
            if (i == 0) {
                ids = attach_ids.get(i);
            } else {
                ids = ids + "," + attach_ids.get(i);
            }

        }
        return ids;
    }

    /**
     * 获取微博详情
     *
     * @param weibo_id
     */
    public void getWeiboDetail(String weibo_id) {
        putArg("weibo_id", weibo_id);
        execute(handler, Url.getApiUrl(Url.COMMENTS), false);
    }

    /**
     * 发送微博评论
     *
     * @param weibo_id
     * @param content
     */
    public void sendWeiboComment(String weibo_id, String content, int comment_id) {
        putArg("weibo_id", weibo_id);
        putArg("content", content);
        putArg("comment_id", comment_id + "");
        execute(handler, Url.getApiUrl(Url.SENDWEIBOCOMMENT), true);
    }

    /**
     * 获取微博评论
     *
     * @param weibo_id
     * @param page
     */
    public void getWeiboComment(String weibo_id, String page) {
        putArg("weibo_id", weibo_id);
        putArg("page", page);
        execute(handler, Url.getApiUrl(Url.COMMENTS), false);
    }

    public void checkUpdate() {
        putArg("version", "0.1.1");
        execute(handler, Url.getApiUrl(Url.CHECKUPDATE), false);
    }

    public void getCheckRankDate() {
        execute(handler, Url.getApiUrl(Url.CHECKRANK), false);
    }

    public void getCheckInfo() {
        execute(handler, Url.getApiUrl(Url.CHECKINFO), true);
    }

    public void CheckIn() {
        execute(handler, Url.getApiUrl(Url.CHECKIN), true);
    }


    public void supportWeibo(String weibo_id) {
        putArg("weibo_id", weibo_id);
        execute(handler, Url.getApiUrl(Url.SUPPORTWEIBO), true);
    }

    /**
     * 获取发表微博字数限制
     */
    public void getWeiboWordsLimit() {

        execute(handler, Url.getApiUrl(Url.WEIBOWORDLIMIT), false);
    }

}
