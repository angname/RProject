package com.thinksky.tox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.adapter.DetailListAdapter;
import com.thinksky.anim3d.RoundBitmap;
import com.thinksky.info.Com2Com;
import com.thinksky.info.WeiboCommentInfo;
import com.thinksky.info.WeiboInfo;
import com.thinksky.myview.MyDetailsListView;
import com.thinksky.myview.SwipeLayout;
import com.thinksky.redefine.FaceTextView;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;
import com.thinksky.utils.LoadImg.ImageDownloadCallBack;
import com.thinksky.utils.MyJson;
import com.tox.BaseFunction;
import com.tox.ToastHelper;
import com.tox.TouchHelper;
import com.tox.Url;
import com.tox.WeiboApi;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

public class WeiboDetailActivity extends Activity {

    private WeiboInfo Weiboinfo = null;
    private LoadImg loadImg;
    private MyJson myJson = new MyJson();
    private RelativeLayout Detail_Back;
    private ImageView mDetail_UserHead, mDetail_SenComment;
    private LinearLayout mDetail_Up, mDetail_Share, mDetail__progressBar, mDetail_SendComment, mDetail_repostWeibo,mDeleteButton;
    private ProgressBar mComment_ProgressBar;
    private ImageView mDetail_Up_Img;
    private TextView mDetail_Up_text, mDetail_ShareNum, mDetail_CommentsNum, mDetail_repostTime, mDetail_repostName, mDetail_repostContent,
            mDetail_AshameID, mDetail_UserName, mDetail_Ctime, mWeiboTop, mWeiboFrom;
    private FaceTextView mDetail_MainText;
    private MyDetailsListView Detail_List;
    private List<Com2Com> mlist = new ArrayList<Com2Com>();
    private List<WeiboCommentInfo> commentInfoList = new ArrayList<WeiboCommentInfo>();
    private DetailListAdapter mAdapter = null;
    private Button ListBottem = null;
    private int mStart = 1;
    private boolean flag = true;
    private boolean upFlag = false;
    private boolean listBottemFlag = true;
    private RoundBitmap roundBitmap = new RoundBitmap();
    private WeiboApi weiboApi = new WeiboApi();
    private List<ImageView> mImgList = new ArrayList<ImageView>();
    private FinalBitmap finalBitmap;
    private SwipeLayout mSwipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weibo_detail);
        Intent intent = getIntent();
        Bundle bund = intent.getBundleExtra("value");
        Weiboinfo = (WeiboInfo) bund.getSerializable("WeiboInfo");
        Log.e("微博内容", Weiboinfo.getWcontent());
        loadImg = new LoadImg(WeiboDetailActivity.this);
        finalBitmap = FinalBitmap.create(this);
        finalBitmap.configBitmapLoadThreadSize(5);
        finalBitmap.configMemoryCacheSize(5);
        initView();
        addInformation();
        addImg();
        mAdapter = new DetailListAdapter(WeiboDetailActivity.this, commentInfoList, Weiboinfo);
        ListBottem = new Button(WeiboDetailActivity.this);
        ListBottem.setText("点击加载更多");
        ListBottem.setBackgroundColor(Color.parseColor("#ffffff"));
        ListBottem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag && listBottemFlag) {
                    weiboApi.setHandler(hand);
                    weiboApi.getWeiboComment(Weiboinfo.getWid(), mStart + "");
                    listBottemFlag = false;
                } else if (!listBottemFlag)
                    Toast.makeText(WeiboDetailActivity.this, "正在加载中...", Toast.LENGTH_LONG)
                            .show();
            }
        });
        Detail_List.addFooterView(ListBottem, null, false);
        ListBottem.setVisibility(View.GONE);
        Detail_List.setAdapter(mAdapter);
        /*String endParames = Url.COMMENTS + "?weibo_id=" + info.getWid() + "&page="
				+ mStart;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));*/
        weiboApi.setHandler(hand);
        weiboApi.getWeiboDetail(Weiboinfo.getWid());
        if (Integer.parseInt(Weiboinfo.getComment_count()) != 0) {

        } else {
            //mDetail__progressBar.setVisibility(View.GONE);
            mComment_ProgressBar = (ProgressBar) findViewById(R.id.Comment_ProgressBar);
            mComment_ProgressBar.setVisibility(View.GONE);
            TextView show = (TextView) findViewById(R.id.TextShowNoComment);
            show.setText("该微博暂无评论！！");
//            show.setHeight(40);
        }
    }

    private void initView() {
        MyOnClickListner myOnclick = new MyOnClickListner();
        mDetail_repostContent = (TextView) findViewById(R.id.Detail_Repost_content);
        mDetail_repostName = (TextView) findViewById(R.id.Detail_Repost_name);
        mDetail_repostTime = (TextView) findViewById(R.id.Detail_Repost_time);
        Detail_Back = (RelativeLayout) findViewById(R.id.Detail_Back);
        mDetail_SenComment = (ImageView) findViewById(R.id.Detail_SenComment);
        mDetail_SendComment = (LinearLayout) findViewById(R.id.Detail_SendComment);
        mDetail_AshameID = (TextView) findViewById(R.id.Detail_AshameID);
        mDetail_UserHead = (ImageView) findViewById(R.id.Detail_UserHead);
        mDetail_UserName = (TextView) findViewById(R.id.Detail_UserName);
        mDetail_MainText = (FaceTextView) findViewById(R.id.Detail_MainText);
        mDetail_Up = (LinearLayout) findViewById(R.id.Detail_Up);
        mDetail_Up_Img = (ImageView) findViewById(R.id.Detail_Up_Img);
        mDetail_Up_text = (TextView) findViewById(R.id.Detail_Up_text);
        mDetail_ShareNum = (TextView) findViewById(R.id.Detail_ShareNum);
        mWeiboFrom = (TextView) findViewById(R.id.WeiboDetail_from);

        mSwipeLayout = (SwipeLayout) findViewById(R.id.swipeLayout);
        mDetail_Share = (LinearLayout) findViewById(R.id.Detail_Share);
        mDetail_repostWeibo = (LinearLayout) findViewById(R.id.Detail_RepostWeibo);
        mDeleteButton = (LinearLayout) findViewById(R.id.delete_button);
        mWeiboTop = (TextView) findViewById(R.id.Weibo_top);
        Detail_List = (MyDetailsListView) findViewById(R.id.Detail_List);
        mDetail__progressBar = (LinearLayout) findViewById(R.id.Detail__progressBar);
        mDetail_CommentsNum = (TextView) findViewById(R.id.Detail_ComNum);
        mDetail_Ctime = (TextView) findViewById(R.id.Detail_ctime);
        Detail_Back.setOnClickListener(myOnclick);
        mDetail_SenComment.setOnClickListener(myOnclick);
        mDetail_UserHead.setOnClickListener(myOnclick);
        mDetail_Up.setOnClickListener(myOnclick);
        mDetail_SendComment.setOnClickListener(myOnclick);
        mDetail_Share.setOnClickListener(myOnclick);
        mDeleteButton.setOnClickListener(myOnclick);

        //初始化图片
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg1));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg2));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg3));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg4));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg5));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg6));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg7));
        mImgList.add((ImageView) findViewById(R.id.Detail_MainImg8));
        for (ImageView view : mImgList) {
            view.setOnClickListener(new MyOnClickListner());
        }
        if (Weiboinfo.getIs_top() != 1) {
            mWeiboTop.setVisibility(View.GONE);
        }
        mDetail_Up.setOnTouchListener(new TouchHelper(WeiboDetailActivity.this, "#ededed", "#ffffff", "color"));
        mDetail_Share.setOnTouchListener(new TouchHelper(WeiboDetailActivity.this, "#ededed", "#ffffff", "color"));
        mDetail_SendComment.setOnTouchListener(new TouchHelper(WeiboDetailActivity.this, "#ededed", "#ffffff", "color"));

    }


    private class MyOnClickListner implements View.OnClickListener {
        public void onClick(View arg0) {
            int mID = arg0.getId();
            switch (mID) {
                case R.id.Detail_Back:
                    WeiboDetailActivity.this.finish();
                    break;
                case R.id.Detail_SenComment:
                    if (!Url.SESSIONID.equals("")) {
                        Intent intent = new Intent(WeiboDetailActivity.this,
                                SendCommentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("weiboInfo", Weiboinfo);
                        intent.putExtra("weiboInfo", bundle);
                        Url.activityFrom="weiboDetail";
                        intent.putExtra("commentType", 1);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(WeiboDetailActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.Detail_SendComment:
                    if (!Url.SESSIONID.equals("")) {
                        Intent intent = new Intent(WeiboDetailActivity.this,
                                SendCommentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("weiboInfo", Weiboinfo);
                        intent.putExtra("weiboInfo", bundle);
                        Url.activityFrom="weiboDetail";
                        intent.putExtra("commentType", 1);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(WeiboDetailActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.Detail_Up:
                    if (BaseFunction.isLogin()) {
                        if (!upFlag) {
                            upFlag = true;
                            mDetail_Up_text.setText(Integer.parseInt(Weiboinfo.getLikenum()) + 1 + "");
                            mDetail_Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, WeiboDetailActivity.this));
                            support();
                        } else {
                            ToastHelper.showToast("点赞失败，重复点赞", WeiboDetailActivity.this);
                        }


                    } else {
                        ToastHelper.showToast("请先登录", WeiboDetailActivity.this);
                    }

                    break;
                case R.id.Detail_Share:
                    if (BaseFunction.isLogin()) {
                        Intent intent = new Intent(WeiboDetailActivity.this, UploadActivity.class);
                        intent.putExtra("weiboType", "repost");
                        if (Weiboinfo.getType().equals("repost")) {
                            intent.putExtra("weibo_master", Weiboinfo.getUser().getNickname());
                            intent.putExtra("weibo_content", Weiboinfo.getWcontent());
                            intent.putExtra("weibo_id", Weiboinfo.getWid());
                            intent.putExtra("source_id", Weiboinfo.getRepostWeiboInfo().getWid());
                        } else {
                            intent.putExtra("weibo_id", Weiboinfo.getWid());
                            intent.putExtra("source_id", Weiboinfo.getWid());
                        }
                        startActivity(intent);
                    } else {
                        ToastHelper.showToast("请先登录", WeiboDetailActivity.this);
                    }
                    break;
                //删除微博
                case R.id.delete_button:
                    if (Weiboinfo.getCan_delete()){
                        weiboApi.setHandler(new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case 0:
                                        Url.activityFrom = "DeleteWeiBoActivity";
                                        ToastHelper.showToast("删除成功",WeiboDetailActivity.this);
                                        finish();
                                        break;
                                    case 401:
                                        ToastHelper.showToast("操作失败，需要登录",WeiboDetailActivity.this);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        new AlertDialog.Builder(WeiboDetailActivity.this)
                                .setMessage("确定删除微博？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        weiboApi.deleteWeiBo(Weiboinfo.getWid());
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }else {
                        mDeleteButton.setClickable(false);
                        ToastHelper.showToast("没有权限操作",WeiboDetailActivity.this);
                    }
                    break;
                case R.id.Detail_MainImg:
                    startPhotoBrowser(0);
                    break;
                case R.id.Detail_MainImg1:
                    startPhotoBrowser(1);
                    break;
                case R.id.Detail_MainImg2:
                    startPhotoBrowser(2);
                    break;
                case R.id.Detail_MainImg3:
                    startPhotoBrowser(3);
                    break;
                case R.id.Detail_MainImg4:
                    startPhotoBrowser(4);
                    break;
                case R.id.Detail_MainImg5:
                    startPhotoBrowser(5);
                    break;
                case R.id.Detail_MainImg6:
                    startPhotoBrowser(6);
                    break;
                case R.id.Detail_MainImg7:
                    startPhotoBrowser(7);
                    break;
                case R.id.Detail_MainImg8:
                    startPhotoBrowser(8);
                    break;
                case R.id.Detail_UserHead:
                    weiboApi.goUserInfo(WeiboDetailActivity.this,Weiboinfo.getUser().getUid());
                    break;
                default:
                    break;
            }
        }

        private void support() {
            WeiboApi weiboApi1 = new WeiboApi();
            weiboApi1.setHandler(supportHand);
            weiboApi1.supportWeibo(Weiboinfo.getWid());
        }
    }

    Handler supportHand = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ToastHelper.showToast("点赞成功", WeiboDetailActivity.this);
            } else if (msg.what == 501) {
                ToastHelper.showToast("点赞失败，重复点赞", WeiboDetailActivity.this);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(WeiboDetailActivity.this, "请求失败，服务器故障", Toast.LENGTH_LONG)
                        .show();
                listBottemFlag = true;
            } else if (msg.what == 100) {
                Toast.makeText(WeiboDetailActivity.this, "服务器无响应", Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 0) {
                String result = (String) msg.obj;
                if (result != null) {
                    List<WeiboCommentInfo> newList = myJson
                            .getWeiboCommentsList(result);
                    Log.e(">>>>>>>>>>", newList.size() + "");
                    if (newList != null) {
                        if (newList.size() == 10) {
                            Detail_List.setVisibility(View.VISIBLE);
                            ListBottem.setVisibility(View.VISIBLE);
                            mDetail__progressBar.setVisibility(View.GONE);
                            mStart += 1;
                        } else if (newList.size() == 0) {

                            if (mlist.size() == 0) {
                                mDetail__progressBar.setVisibility(View.VISIBLE);
                            } else {
                                mDetail__progressBar.setVisibility(View.GONE);
                            }
                            ListBottem.setVisibility(View.GONE);
                            Toast.makeText(WeiboDetailActivity.this,
                                    "没有评论了额...", Toast.LENGTH_LONG).show();
                        } else {
                            mDetail__progressBar.setVisibility(View.GONE);
                            Detail_List.setVisibility(View.VISIBLE);
                            ListBottem.setVisibility(View.GONE);
                        }
                        for (WeiboCommentInfo info : newList) {
                            commentInfoList.add(info);
                        }
                        listBottemFlag = true;
                    } else {
                        //mDetail_CommentsNum.setVisibility(View.VISIBLE);
                        mDetail__progressBar.setVisibility(View.VISIBLE);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }

        ;

    };

    @Override
    protected void onResume() {
        super.onResume();
        //评论完后判断是否插入评论列表
        insertComAfterCom();
    }

    private void insertComAfterCom(){
        if(Url.is2InsertWeiboCom){
            commentInfoList.add(null);
            if(commentInfoList.size()==1){
                Detail_List.setVisibility(View.VISIBLE);
                ListBottem.setVisibility(View.GONE);
                mDetail__progressBar.setVisibility(View.GONE);
                commentInfoList.set(0,Url.weiboCommentInfo);
            }else{

                for(int i=commentInfoList.size()-1;i>0;i--){
                    commentInfoList.set(i,commentInfoList.get(i-1));
                }
                commentInfoList.set(0,Url.weiboCommentInfo);
            }
            Url.is2InsertWeiboCom=false;

            mAdapter.notifyDataSetChanged();
        }
    }

    private void addImg() {
        Bitmap bit = null;
        if (Weiboinfo.getUser().getAvatar().equals("null")) {
            Log.e("不加载图片", "111111111111111111111");
            mDetail_UserHead.setImageResource(R.drawable.side_user_avatar);
        } else {
            BaseFunction.showImage(WeiboDetailActivity.this, mDetail_UserHead, Weiboinfo.getUser().getAvatar(), loadImg, Url.IMGTYPE_HEAD);
        }
        //加载图片
        //Log.e("加载图片",Weiboinfo.getImgList().size()+"");
        if (Weiboinfo.getImgList().size() != 0) {
            mDetail_repostWeibo.setVisibility(View.VISIBLE);
            int imageCount = Weiboinfo.getImgList().size();
            if (imageCount > 9){
                imageCount = 9;
            }
            for (int i = 0; i < imageCount; i++) {
                BaseFunction.showImage(WeiboDetailActivity.this, mImgList.get(i),Weiboinfo.getImgList().get(i), loadImg, Url.IMGTYPE_WEIBO);
                mImgList.get(i).setVisibility(View.VISIBLE);
                // loadWeiboImg(mImgList.get(i),Url.USERHEADURL+"/"+Weiboinfo.getImgList().get(i));
            }
        } else {

        }
    }

    private void loadWeiboImg(final ImageView weiboImgView, String url) {
        weiboImgView.setTag(url);
        final Bitmap imgBitmap = loadImg.loadImage(weiboImgView, url, new ImageDownloadCallBack() {
            @Override
            public void onImageDownload(ImageView imageView, Bitmap bitmap) {
                weiboImgView.setImageBitmap(bitmap);
                weiboImgView.setVisibility(View.VISIBLE);
            }
        });
        if (imgBitmap != null) {
            weiboImgView.setImageBitmap(imgBitmap);
            weiboImgView.setVisibility(View.VISIBLE);
        }
    }

    //初始化微博的详情页的基本信息
    private void addInformation() {
        //mDetail_AshameID.setText("微博ID" + Weiboinfo.getWid());
        mDetail_UserName.setText(Weiboinfo.getUser().getNickname());
        mDetail_MainText.setFaceText(Weiboinfo.getWcontent());
        mDetail_CommentsNum.setText(Weiboinfo.getComment_count());
        mDetail_Up_text.setText(Weiboinfo.getLikenum());
        if (Weiboinfo.getIs_supported()) {
            mDetail_Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, WeiboDetailActivity.this));
            upFlag = true;
        }
        if (Weiboinfo.getFrom().equals("")) {
            mWeiboFrom.setText("来自:网站端");
        } else {
            mWeiboFrom.setText("来自:" + Weiboinfo.getFrom());
        }

        mDetail_ShareNum.setText(Weiboinfo.getRepost_count());
        mDetail_Ctime.setText(Weiboinfo.getCtime());
        if (Weiboinfo.getType().equals("repost")) {
            Log.d("123545",Weiboinfo.getRepostWeiboInfo().getUser()+"");
            if (Weiboinfo.getRepostWeiboInfo().getUser()==null){
                mDetail_repostWeibo.setVisibility(View.VISIBLE);
                mDetail_repostWeibo.setBackgroundColor(Color.parseColor("#F7F7F7"));
                mDetail_repostName.setText(" ");
                mDetail_repostName.setVisibility(View.VISIBLE);
                mDetail_repostName.setTextColor(getResources().getColor(R.color.repostName));
                mDetail_repostContent.setText(""+"   原微博已删除");
                mDetail_repostContent.setVisibility(View.VISIBLE);
                mDetail_repostTime.setText("");
                mDetail_repostTime.setVisibility(View.VISIBLE);
            }else {
                mDetail_repostWeibo.setVisibility(View.VISIBLE);
                mDetail_repostWeibo.setBackgroundColor(Color.parseColor("#F7F7F7"));
                mDetail_repostName.setText("@" + Weiboinfo.getRepostWeiboInfo().getUser().getNickname() + ":");
                mDetail_repostName.setVisibility(View.VISIBLE);
                mDetail_repostName.setTextColor(getResources().getColor(R.color.repostName));
                mDetail_repostContent.setText(Weiboinfo.getRepostWeiboInfo().getWcontent());
                mDetail_repostContent.setVisibility(View.VISIBLE);
                mDetail_repostTime.setText(Weiboinfo.getRepostWeiboInfo().getCtime());
                mDetail_repostTime.setVisibility(View.VISIBLE);
                if (Weiboinfo.getRepostWeiboInfo().getType().equals("image")) {
                    for (int i = 0; i < Weiboinfo.getRepostWeiboInfo().getImgList().size(); i++) {
                        BaseFunction.showImage(WeiboDetailActivity.this, mImgList.get(i), Weiboinfo.getRepostWeiboInfo().getImgList().get(i), loadImg, Url.IMGTYPE_WEIBO);
                    }
                } else {

                }
            }

        }

    }

    private void startPhotoBrowser(int index) {
        if (Weiboinfo.getType().equalsIgnoreCase("image")) {
            List list=new ArrayList();
            Intent intent = new Intent(WeiboDetailActivity.this, ImagePagerActivity.class);
            Bundle bundle = new Bundle();
            for(int i=0;i<Weiboinfo.getImgList().size();i++){
                String str=Weiboinfo.getImgList().get(i);
                list.add(str.substring(0, str.lastIndexOf("/") - 8));
            }
            bundle.putStringArrayList("image_urls", (ArrayList<String>) list);
//            bundle.putStringArrayList("image_urls", (ArrayList<String>) Weiboinfo.getImgList());

            bundle.putInt("image_index", index);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (Weiboinfo.getType().equalsIgnoreCase("repost")) {
            Intent intent = new Intent(WeiboDetailActivity.this, ImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("image_urls", (ArrayList<String>) Weiboinfo.getRepostWeiboInfo().getImgList());
            bundle.putInt("image_index", index);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
