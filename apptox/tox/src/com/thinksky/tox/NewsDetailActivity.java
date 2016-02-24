package com.thinksky.tox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.info.NewsDetailInfo;
import com.thinksky.info.NewsListInfo;
import com.thinksky.info.NewsReplyInfo;
import com.thinksky.redefine.CircleImageView;
import com.thinksky.utils.MyJson;
import com.tox.ImageLoader;
import com.tox.NewsApi;
import com.tox.ToastHelper;
import com.tox.Url;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 资讯详情页面
 * Created by Administrator on 2015/7/23 0023.
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener{

    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;
    private static boolean REPLY=false;
    private static boolean REPLYS=false;
    private String newsId;
    private Context mContext;
    ImageView backMenu,newsLogo,newsShare,newsReply;
    private ScrollView newsScroll;
    private RelativeLayout proBarLine;
    private TextView newsType,newsTitle,newsDescription,newsAuthor,newsTime,replyCount;
    LinearLayout replyBox,replyModule;
    private EditText replyEditText;
    private WebView newWebView;
    private TextView newsContent,sendButn;
    private NewsListInfo newsListInfo;
    private NewsApi newsApi;
    private MyHandler mHandler = new MyHandler(this);
    private NewsDetailInfo newsDetailInfo;
    Set<AsyncTask> taskCollection;
    private ImageLoader imageLoader;
    private KJBitmap kjBitmap = KJBitmap.create();
    private boolean isReply = false;
    private boolean isDelReply = false;
    //评论区控件
    View replyView;
    CircleImageView replyerHead;
    TextView replyAvatar,replyTime,replyContent,replyBtn,delReBtn;
    private int page = 1;
    private LinearLayout loadingBar;
    private TextView loadingBarText;
    private ProgressBar loadingProBar;
    private Message message;

    private Handler handler=new Handler(){
        private ArrayList<NewsReplyInfo> replyInfoList;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    MyJson myJson=new MyJson();
                    replyInfoList=myJson.getNewsReplyInfo((String) msg.obj);
                    Log.d("66666", (String) msg.obj);
                    Log.d("77777",replyInfoList.get(0).getId());
                    Log.d("77777",replyInfoList.get(0).getRow_id());
                    Log.d("77777", replyInfoList.get(0).getApp());
                    if (replyModule.getChildAt(0).getTag() != null && replyModule.getChildAt(0).getTag().equals("isNull")){
                        replyModule.removeViewAt(0);
                    }
                    replyModule.addView(addReplyView(replyInfoList.get(0)), 0);

                    Toast.makeText(NewsDetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(replyEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    replyBox.setVisibility(View.GONE);
//                    isReply = true;
//                    newsApi.setHandler(mHandler);
//                    newsApi.getNewsReply(newsId);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);
        mContext = NewsDetailActivity.this;
        newsListInfo = (NewsListInfo)getIntent().getExtras().get("newsInfo");
        newsId = newsListInfo.getId();
        newsApi = new NewsApi(mHandler);
        imageLoader = ImageLoader.getInstance();
        taskCollection = new HashSet<AsyncTask>();
        intView();
        exitAnim();
    }

    public void intView(){
        backMenu = (ImageView) findViewById(R.id.back_menu);
        newsReply = (ImageView) findViewById(R.id.news_reply);
        newsShare = (ImageView) findViewById(R.id.news_share);
        newsType = (TextView) findViewById(R.id.news_type);
        proBarLine = (RelativeLayout) findViewById(R.id.proBarLine);
        newsScroll = (ScrollView) findViewById(R.id.news_scroll);
        newsTitle = (TextView) findViewById(R.id.news_title);
        newsLogo = (ImageView) findViewById(R.id.news_logo);
        newsDescription = (TextView) findViewById(R.id.news_description);
        newsAuthor = (TextView) findViewById(R.id.news_author);
        newsTime = (TextView) findViewById(R.id.news_time);
        replyCount = (TextView) findViewById(R.id.reply_count);
        newWebView = (WebView)findViewById(R.id.news_web);
        newsContent = (TextView)findViewById(R.id.news_content);
        replyModule = (LinearLayout) findViewById(R.id.reply_module);

        replyBox = (LinearLayout) findViewById(R.id.reply_box);
        replyEditText = (EditText) findViewById(R.id.reply_editText);
        sendButn = (TextView) findViewById(R.id.sendButn);

        //加载更多按钮
        loadingBar = (LinearLayout) findViewById(R.id.loading_bar);
        loadingBarText = (TextView) findViewById(R.id.load_more_text);
        loadingProBar = (ProgressBar) findViewById(R.id.load_more_pro);
        loadingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBarText.setVisibility(View.GONE);
                loadingProBar.setVisibility(View.VISIBLE);
                page++;
                setBoolean(true, false);
                newsApi.setHandler(mHandler);
                message = Message.obtain();
                message.what = 0x640;
                mHandler.sendMessage(message);
            }
        });
        backMenu.setOnClickListener(this);
        newsReply.setOnClickListener(this);
        newsShare.setOnClickListener(this);
        sendButn.setOnClickListener(this);
        //获取资讯信息
        newsApi.getNewsInfo(newsListInfo.getId());
        //发表回复文本框的事件监听器
        replyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    sendButn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    sendButn.setTextColor(Color.WHITE);
                } else {
                    sendButn.setBackgroundResource(R.drawable.border);
                    sendButn.setTextColor(Color.parseColor("#A9ADB0"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    sendButn.setBackgroundResource(R.drawable.forum_enable_btn_send);
                    sendButn.setTextColor(Color.WHITE);
                } else {
                    sendButn.setBackgroundResource(R.drawable.border);
                    sendButn.setTextColor(Color.parseColor("#A9ADB0"));
                }
            }
        });
        newsScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //触摸屏幕隐藏回复界面
                replyBox.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(replyEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                replyEditText.clearFocus();
                return false;
            }
        });
        newWebView.setFocusable(false);
        newWebView.setFocusableInTouchMode(false);
        WebSettings webSettings = newWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//图片自适应大小
        newWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        newWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        //拦截webView的超链接请求
        newWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("url<><><>",url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.back_menu:

                finish();
                break;
            case R.id.news_share:
                Intent intent_Share=new Intent(Intent.ACTION_SEND);
                intent_Share.setType("text/plain");
                intent_Share.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent_Share.putExtra(Intent.EXTRA_TEXT, "【" + newsListInfo.getTitle() + "】" + newsListInfo.getDescription()
                        + Url.USERHEADURL + "news/detail_" + newsListInfo.getId() + ".html" + " （来自OpenSNS手机客户端）");//分享内容体
                startActivity(Intent.createChooser(intent_Share, "分享"));//分享选择页面标题
                break;
            case R.id.news_reply://回复按钮
                initFlag(true,false);
                if (!newsApi.getSeesionId().equals("")) {
                    replyBox.setVisibility(View.VISIBLE);
                    replyEditText.setText("");
                    newsApi.openKeyBoard(mContext, replyEditText);
                }else {
                    ToastHelper.showToast("请登录后操作", mContext);
                }
                break;
            case R.id.sendButn:
                String reply=replyEditText.getText().toString();
                if(reply==null||reply.length()<=0){
                    Toast.makeText(NewsDetailActivity.this,"回复不能为空哟",Toast.LENGTH_SHORT).show();
                    return;
                }
                newsApi.setHandler(handler);
                newsApi.sendReply(newsId,reply);
                break;
            default:
                break;
        }
    }

    private static class MyHandler extends Handler{

        private WeakReference<NewsDetailActivity> mActivityReference;
        private MyJson myJson;
        private ArrayList<NewsReplyInfo> replyInfoList;

        MyHandler(NewsDetailActivity activity) {
            mActivityReference = new WeakReference<NewsDetailActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            NewsDetailActivity activity = mActivityReference.get();
            myJson = new MyJson();
            if (activity != null) {
                switch (msg.what) {
                    case 0x640:
                        activity.newsApi.getNewsReply(activity.newsId,activity.page);
                        activity.setBoolean(true,false);
                        break;
                    case 0:
                        String result = (String) msg.obj;
                        if (!activity.isReply && !activity.isDelReply) {
                            activity.newsDetailInfo = myJson.getNewsInfoList(result);
                            activity.setNewsDetail(activity.newsDetailInfo);
                            activity.message = new Message();
                            activity.message.what = 0x640;
                            sendMessage(activity.message);
                        }else if(activity.isDelReply){
                            activity.replyModule.removeAllViews();
                            activity.page = 1;
                            activity.setBoolean(true, false);
                            activity.message = Message.obtain();
                            activity.message.what = 0x640;
                            sendMessage(activity.message);
                            ToastHelper.showToast("删除成功", activity.mContext);

                        }else {
                            replyInfoList = myJson.getNewsReplyInfo(result);
                            activity.replyCount.setText(replyInfoList.size() + "");
                            if (replyInfoList.size() == 0 && activity.replyModule.getChildCount() == 0){
                                activity.loadingBar.setVisibility(View.GONE);
                                View nullDateView = View.inflate(activity.mContext,R.layout.issue_detail_listview_probar,null);
                                TextView nullTextView = (TextView)nullDateView.findViewById(R.id.issue_reply_load);
                                nullDateView.setTag("isNull");
                                nullTextView.setText("还没有人评论");
                                activity.replyModule.addView(nullDateView);
                            }else if (replyInfoList.size() == 0){
                                activity.loadingBarText.setText("暂无更多评论");
                                activity.loadingBarText.setVisibility(View.VISIBLE);
                                activity.loadingProBar.setVisibility(View.GONE);
                            }else {
                                for (int i =0;i < replyInfoList.size();i++) {
                                    activity.replyModule.addView(activity.addReplyView(replyInfoList.get(i)));
                                }
                                activity.loadingBar.setVisibility(View.VISIBLE);
                                activity.loadingBarText.setVisibility(View.VISIBLE);
                                activity.loadingProBar.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case 800:
                        if (activity.isDelReply){
                            String result1 = (String) msg.obj;
                            ToastHelper.showToast(myJson.getReturnInfo(result1).getMessage(),activity.mContext);
                        }
                        break;
                    case 404:
                        ToastHelper.showToast("网络连接失败",activity.mContext);
                        break;
                        default:
                            break;
                }
            }else{
                Log.d("activity回收了","回收了");
            }
        }
    }

    /**
     *添加评论列表
     */
    public View addReplyView(final NewsReplyInfo replyInfo){
        replyView = View.inflate(mContext, R.layout.news_reply_item, null);
        replyerHead = (CircleImageView) replyView.findViewById(R.id.replyer_head);
        replyAvatar = (TextView) replyView.findViewById(R.id.reply_avatar);
        replyTime = (TextView) replyView.findViewById(R.id.reply_time);
        replyContent = (TextView) replyView.findViewById(R.id.reply_content);
        replyBtn = (TextView) replyView.findViewById(R.id.reply);
        delReBtn = (TextView) replyView.findViewById(R.id.del_reply);

        replyerHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replyInfo.getUser() != null) {
                    newsApi.goUserInfo(mContext, replyInfo.getUser().getUid());
                }
            }
        });
        //回复评论按钮
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newsApi.getSeesionId().equals("")) {
                    replyBox.setVisibility(View.VISIBLE);
                    newsApi.openKeyBoard(mContext, replyEditText);
                    replyEditText.setText("回复@" + replyInfo.getUser().getNickname() + "：");
                    //把光标自动放末尾
                    replyEditText.setSelection(replyEditText.getText().length());
                }else {
                    ToastHelper.showToast("请登录后操作", mContext);
                }
            }
        });
        delReBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newsApi.getSeesionId().equals("")) {
                    if (!newsApi.getSeesionId().equals("")) {
                        newsApi.setHandler(mHandler);
                        setBoolean(false, true);
                        newsApi.exetDelReply(replyInfo.getRow_id(), replyInfo.getId());
                    }
                }else {
                    ToastHelper.showToast("请登录后操作", mContext);
                }
            }
        });
        //控件赋值
        if (replyInfo.getUser() == null){
            if (replyInfo.getArea().length() > 0)
            {
                replyAvatar.setText("游客（" + replyInfo.getArea() + "）");
            }else {
                replyAvatar.setText("游客");
            }
        }else {
            kjBitmap.display(replyerHead, replyInfo.getUser().getAvatar(), 128, 128);
            replyAvatar.setText(replyInfo.getUser().getNickname());
        }
        replyTime.setText(replyInfo.getCreate_time());
        replyContent.setText(replyInfo.getContent());
        return replyView;
    }

    /**
     * 给资讯页面控件赋值
     * @param newsDetailInfo 资讯信息
     */
    public void setNewsDetail(NewsDetailInfo newsDetailInfo){
        newsType.setText(newsDetailInfo.getCategory_title());
        newsTitle.setText(newsDetailInfo.getTitle());
        //加载图片
        LoadImageTask task = new LoadImageTask(newsLogo,540);
        task.execute(newsDetailInfo.getCover());
        taskCollection.add(task);

        newsDescription.setText(Html.fromHtml(newsDetailInfo.getDescription()));
        newsAuthor.setText(newsDetailInfo.getUser().getNickname());
        newsTime.setText(newsDetailInfo.getCreate_time());
        replyCount.setText(newsDetailInfo.getComment());
        String content = newsDetailInfo.getContent();
        if (content.contains("style=")) {
            content = content.replaceAll("style=\"[^\"]+\"","style=\"word-wrap: break-word;word-break: normal\"");
        }
        if (content.contains("<a class=\"popup\" href=")){
            content = content.replaceAll("<a class=\"popup\" href=[^>]+>","");
        }
        if (content.contains("<pre")){
            content = content.replaceAll("<pre","<pre style =\"white-space: pre-wrap;word-wrap: break-word;\"");
        }
        if (content.contains("<-IMG#") &&  newsDetailInfo.getImgList().size() > 0) {
            String regex = "<-IMG#[\\d]++->";
            for (int i = 0; i < newsDetailInfo.getImgList().size(); i++) {
                content = content.replaceFirst(regex, "<img style=\"max-width:100%;height:auto\" src=\"" + newsDetailInfo.getImgList().get(i).getSrc() + "\">");
            }
        }
        newWebView.loadData(content, "text/html; charset=utf-8", "utf-8");
//        Log.e("content<><><><>", content);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" /> " +
                "<style>img{max-width: 100%; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    /**
     * 图片异步下载器
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private String imageUrl;
        private int imageWidth;

        public LoadImageTask(ImageView imageView,int imageWidth) {
            this.imageView = imageView;
            this.imageWidth = imageWidth;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
            if (bitmap == null) {
                bitmap = imageLoader.loadImage(imageUrl, imageWidth);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //图片高比宽长1.1倍时采用把图片放于页面中间的方式放置图片，以免图片拉伸过度
            if (bitmap != null){
                if ( bitmap.getHeight() > 1.1 * bitmap.getWidth()||bitmap.getHeight()<270) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                }
            }
            imageView.setImageBitmap(bitmap);
            if (!newsScroll.isShown()) {
                newsScroll.setVisibility(View.VISIBLE);
                proBarLine.setVisibility(View.GONE);
            }
            taskCollection.remove(this);
        }
    }

    /**
     * @param isReply   获取评论数据开关
     * @param isDelReply    删除评论开关
     */
    public void setBoolean(boolean isReply,boolean isDelReply){
        this.isReply = isReply;
        this.isDelReply = isDelReply;
    }

    @Override
    public void finish() {
        super.finish();
        //解决Activity退出动画无效的问题
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    /**
     * 解决Activity退出动画无效的问题
     */
    public void exitAnim(){
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[] {android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
        activityStyle.recycle();
    }

    public void initFlag(boolean reply,boolean replys){
        REPLY=reply;
        REPLYS=replys;
    }
}
