package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.thinksky.utils.MyJson;
import com.tox.LoginApi;
import com.tox.Url;
import com.tox.login;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends Activity {

    private RelativeLayout mClose;
    private RelativeLayout mLogin, mWeibo, mQQ;
    private EditText mName, mPassword;
    private RelativeLayout mRegister;
    private String NameValue = null;
    private String PasswordValue = null;
    private String url = null;
    private String value = null;
    private MyJson myJson = new MyJson();
    private ProgressDialog progressDialog;
    private login login;
    private int entryActivity;
    private CircularProgressButton btnLogin;
    private LoginApi loginApi;
    private Handler handler;
    private String status[]=null;
    private ArrayList<HashMap<String ,String>> list=new ArrayList<HashMap<String, String>>();
    public static Activity instance;
    String[] shuju=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        shuju=getIntent().getStringArrayExtra("ways");
        loginApi=new LoginApi();
        Intent intent = this.getIntent();
        instance = this;
        Url.activityFrom="LoginActivity";
        entryActivity = intent.getIntExtra("entryActivity", 0);
        login = new login(this, entryActivity);
        login.setLoginHandler();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        String result = (String) msg.obj;
                        Log.e("result>>>>>>>>", result);
                        list=myJson.beforeRegisterStatus(result);
                        break;
                }
            }
        };
        loginApi.setHandler(handler);
        loginApi.beforeRegister("");
        initView();
    }
    private void initView() {
        mClose = (RelativeLayout) findViewById(R.id.loginClose);
        mLogin = (RelativeLayout) findViewById(R.id.login);
        mWeibo = (RelativeLayout) findViewById(R.id.button_weibo);
        mQQ = (RelativeLayout) findViewById(R.id.buton_qq);
        mName = (EditText) findViewById(R.id.Ledit_name);
        mPassword = (EditText) findViewById(R.id.Ledit_password);
        mRegister = (RelativeLayout) findViewById(R.id.register);
        btnLogin=(CircularProgressButton)findViewById(R.id.btn_login);
        MyOnClickListener my = new MyOnClickListener();
        mClose.setOnClickListener(my);
        mLogin.setOnClickListener(my);
        mWeibo.setOnClickListener(my);
        mQQ.setOnClickListener(my);
        mRegister.setOnClickListener(my);
        btnLogin.setOnClickListener(my);
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int mId = v.getId();
            switch (mId) {
                case R.id.loginClose:
                    finish();
                    break;
                case R.id.login:
                    NameValue = mName.getText().toString();
                    PasswordValue = mPassword.getText().toString();
                    Log.e("qianpengyu", "NameValue" + NameValue + "  PasswordValue"
                            + PasswordValue);
                    if (NameValue.equalsIgnoreCase(null)
                            || PasswordValue.equalsIgnoreCase(null)
                            || NameValue.equals("") || PasswordValue.equals("")) {
                        Toast.makeText(LoginActivity.this, "别闹,先把帐号密码填上", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog = ProgressDialog.show(LoginActivity.this, "登录中....", "请等待。", true, false);
                        login.setProgressDialog(progressDialog);
                        //login();

                        login.userLogin(NameValue, PasswordValue);
                    }
                    break;
                case R.id.button_weibo:
                    break;
                case R.id.btn_login:
                    NameValue = mName.getText().toString();
                    PasswordValue = mPassword.getText().toString();
                    Log.e("qianpengyu", "NameValue" + NameValue + "  PasswordValue"
                            + PasswordValue);
                    if (NameValue.equalsIgnoreCase(null)
                            || PasswordValue.equalsIgnoreCase(null)
                            || NameValue.equals("") || PasswordValue.equals("")) {
                        Toast.makeText(LoginActivity.this, "别闹,先把帐号密码填上", Toast.LENGTH_LONG).show();
                    } else {
                        //progressDialog = ProgressDialog.show(LoginActivity.this, "登录中....", "请等待。", true, false);
                        login.setCircularProgressButton(btnLogin);
                        //login();

                        login.userLogin(NameValue, PasswordValue);
                    }
                    break;
                case R.id.buton_qq:
                    break;
                case R.id.register:

                    for(int i=0;i<shuju.length;i++){
                        if(shuju[i].equals("username")){
                            shuju[i]="用户名";
                        }
                        if(shuju[i].equals("email")){
                            shuju[i]="邮箱注册";
                        }
                        if(shuju[i].equals("mobile")){
                            shuju[i]="手机注册";
                        }
                    }
                    AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("注册方式");
                    builder.setItems(shuju, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    Intent intent=new Intent(LoginActivity.this,RegistetActivity.class);
                                    //这是判断有没有开邀请注册
                                    intent.putExtra("title",list);
                                    if(shuju[which].equals("用户名")){
                                        intent.putExtra("opinion","0");
                                    }
                                    if(shuju[which].equals("邮箱注册")){
                                        intent.putExtra("opinion","1");
                                    }
                                    if(shuju[which].equals("手机注册")){
                                        intent.putExtra("opinion","2");
                                    }
                                    startActivity(intent);
                                    break;
                                case 1:
                                    Intent intent1=new Intent(LoginActivity.this,RegistetActivity.class);
                                    //这是判断有没有开邀请注册
                                    intent1.putExtra("title",list);
                                    if(shuju[which].equals("用户名")){
                                        intent1.putExtra("opinion","0");
                                    }
                                    if(shuju[which].equals("邮箱注册")){
                                        intent1.putExtra("opinion","1");
                                    }
                                    if(shuju[which].equals("手机注册")){
                                        intent1.putExtra("opinion","2");
                                    }
                                    startActivity(intent1);
                                    break;
                                case 2:
                                    Intent intent2=new Intent(LoginActivity.this,RegistetActivity.class);
                                    //这是判断有没有开邀请注册
                                    intent2.putExtra("title",list);
                                    if(shuju[which].equals("用户名")){
                                        intent2.putExtra("opinion","0");
                                    }
                                    if(shuju[which].equals("邮箱注册")){
                                        intent2.putExtra("opinion","1");
                                    }
                                    if(shuju[which].equals("手机注册")){
                                        intent2.putExtra("opinion","2");
                                    }
                                    startActivity(intent2);
                                    break;
                            }

                        }
                    });
                    builder.create().show();

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //如果注册成功将用户名复制给输入框
        if (requestCode == 1 && resultCode == 2 && data != null) {
            NameValue = data.getStringExtra("NameValue");
            mName.setText(NameValue);
        }
    }


}