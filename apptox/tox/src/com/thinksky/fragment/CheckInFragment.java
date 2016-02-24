package com.thinksky.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.adapter.CheckInAdapter;
import com.thinksky.info.CheckInfo;
import com.thinksky.myview.MyListView;
import com.thinksky.tox.R;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.InfoHelper;
import com.tox.WeiboApi;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckInFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout mCheckIn;
    private ImageView mReturn;
    private CheckInAdapter mCheckInAdapter = null;
    private Context context;
    private WeiboApi weiboApi = new WeiboApi();
    private ProgressBar mProgressBar;
    private CheckInFragmentCallBack mCheckInFrsgmentCallBack;
    private KJBitmap kjBitmap;
    private View view;
    private List<CheckInfo> mList = new ArrayList<CheckInfo>();
    private MyJson myJson = new MyJson();
    private MyListView mListView;
    private TextView mCheckInfo, mToast, mTimeNow, mCheckInText;
    private LinearLayout mMyCheckInfoShow;
    private boolean firstLoadFlag = true;
    private CheckInfo myCheckInfo;

    public CheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_check_in, null);
        context = view.getContext();
        kjBitmap = KJBitmap.create();
        initView();
        initRequest();
        return view;
    }

    private void initView() {
        mCheckInText = (TextView) view.findViewById(R.id.checkIn_text);
        mListView = (MyListView) view.findViewById(R.id.CheckIn_List);
        mCheckInfo = (TextView) view.findViewById(R.id.MyCheckInfo);
        mTimeNow = (TextView) view.findViewById(R.id.CheckIn_TimeNow);
        mToast = (TextView) view.findViewById(R.id.CheckToast);
        mCheckIn = (RelativeLayout) view.findViewById(R.id.checkIn);
        mReturn = (ImageView) view.findViewById(R.id.Menu);
        mReturn.setOnClickListener(this);
        mCheckIn.setOnClickListener(this);
        mMyCheckInfoShow = (LinearLayout) view.findViewById(R.id.MyCheckInfoShow);
        mProgressBar = new ProgressBar(context);
        mCheckInAdapter = new CheckInAdapter(context, mList);
        mListView.setAdapter(mCheckInAdapter);
        mTimeNow.setText(BaseFunction.getTimeNow());
        mListView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {

                weiboApi.setHandler(handler);
                weiboApi.getCheckRankDate();
            }

        });
    }

    private void initRequest() {
        weiboApi.setHandler(handler);
        weiboApi.getCheckRankDate();
        if (BaseFunction.isLogin()) {
            Log.e(">>>>>>>", "已经登入");
            //TODO 检查是否签到
            WeiboApi weiboApi1 = new WeiboApi();
            weiboApi1.setHandler(handlerMyCheckInfo);
            weiboApi1.getCheckInfo();
        }
    }

    @Override
    public void onClick(View v) {
        int mID = v.getId();
        switch (mID) {
            case R.id.Menu:
                getActivity().finish();
                break;
            case R.id.checkIn:
                if (BaseFunction.isLogin()) {
                    if (myCheckInfo != null && myCheckInfo.getIsChecked() == 0) {
                        checkIn();
                    }
                }
                else  {
                    Toast.makeText(context, "未登陆", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    Handler handlerMyCheckInfo = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String result = (String) msg.obj;
                myCheckInfo = myJson.getMyCheckInfo(result);
                mCheckInfo.setText("连签" + myCheckInfo.getCon_num() + "天,累签" + myCheckInfo.getTotal_num() + "天,超" + myCheckInfo.getOver_tare() + "的用户");
                if (myCheckInfo.getIsChecked() == 1) {
                    mCheckIn.setClickable(false);
                    mToast.setText("今日已签到");
                    mCheckInText.setText("已签到");
                } else {
                    mToast.setText("今日未签到");
                    mCheckIn.setClickable(true);
                }
                mMyCheckInfoShow.setVisibility(View.VISIBLE);
            } else {
                InfoHelper.ShowMessageInfo(context, msg.what);
            }
        }
    };

    private void checkIn() {
        if (BaseFunction.isLogin()) {
            WeiboApi weiboApi2 = new WeiboApi();
            weiboApi2.setHandler(checkHandler);
            weiboApi2.CheckIn();
        } else {
            Toast.makeText(context, "请先登陆", Toast.LENGTH_LONG).show();
        }
    }

    Handler checkHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String result = (String) msg.obj;
                CheckInfo checkInfo = myJson.getMyCheckInfo(result);
                mCheckInfo.setText("连签" + checkInfo.getCon_num() + "天,累签" + checkInfo.getTotal_num() + "天,超" + checkInfo.getOver_tare() + "的用户");
                mCheckInfo.setVisibility(View.VISIBLE);
                mCheckInText.setText("已签到");
                mToast.setText("签到成功");
                mCheckIn.setClickable(false);
                firstLoadFlag = false;
                initRequest();
            } else {
                InfoHelper.ShowMessageInfo(context, msg.what);
              /*  Toast.makeText(context,"已经签到",Toast.LENGTH_LONG).show();
                mCheckInText.setText("已签到");
                mCheckIn.setClickable(false);*/
            }
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mList.removeAll(mList);
                String result = (String) msg.obj;
                Log.d("输出签到信息>>>>>",result);
                List<CheckInfo> newList = myJson.getCheckRank(result);
                mListView.setVisibility(View.VISIBLE);
                for (CheckInfo info : newList) {
                    mList.add(info);
                }
            } else {
            }
            mListView.onRefreshComplete();
            mCheckInAdapter.notifyDataSetChanged();
        }
    };

    public void setCallBack(CheckInFragmentCallBack mCheckInFragmentCallBack) {
        this.mCheckInFrsgmentCallBack = mCheckInFragmentCallBack;
    }

    public interface CheckInFragmentCallBack {
        public void callback(int flag);
    }


}