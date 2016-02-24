package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.myview.IssueListView;
import com.thinksky.net.IsNet;
import com.tox.BaseFunction;
import com.tox.EventApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/4/30 0030.
 */
public class EventDetailActivity extends Activity{

    RelativeLayout event_detail_page;
    RelativeLayout event_back;
    ImageView event_image;
    ProgressBar image_progress;
    TextView prompt_info;
    TextView event_title;
    TextView event_userName;
    TextView view_count;
    TextView event_time;
    TextView event_address;
    TextView event_endTime;
    TextView event_detail;
    TextView limitCount;
    TextView signCount;
    TextView attentionCount;
    TextView join_event;
    //评论控件集
    ImageView reply_btn;
    LinearLayout event_reply_box;
    EditText event_reply_editText;
    TextView event_reply_btn;
    LinearLayout event_reply_box_huifu;
    EditText event_reply_editText_huifu;
    TextView event_reply_btn_huifu;
    //arr[0]是点赞有没有成功   arr[1]是点赞返回的信息！！
    private String arr[]=null;
    ScrollView myScrollView;
    private ArrayList<HashMap<String,String >> reply_infos;
    private ArrayList<HashMap<String,String >> reply_info;
    IssueListView event_listView;
    EventListAdapter replyAdapter;
    View listLoad;
    TextView event_reply_load;
    ProgressBar event_reply_proBar;
    int MaxReplyNum;
    HashMap<String,String> eventInfo;
    int event_id;
    private Handler detailHandler;
    Thread detailThread;
    Thread replyThread;
    Thread refulash;
    View alertView;
    String join_phone;
    String join_name;
    //Session_id
    String session_id=null;
    EventApi eventApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        arr=new String[2];
        //获取session_id
        eventApi = new EventApi();
        session_id = eventApi.getSeesionId();
        //获取控件
        event_detail_page = (RelativeLayout)findViewById(R.id.event_detail_page);
        event_back = (RelativeLayout)findViewById(R.id.event_back);
        event_image = (ImageView) findViewById(R.id.event_image);
        image_progress = (ProgressBar) findViewById(R.id.image_progress);
        prompt_info = (TextView) findViewById(R.id.prompt_info);
        event_title = (TextView) findViewById(R.id.event_title);
        event_userName = (TextView) findViewById(R.id.event_userName);
        view_count = (TextView) findViewById(R.id.view_count);
        limitCount = (TextView) findViewById(R.id.limitCount);
        signCount = (TextView) findViewById(R.id.signCount);
        attentionCount = (TextView) findViewById(R.id.attentionCount);
        join_event = (TextView) findViewById(R.id.join_event);
        //评论控件集
        reply_btn = (ImageView) findViewById(R.id.reply_btn);
        event_reply_box = (LinearLayout) findViewById(R.id.event_reply_box);
        event_reply_editText = (EditText) findViewById(R.id.event_reply_editText);
        event_reply_btn = (TextView) findViewById(R.id.event_reply_btn);
        event_reply_box_huifu = (LinearLayout) findViewById(R.id.event_reply_box_huifu);
        event_reply_editText_huifu = (EditText) findViewById(R.id.event_reply_editText_huifu);
        event_reply_btn_huifu= (TextView) findViewById(R.id.event_reply_btn_huifu);

        event_time = (TextView) findViewById(R.id.event_time);
        event_address = (TextView) findViewById(R.id.event_address);
        event_endTime = (TextView) findViewById(R.id.event_endTime);
        event_detail = (TextView) findViewById(R.id.event_detail);
        myScrollView = (ScrollView) findViewById(R.id.scrollView);
        event_listView = (IssueListView) findViewById(R.id.event_list);
        event_detail_page.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                event_reply_box.setVisibility(View.GONE);
                event_reply_box_huifu.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(event_reply_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        myScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                event_reply_box.setVisibility(View.GONE);
                event_reply_box_huifu.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(event_reply_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        /**
         * 回复代码块
         */
        //文本事件监听器
        event_reply_editText_huifu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {

                    event_reply_btn_huifu.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    event_reply_btn_huifu.setTextColor(Color.WHITE);

                } else {
                    event_reply_btn_huifu.setBackgroundResource(R.drawable.border);
                    event_reply_btn_huifu.setTextColor(Color.parseColor("#A9ADB0"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    event_reply_btn_huifu.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    event_reply_btn_huifu.setTextColor(Color.WHITE);
                } else {
                    event_reply_btn_huifu.setBackgroundResource(R.drawable.border);
                    event_reply_btn_huifu.setTextColor(Color.parseColor("#A9ADB0"));

                }
            }
        });
        event_reply_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {

                    event_reply_btn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    event_reply_btn.setTextColor(Color.WHITE);

                } else {
                    event_reply_btn.setBackgroundResource(R.drawable.border);
                    event_reply_btn.setTextColor(Color.parseColor("#A9ADB0"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    event_reply_btn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    event_reply_btn.setTextColor(Color.WHITE);
                } else {
                    event_reply_btn.setBackgroundResource(R.drawable.border);
                    event_reply_btn.setTextColor(Color.parseColor("#A9ADB0"));

                }
            }
        });
        //发评论
        event_reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MyPostThread().start();
                event_reply_box.setVisibility(View.GONE);
                refulash=new PostComments();
                if (IsNet.IsConnect()) {
                    refulash.start();
                }else{
                    ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                }
                //强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        //评论按钮监听器
        reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_reply_editText.setText("");
                if (event_reply_box.getVisibility()==View.GONE) {
                    event_reply_box.setVisibility(View.VISIBLE);
                    event_reply_editText.setFocusable(true);
                    event_reply_editText.setFocusableInTouchMode(true);
                    event_reply_editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(event_reply_editText, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    event_reply_box.setVisibility(View.GONE);
                }
            }
        });
        //报名按钮监听器
        join_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseFunction.isLogin()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventDetailActivity.this).setTitle("报名");
                    //报名对话框
                    alertView = getLayoutInflater().inflate(R.layout.event_join_alertview,null);
                    alertDialog.setView(alertView);
                    alertDialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText name = (EditText) alertView.findViewById(R.id.join_name);
                            EditText number = (EditText) alertView.findViewById(R.id.join_phoneNumber);
                            join_phone = number.getText().toString();
                            join_name = name.getText().toString();
                            Pattern p = Pattern
                                    .compile("^(13|15|18)\\d{9}$");
                            Matcher m = p.matcher(join_phone);
                            if (join_name==""){
                                Toast.makeText(EventDetailActivity.this,"请填写姓名",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (m.matches()) {
                                new Join().start();
                            }else{
                                Toast.makeText(EventDetailActivity.this,"号码不正确",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).create().show();
                }else {
                    ToastHelper.showToast("还未登录",EventDetailActivity.this);
                }
            }
        });

        //listView最后的加载按钮
        listLoad = getLayoutInflater().inflate(R.layout.issue_detail_listview_probar, null);
        event_reply_load = (TextView)listLoad.findViewById(R.id.issue_reply_load);
        event_reply_proBar = (ProgressBar)listLoad.findViewById(R.id.issue_reply_proBar);
        event_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        event_id = Integer.parseInt(this.getIntent().getStringExtra("event_id"));
        //主页面信息线程
        detailThread = new Thread(new Runnable() {
            HashMap<String,String> eventInfoMap;
            @Override
            public void run() {
                EventApi eventApi = new EventApi();
                JSONArray jsonList = eventApi.getEventDetail("?s=" + Url.EVENTDETAIL,event_id,session_id);
                eventInfoMap = new HashMap<String, String>();
                try {
                    JSONObject jsonObj = jsonList.getJSONObject(0);
                    eventInfoMap.put("event_id", jsonObj.getString("id"));
                    eventInfoMap.put("event_title", jsonObj.getString("title"));
                    eventInfoMap.put("view_count", jsonObj.getString("view_count"));
                    eventInfoMap.put("is_recommend", jsonObj.getString("is_recommend"));
                    eventInfoMap.put("explain", jsonObj.getString("explain"));
                    eventInfoMap.put("limitCount", jsonObj.getString("limitCount"));
                    eventInfoMap.put("sTime", jsonObj.getString("sTime"));
                    eventInfoMap.put("eTime", jsonObj.getString("eTime"));
                    eventInfoMap.put("create_time", jsonObj.getString("create_time"));
                    eventInfoMap.put("update_time", jsonObj.getString("update_time"));
                    eventInfoMap.put("deadline", jsonObj.getString("deadline"));
                    eventInfoMap.put("address", jsonObj.getString("address"));
                    eventInfoMap.put("signCount", jsonObj.getString("signCount"));
                    eventInfoMap.put("attentionCount", jsonObj.getString("attentionCount"));
                    eventInfoMap.put("is_deadline", jsonObj.getString("is_deadline"));
                    eventInfoMap.put("is_end", jsonObj.getString("is_end"));
                    eventInfoMap.put("is_Attend",jsonObj.getString("is_Attend"));
                    eventInfoMap.put("is_pass",jsonObj.getString("is_pass"));
                    eventInfoMap.put("reply_count", jsonObj.getString("reply_count"));
//                    JSONObject imageURL = (JSONObject) jsonObj.get("cover_url");
                    eventInfoMap.put("event_image", Url.USERHEADURL + jsonObj.getString("cover_url"));

                    JSONObject userName = (JSONObject) jsonObj.get("user");
                    eventInfoMap.put("event_nickname", userName.getString("nickname"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0x110;
                message.obj = eventInfoMap;
                detailHandler.sendMessage(message);
            }
        });
        if(IsNet.IsConnect()) {
            detailThread.start();
        }else
        {
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }


        replyThread=new PostComments();
        replyThread.start();
        detailHandler = new Handler(){

            @Override
            @SuppressWarnings(value = {"unchecked"})
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x110:
                        eventInfo = (HashMap<String, String>) msg.obj;
                        KJBitmap kjbImage = KJBitmap.create();
                        kjbImage.display(event_image, eventInfo.get("event_image"));
                        event_image.setVisibility(View.VISIBLE);
                        image_progress.setVisibility(View.GONE);
                        event_title.setText(eventInfo.get("event_title"));
                        event_userName.setText(eventInfo.get("event_nickname"));
                        event_time.setText(eventInfo.get("sTime") + " -- " + eventInfo.get("eTime"));
                        event_endTime.setText(eventInfo.get("deadline"));
                        event_address.setText(eventInfo.get("address"));
                        event_detail.setText(eventInfo.get("explain"));
                        view_count.setText(eventInfo.get("view_count"));
                        limitCount.setText(eventInfo.get("limitCount"));
                        signCount.setText(eventInfo.get("signCount"));
                        attentionCount.setText(eventInfo.get("attentionCount"));
                        Log.d("111111", eventInfo.get("is_Attend"));
                        if (Integer.parseInt(eventInfo.get("is_end")) == 0) {
                            prompt_info.setText("正在进行");
                            prompt_info.setBackgroundColor(Color.parseColor("#D61F39"));
                            if (Integer.parseInt(eventInfo.get("is_Attend"))==1){
                                Log.d("有啊", eventInfo.get("is_Attend"));
                                if (Integer.parseInt(eventInfo.get("is_pass"))==0) {
                                    join_event.setText("正在审核中");
                                    join_event.setClickable(false);
                                }else{
                                    join_event.setText("已报名");
                                    join_event.setClickable(false);
                                }
                            }
                            if (Integer.parseInt(eventInfo.get("is_deadline")) == 1){
                                join_event.setText("报名已截止");
                                join_event.setClickable(false);
                            }else if(Integer.parseInt(eventInfo.get("limitCount")) == Integer.parseInt(eventInfo.get("signCount"))){
                                join_event.setText("参加人数已满");
                                join_event.setClickable(false);
                            }
                        } else if (Integer.parseInt(eventInfo.get("is_end")) == 1) {
                            prompt_info.setText("已结束");
                            prompt_info.setBackgroundColor(Color.parseColor("#000000"));
                            join_event.setText("报名已截止");
                            join_event.setClickable(false);
                        }
                        break;
                    case 0x111:
                        reply_infos = (ArrayList<HashMap<String, String>>) msg.obj;
                        // 加上底部View，注意要放在setAdapter方法前
                        event_listView.addFooterView(listLoad);
                        MaxReplyNum = reply_infos.size();
                        //
                        if (reply_infos.size() > 5){
                            reply_info = new ArrayList<HashMap<String,String >>();
                            for (int i =0;i < 5;i++){
                                reply_info.add(reply_infos.get(i));
                            }
                        }
                        else {
                            reply_info = new ArrayList<HashMap<String,String >>();
                            for (int i =0;i < reply_infos.size();i++){
                                reply_info.add(reply_infos.get(i));
                            }
                        }
                        replyAdapter = new EventListAdapter(EventDetailActivity.this, reply_info, R.layout.event_listview_item,
                                null,null);
                        event_listView.setAdapter(replyAdapter);
                        //设置scrollView初始化时始终在顶部
                        myScrollView.smoothScrollTo(0, 20);
                        break;
                    case 0xaaa:
                        String arg[]=new String[2];
                        arg= (String[]) msg.obj;
                        Log.d("wj...",arg[0]);
                        Log.d("wj...", arg[1]);
                        if (arg[0]=="true"){
                            Toast.makeText(EventDetailActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(EventDetailActivity.this,arg[1],Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0xaab:
                        String ar[]=new String[2];
                        ar= (String[]) msg.obj;
                        Log.d("wj...",ar[0]);
                        Log.d("wj...", ar[1]);
                        if (ar[0]=="true"){
                            Toast.makeText(EventDetailActivity.this,"报名成功",Toast.LENGTH_SHORT).show();
                            join_event.setText("正在审核中");
                            join_event.setClickable(false);
                        }else {
                            Toast.makeText(EventDetailActivity.this,ar[1],Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0xaac:
                        String arg1[]=new String[2];
                        arg1= (String[]) msg.obj;
                        Log.d("wj...",arg1[0]);
                        Log.d("wj...", arg1[1]);
                        if (arg1[0]=="true"){
                            Toast.makeText(EventDetailActivity.this,"回复楼中楼成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(EventDetailActivity.this,arg1[1],Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        event_reply_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_reply_proBar.setVisibility(View.VISIBLE);
                event_reply_load.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        LoadingMoreReply();
                        event_reply_load.setVisibility(View.VISIBLE);
                        event_reply_proBar.setVisibility(View.GONE);
                        replyAdapter.notifyDataSetChanged();
                    }
                }, 2000);
                // 所有的条目已经和最大条数相等，则移除底部的View
                if (MaxReplyNum == replyAdapter.getCount() && MaxReplyNum > 0) {
                    event_listView.removeFooterView(listLoad);
                    Toast.makeText(EventDetailActivity.this, "已没有更多评论", Toast.LENGTH_SHORT).show();
                }
                else if (MaxReplyNum == 0){
                    event_reply_load.setText("还没有评论");
                    Toast.makeText(EventDetailActivity.this,"还没有人评论，留个脚印吧", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //获取评论回复线程
    class PostComments extends Thread{
        private ArrayList<HashMap<String,String >> replyList;
        @Override
        public void run() {
            EventApi eventApi = new EventApi();
            ArrayList<JSONObject> jsonList= eventApi.getEventReplyList("?s=" + Url.EVENTCOMMENTS,event_id);
            replyList = new ArrayList<HashMap<String,String >>();
            try {
                for (int i = 0; i < jsonList.size(); i++) {
                    JSONObject jsonObj = jsonList.get(i);
                    HashMap<String, String> map1 = new HashMap<String, String>();
                    map1.put("event_content", jsonObj.getString("content"));
                    map1.put("event_create_time", jsonObj.getString("create_time"));
                    //评论回复用户信息
                    try {
                        JSONObject userJSONObj = jsonObj.getJSONObject("user");
                        if (userJSONObj != null) {
                            map1.put("user_id", userJSONObj.getString("uid"));
                            map1.put("user_name", userJSONObj.getString("nickname"));
                            map1.put("user_image", Url.USERHEADURL + userJSONObj.getString("avatar128"));
                            replyList.add(map1);
                        }
                    }catch (JSONException e)
                    {
                        map1.put("user_name","游客");
                        replyList.add(map1);
                    }
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = 0x111;
            message.obj = replyList;
            detailHandler.sendMessage(message);
        }
    }

    private class EventListAdapter extends SimpleAdapter {

        ArrayList<HashMap<String,String>> reply_info;
        KJBitmap kjbImage;
        ViewHolder viewHolder;
        private Context mContext;
        public EventListAdapter(Context context, ArrayList<HashMap<String,String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.mContext = context;
            this.reply_info = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            kjbImage = KJBitmap.create();
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.issue_detail_listview_item, null);
                //获取listView中的控件
                viewHolder.replyUserImage = (ImageView) convertView.findViewById(R.id.replyUserImage);
                viewHolder.replyUserName = (TextView) convertView.findViewById(R.id.replyUserName);
                viewHolder.replyTime = (TextView) convertView.findViewById(R.id.replyTime);
                viewHolder.replyTextView = (TextView) convertView.findViewById(R.id.replyTextView);
                viewHolder.comment_reply = (RelativeLayout) convertView.findViewById(R.id.huiFuPingLun);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            //给控件赋值
            if (reply_info.get(position).get("user_image") != null) {
                kjbImage.display(viewHolder.replyUserImage, reply_info.get(position).get("user_image"));
            }
            viewHolder.replyUserName.setText(reply_info.get(position).get("user_name"));
            viewHolder.replyTextView.setText(reply_info.get(position).get("event_content"));
            viewHolder.replyTime.setText(reply_info.get(position).get("event_create_time"));

            //查看用户资料
            viewHolder.replyUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reply_info.get(position).get("user_id") != null) {
                        eventApi.goUserInfo(mContext, reply_info.get(position).get("user_id"));
                    }
                }
            });

            //回复评论监听器
            viewHolder.comment_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (event_reply_box_huifu.getVisibility() == View.GONE){
                        event_reply_box_huifu.setVisibility(View.VISIBLE);
                        event_reply_editText_huifu.setFocusable(true);
                        event_reply_editText_huifu.setFocusableInTouchMode(true);
                        event_reply_editText_huifu.requestFocus();
                        event_reply_editText_huifu.setText("");
                        event_reply_btn_huifu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String s = event_reply_editText_huifu.getText().toString();
                            if (s.equals("")) {
                                Toast.makeText(EventDetailActivity.this, "评论不能为空", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                final String name = viewHolder.replyUserName.getText().toString();
                                if (IsNet.IsConnect()){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BufferedReader in = null;
                                            try {
                                                HttpClient httpClient = new DefaultHttpClient();
                                                HttpPost httpPost = new HttpPost(Url.HTTPURL + "?s=" + Url.POSTEVENTCOMMENT);
                                                String comments = event_reply_editText_huifu.getText().toString();
                                                Log.d("wj测试回复楼中楼", comments);
                                                System.out.println("wj测试楼中楼ID:" + event_id);
                                                // 创建名/值组列表
                                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                params.add(new BasicNameValuePair("row_id", String.valueOf(event_id)));
                                                params.add(new BasicNameValuePair("content", "回复 @" + name + ":" + comments));
                                                params.add(new BasicNameValuePair("session_id", session_id));
                                                // 创建UrlEncodedFormEntity对象
                                                UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(params, "UTF-8");
                                                httpPost.setEntity(formEntiry);

                                                // 执行请求
                                                HttpResponse response = httpClient.execute(httpPost);

                                                in = new BufferedReader(new InputStreamReader(response.getEntity()
                                                        .getContent()));
                                                StringBuffer sb = new StringBuffer("");
                                                sb.append(in.readLine());
                                                in.close();
                                                String result = sb.toString();
                                                MyJson(result);
                                                Message message = new Message();
                                                message.obj = arr;
                                                message.what = 0xaac;
                                                detailHandler.sendMessage(message);
                                            } catch (ClientProtocolException e) {
                                                e.printStackTrace();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } finally {
                                                if (in != null) {
                                                    try {
                                                        in.close();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }).start();
                                }else{
                                    ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                                }
                            }
                            if (IsNet.IsConnect()) {
                                Thread refush = new PostComments();
                                refush.start();
                            }else{
                                ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                            }

                            event_reply_box_huifu.setVisibility(View.GONE);
                            //强制隐藏键盘
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    });
                }else{
                        event_reply_box_huifu.setVisibility(View.GONE);
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return reply_info.size();
        }
    }
    //ViewHolder缓存类
    private class ViewHolder{
        //listView中的控件
        RelativeLayout comment_reply;
        ImageView replyUserImage;
        TextView replyUserName;
        TextView replyTime;
        TextView replyTextView;
    }
    //评论分页加载
    private void LoadingMoreReply(){
        int count = replyAdapter.getCount();
        if (count + 5 < MaxReplyNum){
            for (int i = count; i < count + 5; i++){
                reply_info.add(reply_infos.get(i));
            }
        }
        else {
            for (int i = count; i < MaxReplyNum; i++){
                reply_info.add(reply_infos.get(i));
            }
        }
    }
    //发评论的线程类
    class MyPostThread extends Thread{
        @Override
        public void run() {
            BufferedReader in = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Url.HTTPURL + "?s=" + Url.POSTEVENTCOMMENT);
                String comments=event_reply_editText.getText().toString();
                Log.d("comments",comments);
                if (comments.equals("")){
                    Log.d("comments1232132121",comments);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EventDetailActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("comments1666666666", comments);
                    return;
                }

                Log.d("wj测试评论",comments);
                System.out.println("wj测试ID:"+event_id);
                // 创建名/值组列表
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("row_id",String.valueOf(event_id)));
                params.add(new BasicNameValuePair("content", comments));
                params.add(new BasicNameValuePair("session_id", session_id));
                // 创建UrlEncodedFormEntity对象
                UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(params,"UTF-8");
                httpPost.setEntity(formEntiry);

                // 执行请求
                HttpResponse response = httpClient.execute(httpPost);

                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent()));
                StringBuffer sb = new StringBuffer("");
                sb.append(in.readLine());
                in.close();
                String result = sb.toString();
                MyJson(result);
                Message message=detailHandler.obtainMessage();
                message.obj=arr;
                message.what=0xaaa;
                detailHandler.sendMessage(message);
            }catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(in!=null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //活动报名
    class Join extends Thread{
        @Override
        public void run() {
            BufferedReader in = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Url.HTTPURL + "?s=" + Url.JOINEVENTS);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("session_id", session_id));
                params.add(new BasicNameValuePair("event_id", String.valueOf(event_id)));
                params.add(new BasicNameValuePair("name", join_name));
                params.add(new BasicNameValuePair("phone", join_phone));
                // 创建UrlEncodedFormEntity对象
                UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(params);
                httpPost.setEntity(formEntiry);
                // 执行请求
                HttpResponse response = httpClient.execute(httpPost);
                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent()));
                String sb = null;
                sb=in.readLine();
                in.close();
                String result = sb.toString();
                MyJson(result);
                Message message=detailHandler.obtainMessage();
                message.what=0xaab;
                message.obj=arr;
                detailHandler.sendMessage(message);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //JSON解析是否成功
    private void MyJson(String result){
        String s="";
        try {
            JSONObject jsonObject=new JSONObject(result);
            arr[0]=jsonObject.getString("success");
            arr[1]=jsonObject.getString("message");
            Log.d("status>>>>>>>>>>>",arr[0]);
            Log.d("status>>>>>>>>>>>",arr[1]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}