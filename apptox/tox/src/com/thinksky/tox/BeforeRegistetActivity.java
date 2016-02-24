package com.thinksky.tox;

import android.app.Activity;
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

import com.thinksky.utils.MyJson;
import com.tox.LoginApi;
import com.tox.Url;

/**
 * Created by 王杰 on 2015/7/9.
 */
public class BeforeRegistetActivity extends Activity{
    private RelativeLayout next,registClose;
    private LoginApi loginApi;
    private MyJson myJson=new MyJson();
    private EditText checkbox;
    private Handler handler;
    private String[] status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Url.activityFrom="BeforeRegistetActivity";
        setContentView(R.layout.before_regist);
        status=new String[2];
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        String result= (String) msg.obj;
                        status=myJson.checkRegisterStatus(result);
                        Log.d("id>>>:",status[0]);
                        Log.d("title>>>:",status[1]);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Intent intent=new Intent(BeforeRegistetActivity.this,RegistetActivity.class);
//                                intent.putExtra("role",status[1]);
//                                intent.putExtra("id",status[0]);
//                                intent.putExtra("code",checkbox.getText().toString());
//                                startActivity(intent);
                                Intent intent=new Intent();
                                intent.putExtra("role",status[1]);
                                intent.putExtra("id",status[0]);
                                intent.putExtra("code",checkbox.getText().toString());
                                setResult(1, intent);
                                finish();
                            }
                        },500);
                        break;
                    case 404:
                        Toast.makeText(BeforeRegistetActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 800:
                        Toast.makeText(BeforeRegistetActivity.this, "邀请码不存在", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
            }
        };
        loginApi=new LoginApi();
        next= (RelativeLayout) findViewById(R.id.next);
        checkbox= (EditText) findViewById(R.id.checkbox);
        registClose= (RelativeLayout) findViewById(R.id.registClose);
        loginApi.setHandler(handler);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = checkbox.getText().toString();
                if (code.equals("")) {
                    Toast.makeText(BeforeRegistetActivity.this,"请填写验证码",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginApi.beforeRegister(code);
            }
        });
        registClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
