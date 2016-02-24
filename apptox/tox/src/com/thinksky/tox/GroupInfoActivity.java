package com.thinksky.tox;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.myview.MoreTextView;
import com.tox.BaseApi;
import com.tox.GroupApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/5/18 0018.
 */
public class GroupInfoActivity extends Activity implements View.OnClickListener{

    Context mContext;
    private static boolean PUBLICGROUP=false;
    private static boolean PRIVATEGROUP=false;
    private static boolean DISMISSGROUP=false;
    HashMap<String,String> groupInfoMap;
    private GroupApi groupApi;
    Bundle GroupBundle;
    String session_id;
    Boolean isWeGroup;
    Intent postIntent;
    Bundle postBundle;
    private int group_id;
    private int cateID = 0;
    private Context ctx;
    protected ScrollView group_scro;
    private LinearLayout linear_list;
    private LinearLayout linear_body;
    private LinearLayout linear_isnull;
    private RelativeLayout body_probar;
    protected ImageView back_menu;
    private ImageView group_post;
    private ImageView group_logo;
    private TextView group_name;
    private TextView group_type;
    private TextView group_type_name;
    private TextView post_count;
    private TextView man_count;
    private TextView join_status;
    protected ImageView cate_menu;
    private LinearLayout join_group;
    private LinearLayout post_at_top;
    private MoreTextView group_detail;
    protected RelativeLayout refreshButn;
    private ImageView refreshImage;
    ListView group_post_listView;
    private HashMap<String, String> titleMap;
    protected ArrayList<HashMap<String, String>> categoryList;
    private ArrayList<HashMap<String, String>> postInfoList;
    KJBitmap kjbImage;
    PopupWindow cateWindow;
    private boolean count = true;
    private int maxNumber = 0;
    private int page = 1;
    private int index = 0;
    private Intent post;
    private boolean joinFlag = false;
    private int isJoin;
    private long lastClick;

    @Override
    @SuppressWarnings(value = {"unchecked"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        ctx=this;
        mContext = GroupInfoActivity.this;
        session_id = new BaseApi().getSeesionId();
        groupApi = new GroupApi();
        kjbImage = KJBitmap.create();
        //获得上个activity传递的群组信息
        Intent groupIntent = this.getIntent();
        GroupBundle = groupIntent.getExtras();
        isWeGroup = groupIntent.getExtras().getBoolean("isWeGroup");
        groupInfoMap = (HashMap<String,String>)GroupBundle.getSerializable("group_info");
        Log.e("groupInfoMap>>>>>>>>",groupInfoMap.toString());

        group_id = Integer.parseInt(groupInfoMap.get("id"));
        //即时获取群组信息线程
        groupApi.setHandler(tempHandler);
        groupApi.getGroupInfo(String.valueOf(group_id));

        post = new Intent(mContext, PostGroupActivity.class);
        post.putExtra("group_id",group_id);

        back_menu = (ImageView) findViewById(R.id.back_menu);
        group_post = (ImageView) findViewById(R.id.group_post);
        group_scro = (ScrollView) findViewById(R.id.group_scro);
        linear_list = (LinearLayout) findViewById(R.id.linear_list);
        linear_body = (LinearLayout) findViewById(R.id.linear_body);
        linear_isnull = (LinearLayout) findViewById(R.id.linear_isnull);
        body_probar = (RelativeLayout) findViewById(R.id.body_probar);
        group_logo = (ImageView) findViewById(R.id.group_logo);
        group_name = (TextView) findViewById(R.id.group_name);
        group_type = (TextView) findViewById(R.id.group_type);
        group_type_name = (TextView) findViewById(R.id.group_type_name);
        post_count = (TextView) findViewById(R.id.post_count);
        man_count = (TextView) findViewById(R.id.man_count);
        join_status= (TextView) findViewById(R.id.join_status);
        cate_menu = (ImageView) findViewById(R.id.cate_menu);

        join_group = (LinearLayout) findViewById(R.id.join_group);
        post_at_top = (LinearLayout) findViewById(R.id.post_at_top);
        group_post_listView = (ListView) findViewById(R.id.group_post_listView);
        group_detail = (MoreTextView) findViewById(R.id.group_detail);
        refreshButn = (RelativeLayout) findViewById(R.id.refresh_butn);
        refreshImage = (ImageView) findViewById(R.id.refresh_image);


        group_logo.setOnClickListener(this);
        back_menu.setOnClickListener(this);
        group_post.setOnClickListener(this);
        linear_isnull.setOnClickListener(this);
        cate_menu.setOnClickListener(this);
        join_group.setOnClickListener(this);
        refreshButn.setOnClickListener(this);

        InitGroupView(groupInfoMap);
        //ListView的滑动监听器
        group_scro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        index++;
                        break;
                    default:
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
                    index = 0;
                    View view = ((ScrollView) v).getChildAt(0);
                    if (count && view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                        //加载数据代码
                        if (maxNumber < page * 10) {
                            ToastHelper.showToast("没有更多内容", mContext);
                            count = false;
                        } else {
                            page++;
                            ToastHelper.showToast("正在加载更多内容", mContext);
                            new GroupPostThread(page, cateID).start();
                        }
                    }
                }
                return false;
            }
        });

        //获取帖子分类线程
        postInfoList = new ArrayList<HashMap<String, String>>();
        new CategoryThread().start();
        new TopPostThread(page).start();
        new GroupPostThread(page,cateID).start();


        //ListView的item点击监听器
        group_post_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                postInfoTo((HashMap<String, String>) parent.getItemAtPosition(position));
//                Log.e("postID>>>>>>>>>",parent.getItemAtPosition(position).toString());
            }
        });
        group_scro.smoothScrollTo(0, 0);
    }

    //初始化群组固有信息
    public void InitGroupView(HashMap<String,String> groupInfoMap){

        group_name.setText(groupInfoMap.get("title"));
        if (groupInfoMap.get("group_type").equals("1")){
            group_type.setText("私有群组");
        }
        group_detail.setText("群组简介：" + groupInfoMap.get("detail"));
        group_type_name.setText(groupInfoMap.get("type_name"));
        post_count.setText(groupInfoMap.get("post_count"));
        man_count.setText(groupInfoMap.get("memberCount"));
        if (groupInfoMap.get("group_logo").equals(Url.USERHEADURL +"Public/Core/images/nopic.png")) {
            kjbImage.display(group_logo, Url.USERHEADURL + "Public/Group/images/icon.jpg");
        }else {
            kjbImage.display(group_logo, groupInfoMap.get("group_logo"));
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.back_menu:
                finish();
                break;
            case R.id.group_post:
                sendPost();
                break;
            case R.id.cate_menu:
                cateWindow.showAsDropDown(group_post);
                break;
            case R.id.linear_isnull:
                sendPost();
                break;
            case R.id.group_logo:
                Intent intent = new Intent(mContext,GroupDetailActivity.class);
                intent.putExtra("groupInfoMap",groupInfoMap);
                startActivity(intent);
                break;
            case R.id.join_group:
                if (!groupApi.getSeesionId().equals("")) {
                    if (groupInfoMap.get("uid").equals(groupApi.getUid())) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("解散群组")
                                .setMessage("确定吗？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        initFlag(false, false, true);
                                        groupApi.setHandler(myHandler);
                                        groupApi.dismissGroup(group_id + "");
                                    }
                                })
                                .setNegativeButton("否", null)
                                .show();
                        break;
                    }
                    if (isJoin == -1) {
                        join_group.setClickable(false);
                    }
                    if (joinFlag) {
                        //加入群组
                        if (groupInfoMap.get("group_type").equals("1")) {
                            //加入的是私有群组
                            initFlag(false, true, false);
                        } else {
                            initFlag(true, false, false);
                        }
                        groupApi.setHandler(myHandler);
                        groupApi.joinGroupPost(group_id + "");
                        joinFlag = false;
                    } else {
                        //退出群组
                        new AlertDialog.Builder(mContext)
                                .setTitle("退出群组")
                                .setMessage("确定吗？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        groupApi.setHandler(myHandler);
                                        groupApi.quitGroupPost(group_id + "");
                                        joinFlag = true;
                                    }
                                })
                                .setNegativeButton("否", null)
                                .show();
                    }
                }else {
                    ToastHelper.showToast("请登陆后操作",mContext);
                    join_group.setClickable(false);
                }
                break;
            case R.id.refresh_butn:
                //大于2秒可以通过
                if(System.currentTimeMillis()-lastClick<=2000){
                    Toast.makeText(GroupInfoActivity.this,"请勿重复刷新",Toast.LENGTH_SHORT).show();
                    return;
                }
                lastClick=System.currentTimeMillis();
                postInfoList.clear();
                page = 1;
                count = true;
                new GroupPostThread(page,cateID).start();
                AnimationSet animationSet = new AnimationSet(true);
                RotateAnimation rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(2000);
                animationSet.addAnimation(rotateAnimation);
                refreshImage.startAnimation(animationSet);
                break;
            default:
                break;
        }
    }

    //判断发帖权限
    public void sendPost(){
        if (!joinFlag) {
            post.putExtra("categoryList",categoryList);
            startActivity(post);
        }else {
            ToastHelper.showToast("加群才能发帖",mContext);
        }
    }

    //封装的帖子信息发送方法
    public void postInfoTo(HashMap<String, String> mapTwo){

        postIntent = new Intent(mContext,GroupPostInfoActivity.class);
        postBundle = new Bundle();
        postBundle.putSerializable("post_info", mapTwo);
        postIntent.putExtras(postBundle);
        startActivity(postIntent);
    }

    //对加入群组的状态进行实时判断
    private Handler tempHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                String result = (String)msg.obj;
                groupInfoMap = groupApi.getGroupInfoMap(result, groupInfoMap);
                Log.e("groupInfoMap>>>>>>>>",groupInfoMap.toString());
                isJoin = Integer.parseInt(groupInfoMap.get("is_join"));
                if (isJoin == 1) {
                    joinFlag = false;
                    join_status.setText("退出群组");
                }
                if(groupInfoMap.get("uid").equals(groupApi.getUid())){
                    join_status.setText("解散群组");
                }
                if(isJoin == -1){
                    join_status.setText("已申请，审核中");
                    join_group.setClickable(false);
                }else if (isJoin != 1){
                    join_status.setText("+加入群组");
                    joinFlag = true;
                }
            }
        }
    };

    private Handler myHandler = new Handler(){

        private ArrayList<HashMap<String, String>> categoryList;
        private ArrayList<HashMap<String, String>> topPostInfoList;

        @Override
        @SuppressWarnings(value = {"unchecked"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (DISMISSGROUP){
                        Intent intent=new Intent();
                        intent.putExtra("isWeGroup", isWeGroup);
                        setResult(1, intent);
                        GroupInfoActivity.this.finish();
                        break;
                    }
                    if (joinFlag){
                        joinFlag = true;
                        ToastHelper.showToast("退出群组成功",ctx);
                        join_status.setText("+加入群组");
                    }else{
                        if (PUBLICGROUP) {
                            joinFlag = false;
                            ToastHelper.showToast("加入群组成功", ctx);
                            join_status.setText("退出群组");
                        }
                        if (PRIVATEGROUP){
                            join_group.setClickable(false);
                            ToastHelper.showToast("申请加入群组成功", ctx);
                            join_status.setText("正在审核");
                        }
                    }
                    break;
                case 800:
                    if (PUBLICGROUP){
                        if (joinFlag) {
                            join_status.setText("+加入群组");
                            ToastHelper.showToast("退群失败，还未加入该群", ctx);
                        }else {
                            join_status.setText("+退出群组");
                            ToastHelper.showToast("你已加入该群", ctx);
                        }
                    }
                    if (PRIVATEGROUP) {
                        join_status.setText("已申请，审核中");
                        join_group.setClickable(false);
                    }
                    break;
                case 0x120:
                    categoryList = (ArrayList<HashMap<String, String>>) msg.obj;
//                    Log.e("categoryList>>>>>>>>",categoryList.toString());
                    createCatePopWindow(categoryList);
                    break;
                case 0x122:
                    maxNumber = postInfoList.size();
                    if (postInfoList.size() != 0){
                        linear_list.setVisibility(View.VISIBLE);
                        linear_isnull.setVisibility(View.GONE);
                        group_post_listView.setAdapter(new GroupListAdapter(mContext, postInfoList, R.layout.group_post_item, null, null));
                        Utility.setListViewHeightBasedOnChildren(group_post_listView);
                    }else {
                        linear_isnull.setVisibility(View.VISIBLE);
                        linear_list.setVisibility(View.GONE);
                    }
                    linear_body.setVisibility(View.VISIBLE);
                    body_probar.setVisibility(View.GONE);
                    break;
                //置顶区帖子
                case 0x124:
                    topPostInfoList = (ArrayList<HashMap<String, String>>) msg.obj;
                    //设置子控件间距
                    LinearLayout.LayoutParams linerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    linerParams.setMargins(0,0,0,2);
                    if (topPostInfoList.size() > 0) {
                        for (HashMap<String, String> map:topPostInfoList) {
                            LinearLayout tempLinear = new LinearLayout(mContext);
                            tempLinear.setOrientation(LinearLayout.HORIZONTAL);
                            tempLinear.setLayoutParams(linerParams);
                            tempLinear.setPadding(5, 5, 5, 5);
                            tempLinear.setId(Integer.parseInt(map.get("id")));
                            tempLinear.setTag(map);
                            tempLinear.setBackgroundResource(R.drawable.item_bg);
                            tempLinear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    postInfoTo((HashMap<String, String>) v.getTag());
//                                    Log.e("postID>>>>>>>>>", v.getTag().toString());
                                }
                            });
                            ImageView topImage = new ImageView(mContext);
                            topImage.setPadding(0, 0, 20, 0);
                            topImage.setImageResource(R.drawable.ic_top);
                            TextView topText = new TextView(mContext);
                            topText.setTextColor(Color.parseColor("#000000"));
                            topText.setSingleLine(true);
                            topText.setEllipsize(TextUtils.TruncateAt.END);
                            topText.setText(map.get("title"));
                            tempLinear.addView(topImage);
                            tempLinear.addView(topText);
                            post_at_top.addView(tempLinear);
                        }
                    }else {
                        TextView tempView = new TextView(mContext);
                        tempView.setBackgroundResource(R.drawable.item_bg);
                        tempView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendPost();
                            }
                        });
                        tempView.setTextColor(Color.parseColor("#666666"));
                        tempView.setText("大家一起来讨论吧");
                        post_at_top.addView(tempView,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //ListView适配器
    private class GroupListAdapter extends SimpleAdapter{

        private ArrayList<HashMap<String, String>> postInfoList;
        private int resource;
        private ViewHolder viewHolder;

        public GroupListAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.resource = resource;
            this.postInfoList = data;
        }

        @Override
        public long getItemId(int position) {
            return Integer.parseInt(postInfoList.get(position).get("id"));
        }

        @Override
        public int getCount() {
            return postInfoList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            KJBitmap kjbImage = KJBitmap.create();
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(resource, null);
                viewHolder.user_image = (ImageView) convertView.findViewById(R.id.user_image);
                viewHolder.post_title = (TextView) convertView.findViewById(R.id.post_title);
                viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.post_category = (TextView) convertView.findViewById(R.id.post_category);
                viewHolder.view_count = (TextView) convertView.findViewById(R.id.view_count);
                viewHolder.reply_count = (TextView) convertView.findViewById(R.id.reply_count);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            kjbImage.display(viewHolder.user_image,postInfoList.get(position).get("user_logo"));
            viewHolder.user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupApi.goUserInfo(mContext,postInfoList.get(position).get("user_uid"));
                }
            });
            viewHolder.post_title.setText(postInfoList.get(position).get("title"));
            viewHolder.user_name.setText(postInfoList.get(position).get("user_nickname"));
            viewHolder.post_category.setText(titleMap.get(postInfoList.get(position).get("cate_id")));
            viewHolder.view_count.setText(postInfoList.get(position).get("view_count"));
            viewHolder.reply_count.setText(postInfoList.get(position).get("reply_count"));
            return convertView;
        }
    }

    //解决ListView在scrollView中显示不全
    public static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            //获取ListView对应的Adapter
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);  //计算子项View 的宽高
//                int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
//                listItem.measure(desiredWidth, 0);
                totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight()* listAdapter.getCount());
            //listView.getDividerHeight()获取子项间分隔符占用的高度
            //params.height最后得到整个ListView完整显示需要的高度
            listView.setLayoutParams(params);
        }

    }

    //控件缓存器
    private class ViewHolder{
        ImageView user_image;
        TextView post_title;
        TextView user_name;
        TextView post_category;
        TextView view_count;
        TextView reply_count;
    }

    //帖子分类导航线程
    class CategoryThread extends Thread implements Runnable{

        private ArrayList<JSONObject> jsonObjArrayList;

        public CategoryThread() {
            super();
        }

        @Override
        public void run() {
            categoryList = new ArrayList<HashMap<String, String>>();
            titleMap = new HashMap<String,String>();
            jsonObjArrayList = groupApi.getGroupCategory("?s=" + Url.POSTCATEGORY, group_id);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id","0");
            map.put("group_id",String.valueOf(group_id));
            map.put("title","全部分类");
            map.put("status","1");
            categoryList.add(map);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                HashMap<String, String> map1 = new HashMap<String, String>();
                try {
                    titleMap.put(jsonObj.getString("id"),jsonObj.getString("title"));
                    map1.put("id", jsonObj.getString("id"));
                    map1.put("group_id", jsonObj.getString("group_id"));
                    map1.put("title", jsonObj.getString("title"));
                    map1.put("status", jsonObj.getString("status"));

                    categoryList.add(map1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Message message = new Message();
            message.what = 0x120;
            message.obj = categoryList;
            myHandler.sendMessage(message);
        }
    }

    //全部帖子信息线程
    class GroupPostThread extends Thread implements Runnable{

        private ArrayList<JSONObject> jsonObjArrayList;
        private int page;
        private int cateID;

        public GroupPostThread(int page,int cateID) {
            this.page = page;
            this.cateID = cateID;
        }

        @Override
        public void run() {
            jsonObjArrayList = groupApi.getGroupPostList("?s=" + Url.POSTALL, page,group_id,cateID,session_id);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                postInfoList.add(getPostMap(jsonObj));
            }
            Message message = new Message();
            message.what = 0x122;
            myHandler.sendMessage(message);
        }
    }

    //置顶帖子信息线程
    class TopPostThread extends Thread implements Runnable{

        private ArrayList<JSONObject> jsonObjArrayList;
        ArrayList<HashMap<String, String>> topPostInfoList;
        private int page;

        public TopPostThread(int page) {
            this.page = page;
        }

        @Override
        public void run() {
            topPostInfoList = new ArrayList<HashMap<String, String>>();
            jsonObjArrayList = groupApi.getGroupPostList("?s=" + Url.TOPPOST, page, group_id,0,session_id);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                topPostInfoList.add(getPostMap(jsonObj));
            }
            Message message = new Message();
            message.what = 0x124;
            message.obj = topPostInfoList;
            myHandler.sendMessage(message);
        }
    }

    //封装的帖子信息
    public HashMap<String, String> getPostMap(JSONObject jsonObj){

        HashMap<String, String> map1 = new HashMap<String, String>();
        try {
            map1.put("id", jsonObj.getString("id"));
            map1.put("uid", jsonObj.getString("uid"));
            map1.put("group_id", jsonObj.getString("group_id"));
            map1.put("group_name", groupInfoMap.get("title"));
            map1.put("title", jsonObj.getString("title"));
            map1.put("content", jsonObj.getString("content"));
            map1.put("create_time", jsonObj.getString("create_time"));
            map1.put("update_time", jsonObj.getString("update_time"));
            map1.put("last_reply_time", jsonObj.getString("last_reply_time"));
            map1.put("status", jsonObj.getString("status"));
            map1.put("view_count", jsonObj.getString("view_count"));
            map1.put("reply_count", jsonObj.getString("reply_count"));
            map1.put("is_top", jsonObj.getString("is_top"));
            map1.put("cate_id", jsonObj.getString("cate_id"));
            map1.put("supportCount", jsonObj.getString("supportCount"));
            map1.put("is_support", jsonObj.getString("is_support"));
            JSONObject tempJSONObj = jsonObj.getJSONObject("user");
            map1.put("user_uid", tempJSONObj.getString("uid"));
            map1.put("user_nickname", tempJSONObj.getString("nickname"));
            map1.put("user_logo", Url.USERHEADURL + tempJSONObj.getString("avatar128"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return map1;
    }

    //创建帖子分类PopWindow
    private void createCatePopWindow(ArrayList<HashMap<String, String>> cateList){
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_postcate_popview, null);
        final LinearLayout post_cateLinear = (LinearLayout) view.findViewById(R.id.post_cate);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        cateWindow = new PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可以通过点击屏幕其他地方自动消失
        cateWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        cateWindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        cateWindow.setAnimationStyle(R.style.CatePopWindow);
        //导航标题块
        LinearLayout.LayoutParams tempTextPar= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tempTextPar.setMargins(10, 10, 0, 10);
        for (int i =0;i < cateList.size();i++){
            if (Integer.parseInt(cateList.get(i).get("status")) == 1) {
                TextView cateLabel = new TextView(mContext);
                cateLabel.setPadding(10, 5, 15, 5);
                cateLabel.setLayoutParams(tempTextPar);
                cateLabel.setText(cateList.get(i).get("title"));
                cateLabel.setId(Integer.parseInt(cateList.get(i).get("id")));
                if (i == 0){
                    cateLabel.setBackgroundColor(Color.parseColor("#EDEDED"));
                }
                cateLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        v.setClickable(false);
                        postInfoList.clear();
                        count = true;
                        page = 1;
                        setBackColor(post_cateLinear);
                        v.setBackgroundColor(Color.parseColor("#EDEDED"));
                        cateID = v.getId();
                        new GroupPostThread(page,cateID).start();
                        //防暴力点击
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(true);
                            }
                        }, 1500);
                    }
                });
                post_cateLinear.addView(cateLabel);
            }
        }
    }

    //清空导航标签背景色
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackColor(LinearLayout layout){
        for (int i =0;i <layout.getChildCount();i++ ) {
            layout.getChildAt(i).setBackground(null);
        }
    }

    //标记加入的是私有还是共有群组
    public void initFlag(boolean publicgroup,boolean privategroup,boolean dismissgroup){
        PUBLICGROUP=publicgroup;
        PRIVATEGROUP=privategroup;
        DISMISSGROUP=dismissgroup;
    }
}