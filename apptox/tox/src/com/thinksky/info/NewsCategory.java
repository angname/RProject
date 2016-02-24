package com.thinksky.info;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsCategory implements Serializable{

    /**
     * id : 1
     * title :
     * sort : 1
     * NewsSecond : NewsCategory
     * status : 1
     * need_audit : 1
     * create_time :
     * can_post : 1
     * pid : 0
     */
    private String id;
    private String title;
    private String sort;
    private ArrayList<NewsCategory> NewsSecond;
    private String status;
    private String need_audit;
    private String create_time;
    private String can_post;
    private String pid;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setNewsSecond(ArrayList<NewsCategory> NewsSecond) {
        this.NewsSecond = NewsSecond;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNeed_audit(String need_audit) {
        this.need_audit = need_audit;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setCan_post(String can_post) {
        this.can_post = can_post;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSort() {
        return sort;
    }

    public ArrayList<NewsCategory> getNewsSecond() {
        return NewsSecond;
    }

    public String getStatus() {
        return status;
    }

    public String getNeed_audit() {
        return need_audit;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getCan_post() {
        return can_post;
    }

    public String getPid() {
        return pid;
    }

}
