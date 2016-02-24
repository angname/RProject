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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.thinksky.net.IsNet;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.MyJson;
import com.tox.IssueApi;
import com.tox.ToastHelper;
import com.tox.Url;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 时间拾取器界面
 *
 * @author wwj_748
 *
 */
public class PostEventActivity extends Activity {
    public static long SDATETIME=System.currentTimeMillis()/1000;
    public static long SPDATETIME=System.currentTimeMillis()/1000-1;
    public static long EDATETIME=System.currentTimeMillis()/1000;
    private static String EVENTID=null;
    private static String PICTURE=null;
    private static String IMG_ID=null;
    private static int RESULT_LOAD_IMAGE = 1;
    private ProgressDialog progressDialog;
    private MyJson myJson = new MyJson();
    private ImageView imageView;
    //声明活动标题
    private EditText mTitleEdit;
    //声明活动介绍
    private EditText post_send_contentEdit;
    //声明活动地址
    private EditText post_Send_Place;
    //声明活动人数
    private EditText post_Send_Num;
    //声明session_id
    private String session_id;
    //声明返回菜单按钮
    private RelativeLayout backBtn;
    //声明发布按钮
    private RelativeLayout post_Send;
    //声明上传活动图片按钮
    private Button upload;
    //准备上传图片
    private List<String> attachIds = new ArrayList<String>();
    private List<String> scrollImg = new ArrayList<String>();
    private Handler handler;
    private Spinner spinner;
    /** Called when the activity is first created. */
    private EditText startDateTime;
    private EditText endDateTime;
    private EditText stopDateTime;

    private ArrayAdapter<String> adapter;
    //声明模块相对应的ID
    private static String arr[];

    //发布活动的方法
    class PostThread extends Thread{
        @Override
        public void run() {

            PostThread post=new PostThread();

            String title=mTitleEdit.getText().toString();
            String content=post_send_contentEdit.getText().toString();
            String place=post_Send_Place.getText().toString();
            String num=post_Send_Num.getText().toString();
            String mukuai=EVENTID;
            try {
                HttpClient httpClient=new DefaultHttpClient();
                Log.d("session_id的值:",session_id);

//                PostIssueActivity postIssueActivity=new PostIssueActivity();
//                postIssueActivity.issue_id

                Log.d("EVENTID",EVENTID);
                Log.d("title的值:",title);
                Log.d("mukuai",mukuai);
                Log.d("content的值:",content);
                Log.d("place的值:",place);
                Log.d("num的值:",num);
                System.out.println("sTime"+SDATETIME);
                System.out.println("eTime"+EDATETIME);
                System.out.println("spTime"+SPDATETIME);


//            HttpGet httpGet=new HttpGet("http://demo.opensns.cn/api.php?s=/issue/sendIssue&session_id="+session_id+"&issue_id="+21+"&cover_id="+222+"&title="+title+"&content="+content+"&url="+url);
                String uriAPI =Url.HTTPURL+"?s="+Url.ADDEVENTS+"&session_id="+session_id+"&cover_id="+IMG_ID+"&title="+URLEncoder.encode(title,"UTF-8")+"&address="+URLEncoder.encode(place,"UTF-8")+"&sTime="+SDATETIME+"&eTime="+EDATETIME+"&deadline="+SPDATETIME+"&explain="+URLEncoder.encode(content,"UTF-8")+"&type_id="+mukuai+"&limitCount="+num;
                Log.d("网址:",uriAPI);
                HttpGet httpGet=new HttpGet(uriAPI);

                HttpResponse httpResponse=httpClient.execute(httpGet);
                int code=httpResponse.getStatusLine().getStatusCode();

//                ProgressBar progressBar=new ProgressBar(PostIssueActivity.this);
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setTitle("发布中请等待");
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void MyJson(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray=jsonObject.getJSONArray("list");
            arr=new String[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++){

                JSONObject lan=jsonArray.getJSONObject(i);
                //获取spinner中的adapter
                adapter.add(lan.getString("title"));
                //获取title对应的ID号
                arr[i]=lan.getString("id");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.eventpost);
        //获取session_id
        IssueApi issueApi=new IssueApi();
        session_id = issueApi.getSeesionId();
        adapter=new ArrayAdapter<String>(PostEventActivity.this,R.layout.simple_list_item_activity_1);
        imageView= (ImageView) findViewById(R.id.imageView);

        //活动标题
        mTitleEdit= (EditText) findViewById(R.id.postSendTitleEdit);
        //活动介绍
        post_send_contentEdit= (EditText) findViewById(R.id.post_send_contentEdit);
        //活动地点
        post_Send_Place= (EditText) findViewById(R.id.post_Send_Place);
        //活动人数
        post_Send_Num= (EditText) findViewById(R.id.post_Send_Num);

        backBtn= (RelativeLayout) findViewById(R.id.Post_send_Back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 两个输入框
        startDateTime = (EditText) findViewById(R.id.inputDate);
        endDateTime = (EditText) findViewById(R.id.inputDate2);
        //报名截止框
        stopDateTime= (EditText) findViewById(R.id.inputDate3);

        progressDialog=new ProgressDialog(this);
        //上传图片按钮
        upload= (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        String initStartDateTime =getStrTime(System.currentTimeMillis()/1000+""); // 初始化开始时间
        String initEndDateTime = getStrTime((System.currentTimeMillis()/1000-1)+""); // 初始化结束时间
        String initStopDateTime= getStrTime(System.currentTimeMillis()/1000+"");//初始化报名截止时间
        //时间
        startDateTime.setText(initStartDateTime);
        endDateTime.setText(initEndDateTime);
        stopDateTime.setText(initStopDateTime);
        startDateTime.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String s = startDateTime.getText().toString();
                String sDateTime = getTime(s);
                String ssDateTime =getStrTime(sDateTime);
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        PostEventActivity.this, ssDateTime);
                dateTimePicKDialog.dateTimePicKDialog(startDateTime, PostEventActivity.this);

                SDATETIME= Long.parseLong(sDateTime);
                System.out.println("sDateTime:"+sDateTime+"<------>"+SDATETIME);
//                Toast.makeText(PostEventActivity.this,sDateTime, Toast.LENGTH_SHORT).show();
            }
        });

        endDateTime.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String s = endDateTime.getText().toString();
                String eDateTime = getTime(s);
                String eeDateTime=getStrTime(eDateTime);
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        PostEventActivity.this, eeDateTime);
                dateTimePicKDialog.dateTimePicKDialog(endDateTime,PostEventActivity.this);

                EDATETIME= Long.parseLong(eDateTime);
                System.out.println("eDateTime:"+eDateTime+"<------>"+EDATETIME);
//                Toast.makeText(PostEventActivity.this,eDateTime, Toast.LENGTH_SHORT).show();
            }
        });

        stopDateTime.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String s = stopDateTime.getText().toString();
                String spDateTime = getTime(s);
                String spspDateTime =getStrTime(spDateTime);
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        PostEventActivity.this, spspDateTime);
                dateTimePicKDialog.dateTimePicKDialog(stopDateTime, PostEventActivity.this);

                SPDATETIME= Long.parseLong(spDateTime);
                System.out.println("spDateTime:"+spDateTime+"<------>"+SPDATETIME);
//                Toast.makeText(PostEventActivity.this,spDateTime, Toast.LENGTH_SHORT).show();
            }
        });

        //发布按钮
        post_Send= (RelativeLayout) findViewById(R.id.post_Send);
        post_Send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String time1=startDateTime.getText().toString();
                String sDateTime = getTime(time1);
                SDATETIME= Long.parseLong(sDateTime);
                System.out.println("这才是真正的值咯SDATETIME："+SDATETIME);

                String time2=stopDateTime.getText().toString();
                String spDateTime = getTime(time2);
                SPDATETIME= Long.parseLong(spDateTime);
                System.out.println("这才是真正的值咯SPDATETIME："+SPDATETIME);


                String time3=endDateTime.getText().toString();
                String eDateTime = getTime(time3);
                EDATETIME= Long.parseLong(eDateTime);
                System.out.println("这才是真正的值咯EDATETIME："+EDATETIME);

                System.out.println("SDATETIME"+SDATETIME);
                System.out.println("EDATETIME"+EDATETIME);
                System.out.println("SPDATETIME" + SPDATETIME);

                Log.d("获取Session;",session_id);  //判断有没有取到Session_id
                if(session_id==""){
                    Toast.makeText(PostEventActivity.this, "未登入", Toast.LENGTH_SHORT).show();
                }
                else if (SDATETIME>EDATETIME){
                    Toast.makeText(PostEventActivity.this,"开始时间不能大于结束时间",Toast.LENGTH_SHORT).show();
                }
                else if (SPDATETIME>SDATETIME){
                    Toast.makeText(PostEventActivity.this,"报名截止日期不能大于开始日期",Toast.LENGTH_SHORT).show();
                }
                else if (mTitleEdit.getText().toString().equals("")) {
                    Toast.makeText(PostEventActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                }
                else if (post_Send_Place.getText().toString().equals("")){
                    Toast.makeText(PostEventActivity.this, "地点不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(post_Send_Num.getText().toString().equals("")){
                    Toast.makeText(PostEventActivity.this, "人数不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(!post_Send_Num.getText().toString().matches("[0-9]*")){
                    Toast.makeText(PostEventActivity.this, "人数只能是数字哟", Toast.LENGTH_SHORT).show();
                }
                else if (PICTURE==null){
                    Toast.makeText(PostEventActivity.this,"封面不能为空",Toast.LENGTH_SHORT).show();
                }

                else if (post_send_contentEdit.getText().toString().equals("")){
                    Toast.makeText(PostEventActivity.this,"介绍不能为空",Toast.LENGTH_SHORT).show();
                }

                else {
                    uploadImage();
                }
            }
        });
        //模块选择
        spinner= (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取模块相对应的ID
                String event_id=arr[position];
                EVENTID=event_id;
//                Toast.makeText(PostEventActivity.this,event_id,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                spinner.setAdapter(adapter);
            }
        };

        if (IsNet.IsConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //创建HttpClient对象
                    HttpClient httpClient = new DefaultHttpClient();
                    //创建代表请求的对象,参数是访问的服务器地址
                    //http://www.baidu.com
                    HttpGet httpGet = new HttpGet(Url.HTTPURL + "?s=" + Url.EVENTMODULES);
                    //执行请求，获取服务器发还的响应对象
                    try {
                        HttpResponse resp = httpClient.execute(httpGet);
                        //检查响应的状态是否正常,检查状态码的值是否等于200
                        int code = resp.getStatusLine().getStatusCode();
                        if (code == 200) {
                            //从响应对象当中取出数据
                            HttpEntity entity = resp.getEntity();
                            InputStream in = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String line = reader.readLine();
//                    textView.setText(line);
                            MyJson(line);
                            Message msg = handler.obtainMessage();
                            msg.obj = adapter;
                            handler.sendMessage(msg);
                            Log.d("HTTP", "从服务器取得的数据为:" + adapter);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
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



            long count= BitmapUtiles.getFileSize_(picturePath);

            cursor.close();


        }
    }

    private void uploadImage() {
        attachIds.clear();
        progressDialog = ProgressDialog.show(PostEventActivity.this, "请稍等...", "发布中...", true);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setTitle("发布中请等待");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
        //kjUpload(scrollImg.get(i));



        try {
            AjaxParams params = new AjaxParams();
            String path1=PICTURE;
            Log.e("原路径", path1);
            String path = BitmapUtiles.getOnlyUploadImgPath(path1);
            Log.e("压缩后路径", path);

            // Toast.makeText(SendPostActivity.this, "压缩后路径" + path, Toast.LENGTH_LONG).show();
                /*String name=path.substring(path.lastIndexOf("/")+1,path.length());
                File file= FileUtils.getSaveFile(Url.UPLOADTEMPORARYPATH,name);*/
            File file=new File(BitmapUtiles.getOnlyUploadImgPath(PICTURE));
            Log.e("file", file.toString());
            params.put("image",file);
            params.put("image",path1);
            params.put("session_id", session_id);
            FinalHttp fh = new FinalHttp();
            fh.post(Url.UPLOADIMGURL, params, new AjaxCallBack<Object>() {
                @Override
                public void onLoading(long count, long current) {
//                        progressDialog.setProgressNumberFormat("%1dKB/%2dKB");;
//                        progressDialog.setMax((int)count/1024);
//                        progressDialog.setProgress((int)(current/1024));
                }
                @Override
                public void onSuccess(Object o) {
                    //progressDialog.dismiss();
                    Log.e("Object",o.toString());
                    attachIds.add(myJson.getAttachId(o));
                    Log.e("上传照片成功", o.toString());
                    Log.e("上传照片成功", "Test!!!");

                    String img_id=attachIds.get(0);
                    IMG_ID=img_id;
                    if (IsNet.IsConnect()) {
                        new PostThread().start();  //session_id,title,content,url
                    }else{
                        ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                    }
                    Log.d("postIssue","调用了");
                    Log.e("img_id",img_id);
                    if (attachIds.size() == scrollImg.size()-1) {
                        //progressDialog1.show(UploadActivity.this,"提示","发布中...",true,false);
//                            sendWeibo();
                        Log.e("上传照片成功", "Test2!!!");
                    }
                    Toast.makeText(PostEventActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                    PICTURE=null;
                    progressDialog.dismiss();

//                        Intent intent=new Intent(PostIssueActivity.this,IssueActivity2.class);
//                        startActivity(intent);
                    finish();
                }
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    Log.e("上传照片失败", "");
                    progressDialog.dismiss();
                    Toast.makeText(PostEventActivity.this, "上传照片失败！", Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            AlertDialog.Builder builder= new AlertDialog.Builder(PostEventActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("ERROR");
            builder.setMessage("发布失败!");
            builder.setPositiveButton("确认", null);
            builder.create().show();
        }

    }

    //把时间字符串转换为时间戳
    public static String getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d;
        try {


            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);


        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return re_time;
    }

    // 将时间戳转为字符串
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
// 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

}
