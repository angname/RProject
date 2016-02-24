package com.thinksky.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.adapter.NearAdapter;
import com.thinksky.info.UserInfo;
import com.thinksky.myview.MyListView;
import com.thinksky.myview.MyListView.OnRefreshListener;
import com.thinksky.net.ThreadPoolUtils;
import com.thinksky.thread.HttpGetThread;
import com.thinksky.tox.R;
import com.thinksky.tox.UserInfoActivity;
import com.thinksky.utils.MyJson;
import com.tox.Url;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment implements OnClickListener {

    private NearFragmentCallBack mNearFragmentCallBack;
    private View view;
    private Context ctx;
    private LinearLayout mLinearLayout, load_progressBar;
    private LinearLayout Near_Back;
    private ImageView Near_Seting, Near_More;
    private TextView HomeNoValue;
    private MyListView myListView;
    private NearAdapter mAdapter = null;
    private MyJson myJson = new MyJson();
    private List<UserInfo> list = new ArrayList<UserInfo>();
    private Button ListBottem = null;
    private int mStart = 0;
    private int mEnd = 5;
    private String url = null;
    private boolean flag = true;
    private boolean loadflag = false;
    private boolean listBottemFlag = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_near, null);
        ctx = view.getContext();
        myListView = new MyListView(ctx);
        //initView();
        return view;
    }

    private void initView() {
        load_progressBar = (LinearLayout) view
                .findViewById(R.id.load_progressBar);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.HomeGroup);
        myListView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myListView.setDivider(null);
        mLinearLayout.addView(myListView);

        HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
        Near_Back.setOnClickListener(this);
        Near_Seting.setOnClickListener(this);
        Near_More.setOnClickListener(this);
        mAdapter = new NearAdapter(ctx, list);
        ListBottem = new Button(ctx);
        ListBottem.setText("点击加载更多");

        ListBottem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag && listBottemFlag) {
                    url = Url.NEAR + "start=" + mStart + "&end=" + mEnd;
                    ThreadPoolUtils.execute(new HttpGetThread(hand, url));
                    listBottemFlag = false;
                } else if (!listBottemFlag)
                    Toast.makeText(ctx, "正在加载中...", Toast.LENGTH_LONG).show();
            }
        });
        myListView.addFooterView(ListBottem, null, false);
        ListBottem.setVisibility(View.GONE);
        myListView.setAdapter(mAdapter);
        myListView.setOnItemClickListener(new MainListOnItemClickListener());
        url = Url.NEAR + "start=" + mStart + "&end=" + mEnd;
        ThreadPoolUtils.execute(new HttpGetThread(hand, url));
        myListView.setonRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                if (loadflag == true) {
                    mStart = 0;
                    mEnd = 5;
                    url = Url.NEAR + "start=" + mStart + "&end=" + mEnd;
                    ListBottem.setVisibility(View.GONE);
                    ThreadPoolUtils.execute(new HttpGetThread(hand, url));
                    loadflag = false;
                } else {
                    Toast.makeText(ctx, "正在加载中，请勿重复刷新",  Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onClick(View arg0) {
        int mID = arg0.getId();
        switch (mID) {
            case R.id.Menu:
                mNearFragmentCallBack.callback(R.id.Menu);
                break;
            case R.id.SendForum:
                mNearFragmentCallBack.callback(R.id.SendForum);
                break;
            default:
                break;
        }

    }

    private class MainListOnItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            Intent intent = new Intent(ctx, UserInfoActivity.class);
            Bundle bund = new Bundle();
            bund.putSerializable("UserInfo", list.get(arg2 - 1));
            intent.putExtra("value", bund);
            startActivity(intent);
        }
    }

    public void setCallBack(NearFragmentCallBack mNearFragmentCallBack) {
        this.mNearFragmentCallBack = mNearFragmentCallBack;
    }

    public interface NearFragmentCallBack {
        public void callback(int flag);
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(ctx, "找不到地址",  Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 100) {
                Toast.makeText(ctx, "传输失败",  Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null) {
                    List<UserInfo> newList = myJson.getNearUserList(result);
                    if (newList != null) {
                        Log.e("liuxiaowei", "newList=" + newList.size()
                                + "  list=" + list.size());
                        if (newList.size() == 5) {
                            ListBottem.setVisibility(View.VISIBLE);
                            mStart += 5;
                            mEnd += 5;
                        } else if (newList.size() == 0) {
                            if (list.size() == 0)
                                HomeNoValue.setVisibility(View.VISIBLE);
                            ListBottem.setVisibility(View.GONE);
                            Toast.makeText(ctx, "已经没有了...",  Toast.LENGTH_LONG).show();
                        } else {
                            ListBottem.setVisibility(View.GONE);
                        }
                        if (!loadflag) {
                            list.removeAll(list);
                        }
                        for (UserInfo info : newList) {
                            list.add(info);
                        }
                        listBottemFlag = true;
                    } else {
                        if (list.size() == 0)
                            HomeNoValue.setVisibility(View.VISIBLE);
                    }
                }
                mLinearLayout.setVisibility(View.VISIBLE);
                load_progressBar.setVisibility(View.GONE);
                myListView.onRefreshComplete();
                mAdapter.notifyDataSetChanged();
                loadflag = true;
            }
        }

        ;
    };
}
