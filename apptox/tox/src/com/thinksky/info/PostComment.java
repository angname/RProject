package com.thinksky.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/1/23 0023.
 */
public class PostComment implements Serializable {
    private String postComId;
    private UserInfo userInfo;
    private String postId;
    private String cTime;
    private String uTime;
    private List<String> imgList;
    private String comContent;
    private String title;
    private boolean head;
    private List<Com2Com> com2comList=new ArrayList<Com2Com>();

    public List<Com2Com> getCom2comList() {
        return com2comList;
    }

    public void setCom2comList(List<Com2Com> com2comList) {
        this.com2comList = com2comList;
    }

    public boolean getHead() {
        return head;
    }

    public void setHead(boolean head) {
        this.head = head;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComContent() {
        return comContent;
    }

    public void setComContent(String comContent) {
        this.comContent = comContent;
    }

    public String getPostComId() {
        return postComId;
    }

    public void setPostComId(String postComId) {
        this.postComId = postComId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getuTime() {
        return uTime;
    }

    public void setuTime(String uTime) {
        this.uTime = uTime;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
