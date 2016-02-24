package com.thinksky.tox;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.fragment.IssueFragment;
import com.thinksky.net.IsNet;
import com.tox.IssueData;
import com.tox.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by 王杰 on 2015/3/20.
 */
public class IssueActivity2 extends FragmentActivity{
    ArrayList<HashMap<String,String>> issueTitle;
    private LinearLayout layout;
    private ListView listView;
    private PopupWindow popupWindow;


    ImageView mMenu,mWritePost;
    IssueFragmentAdapter myIssueFragmentAdapter;
    ViewPager pager;
    protected PagerSlidingTabStrip tabs;
    ArrayList<HashMap<String,String >> titles;
    ArrayList<HashMap<String,String>> spinnerList;
    ArrayList<HashMap<String,String>> issueTitles;
    ArrayList<String> spinnerArray;
    TextView issueSpinner;
    Handler mHandler;
    ArrayAdapter myAdapter;
    TextView test1;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        mMenu = (ImageView) findViewById(R.id.Menu);
        tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        pager =(ViewPager)findViewById(R.id.pager);
        mWritePost = (ImageView) findViewById(R.id.Issue_writePost);
        //设置一个监听器，点击后返回菜单页
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //点击后，跳转到PostIssueActivity页面
        mWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Issue = new Intent(IssueActivity2.this, PostIssueActivity.class);
                startActivity(intent_Issue);
            }
        });
        mHandler = new Handler(){
            @Override
            @SuppressWarnings(value = {"unchecked"})
            public void handleMessage(Message msg) {

                titles = (ArrayList<HashMap<String, String>>) msg.obj;

                spinnerArray = new ArrayList<String>();
                spinnerList = new ArrayList<HashMap<String,String>>();
                issueTitles = new ArrayList<HashMap<String,String>>();
                for (int i = 0;i<titles.size();i++) {
                    if (Integer.parseInt(titles.get(i).get("pid")) == 0) {
                        spinnerArray.add(titles.get(i).get("title"));
                        spinnerList.add(titles.get(i));
                    }
                    else {
                        issueTitles.add(titles.get(i));
                    }
                }
                issueSpinner = (TextView) findViewById(R.id.spinner);
                final ArrayAdapter<String> adapter111=new ArrayAdapter<String>(IssueActivity2.this,R.layout.issue_daohang_item,spinnerArray);
                issueSpinner.setText(adapter111.getItem(0));
                issueTitle = new ArrayList<HashMap<String,String>>();
                issueTitle.add(spinnerList.get(0));
                for (int i =0;i < issueTitles.size();i++){
                    if (Integer.parseInt(spinnerList.get(0).get("issue_id")) == Integer.parseInt(issueTitles.get(i).get("pid"))){
                        issueTitle.add(issueTitles.get(i));
                    }
                }
//            test1.setText(issueTitle.toString()+position+"阿达");
                myIssueFragmentAdapter = new IssueFragmentAdapter(getSupportFragmentManager(),issueTitle);
                pager.setAdapter(myIssueFragmentAdapter);
                tabs.setViewPager(pager);




                issueSpinner.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(IssueActivity2.this,"hahaha",Toast.LENGTH_SHORT).show();
                        // 找到布局文件
                        Log.d("Popup","Onclick");
                        layout = (LinearLayout) LayoutInflater.from(IssueActivity2.this).inflate(R.layout.mypinner_dropdown, null);
                        // 实例化listView
                        listView = (ListView) layout.findViewById(R.id.listView111);
                        // 设置listView的适配器
                        listView.setAdapter(adapter111);
                        // 实例化一个PopuWindow对象
                        popupWindow = new PopupWindow(v);
                        // 设置弹框的宽度为布局文件的宽
                        Log.d("width:-------->:", String.valueOf(issueSpinner.getWidth()));
                        popupWindow.setWidth(issueSpinner.getWidth());
                        // 高度随着内容变化
                        popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
                        // 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        // 设置点击弹框外部，弹框消失
                        popupWindow.setOutsideTouchable(true);
                        // 设置焦点
                        popupWindow.setFocusable(true);
                        // 设置所在布局
                        popupWindow.setContentView(layout);
                        // 设置弹框出现的位置，在v的正下方横轴偏移textview的宽度，为了对齐~纵轴不偏移
                        popupWindow.showAsDropDown(v, -issueSpinner.getWidth(), 0);
                        // listView的item点击事件
                        listView.setOnItemClickListener(new OnItemClickListener() {


                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                // TODO Auto-generated method stub
                                issueSpinner.setText(spinnerArray.get(arg2));// 设置所选的item作为下拉框的标题
                                // 弹框消失
                                popupWindow.dismiss();
                                popupWindow = null;

                                issueTitle = new ArrayList<HashMap<String,String>>();
                                issueTitle.add(spinnerList.get(arg2));
                                for (int i =0;i < issueTitles.size();i++){
                                    if (Integer.parseInt(spinnerList.get(arg2).get("issue_id")) == Integer.parseInt(issueTitles.get(i).get("pid"))){
                                        issueTitle.add(issueTitles.get(i));
                                    }
                                }
//            test1.setText(issueTitle.toString()+position+"阿达");
                                myIssueFragmentAdapter = new IssueFragmentAdapter(getSupportFragmentManager(),issueTitle);
                                pager.setAdapter(myIssueFragmentAdapter);
                                tabs.setViewPager(pager);
                            }

                        });



                        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            ArrayList<HashMap<String,String>> issueTitle;

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                issueTitle = new ArrayList<HashMap<String,String>>();
                                issueTitle.add(spinnerList.get(position));
                                for (int i =0;i < issueTitles.size();i++){
                                    if (Integer.parseInt(spinnerList.get(position).get("issue_id")) == Integer.parseInt(issueTitles.get(i).get("pid"))){
                                        issueTitle.add(issueTitles.get(i));
                                    }
                                }
//            test1.setText(issueTitle.toString()+position+"阿达");
                                myIssueFragmentAdapter = new IssueFragmentAdapter(getSupportFragmentManager(),issueTitle);
                                pager.setAdapter(myIssueFragmentAdapter);
                                tabs.setViewPager(pager);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    }
                });
            }
        };
        if (IsNet.IsConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    titles = new ArrayList<HashMap<String, String>>();

                    IssueData issueData = new IssueData();
                    try {
                        ArrayList<JSONObject> jsonList = issueData.getJson("?s=" + Url.ISSUEMODULES);
                        for (int i = 0; i < jsonList.size(); i++) {
                            HashMap<String, String> map1 = new HashMap<String, String>();
                            JSONObject jsonObj = jsonList.get(i);
                            map1.put("pid", jsonObj.getString("pid"));
                            map1.put("issue_id", jsonObj.getString("id"));
                            map1.put("title", jsonObj.getString("title"));
                            titles.add(map1);
                            JSONArray jsonObjsErJis = jsonList.get(i).optJSONArray("Issues");
                            if (jsonObjsErJis != null) {
                                for (int j = 0; j < jsonObjsErJis.length(); j++) {
                                    HashMap<String, String> map2 = new HashMap<String, String>();
                                    JSONObject jsonObjsErJi = (JSONObject) jsonObjsErJis.opt(j);
                                    map2.put("pid", jsonObjsErJi.getString("pid"));
                                    map2.put("issue_id", jsonObjsErJi.getString("id"));
                                    map2.put("title", jsonObjsErJi.getString("title"));
                                    titles.add(map2);
                                }
                            }
                        }
                        Message message = new Message();
                        message.obj = titles;
                        mHandler.sendMessageDelayed(message, 10);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            Toast.makeText(IssueActivity2.this,"网络不可用，请检查网络是否开启！",Toast.LENGTH_LONG).show();
        }
    }
    private class IssueFragmentAdapter extends FragmentStatePagerAdapter {

        ArrayList<HashMap<String,String >> issueTitle;
        IssueFragment fragment;

        public IssueFragmentAdapter(FragmentManager fm,ArrayList<HashMap<String,String >> titles) {
            super(fm);
                this.issueTitle = titles;
        }
        @Override
        public Fragment getItem(int position) {

            fragment = new IssueFragment();
            Bundle screenID = new Bundle();
            screenID.putInt("issue_id",Integer.parseInt(issueTitle.get(position).get("issue_id")));
            screenID.putInt("pid",Integer.parseInt(issueTitle.get(position).get("pid")));
            fragment.setArguments(screenID);

            return fragment;
        }
        @Override
        public int getCount() {
            return issueTitle.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0){
                return "全部分类";
            }
            return issueTitle.get(position).get("title");
        }
    }

    //spinner监听器

    //还未使用
    public interface IssueCallBack {
        public void callback(int flag);
    }
}