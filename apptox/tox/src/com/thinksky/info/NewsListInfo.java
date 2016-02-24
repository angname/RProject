package com.thinksky.info;

import java.io.Serializable;

/**
 * news列表对象
 * Created by Administrator on 2015/7/20 0020.
 */
public class NewsListInfo implements Serializable{

    /**
     * uid : 166
     * position : 0
     * sort : 0
     * status : 1
     * reason :
     * dead_line : 2015-07-20 14:56
     * collection : 0
     * id : 148
     * title : 测试标题
     * category : 1
     * cover : /Uploads/Picture/2015-07-20/55ac9a85744ed.jpg
     * update_time : 49分钟前
     * source :
     * description : 简介简介简介简介简介简介简介简介简介简介简介简介简介简介简介
     * create_time : 49分钟前
     * view : 4
     * comment : 0
     * approval: 已过期
     * user : {"uid":"166","username":"Andy_","avatar128":"/Uploads/Avatar/2015-07-08/559c8b092c3ff_128_128.jpg","nickname":"O记_Andy"}
     */
    private String uid;
    private String position;
    private String sort;
    private String status;
    private String reason;
    private String dead_line;
    private String collection;
    private String id;
    private String title;
    private String category;
    private String cover;
    private String update_time;
    private String source;
    private String description;
    private String create_time;
    private String view;
    private String comment;
    private String approval;
    private UserInfo user;

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

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getApproval() {
        return approval;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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
}
