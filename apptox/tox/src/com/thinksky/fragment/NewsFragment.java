package com.thinksky.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.thinksky.adapter.NewsListAdapter;
import com.thinksky.info.NewsCategory;
import com.thinksky.info.NewsListInfo;
import com.thinksky.myview.DotLoadView;
import com.thinksky.myview.MyListView;
import com.thinksky.tox.NewsDetailActivity;
import com.thinksky.tox.R;
import com.thinksky.utils.MyJson;
import com.tox.NewsApi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class NewsFragment extends Fragment {
    
    private Context mContext;
    protected NewsCategory newsTitle;
    private MyListView newsListView;
    private Button listBottom;
    private ProgressBar mAddMoreProgressBar;
    private RelativeLayout fistProBarLine;//activity里面的控件，需要在本fragment里面做操作
    private DotLoadView dotLoadBar;
    private MyHandler mHandler = new MyHandler(NewsFragment.this);
    protected NewsApi newsApi;
    protected String category;
    private Message message;
    private ArrayList<NewsListInfo> newsListInfos;
    private Boolean isUpdate = true;
    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsApi = new NewsApi(mHandler);
        newsListInfos = new ArrayList<NewsListInfo>();
        mContext = getActivity();
        newsTitle = (NewsCategory)getArguments().get("newsTitle");
        category = newsTitle.getId();
        message = new Message();
        message.what = 0x620;
        mHandler.sendMessage(message);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_fragment, container, false);
        newsListView = (MyListView)rootView.findViewById(R.id.news_listView);
        dotLoadBar = (DotLoadView)rootView.findViewById(R.id.dotLoadBar);
        fistProBarLine = (RelativeLayout) getActivity().findViewById(R.id.proBarLine);
        newsListView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                isUpdate = true;
                message = Message.obtain();
                message.what = 0x620;
                mHandler.sendMessage(message);
            }
        });
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) != null) {
                    Intent tempIntent = new Intent(mContext, NewsDetailActivity.class);
                    tempIntent.putExtra("newsInfo", (NewsListInfo) parent.getItemAtPosition(position));
                    startActivity(tempIntent);
                }
            }
        });
        //设置底部加载
        mAddMoreProgressBar = new ProgressBar(mContext);
        mAddMoreProgressBar.setIndeterminate(false);
        mAddMoreProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bg));
        listBottom = new Button(mContext);
        listBottom.setBackgroundColor(getResources().getColor(R.color.mycolor));
        listBottom.setText("点击加载更多");
        listBottom.setTextSize(18);
        listBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsListView.removeFooterView(listBottom);
                newsListView.addFooterView(mAddMoreProgressBar);
                page++;
                isUpdate = false;
                message = Message.obtain();
                message.what = 0x620;
                mHandler.sendMessage(message);
            }
        });
        newsListView.addFooterView(listBottom);
        return rootView;
    }

    /**
     * Handler
     */
    private static class MyHandler extends Handler {

        private WeakReference<NewsFragment> mFragmentReference;
        private MyJson myJson;
        private NewsFragment mFragment;
        private NewsListAdapter newsListAdapter;

        MyHandler(NewsFragment fragment) {
            mFragmentReference = new WeakReference<NewsFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            mFragment = mFragmentReference.get();
            myJson = new MyJson();
            if (mFragment != null) {
                switch (msg.what){
                    case 0x620:
                        mFragment.newsApi.getNewsList(mFragment.category,mFragment.page);
                        break;
                    case 0:
                        String result = (String) msg.obj;
                        if (mFragment.isUpdate){
                            mFragment.newsListInfos.clear();
                            mFragment.newsListInfos.addAll(myJson.getNewsList(result));
                            newsListAdapter = new NewsListAdapter(mFragment.mContext,mFragment.newsListInfos);
                            mFragment.newsListView.setAdapter(newsListAdapter);
                            //结束下拉刷新
                            mFragment.newsListView.onRefreshComplete();
                            mFragment.newsListView.setVisibility(View.VISIBLE);//显示资讯列表
                        }else {
                            mFragment.newsListInfos.addAll(myJson.getNewsList(result));
                            mFragment.newsListView.removeFooterView(mFragment.mAddMoreProgressBar);
                            newsListAdapter.notifyDataSetChanged();
                            mFragment.newsListView.addFooterView(mFragment.listBottom);
                        }
                        //加载效果
                        mFragment.dotLoadBar.setVisibility(View.GONE);//隐藏fragment的加载效果
                        mFragment.fistProBarLine.setVisibility(View.GONE);//隐藏activity的加载效果
                        break;
                    default:
                        break;
                }
            }
        }
    }
}