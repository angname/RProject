package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.info.UserInfo;
import com.thinksky.net.IsNet;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ToastHelper;
import com.tox.Url;
import com.tox.UserApi;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/6/19 0019.
 */
public class SetUserInfoActivity extends Activity implements View.OnClickListener {
    private static String PICTURE=null;
    private static boolean CHANGESEX=false;
    private static boolean CHANGENAME=false;
    private static boolean CHANGESIGN=false;
    private static boolean CHANGEEMAIL=false;
    private static boolean CHANGEBIRTH=false;
    private static boolean CHANGEQQ=false;
    private static String IMG_ID=null;
    private ImageView touXiang,img_edit_sex,img_edit_name1,img_edit_sign,img_edit_email,img_edit_qq,img_edit_age;
    private TextView xiangCe;
    private TextView xiangJi;
    private TextView xianshi_nicheng,xianshi_xingbie,xianshi_nianling,xianshi_qianming,xianshi_qq,xianshi_email,xianshi_place;
    private RelativeLayout backBtn;
    private String mTempPhotoName;
    private String imgHead;
    private int img_num = 0;
    private ProgressDialog progressDialog;
    private List<String> attachIds = new ArrayList<String>();
    private List<String> scrollImg = new ArrayList<String>();
    private MyJson myJson = new MyJson();
    private UserApi userApi=new UserApi();
    private UserInfo user;
    private LoadImg loadImgHeadImg;
    private String sex="sex";
    private int sex_flag=0;
    private Handler myHandler;
    //分隔生日字符串
    private String str;
    private String[] strs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_user_info);
        Log.d("SetUserInfoActivity", "234234");
        initView();
        setUserInfo();
        myHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        if (CHANGESEX) {
                            Toast.makeText(SetUserInfoActivity.this, "修改性别成功", Toast.LENGTH_SHORT).show();
                        }
                        if(CHANGENAME){
                            Toast.makeText(SetUserInfoActivity.this, "修改昵称成功", Toast.LENGTH_SHORT).show();
                        }
                        if (CHANGESIGN){
                            Toast.makeText(SetUserInfoActivity.this, "修改签名成功", Toast.LENGTH_SHORT).show();
                        }
                        if (CHANGEEMAIL){
                            Toast.makeText(SetUserInfoActivity.this, "修改邮箱成功", Toast.LENGTH_SHORT).show();
                        }
                        if (CHANGEQQ){
                            Toast.makeText(SetUserInfoActivity.this, "修改QQ成功", Toast.LENGTH_SHORT).show();
                        }
                        if (CHANGEBIRTH){
                            Toast.makeText(SetUserInfoActivity.this, "修改生日成功", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 404:
                            Toast.makeText(SetUserInfoActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    private void setUserInfo() {
        Intent intent=getIntent();
        Log.d("66666",intent.getSerializableExtra("inf")+"");
        user= (UserInfo) intent.getSerializableExtra("inf");

        sex_flag= Integer.parseInt(user.getSex());
        Log.d("getSex",user.getSex());
        if(user.getSex().equals("0")){
            sex="保密";
        }
        if (user.getSex().equals("1")){
            sex="男";
        }
        if(user.getSex().equals("2")){
            sex="女";
        }
        xianshi_nicheng.setText(user.getNickname());
        xianshi_xingbie.setText(sex);
        xianshi_nianling.setText(user.getBirth());
        Log.d("signature", user.getSignature());
        xianshi_qianming.setText(user.getSignature());
        xianshi_email.setText(user.getEmail());
        xianshi_qq.setText(user.getQq());
        Log.d("Province",user.getProvince()+"  123");
        xianshi_place.setText(user.getProvince()+""+user.getCity()+""+user.getDistrict());
    }

    private void initView() {
        Intent intent=getIntent();
        imgHead=intent.getExtras().getString("info");
        Log.d("Anddd",imgHead);
        loadImgHeadImg = new LoadImg(this);
        touXiang= (ImageView) findViewById(R.id.img_touxing);
        xiangCe= (TextView) findViewById(R.id.textView_xc);
        xiangJi= (TextView) findViewById(R.id.textView_xz);
        xianshi_nicheng= (TextView) findViewById(R.id.xianshi_nicheng);
        xianshi_xingbie= (TextView) findViewById(R.id.xianshi_xingbie);
        xianshi_nianling= (TextView) findViewById(R.id.xianshi_nianling);
        xianshi_qianming= (TextView) findViewById(R.id.xianshi_qianming);
        xianshi_email= (TextView) findViewById(R.id.xianshi_email);
        xianshi_qq= (TextView) findViewById(R.id.xianshi_qq);
        img_edit_sex= (ImageView) findViewById(R.id.img_edit_sex);
        img_edit_name1= (ImageView) findViewById(R.id.img_edit_name1);
        img_edit_sign= (ImageView) findViewById(R.id.img_edit_sign);
        img_edit_email= (ImageView) findViewById(R.id.img_edit_email);
        img_edit_qq= (ImageView) findViewById(R.id.img_edit_qq);
        img_edit_age= (ImageView) findViewById(R.id.img_edit_age);
        backBtn= (RelativeLayout) findViewById(R.id.back_button);
        xianshi_place= (TextView) findViewById(R.id.xianshi_place);
        progressDialog=new ProgressDialog(this);
        backBtn.setOnClickListener(this);
        xiangCe.setOnClickListener(this);
        xiangJi.setOnClickListener(this);
        img_edit_sex.setOnClickListener(this);
        img_edit_name1.setOnClickListener(this);
        img_edit_sign.setOnClickListener(this);
        img_edit_email.setOnClickListener(this);
        img_edit_qq.setOnClickListener(this);
        img_edit_age.setOnClickListener(this);
        BaseFunction.showImage(this, touXiang, imgHead, loadImgHeadImg, Url.IMGTYPE_HEAD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                Intent intent2=new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("inff",user);
                intent2.putExtras(bundle);
                setResult(1, intent2);
                finish();
                break;
            //从相册进入
            case R.id.textView_xc:
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
                break;
            //从拍照进入
            case R.id.textView_xz:
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
                startActivityForResult(intent, 2);
                break;
            //更改性别按钮
            case R.id.img_edit_sex:
                Log.d("sex_number", sex);
                final String[] sex_all=new String[]{"保密", "男", "女"};
                new AlertDialog.Builder(this).setTitle("性别")
                        .setSingleChoiceItems(sex_all, sex_flag, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userApi.setHandler(myHandler);
                                initFlag(false, true, false,false,false,false);
                                userApi.changeSex(which + "");
                                xianshi_xingbie.setText(sex_all[which]);
                                user.setSex(which+"");
                                Log.d("sex111",user.getSex());
                                dialog.dismiss();
                            }
                        })	.setNegativeButton("取消", null)
                        .show();
                break;
            //更改昵称按钮
            case R.id.img_edit_name1:
                final EditText editText=new EditText(this);
                editText.setText(user.getNickname());
                new AlertDialog.Builder(this).setTitle("昵称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initFlag(true,false,false,false,false,false);
                                userApi.setHandler(myHandler);
                                userApi.changeName(editText.getText().toString());
                                xianshi_nicheng.setText(editText.getText().toString());
                                user.setNickname(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            //更改个性签名按钮
            case R.id.img_edit_sign:
                final EditText editText_sign=new EditText(this);
                new AlertDialog.Builder(this).setTitle("个性签名")
                        .setView(editText_sign)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initFlag(false,false,true,false,false,false);
                                userApi.setHandler(myHandler);
                                userApi.changeSign(editText_sign.getText().toString());
                                xianshi_qianming.setText(editText_sign.getText().toString());
                                user.setSignature(editText_sign.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            //更改Email按钮
            case R.id.img_edit_email:
                final EditText editText_email=new EditText(this);
                new AlertDialog.Builder(this).setTitle("邮箱")
                        .setView(editText_email)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
                                Pattern p = Pattern.compile(str);
                                Matcher matcher = p.matcher(editText_email.getText().toString());
                                System.out.println(matcher.matches());
                                if (!matcher.matches()){
                                    ToastHelper.showToast("格式错误",SetUserInfoActivity.this);
                                    return;
                                }
                                initFlag(false,false,false,true,false,false);
                                userApi.setHandler(myHandler);
                                userApi.changeEmail(editText_email.getText().toString());
                                xianshi_email.setText(editText_email.getText().toString());
                                user.setEmail(editText_email.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.img_edit_qq:
                final EditText editText_qq=new EditText(this);
                new AlertDialog.Builder(this).setTitle("QQ")
                        .setView(editText_qq)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initFlag(false,false,false,false,false,true);
                                userApi.setHandler(myHandler);
                                userApi.changeQq(editText_qq.getText().toString());
                                xianshi_qq.setText(editText_qq.getText().toString());
                                user.setQq(editText_qq.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.img_edit_age:
                str = xianshi_nianling.getText().toString();
                strs = str.split("-");
                Log.d("year:",strs[0]);
                Log.d("month:", strs[1]);
                Log.d("day:", strs[2]);
                new DatePickerDialog(SetUserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    Date date=new Date();
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Log.d("monthOfYear>>>>",monthOfYear+"");
//                        xianshi_nianling.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                        initFlag(false, false, false, false, true, false);
                        userApi.setHandler(myHandler);
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
                        try {
                            date=sf.parse(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                            long timeStemp = date.getTime();
                            Log.d("date", String.valueOf(date));
                            Log.d("timeStemp", timeStemp/1000+"");
                            userApi.changeBirth(timeStemp/1000+"");
                            xianshi_nianling.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                            user.setBirth(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },Integer.parseInt(strs[0]),Integer.parseInt(strs[1])-1, Integer.parseInt(strs[2])).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("inff",user);
        intent.putExtras(bundle);
        setResult(1, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);       //图片路径
            PICTURE = picturePath;
            Log.d("取到图片的路径", picturePath);
            new Thread(){
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            user.setAvatar(PICTURE);
                            touXiang.setImageBitmap(BitmapFactory.decodeFile(PICTURE));
                        }
                    });
                }
            }.start();
            long count= BitmapUtiles.getFileSize_(picturePath);
            uploadImage();
            cursor.close();
        }else if (requestCode == 2 && resultCode == RESULT_OK) {
            Log.d("拍照返回","拍照返回");
            File temFile = new File(Environment.getExternalStorageDirectory() + "/tox/photos/" + mTempPhotoName);
            if (temFile.exists()) {
                if(scrollImg.size()!=0){
                    scrollImg.remove((scrollImg.size()-1));
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                if (!BaseFunction.isExistsInList(temFile.getPath(), scrollImg)) {
                    if (img_num <= 9) {
                        scrollImg.add(temFile.getPath());
                        img_num++;
                    }
                }
                scrollImg.add(img_num, "add");
                Log.d("scrollImg", scrollImg.get(0));
                PICTURE=scrollImg.get(0);
                user.setAvatar(String.valueOf(scrollImg.get(0)));
                touXiang.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(scrollImg.get(0))));
                long count= BitmapUtiles.getFileSize_(scrollImg.get(0));
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        attachIds.clear();
        progressDialog = ProgressDialog.show(this, "请稍等...", "发布中...", true);
        try {
            AjaxParams params = new AjaxParams();
            String path1=PICTURE;
            Log.e("原路径", path1);
            String path = BitmapUtiles.getOnlyUploadImgPath(path1);
            Log.e("压缩后路径", path);
            // Toast.makeText(SendPostActivity.this, "压缩后路径" + path, Toast.LENGTH_LONG).show();
                /*String name=path.substring(path.lastIndexOf("/")+1,path.length());
                File file= FileUtils.getSaveFile(Url.UPLOADTEMPORARYPATH,name);*/
            File file=new File(BitmapUtiles.getOnlyUploadImgPath(path1));
            Log.e("file", file.toString());
            params.put("image",file);
            params.put("image",path1);
            params.put("session_id", Url.SESSIONID);
            FinalHttp fh = new FinalHttp();
            fh.post(Url.UPLOADTOUXIANG, params, new AjaxCallBack<Object>() {
                @Override
                public void onLoading(long count, long current) {
//                        progressDialog.setProgressNumberFormat("%1dKB/%2dKB");;
//                        progressDialog.setMax((int)count/1024);
//                        progressDialog.setProgress((int)(current/1024));
                }
                @Override
                public void onSuccess(Object o) {
                    Log.e("Object",o.toString());
                    attachIds.add(myJson.getAttachId(o));
                    Log.e("上传照片成功", o.toString());
                    Log.e("上传照片成功", "Test!!!");
                    String img_id=attachIds.get(0);
                    IMG_ID=img_id;
                    if (IsNet.IsConnect()) {
//                        new Thread(new PostThread()).start();  //session_id,title,content,url
                    }else{
                        ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                    }
                    Log.d("postIssue", "调用了");
                    Log.e("img_id",img_id);
                    if (attachIds.size() == scrollImg.size()-1) {
                        //progressDialog1.show(UploadActivity.this,"提示","发布中...",true,false);
//                            sendWeibo();
                        Log.e("上传照片成功", "Test2!!!");
                    }
                    Toast.makeText(SetUserInfoActivity.this, "更换头像成功", Toast.LENGTH_SHORT).show();
                    PICTURE=null;
                    progressDialog.dismiss();
//                    finish();
                }
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    Log.e("上传照片失败", "");
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, "上传照片失败！", Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            AlertDialog.Builder builder= new AlertDialog.Builder(SetUserInfoActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("ERROR");
            builder.setMessage("发布失败!");
            builder.setPositiveButton("确认", null);
            builder.create().show();
            progressDialog.dismiss();
        }
    }

    private void initFlag(boolean changeName,boolean changeSex,boolean changeSign,boolean changeEmail,boolean birth,boolean qq){
        CHANGESEX=changeSex;
        CHANGENAME=changeName;
        CHANGESIGN=changeSign;
        CHANGEEMAIL=changeEmail;
        CHANGEBIRTH=birth;
        CHANGEQQ=qq;
    }
}
