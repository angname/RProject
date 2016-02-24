package com.thinksky.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/7/17.
 */
public class WeiboCommentInfo implements Serializable {
    private String id;
    private String content;
    private UserInfo user;
    private String ctime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getCtime() {
        return ctime;
    }
}
