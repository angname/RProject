package com.thinksky.tox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.thinksky.redefine.CircleImageView;
import com.tox.GroupApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/4 0004.
 */
public class GroupFloorReplyActivity extends Activity implements View.OnClickListener{

    private Context mContext;
    private GroupApi groupApi;
    private int post_id;
    private int ReplyId;
    private int page = 1;
    HashMap<String,String> floorInfoMap;
    ScrollView bodyScroView;
    ImageView backMenu;
    TextView roomNumber;
    CircleImageView floorUserHead;
    TextView floorUserName;
    TextView ReplyTime;
    TextView ReplyContent;
    ImageView replyForFloorBtn;
    LinearLayout replyBox;
    LinearLayout isHost;

    //加载更多模块
    LinearLayout loadBar;
    TextView loadMoreText;
    ProgressBar loadMorePro;

    KJBitmap kjBitmap;
    LinearLayout lzlReplyBody;
    private EditText replyEdtext;
    private TextView sendReplyBtn;

    @Override
    @SuppressWarnings(value = {"unchecked"})
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_floor_reply);

        mContext = GroupFloorReplyActivity.this;
        kjBitmap = KJBitmap.create();
        groupApi = new GroupApi();
        groupApi.setHandler(mHandler);
        floorInfoMap = (HashMap<String,String>)getIntent().getExtras().getSerializable("floorInfo");
        post_id = Integer.parseInt(floorInfoMap.get("post_id"));
        ReplyId = Integer.parseInt(floorInfoMap.get("id"));
        Log.e("floorInfoMap>>>>>>>>>>>",floorInfoMap.toString());

        backMenu = (ImageView) findViewById(R.id.back_menu);
        roomNumber = (TextView) findViewById(R.id.room_number);
        bodyScroView = (ScrollView) findViewById(R.id.floor_body);
        floorUserHead = (CircleImageView) findViewById(R.id.floor_UserHead);
        floorUserName = (TextView) findViewById(R.id.floor_Username);
        isHost = (LinearLayout) findViewById(R.id.is_host);
        ReplyTime = (TextView) findViewById(R.id.floor_toReply_time);
        replyForFloorBtn = (ImageView) findViewById(R.id.reply_floor_btn);
        ReplyContent = (TextView) findViewById(R.id.floor_reply_content);
        lzlReplyBody = (LinearLayout) findViewById(R.id.lzl_reply_body);
        //加载更多模块
        loadBar = (LinearLayout) findViewById(R.id.load_more_btn);
        loadMoreText = (TextView) findViewById(R.id.load_more_text);
        loadMorePro = (ProgressBar) findViewById(R.id.load_more_pro);
        //回复模块
        replyBox = (LinearLayout) findViewById(R.id.reply_box);
        replyEdtext = (EditText) findViewById(R.id.reply_editText);
        sendReplyBtn = (TextView) findViewById(R.id.send_reply_btn);

        backMenu.setOnClickListener(this);
        floorUserHead.setOnClickListener(this);
        replyForFloorBtn.setOnClickListener(this);
        loadBar.setOnClickListener(this);
        sendReplyBtn.setOnClickListener(this);

        bodyScroView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                replyBox.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(replyEdtext.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        //初始化页面
        InitFloorView(floorInfoMap);


        //开启楼中楼回复列表数据获取线程
        new LzlReplyThread().start();
    }

    //初始化页面函数
    private void InitFloorView(HashMap<String,String> floorInfoMap){

        String floorNumber = floorInfoMap.get("floor_id");
        roomNumber.setText(floorNumber + "楼");
        kjBitmap.display(floorUserHead, floorInfoMap.get("user_logo"));
        floorUserName.setText(floorInfoMap.get("nickname"));
        if (floorInfoMap.get("is_landlord").equals("1")) {
            isHost.setVisibility(View.VISIBLE);
        }
        ReplyTime.setText("第" + floorNumber + "楼 " + floorInfoMap.get("create_time"));
        ReplyContent.setText(floorInfoMap.get("content"));

        //发表回复文本框的事件监听器
        replyEdtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    sendReplyBtn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    sendReplyBtn.setTextColor(Color.WHITE);
                } else {
                    sendReplyBtn.setBackgroundResource(R.drawable.border);
                    sendReplyBtn.setTextColor(Color.parseColor("#A9ADB0"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    sendReplyBtn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    sendReplyBtn.setTextColor(Color.WHITE);
                } else {
                    sendReplyBtn.setBackgroundResource(R.drawable.border);
                    sendReplyBtn.setTextColor(Color.parseColor("#A9ADB0"));
                }
            }
        });
//        判断页面是否需要自动打开回复栏
        if (floorInfoMap.get("keyLock").equals("1")){
            //打开软键盘
            replyBox.setVisibility(View.VISIBLE);
            replyEdtext.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID){
            case R.id.back_menu:
                finish();
                break;
            //加载更多回复
            case R.id.load_more_btn:
                loadMoreText.setText("正在加载...");
                loadMorePro.setVisibility(View.VISIBLE);
                new LzlReplyThread().start();
                break;
            case R.id.reply_floor_btn:
                if (!groupApi.getSeesionId().equals("")) {
                    replyEdtext.setText("");
                    replyBox.setVisibility(View.VISIBLE);
                    //打开软键盘并自动获取焦点
                    groupApi.openKeyBoard(mContext, replyEdtext);
                }else {
                    ToastHelper.showToast("请登录后操作",mContext);
                }
                break;
            case R.id.send_reply_btn:
                if (replyEdtext.getText().length() > 0 && !"".equals(replyEdtext.getText().toString().trim())) {
                    sendReplyBtn.setBackgroundResource(R.drawable.border);
                    sendReplyBtn.setTextColor(Color.parseColor("#A9ADB0"));
                    replyBox.setVisibility(View.GONE);
                    //自动隐藏软键盘
                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm2.isActive()) {
                        imm2.hideSoftInputFromWindow(replyEdtext.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    Log.d("ReplyId",ReplyId+"");
                    Log.d("ReplyId", ReplyId + "");
                    Log.e("评论内容>>>>>>>>>>", replyEdtext.getText().toString());
                    groupApi.replyComment(String.valueOf(post_id), ReplyId + "", replyEdtext.getText().toString());
                    replyEdtext.setText(null);
                    loadMoreText.setText("有新评论，点击加载");
                    loadBar.setVisibility(View.VISIBLE);
                }else {
                    ToastHelper.showToast("评论不能为空",mContext);
                }
                break;
            case R.id.floor_UserHead:
                groupApi.goUserInfo(mContext,floorInfoMap.get("user_uid"));
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler() {

        private ArrayList<HashMap<String, String>> floorReplyList;
        private int countOne = 0;
        private int countTwo = 0;
        private boolean lock = true;

        @Override
        @SuppressWarnings(value = {"unchecked"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastHelper.showToast("回复楼中楼成功",GroupFloorReplyActivity.this);
                    break;
                case 800:
                    ToastHelper.showToast("回复失败",GroupFloorReplyActivity.this);
                    break;
                case 0x140:
                    floorReplyList = (ArrayList<HashMap<String, String>>) msg.obj;
                    countTwo = floorReplyList.size();
                    Log.e("floorReplyList>>>>>page", floorReplyList.toString() + "page=" + page);
                    //判断是否该分页加载
                    if (floorReplyList.size() == 0){
                        if (page != 1) {
                            loadBar.setVisibility(View.VISIBLE);
                            loadMoreText.setText("暂无更多");
                        }
                    }else {
                        if (lock && countTwo == 10) {
                            page++;
                            for (int i = 0; i < countTwo; i++) {
                                lzlReplyBody.addView(getItemView(floorReplyList.get(i)));
                                loadBar.setVisibility(View.VISIBLE);
                                loadMoreText.setText("点击加载更多");
                            }
                        }else{
                            lock = false;
                            loadMoreText.setText("暂无更多");
                            for (int i = countOne; i < countTwo; i++) {
                                lzlReplyBody.addView(getItemView(floorReplyList.get(i)));
                                loadBar.setVisibility(View.VISIBLE);
                            }
                            countOne = floorReplyList.size();
                            if (countOne == 10){
                                page++;
                                lock = true;
                                loadMoreText.setText("点击加载更多");
                                countOne = 0;
                            }
                        }
                    }
                    loadMorePro.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    //获得一条楼中楼回复item
    public View getItemView(final HashMap<String, String> tempMap){
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.view = LayoutInflater.from(mContext).inflate(R.layout.group_lzl_reply_item, null);
        viewHolder.userHead = (CircleImageView) viewHolder.view.findViewById(R.id.lzl_userHead);
        viewHolder.userName = (TextView) viewHolder.view.findViewById(R.id.lzl_username);
        viewHolder.isLouZhu = (LinearLayout) viewHolder.view.findViewById(R.id.lzl_louzhu);
        viewHolder.replyTime = (TextView) viewHolder.view.findViewById(R.id.lzl_reply_time);
        viewHolder.replyContent = (TextView) viewHolder.view.findViewById(R.id.lzl_reply_content);

        kjBitmap.display(viewHolder.userHead, tempMap.get("user_logo"));
        viewHolder.userName.setText(tempMap.get("nickname"));
        viewHolder.replyTime.setText(tempMap.get("create_time"));
        viewHolder.replyContent.setText(tempMap.get("content"));
        if (tempMap.get("is_landlord").equals("1")) {
            viewHolder.isLouZhu.setVisibility(View.VISIBLE);
        }
        viewHolder.userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupApi.goUserInfo(mContext,tempMap.get("user_uid"));
            }
        });
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!groupApi.getSeesionId().equals("")){
                    replyBox.setVisibility(View.VISIBLE);
                    //打开软键盘并自动获取焦点
                    groupApi.openKeyBoard(mContext, replyEdtext);
    //                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    replyEdtext.setText("回复@" + tempMap.get("nickname") + "：");
                    //把光标自动放末尾
                    replyEdtext.setSelection(replyEdtext.getText().length());
                }else {
                    ToastHelper.showToast("请登录后操作",mContext);
                }
            }
        });
        return viewHolder.view;
    }

    //控件缓存器
    private class ViewHolder{
        View view;
        CircleImageView userHead;
        TextView userName;
        LinearLayout isLouZhu;
        TextView replyTime;
        TextView replyContent;
    }

    //获得楼中楼回复数据的线程
    class LzlReplyThread extends Thread implements Runnable{

        private ArrayList<JSONObject> jsonObjArrayList;
        private ArrayList<HashMap<String, String>> arrayList;

        public LzlReplyThread() {
            super();
        }

        @Override
        public void run() {
            arrayList = new ArrayList<HashMap<String,String>>();
            jsonObjArrayList = groupApi.getLzlReply("?s=" + Url.POSTLZL, post_id, ReplyId, page);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                try {
                    map.put("id", jsonObj.getString("id"));
                    map.put("post_id", jsonObj.getString("post_id"));
                    map.put("to_f_reply_id", jsonObj.getString("to_f_reply_id"));
                    map.put("to_reply_id", jsonObj.getString("to_reply_id"));
                    map.put("content", jsonObj.getString("content"));
                    map.put("uid", jsonObj.getString("uid"));
                    map.put("to_uid", jsonObj.getString("to_uid"));
                    map.put("create_time", jsonObj.getString("create_time"));
                    map.put("status", jsonObj.getString("status"));
                    map.put("is_landlord", jsonObj.getString("is_landlord"));
                    JSONObject jsonUserObj = jsonObj.getJSONObject("user");
                    map.put("user_uid", jsonUserObj.getString("uid"));
                    map.put("nickname", jsonUserObj.getString("nickname"));
                    map.put("user_logo", Url.USERHEADURL + jsonUserObj.getString("avatar128"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                arrayList.add(map);
            }
            Message message = new Message();
            message.what = 0x140;
            message.obj = arrayList;
            mHandler.sendMessage(message);
        }
    }
}