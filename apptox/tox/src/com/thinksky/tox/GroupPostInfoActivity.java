package com.thinksky.tox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.thinksky.myview.IssueListView;
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
 * Created by Administrator on 2015/5/28 0028.
 */
public class GroupPostInfoActivity extends Activity implements View.OnClickListener,View.OnTouchListener{

    LinearLayout loadingBar;
    private static boolean SUPPORT=false;
    private static boolean POSTCOMMENT=false;
    TextView loadingBarText;
    ProgressBar loadingProBar;
    GroupApi groupApi;
    String support_flag;
    private int post_id;
    private int page = 1;
    private KJBitmap kjBitmap;
    private ImageView back_menu;
    private TextView group_name;
    private ScrollView post_scroll;
    private LinearLayout postBody;
    private TextView post_title;
    private TextView loadingText;
    private CircleImageView user_logo;
    private TextView post_user_name;
    private TextView post_create_time;
    private TextView post_content;
    private LinearLayout reply_bottom_layout;
    private LinearLayout support_button;
    private TextView supportCountView;
    private LinearLayout reply_button;
    private LinearLayout reply_box;
    private EditText reply_editText;
    private TextView replyCountView;
    private TextView sendPostButtn;
    private HashMap<String,String> postMap;
    private String position;
    HashMap<String,Integer> countMap;
    private Context mContext;
    private int replyCount;
    private int supportCount;
    private int width;

    @Override
    @SuppressWarnings(value = {"unchecked"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = GroupPostInfoActivity.this;
        groupApi = new GroupApi();
        groupApi.setHandler(mHandler);
        kjBitmap = KJBitmap.create();
        setContentView(R.layout.activity_group_post_info);
        postMap = (HashMap<String,String>)getIntent().getExtras().getSerializable("post_info");
        position=getIntent().getExtras().getString("position");
        Log.e("postMap>>>>>>>>>",postMap.toString());
        post_id = Integer.parseInt(postMap.get("id"));
        //获取手机的分辨率
        Display display = getWindowManager().getDefaultDisplay(); //Activity#getWindowManager()
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        new PostAllCount().start();

        back_menu = (ImageView)findViewById(R.id.back_menu);
        group_name = (TextView)findViewById(R.id.group_name);
        post_scroll = (ScrollView)findViewById(R.id.post_scroll);
        postBody = (LinearLayout)findViewById(R.id.post_body_line);
        post_title = (TextView)findViewById(R.id.post_title);
        user_logo = (CircleImageView)findViewById(R.id.user_logo);
        post_user_name = (TextView)findViewById(R.id.post_user_name);
        post_create_time = (TextView)findViewById(R.id.post_create_time);
        post_content = (TextView)findViewById(R.id.post_content);
        loadingText = (TextView)findViewById(R.id.loading_text);

        //加载更多按钮
        loadingBar = (LinearLayout) findViewById(R.id.loading_bar);
        loadingBarText = (TextView) findViewById(R.id.load_more_text);
        loadingProBar = (ProgressBar) findViewById(R.id.load_more_pro);

        //点赞和回复 块
        reply_bottom_layout = (LinearLayout)findViewById(R.id.reply_bottom_layout);
        support_button = (LinearLayout)findViewById(R.id.support_button);
        reply_button = (LinearLayout)findViewById(R.id.reply_button);
        reply_box = (LinearLayout)findViewById(R.id.reply_box);
        supportCountView = (TextView)findViewById(R.id.supportCount);
        replyCountView = (TextView)findViewById(R.id.replyCount);
        reply_editText = (EditText)findViewById(R.id.reply_editText);
        sendPostButtn = (TextView)findViewById(R.id.sendPostButn);

        back_menu.setOnClickListener(this);
        user_logo.setOnClickListener(this);
        support_button.setOnClickListener(this);
        reply_button.setOnClickListener(this);
        sendPostButtn.setOnClickListener(this);
        post_scroll.setOnTouchListener(this);

        new PostReplyThread(post_id,page).start();
        InitPostView(postMap);

        post_scroll.smoothScrollTo(0, 0);

        //点击加载更多数据监听器
        loadingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBarText.setText("正在加载...");
                new PostReplyThread(post_id,page).start();
            }
        });
    }

    //初始化activity
    public void InitPostView(HashMap<String,String> postMap){

        group_name.setText(postMap.get("group_name"));
        post_title.setText(postMap.get("title"));
        post_user_name.setText(postMap.get("user_nickname"));
        kjBitmap.display(user_logo, postMap.get("user_logo"));
        post_create_time.setText(postMap.get("create_time"));
        post_content.setText(postMap.get("content"));
        support_flag=postMap.get("is_support");
        //发表回复文本框的事件监听器
        reply_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    sendPostButtn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    sendPostButtn.setTextColor(Color.WHITE);
                } else {
                    sendPostButtn.setBackgroundResource(R.drawable.border);
                    sendPostButtn.setTextColor(Color.parseColor("#A9ADB0"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    sendPostButtn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    sendPostButtn.setTextColor(Color.WHITE);
                } else {
                    sendPostButtn.setBackgroundResource(R.drawable.border);
                    sendPostButtn.setTextColor(Color.parseColor("#A9ADB0"));
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        reply_bottom_layout.setVisibility(View.VISIBLE);
        reply_box.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(reply_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.back_menu:
                finish();
                break;
            //scrollView容器
            case R.id.post_scroll:
                break;
            //点赞按钮
            case R.id.support_button:
                if (!groupApi.getSeesionId().equals("")) {
                    initFlag(true, false);
                    groupApi.supportGroupPost(post_id + "");
                }else {
                    ToastHelper.showToast("请登录后操作",mContext);
                }
                break;
            //回复按钮
            case R.id.reply_button:
                if (!groupApi.getSeesionId().equals("")) {
                    reply_box.setVisibility(View.VISIBLE);
                    //自动打开软键盘并获取焦点
                    reply_editText.setFocusable(true);
                    reply_editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    reply_bottom_layout.setVisibility(View.GONE);
                }else {
                    ToastHelper.showToast("请登录后操作",mContext);
                }
                break;
            //发表评论按钮
            case R.id.sendPostButn:
                if (reply_editText.getText().length() > 0 && !"".equals(reply_editText.getText().toString().trim())) {
                    sendPostButtn.setBackgroundResource(R.drawable.border);
                    sendPostButtn.setTextColor(Color.parseColor("#A9ADB0"));
                    reply_bottom_layout.setVisibility(View.VISIBLE);
                    reply_box.setVisibility(View.GONE);
                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm2.isActive()) {
                        imm2.hideSoftInputFromWindow(reply_editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    Log.e("评论内容>>>>>>>>>>", reply_editText.getText().toString());
                    initFlag(false,true);
                    groupApi.postComment(post_id+"",reply_editText.getText().toString());
                    reply_editText.setText(null);
                }else {
                    ToastHelper.showToast("评论不能为空",mContext);
                }
                break;
            case R.id.user_logo:
                groupApi.goUserInfo(mContext,postMap.get("user_uid"));
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler(){

        private ArrayList<HashMap<String, String>> postReplyList;
        private int countOne = 0;
        private int countTwo = 0;
        private int floorCount = 2;
        private boolean lock = true;

        @Override
        @SuppressWarnings(value = {"unchecked"})
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (SUPPORT) {
                        Log.d("TAG", "感谢你的支持");
                        ToastHelper.showToast("感谢你的支持", mContext);
                        supportCount++;
                        supportCountView.setText(supportCount + "");
                    }
                    if (POSTCOMMENT){
                        replyCount++;
                        replyCountView.setText(replyCount + "");
                        ToastHelper.showToast("评论成功",mContext);
                    }
                    break;
                case 800:
                    Log.d("TAG","点赞失败");
                    ToastHelper.showToast("重复点赞",mContext);
                    break;
                //设置评论和点赞数目
                case 0x135:
                    supportCount  = countMap.get("supportCount");
                    replyCount  = countMap.get("replyCount");
                    supportCountView.setText(supportCount + "");
                    replyCountView.setText(replyCount + "");
                    break;
                case 0x130:
                    postReplyList = (ArrayList<HashMap<String, String>>)msg.obj;
                    loadingProBar.setVisibility(View.GONE);
                    countTwo = postReplyList.size();
                    Log.e("postReplyList>>>>>>>>", postReplyList.toString());
                    //判断是否该分页加载
                    if (postReplyList.size() == 0){
                        loadingBarText.setText("暂无更多");
                    }else {
                        if (lock && countTwo == 10) {
                            page++;
                            for (int i = 0; i < countTwo; i++) {
                                postBody.addView(getItemView(postReplyList.get(i), floorCount));
                                floorCount++;
                            }
                        }else{
                            lock = false;
                            loadingBarText.setText("暂无更多");
                            for (int i = countOne; i < countTwo; i++) {
                                postBody.addView(getItemView(postReplyList.get(i), floorCount));
                                floorCount++;
                            }
                            countOne = postReplyList.size();
                            if (countOne == 10){
                                page++;
                                lock = true;
                                loadingBarText.setText("点击加载更多");
                                countOne = 0;
                            }
                        }
                    }
                    loadingText.setVisibility(View.GONE);
                    loadingBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    //评论item
    public View getItemView(final HashMap<String, String> map,int floorCount){

        map.put("floor_id",String.valueOf(floorCount));
        ViewHolder viewHolder = new ViewHolder();
        int toReplyCount = Integer.parseInt(map.get("toReplyCount"));
        viewHolder.view = LayoutInflater.from(mContext).inflate(R.layout.group_post_comment_item, null);
        viewHolder.replyUserHead = (CircleImageView) viewHolder.view.findViewById(R.id.Post_comItem_UserHead);
        viewHolder.replyUsername = (TextView) viewHolder.view.findViewById(R.id.Post_commentUsername);
        viewHolder.replyTime = (TextView) viewHolder.view.findViewById(R.id.Post_reply_time);
        viewHolder.replyButton = (ImageView) viewHolder.view.findViewById(R.id.reply_floor_btn);
        viewHolder.replyContent = (TextView) viewHolder.view.findViewById(R.id.reply_content);
        viewHolder.replyHost = (LinearLayout) viewHolder.view.findViewById(R.id.reply_is_host);
        //楼中楼模块
        viewHolder.LzlReplyBox = (LinearLayout) viewHolder.view.findViewById(R.id.lzl_reply_box);
        viewHolder.lzlOneLayout = (RelativeLayout) viewHolder.view.findViewById(R.id.lzl_one_layout);
        viewHolder.lzlTwoLayout = (RelativeLayout) viewHolder.view.findViewById(R.id.lzl_two_layout);
        viewHolder.lzlOneLouzhu = (LinearLayout) viewHolder.view.findViewById(R.id.lzl_one_louzhu);
        viewHolder.lzlOneUserLogo = (CircleImageView) viewHolder.view.findViewById(R.id.lzl_one_userHead);
        viewHolder.lzlOneUsername = (TextView) viewHolder.view.findViewById(R.id.lzl_one_username);
        viewHolder.lzlOneTime = (TextView) viewHolder.view.findViewById(R.id.lzl_one_time);
        viewHolder.lzlOneReplyContent = (TextView) viewHolder.view.findViewById(R.id.lzl_one_reply_content);

        viewHolder.lzlTwoUserLogo = (CircleImageView) viewHolder.view.findViewById(R.id.lzl_two_userHead);
        viewHolder.lzlTwoLouzhu = (LinearLayout) viewHolder.view.findViewById(R.id.lzl_two_louzhu);
        viewHolder.lzlTwoUsername = (TextView) viewHolder.view.findViewById(R.id.lzl_two_username);
        viewHolder.lzlTwoTime = (TextView) viewHolder.view.findViewById(R.id.lzl_two_time);
        viewHolder.lzlTwoReplyContent = (TextView) viewHolder.view.findViewById(R.id.lzl_two_content);
        viewHolder.morLzlReplyBtn = (TextView) viewHolder.view.findViewById(R.id.more_lzl_reply_btn);

        if (toReplyCount > 0) {

            //楼中楼回复异步加载入口
            LzlTask lzlTask = new LzlTask(viewHolder);
            lzlTask.execute(Integer.parseInt(map.get("id")));
        }

        if (map.get("is_landlord").equals("1")) {
            viewHolder.replyHost.setVisibility(View.VISIBLE);
        }
        kjBitmap.display(viewHolder.replyUserHead, map.get("user_logo"));
        viewHolder.replyUsername.setText(map.get("nickname"));
        viewHolder.replyTime.setText("第" + floorCount + "楼 " + map.get("create_time"));
        viewHolder.replyContent.setText(map.get("content"));

        viewHolder.replyUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupApi.goUserInfo(mContext,map.get("user_uid"));
            }
        });

        //回复楼层按钮
        viewHolder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!groupApi.getSeesionId().equals("")) {
                    map.put("keyLock", "1");
                    sendFloorInfo(map);
                }else {
                    ToastHelper.showToast("请登录后操作",mContext);
                }
            }
        });
        viewHolder.lzlOneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.put("keyLock","0");
                sendFloorInfo(map);
            }
        });
        viewHolder.lzlTwoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.put("keyLock","0");
                sendFloorInfo(map);
            }
        });
        viewHolder.morLzlReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.put("keyLock","0");
                sendFloorInfo(map);
            }
        });
        return viewHolder.view;
    }

    //发送请求GroupFloorReplyActivity页面
    public void sendFloorInfo(HashMap<String, String> map){
        Intent replyFloorIntent = new Intent(mContext,GroupFloorReplyActivity.class);
        Bundle tempBundle = new Bundle();
        tempBundle.putSerializable("floorInfo",map);
        replyFloorIntent.putExtras(tempBundle);
        startActivity(replyFloorIntent);
    }

    //楼中楼数据异步加载器
    class LzlTask extends AsyncTask<Integer, Void, ArrayList<HashMap<String, String>>>{

        private ArrayList<JSONObject> jsonObjArrayList;
        private ArrayList<HashMap<String, String>> arrayList;
        private ViewHolder viewHolder;
        private int toReplyCount;

        public LzlTask(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Integer[] params) {
            arrayList = new ArrayList<HashMap<String,String>>();
            jsonObjArrayList = groupApi.getLzlReply("?s=" + Url.POSTLZL, post_id, params[0],1);
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
            return arrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<HashMap<String, String>> lzlReplyList) {
            toReplyCount = lzlReplyList.size();
            viewHolder.LzlReplyBox.setVisibility(View.VISIBLE);
            kjBitmap.display(viewHolder.lzlOneUserLogo, lzlReplyList.get(0).get("user_logo"));
            viewHolder.lzlOneUserLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupApi.goUserInfo(mContext, lzlReplyList.get(0).get("user_uid"));
                }
            });
            viewHolder.lzlOneUsername.setText(lzlReplyList.get(0).get("nickname"));
            viewHolder.lzlOneTime.setText(lzlReplyList.get(0).get("create_time"));
            viewHolder.lzlOneReplyContent.setText(lzlReplyList.get(0).get("content"));
            if (lzlReplyList.get(0).get("is_landlord").equals("1")){
                viewHolder.lzlOneLouzhu.setVisibility(View.VISIBLE);
            }
            if (toReplyCount >= 2){
                viewHolder.lzlTwoLayout.setVisibility(View.VISIBLE);
                kjBitmap.display(viewHolder.lzlTwoUserLogo, lzlReplyList.get(1).get("user_logo"));
                viewHolder.lzlTwoUserLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupApi.goUserInfo(mContext,lzlReplyList.get(1).get("user_uid"));
                    }
                });
                viewHolder.lzlTwoUsername.setText(lzlReplyList.get(1).get("nickname"));
                viewHolder.lzlTwoTime.setText(lzlReplyList.get(1).get("create_time"));
                viewHolder.lzlTwoReplyContent.setText(lzlReplyList.get(1).get("content"));
                if (lzlReplyList.get(1).get("is_landlord").equals("1")){
                    viewHolder.lzlTwoLouzhu.setVisibility(View.VISIBLE);
                }
            }
            if (toReplyCount > 2) {
                viewHolder.morLzlReplyBtn.setText("还有"+ (toReplyCount - 2) + "条回复，点击查看" );
                viewHolder.morLzlReplyBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    //ViewHolder缓存器
    private class ViewHolder{
        View view;
        CircleImageView replyUserHead;
        TextView replyUsername;
        TextView replyTime;
        ImageView replyButton;
        TextView replyContent;
        LinearLayout replyHost;
        //楼中楼回复块
        LinearLayout LzlReplyBox;
        LinearLayout lzlOneLouzhu;
        LinearLayout lzlTwoLouzhu;
        RelativeLayout lzlOneLayout;
        RelativeLayout lzlTwoLayout;
        CircleImageView lzlOneUserLogo;
        TextView lzlOneUsername;
        TextView lzlOneTime;
        TextView lzlOneReplyContent;
        CircleImageView lzlTwoUserLogo;
        TextView lzlTwoUsername;
        TextView lzlTwoTime;
        TextView lzlTwoReplyContent;
        TextView morLzlReplyBtn;

    }

    //帖子回复数据线程
    private class PostReplyThread extends Thread implements Runnable{

        private ArrayList<HashMap<String, String>> postReplyList;
        private ArrayList<JSONObject> jsonObjArrayList;
        private int page;
        private int post_id;

        public PostReplyThread(int post_id,int page) {
            this.page = page;
            this.post_id = post_id;
        }

        @Override
        public void run() {
            postReplyList = new ArrayList<HashMap<String, String>>();
            jsonObjArrayList = groupApi.getPostReply("?s=" + Url.POSTREPLY, post_id, page);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                try {
                    map.put("id", jsonObj.getString("id"));
                    map.put("uid", jsonObj.getString("uid"));
                    map.put("post_id", jsonObj.getString("post_id"));
                    map.put("content", jsonObj.getString("content"));
                    map.put("create_time", jsonObj.getString("create_time"));
                    map.put("update_time", jsonObj.getString("update_time"));
                    map.put("status", jsonObj.getString("status"));
                    map.put("imgList", jsonObj.getString("imgList"));
                    map.put("is_landlord", jsonObj.getString("is_landlord"));
                    map.put("toReplyCount", jsonObj.getString("toReplyCount"));
                    JSONObject jsonUserObj = jsonObj.getJSONObject("user");
                    map.put("user_uid", jsonUserObj.getString("uid"));
                    map.put("nickname", jsonUserObj.getString("nickname"));
                    map.put("user_logo", Url.USERHEADURL + jsonUserObj.getString("avatar128"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                postReplyList.add(map);
            }
            Message message = new Message();
            message.what = 0x130;
            message.obj = postReplyList;
            mHandler.sendMessage(message);
        }
    }

    //实时更新帖子点赞数和回复数线程
    private class PostAllCount extends Thread implements Runnable{

        private JSONObject jsonObject;

        public PostAllCount() {
            super();
        }
        @Override
        public void run() {
            jsonObject = groupApi.getPostCount("?s=" + Url.POSTPRM, post_id);
            countMap = new HashMap<String,Integer>();
            try {
                countMap.put("supportCount",Integer.parseInt(jsonObject.getString("supportCount")));
                countMap.put("replyCount",Integer.parseInt(jsonObject.getString("reply_count")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = 0x135;
            mHandler.sendMessage(message);
        }
    }

    //解决ListView在scrollView中显示不全
    public static class Utility {
        public static void setListViewHeightBasedOnChildren(IssueListView listView,int width) {
            //获取ListView对应的Adapter
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
                View listItem = listAdapter.getView(i, null, listView);
                //符合中文的解决方式，据说完美，but还未尝试过
                int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
                listItem.measure(desiredWidth, 0);   //计算子项View 的宽高

                totalHeight += listItem.getMeasuredHeight();
                Log.e("totalHeight>>>>>>>", listItem.getMeasuredHeight() + "");
            }
            Log.e("totalHeight>>>>>>>", totalHeight + "");
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            //listView.getDividerHeight()获取子项间分隔符占用的高度
            //params.height最后得到整个ListView完整显示需要的高度
            listView.setLayoutParams(params);
        }

    }
    //标记状态
    private void initFlag(boolean support,boolean postComment){
        SUPPORT=support;
        POSTCOMMENT=postComment;
    }

}