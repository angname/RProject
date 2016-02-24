package com.thinksky.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.thinksky.adapter.MyNewsListAdapter;
import com.thinksky.info.NewsListInfo;
import com.thinksky.myview.DotLoadView;
import com.thinksky.myview.MyListView;
import com.thinksky.tox.NewsDetailActivity;
import com.thinksky.tox.R;
import com.thinksky.utils.MyJson;
import com.tox.NewsApi;
import com.tox.ToastHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *我的资讯Fragment
 * Created by Administrator on 2015/7/24 0024.
 */
public class MyNewsFragment extends Fragment{

    private DotLoadView dotLoadBar;
    private MyListView myNewsListView;
    private Context mContext;
    private NewsApi newsApi;
    private MyHandler mHandler = new MyHandler(this);
    ArrayList<NewsListInfo> myNewsList;
    private int page = 1;
    String overdue ="0";
    String status;
    private Message message;
    private Boolean isUpdate = true;
    private Button listBottom;
    private ProgressBar mAddMoreProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        myNewsList = new ArrayList<NewsListInfo>();
        newsApi = new NewsApi(mHandler);
        status = getArguments().getString("status");
        message = new Message();
        message.what = 0x630;
        mHandler.sendMessage(message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_fragment,container,false);
        dotLoadBar = (DotLoadView)rootView.findViewById(R.id.dotLoadBar);
        myNewsListView = (MyListView)rootView.findViewById(R.id.news_listView);
        myNewsListView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                isUpdate = true;
                message = Message.obtain();
                message.what = 0x630;
                mHandler.sendMessage(message);
            }
        });
        myNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsListInfo newsListInfo = (NewsListInfo) parent.getItemAtPosition(position);
                if (newsListInfo != null && newsListInfo.getStatus().equals("1")) {
                    Intent tempIntent = new Intent(mContext, NewsDetailActivity.class);
                    tempIntent.putExtra("newsInfo", newsListInfo);
                    startActivity(tempIntent);
                }else {
                    ToastHelper.showToast("等待审核中",mContext);
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
        listBottom.setTextColor(getResources().getColor(R.color.black));
        listBottom.setTextSize(18);
        listBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myNewsListView.removeFooterView(listBottom);
                myNewsListView.addFooterView(mAddMoreProgressBar);
                page++;
                isUpdate = false;
                message = Message.obtain();
                message.what = 0x630;
                mHandler.sendMessage(message);
            }
        });
        myNewsListView.addFooterView(listBottom);
        return rootView;
    }

    private static class MyHandler extends Handler{

        private WeakReference<MyNewsFragment> mFragmentReference;
        private String result;
        private MyJson myJson;
        private MyNewsFragment mFragment;
        private MyNewsListAdapter mNewsListAdapter;

        MyHandler(MyNewsFragment fragment) {
            mFragmentReference = new WeakReference<MyNewsFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            mFragment = mFragmentReference.get();
            myJson = new MyJson();
            switch (msg.what){
                case 0x630:
                    mFragment.newsApi.getMyNewsList(mFragment.page,mFragment.overdue,mFragment.status);
                    break;
                case 0:
                    result = (String)msg.obj;
                    if (mFragment.isUpdate){
                        mFragment.myNewsList.clear();
                        mFragment.myNewsList.addAll(myJson.getMyNewsList(result));
                        mNewsListAdapter = new MyNewsListAdapter(mFragment.mContext,mFragment.myNewsList);
                        mFragment.myNewsListView.setAdapter(mNewsListAdapter);
                        //结束下拉刷新
                        mFragment.myNewsListView.onRefreshComplete();
                        mFragment.myNewsListView.setVisibility(View.VISIBLE);//显示资讯列表
                    }else {
                        mFragment.myNewsList.addAll(myJson.getNewsList(result));
                        mFragment.myNewsListView.removeFooterView(mFragment.mAddMoreProgressBar);
                        mNewsListAdapter.notifyDataSetChanged();
                        mFragment.myNewsListView.addFooterView(mFragment.listBottom);
                    }
                    if (mFragment.myNewsList.size() == 0){
                        mFragment.listBottom.setText("没有资讯");
                    }
                    mFragment.dotLoadBar.setVisibility(View.GONE);//隐藏fragment的加载效果
                    break;
                case 401:
                    ToastHelper.showToast("未登陆，请登陆后操作",mFragment.mContext);
                    mFragment.dotLoadBar.setVisibility(View.GONE);
                    break;
                case 404:
                    ToastHelper.showToast("数据请求失败",mFragment.mContext);
                    break;
                default:
                    break;
            }
        }
    }
}
