package com.thinksky.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.myview.MyListView;
import com.thinksky.net.IsNet;
import com.thinksky.tox.EventDetailActivity;
import com.thinksky.tox.R;
import com.tox.EventApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    View fragmentView;
    private MyListView event_list;
    Bundle eventData;
    private int type_id;
    private LinearLayout event_pager_probar;
    private ArrayList<HashMap<String,String>> eventLists;
    ArrayList<HashMap<String,String>> eventList;
    private boolean loadFlag = true;
    private boolean count = true;
    public static final int PAGE_SIZE = 10;
    private int page;
    private boolean isLastRow = false;
    private int maxDataIndex;
    EventListAdapter mAdapter;
    private SwipeRefreshLayout refresh;
    TextView test;
    boolean flag=false;
    private Handler mHandler = new Handler(){
        @Override
        @SuppressWarnings(value = {"unchecked"})
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            eventLists = (ArrayList<HashMap<String, String>>) msg.obj;
            maxDataIndex = eventLists.size();
            eventList.clear();
            Log.d("eventLists", String.valueOf(eventLists));
//                loadMoreImages(eventLists);
            int startIndex = 0;
            int endIndex = 10;
            if (startIndex < eventLists.size()) {
                if (endIndex > eventLists.size()) {
                    endIndex = eventLists.size();
                }
                for (int i = startIndex; i < endIndex; i++) {
                    eventList.add(eventLists.get(i));
                }
            }

            mAdapter = new EventListAdapter(context, eventList, R.layout.event_listview_item, null, null);
            event_list.setAdapter(mAdapter);
            //ListView滑动底部加载事件
            event_list.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    int mAdapterCount = mAdapter.getCount();
                    // 滑到底部后自动加载，判断listView已经停止滚动并且最后可视的条目等于adapter的条目
                    if (count && isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && maxDataIndex == mAdapterCount) {
                        Toast.makeText(context, "已没有更多内容", Toast.LENGTH_SHORT).show();
                        count = false;
                    } else if (count && isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        //当滑到底部时自动加载
                        page++;
                        Toast.makeText(getActivity(), "正在加载更多数据！", Toast.LENGTH_SHORT).show();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                loadMoreImages(eventLists);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
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

            flag=true;
            event_list.setVisibility(View.VISIBLE);
            event_pager_probar.setVisibility(View.GONE);
//            event_list.onRefreshComplete();
//            loadFlag = true;
        }
    };

    public void loadMoreImages(ArrayList<HashMap<String,String>>  eventLists){
        int startIndex = page * PAGE_SIZE;
        int endIndex = page * PAGE_SIZE + PAGE_SIZE;
        if (startIndex < eventLists.size()) {
            if (endIndex > eventLists.size()) {
                endIndex = eventLists.size();
            }
            for (int i = startIndex; i < endIndex; i++) {
                eventList.add(eventLists.get(i));
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        eventData = getArguments();
        type_id = Integer.parseInt(eventData.get("type_id").toString());
        eventList = new ArrayList<HashMap<String,String>>();
        if (IsNet.IsConnect()) {
            new MyThread().start();
        }else{
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        fragmentView = inflater.inflate(R.layout.event_fragment, container, false);
        event_list = (MyListView)fragmentView.findViewById(R.id.event_list);
        refresh= (SwipeRefreshLayout) fragmentView.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        refresh.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        event_pager_probar = (LinearLayout) fragmentView.findViewById(R.id.event_pager_probar);
        //测试模块
//        test = (TextView)fragmentView.findViewById(R.id.testText);
//        test.setText(eventLists.size() + "");

        //ListView的下拉刷新监听器
//        event_list.setonRefreshListener(new MyListView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (loadFlag) {
//                    new MyThread().start();
//                    loadFlag = false;
//                } else {
//                    Toast.makeText(context, "正在加载中，请勿重复刷新", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        //listView的item点击监听器
        event_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (IsNet.IsConnect()) {
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra("event_id", String.valueOf(id));
                    context.startActivity(intent);
                }else{
                    ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onRefresh() {
        if (IsNet.IsConnect()) {
            eventLists.clear();
            new MyThread().start();
            count = true;
            page = 0;
        }else{
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
            }
        },1500);
    }

    //MyListView适配器
    private class EventListAdapter extends SimpleAdapter{

        int resource;
        ArrayList<HashMap<String,String>> eventList;

        public EventListAdapter(Context context, ArrayList<HashMap<String,String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.resource = resource;
            this.eventList = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            KJBitmap kjbImage = KJBitmap.create();
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(resource, null);
                viewHolder.event_image = (ImageView) convertView.findViewById(R.id.event_image);
                viewHolder.prompt_info = (TextView) convertView.findViewById(R.id.prompt_info);
                viewHolder.event_title = (TextView) convertView.findViewById(R.id.event_title);
                viewHolder.event_userName = (TextView) convertView.findViewById(R.id.event_userName);
                viewHolder.signCount = (TextView) convertView.findViewById(R.id.signCount);
                viewHolder.event_time = (TextView) convertView.findViewById(R.id.event_time);
                viewHolder.event_endTime = (TextView) convertView.findViewById(R.id.event_endTime);
                viewHolder.event_content = (TextView) convertView.findViewById(R.id.event_content);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            //为控件赋值
            kjbImage.display(viewHolder.event_image,eventList.get(position).get("event_image"));
            viewHolder.event_title.setText(eventList.get(position).get("event_title"));
            viewHolder.event_userName.setText(eventList.get(position).get("event_nickname"));
            viewHolder.event_time.setText(eventList.get(position).get("sTime") + " -- " + eventList.get(position).get("eTime"));
            viewHolder.event_endTime.setText(eventList.get(position).get("deadline"));
            viewHolder.event_content.setText(eventList.get(position).get("explain"));
            viewHolder.signCount.setText(eventList.get(position).get("signCount"));
            if (Integer.parseInt(eventList.get(position).get("is_end")) == 0){
                viewHolder.prompt_info.setText("正在进行");
                viewHolder.prompt_info.setBackgroundColor(Color.parseColor("#D61F39"));
            }else if (Integer.parseInt(eventList.get(position).get("is_end")) == 1){
                viewHolder.prompt_info.setText("已结束");
                viewHolder.prompt_info.setBackgroundColor(Color.parseColor("#000000"));
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return eventList.size();
        }

        @Override
        public long getItemId(int position) {
            return Integer.parseInt(eventList.get(position).get("event_id"));
        }
    }
    //加载的线程类
    private class MyThread extends Thread implements Runnable{

        public MyThread() {
            super();
        }
        @Override
        public void run() {
            eventLists = new ArrayList<HashMap<String, String>>();
            EventApi eventApi = new EventApi();
            ArrayList<JSONObject> jsonList = eventApi.getEventList("?s=" + Url.EVENTSALL,type_id);
            for (int i = 0; i < jsonList.size(); i++) {
                JSONObject jsonObj = jsonList.get(i);
                HashMap<String,String> map = new HashMap<String, String>();
                try {
                    map.put("event_id", jsonObj.getString("id"));
                    map.put("event_title", jsonObj.getString("title"));
                    map.put("is_recommend", jsonObj.getString("is_recommend"));
                    map.put("explain", jsonObj.getString("explain"));
                    map.put("limitCount", jsonObj.getString("limitCount"));
                    map.put("sTime", jsonObj.getString("sTime"));
                    map.put("eTime", jsonObj.getString("eTime"));
                    map.put("create_time", jsonObj.getString("create_time"));
                    map.put("update_time", jsonObj.getString("update_time"));
                    map.put("deadline", jsonObj.getString("deadline"));
                    map.put("address", jsonObj.getString("address"));
                    map.put("signCount", jsonObj.getString("signCount"));
                    map.put("is_deadline", jsonObj.getString("is_deadline"));
                    map.put("is_end", jsonObj.getString("is_end"));

//                    JSONObject imageURL = (JSONObject) jsonObj.get("cover_url");
                    map.put("event_image", Url.USERHEADURL + jsonObj.getString("cover_url"));
                    JSONObject userName = (JSONObject) jsonObj.get("user");
                    map.put("event_nickname", userName.getString("nickname"));
                    eventLists.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Message message = new Message();
            message.obj = eventLists;
            mHandler.sendMessage(message);
        }
    }
    //ViewHolder缓存类
    private class ViewHolder{
        ImageView event_image;
        TextView prompt_info;
        TextView event_title;
        TextView event_userName;
        TextView signCount;
        TextView event_time;
        TextView event_endTime;
        TextView event_content;
    }
}