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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.adapter.PostCommentAdapter;
import com.thinksky.info.PostComment;
import com.thinksky.info.PostInfo;
import com.thinksky.myview.MyDetailsListView;
import com.thinksky.redefine.MyScrollView;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ForumApi;
import com.tox.IssueApi;
import com.tox.ToastHelper;
import com.tox.Url;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends Activity implements View.OnClickListener ,MyScrollView.OnScrollListener{
   private static boolean SUPPORTPOST=false;
    private static boolean SENDCOMMENT=false;
    private static boolean GETPOSTCOM=true;
    private static boolean ADDMOREPOSTCOM=false;
    private RelativeLayout back;
    private LinearLayout mSupportBtn,mComBtn,mPostDetailEditBox,mLikeSupportLayout,mPhotoLayout;
    private MyDetailsListView mListView;
    private FinalBitmap finalBitmap;
    private TextView mForumName;
    private KJBitmap kjBitmap;
    private Context ctx;
    private PostInfo mPostInfo;
    private TextView mPostSendComBtn,mSupportCount,mComCount,postHead_ctime,postHead_content,postHead_title,postHead_userName;
    private ImageView postHead_userHead;
    private PostCommentAdapter postCommentAdapter;
    private MyJson myJson=new MyJson();
    private ForumApi forumApi=new ForumApi();
    private EditText mPostEdit;
    private MyScrollView myScrollView;
    private boolean likeFlag=false;
    private int page=1;
    private List<PostComment> mComList=new ArrayList<PostComment>();
    private ProgressBar mProgressBar;
    private LayoutInflater layoutInflater;
    private LoadImg loadImg;
    private Button ListBottem=null;
    private LinearLayout layout=null;
    private boolean addMoreCom=true;
    private ProgressBar mAddMoreProgressBar;
    //声明session_id
    private String session_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("post");
        mPostInfo=(PostInfo)bundle.getSerializable("postInfo");
        if(mPostInfo.getIs_support()==Url.SUPPORTED){
            likeFlag=true;
        }
        loadImg=new LoadImg(this);
        ctx=this;
        forumApi.setHandler(handler);

        initView();
    }

    protected void initView(){
        if (Integer.parseInt(mPostInfo.getReplyCount())<10){
             addMoreCom=false;
        }
        WindowManager wm = (WindowManager)this
                .getSystemService(Context.WINDOW_SERVICE);
        kjBitmap=KJBitmap.create();
        finalBitmap = FinalBitmap.create(ctx);
        //获取session_id
        IssueApi issueApi=new IssueApi();
        session_id = issueApi.getSeesionId();
        mComBtn=(LinearLayout)findViewById(R.id.Post_detail_comBtn);
        mForumName=(TextView)findViewById(R.id.forum_name);
        mSupportBtn=(LinearLayout)findViewById(R.id.Post_detail_likeBtn);
        mSupportBtn.setOnClickListener(this);
        mComBtn.setOnClickListener(this);
        mSupportCount=(TextView)findViewById(R.id.Post_detail_supportCounts);
        mSupportCount.setText(mPostInfo.getSupportCount());
        mComCount=(TextView)findViewById(R.id.Post_detail_comCount);
        mComCount.setText(mPostInfo.getReplyCount());

        /*mProgressBar=(ProgressBar)findViewById(R.id.Post_detail_progress);*/
        mPostEdit=(EditText)findViewById(R.id.Post_detail_edittext);
        mPostSendComBtn=(TextView)findViewById(R.id.Post_detail_sendBtn);
        mPostEdit.addTextChangedListener(watcher);
        mPostSendComBtn.setOnClickListener(this);

       /* *//*myScrollView=(MyScrollView)findViewById(R.id.Post_detail_ScrollView);*//*
        myScrollView.setOnScrollListener(this);*/

        mLikeSupportLayout=(LinearLayout)findViewById(R.id.Post_detail_like_com_layout);
        mPostDetailEditBox=(LinearLayout)findViewById(R.id.Post_detail_editBox);
        back=(RelativeLayout)this.findViewById(R.id.Post_detail_Back);
        back.setOnClickListener(new BackListener());
        mListView=(MyDetailsListView)findViewById(R.id.Post_detail_comList);
        initHeadView();
        postCommentAdapter=new PostCommentAdapter(ctx,mComList,kjBitmap,finalBitmap,mPostInfo,new MyPostDetailCallBack());
        ViewGroup.LayoutParams params=mListView.getLayoutParams();
        //设置底部加载
        ListBottem = new Button(ctx);
        ListBottem.setBackgroundColor(getResources().getColor(R.color.forumAdd));
        ListBottem.setTextColor(getResources().getColor(R.color.black));
        ListBottem.setText("点击加载更多");
//            ListBottem.setPadding(20,10,20,10);
        ListBottem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addMoreCom){
                    initFlag(false,false,false,true);
                    forumApi.getPostComments(mPostInfo.getPostId(),page+"","10");
                    listBottom();
                }
            }
        });
        mAddMoreProgressBar = new ProgressBar(ctx);
        mAddMoreProgressBar.setIndeterminate(false);
        mAddMoreProgressBar.setBackgroundColor(getResources().getColor(R.color.forumAdd));
        mAddMoreProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bg));
        mAddMoreProgressBar.setVisibility(View.GONE);

        mListView.setAdapter(postCommentAdapter);
        initFlag(false,false,true,false);
        if(Integer.parseInt(mPostInfo.getReplyCount())!=0){
            forumApi.getPostComments(mPostInfo.getPostId(),page+"","10");

        }else{
            forumApi.getPostComments(mPostInfo.getPostId(),page+"","10");

        }
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    //空闲状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        break;
                    //滚动状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                    //触摸后滚动状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                   /* mPostDetailEditBox.setVisibility(View.GONE);
                    mLikeSupportLayout.setVisibility(View.VISIBLE);*/

            }
        });
//        initHeadView();


    }

    private void listBottom() {

            mAddMoreProgressBar.setVisibility(View.VISIBLE);
            mListView.removeFooterView(ListBottem);
            mListView.addFooterView(mAddMoreProgressBar, null, false);


    }





    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.Post_detail_likeBtn:
                if(!likeFlag){
                    initFlag(false,true,false,false);
                    forumApi.supportPost(mPostInfo.getPostId());
//                     mSupportCount.setText(Integer.parseInt(mPostInfo.getSupportCount())+1);
                }else{
                    ToastHelper.showToast("重复点赞",ctx);
                }
                break;
            case R.id.Post_detail_comBtn:
                mLikeSupportLayout.setVisibility(View.GONE);
                mPostDetailEditBox.setVisibility(View.VISIBLE);

                mPostEdit.setFocusable(true);
                mPostEdit.setFocusableInTouchMode(true);
                mPostEdit.requestFocus();
//              forumApi.sendComment(mPostInfo.getPostId(),);
                break;
            case R.id.Post_detail_sendBtn:
                if (session_id.equals("")){
                    Toast.makeText(PostDetailActivity.this,"未登录",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPostEdit.getText().toString().equals("")){
                    Toast.makeText(PostDetailActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                initFlag(true,false,false,false);
                forumApi.sendPostComment(mPostInfo.getPostId(),mPostEdit.getText().toString().trim());
            default:
                break;
        }
    }

    @Override
    public void onScroll(int scrollY) {
        Log.d("scrollY+"+scrollY,"");
        if(scrollY!=0){
            mPostDetailEditBox.setVisibility(View.GONE);
            mLikeSupportLayout.setVisibility(View.VISIBLE);
        }
    }

    private class MyPostDetailCallBack implements PostCommentAdapter.PostDetailCallBack{

        @Override
        public void callback() {
            if(mPostDetailEditBox.isShown()){
                mPostDetailEditBox.setVisibility(View.GONE);
                mLikeSupportLayout.setVisibility(View.VISIBLE);
            }else{
                mPostDetailEditBox.setVisibility(View.VISIBLE);
                mLikeSupportLayout.setVisibility(View.GONE);
            }
        }
    }
    public class BackListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.Post_detail_Back:
                    PostDetailActivity.this.finish();
                    break;
            }
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                if(msg.what==404){

                }else if(msg.what==0){
                    String result=(String)msg.obj;
                    if(result!=null){
                        if(SUPPORTPOST){
                            if(myJson.getSuccess(result)){
                                ToastHelper.showToast("点赞成功",ctx);
                                mSupportCount.setText((Integer.parseInt(mPostInfo.getSupportCount())+1)+"");
                                likeFlag=true;
                            }else{
                                ToastHelper.showToast("点赞失败",ctx);
                                mSupportCount.setText(mPostInfo.getSupportCount());
                            }
                        }
                        if (SENDCOMMENT){
                            if(myJson.getSuccess(result)){
                                // 隐藏输入法
                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                // 显示或者隐藏输入法
                                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                PostComment postComment=myJson.getPostComment(result);
                                if(mComList.size()<10){
                                    mComList.add(postComment);

                                }
                                postCommentAdapter.notifyDataSetChanged();
                                mPostDetailEditBox.setVisibility(View.GONE);
                                mLikeSupportLayout.setVisibility(View.VISIBLE);
                                mPostEdit.setText("");
                                ToastHelper.showToast("发送成功",ctx,R.drawable.checkmark);
                            }
                        }
                        if(GETPOSTCOM){
                           List<PostComment> list=myJson.getPostComments(result);
                            mComList.removeAll(mComList);
                           // mProgressBar.setVisibility(View.GONE);

//                            mComList.add(postInfo2CommentInfo(mPostInfo));
                            for (PostComment item:list){
                                mComList.add(item);
                            }

                            postCommentAdapter.notifyDataSetChanged();
                            if(list.size()<10){
                                ListBottem.setText("没有更多了");
                                mListView.addFooterView(ListBottem,null,false);
                            }
                            mListView.addFooterView(ListBottem,null,false);
                            page++;
                        }
                        if(ADDMOREPOSTCOM){
                            List<PostComment> list=myJson.getPostComments(result);
                            for(PostComment item:list){
                                mComList.add(item);
                            }
                            mAddMoreProgressBar.setVisibility(View.GONE);
                            postCommentAdapter.notifyDataSetChanged();
                            mListView.removeFooterView(mAddMoreProgressBar);
                            if(list.size()<10){
                                ListBottem.setText("没有更多了");
                                addMoreCom=false;
                               mListView.addFooterView(ListBottem);
                            }
                            page++;
                        }
                    }
                }
        }
    };


    private void initFlag(boolean sendCom,boolean support,boolean getPostCom,boolean addMorePostCom){
        SENDCOMMENT=sendCom;
        SUPPORTPOST=support;
        GETPOSTCOM=getPostCom;
        ADDMOREPOSTCOM=addMorePostCom;
    }

    private void initHeadView(){
        mForumName.setText(mPostInfo.getForumTitle());
        layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView=layoutInflater.inflate(R.layout.post_detail_item_head,null);
        postHead_content=(TextView)headView.findViewById(R.id.Post_detail_content);
        postHead_content.setTextColor(getResources().getColor(R.color.black));
        postHead_ctime=(TextView)headView.findViewById(R.id.Post_detail_from);
        postHead_title=(TextView)headView.findViewById(R.id.Post_detail_title);
        postHead_userName=(TextView)headView.findViewById(R.id.Post_detail_UserName);
        postHead_userHead=(ImageView)headView.findViewById(R.id.Post_detail_UserHead);
        mPhotoLayout=(LinearLayout)headView.findViewById(R.id.Post_contentLayout);
        BaseFunction.showImage(ctx,postHead_userHead,mPostInfo.getUserInfo().getAvatar(),loadImg,Url.IMGTYPE_HEAD);
        postHead_userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumApi.goUserInfo(ctx,mPostInfo.getUserInfo().getUid());
            }
        });
        postHead_userName.setText(mPostInfo.getUserInfo().getNickname());
        postHead_title.setText(mPostInfo.getPostTitle());
        postHead_ctime.setText(mPostInfo.getCreatTime());
        postHead_content.setText(mPostInfo.getPostContent());
        for(String imgUrl:mPostInfo.getImgList()){
            ImageView imgView=new ImageView(this);
            imgView.setImageResource(R.drawable.friends_sends_pictures_no);
           /* LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,-20,0,-20);*/
            imgView.setPadding(0, 0, 0, 0);
           /* imgView.setLayoutParams(layoutParams);*/
            mPhotoLayout.addView(imgView);
            BaseFunction.showImage(ctx,imgView,imgUrl,loadImg,Url.IMGTYPE_WEIBO);
        }
        mListView.addHeaderView(headView);
    }
    private TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count!=0){

                mPostSendComBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.forum_enable_btn_send));
                mPostSendComBtn.setTextColor(Color.WHITE);
            }else{
                mPostSendComBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                mPostSendComBtn.setTextColor(Color.parseColor("#A9ADB0"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()!=0){
                mPostSendComBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.forum_enable_btn_send));
                mPostSendComBtn.setTextColor(Color.WHITE);
            }else{
                mPostSendComBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                mPostSendComBtn.setTextColor(Color.parseColor("#A9ADB0"));
            }
        }
    };
}
