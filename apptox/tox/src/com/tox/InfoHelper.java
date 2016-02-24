package com.tox;

import android.content.Context;

/**
 * Created by Administrator on 2014/8/15.
 */
public class InfoHelper {

    private InfoHelper() {

    }

    public static void ShowMessageInfo(Context context, int id) {
        switch (id) {
            case 701:
                ToastHelper.showToast("用户名长度必须在16个字符以内！", context);
                break;
            case 702:
                break;
            case 703:
                break;
            case 704:
                break;
            case 601:
                ToastHelper.showToast("用户不存在或被禁用！", context);
                break;
            case 602:
                ToastHelper.showToast("密码错误！", context);
                break;
            case 603:
                ToastHelper.showToast("未知错误", context);
                break;
            case 404:
                ToastHelper.showToast("找不到接口", context);
                break;
            case 403:
                ToastHelper.showToast("站点关闭", context);
                break;
            case 401:
                ToastHelper.showToast("需要登录", context);
                break;
            case 400:
                ToastHelper.showToast("参数格式错误", context);
                break;
            case 501:
                ToastHelper.showToast("赞失败，重复", context);
                break;
            case 502:
                ToastHelper.showToast("赞失败，数据库写入错误", context);
                break;
            case 8000:
                ToastHelper.showToast("已经签到或签到还未开始", context);
            default:
                break;
        }
    }
}
