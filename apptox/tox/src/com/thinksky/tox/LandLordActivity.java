package com.thinksky.tox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.adapter.LandlordComAdapter;
import com.thinksky.info.Com2Com;
import com.thinksky.info.PostComment;
import com.thinksky.redefine.MyScrollView;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ForumApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LandLordActivity extends Activity implements View.OnClickListener {
    private static boolean ADDMORECOMMENT=false;
    private static boolean SENDCOMMENT=false;
    private ImageView mUserHead,mFloatBtn;
    private TextView mUserName,mCtime,mContent,mMoreCom,mSendBtn;
    private LinearLayout mLouzhu,mPhotoLayout,mFloatLayout,mHideLayout,mEditBoxLayout;
    private ListView mListView;
    private LandlordComAdapter landlordComAdapter;
    private Context ctx;
    private EditText editText;
    private List<Com2Com> com2ComList=new ArrayList<Com2Com>();
    private PostComment postComment;
    private ForumApi forumApi=new ForumApi();
    private MyJson myJson=new MyJson();
    private int page=1;
    private MyScrollView scrollView;
    private Com2Com to_reply_landlord;
    private LoadImg loadImg;
    private int type=0;
    Bundle bundle;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        loadImg=new LoadImg(ctx);
        setContentView(R.layout.activity_land_lord);
        intent = getIntent();

        scrollView=(MyScrollView)findViewById(R.id.landlord_scrollview);
        mUserHead=(ImageView)findViewById(R.id.landlord_head_UserHead);
        mFloatBtn=(ImageView)findViewById(R.id.landlord_floatComBtn);
        mUserName=(TextView)findViewById(R.id.landlord_head_Username);
        mCtime=(TextView)findViewById(R.id.landlord_louCeng);
        mContent=(TextView)findViewById(R.id.landlord_comContent);
        mMoreCom=(TextView)findViewById(R.id.landlord_more_com2com);
        mLouzhu=(LinearLayout)findViewById(R.id.landlord_louzhu);
        mPhotoLayout=(LinearLayout)findViewById(R.id.landlord_photoLayout);
        mListView=(ListView)findViewById(R.id.landlord_comList);
        mFloatLayout=(LinearLayout)findViewById(R.id.landlord_floatLayout);
        mHideLayout=(LinearLayout)findViewById(R.id.landlord_hideLayout);
        mEditBoxLayout=(LinearLayout)findViewById(R.id.landlord_editBox_layout);
        mSendBtn=(TextView)findViewById(R.id.landlord_send_com);
        editText=(EditText)findViewById(R.id.landlord_editBox);

        bundle = intent.getBundleExtra("postComment");
        postComment=(PostComment)bundle.getSerializable("comInfo");

        com2ComList=postComment.getCom2comList();
        landlordComAdapter = new LandlordComAdapter(ctx,com2ComList,new MyLandlordCallBack());
        editText.addTextChangedListener(watcher);
        mListView.setAdapter(landlordComAdapter);
        landlordComAdapter.notifyDataSetChanged();
        if(postComment.getCom2comList().size()==0){
            mMoreCom.setVisibility(View.GONE);
        }else  if(postComment.getCom2comList().size()==10){
            mMoreCom.setVisibility(View.VISIBLE);
            mMoreCom.setText("查看更多回复");
        }else{
            mMoreCom.setVisibility(View.GONE);
        }
        BaseFunction .showImage(this, mUserHead, postComment.getUserInfo().getAvatar(), loadImg, Url.IMGTYPE_HEAD);
        mUserName.setText(postComment.getUserInfo().getNickname());
        mCtime.setText(postComment.getcTime());
        mContent.setText(postComment.getComContent());
        if(postComment.getImgList().size()>0){
            for(String imgUrl:postComment.getImgList()){
                ImageView imageView=new ImageView(ctx);
                imageView.setImageResource(R.drawable.friends_sends_pictures_no);
           /* LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,-20,0,-20);*/
                imageView.setPadding(0, 0, 0, 0);
           /* imgView.setLayoutParams(layoutParams);*/
                mPhotoLayout.addView(imageView);
                BaseFunction.showImage(ctx,imageView,imgUrl,loadImg,Url.IMGTYPE_WEIBO);
            }
        }
        forumApi.setHandler(handler);
        mMoreCom.setOnClickListener(this);
        mFloatBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mFloatLayout.setOnClickListener(this);
        mHideLayout.setOnClickListener(this);
        mUserHead.setOnClickListener(this);
        scrollView.smoothScrollTo(0, 0);

        type=intent.getIntExtra("type",-1);
        if(type==Url.Type_landlord){
            to_reply_landlord=(Com2Com)bundle.get("com2com");
            mHideLayout.setVisibility(View.GONE);
            editText.setText("回复 @"+to_reply_landlord.getUserInfo().getNickname()+"：");
            mEditBoxLayout.setVisibility(View.VISIBLE);
        }else if(type==Url.Type_postCom){
            mHideLayout.setVisibility(View.GONE);
            editText.setText("");
            mEditBoxLayout.setVisibility(View.VISIBLE);
        }

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFloatLayout.setVisibility(View.GONE);
                mEditBoxLayout.setVisibility(View.GONE);
                mHideLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private class MyLandlordCallBack implements LandlordComAdapter.LandLordCallBack{
        @Override
        public void callBack(Com2Com com) {
            to_reply_landlord=com;
            mHideLayout.setVisibility(View.GONE);
            mEditBoxLayout.setVisibility(View.VISIBLE);
            editText.setText("回复@ "+com.getUserInfo().getNickname()+"：");
            type=Url.Type_landlord;
        }
    }

    private TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count!=0){

                mSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.forum_enable_btn_send));
                mSendBtn.setTextColor(Color.WHITE);
            }else{
                mSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                mSendBtn.setTextColor(Color.parseColor("#A9ADB0"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()!=0){
                mSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.forum_enable_btn_send));
                mSendBtn.setTextColor(Color.WHITE);
            }else{
                mSendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                mSendBtn.setTextColor(Color.parseColor("#A9ADB0"));
            }
        }
    };
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.landlord_more_com2com:
                page++;
                forumApi.setHandler(handler);
                initFlag(true,false);
                forumApi.getComments(postComment.getPostComId(),page+"","10",postComment.getPostId());
                break;
            case R.id.landlord_floatComBtn:
                if(mFloatLayout.isShown()){
                    ScaleAnimation scaleAnimation = new ScaleAnimation(
                            1.0f, 0.0f,1.0f,1.0f,
                            Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(200);
                    mFloatLayout.startAnimation(scaleAnimation);
                    mFloatLayout.setVisibility(View.GONE);
                }else{
                    ScaleAnimation scaleAnimation = new ScaleAnimation(
                            0.0f, 1.0f,1.0f,1.0f,
                            Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(200);
                    mFloatLayout.startAnimation(scaleAnimation);
                    mFloatLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.landlord_floatLayout:
                mHideLayout.setVisibility(View.GONE);
                editText.setText("");
                mEditBoxLayout.setVisibility(View.VISIBLE);
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1.0f, 0.0f,1.0f,1.0f,
                        Animation.RELATIVE_TO_SELF,1f,
                        Animation.RELATIVE_TO_SELF,1f);
                scaleAnimation.setDuration(200);
                mFloatLayout.startAnimation(scaleAnimation);
                mFloatLayout.setVisibility(View.GONE);
                break;
            case R.id.landlord_hideLayout:
                this.finish();
                break;
            case R.id.landlord_send_com:
                initFlag(false,true);
                if (editText.getText().toString().equals("")){
                    Toast.makeText(LandLordActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(type==Url.Type_landlord){
                    forumApi.sendComment(to_reply_landlord.getPostId(),editText.getText().toString(),to_reply_landlord.getId(),"0");
                    Log.d("111111>>>>>>>>>", to_reply_landlord.getPostId() + editText.getText().toString() + "0" + to_reply_landlord.getId());
                }else if(type==Url.Type_postCom){
                    forumApi.sendComment(postComment.getPostId(), editText.getText().toString(), "0", postComment.getPostComId());
                    Log.d("222222>>>>>>>>>",postComment.getPostId() + editText.getText().toString() + "0" + postComment.getPostComId());
                }
                break;
            case R.id.landlord_head_UserHead:
                forumApi.goUserInfo(ctx,postComment.getUserInfo().getUid());
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                if(ADDMORECOMMENT){
                    try{
                        JSONObject jsonObject=new JSONObject((String)msg.obj);
                        JSONArray jsonArray=jsonObject.getJSONArray("list");
                        List<Com2Com> list=myJson.getComments(jsonArray);
                        for (Com2Com item:list){
                            com2ComList.add(item);
                        }
                        if(list.size()<10){
                            mMoreCom.setVisibility(View.GONE);
                        }
                        landlordComAdapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else if(SENDCOMMENT){
                    Com2Com com=myJson.getComment((String)msg.obj);
                    if (com.getContent()!=null) {
                        if (com2ComList.size() < 10) {
                            com2ComList.add(com);
                            ToastHelper.showToast("评论成功", ctx);
                        } else {
                            ToastHelper.showToast("评论成功", ctx);
                        }
                        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        editText.setText("");
                        mEditBoxLayout.setVisibility(View.GONE);
                        mHideLayout.setVisibility(View.VISIBLE);
                        landlordComAdapter.notifyDataSetChanged();
                    }else{
                        ToastHelper.showToast("评论失败", ctx);
                    }
                }
            }else{
//                ToastHelper.showToast((String)msg.obj,ctx);
                ToastHelper.showToast("未登录",ctx);
            }
        }
    };

    private void initFlag(boolean addComment,boolean sendCom){
        ADDMORECOMMENT=addComment;
        SENDCOMMENT=sendCom;
    }

}
