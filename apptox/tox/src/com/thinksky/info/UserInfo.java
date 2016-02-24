package com.thinksky.info;

import com.tox.Url;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String uid;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String ctime;
    private String score;
    private String title;
    private String session_id;
    private String fans;
    private String following;
    private String version;
    private String word_limit;
    private String isFollow;
    private String sex;
    private String birth;
    private String signature;
    private String qq;
    private String place;
    private String province;
    private String city;
    private String district;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getWord_limit() {
        return word_limit;
    }

    public void setWord_limit(String word_limit) {
        this.word_limit = word_limit;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getFans() {
        return fans;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFollowing() {
        return following;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String userid) {
        this.uid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getAvatar() {
        String s=avatar;
        if (s!=null) {
            if (s.startsWith(Url.USERHEADURL)) {
                String cut = s.substring(Url.USERHEADURL.length());
                if (cut.startsWith("http")) {
                    return cut;
                }
            }
        }
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public void setIsFollow(String isFollow){
        this.isFollow = isFollow;
    }

    public String getIsFollow(){
        return isFollow;
    }

    public void setSex(String sex){this.sex=sex;}

    public String getSex(){ return sex;}

    public void setBirth(String birth){this.birth=birth;}

    public String getBirth(){ return birth;}

    public void setSignature(String signature){this.signature=signature;}

    public String getSignature(){ return signature;}

    public void setQq(String qq){this.qq=qq;}

    public String getQq(){ return qq;}

    public void setPlace(String place){ this.place=place;}

    public String getPlace(){ return place;}

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}