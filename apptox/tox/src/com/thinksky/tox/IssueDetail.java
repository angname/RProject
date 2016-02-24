package com.thinksky.tox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
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
import com.thinksky.redefine.CircleImageView;
import com.tox.IssueApi;
import com.tox.IssueData;
import com.tox.ToastHelper;
import com.tox.Url;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.bitmap.KJBitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 王杰 on 2015/3/20.
 */
public class IssueDetail extends Activity{
    //detail中的控件
    TextView issue_title;
    ImageView issue_image;
    View listLoad;
    int lastVisibleReplyIndex;
    int MaxReplyNum;
    IssueListView repListView;
    ProgressBar image_progress;
    TextView issue_signature;
    ScrollView scrollView;
    //ListView中的控件
    TextView issue_userName;
    CircleImageView issue_userImage;
    TextView detail_supportCounts;
    TextView detail_reply_count;
    TextView issue_reply_load;
    ProgressBar issue_reply_proBar;
    IssueApi issueApi;
    int issueID;
//    TextView issDetails;
    TextView issDetails;
    ImageView issue_back;
    //获取详情主页面的数据
    ArrayList<HashMap<String,String >> issue_info;
    //回复数据
    ArrayList<HashMap<String,String >> reply_infos;
    ArrayList<HashMap<String,String >> reply_info;

    ArrayList<HashMap<String,String >> replyList;
    KJBitmap kjBitmap;
    IssueListAdapter myAdapter;
    Handler mHandler;
    //定义点赞的按钮
    private LinearLayout zan;
    //session_id
    private String session_id;
    //session_id和status容器 arr[0]是点赞有没有成功   arr[1]是点赞返回的信息！！
    private String arr[]=null;
    //声明评论按钮
     LinearLayout Post_detail_comBtn;
    private Handler handler;
    //声明评论文本框
    private EditText issue_index_edittext;
    //声明评论的布局
    private LinearLayout issue_editBox;
    //声明评论"发表"
     TextView issue_index_send_com;
    //声明访问网站的按钮
    private LinearLayout issue_internet;
//    //声明回复评论---
//    private LinearLayout issue_huifu;
//    private EditText issue_huifu_edittext;
//    private TextView issue_huifu_send_com;
//    private LinearLayout huiFuPingLun;

    //JSON解析点赞
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
    //开一个关于点赞的线程
    class MyThread extends Thread{
        @Override
        public void run() {
            Log.d("sessionj_id",session_id);
            System.out.println("issueID>>>>>>"+issueID);
            HttpClient httpClient=new DefaultHttpClient();
            HttpGet httpGet=new HttpGet(Url.HTTPURL+"?s="+Url.SUPPORTISSUE+"&session_id="+session_id+"&id="+issueID);

            try {
                HttpResponse resp=httpClient.execute(httpGet);
                //检查响应的状态是否正常,检查状态码的值是否等于200
                int code=resp.getStatusLine().getStatusCode();
                if (code==200) {
                    //从响应对象当中取出数据
                    HttpEntity entity = resp.getEntity();
                    InputStream in = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = reader.readLine();
//                    textView.setText(line);
                    MyJson(line);
                    Message msg = handler.obtainMessage();
                    msg.what=3;
                    msg.obj = arr[0];
                    handler.sendMessage(msg);
                    Log.d("HTTP", "从服务器取得的数据为:" + arr[0]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



       //获取评论的线程
    class ContentThread extends Thread{
        @Override
        public void run() {
            IssueData issueData = new IssueData();
            ArrayList<JSONObject> jsonList= issueData.getIssueReplyJson("?s=" + Url.GETISSUECOMMENTS, issueID);
            replyList = new ArrayList<HashMap<String,String >>();
            try {
                for (int i = 0; i < jsonList.size(); i++) {
                    JSONObject jsonObj = jsonList.get(i);
                    HashMap<String, String> map1 = new HashMap<String, String>();
                    map1.put("Comments_content", jsonObj.getString("content"));
                    map1.put("Comments_create_time", jsonObj.getString("create_time"));
                    //评论回复用户信息
                    try {
                        JSONObject userJSONObj = jsonObj.getJSONObject("user");
                        if (userJSONObj != null) {
                            map1.put("user_id", userJSONObj.getString("uid"));
                            map1.put("user_name", userJSONObj.getString("nickname"));
                            map1.put("user_image", issueData.getResourcesURL(userJSONObj.getString("avatar128")));
                            replyList.add(map1);
                        }
                    }catch (JSONException e)
                    {
                        map1.put("user_name","游客");
                        replyList.add(map1);
                    }
                }
                new IssueInfoTask().execute(issueID);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            Message message = new Message();
            message.obj = replyList;
            mHandler.sendMessage(message);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_issue_detail);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    //评论专辑的handler
                    case 1:
                        arr[0]= (String) msg.obj;
                        Log.d("Handler status",arr[0]);
                        if (arr[0]=="true") {
                            Thread thread1=new ContentThread();
                            thread1.start();
                            Toast.makeText(IssueDetail.this, "评论成功", Toast.LENGTH_SHORT).show();
                            issue_editBox.setVisibility(View.GONE);
                        }
                    break;
                    //回复专辑评论的handler
                    case 2:
                        arr[0]= (String) msg.obj;
                        Log.d("Handler status",arr[0]);
                        if (arr[0]=="true"){
                            Thread thread2=new ContentThread();
                            thread2.start();
                            Toast.makeText(IssueDetail.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    //专辑点赞的handler
                    case 3:
                        Log.d("Handler status",arr[0]);
                        Log.d("Handler message",arr[1]);
                        if (arr[1].equals("需要登录")){
                            Toast.makeText(IssueDetail.this,"未登入",Toast.LENGTH_SHORT).show();
                        }
                        else if(arr[0]=="false"&&arr[1].equals("您已经赞过，不能再赞了。")){
                            Toast.makeText(IssueDetail.this,"您已经赞过，不能再赞了。",Toast.LENGTH_SHORT).show();
                        }
                        else if (arr[0] == "true"){
                            Toast.makeText(IssueDetail.this,"点赞成功",Toast.LENGTH_SHORT).show();
                            new IssueInfoTask().execute(issueID);
                        }
                        break;

                    default:
                        break;

                 }
            }
        };
        //获取Detail里的控件
        issue_back = (ImageView) findViewById(R.id.Issue_Back_list);
        issue_title = (TextView) findViewById(R.id.issue_title);
        issue_image = (ImageView) findViewById(R.id.issue_image);
        issue_userName = (TextView) findViewById(R.id.issue_userName);
        issue_signature = (TextView) findViewById(R.id.issue_signature);
        issue_userImage = (CircleImageView) findViewById(R.id.issue_userImage);
        detail_supportCounts = (TextView) findViewById(R.id.detail_supportCounts);
        detail_reply_count = (TextView) findViewById(R.id.detail_reply_count);
        image_progress = (ProgressBar) findViewById(R.id.image_progress);
        //评论和回复列表
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        repListView = (IssueListView) findViewById(R.id.issue_list);
        //listView最后的加载按钮
        listLoad = getLayoutInflater().inflate(R.layout.issue_detail_listview_probar, null);
        issue_reply_load = (TextView)listLoad.findViewById(R.id.issue_reply_load);
        issue_reply_proBar = (ProgressBar)listLoad.findViewById(R.id.issue_reply_proBar);
        //获取Session_id
        issueApi = new IssueApi();
        session_id = issueApi.getSeesionId();
        Log.e("session_id>?>?>",session_id);

        //获取访问网络按钮
        issue_internet= (LinearLayout) findViewById(R.id.issue_internet);

        //点赞
        zan= (LinearLayout) findViewById(R.id.Post_detail_likeBtn);
        zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyThread().start();
            }
        });
        //new出点赞状态容器
        arr=new String[2];
        //获取评论布局
        issue_editBox= (LinearLayout) findViewById(R.id.issue_editBox);
        //获取评论的EditBox
        issue_index_edittext= (EditText) findViewById(R.id.issue_index_edittext);

        //获取评论的TextView
        issue_index_send_com= (TextView) findViewById(R.id.issue_index_send_com);
        issue_index_send_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到EditView中的评论
                final String com=issue_index_edittext.getText().toString();
                if (com.equals("")){
                    Toast.makeText(IssueDetail.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("issue_index_edittext", com);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpClient=new DefaultHttpClient();
                        String url=Url.HTTPURL+"?s="+Url.SENDISSUECOMMENT+"&session_id="+session_id+"&row_id="+issueID+"&content=";
                        HttpGet httpGet=new HttpGet(url+URLEncoder.encode(com));
                        try {
                            HttpResponse resp=httpClient.execute(httpGet);
                            //检查响应的状态是否正常,检查状态码的值是否等于200
                            int code=resp.getStatusLine().getStatusCode();
                            if (code==200) {
                                //从响应对象当中取出数据
                                HttpEntity entity = resp.getEntity();
                                InputStream in = entity.getContent();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                String line = reader.readLine();
//                    textView.setText(line);
                                MyJson(line);
                                Message msg = handler.obtainMessage();
                                msg.what=1;
                                msg.obj = arr[0];
                                handler.sendMessage(msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        //获取评论按钮
        Post_detail_comBtn= (LinearLayout) findViewById(R.id.Post_detail_comBtn);
        Post_detail_comBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (issue_editBox.getVisibility()==View.GONE){
                 issue_editBox.setVisibility(View.VISIBLE);
                }
                else
                {
                    issue_editBox.setVisibility(View.GONE);
                }
            }
        });
        //专辑详情
        issDetails = (TextView)findViewById(R.id.iss_details);
        Bundle id = this.getIntent().getExtras();
        issueID = (int)id.getLong("id");

        //listView的异步加载器
        new IssueInfoTask().execute(issueID);
        mHandler = new Handler() {
            @Override
            @SuppressWarnings(value = {"unchecked"})
            public void handleMessage(Message msg) {
                reply_infos = (ArrayList<HashMap<String, String>>) msg.obj;
                // 加上底部View，注意要放在setAdapter方法前
                repListView.addFooterView(listLoad);
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
                myAdapter = new IssueListAdapter(IssueDetail.this, reply_info, R.layout.issue_detail_listview_item,
                        null, new int[]{R.id.replyTime, R.id.replyTextView});
                repListView.setAdapter(myAdapter);
                //设置scrollView初始化时始终在顶部
                scrollView.smoothScrollTo(0, 20);
            }
        };

        Thread thread=new ContentThread();
        thread.start();

        repListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int totalItemCount;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 计算最后可见条目的索引
                lastVisibleReplyIndex = firstVisibleItem + visibleItemCount - 1;
                this.totalItemCount = totalItemCount;
            }
        });

        issue_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueDetail.this.finish();
            }
        });
        issue_reply_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issue_reply_proBar.setVisibility(View.VISIBLE);
                issue_reply_load.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        LoadingMoreReply();
                        issue_reply_load.setVisibility(View.VISIBLE);
                        issue_reply_proBar.setVisibility(View.GONE);
                        myAdapter.notifyDataSetChanged();
                    }
                }, 2000);
                // 所有的条目已经和最大条数相等，则移除底部的View
                if (MaxReplyNum == myAdapter.getCount() && MaxReplyNum > 0) {
                    repListView.removeFooterView(listLoad);
                    Toast.makeText(IssueDetail.this,"已没有更多评论", Toast.LENGTH_SHORT).show();
                }
                else if (MaxReplyNum == 0){
//                    repListView.removeFooterView(listLoad);
                    issue_reply_load.setText("还没有评论");
                    Toast.makeText(IssueDetail.this,"还没有人评论，留个脚印吧", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //主页面的异步加载器
    private class IssueInfoTask extends AsyncTask<Integer,Void,ArrayList<HashMap<String,String>>> {

        private ArrayList<HashMap<String,String >> comments;
        int issueID;
        String str;

        @Override
        protected void onPostExecute(final ArrayList<HashMap<String,String>> comments) {
            //为每个文本控件赋值
            issue_title.setText(comments.get(0).get("title"));
            //专辑介绍正则处理
//            str = comments.get(0).get("content").replaceAll("[<][/pbr]{2,3}[>]","\n").replaceAll("[<][^>]+[>]","");
//            Log.e("测试：",comments.get(0).get("content"));
            str = comments.get(0).get("content");
            issDetails.setText(Html.fromHtml(str));
            //给textView加滚动条
//            issDetails.setMovementMethod(new ScrollingMovementMethod());
//            issDetails.setBackgroundColor(0);
//            issDetails.loadDataWithBaseURL(null, comments.get(0).get("content"), "text/html", "utf-8", null);
//            issDetails.getSettings().setSupportZoom(true);
            issue_userName.setText(comments.get(0).get("user_name"));
            detail_supportCounts.setText(comments.get(0).get("support_count"));
            detail_reply_count.setText(comments.get(0).get("reply_count"));
            if(comments.get(0).get("signature").length() != 0) {
                issue_signature.setText(comments.get(0).get("signature"));
            }else {
                issue_signature.setText("主人太懒，还没有个性签名");
            }
            //为图片控件加载数据
            kjBitmap = KJBitmap.create();
            kjBitmap.display(issue_image,comments.get(0).get("cover_url"));
            issue_image.setVisibility(View.VISIBLE);
            image_progress.setVisibility(View.GONE);
            kjBitmap.display(issue_userImage, comments.get(0).get("user_image"));

            issue_userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueApi.goUserInfo(IssueDetail.this, comments.get(0).get("user_id"));
                }
            });

            issue_internet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url=comments.get(0).get("url");
                    Log.d("url------------------>","url");
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                }
            });
        }
        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(Integer... params) {
            try{
                issueID = params[0];
                IssueData issueData = new IssueData();
                JSONArray jsonList= issueData.getIssueDetailJson("?s=" + Url.GETISSUEDETAIL, issueID);
                comments = new ArrayList<HashMap<String,String >>();
                HashMap<String, String> map1 = new HashMap<String, String>();
                //专辑详情
                if (jsonList.length() > 0) {
                    JSONObject jsonObj = (JSONObject) jsonList.get(0);
                    map1.put("id", jsonObj.getString("id"));
                    map1.put("title", jsonObj.getString("title"));
                    map1.put("cover_url", issueData.getResourcesURL(jsonObj.getString("cover_url")));
                    map1.put("content", jsonObj.getString("content"));
                    map1.put("url", jsonObj.getString("url"));
                    map1.put("reply_count", jsonObj.getString("reply_count"));
                    map1.put("support_count", jsonObj.getString("support_count"));
                    //发布专辑用户信息
                    JSONObject userJSONObj = jsonObj.getJSONObject("user");
                    map1.put("user_id", userJSONObj.getString("uid"));
                    map1.put("user_name", userJSONObj.getString("nickname"));
                    map1.put("signature", userJSONObj.getString("signature"));
                    map1.put("user_image", issueData.getResourcesURL(Url.USERHEADURL + userJSONObj.getString("avatar128")));
                    comments.add(map1);
                    return comments;
                }
            }catch (NullPointerException e){
                ToastHelper.showToast("获取数据失败，请重试",IssueDetail.this);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //ViewHolder缓存类
    private class ViewHolder{
        //listView中的控件
        ImageView replyUserImage;
        TextView replyUserName;
        TextView replyTime;
        TextView replyTextView;
        RelativeLayout huiFuPingLun;
        LinearLayout issue_huifu;
        EditText issue_huifu_edittext;
        TextView issue_huifu_send_com;
    }
    private class IssueListAdapter extends SimpleAdapter{
        KJBitmap kjbImage;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            kjbImage = KJBitmap.create();
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.issue_detail_listview_item,null);
                //获取listView中的控件
                viewHolder.replyUserImage = (ImageView) convertView.findViewById(R.id.replyUserImage);
                viewHolder.replyUserName = (TextView) convertView.findViewById(R.id.replyUserName);
                viewHolder.replyTime = (TextView) convertView.findViewById(R.id.replyTime);
                viewHolder.replyTextView = (TextView) convertView.findViewById(R.id.replyTextView);
                //获取回复专辑中的评论
                viewHolder.huiFuPingLun= (RelativeLayout) convertView.findViewById(R.id.huiFuPingLun);
                viewHolder.issue_huifu= (LinearLayout)convertView.findViewById(R.id.issue_huifu);
                viewHolder.issue_huifu_edittext= (EditText)convertView.findViewById(R.id.issue_huifu_edittext);
                viewHolder.issue_huifu_send_com= (TextView)convertView.findViewById(R.id.issue_huifu_send_com);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.huiFuPingLun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.issue_huifu.getVisibility()==View.GONE) {
                            viewHolder.issue_huifu.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            viewHolder.issue_huifu.setVisibility(View.GONE);
                        }
                    }
                });
                 viewHolder.issue_huifu_send_com.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                 final String s=viewHolder.issue_huifu_edittext.getText().toString();
                 if (s.equals("")){
                     Toast.makeText(IssueDetail.this,"回复不能为空",Toast.LENGTH_SHORT).show();
                     return;
                 }
                 final String name=viewHolder.replyUserName.getText().toString();
                 new Thread(new Runnable() {
                     @Override
                     public void run() {
                            HttpClient httpClient=new DefaultHttpClient();
                            String url=Url.HTTPURL+"?s="+Url.SENDISSUECOMMENT+"&session_id="+session_id+"&row_id="+issueID+"&content=";
                            HttpGet httpGet=new HttpGet(url+URLEncoder.encode("回复 @"+name+":"+s));
                         try {
                             HttpResponse resp=httpClient.execute(httpGet);
                             //检查响应的状态是否正常,检查状态码的值是否等于200
                             int code=resp.getStatusLine().getStatusCode();
                             if (code==200) {
                                 //从响应对象当中取出数据
                                 HttpEntity entity = resp.getEntity();
                                 InputStream in = entity.getContent();
                                 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                 String line = reader.readLine();
                                 MyJson(line);
                                 Message msg = handler.obtainMessage();
                                 msg.what=2;
                                 msg.obj = arr[0];
                                 handler.sendMessage(msg);
                             }
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 }).start();
//                         Toast.makeText(IssueDetail.this,name,Toast.LENGTH_SHORT).show();
                 viewHolder.issue_huifu.setVisibility(View.GONE);
                     }
                 });
                //给控件赋值
                if (reply_info.get(position).get("user_image") != null) {
                    kjbImage.display(viewHolder.replyUserImage, reply_info.get(position).get("user_image"));
                }
                viewHolder.replyUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reply_info.get(position).get("user_id") != null) {
                            issueApi.goUserInfo(mContext, reply_info.get(position).get("user_id"));
                        }
                    }
                });
                viewHolder.replyUserName.setText(reply_info.get(position).get("user_name"));
                viewHolder.replyTextView.setText(reply_info.get(position).get("Comments_content"));
                viewHolder.replyTime.setText(reply_info.get(position).get("Comments_create_time"));

            return convertView;
        }
        private Context mContext;
        public IssueListAdapter(Context context, ArrayList<HashMap<String,String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.mContext = context;
        }
        @Override
        public int getCount() {
            return reply_info.size();
        }
    }
    //评论分页加载
    private void LoadingMoreReply(){
        int count = myAdapter.getCount();
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
}