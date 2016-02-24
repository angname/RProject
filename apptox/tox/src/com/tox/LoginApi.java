package com.tox;

import java.util.logging.Handler;

/**
 * Created by 王杰 on 2015/7/9.
 */
public class LoginApi extends BaseApi{
    Handler mHandler;

    public LoginApi() {
        super();
    }

    public LoginApi(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 1.检查是否开启验证注册
     * 2.检查验证码的类别
     */
    public void beforeRegister(String code){
        putArg("code",code);
        execute(handler,Url.getApiUrl(Url.LOGINCHECK),false);
    }

    /**
     * 返回可用注册方式
     */
    public void checkWay(){
        execute(handler,Url.getApiUrl(Url.CHECKWAY),false);
    }

}
