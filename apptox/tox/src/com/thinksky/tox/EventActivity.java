package com.thinksky.tox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.fragment.EventFragment;
import com.thinksky.net.IsNet;
import com.tox.EventApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 王杰 on 2015/4/24.
 */
public class EventActivity extends FragmentActivity {


    ImageView event_Menu;
    ImageView event_WritePost;
    LinearLayout top_probar;
    RelativeLayout top_pager;
    ViewPager event_top_pager;
    PagerSlidingTabStrip event_tabs;
    ViewPager event_pager;
    private Handler mHandler;
    ArrayList<HashMap<String,String>> title;
    ArrayList<HashMap<String,String>> hotTops;
    EventFragmentAdapter myEventFragmentAdapter;
    HotTopAdapter vpagerAdapter;
    EventApi eventJson;
    private LinearLayout linearLayout;
    ImageView vPager_choice1;
    ImageView vPager_choice2;
    ImageView vPager_choice3;
    ImageView vPager_choice4;
    private ImageView[] tips;
    private int nowPage = 0;
    Bundle bundle;

    TextView test1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //获取控件
        event_Menu = (ImageView) findViewById(R.id.event_Menu);
        event_tabs = (PagerSlidingTabStrip) findViewById(R.id.event_tabs);
        event_pager = (ViewPager) findViewById(R.id.event_pager);
        event_top_pager = (ViewPager) findViewById(R.id.event_top_pager);
        top_probar = (LinearLayout) findViewById(R.id.top_probar);
        top_pager = (RelativeLayout) findViewById(R.id.top_pager);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        vPager_choice1 = (ImageView) findViewById(R.id.vPager_choice1);
        vPager_choice2 = (ImageView) findViewById(R.id.vPager_choice2);
        vPager_choice3 = (ImageView) findViewById(R.id.vPager_choice3);
        vPager_choice4 = (ImageView) findViewById(R.id.vPager_choice4);

        //测试专用文本
//        test1 = (TextView) findViewById(R.id.test1);

        event_WritePost= (ImageView) findViewById(R.id.event_WritePost);
        event_WritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Event = new Intent(EventActivity.this, PostEventActivity.class);
                startActivity(intent_Event);
            }
        });
        event_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        eventJson = new EventApi();
        //初始化 提示点点
        tips = new ImageView[]{vPager_choice1,vPager_choice2,vPager_choice3,vPager_choice4};
        //更改当前tip
        event_top_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tips[nowPage].setBackgroundResource(R.drawable.eventchoice);
                nowPage = position;
                tips[position].setBackgroundResource(R.drawable.eventchoiceyes);
            }
        });
        mHandler = new Handler(){

            private int count = 0;

            @Override
            @SuppressWarnings(value = {"unchecked"})
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 0x110:
                        title = (ArrayList<HashMap<String,String>>)msg.obj;
                        myEventFragmentAdapter = new EventFragmentAdapter(getSupportFragmentManager(),title);
                        event_pager.setAdapter(myEventFragmentAdapter);
                        event_tabs.setViewPager(event_pager);
                        break;
                    case 0x111:
                        hotTops = (ArrayList<HashMap<String,String>>)msg.obj;
                        ArrayList<HashMap<String,String>> hotTop = new ArrayList<HashMap<String,String>>();
                        for (int i = 0; i < hotTops.size() && count < 4; i++) {
                            if (Integer.parseInt(hotTops.get(i).get("is_recommend")) == 1 && Integer.parseInt(hotTops.get(i).get("is_end")) == 0) {
                                hotTop.add(hotTops.get(i));
                                count++;
                            }
                        }
                        if (hotTop.size() == 0 || hotTop.size() < 4) {
                            int topCount = hotTop.size();
                            if ( hotTops.size() >= 4 - topCount) {
                                for (int i = 0; i < 4 - topCount; i++) {
                                    hotTop.add(hotTops.get(i));
                                }
                            }else {
                                for (int i = 0; i < hotTops.size(); i++) {
                                    hotTop.add(hotTops.get(i));
                                }
                            }
                        }

                        vpagerAdapter = new HotTopAdapter(hotTop);
                        event_top_pager.setAdapter(vpagerAdapter);
                        linearLayout.setVisibility(View.VISIBLE);
                        top_pager.setVisibility(View.VISIBLE);
                        top_probar.setVisibility(View.GONE);
                        break;
                }
            }
        };
        if (IsNet.IsConnect()) {
            new Thread(new Runnable() {
                private ArrayList<HashMap<String, String>> titles;

                @Override
                public void run() {
                    ArrayList<JSONObject> jsonObjectArrayList = eventJson.getEventJson("?s=" + Url.EVENTMODULES);
                    titles = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("pid", "0");
                    map.put("event_id", "0");
                    map.put("title", "全部活动");
                    titles.add(map);
                    try {
                        for (int i = 0; i < jsonObjectArrayList.size(); i++) {
                            HashMap<String, String> map1 = new HashMap<String, String>();
                            JSONObject jsonObj = jsonObjectArrayList.get(i);
                            map1.put("pid", jsonObj.getString("pid"));
                            map1.put("event_id", jsonObj.getString("id"));
                            map1.put("title", jsonObj.getString("title"));
                            titles.add(map1);
                            JSONArray jsonObjsErJis = jsonObjectArrayList.get(i).optJSONArray("EventSecond");
                            if (jsonObjsErJis != null) {
                                for (int j = 0; j < jsonObjsErJis.length(); j++) {
                                    HashMap<String, String> map2 = new HashMap<String, String>();
                                    JSONObject jsonObjsErJi = (JSONObject) jsonObjsErJis.opt(j);
                                    map2.put("pid", jsonObjsErJi.getString("pid"));
                                    map2.put("event_id", jsonObjsErJi.getString("id"));
                                    map2.put("title", jsonObjsErJi.getString("title"));
                                    titles.add(map2);
                                }
                            }
                        }
                        Message message = new Message();
                        message.what = 0x110;
                        message.obj = titles;
                        mHandler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }

        if (IsNet.IsConnect()) {
            new Thread(new Runnable() {
                private ArrayList<HashMap<String, String>> hotTops;

                @Override
                public void run() {
                    ArrayList<JSONObject> jsonObjectArrayList = eventJson.getEventJson("?s=" + Url.EVENTSALL);
                    hotTops = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < jsonObjectArrayList.size(); i++) {
                        JSONObject jsonObj = jsonObjectArrayList.get(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        try {
                            map.put("id", jsonObj.getString("id"));
                            map.put("title", jsonObj.getString("title"));
                            map.put("is_end", jsonObj.getString("is_end"));
                            map.put("is_recommend", jsonObj.getString("is_recommend"));
                            map.put("cover_url", Url.USERHEADURL + jsonObj.getString("cover_url"));
                            hotTops.add(map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = new Message();
                    message.what = 0x111;
                    message.obj = hotTops;
                    mHandler.sendMessage(message);
                }
            }).start();
        }else{
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }
    }

    //导航栏适配器
    private class EventFragmentAdapter extends FragmentStatePagerAdapter {

        ArrayList<HashMap<String,String >> eventTitle;
        EventFragment fragment;

        public EventFragmentAdapter(FragmentManager fm,ArrayList<HashMap<String,String >> titles) {
            super(fm);
            this.eventTitle = titles;
        }
        @Override
        public Fragment getItem(int position) {

            fragment = new EventFragment();
            Bundle screenID = new Bundle();
            screenID.putInt("type_id",Integer.parseInt(eventTitle.get(position).get("event_id")));
            fragment.setArguments(screenID);
            return fragment;
        }
        @Override
        public int getCount() {
            return eventTitle.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return eventTitle.get(position).get("title");
        }
    }
    //hotTop适配器
    private class HotTopAdapter extends PagerAdapter{

        private ImageView hotImage;
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            hotImage = new ImageView(EventActivity.this);
            hotImage.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            hotImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(IsNet.IsConnect()) {
                        Intent intent = new Intent(EventActivity.this, EventDetailActivity.class);
                        intent.putExtra("event_id", hotTop.get(position).get("id"));
                        startActivity(intent);
                    }else{
                        ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                    }
                }
            });
            hotImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            KJBitmap kjb = KJBitmap.create();
            kjb.display(hotImage,hotTop.get(position).get("cover_url"));
            container.addView(hotImage);
            return hotImage;
        }

        @Override
        public void destroyItem(ViewGroup container,int position,Object o){

//            container.removeViewAt(position);
        }
        private ArrayList<HashMap<String,String>> hotTop;
        public HotTopAdapter(ArrayList<HashMap<String,String>> hotTop) {
            super();
            this.hotTop = hotTop;
        }

        @Override
        public int getCount() {
            return hotTop.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
