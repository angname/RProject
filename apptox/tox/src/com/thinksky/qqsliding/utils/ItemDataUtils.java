package com.thinksky.qqsliding.utils;


import com.thinksky.qqsliding.bean.ItemBean;
import com.thinksky.tox.R;

import java.util.ArrayList;
import java.util.List;


public class ItemDataUtils {
    public static List<ItemBean> getItemBeans() {
        List<ItemBean> itemBeans = new ArrayList<>();
        itemBeans.add(new ItemBean(R.drawable.icon_zhanghaoxinxi, "账号信息"));
        itemBeans.add(new ItemBean(R.drawable.icon_wodeguanzhu, "我的关注"));
        itemBeans.add(new ItemBean(R.drawable.icon_shoucang, "我的收藏"));
        itemBeans.add(new ItemBean(R.drawable.icon_yijianfankui, "意见反馈"));
        return itemBeans;
    }
}
