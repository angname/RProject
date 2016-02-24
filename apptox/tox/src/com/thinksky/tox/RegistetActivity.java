package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ToastHelper;
import com.tox.Url;
import com.tox.UserApi;
import com.tox.login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.thinksky.tox.R.drawable.email;

public class RegistetActivity extends Activity {
    private RelativeLayout mClose,mNext;
    private EditText mName, mPassword,role,nickname,verifyId;
    private TextView openRegist,registerText;
    private LinearLayout checkId;
    private TextView mStartlogin;
    private String url = null;
    private String value = null;
    private String code=null;
    private String juese=null;
    private String email_verify_type=null;
    private String mobile_verify_type=null;
    private String username = null;
    private String password = null;
    private UserApi mUserapi=new UserApi();
    private ProgressDialog progressDialog;
    private login login;
    private Button sendVerify;
    private RadioButton tempButton;
    private RadioGroup radioGroup;
    private ArrayList<HashMap<String,String>> arr=new ArrayList<HashMap<String, String>>();
    private MyJson myJson;
    private String type;
    private TimeCount time;
    //存储从那种注册方式进来
    private String opinion;
    private String reg_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d("验证声明周期", "asdasda");
        setContentView(R.layout.activity_register);
        myJson = new MyJson();
        login = new login(RegistetActivity.this,2);
        login.setLoginHandler();
        initView();
        if (Url.activityFrom.equals("LoginActivity")){
            role.setText("");
            code="";
            juese="";

        }else{
            role.setVisibility(View.VISIBLE);
            role.setText(getIntent().getExtras().getString("role"));
            if (getIntent().getExtras().getString("role")==null){
                role.setVisibility(View.GONE);
            }
            code=getIntent().getExtras().getString("code");
            juese=getIntent().getExtras().getString("role");
        }
        arr= (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("title");
        for (int i=1;i<arr.size();i++){
            tempButton=new RadioButton(this);
            tempButton.setText(arr.get(i).get("title"));
            radioGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i==1){
                tempButton.setChecked(true);
            }
        }
        Log.d("asdf>>>", arr.get(1).get("title"));
        juese=arr.get(1).get("id");
        opinion=getIntent().getExtras().getString("opinion");
        if (opinion.equals("0")){
            mName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            registerText.setText("用户名注册");
            reg_type="username";
        }
        if (opinion.equals("1")){
            mName.setHint("输入邮箱");
            mName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            registerText.setText("邮箱注册");
            Drawable drawable = getResources().getDrawable(R.drawable.email);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mName.setCompoundDrawables(drawable, null, null, null);
            reg_type="email";
        }
        if (opinion.equals("2")){
            mName.setHint("输入手机号");
            mName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
            registerText.setText("手机号注册");
            Drawable drawable = getResources().getDrawable(R.drawable.mobile);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mName.setCompoundDrawables(drawable, null, null, null);
            reg_type="mobile";
        }
        if (arr.get(0).get("open_invite").equals("0")){
            openRegist.setVisibility(View.GONE);
        }
        if (arr.get(0).get("email_verify_type").equals("2")&&registerText.getText()=="邮箱注册"){
            checkId.setVisibility(View.VISIBLE);
            type="email";
        }
        if (arr.get(0).get("email_verify_type").equals("1")&&registerText.getText()=="邮箱注册"){
            type="email";
        }
        if (arr.get(0).get("mobile_verify_type").equals("1")&&registerText.getText()=="手机号注册"){
            checkId.setVisibility(View.VISIBLE);
            type="mobile";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            role.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.GONE);
            role.setText(data.getStringExtra("role"));
            code=data.getExtras().getString("code");
            juese=data.getExtras().getString("id");
        }
    }

    private void initView() {
        mClose = (RelativeLayout) findViewById(R.id.registClose);
        mName = (EditText) findViewById(R.id.Redit_name);
        mPassword = (EditText) findViewById(R.id.Redit_password);
        verifyId = (EditText) findViewById(R.id.verifyId);
        checkId = (LinearLayout) findViewById(R.id.checkId);
        mNext = (RelativeLayout) findViewById(R.id.next);
        mStartlogin = (TextView) findViewById(R.id.startlogin);
        openRegist= (TextView) findViewById(R.id.openRegist);
        registerText= (TextView) findViewById(R.id.registerText);
        role= (EditText) findViewById(R.id.role);
        nickname= (EditText) findViewById(R.id.nickname);
        radioGroup= (RadioGroup) findViewById(R.id.radioGroup);
        sendVerify= (Button) findViewById(R.id.sendVerify);
        progressDialog=new ProgressDialog(this);
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        MyOnClickListener my = new MyOnClickListener();
        mClose.setOnClickListener(my);
        mNext.setOnClickListener(my);
        mStartlogin.setOnClickListener(my);
        openRegist.setOnClickListener(my);
        sendVerify.setOnClickListener(my);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tempButton = (RadioButton)findViewById(checkedId); // 通过RadioGroup的findViewById方法，找到ID为checkedID的RadioButton
                // 以下就可以对这个RadioButton进行处理了
                if (checkedId>1) {
                    checkedId = (checkedId - 1) % radioGroup.getChildCount() + 1;
                    Log.d("RadioButton>>>>>>>", checkedId + "");
                }

                juese=arr.get(checkedId).get("id");
                Log.d("juese",juese);
            }
        });
    }
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int mId = v.getId();
            switch (mId) {
                case R.id.registClose:
                    finish();
                    break;
                case R.id.next:
                    username = mName.getText().toString();
                    password = mPassword.getText().toString();
                    if(opinion.equals("0")) {
                        if (!username.matches("[0-9A-Za-z_]*")) {
                            Toast.makeText(RegistetActivity.this, "用户名只能是英文数字和下划线", Toast.LENGTH_SHORT).show();
                        } else if (username.length() > 16 || username.length() < 4) {
                            Toast.makeText(RegistetActivity.this, "用户名不能小于4或者大于16字符", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(opinion.equals("1")){
                        if (!username.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$")){
                            Toast.makeText(RegistetActivity.this,"邮箱不正确",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(opinion.equals("2")){
                        if(!username.matches("^(13|15|17|18)\\d{9}$")){
                            Toast.makeText(RegistetActivity.this,"号码不正确",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (!username.equalsIgnoreCase(null)
                            && !password.equalsIgnoreCase(null)
                            && !username.equals("") && !password.equals("")) {
                        if (verifyId.getText().toString().equals("")&&(checkId.getVisibility()==View.VISIBLE)){
                            Toast.makeText(RegistetActivity.this,"请填写验证码",Toast.LENGTH_SHORT).show();
                            return;
                        }
                      // 请求注册功能
                        mUserapi.setHandler(hand);
                        mUserapi.register(username, password, code, juese, nickname.getText().toString(),reg_type,verifyId.getText().toString());
                        Log.d("username", username);
                        Log.d("password", password);
                        Log.d("code", code);
                        Log.d("juese", juese);
                        Log.d("nickname", nickname.getText().toString());
                        Log.d("reg_type",reg_type);
                        progressDialog.setTitle("提示");
                        progressDialog.setMessage("注册中...");
                        progressDialog.show();
                    } else {
                        Toast.makeText(RegistetActivity.this, "请先填写昵称或密码", Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
                case R.id.startlogin:
                    finish();
                    break;
                case R.id.openRegist:
                    Intent intent1=new Intent(RegistetActivity.this,BeforeRegistetActivity.class);
                    startActivityForResult(intent1,0);
                    break;
                case R.id.sendVerify:
                    if (mName.getText().toString().equals("")){
                       if (type.equals("email")){
                           Toast.makeText(RegistetActivity.this,"请填写邮箱",Toast.LENGTH_SHORT).show();
                           return;
                       }else {
                           Toast.makeText(RegistetActivity.this,"请填写手机号",Toast.LENGTH_SHORT).show();
                           return;
                       }
                    }
                    time.start();//开始计时
                    mUserapi.setHandler(handler);
                    mUserapi.sendVerify(mName.getText().toString(), type);
                    break;
                default:
            }
        }
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.getString("success").equals("true")){
                    Toast.makeText(RegistetActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegistetActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    // INSERT INTO `quser`( `uname`, `upassword`) VALUES ('111111','111111');
    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
                String result = (String) msg.obj;
                progressDialog.cancel();
                try {
                    JSONObject jsonObject=new JSONObject(result);

                    if(jsonObject.getBoolean("success")){
                        if (jsonObject.getString("message").equals("注册成功，请登录邮箱进行激活")){
                            new AlertDialog.Builder(RegistetActivity.this)
                                    .setTitle("注意")
                                    .setMessage("请先进您的邮箱进行验证,再登入账号")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(RegistetActivity.this,MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                            return;
                        }
                        BaseFunction.putSharepreference("username", username, RegistetActivity.this, Url.SharedPreferenceName);
                        BaseFunction.putSharepreference("password", password, RegistetActivity.this, Url.SharedPreferenceName);
                        //TODO 注册后的nickname问题，头像路径问题
                        ToastHelper.showToast("注册成功了", RegistetActivity.this);
                        Url.activityFrom="registe";
                        Url.MYUSERINFO = myJson.getUserAllInfo(result);
                        mUserapi.saveUserInfoToNative(RegistetActivity.this);
//                        login.userLogin(username, password);
                        Intent intent = new Intent(RegistetActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.cancel();
                        ToastHelper.showToast(jsonObject.getString("message"),RegistetActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        ;
    };


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            sendVerify.setText("重新验证");
            sendVerify.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            sendVerify.setClickable(false);
            sendVerify.setText(millisUntilFinished / 1000 + "秒");
        }
    }

}
