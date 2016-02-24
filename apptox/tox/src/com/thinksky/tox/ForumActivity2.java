package com.thinksky.tox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.ParallaxHeaderViewPagerForum.SampleListFragment;
import com.thinksky.ParallaxHeaderViewPagerForum.ScrollTabHolder;
import com.thinksky.ParallaxHeaderViewPagerForum.ScrollTabHolderFragment;
import com.thinksky.info.ForumInfo;
import com.thinksky.info.PostInfo;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ForumApi;
import com.tox.IssueApi;
import com.tox.ToastHelper;
import com.tox.Url;

import java.util.ArrayList;
import java.util.List;


public class ForumActivity2 extends ActionBarActivity implements ScrollTabHolder,
        ViewPager.OnPageChangeListener ,View.OnClickListener{

    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();
    private static boolean GETFORUMPOST=false;
    private static boolean GETFORUMS=true;
    private static boolean ADDFORUMPOST=false;
    private static boolean REFRESH=false;
    private static boolean SENDPOSTCOMMENT=false;
    private View mHeader;
    private Context ctx;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private LinearLayout mEditBox,mForumBackground;
    private int mActionBarHeight;
    private int mMinHeaderHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private RelativeLayout mForumTop;
    private RelativeLayout mParentlayout;
    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();
    private ImageView floatImg;
    private RelativeLayout floatingRelative,mForumBody,mForumLoading;
    private TypedValue mTypedValue = new TypedValue();
    private EditText editText;
    private SpannableString mSpannableString;
    private ImageView mMenu,mWritePost,mForumLogo;
    private TextView mForumSendCom,mForumSignature,mForumPostCountTopicCount,mForumLastReply;
    private List<ScrollTabHolderFragment> mFragmentList=new ArrayList<ScrollTabHolderFragment>();
    private ForumApi forumApi=new ForumApi();
    private MyJson myJson=new MyJson();
    //当前论坛的加载页数
    private int page=1;
    private List<PostInfo> mPostList=new ArrayList<PostInfo>();
    private List<ForumInfo> mForumList=new ArrayList<ForumInfo>();
    //第几个论坛
    private int fragPage=0;
    private LoadImg imgLoad;
    private boolean addFlag=false;
    //声明session_id
    private String session_id;
    private String forumTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_activity2);
        mMinHeaderHeight = getResources().getDimensionPixelSize(
                R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(
                R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight;

        //获取session_id
        IssueApi issueApi=new IssueApi();
        session_id = issueApi.getSeesionId();
        getInitData();

    }

    private void initView(List<ForumInfo> forumInfos){
        ctx=ForumActivity2.this;
        imgLoad=new LoadImg(this);
        mForumBody=(RelativeLayout)findViewById(R.id.Forum_body);
        mForumLoading=(RelativeLayout)findViewById(R.id.Forum_loading);
        mHeader = findViewById(R.id.header);
        mForumTop=(RelativeLayout)findViewById(R.id.Forum_top);
        mParentlayout=(RelativeLayout)findViewById(R.id.Forum_parent_layout);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.ParallaxHeaderViewPager_tabs);
        mViewPager = (ViewPager) findViewById(R.id.ParallaxHeaderViewPager_pagers);
        mViewPager.setOffscreenPageLimit(20);
        mEditBox=(LinearLayout)findViewById(R.id.Forum_editBox);
        mForumLastReply=(TextView)findViewById(R.id.Forum_lastReply);
        mForumPostCountTopicCount=(TextView)findViewById(R.id.Forum_postCount_topicCount);
        mForumSignature=(TextView)findViewById(R.id.Forum_signature);
        mForumLogo=(ImageView)findViewById(R.id.Forum_logo);
        mForumBackground=(LinearLayout)findViewById(R.id.Forum_backgroundImg);
        //test
        FragmentManager fragmentManager=getSupportFragmentManager();
        
        for (int i=0;i<forumInfos.size();i++){
           
            mFragmentList.add((ScrollTabHolderFragment)SampleListFragment.newInstance(i,new MySampleListCallBack(),mEditBox));

        }

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(),mFragmentList,forumInfos);
        mPagerAdapter.setTabHolderScrollingContent(this);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(this);
        mSpannableString = new SpannableString(
                getString(R.string.app_name));
        mMenu=(ImageView)findViewById(R.id.Menu);
        mWritePost=(ImageView)findViewById(R.id.Forum_writePost);
        mMenu.setOnClickListener(this);
        mWritePost.setOnClickListener(this);
        floatImg=(ImageView)findViewById(R.id.floating_view);
        floatingRelative=(RelativeLayout)findViewById(R.id.floating_relativeLayout);
        floatingRelative.setAlpha(80);
        floatingRelative.setOnClickListener(this);
        editText=(EditText)findViewById(R.id.Forum_index_edittext);
        editText.addTextChangedListener(watcher);
        mForumSendCom=(TextView)findViewById(R.id.Forum_index_send_com);
        mForumSendCom.setOnClickListener(this);
    }



    private TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count!=0){

                mForumSendCom.setBackgroundDrawable(getResources().getDrawable(R.drawable.forum_enable_btn_send));
                mForumSendCom.setTextColor(Color.WHITE);
            }else{
                mForumSendCom.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                mForumSendCom.setTextColor(Color.parseColor("#A9ADB0"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()!=0){

                mForumSendCom.setBackgroundDrawable(getResources().getDrawable(R.drawable.forum_enable_btn_send));
                mForumSendCom.setTextColor(Color.WHITE);
            }else{
                mForumSendCom.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                mForumSendCom.setTextColor(Color.parseColor("#A9ADB0"));
            }
        }
    };
    @Override
    public void onPageScrollStateChanged(int arg0) {
//        ToastHelper.showToast(""+arg0,ForumActivity2.this);
        // nothing
        if(arg0==ViewPager.SCROLL_STATE_DRAGGING){
            mEditBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
//        ToastHelper.showToast("position:"+position+"positionOffset"+positionOffset,ForumActivity2.this);
        // nothing
    }

    @Override
    public void onPageSelected(int position) {
      //  ToastHelper.showToast(""+position,ForumActivity2.this);
        fragPage=position;
        SampleListFragment f=(SampleListFragment)mFragmentList.get(position);
        if(f.firstInitData){
            getForumPost(mForumList.get(position).getForumId(),"1","10",position);
            forumTitle = mForumList.get(position).getTitle();
        }
        initForumData();
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mPagerAdapter
                .getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
        currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper
                .getTranslationY(mHeader)));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            ViewHelper.setTranslationY(mHeader,
                    Math.max(-scrollY, mMinHeaderTranslation));
        }
        mViewPager.getScrollY();

//        mForumTop.layout(0, mViewPager.getScrollY(), mForumTop.getWidth(), mViewPager.getScrollY()+mForumTop.getHeight());
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        // nothing
    }

    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();
        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(),
                view.getBottom());
        return rect;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getTheme().resolveAttribute(android.R.attr.actionBarSize,
                    mTypedValue, true);
        } else {
            getTheme()
                    .resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
        }

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch (viewId){
            case R.id.Menu:
                super.onBackPressed();
                break;
            case R.id.writePost:
                break;
            case R.id.floating_relativeLayout:
//                ToastHelper.showToast("refresh刷新",ctx);
                AnimationSet animationSet=new AnimationSet(true);
                RotateAnimation rotateAnimation=new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(2000);
                animationSet.addAnimation(rotateAnimation);
                floatImg.startAnimation(animationSet);
                initFlag(false,false,true,false,false);
                getForumPost(mForumList.get(fragPage).getForumId(),"1","10",fragPage);
                break;
            case R.id.Forum_index_send_com:
                //ToastHelper.showToast("发送评论",this);
                String com=editText.getText().toString().trim();
                if (com.equals("")){
                    Toast.makeText(ForumActivity2.this,"评论不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (session_id.equals("")){
                    Toast.makeText(ForumActivity2.this,"未登录",Toast.LENGTH_SHORT).show();
                }
                initFlag(false,false,false,false,true);
                forumApi.sendPostComment(Url.PostID,com);
                break;
            case R.id.Forum_writePost:
                Intent intent=new Intent(ForumActivity2.this,SendPostActivity.class);
                intent.putExtra("forumId",mForumList.get(fragPage).getForumId());
                startActivity(intent);
                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initFlag(false,false,false,false,false);
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;

        private ScrollTabHolder mListener;
        private List<ScrollTabHolderFragment> fragmentList=new ArrayList<ScrollTabHolderFragment>();
        private List<ForumInfo> list;
        public PagerAdapter(FragmentManager fm,List<ScrollTabHolderFragment> fragmentList,List<ForumInfo> list) {
            super(fm);
            this.fragmentList=fragmentList;
            this.list=list;
            mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
        }

        public void setTabHolderScrollingContent(ScrollTabHolder listener) {
            mListener = listener;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).getTitle();
//            return TITLES[position];
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int position) {
            /*ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) SampleListFragment
                    .newInstance(position);*/
            ScrollTabHolderFragment fragment = fragmentList.get(position);
            mScrollTabHolders.put(position, fragment);
            if (mListener != null) {
                fragment.setScrollTabHolder(mListener);
            }
            Log.e("postion",position+"");

            return fragment;
        }

        public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
            return mScrollTabHolders;
        }
    }

    public static void  showEditBox(int position){

    }

    private void showEditbox(int position){
       // ToastHelper.showToast("show position:"+position,ctx);
        mEditBox.setVisibility(View.VISIBLE);
    }

    private class MySampleListCallBack implements SampleListFragment.SampListCallBack{

        @Override
        public void callback(int flag,int page) {
            switch (flag){
                case R.id.Post_comImg:

                    break;
                case 19:
                   // ToastHelper.showToast("callback被点击了,当前page +"+page,ctx);
                    addFlag=true;
                    forumApi.getPosts(mForumList.get(fragPage).getForumId(),page+"","10");
                    break;
            }
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==404){
                ToastHelper.showToast("请求失败",ForumActivity2.this);

            }else if(msg.what==0){
                String result=(String) msg.obj;
                if(result!=null){
                    if(ADDFORUMPOST){

                    }
                    if(GETFORUMPOST){
                        mPostList=myJson.getPostInfos(result,forumTitle);
                        if (mPostList.size() > 0) {
                            SampleListFragment f = (SampleListFragment) mFragmentList.get(fragPage);
                            if (mForumLoading.isShown()) {
                                mForumLoading.setVisibility(View.GONE);
                                mForumBody.setVisibility(View.VISIBLE);
                            }
                            f.updatePosts(mPostList, addFlag);

                            addFlag = false;
                        }else{
                            ToastHelper.showToast("数据请求失败", ForumActivity2.this);
                        }
                    }
                    if(REFRESH){
                        mPostList=myJson.getPostInfos(result,forumTitle);
                        if (mPostList.size() > 0) {
                            SampleListFragment f = (SampleListFragment) mFragmentList.get(fragPage);
                            f.updatePosts(mPostList, addFlag);
                            floatImg.clearAnimation();
                        }else{
                            ToastHelper.showToast("数据请求失败", ForumActivity2.this);
                        }
                    }
                    if(GETFORUMS){
                        mForumList=myJson.getForumInfos(result);
                        if(mForumList.size() > 0){
                            forumTitle = mForumList.get(0).getTitle();
                            getForumPost(mForumList.get(0).getForumId(),"1","count",0);
                            initView(mForumList);
                            initForumData();
                        }else{
                            ToastHelper.showToast("数据请求失败",ForumActivity2.this);
                        }
                    }
                    if(SENDPOSTCOMMENT){
                        if(myJson.getSuccess(result)){

                            ToastHelper.showToast("发送成功",ctx,R.drawable.checkmark);
                            InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            mEditBox.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        Log.e("回来了","所以resume");

//        insertPost();

    }
    private void insertPost(){
        if(Url.is2InsertPost){
            mPostList.add(null);
            for(int i=mPostList.size()-1;i>0;i--){

                mPostList.set(i,mPostList.get(i-1));
            }
            mPostList.set(0,Url.postInfo);

            Url.is2InsertWeibo=false;
            SampleListFragment f=(SampleListFragment)mFragmentList.get(fragPage);

            f.updatePosts(mPostList,addFlag);

        }

    }
    /**
     * 获取论坛的板块数据
     */
    private void getInitData(){
        initFlag(false,false,false,true,false);
        forumApi.setHandler(handler);
        forumApi.getForumModules();
    }

    /**
     *
     * @param fourmId
     * @param page
     * @param count
     * @param fragPostion
     */
    private void getForumPost(String fourmId,String page,String count,int fragPostion){
        initFlag(false,true,false,false,false);
        forumApi.getPosts(fourmId,"1","10");

    }

    /**
     * 设置请求的类型
     * @param addForumPost
     * @param getForumPost
     * @param refresh
     * @param getForums
     */
    private void initFlag(boolean addForumPost,boolean getForumPost,boolean refresh,boolean getForums,boolean sendPostCom){
        ADDFORUMPOST=addForumPost;
        GETFORUMS=getForums;
        REFRESH=refresh;
        GETFORUMPOST=getForumPost;
        SENDPOSTCOMMENT=sendPostCom;
        addFlag=false;
    }

    /**
     *显示论坛发的基本信息
     */
    private void initForumData(){
        mForumSignature.setText(mForumList.get(fragPage).getDescription());

        mForumPostCountTopicCount.setText("主题："+mForumList.get(fragPage).getTopicCount()+" 帖子："+mForumList.get(fragPage).getPostCount());
        mForumLastReply.setText("最后回复时间："+mForumList.get(fragPage).getLastReplyTime());
        BaseFunction.showImage(ctx,mForumLogo,mForumList.get(fragPage).getLogo(),imgLoad, Url.IMGTYPE_WEIBO);
        BaseFunction.setLayoutBackGround(mForumLogo,ctx,mForumBackground,mForumList.get(fragPage).getBackground(),imgLoad);
    }

}
