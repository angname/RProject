package com.thinksky.info;

import java.util.ArrayList;

/**
 *资讯详细信息
 * Created by Administrator on 2015/7/27 0027.
 */
public class NewsDetailInfo {

    /**
     * uid : 1
     * position : 0
     * sort : 0
     * status : 1
     * reason :
     * dead_line : 1436111700
     * collection : 0
     * content :  各位
     * id : 5
     * title : 如来神掌
     * category : 1
     * category_title：分类标题
     * cover :
     * update_time : 06月01日 09:00
     * source : 刀锋所指
     * description : 老爸由于太多
     * create_time : 06月01日 09:00
     * view : 31
     * comment : 3
     * user :   用户信息
     * imgList：图片list
     */
    private String uid;
    private String position;
    private String sort;
    private String status;
    private String reason;
    private String dead_line;
    private String collection;
    private String content;
    private String id;
    private String title;
    private String category;
    private String category_title;
    private String cover;
    private String update_time;
    private String source;
    private String description;
    private String create_time;
    private String view;
    private String comment;
    private UserInfo user;
    private ArrayList<ImageInfo> imgList;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDead_line(String dead_line) {
        this.dead_line = dead_line;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public void setImgList(ArrayList<ImageInfo> imgList) {
        this.imgList = imgList;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public String getUid() {
        return uid;
    }

    public String getPosition() {
        return position;
    }

    public String getSort() {
        return sort;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getDead_line() {
        return dead_line;
    }

    public String getCollection() {
        return collection;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getCover() {
        return cover;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public String getSource() {
        return source;
    }

    public String getDescription() {
        return description;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getView() {
        return view;
    }

    public String getComment() {
        return comment;
    }

    public UserInfo getUser() {
        return user;
    }

    public ArrayList<ImageInfo> getImgList() {
        return imgList;
    }

    public String getCategory_title() {
        return category_title;
    }
}