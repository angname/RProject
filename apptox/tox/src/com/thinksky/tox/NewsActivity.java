package com.thinksky.tox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.fragment.NewsFragment;
import com.thinksky.info.NewsCategory;
import com.thinksky.utils.MyJson;
import com.tox.NewsApi;
import com.tox.ToastHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by HuJiaYu on 2015/7/7
 */
public class NewsActivity extends FragmentActivity implements View.OnClickListener{

    protected ImageView backMenu;
    protected ImageView newsPost,myNews;
    protected ViewPager newsPager;
    protected PagerSlidingTabStrip newsTabs;
    private LinearLayout newsBody,firstNavigationLine;
    protected RelativeLayout proBarLine;
    protected NewsApi newsApi;
    private Context context;
    private MyHandler mHandler = new MyHandler(NewsActivity.this);
    protected String id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news);
        context = NewsActivity.this;
        newsApi = new NewsApi(mHandler);
        newsApi.getNavigation(id);
        intView();
    }

    public void intView(){
        backMenu = (ImageView)findViewById(R.id.back_menu);
        newsPost = (ImageView)findViewById(R.id.news_post);
        myNews = (ImageView)findViewById(R.id.my_news);
        newsPager = (ViewPager)findViewById(R.id.news_pager);
        newsTabs = (PagerSlidingTabStrip)findViewById(R.id.news_tabs);
        newsBody = (LinearLayout)findViewById(R.id.newsBody);
        firstNavigationLine = (LinearLayout)findViewById(R.id.first_navigation_line);
        proBarLine = (RelativeLayout)findViewById(R.id.proBarLine);
        backMenu.setOnClickListener(this);
        newsPost.setOnClickListener(this);
        myNews.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID){
            case R.id.back_menu:
                finish();
                break;
            case R.id.my_news://我的资讯
                if (!newsApi.getSeesionId().equals("")) {
                    Intent myNewsIntent = new Intent(context, NewsMeActivity.class);
                    startActivity(myNewsIntent);
                }else {
                    ToastHelper.showToast("请登陆后操作",context);
                }
                break;
            case R.id.news_post://资讯投稿
                Intent postNewsIntent = new Intent(context,PostNewsActivity.class);
                startActivity(postNewsIntent);
                break;
            default:
                break;
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<NewsActivity> mActivityReference;
        private MyJson myJson;
        private ArrayList<NewsCategory> navigationLineList;
        private ArrayList<NewsCategory> secondList;

        MyHandler(NewsActivity activity) {
            mActivityReference = new WeakReference<NewsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            final NewsActivity activity = mActivityReference.get();
            myJson = new MyJson();
            if (activity != null) {
                switch (msg.what){
                    case 0:
                        String result = (String) msg.obj;
                        navigationLineList = myJson.getNewsNavigation(result);
                        if (navigationLineList.size() > 0) {
                            LinearLayout.LayoutParams textLpa= new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textLpa.setMargins(5,0,20,0);
                            for (int i = 0;i < navigationLineList.size();i++) {
                                TextView textView = new TextView(activity.context);
                                textView.setLayoutParams(textLpa);
                                textView.setTextSize(20);
                                textView.setPadding(5, 3, 5, 3);
                                textView.setTextColor(activity.context.getResources().getColor(R.color.black));
                                textView.setText(navigationLineList.get(i).getTitle());
                                textView.setTag(navigationLineList.get(i).getNewsSecond());
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    @SuppressWarnings(value = {"unchecked"})
                                    public void onClick(View v) {
                                        setBackColor(activity.firstNavigationLine);
                                        v.setBackgroundResource(R.color.post_time);
                                        secondList = (ArrayList<NewsCategory>) v.getTag();
                                        activity.newsPager.setAdapter(new NewsFragmentAdapter(activity.getSupportFragmentManager(), secondList));
                                        activity.newsTabs.setViewPager(activity.newsPager);
                                        activity.newsTabs.notifyDataSetChanged();
                                    }
                                });
                                activity.firstNavigationLine.addView(textView);
                                if (i == 0){
                                    textView.setBackgroundResource(R.color.post_time);
                                    activity.newsPager.setAdapter(new NewsFragmentAdapter(activity.getSupportFragmentManager(), navigationLineList.get(i).getNewsSecond()));
                                    activity.newsTabs.setViewPager(activity.newsPager);
                                }
                            }
                        }
                        activity.newsBody.setVisibility(View.VISIBLE);
//                        activity.proBarLine.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        }

        private class NewsFragmentAdapter extends FragmentStatePagerAdapter {

            private ArrayList<NewsCategory> secondList;
            private NewsFragment fragment;

            public NewsFragmentAdapter(FragmentManager fm,ArrayList<NewsCategory> secondList) {
                super(fm);
                this.secondList = secondList;
            }

            @Override
            public Fragment getItem(int position) {
                fragment = new NewsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("newsTitle",secondList.get(position));
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return secondList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {

                SpannableStringBuilder ssb = new SpannableStringBuilder(secondList.get(position).getTitle());
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);// 字体颜色设置
                ssb.setSpan(fcs, 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// 设置字体颜色
                return ssb;
            }
        }

        //清空导航标签背景色
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void setBackColor(LinearLayout layout){
            for (int i =0;i <layout.getChildCount();i++ ) {
                layout.getChildAt(i).setBackground(null);
            }
        }
    }
}