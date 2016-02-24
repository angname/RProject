package com.thinksky.tox;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.fragment.MyNewsFragment;

import java.util.ArrayList;

/**
 * 自己的咨询列表
 * Created by Administrator on 2015/7/24 0024.
 */
public class NewsMeActivity extends FragmentActivity implements View.OnClickListener{

    private int activityCloseEnterAnimation;
    private int activityCloseExitAnimation;
    private Context mContext;
    ImageView backMenu,newsPost;
    PagerSlidingTabStrip myNewsTabs;
    ViewPager myNewsPager;
    Message message;
    ArrayList<MyNewsTitle> titleLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_news);
        mContext = NewsMeActivity.this;
        exitAnim();
        intView();
        titleLists = getTitleList();
        myNewsPager.setAdapter(new MyNewsFragmentAdapter(getSupportFragmentManager(),titleLists));
        myNewsTabs.setViewPager(myNewsPager);
    }

    /**
     * 初始化Activity
     */
    public void intView(){
        backMenu = (ImageView) findViewById(R.id.back_menu);
        newsPost = (ImageView) findViewById(R.id.news_post);
        myNewsTabs = (PagerSlidingTabStrip) findViewById(R.id.my_news_tabs);
        myNewsPager = (ViewPager) findViewById(R.id.my_news_pager);
        backMenu.setOnClickListener(this);
        newsPost.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.back_menu:
                finish();
                break;
            case R.id.news_post:
                Intent postNewsIntent = new Intent(mContext,PostNewsActivity.class);
                startActivity(postNewsIntent);
                break;
            default:
                break;
        }
    }

    private class MyNewsFragmentAdapter extends FragmentStatePagerAdapter{

        private MyNewsFragment fragment;
        private ArrayList<MyNewsTitle> titleList;

        public MyNewsFragmentAdapter(FragmentManager fm,ArrayList<MyNewsTitle> list) {
            super(fm);
            this.titleList = list;
        }

        @Override
        public Fragment getItem(int position) {
            fragment = new MyNewsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("status",titleList.get(position).getId());
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position).getTitle();
        }
    }

    @Override
    public void finish() {
        super.finish();
        //解决Activity退出动画无效的问题
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    /**
     * 解决Activity退出动画无效的问题
     */
    public void exitAnim(){
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[] {android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
        activityStyle.recycle();
    }

    /**
     * 创建选项卡list
     */
    private ArrayList<MyNewsTitle> getTitleList(){
        MyNewsTitle newsTitle;
        ArrayList<MyNewsTitle> titlesList = new ArrayList<MyNewsTitle>();
        newsTitle = new MyNewsTitle();
        newsTitle.setTitle("全部");
        newsTitle.setId("0");
        titlesList.add(newsTitle);
        newsTitle = new MyNewsTitle();
        newsTitle.setTitle("已审核");
        newsTitle.setId("1");
        titlesList.add(newsTitle);
        newsTitle = new MyNewsTitle();
        newsTitle.setTitle("待审核");
        newsTitle.setId("2");
        titlesList.add(newsTitle);
        newsTitle = new MyNewsTitle();
        newsTitle.setTitle("审核未通过");
        newsTitle.setId("-1");
        titlesList.add(newsTitle);
        return titlesList;
    }

    /**
     * 选项卡对象
     */
    private class MyNewsTitle{

        private String title;
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}