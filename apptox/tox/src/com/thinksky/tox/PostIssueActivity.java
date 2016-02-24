package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.info.PostInfo;
import com.thinksky.net.IsNet;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.FileUtiles;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.MyJson;
import com.tox.ForumApi;
import com.tox.IssueApi;
import com.tox.ToastHelper;
import com.tox.TouchHelper;
import com.tox.Url;

import net.tsz.afinal.FinalBitmap;
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
import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.ui.widget.HorizontalListView;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.kjframe.http.HttpParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostIssueActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private static String ISSUEID=null;
    private static String PICTURE=null;
    private static String IMG_ID=null;
    private EditText issue_Url,post_send_contentEdit;
    private RelativeLayout Post_send;
    private static int RESULT_LOAD_IMAGE = 1;
    private Button upload;
    private LinearLayout photoLayout;
    private String mTempPhotoName;
    private EditText mTitleEdit,mContentEdit;
    private ImageView imageView;
    private String arr[][],arr_id[][];
    private ArrayAdapter<CharSequence> arrayAdapter;


    private RelativeLayout postSendLayout;
    /**
     * 已选择准备上传的图片数量
     */
    private int img_num = 0;
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10;

    private List<RelativeLayout> imgLayList=new ArrayList<RelativeLayout>();
    private RelativeLayout imgLay1,imgLay2,imgLay3,imgLay4,imgLay5,imgLay6,imgLay7,imgLay8,imgLay9;
    private ProgressDialog progressDialog, progressDialog1;
    /**
     * 用来保存准备选择上传的图片路径
     */
    private List<String> scrollImg = new ArrayList<String>();
    private List<ImageView> imgList = new ArrayList<ImageView>();
    private MyJson myJson = new MyJson();
    private ForumApi forumApi=new ForumApi();
    private List<String> attachIds = new ArrayList<String>();
    private String forumId="";
    private RelativeLayout backBtn;
    private HorizontalListView horizontalListView;
    private LinearLayout mAttachLayout,mAttachBtn,mFaceBtn;
    private FrameLayout mPhotoShowLayout;
    private PhotoAdapter photoAdapter;
    private FinalBitmap finalBitmap;
    private KJBitmap kjBitmap;
    private TextView photoCount;
    private Spinner spinner1,spinner2;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<CharSequence> adapter2,adapter3;
    private Handler handler;
    private String session_id;


    class PostThread extends Thread{
        //发布专辑的方法

        @Override
        public void run() {
            PostThread post=new PostThread();

            String title=mTitleEdit.getText().toString();
            String content=post_send_contentEdit.getText().toString();
            String url=issue_Url.getText().toString();
            try {
                HttpClient httpClient=new DefaultHttpClient();
                Log.d("session_id的值:",session_id);

//                PostIssueActivity postIssueActivity=new PostIssueActivity();
//                postIssueActivity.issue_id

                Log.d("ISSUEID",ISSUEID);
                Log.d("title的值:",title);
                Log.d("content的值:",content);
                Log.d("url的值:", url);
//            HttpGet httpGet=new HttpGet("http://demo.opensns.cn/api.php?s=/issue/sendIssue&session_id="+session_id+"&issue_id="+21+"&cover_id="+222+"&title="+title+"&content="+content+"&url="+url);

                String uriAPI =Url.HTTPURL+"?s="+Url.SENDISSUE+"&session_id="+session_id+"&issue_id="+ISSUEID+"&cover_id="+IMG_ID+"&title="+URLEncoder.encode(title,"UTF-8")+"&content="+URLEncoder.encode(content,"UTF-8")+"&url=http://"+URLEncoder.encode(url,"UTF-8");
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
        String s="";
        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray=jsonObject.getJSONArray("list");
            arr=new String[jsonArray.length()][];
            arr_id=new String[jsonArray.length()][];
            for(int i=0;i<jsonArray.length();i++){

                JSONObject lan=jsonArray.getJSONObject(i);
//                s=lan.getString("Issues");

                //获取第一个spinner中的adapter
                adapter1.add(lan.getString("title"));
                JSONArray jsonArray1=lan.getJSONArray("Issues");
                for (int j=0;j<jsonArray1.length();j++){
                    JSONObject aaa=jsonArray1.getJSONObject(j);
                    if (j==0) {
                        arr[i] = new String[jsonArray1.length()]; //这边要改
                        arr_id[i]=new String[jsonArray1.length()];  //获取id
                    }
                    Log.d("要传的值",aaa.getString("title").toString());
                    Log.d("要传的id",aaa.getString("id").toString());
                    arr[i][j]=aaa.getString("title").toString();
                    arr_id[i][j]=aaa.getString("id").toString();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch (parent.getId()) {
                case R.id.spinner1:
                    adapter2 = new ArrayAdapter<CharSequence>(
                            PostIssueActivity.this,
                            R.layout.simple_list_item_activity_1, PostIssueActivity.this.arr[position]);
                    adapter3 = new ArrayAdapter<CharSequence>(
                            PostIssueActivity.this,
                            R.layout.simple_list_item_activity_1, PostIssueActivity.this.arr_id[position]);
                    spinner2.setAdapter(PostIssueActivity.this.adapter2);

                    break;
                case R.id.spinner2:
                    String issue_id = adapter3.getItem(position).toString();
                    //显示当前spinner的模块编号
//                    Toast.makeText(PostIssueActivity.this, issue_id, Toast.LENGTH_SHORT).show();
                    ISSUEID=issue_id;
                break;

            }
        }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
//            adapter1= (ArrayAdapter<String>) msg.obj;
//            textView.setText(line);
//            adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,);
            spinner1.setAdapter(adapter1);
            spinner2.setAdapter(adapter2);
        }
    }


    class MyThread extends Thread{
        @Override
        public void run() {
            //创建HttpClient对象
            HttpClient httpClient=new DefaultHttpClient();
            //创建代表请求的对象,参数是访问的服务器地址
            //http://www.baidu.com
            HttpGet httpGet=new HttpGet(Url.HTTPURL+"?s="+Url.ISSUEMODULES+"&session_id=");
            //执行请求，获取服务器发还的响应对象
            try {
                HttpResponse resp=httpClient.execute(httpGet);
                //检查响应的状态是否正常,检查状态码的值是否等于200
                int code=resp.getStatusLine().getStatusCode();
                if (code==200){
                    //从响应对象当中取出数据
                    HttpEntity entity=resp.getEntity();
                    InputStream in=entity.getContent();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String line=reader.readLine();
//                    textView.setText(line);
                    MyJson(line);

                    Message msg=handler.obtainMessage();
                    msg.obj=adapter1;
                    handler.sendMessage(msg);
                    Log.d("HTTP","从服务器取得的数据为:"+adapter1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.issuepost);
        spinner1= (Spinner) findViewById(R.id.spinner1);
        spinner2= (Spinner) findViewById(R.id.spinner2);
        adapter1=new ArrayAdapter<String>(this,R.layout.simple_list_item_activity_1);
        adapter2=new ArrayAdapter<CharSequence>(this,R.layout.simple_list_item_activity_1);
        adapter3=new ArrayAdapter<CharSequence>(this,R.layout.simple_list_item_activity_1);
        issue_Url= (EditText) findViewById(R.id.issue_Url);
        post_send_contentEdit= (EditText) findViewById(R.id.post_send_contentEdit);
        imageView= (ImageView) findViewById(R.id.imageView);
        Post_send= (RelativeLayout) findViewById(R.id.Post_send);
        mTitleEdit=(EditText)findViewById(R.id.Post_send_titleEdit);

        IssueApi issueApi=new IssueApi();
        session_id = issueApi.getSeesionId();

        upload= (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);



//        postSendLayout=(RelativeLayout)findViewById(R.id.Post_send);
        backBtn=(RelativeLayout)findViewById(R.id.Post_send_Back);
        Post_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PostIssueActivity1","进入OncClick");
                Log.d("PostIssueActivity2222",mTitleEdit.getText().toString());

                Log.d("获取Session;",session_id);  //判断有没有取到Session_id
                if(session_id==""){
                    Toast.makeText(PostIssueActivity.this, "未登入", Toast.LENGTH_SHORT).show();
                }
                else if (mTitleEdit.getText().toString().equals("")) {
                    Toast.makeText(PostIssueActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                }
                else if (issue_Url.getText().toString().equals("")) {
                    Toast.makeText(PostIssueActivity.this, "网址不能为空", Toast.LENGTH_SHORT).show();
                }
                else if (PICTURE==null){
                    Toast.makeText(PostIssueActivity.this,"封面不能为空",Toast.LENGTH_SHORT).show();
                }

                else if (post_send_contentEdit.getText().toString().equals("")){
                    Toast.makeText(PostIssueActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }

                else {
                    String title=mTitleEdit.getText().toString();
                    String content=post_send_contentEdit.getText().toString();
                    String url=issue_Url.getText().toString();
                    //调用发布专辑的方法
//                    new PostThread().start();  //session_id,title,content,url
//                    Log.d("postIssue","调用了");
//                    AlertDialog.Builder builder= new AlertDialog.Builder(PostIssueActivity.this);
//                    builder.setIcon(android.R.drawable.ic_dialog_info);
//                    builder.setTitle(title);
//                    builder.setMessage("正在发布，请稍后");
//                    builder.setPositiveButton("确认", null);
//                    builder.create().show();
                    uploadImage();
//                ProgressDialog progressDialog=new ProgressDialog(PostIssueActivity.this);
//
//                progressDialog.setTitle("发布中请等待");
//                progressDialog.setMessage("Loading...");
//                progressDialog.setCanceledOnTouchOutside(true);
//                progressDialog.show();
                }




            }

        });
//        postSendLayout.setOnClickListener(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        initView();

        handler=new MyHandler();
        if (IsNet.IsConnect()) {
            new MyThread().start();
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



            long count=BitmapUtiles.getFileSize_(picturePath);

            cursor.close();


        }

    }











    private void initView(){
        kjBitmap=KJBitmap.create();
        finalBitmap=FinalBitmap.create(this);
        forumApi.setHandler(hand);
        Intent intent=getIntent();
        forumId=intent.getStringExtra("forumId");
        photoLayout=(LinearLayout)findViewById(R.id.Post_send_photo);
//        photoLayout.setOnClickListener(this);
        photoLayout.setOnTouchListener(new TouchHelper(this, R.drawable.borderradius_postsend + "", R.drawable.borderradius_postsend_touched + "", "drawable"));

        mContentEdit=(EditText)findViewById(R.id.Post_send_contentEdit);
        mTitleEdit=(EditText)findViewById(R.id.Post_send_titleEdit);
        postSendLayout=(RelativeLayout)findViewById(R.id.Post_send);
        mAttachLayout=(LinearLayout)findViewById(R.id.Post_attach_layout);
        mPhotoShowLayout=(FrameLayout)findViewById(R.id.Post_photo_layout);
        horizontalListView=(HorizontalListView)findViewById(R.id.HorizontalListView);
        photoAdapter=new PhotoAdapter(this,finalBitmap,kjBitmap,scrollImg);
        horizontalListView.setAdapter(photoAdapter);
        mAttachBtn=(LinearLayout)findViewById(R.id.Post_send_attachBtn);
        mFaceBtn=(LinearLayout)findViewById(R.id.Post_send_faceBtn);
        backBtn=(RelativeLayout)findViewById(R.id.Post_send_Back);
//        backBtn.setOnClickListener(this);
//        mFaceBtn.setOnClickListener(this);
//        mAttachBtn.setOnClickListener(this);
//        postSendLayout.setOnClickListener(this);


        spinner1= (Spinner) findViewById(R.id.spinner1);
        spinner2= (Spinner) findViewById(R.id.spinner2);


        progressDialog=new ProgressDialog(this);
        photoCount=(TextView)findViewById(R.id.photo_count);
        initList();
    }

    private void initList() {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FileUtiles.DeleteTempFiles(Url.getDeleteFilesPath());
        PostIssueActivity.this.finish();
    }

    private void setImageGone(List<ImageView> imageLists) {
        for (int i = 0; i < img_num; i++) {
            imageLists.get(i).setVisibility(View.GONE);
            imgLayList.get(i).setVisibility(View.GONE);
        }
    }

    private void ableAddPhoto(){
        if(img_num>=9){

        }

    }


    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                progressDialog.dismiss();
                Log.e("after dismiss", "");
                String result = (String) msg.obj;
                if (result != null) {
                    PostInfo postInfo=myJson.getPostInfo(result);
                    FileUtiles.DeleteTempFiles(Url.getDeleteFilesPath());
                    Url.postInfo=postInfo;
                    Url.is2InsertPost=true;
                    PostIssueActivity.this.finish();

                }
            }
        };
    };

    private void uploadImage() {
        attachIds.clear();
        progressDialog = ProgressDialog.show(PostIssueActivity.this, "请稍等...", "发布中...", true);
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
                        Log.e("Object", o.toString());
                        attachIds.add(myJson.getAttachId(o));
                        Log.e("上传照片成功", o.toString());
                        Log.e("上传照片成功", "Test!!!");
                        if (IsNet.IsConnect()) {
                            new PostThread().start();  //session_id,title,content,url
                        }else {
                            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                        }
                        Log.d("postIssue","调用了");
                        String img_id=attachIds.get(0);
                        IMG_ID=img_id;
                        Log.e("img_id",img_id);
                        if (attachIds.size() == scrollImg.size()-1) {
                            //progressDialog1.show(UploadActivity.this,"提示","发布中...",true,false);
//                            sendWeibo();
                            Log.e("上传照片成功", "Test2!!!");
                        }
                        Toast.makeText(PostIssueActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

//                        Intent intent=new Intent(PostIssueActivity.this,IssueActivity2.class);
//                        startActivity(intent);
                        PICTURE=null;
                        IMG_ID=null;
                        finish();
                    }
                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        Log.e("上传照片失败", "");
                        progressDialog.dismiss();
                        Toast.makeText(PostIssueActivity.this, "上传照片失败！", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (FileNotFoundException e) {
                AlertDialog.Builder builder= new AlertDialog.Builder(PostIssueActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle("ERROR");
                builder.setMessage("发布失败!");
                builder.setPositiveButton("确认", null);
                builder.create().show();
            }

    }

    private void kjUpload(String path){
        HttpParams params=new HttpParams();
        KJHttp kjHttp=new KJHttp();

        try{
            params.put("image", FileUtils.getSaveFile(Url.UPLOADTEMPORARYPATH, path.substring(path.lastIndexOf("/") + 1, path.length())));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    private void sendWeibo() {
        if (mContentEdit.getText().toString().equals("")||mTitleEdit.getText().toString().trim().length()==0) {
            Toast.makeText(PostIssueActivity.this, "内容不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        forumApi.sendPost(mTitleEdit.getText().toString().trim(),mContentEdit.getText().toString().trim(),attachIds,forumId);
    }

    public class PhotoAdapter extends BaseAdapter {
        private KJBitmap kjBitmap;
        private List<String> imgUrl=new ArrayList<String>();
        private Context ctx;
        private FinalBitmap finalBitmap;
        private LoadImg loadImg;
        public PhotoAdapter(Context ctx,FinalBitmap finalBitmap,KJBitmap kjBitmap,List<String> list) {
            this.kjBitmap=kjBitmap;
            this.finalBitmap=finalBitmap;
            this.imgUrl=list;
            this.ctx=ctx;
            loadImg=new LoadImg(ctx);
        }

        @Override
        public int getCount() {
            return imgUrl.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final  Holder holder;
            final  View view;

            if(convertView==null){
                holder=new Holder();
                convertView=View.inflate(ctx, R.layout.photo_item,null);
                holder.imageView=(ImageView)convertView.findViewById(R.id.Photo_item);
                holder.imageView.setTag(imgUrl.get(position));
                holder.delImg=(ImageView)convertView.findViewById(R.id.delImg);
                holder.delImg.setAlpha(180);
                convertView.setTag(holder);

            }else{
                holder=(Holder)convertView.getTag();
            }
            view=convertView;

            if(holder.imageView.getTag().equals(imgUrl.get(position))){
                if(imgUrl.get(position).equals("add")){
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.add_post_photo);
                    holder.imageView.setImageBitmap(bitmapDrawable.getBitmap());
                    holder.delImg.setVisibility(View.GONE);
                }else{
                    holder.imageView.setImageBitmap(BitmapUtiles.loadBitmap(imgUrl.get(position),3));
                    holder.delImg.setVisibility(View.VISIBLE);
                }

            }else {
                if(imgUrl.get(position).equals("add")){
                    holder.imageView.setTag(imgUrl.get(position));
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.add_post_photo);
                    holder.imageView.setImageBitmap(bitmapDrawable.getBitmap());
                    holder.delImg.setVisibility(View.GONE);
                }else{
                    holder.imageView.setTag(imgUrl.get(position));
                    holder.imageView.setImageBitmap(BitmapUtiles.loadBitmap(imgUrl.get(position),3));
                    holder.delImg.setVisibility(View.VISIBLE);
                }
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imgUrl.get(position).equals("add")){
                        if(img_num<9){
                            //ToastHelper.showToast("点击了罗",ctx);
                            String[] items={"相册","拍照"};
                            AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
                            builder.setTitle("操作");
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            Intent intent3 = new Intent(PostIssueActivity.this, ScanPhotoActivity.class);
                                            startActivityForResult(intent3, 9);
                                            break;
                                        case 1:
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            File file = new File(Environment.getExternalStorageDirectory() + "/tox/photos");
                                            mTempPhotoName = System.currentTimeMillis() + ".png";
                                            if (!file.exists()) {
                                                file.mkdirs();

                                                File photo = new File(file, mTempPhotoName);
                                                Uri u = Uri.fromFile(photo);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                                            } else {

                                                File photo = new File(file, mTempPhotoName);
                                                Uri u = Uri.fromFile(photo);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                                            }
                                            startActivityForResult(intent, 1);
                                            break;
                                    }
                                }
                            });
                            builder.setCancelable(true);
                            builder.show();
                        }else{
                            ToastHelper.showToast("最多上传9张图片",ctx);
                        }
                    }
                }
            });
            holder.delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCell(view, position);
                }
            });
            return convertView;
        }


        class Holder{
            ImageView imageView,delImg;
        }

    }
    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                horizontalListView.scrollTo(0);
                scrollImg.remove(index);
                img_num--;
                photoCount.setText("已选"+img_num+"张，还剩"+(9-img_num)+"张");
                PhotoAdapter.Holder vh = (PhotoAdapter.Holder)v.getTag();
                photoAdapter.notifyDataSetChanged();
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };
        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        TranslateAnimation translateAnimation=new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,1f);

        if (al!=null) {
            translateAnimation.setAnimationListener(al);
        }
        translateAnimation.setDuration(300);
        v.startAnimation(translateAnimation);
    }

}
