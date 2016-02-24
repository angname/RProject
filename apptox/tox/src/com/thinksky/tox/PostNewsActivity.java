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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.MyJson;
import com.tox.NewsApi;
import com.tox.Url;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 王杰 on 2015/7/20.
 */
public class PostNewsActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static int RESULT_LOAD_IMAGE = 1;
    RelativeLayout back,send;
    private NewsApi newsApi;
    private Spinner spinner;
    ArrayAdapter adapter;
    private String[] arr_id;
    String[] arr_title;
    Button upload;
    private ImageView imageView;
    private EditText Post_send_titleEdit,Post_send_contentEdit;
    private int i=0;
    private String chooiseId;
    private String picture;
    private MyJson myJson=new MyJson();
    private ProgressDialog progressDialog;
    private String title,content;
    //准备上传图片
    String attachIds = new String();
    List<String> scrollImg = new ArrayList<String>();
    private MyHandler myHandler=new MyHandler();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_post);
        //初始化控件
        initView();
        Log.d("scrollImg",scrollImg.size() - 1+"");
    }

    private void initView() {
        newsApi=new NewsApi();
        newsApi.setHandler(myHandler);
        newsApi.getType();
        back= (RelativeLayout) findViewById(R.id.Post_send_back);
        send= (RelativeLayout) findViewById(R.id.Post_send);
        spinner= (Spinner) findViewById(R.id.spinner);
        upload= (Button) findViewById(R.id.upload);
        imageView= (ImageView) findViewById(R.id.imageView);
        Post_send_titleEdit= (EditText) findViewById(R.id.Post_send_titleEdit);
        Post_send_contentEdit= (EditText) findViewById(R.id.Post_send_contentEdit);
        progressDialog=new ProgressDialog(this);
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        upload.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Post_send_back:
                finish();
                break;

            case R.id.Post_send:
                title=Post_send_titleEdit.getText().toString();
                content=Post_send_contentEdit.getText().toString();
                if (title==null||title.length()<=0){
                    Toast.makeText(PostNewsActivity.this,"标题不能为空哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content.length()<=20){
                    Toast.makeText(PostNewsActivity.this,"详情不能少于50字哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (picture==null){
                    Toast.makeText(PostNewsActivity.this,"请上传封面哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadImage();
                break;

            case R.id.upload:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("choose:>>>>>>>>>>", arr_id[position]);
        chooiseId=arr_id[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);       //图片路径
            picture=picturePath;
            Log.d("取到图片的路径",picturePath);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            long count = BitmapUtiles.getFileSize_(picturePath);
            Log.d("图片的大小:",count+"KB");
            cursor.close();
        }
    }

    private void uploadImage() {
        progressDialog = ProgressDialog.show(PostNewsActivity.this, "请稍等...", "发布中...", true);
        try {
            AjaxParams params = new AjaxParams();
            String path1=picture;
            Log.e("原路径", path1);
            String path = BitmapUtiles.getOnlyUploadImgPath(path1);
            Log.e("压缩后路径", path);
            File file=new File(BitmapUtiles.getOnlyUploadImgPath(picture));
            Log.e("file", file.toString());
            params.put("image",file);
            params.put("image",path1);
            params.put("session_id", newsApi.getSeesionId());
            FinalHttp fh = new FinalHttp();
            fh.post(Url.UPLOADIMGURL, params, new AjaxCallBack<Object>() {
                @Override
                public void onLoading(long count, long current) {

                }

                @Override
                public void onSuccess(Object o) {
                    attachIds=myJson.getAttachId(o);
                    String img_id = attachIds;
                    Toast.makeText(PostNewsActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    newsApi.setHandler(handler);
                    newsApi.sendNews(chooiseId,img_id,title,content);
                    progressDialog.dismiss();
                    finish();
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    progressDialog.dismiss();
                    Toast.makeText(PostNewsActivity.this, "上传照片失败！", Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            AlertDialog.Builder builder= new AlertDialog.Builder(PostNewsActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("ERROR");
            builder.setMessage("发布失败!");
            builder.setPositiveButton("确认", null);
            builder.create().show();
        }

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<HashMap<String,String>> title=new ArrayList<HashMap<String,String>>();
            String value= (String) msg.obj;
            MyJson myJson=new MyJson();
            title=myJson.getType(value);
            Log.d("title>>>>>>>>:::",title+"");
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
            chooiseId=arr_id[0];
            adapter=new ArrayAdapter(PostNewsActivity.this,android.R.layout.simple_list_item_1,arr_title);
            spinner.setAdapter(adapter);
        }
    }
}
