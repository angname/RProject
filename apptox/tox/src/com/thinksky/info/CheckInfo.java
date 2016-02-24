package com.thinksky.info;

/**
 * Created by Administrator on 2014/8/12.
 */
public class CheckInfo {
    private String uid;
    private String con_num;
    private String total_num;
    private String ctime;
    private UserInfo userInfo;
    private String over_tare;
    private int isChecked;

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCon_num() {
        return con_num;
    }

    public void setCon_num(String con_num) {
        this.con_num = con_num;
    }

    public String getTotal_num() {
        return total_num;
    }

    public void setTotal_num(String total_num) {
        this.total_num = total_num;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getOver_tare() {
        return over_tare;
    }

    public void setOver_tare(String over_tare) {
        this.over_tare = over_tare;
    }
}
