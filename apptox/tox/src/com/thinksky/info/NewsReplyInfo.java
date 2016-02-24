package com.thinksky.info;

import java.util.List;

/**
 * NewsReplyInfo
 * Created by Administrator on 2015/7/29 0029.
 */
public class NewsReplyInfo {

    /**
     * is_landlord : 1
     * uid : 264
     * app : News
     * row_id : 40
     * status : 1
     * mod : index
     * pid : 0
     * ip : 666809183
     * id : 177
     * content : 回复 @O记_天心 ：我干杯，你随意~
     * imgList : []
     * area : 北京北京
     * create_time : 05月26日 08:40
     * parse : 0
     * user :
     */
    private String is_landlord;
    private String uid;
    private String app;
    private String row_id;
    private String status;
    private String mod;
    private String pid;
    private String ip;
    private String id;
    private String content;
    private List<ImageInfo> imgList;
    private String area;
    private String create_time;
    private String parse;
    private UserInfo user;

    public void setIs_landlord(String is_landlord) {
        this.is_landlord = is_landlord;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setRow_id(String row_id) {
        this.row_id = row_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImgList(List<ImageInfo> imgList) {
        this.imgList = imgList;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getIs_landlord() {
        return is_landlord;
    }

    public String getUid() {
        return uid;
    }

    public String getApp() {
        return app;
    }

    public String getRow_id() {
        return row_id;
    }

    public String getStatus() {
        return status;
    }

    public String getMod() {
        return mod;
    }

    public String getPid() {
        return pid;
    }

    public String getIp() {
        return ip;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public List<ImageInfo> getImgList() {
        return imgList;
    }

    public String getArea() {
        return area;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getParse() {
        return parse;
    }

    public UserInfo getUser() {
        return user;
    }
}
