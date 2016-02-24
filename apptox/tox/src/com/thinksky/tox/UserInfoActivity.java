package com.thinksky.tox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.adapter.WeiboAdapter;
import com.thinksky.anim3d.RoundBitmap;
import com.thinksky.info.UserInfo;
import com.thinksky.info.WeiboInfo;
import com.thinksky.myview.MyDetailsListView;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ToastHelper;
import com.tox.Url;
import com.tox.UserApi;
import com.tox.WeiboApi;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends Activity {

    private UserInfo info = null;
    private ImageView mUserRevise, mUserMore, mUserCamera;
    private LinearLayout mBrief, mQiushi;
    private LinearLayout mUserBrief, mUserWeibo;
    private RelativeLayout mBack,mChangeMsg;
    private Boolean myflag = true;
    private RoundBitmap roundBitmap = new RoundBitmap();
    private LoadImg loadImgHeadImg;
    private MyJson myJson = new MyJson();
    private List<WeiboInfo> list = new ArrayList<WeiboInfo>();
    private WeiboAdapter mAdapter = null;
    private Button ListBottem = null;
    private Button mLogout = null;
    private Button followBtn = null;
    private int mStart = 1;
    private int mEnd = 5;
    private String url = null;
    private boolean flag = true;
    private boolean loadflag = false;
    private boolean listBottemFlag = true;
    private MyDetailsListView Detail_List;
    private LinearLayout Detail__progressBar, mLinearLayout, load_progressBar;
    private RelativeLayout Detail_CommentsNum;
    private UserApi userApi = new UserApi();
    private TextView UserName, UserTitle, UserTime, UserWriter, UserFollowing, UserFans, UserEmail, UserScore;
    private WeiboApi mWeiboApi = new WeiboApi();
    private FinalBitmap finalBitmap;
    private String mhotUrl = Url.WEIBO;
    private WeiboApi weiboApi = new WeiboApi();
    private SharedPreferences sp;
    private String userUid;
    private boolean followFlag;
    private RelativeLayout progress;
    UserApi userApi2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);
        //记录当前activity以备下一个activity调用
        finalBitmap = FinalBitmap.create(this);
        finalBitmap.configMemoryCacheSize(20);
        finalBitmap.configBitmapLoadThreadSize(15);
        loadImgHeadImg = new LoadImg(this);
        userUid = getIntent().getExtras().getString("userUid");
        Url.activityFrom = userUid + "UserInfoActivity";

        sp = getSharedPreferences("userInfo", 0);
        initView();
        if (!userUid.equals(Url.USERID)){
            mChangeMsg.setVisibility(View.GONE);
        }
        userApi2 = new UserApi(followHandler);
        //加载用户数据
//        if (!Url.SESSIONID.equalsIgnoreCase("")) {
        progress= (RelativeLayout) findViewById(R.id.progress);
        //7秒后如果还没获取到用户信息，自动关闭并提示网络环境
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progress.getVisibility()==View.VISIBLE){
                    progress.setVisibility(View.GONE);
                    ToastHelper.showToast("网络连接状态不稳定",UserInfoActivity.this);
                }
            }
        },7000);
        userApi.setHandler(hand);
        userApi.getUserInfo(userUid);
        //判断是否是登陆者信息，如果是就保存当前页面以重复使用
        if (!sp.getString("avatar", "").equalsIgnoreCase("") && userUid.equals(Url.USERID)) {
            BaseFunction.showImage(this, mUserCamera, sp.getString("avatar", ""), loadImgHeadImg, Url.IMGTYPE_HEAD);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //删除微博后刷新listview
        if (Url.activityFrom.equals("DeleteWeiBoActivity")) {
            Url.activityFrom = "";
            mStart = 1;
            list.clear();
            mWeiboApi.setHandler(weiboHand);
            mWeiboApi.listAllWeibo(mStart, userUid);
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        mChangeMsg= (RelativeLayout) findViewById(R.id.changeMsg);
        mBrief = (LinearLayout) findViewById(R.id.Brief);
        mQiushi = (LinearLayout) findViewById(R.id.Weibo);
        mUserBrief = (LinearLayout) findViewById(R.id.UserBrief);
        mUserWeibo = (LinearLayout) findViewById(R.id.UserQiushi);
        mBack = (RelativeLayout) findViewById(R.id.UserBack);
        mUserRevise = (ImageView) findViewById(R.id.UserRevise);
        mUserCamera = (ImageView) findViewById(R.id.UserCamera);
        UserName = (TextView) findViewById(R.id.UserName);
        UserTitle = (TextView) findViewById(R.id.UserTitle);
        UserTime = (TextView) findViewById(R.id.Ctime);
        UserWriter = (TextView) findViewById(R.id.userinfo);
        UserFollowing = (TextView) findViewById(R.id.UserFollowing_num);
        UserFans = (TextView) findViewById(R.id.fans_numer);
        UserEmail = (TextView) findViewById(R.id.email);
        UserScore = (TextView) findViewById(R.id.Score);
        Detail_List = (MyDetailsListView) findViewById(R.id.Detail_List);
        Detail__progressBar = (LinearLayout) findViewById(R.id.Detail__progressBar);
        Detail_CommentsNum = (RelativeLayout) findViewById(R.id.usernoashamed);
        mLogout = (Button) findViewById(R.id.Logout);
        followBtn = (Button) findViewById(R.id.follow_btn);

        mAdapter = new WeiboAdapter(this, list, finalBitmap, KJBitmap.create());
        ListBottem = new Button(UserInfoActivity.this);
        ListBottem.setText("点击加载更多");
        ListBottem.setBackgroundColor(getResources().getColor(R.color.mycolor));
        ListBottem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag && listBottemFlag) {
                    mWeiboApi.setHandler(weiboHand);
                    mWeiboApi.listAllWeibo(mStart, userUid);
                    listBottemFlag = false;
                } else if (!listBottemFlag)
                    Toast.makeText(UserInfoActivity.this, "正在加载中...", Toast.LENGTH_LONG).show();
            }
        });

        Detail_List.addFooterView(ListBottem, null, false);

        ListBottem.setVisibility(View.GONE);

        Detail_List.setAdapter(mAdapter);
        mWeiboApi.setHandler(weiboHand);
        mWeiboApi.listAllWeibo(mStart, userUid);

        MyOnClick my = new MyOnClick();
        Detail_List.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(UserInfoActivity.this,
                        WeiboDetailActivity.class);
                Bundle bund = new Bundle();
                bund.putSerializable("WeiboInfo", list.get(arg2));
                intent.putExtra("value", bund);
                startActivity(intent);
            }
        });
        mBrief.setOnClickListener(my);
        mQiushi.setOnClickListener(my);
        mUserRevise.setOnClickListener(my);
        mBack.setOnClickListener(my);
        mUserCamera.setOnClickListener(my);
        mLogout.setOnClickListener(my);
        followBtn.setOnClickListener(my);
        mChangeMsg.setOnClickListener(my);
        mUserCamera.setClickable(false);
        mChangeMsg.setClickable(false);
    }

    private class MainListOnItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            Intent intent = new Intent(UserInfoActivity.this, WeiboDetailActivity.class);
            Bundle bund = new Bundle();
            Log.e("被点击啦啦啦啦啦", arg2+"");
            bund.putSerializable("WeiboInfo", list.get(arg2 - 1));
            intent.putExtra("value", bund);
            startActivity(intent);
        }
    }

    class MyOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int mId = v.getId();
            switch (mId) {
                case R.id.Brief:
                    myflag = true;
                    initCont(myflag);
                    break;
                case R.id.Weibo:
                    myflag = false;
                    initCont(myflag);
                    break;
                case R.id.UserBack:
                    Intent ii=new Intent();
                    if (info != null) {
                        ii.putExtra("Avater", info.getAvatar());
                        ii.putExtra("NickName", info.getNickname());
                        setResult(1, ii);
                    }
                        finish();
                    break;
                case R.id.UserRevise:
                    // Intent intent = new Intent(UserInfoActivity.this,.class);
                    // startActivity(intent);
                    break;
                case R.id.UserCamera:
                    if (userUid.equals(Url.USERID)) {
                        Intent intent = new Intent(UserInfoActivity.this,SetUserInfoActivity.class);
                        intent.putExtra("info", Url.MYUSERINFO.getAvatar());
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("inf", info);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    }
                    break;
                case R.id.changeMsg:
                    if (userUid.equals(Url.USERID)) {
                        Intent intent = new Intent(UserInfoActivity.this,SetUserInfoActivity.class);
                        intent.putExtra("info", Url.MYUSERINFO.getAvatar());
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("inf", info);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    }
                    break;
                case R.id.Logout:

                    new AlertDialog.Builder(UserInfoActivity.this)
                            .setTitle("确认")
                            .setMessage("确定退出登入")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clearUserInfo();
                                    UserApi userApi1 = new UserApi();
                                    userApi1.setHandler(logoutHandler);
                                    userApi1.logout();
                                    Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
                //关注按钮事件
                case R.id.follow_btn:
                    if (!userApi.getSeesionId().equals("")) {
                        if (followFlag) {
                            //取消关注的操作
                            new AlertDialog.Builder(UserInfoActivity.this)
                                    .setMessage("确定取消关注？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            userApi2.endFollow(userUid);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        } else {
                            //加关注的操作
                            userApi2.doFollow(userUid);
                        }
                    }else {
                        ToastHelper.showToast("请登陆后操作",UserInfoActivity.this);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent ii=new Intent();
        ii.putExtra("Avater",info.getAvatar());
        ii.putExtra("NickName",info.getNickname());
        setResult(1,ii);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==1){
            info= (UserInfo) data.getSerializableExtra("inff");
            Log.d("66666",info.getNickname());
            UserName.setText(info.getNickname());
            UserEmail.setText(info.getEmail());
            mUserCamera.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(info.getAvatar())));
        }
    }

    //加关注操作
    Handler followHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (followFlag){
                        ToastHelper.showToast("取消关注成功",UserInfoActivity.this);
                        followBtn.setBackgroundColor(Color.parseColor("#3BAFDA"));
                        followFlag = false;
                        followBtn.setText("添加关注");
                    }else {
                        ToastHelper.showToast("关注成功", UserInfoActivity.this);
                        followBtn.setBackgroundColor(Color.parseColor("#3BAFC7"));
                        followFlag = true;
                        followBtn.setText("取消关注");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Handler logoutHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };

    private void clearUserInfo() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("session_id", "");
        Url.MYUSERINFO = null;
        Url.LASTPOSTTIME = 0;
        Url.SESSIONID = "";
        Url.USERID = "";
        editor.commit();
    }

    //微博列表
    Handler weiboHand = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String result = (String) msg.obj;
                List<WeiboInfo> newList = myJson.getWeiboList(result);
                if (newList != null) {
                    if (newList.size() == 10) {
                        Detail_List.setVisibility(View.VISIBLE);
                        ListBottem.setVisibility(View.VISIBLE);
                        mStart += 1;
                        mEnd += 5;
                    } else if (newList.size() == 0) {
                        if (list.size() == 0)
                            Detail_CommentsNum.setVisibility(View.VISIBLE);
                        ListBottem.setVisibility(View.GONE);
                        Detail_List.setVisibility(View.GONE);
                    } else {
                        Detail_List.setVisibility(View.VISIBLE);
                        ListBottem.setVisibility(View.GONE);
                    }
                    for (WeiboInfo info : newList) {
                        list.add(info);
                    }
                    listBottemFlag = true;
                } else {

                    Detail_List.setVisibility(View.GONE);
                    Detail_CommentsNum.setVisibility(View.VISIBLE);
                }
            }
            Detail__progressBar.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
            loadflag = true;
        }
    };


    @SuppressLint("HandlerLeak")
    Handler hand = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(UserInfoActivity.this, "请求失败，服务器故障", Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 100) {
                Toast.makeText(UserInfoActivity.this, "服务器无响应", Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 0) {
                String result = (String) msg.obj;
                Log.e("result>>>>>>>>",result);
                List<WeiboInfo> newList;
                if (result != null) {
                    //获取数据成功
                    Log.e("加载 userInfo", "u");
                    Url.MYUSERINFO = myJson.getUserAllInfo(result);
                    info=Url.MYUSERINFO;
                    Log.d("Url.MYUSERINFO.Avatar()",Url.MYUSERINFO.getAvatar());
                    BaseFunction.showImage(UserInfoActivity.this, mUserCamera, Url.MYUSERINFO.getAvatar(), loadImgHeadImg, Url.IMGTYPE_WEIBO);
                    if (userUid.equals(Url.USERID)) {
                        saveUserInfoToNative();
                    }
                    createUserInfo(Url.MYUSERINFO);
                }
            }
            mChangeMsg.setClickable(true);
            mUserCamera.setClickable(true);
            progress.setVisibility(View.GONE);
        }
    };


    //保存用户信息至本地
    private void saveUserInfoToNative() {

        if(Url.MYUSERINFO.getAvatar().equals(BaseFunction.getSharepreference("avatar",UserInfoActivity.this,Url.SharedPreferenceName))){
            BaseFunction.showImage(this, mUserCamera, Url.MYUSERINFO.getAvatar(), loadImgHeadImg, Url.IMGTYPE_HEAD);
        }
        SharedPreferences sp = this.getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", Url.MYUSERINFO.getNickname());
        editor.putString("avatar", Url.MYUSERINFO.getAvatar());
        Log.e("我要保存", Url.MYUSERINFO.getAvatar());
        editor.commit();
    }

    //切换简介与微博的tab
    private void initCont(Boolean myflag) {
        if (myflag) {
            mBrief.setBackgroundResource(R.drawable.cab_background_top_light);
            mQiushi.setBackgroundResource(R.drawable.ab_stacked_solid_light);
            mUserBrief.setVisibility(View.VISIBLE);
            mUserWeibo.setVisibility(View.GONE);
        } else {
            mBrief.setBackgroundResource(R.drawable.ab_stacked_solid_light);
            mQiushi.setBackgroundResource(R.drawable.cab_background_top_light);
            mUserBrief.setVisibility(View.GONE);
            mUserWeibo.setVisibility(View.VISIBLE);
        }
    }

    private void createUserInfo(UserInfo userInfo) {
        UserScore.setText(userInfo.getScore());
        UserEmail.setText(userInfo.getEmail());
        UserFans.setText(userInfo.getFans());
        UserFollowing.setText(userInfo.getFollowing());
        UserTime.setText(userInfo.getCtime());
        UserName.setText(userInfo.getNickname());
        UserTitle.setText(userInfo.getTitle());
        Log.d("userInfo.getAvatar()",userInfo.getAvatar());
//        mUserCamera.setImageBitmap(userInfo.getAvatar());
        if (!userUid.equals(Url.USERID) ) {
            if (userInfo.getIsFollow().equals("0")){
                followFlag = false;
                followBtn.setText("添加关注");
                followBtn.setVisibility(View.VISIBLE);
            }else{
                followFlag = true;
                followBtn.setText("取消关注");
                followBtn.setVisibility(View.VISIBLE);
            }
        }else {
            mLogout.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.GONE);
    }

    private void initUserInfo() {
        SharedPreferences sp = this.getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor = sp.edit();

    }

}
