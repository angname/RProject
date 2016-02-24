package com.thinksky.info;

import java.io.Serializable;

public class Com2Com implements Serializable {
    private String id;
    private String postId;
    private String content;
    private String uid;
    /**
     * 回复的楼层id
     */
    private String to_f_reply_id;
    /**
     * 回复楼中楼评论中的id
     */
    private String to_reply_id;
    private String to_uid;
    private String cTime;
    private UserInfo userInfo;
    private boolean isLandlord;

    public boolean getIsLandlord() {
        return isLandlord;
    }

    public void setLandlord(boolean isLandlord) {
        this.isLandlord = isLandlord;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTo_f_reply_id() {
        return to_f_reply_id;
    }

    public void setTo_f_reply_id(String to_f_reply_id) {
        this.to_f_reply_id = to_f_reply_id;
    }

    public String getTo_reply_id() {
        return to_reply_id;
    }

    public void setTo_reply_id(String to_reply_id) {
        this.to_reply_id = to_reply_id;
    }

    public String getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(String to_uid) {
        this.to_uid = to_uid;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }
}
