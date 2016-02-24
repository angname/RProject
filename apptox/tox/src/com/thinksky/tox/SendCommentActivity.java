package com.thinksky.tox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.Face.FaceParser;
import com.thinksky.Face.FaceView;
import com.thinksky.info.AshamedInfo;
import com.thinksky.info.WeiboCommentInfo;
import com.thinksky.info.WeiboInfo;
import com.thinksky.model.ActivityModel;
import com.thinksky.utils.MyJson;
import com.tox.TouchHelper;
import com.tox.Url;
import com.tox.WeiboApi;

/**
 * 发发评论模块
 *
 * @author 534429149
 */

public class SendCommentActivity extends Activity implements FaceView.Work {

    private static int FACESHOW=1;
    private static int FACELOAD=1;
    private AshamedInfo info = null;
    private RelativeLayout Comment_Send;
    private EditText Comment_Edit;
    private RelativeLayout Comment_Back,mFaceBtnArea;
    private WeiboApi weiApi = new WeiboApi();
    private WeiboInfo weiboInfo;
    private ProgressDialog progressDialog;
    private int commentType = 0;
    private WeiboCommentInfo weiboCommentInfo;
    private FaceView mFaceView;
    private Context context=this;
    private LinearLayout mFaceArea,mFaceBtn;
    private FaceParser mFaceParser;
    private MyJson myJson=new MyJson();
    private int mFaceFlag=0;
     private int isLoadFace=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_comment);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("发布中...");
        initView();
        Intent intent = this.getIntent();
        if (intent.getIntExtra("commentType", 0) == 1) {
            commentType = 1;
            Bundle bundle = intent.getBundleExtra("weiboInfo");
            weiboInfo = (WeiboInfo) bundle.getSerializable("weiboInfo");
        } else {
            commentType = 2;
            Bundle bundle = intent.getBundleExtra("comment");
            weiboCommentInfo = (WeiboCommentInfo) bundle.getSerializable("comment");
            weiboInfo = (WeiboInfo) bundle.getSerializable("weiboInfo");
            Comment_Edit.setText("回复@" + weiboCommentInfo.getUser().getNickname() + "：");
        }


    }

    private void initView() {
        Comment_Back = (RelativeLayout) findViewById(R.id.Comment_Back);
        Comment_Send = (RelativeLayout) findViewById(R.id.Comment_Send);
        Comment_Edit = (EditText) findViewById(R.id.Comment_Edit);
        mFaceArea=(LinearLayout)findViewById(R.id.Comment_FaceArea);
        mFaceBtn=(LinearLayout)findViewById(R.id.Comment_FaceBtn);
        mFaceBtnArea=(RelativeLayout)findViewById(R.id.Comment_face);
        Comment_Back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendCommentActivity.this.finish();
            }
        });
        Comment_Edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                RelativeLayout.LayoutParams layoutParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(hasFocus){
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    mFaceArea.setVisibility(View.GONE);
                    mFaceFlag=0;
                }else{
                    layoutParams.addRule(RelativeLayout.ABOVE,R.id.Comment_FaceArea);
                    mFaceArea.setVisibility(View.VISIBLE);
                    mFaceFlag=0;
                }
                mFaceBtnArea.setLayoutParams(layoutParams);
            }
        });
        Comment_Send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!Url.SESSIONID.equals("")) {
                    if (Comment_Edit.getText().toString().trim().length() <= 5 || Comment_Edit.getText().toString().trim().equals("")) {
                        Toast.makeText(SendCommentActivity.this, "请输入5个以上有效字符", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("StartSenComment>>>>>>>", "");
                        progressDialog.show();
                        weiApi.setHandler(hand);
                        if (commentType == 2) {
                            weiApi.sendWeiboComment(weiboInfo.getWid(), Comment_Edit.getText().toString(), Integer.parseInt(weiboCommentInfo.getId()));
                            Log.e("BUG1111>>>>>>",weiboInfo.getWid()+ Comment_Edit.getText().toString() + weiboCommentInfo.getId());
                        } else {
                            weiApi.sendWeiboComment(weiboInfo.getWid(), Comment_Edit.getText().toString(), 0);
                            Log.e("BUG2222>>>>>>", weiboInfo.getWid() + Comment_Edit.getText().toString() + 0);
                        }

                    }
                } else {
                    Intent intent = new Intent(SendCommentActivity.this,
                            LoginActivity.class);
                    intent.putExtra("entryActivity", ActivityModel.WEIBOCOMMENT);
                    startActivity(intent);
                }
            }
        });
        mFaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment_Edit.clearFocus();
                RelativeLayout.LayoutParams layoutParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Comment_Edit.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                if(mFaceFlag==FACESHOW){
                   layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    mFaceArea.setVisibility(View.GONE);
                   mFaceFlag=0;
                }else{
                    if(isLoadFace!=FACELOAD){
                        loadFaces();
                        isLoadFace=1;
                    }
                    layoutParams.addRule(RelativeLayout.ABOVE,R.id.Comment_FaceArea);
                    mFaceArea.setVisibility(View.VISIBLE);
                    mFaceFlag=1;

                }
                mFaceBtnArea.setLayoutParams(layoutParams);

            }
        });
        mFaceBtn.setOnTouchListener(new TouchHelper(SendCommentActivity.this,"#BBBBBB","#F9F9F9","color"));
    }

    private void loadFaces(){
        mFaceView=new FaceView(context,null,this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFaceArea.addView(mFaceView,layoutParams);
        FaceParser.init(this);
        mFaceParser=FaceParser.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();

    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

                if(Url.activityFrom.equals("weiboDetail")){
                    String result=(String)msg.obj;
                    WeiboCommentInfo weiboComInfo=myJson.getWeiboComAfterCom(result);
                    Url.weiboCommentInfo=weiboComInfo;
                    Url.is2InsertWeiboCom=true;
                }
                progressDialog.dismiss();
                Toast.makeText(SendCommentActivity.this, "评论成功", Toast.LENGTH_LONG)
                        .show();
                SendCommentActivity.this.finish();

            } else {
                progressDialog.dismiss();
                Toast.makeText(SendCommentActivity.this, ",操作太频繁，评论失败", Toast.LENGTH_LONG);
            }
        }
        ;
    };



    @Override
    public void onClick(int id, String item_str) {
        replace(id,item_str,Comment_Edit);
    }

    private  void replace(int id,String item_str,TextView view){
        Drawable drawable=getResources().getDrawable(id);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());

        SpannableString spannableString=new SpannableString(item_str);
        ImageSpan span =new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(span,0,spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        view.append(spannableString);

    }
}
