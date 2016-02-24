package com.tox;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Administrator on 2014/7/18.
 */
public class UserApi extends BaseApi {

    public UserApi() {
        super();
    }

    public UserApi(Handler handler) {
        this.handler = handler;
    }

    /**
     * 修改个性签名
     */
    public void changeSign(String sign) {
        putArg("session_id",Url.SESSIONID);
        putArg("signature",sign);
        execute(handler, Url.getApiUrl(Url.SETUSERINFO), true);
    }

    /**
     * 修改邮箱
     */
    public void changeEmail(String email) {
        putArg("session_id",Url.SESSIONID);
        putArg("email",email);
        execute(handler, Url.getApiUrl(Url.SETUSERINFO), true);
    }

    /**
     * 修改性别
     */
    public void changeSex(String num){
        putArg("session_id",Url.SESSIONID);
        putArg("sex",num);
        execute(handler, Url.getApiUrl(Url.SETUSERINFO), true);
    }

    /**
     * 修改昵称
     */
    public void changeName(String name){
        putArg("session_id",Url.SESSIONID);
        putArg("name",name);
        execute(handler, Url.getApiUrl(Url.SETUSERINFO), true);
    }

    /**
     * 拓展资料
     * 修改拓展资料：QQ
     */
    public void changeQq(String qq) {
        putArg("session_id",Url.SESSIONID);
        putArg("data",qq);
        putArg("id",1+"");
        execute(handler,Url.getApiUrl(Url.SETOTHERINFO),true);
    }

    public void changeBirth(String data){
        putArg("session_id",Url.SESSIONID);
        putArg("data",data);
        putArg("id",2+"");
        execute(handler,Url.getApiUrl(Url.SETOTHERINFO),true);
    }

    /**
     * 用户加关注操作
     */
    public void doFollow(String followId){
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }
        putArg("follow_who",followId);
        execute(handler, Url.getApiUrl(Url.USERDOFOLLOW), false);
    }

    /**
     * 用户取消关注操作
     */
    public void endFollow(String followId){
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }
        putArg("follow_who",followId);
        execute(handler, Url.getApiUrl(Url.USERENDFOLLOW), false);
    }

    /**
     * 获取用户信息
     *
     * @param uid
     */
    public void getUserInfo(String uid) {
        String fields = "avatar256,sex,nickname,username,score,tox_money,email,weibo_count,rank_link,expand_info,fans,following,reg_time,title,signature,birthday,qq,pos_city,pos_district,pos_province";
        if (!Url.SESSIONID.equals("")) {
            putArg("session_id", Url.SESSIONID);
        }
        putArg("uid", uid);
        putArg("fields", fields);
        execute(handler, Url.getApiUrl(Url.USERINFO), false);
    }

    /**
     * 获取用户拓展资料
     */
    public void getOtherInfo(){
        putArg("session_id",Url.SESSIONID);
        execute(handler, Url.getApiUrl(Url.OTHERINFO), false);
    }

    /**
     * 自动登入
     *
     * @param username
     * @param password
     */
    public void autoLogin(String username, String password) {

        Log.e("START AUTO LOGIN>>>>>", "2");
        putArg("username", username);
        putArg("password", password);
        execute(handler, Url.getApiUrl(Url.LOGIN), false);
    }

    public void logout() {
        execute(handler, Url.getApiUrl(Url.LOGOUT), false);
    }

    public void register(String username,String password,String code,String role,String nickname,String reg_type,String verify){
        Log.e("开始注册",">>>>>>>>>>>>>>");
        putArg("username", username);
        putArg("password", password);
        putArg("code", code);
        putArg("role", role);
        putArg("nickname", nickname);
        putArg("reg_type",reg_type);
        putArg("reg_verify",verify);
        Log.d("role", role);
        Log.d("code",code);
        Log.d("reg_type",reg_type);
        Log.d("reg_verify",verify);
        execute(handler, Url.getApiUrl(Url.REGISTER),false);
    }

    public void sendVerify(String account,String type){
        putArg("account",account);
        putArg("type",type);
        execute(handler,Url.getApiUrl(Url.VERIFY),false);
    }

}
