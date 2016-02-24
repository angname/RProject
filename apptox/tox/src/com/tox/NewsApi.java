package com.tox;

import android.util.Log;

import android.os.Handler;

public class NewsApi extends BaseApi {

    public NewsApi() {
        super();
    }

    public NewsApi(Handler handler) {
        this.handler = handler;
    }

    /**
     * 获取导航栏
     */
    public void getNavigation(String id) {
        putArg("id", id);
        execute(handler, Url.getApiUrl(Url.NEWSCATEGORY), false);
    }

    /**
     * 获取资讯大纲
     */
    public void getNewsList(String category, int page) {
        putArg("category", category);
        putArg("page", String.valueOf(page));
        execute(handler, Url.getApiUrl(Url.NEWSALL), false);
    }

    /**
     *获取我的咨询信息
     */
    public void getMyNewsList(int page,String overdue,String status){
        putArg("session_id", Url.SESSIONID);
        putArg("page",String.valueOf(page));
        putArg("overdue",overdue);
        putArg("status",status);
        execute(handler,Url.getApiUrl(Url.MYNEWSALL),true);
    }


    /**
     * 获取资讯详情信息
     */
    public void getNewsInfo(String id){
        putArg("id",id);
        execute(handler,Url.getApiUrl(Url.NEWSDETAIL),false);
    }

    /**
     * 获取资讯评论信息
     */
    public void getNewsReply(String row_id,int page){
        putArg("page",String.valueOf(page));
        putArg("row_id",row_id);
        execute(handler,Url.getApiUrl(Url.NEWSCOMMENTS),false);
    }

    /**
     * 执行删除评论操作
     */
    public void exetDelReply(String row_id,String id){
        putArg("session_id",Url.SESSIONID);
        putArg("row_id",row_id);
        putArg("id",id);
        execute(handler,Url.getApiUrl(Url.DELNEWSREPLY),true);
    }
    /**
     * 获取资讯全部分类
     */
    public void getType() {
        execute(handler, Url.getApiUrl(Url.NEWSCATEGORY), false);
    }


    /**
     *
     * @param category 资讯分类
     * @param imgId 封面ID
     * @param title 资讯标题
     * @param content  资讯内容
     */
    public void sendNews(String category, String imgId,String title,String content) {
        putArg("title", title);
        putArg("content", content);
        putArg("category", category);
        Log.d("tre", category);
//        putArg("description", "简介简介简介简介简介简介简介简介简介简介简介简介简介简介简介");
        putArg("cover", imgId);
        putArg("session_id", getSeesionId());
        execute(handler, Url.getApiUrl(Url.SENDNEWS), true);
    }

    /**
     *
     * @param row_id 资讯ID
     * @param content 评论内容
     */
    public void sendReply(String row_id,String content){
        putArg("row_id",row_id);
        putArg("content",content);
        execute(handler, Url.getApiUrl(Url.SENDNEWSCOMMENT), true);
    }

    public void sendReplys(){

    }
}
