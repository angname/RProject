package com.thinksky.tox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.tox.IssueApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 王杰 on 2015/5/8.
 */
public class PostGroupActivity extends Activity implements View.OnClickListener{
    private ArrayList<HashMap<String, String>> categoryList;
    //arr[0]是发送成功   arr[1]是返回的信息
    private String arr[]=null;
    private Spinner spinner;
    private String[] arr_id;
    private String[] arr_title;
    private static String POS="0";
    private int i=0;
    private RelativeLayout post_Send;
    private RelativeLayout post_send_Back;
    private EditText postSendTitleEdit;
    private EditText postSendContentEdit;
    private String titleEdit = null;
    private String contentEdit = null;
    private String session_id;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0xaaa:
                    String arg[]=new String[2];
                    arg= (String[]) msg.obj;
//                    Log.d("wj...",arg[0]);
//                    Log.d("wj...", arg[1]);
                    if (arg[0]=="true"){
                        Toast.makeText(PostGroupActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PostGroupActivity.this,arg[1] + "发布失败",Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.grouppost);
        //获取session_id
        IssueApi issueApi=new IssueApi();
        session_id = issueApi.getSeesionId();
        arr=new String[2];
        post_Send= (RelativeLayout) findViewById(R.id.post_Send);
        post_Send.setOnClickListener(this);
        post_send_Back= (RelativeLayout) findViewById(R.id.post_send_Back);
        post_send_Back.setOnClickListener(this);
        postSendTitleEdit = (EditText) findViewById(R.id.postSendTitleEdit);
        postSendContentEdit = (EditText) findViewById(R.id.postSendContentEdit);
        categoryList= (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("categoryList");
        categoryList.remove(0);
        Iterator iter = categoryList.iterator();
        arr_id=new String[categoryList.size()];
        arr_title=new String[categoryList.size()];
        while(iter.hasNext()){
            iter.next();
            arr_id[i]=categoryList.get(i).get("id");
            arr_title[i]=categoryList.get(i).get("title");
            i++;
        }
        spinner= (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter(this,R.layout.simple_list_item_activity_1,arr_title));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()){
                    case R.id.spinner:
                        POS=arr_id[position];
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post_Send:
                if(!"".equals(postSendTitleEdit.getText().toString().trim()) && !"".equals(postSendContentEdit.getText().toString().trim())) {
                    titleEdit = postSendTitleEdit.getText().toString();
                    contentEdit = postSendContentEdit.getText().toString();
                    if (contentEdit.length()<25){
                        ToastHelper.showToast("内容长度不能小于25",PostGroupActivity.this);
                    }else {
                        new Post().start();
                    }
                }else {
                    ToastHelper.showToast("请输入有效内容", PostGroupActivity.this);
                }
                break;
            case R.id.post_send_Back:
                finish();
                break;
            default:
                break;
        }
    }

    //发布帖子的线程
    class Post extends Thread {
        BufferedReader reader;
        @Override
        public void run() {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Url.HTTPURL + "?s="+ Url.SEND);
                // 创建名/值组列表
                Intent intent=getIntent();
                int group_id=intent.getExtras().getInt("group_id");
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("session_id",session_id));
                params.add(new BasicNameValuePair("title", URLDecoder.decode(titleEdit, "UTF-8")));
                params.add(new BasicNameValuePair("content", URLDecoder.decode(contentEdit, "UTF-8")));
                params.add(new BasicNameValuePair("group_id", group_id+""));
                params.add(new BasicNameValuePair("cate_id",POS));
                Log.d("group_id",group_id+"");
                Log.d("pos",POS);
                Log.d("title", titleEdit);
                Log.d("contentEdit",contentEdit);
                Log.d("session_id",session_id);
                // 创建UrlEncodedFormEntity对象
                UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(formEntiry);
                // 执行请求
                HttpResponse response = httpClient.execute(httpPost);
                int code=response.getStatusLine().getStatusCode();
                if (code==200) {
                    //从响应对象当中取出数据
                    HttpEntity entity = response.getEntity();
                    InputStream in = entity.getContent();
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = reader.readLine();
                    Log.d("line",line);
                    MyJson(line);
                    Message msg = handler.obtainMessage();
                    msg.obj=arr;
                    msg.what=0xaaa;
                    handler.sendMessage(msg);
                    Log.d("HTTP", "从服务器取得的数据为:" + arr[0]);
                }else {
                    Toast.makeText(PostGroupActivity.this,"网络连接失败，发布失败",Toast.LENGTH_SHORT).show();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
