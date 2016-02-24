package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.MyJson;
import com.tox.IssueApi;
import com.tox.Url;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 王杰 on 2015/5/18.
 */
public class AddGroupActivity extends Activity implements AdapterView.OnItemSelectedListener,View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static int RESULT_LOAD_IMAGE = 1;
    private static String NUM = null;
    private String PICTURE = null;
    private String IMG_ID = null;
    private MyJson myJson = new MyJson();
    private String session_id;
    private Spinner spinner;
    private EditText group_title;
    private EditText group_content;
    private List<HashMap<String, String>> title;
    private ArrayAdapter spinner_adapter;
    private RadioGroup group;
    private RadioButton public_group,private_group;
    private Handler handler;
    private String[] arr_id;
    private String[] arr_title;
    private Button upload;
    private RelativeLayout Post_send_Back;
    private RelativeLayout post_send;
    private ImageView imageView;
    //准备上传图片
    private List<String> attachIds = new ArrayList<String>();
    private List<String> scrollImg = new ArrayList<String>();
    private ProgressDialog progressDialog;
    String selected = "0";
    private int flag=0;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addgroup);
        initView();
        //获取session_id
        IssueApi issueApi=new IssueApi();
        session_id = issueApi.getSeesionId();
    }

    public void initView() {
        title = new ArrayList<HashMap<String, String>>();
        group_title = (EditText) findViewById(R.id.group_title);
        spinner = (Spinner) findViewById(R.id.spinner);
        group_content = (EditText) findViewById(R.id.group_content);
        upload= (Button) findViewById(R.id.upload);
        Post_send_Back= (RelativeLayout) findViewById(R.id.Post_send_Back);
        post_send= (RelativeLayout) findViewById(R.id.Post_send);
        imageView= (ImageView) findViewById(R.id.imageView);
        group= (RadioGroup) findViewById(R.id.group);
        progressDialog=new ProgressDialog(this);
        group.setOnCheckedChangeListener(this);
        spinner.setOnItemSelectedListener(this);
        upload.setOnClickListener(this);
        Post_send_Back.setOnClickListener(this);
        post_send.setOnClickListener(this);
        MySpinner MySpinner = new MySpinner();
        new Thread(MySpinner).start();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                List<HashMap<String, String>> title= (List<HashMap<String, String>>) msg.obj;
                Log.d("ssdsds", String.valueOf(title.size()));
                arr_id=new String[title.size()];
                arr_title=new String[title.size()];
                for(Iterator it=title.listIterator();it.hasNext();){
                    Object key=it.next();
                    System.out.println(key+"="+title.get(i).get("title"));
                    arr_id[i]=title.get(i).get("id");
                    arr_title[i]=title.get(i).get("title");
                    i++;
                    System.out.println(i+"="+i);
                }
                spinner_adapter=new ArrayAdapter(AddGroupActivity.this,R.layout.simple_list_item_activity_1,arr_title);
                spinner.setAdapter(spinner_adapter);
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner:
                selected = parent.getItemAtPosition(position).toString();
                NUM=arr_id[position];
//                Toast.makeText(AddGroupActivity.this,selected, Toast.LENGTH_SHORT).show();
//                Toast.makeText(AddGroupActivity.this,arr_id[position], Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.Post_send:
                if (group_title.getText().toString().equals("")){
                    Toast.makeText(AddGroupActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (group_content.getText().toString().equals("")){
                    Toast.makeText(AddGroupActivity.this,"介绍不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (PICTURE == null){
                    Toast.makeText(AddGroupActivity.this,"请上传封面哟",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    uploadImage();
                }
                break;
            case R.id.Post_send_Back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);       //图片路径
            PICTURE=picturePath;
            Log.d("取到图片的路径",picturePath);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            long count = BitmapUtiles.getFileSize_(picturePath);
            cursor.close();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
           //获取变更后的选中项的ID
           int radioButtonId = group.getCheckedRadioButtonId();
           //根据ID获取RadioButton的实例
           RadioButton rb = (RadioButton)AddGroupActivity.this.findViewById(radioButtonId);
           //更新文本内容，以符合选中项
        Log.d("jinlaile", rb.getText().toString());
           if (rb.getText().toString().equals("私有群组")){
                flag=1;
               Log.d("jinlai l","jinlai le");
           }else {
               flag=0;
           }
           Toast.makeText(AddGroupActivity.this,"你选择了"+flag,Toast.LENGTH_SHORT).show();
    }

    class MySpinner implements Runnable {
        @Override
        public void run() {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Url.HTTPURL + Url.GROUPTYPE);
                HttpResponse res = httpClient.execute(httpGet);
                if (res.getStatusLine().getStatusCode() == 200) {
                    //从响应对象当中取出数据
                    HttpEntity entity = res.getEntity();
                    InputStream in = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String result = reader.readLine();
                    MyJson(result);
                    Message message=handler.obtainMessage();
                    message.obj=title;
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void MyJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject title_object = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", title_object.getString("id"));
                map.put("title", title_object.getString("title"));
                title.add(map);
                JSONArray jsonArray1 = title_object.optJSONArray("GroupSecond");
                if (jsonArray1 != null) {
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        JSONObject second = jsonArray1.getJSONObject(j);
                        map1.put("id", second.getString("id"));
                        map1.put("title", "----" + second.getString("title"));
                        title.add(map1);
                    }
                }
            }
            Log.d("wj-----", String.valueOf(title));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage() {
        attachIds.clear();
        progressDialog = ProgressDialog.show(AddGroupActivity.this, "请稍等...", "发布中...", true);
        try {
            AjaxParams params = new AjaxParams();
            String path1=PICTURE;
            Log.e("原路径", path1);
            String path = BitmapUtiles.getOnlyUploadImgPath(path1);
            Log.e("压缩后路径", path);
            File file=new File(BitmapUtiles.getOnlyUploadImgPath(PICTURE));
            Log.e("file", file.toString());
            params.put("image",file);
            params.put("image",path1);
            params.put("session_id", session_id);
            FinalHttp fh = new FinalHttp();
            fh.post(Url.UPLOADIMGURL, params, new AjaxCallBack<Object>() {
                @Override
                public void onLoading(long count, long current) {

                }

                @Override
                public void onSuccess(Object o) {
                    //progressDialog.dismiss();
                    Log.e("Object", o.toString());
                    attachIds.add(myJson.getAttachId(o));
                    Log.e("上传照片成功", o.toString());
                    Log.e("上传照片成功", "Test!!!");
                    String img_id = attachIds.get(0);
                    IMG_ID = img_id;
                    new PostThread().start();
                    Log.d("postIssue", "调用了");
                    Log.e("img_id", img_id);
                    if (attachIds.size() == scrollImg.size() - 1) {
                        Log.e("上传照片成功", "Test2!!!");
                    }
                    Toast.makeText(AddGroupActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    Log.e("上传照片失败", "");
                    progressDialog.dismiss();
                    Toast.makeText(AddGroupActivity.this, "上传照片失败！", Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            AlertDialog.Builder builder= new AlertDialog.Builder(AddGroupActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("ERROR");
            builder.setMessage("发布失败!");
            builder.setPositiveButton("确认", null);
            builder.create().show();
        }

    }

    //发布活动的方法
    class PostThread extends Thread{
        @Override
        public void run() {
            String title=group_title.getText().toString();
            String id=NUM;
            String content=group_content.getText().toString();
            String type_id = NUM;
            BufferedReader in = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Url.HTTPURL + "?s=" + Url.ADDGROUP);
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("session_id", session_id));
                params.add(new BasicNameValuePair("title", title));
                params.add(new BasicNameValuePair("type_id",id));
                params.add(new BasicNameValuePair("type",flag+""));
                params.add(new BasicNameValuePair("detail", content));
                params.add(new BasicNameValuePair("logo",IMG_ID));
                Log.d("logo",IMG_ID);
                Log.d("type_id",id);
                Log.d("type",flag+"");
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
                Log.d("wj,",result);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
