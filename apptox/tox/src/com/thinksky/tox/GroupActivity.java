package com.thinksky.tox;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.model.ActivityModel;
import com.thinksky.myview.MyListView;
import com.tox.BaseApi;
import com.tox.GroupApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/5/7 0007.
 */
public class GroupActivity extends Activity implements View.OnClickListener {

    RelativeLayout group_top;
    protected ImageView group_menu;
    protected ImageView add_group;
    ImageView typeMenu;
    protected TextView my_group_menu,progressBarText;
    protected TextView all_group_menu;
    protected MyListView groupListView;
    private LinearLayout group_probar;
    ArrayList<JSONObject> jsonObjArrayList;
    ArrayList<HashMap<String, String>> allGroupList;
    private HashMap<String, String> tempMap;
    Animation animation;
    private GroupApi groupApi;
    private String session_id;
    private String userUid;
    private int page = 1;
    private boolean isLastRow = false;
    boolean count = true;
    boolean onClick = false;
    private boolean isWeGroup = true;
    MyGroupAdapter myGroupAdapter;
    int maxNumber;
    Context context;
    PopupWindow window;
    private int type_id;
    private BaseApi baseApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        groupApi = new GroupApi();
        baseApi = new BaseApi();
        session_id = baseApi.getSeesionId();
        userUid = baseApi.getUid();
        allGroupList = new ArrayList<HashMap<String, String>>();
        context = GroupActivity.this;
        new TempThread().start();
        group_top = (RelativeLayout) findViewById(R.id.group_top);
        group_menu = (ImageView) findViewById(R.id.group_menu);
        typeMenu = (ImageView) findViewById(R.id.type_menu);
        add_group = (ImageView) findViewById(R.id.add_group);
        my_group_menu = (TextView) findViewById(R.id.my_group_menu);
        all_group_menu = (TextView) findViewById(R.id.all_group_menu);
        groupListView = (MyListView) findViewById(R.id.groupListView);
        group_probar = (LinearLayout) findViewById(R.id.group_probar);
        progressBarText = (TextView) findViewById(R.id.progressBar_text);
        group_menu.setOnClickListener(this);
        add_group.setOnClickListener(this);
        my_group_menu.setOnClickListener(this);
        all_group_menu.setOnClickListener(this);
        animation = AnimationUtils.loadAnimation(this,R.anim.popshow_anim);
        typeMenu.setOnClickListener(this);

        if (!baseApi.getSeesionId().equals("")) {
            group_probar.setVisibility(View.VISIBLE);
            progressBarText.setVisibility(View.GONE);
            new MyGroupThread().start();
        }else {
            progressBarText.setVisibility(View.VISIBLE);
            ToastHelper.showToast("未登陆，请登陆", context);
        }
        new GroupTypeThread(0).start();

        progressBarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("entryActivity", ActivityModel.USERINFO);
                startActivity(intent);
            }
        });

        //gridView滑动加载监听器
        groupListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                // 滑到底部后自动加载
                if (onClick && count && isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && maxNumber < page * 10) {
                    Toast.makeText(GroupActivity.this, "已没有更多内容", Toast.LENGTH_SHORT).show();
                    count = false;
                } else if ( onClick && count && isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //当滑到底部时自动加载
                    Toast.makeText(GroupActivity.this, "正在加载更多内容！", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page ++;
                            new AllGroupThread(page,type_id).start();
                        }
                    }, 2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 计算最后可见条目的索引
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    isLastRow = true;
                }
            }
        });
        //listView的item点击监听器
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                Log.e("333333>>>>>>>>>>>", parent.getItemAtPosition(position).toString());
                bundle.putSerializable("group_info", (HashMap<String, String>) parent.getItemAtPosition(position));
                bundle.putBoolean("isWeGroup", isWeGroup);
                Intent intent = new Intent(context,GroupInfoActivity.class);
                intent.putExtras(bundle);
//                startActivity(intent);
                startActivityForResult(intent,0);
            }
        });
    }

    //页面重新前置时触发本函数
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Url.activityFrom", Url.activityFrom + "activityFrom");
        if (isWeGroup && Url.activityFrom.equals(Url.USERID + "UserInfoActivity")){
            Url.activityFrom = "";
            new MyGroupThread().start();
            progressBarText.setVisibility(View.GONE);
            group_probar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1){
            Log.d("回传isWeGroup的值", data.getExtras().getBoolean("isWeGroup") + "");
            if (data.getExtras().getBoolean("isWeGroup")) {
                new MyGroupThread().start();
            }else {
                page = 1;
                allGroupList = new ArrayList<HashMap<String, String>>();
                new AllGroupThread(page,type_id).start();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.group_menu:
                onBackPressed();
                break;
            case R.id.add_group:
                if (!baseApi.getSeesionId().equals("")) {
                    Intent allGroupIntent = new Intent(GroupActivity.this,AddGroupActivity.class);
                    startActivity(allGroupIntent);
                }else {
                    ToastHelper.showToast("未登陆", context);
                }
                break;
            //我的群组
            case R.id.my_group_menu:
                isWeGroup = true;
                groupListView.setAdapter(null);
                groupListView.setVisibility(View.GONE);
                onClick = false;
                if (!baseApi.getSeesionId().equals("")) {
                    progressBarText.setVisibility(View.GONE);
                    group_probar.setVisibility(View.VISIBLE);
                    new MyGroupThread().start();
                }else {
                    progressBarText.setVisibility(View.VISIBLE);
//                    ToastHelper.showToast("未登陆，请登陆", context);
                }
                all_group_menu.setTextColor(getResources().getColor(R.color.tab));
                all_group_menu.setBackgroundColor(getResources().getColor(R.color.head));
                my_group_menu.setBackgroundColor(getResources().getColor(R.color.tab));
                my_group_menu.setTextColor(Color.parseColor("#ffffff"));
                typeMenu.setVisibility(View.GONE);
                break;
            //全部群组
            case R.id.all_group_menu:
                isWeGroup = false;
                all_group_menu.setClickable(false);
                progressBarText.setVisibility(View.GONE);
                group_probar.setVisibility(View.VISIBLE);
                groupListView.setVisibility(View.GONE);
                //清空适配器数据
                allGroupList.clear();
                page =1;
                //支持滑动到底自动加载操作
                onClick = true;
                isLastRow = false;
                //跳转重置
                count = true;
                new AllGroupThread(page,type_id).start();

                my_group_menu.setTextColor(getResources().getColor(R.color.tab));
                my_group_menu.setBackgroundColor(getResources().getColor(R.color.head));
                all_group_menu.setBackgroundColor(getResources().getColor(R.color.tab));
                all_group_menu.setTextColor(Color.parseColor("#ffffff"));
                typeMenu.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        all_group_menu.setClickable(true);
                    }
                }, 2500);
                break;
            //弹出导航栏
            case R.id.type_menu:
                // 在标题栏下方显示
                int[] location = new int[2];
                group_top.getLocationOnScreen(location);
                window.showAtLocation(v, Gravity.NO_GRAVITY, 0, location[1] + group_top.getHeight());
                break;
        }
    }

    //导航栏线程
    class GroupTypeThread extends Thread implements Runnable{

        private HashMap<String,ArrayList> groupMap;
        private ArrayList<HashMap<String, String>> groupOneTypeList;
        private ArrayList<HashMap<String, String>> groupTwoTypeList;
        private int id;
        public GroupTypeThread(int id) {
            super();
            this.id =id;
        }

        @Override
        public void run() {
            groupMap = new HashMap<String,ArrayList>();
            jsonObjArrayList = groupApi.getGroupType("?s=" + Url.GROUPTYPE,id);
            groupOneTypeList = new ArrayList<HashMap<String, String>>();
            groupTwoTypeList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id","0");
            map.put("pid","0");
            map.put("title","全部分类");
            map.put("is_second", "0");
            groupOneTypeList.add(map);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                HashMap<String, String> map1 = new HashMap<String, String>();
                try {
                    map1.put("id", jsonObj.getString("id"));
                    map1.put("pid", jsonObj.getString("pid"));
                    map1.put("title", jsonObj.getString("title"));

                    JSONArray secondJsonArray = jsonObj.optJSONArray("GroupSecond");
                    if (secondJsonArray != null){
                        map1.put("is_second","1");
                        for (int j =0;j < secondJsonArray.length();j++) {
                            HashMap<String, String> map2 = new HashMap<String, String>();
                            JSONObject jsonObjSecond = (JSONObject) secondJsonArray.opt(j);
                            map2.put("id", jsonObjSecond.getString("id"));
                            map2.put("pid", jsonObjSecond.getString("pid"));
                            map2.put("title", jsonObjSecond.getString("title"));
                            groupTwoTypeList.add(map2);
                        }
                    }else {
                        map1.put("is_second","0");
                    }
                    groupOneTypeList.add(map1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            groupMap.put("classOne",groupOneTypeList);
            groupMap.put("classTwo",groupTwoTypeList);
            Message message = new Message();
            message.what = 0x110;
            message.obj = groupMap;
            mHandler.sendMessage(message);
        }
    }
    //我的群组线程
    class MyGroupThread extends Thread implements Runnable {

        private ArrayList<HashMap<String, String>> myGroupList;
        public MyGroupThread() {
            super();
        }

        @Override
        public void run() {
            ArrayList<JSONObject> jsonObjectArrayList = groupApi.getMyGroupList("?s=" + Url.WEGROUPALL, baseApi.getSeesionId());
            myGroupList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < jsonObjectArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjectArrayList.get(i);
                myGroupList.add(getGroupMap(jsonObj));
            }
            Message message = new Message();
            message.what = 0x115;
            message.obj = myGroupList;
            mHandler.sendMessage(message);
        }
    }
    //全部群组线程
    class AllGroupThread extends Thread implements Runnable {

        private int page;
        private int type_id;

        public AllGroupThread(int page,int type_id) {
            this.page = page;
            this.type_id = type_id;
        }

        @Override
        public void run() {

            ArrayList<JSONObject> jsonObjectArrayList = groupApi.getAllGroupList("?s=" + Url.GROUPALL, page,type_id,session_id);
            for (int i = 0; i < jsonObjectArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjectArrayList.get(i);
                allGroupList.add(getGroupMap(jsonObj));
            }
            Message message = new Message();
            message.what = 0x119;
            message.obj = allGroupList;
            mHandler.sendMessage(message);
        }
    }
    //单独的导航栏线程
    private class TempThread extends Thread implements Runnable{

        public TempThread() {
            super();
        }

        @Override
        public void run() {

            tempMap = new HashMap<String, String>();
            jsonObjArrayList = groupApi.getGroupType("?s=" + Url.GROUPTYPE, 0);
            for (int i = 0; i < jsonObjArrayList.size(); i++) {
                JSONObject jsonObj = jsonObjArrayList.get(i);
                try {
                    tempMap.put(jsonObj.getString("id"), jsonObj.getString("title"));
                    JSONArray secondJsonArray = jsonObj.optJSONArray("GroupSecond");
                    if (secondJsonArray != null) {
                        for (int j = 0; j < secondJsonArray.length(); j++) {
                            JSONObject jsonObjSecond = (JSONObject) secondJsonArray.opt(j);
                            tempMap.put(jsonObjSecond.getString("id"), jsonObjSecond.getString("title"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //封装群组信息
    public HashMap<String, String> getGroupMap(JSONObject jsonObj){

        String type_id;
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            type_id = jsonObj.getString("type_id");
            map.put("id", jsonObj.getString("id"));
            map.put("title", jsonObj.getString("title"));
            map.put("type_name", tempMap.get(type_id));
            map.put("uid", jsonObj.getString("uid"));
            map.put("post_count", jsonObj.getString("post_count"));
            map.put("group_logo", Url.USERHEADURL + jsonObj.getString("logo"));
            map.put("group_background", Url.USERHEADURL + jsonObj.getString("background"));
            map.put("type_id", type_id);
            map.put("detail", jsonObj.getString("detail"));
            map.put("group_type", jsonObj.getString("type"));
            map.put("activity", jsonObj.getString("activity"));
            map.put("memberCount", jsonObj.getString("menmberCount"));
            map.put("is_join", jsonObj.getString("is_join"));
            JSONObject tempObj = jsonObj.getJSONObject("user");
            map.put("user_uid", tempObj.getString("uid"));
            map.put("user_username", tempObj.getString("username"));
            map.put("user_nickname", tempObj.getString("nickname"));
            map.put("user_avatar128", Url.USERHEADURL + tempObj.getString("avatar128"));

        }catch (JSONException e){
            e.printStackTrace();
        }
        return map;
    }

    private Handler mHandler = new Handler() {

        private HashMap<String,ArrayList> groupMap;
        private ArrayList<HashMap<String, String>> groupList;

        @Override
        @SuppressWarnings(value = {"unchecked"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x110:
                    groupMap = (HashMap<String,ArrayList>) msg.obj;
                    Log.d("gruopMap",groupMap+"");
                    createPopWindow(groupMap.get("classOne"), groupMap.get("classTwo"));
                    break;
                case 0x115:
                    groupList = (ArrayList<HashMap<String, String>>) msg.obj;
                    Log.e("groupList>>>>>>>>>>",groupList.toString());
                    groupListView.setAdapter(new MyGroupAdapter(GroupActivity.this, groupList, R.layout.mygroup_item, null, null));
                    groupListView.setVisibility(View.VISIBLE);
                    group_probar.setVisibility(View.GONE);
                    break;
                case 0x119:
                    if (page == 1) {
                        groupList = (ArrayList<HashMap<String, String>>) msg.obj;
                        maxNumber = groupList.size();
                        myGroupAdapter = new MyGroupAdapter(GroupActivity.this, groupList, R.layout.mygroup_item, null, null);
                        groupListView.setAdapter(myGroupAdapter);
                        groupListView.setVisibility(View.VISIBLE);
                        group_probar.setVisibility(View.GONE);
                    }else {
                        maxNumber = groupList.size();
                        myGroupAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    //ListView适配器
    private class MyGroupAdapter extends SimpleAdapter {

        private ArrayList<HashMap<String, String>> myGroupList;
        private int resource;
        public MyGroupAdapter(Context context,ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.myGroupList = data;
            this.resource = resource;
        }

        @Override
        public long getItemId(int position) {
            return Integer.parseInt(myGroupList.get(position).get("id"));
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getCount() {
//            Log.e("myGroupList>>>>>>>>>",myGroupList.toString());
            return myGroupList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            KJBitmap kjbImage = KJBitmap.create();
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(GroupActivity.this).inflate(resource, null);
                viewHolder.group_title = (TextView) convertView.findViewById(R.id.group_title);
                viewHolder.group_detail = (TextView) convertView.findViewById(R.id.group_detail);
                viewHolder.is_master = (LinearLayout) convertView.findViewById(R.id.is_master);
                viewHolder.group_logo = (ImageView) convertView.findViewById(R.id.group_logo);
//                viewHolder.post_count = (TextView) convertView.findViewById(R.id.post_count);
                viewHolder.man_count = (TextView) convertView.findViewById(R.id.man_count);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            if (myGroupList.get(position).get("group_logo").equals(Url.USERHEADURL +"Public/Core/images/nopic.png")) {
                kjbImage.display(viewHolder.group_logo, Url.USERHEADURL + "Public/Group/images/icon.jpg");
            }else {
                kjbImage.display(viewHolder.group_logo, myGroupList.get(position).get("group_logo"));
            }
            if (myGroupList.get(position).get("user_uid").equals(userUid)){
                viewHolder.is_master.setVisibility(View.VISIBLE);
            }else {
                viewHolder.is_master.setVisibility(View.GONE);
            }
            viewHolder.group_title.setText(myGroupList.get(position).get("title"));
            viewHolder.group_detail.setText(myGroupList.get(position).get("detail"));
//            viewHolder.post_count.setText(myGroupList.get(position).get("post_count"));
            viewHolder.man_count.setText(myGroupList.get(position).get("memberCount"));
            return convertView;
        }
    }

    //生成的pop导航窗体
    private void createPopWindow(ArrayList<HashMap<String, String>> groupOneTypeList,final ArrayList<HashMap<String, String>> groupTwoTypeList){
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.poplayout, null);
        final LinearLayout first_group = (LinearLayout) view.findViewById(R.id.first_group);
        final LinearLayout second_group = (LinearLayout) view.findViewById(R.id.second_group);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可以通过点击屏幕其他地方自动消失
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.popWindow_anim_style);
        //导航标题块
        final LinearLayout.LayoutParams textLpars= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textLpars.setMargins(0,0,10,0);
        //添加导航标签
        for (int i = 0;i < groupOneTypeList.size();i++){
            final TextView classText = new TextView(context);
            classText.setPadding(15, 5, 15, 5);
            classText.setLayoutParams(textLpars);
            classText.setText(groupOneTypeList.get(i).get("title"));
            classText.setId(Integer.parseInt(groupOneTypeList.get(i).get("id")));
            classText.setTag(groupOneTypeList.get(i).get("is_second"));
            if (i == 0){
                classText.setBackgroundColor(Color.parseColor("#EDEDED"));
            }
            classText.setOnClickListener(new View.OnClickListener() {

                private int viewTag;

                @Override
                public void onClick(final View v) {
                    v.setClickable(false);
                    //清空适配器数据
                    second_group.removeAllViews();
                    setBackColor(first_group);
                    //点击加载群组信息
                    type_id = v.getId();
                    v.setBackgroundColor(Color.parseColor("#EDEDED"));

                    Log.d("type_id=", type_id + "");
                    Log.d("tag=", viewTag + "");
                    //点击生成2级导航目录
                    viewTag = Integer.parseInt(v.getTag().toString());
                    if (viewTag == 1) {
                        for (int j = -1; j < groupTwoTypeList.size(); j++) {
                            TextView twoClassText = new TextView(context);
                            twoClassText.setPadding(15, 5, 15, 5);
                            twoClassText.setLayoutParams(textLpars);
                            if (j == -1){
                                twoClassText.setText("全部");
                                twoClassText.setId(type_id);
                                twoClassText.setBackgroundColor(Color.parseColor("#EDEDED"));
                            }else {
                                if (groupTwoTypeList.get(j).get("pid").equals(String.valueOf(type_id))) {
                                    twoClassText.setText(groupTwoTypeList.get(j).get("title"));
                                    twoClassText.setId(Integer.parseInt(groupTwoTypeList.get(j).get("id")));
                                }else {
                                    continue;
                                }
                            }
                            twoClassText.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(final View v) {

                                    v.setClickable(false);
                                    setBackColor(second_group);
                                    type_id = v.getId();
                                    v.setBackgroundColor(Color.parseColor("#EDEDED"));
                                    count = true;
                                    isLastRow = false;
                                    page = 1;
                                    allGroupList.clear();
                                    new AllGroupThread(page, type_id).start();
                                    //防暴力点击
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setClickable(true);
                                        }
                                    }, 2000);
                                }
                            });
                            second_group.addView(twoClassText);

                        }
                    }
                    count = true;
                    isLastRow = false;
                    page = 1;
                    allGroupList.clear();
                    new AllGroupThread(page, type_id).start();
                    //防暴力点击
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setClickable(true);
                        }
                    }, 2000);
                }
            });
            first_group.addView(classText);
            }
        }
    //2级导航栏异步生成

    //清空导航标签背景色
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackColor(LinearLayout layout){
        for (int i =0;i <layout.getChildCount();i++ ) {
            layout.getChildAt(i).setBackground(null);
        }
    }

    private class ViewHolder{
        TextView group_title;
        TextView group_detail;
        LinearLayout is_master;
        ImageView group_logo;
        TextView post_count;
        TextView man_count;
    }
}