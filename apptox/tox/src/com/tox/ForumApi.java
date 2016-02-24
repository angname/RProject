package com.tox;

import java.util.List;
import java.util.logging.Handler;

/**
 * Created by Administrator on 2015/1/22 0022.
 */
public class ForumApi extends BaseApi {
    Handler mHandler;

    public ForumApi() {
        super();
    }

    public ForumApi(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 获取论坛板块
     */
    public void getForumModules(){
        execute(handler,Url.getApiUrl(Url.FORUMMODULES),false);

    }

    /**
     * 获取某个论坛的帖子
     * @param forumId
     * @param page
     * @param count
     */
    public void getPosts(String forumId,String page,String count){
        putArg("forum_id",forumId);
        putArg("page",page);
        putArg("count",count);
        execute(handler,Url.getApiUrl(Url.POST),false);
    }

    /**
     * 获取某个帖子的评论
     * @param postId
     * @param page
     * @param count
     */
    public void getPostComments(String postId,String page,String count){
        putArg("post_id",postId);
        putArg("page",page);
        putArg("count",count);
        execute(handler,Url.getApiUrl(Url.POSTCOMMENTS),false);
    }

    /**
     * 获取回复的评论
     * @param reply_id
     * @param page
     * @param count
     */
    public void getComments(String reply_id,String page,String count,String postId){
        putArg("to_f_reply_id",reply_id);
        putArg("page",page);
        putArg("count",count);
        putArg("post_id",postId);
        execute(handler,Url.getApiUrl(Url.FORUMCOMMENTS),false);
    }

    /**
     * 给帖子点赞

     * @param postId
     */
    public void supportPost(String postId){

        putArg("post_id",postId);
        putArg("session_id",Url.SESSIONID);
        execute(handler,Url.getApiUrl(Url.SUPPORTPOST),true);
    }

    /**
     * 给帖子回复
     * @param postId

     * @param content
     */
    public void sendPostComment(String postId,String content){
        putArg("post_id",postId);

        putArg("content",content);
        execute(handler,Url.getApiUrl(Url.SENDPOSTCOMMENT),true);
    }

    /**
     * 给评论回复和楼中楼的回复
     * @param postId
     * @param content
     */
    public void sendComment(String postId,String content,String toReplyId,String to_f_replyId){
        if(toReplyId.equals("0")){
            putArg("to_f_reply_id",to_f_replyId);
        }else{
            putArg("to_reply_id",toReplyId);
        }
        putArg("post_id",postId);
        putArg("content",content);
        execute(handler,Url.getApiUrl(Url.SENDCOMMENNT),true);
    }


    /**
     * 收藏帖子
     * @param uid
     * @param postId
     */
    public void collectionPost(String uid,String postId){
        putArg("uid",uid);
        putArg("post_id",postId);
        execute(handler,Url.COLLECTPOST,true);
    }

    /**
     * 发布帖子

     * @param title
     * @param content
     * @param attachIds
     */
    public void sendPost(String title,String content,List<String> attachIds,String forumId){
        putArg("forum_id",forumId);
        putArg("title",title);
        putArg("content",content);
        if(attachIds.size()!=0){
           putArg("attach_id",WeiboApi.getAttachIds(attachIds));
        }
        execute(handler,Url.getApiUrl(Url.SENDPOST),true);
    }
}
