package com.thinksky.info;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2014/7/16.
 */
public class WeiboInfo implements Serializable {
    private String wid;
    private String wcontent;
    private String ctime;
    private String comment_count;
    private String repost_count;
    private UserInfo user;
    private List<String> imgList;
    private String type;
    private String likenum;
    private WeiboInfo repostWeiboInfo;
    private String from;
    private int is_top;
    private boolean is_supported;
    private boolean can_delete;

    public void setCan_delete(boolean can_delete) {
        this.can_delete = can_delete;
    }

    public boolean getCan_delete() {
        return can_delete;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean getIs_supported() {
        return is_supported;
    }

    public void setIs_supported(boolean is_supported) {
        this.is_supported = is_supported;
    }

    public int getIs_top() {
        return is_top;
    }

    public void setIs_top(int is_top) {
        this.is_top = is_top;
    }

    public void setRepostWeiboInfo(WeiboInfo weiboInfo) {
        this.repostWeiboInfo = weiboInfo;
    }

    public WeiboInfo getRepostWeiboInfo() {
        return repostWeiboInfo;
    }

    public void setLikenum(String like) {
        this.likenum = like;
    }

    public String getLikenum() {
        return likenum;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getWcontent() {
        return wcontent;
    }

    public void setWcontent(String wcontent) {
        this.wcontent = wcontent;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getRepost_count() {
        return repost_count;
    }

    public void setRepost_count(String repost_count) {
        this.repost_count = repost_count;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public void setImgList(List imgList) {
        this.imgList = imgList;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
