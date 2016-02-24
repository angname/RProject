package com.thinksky.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.info.WeiboInfo;
import com.thinksky.redefine.FaceTextView;
import com.thinksky.tox.ImagePagerActivity;
import com.thinksky.tox.R;
import com.thinksky.tox.SendCommentActivity;
import com.thinksky.tox.UploadActivity;
import com.thinksky.tox.WeiboDetailActivity;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;
import com.tox.BaseFunction;
import com.tox.ToastHelper;
import com.tox.TouchHelper;
import com.tox.Url;
import com.tox.WeiboApi;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.List;

public class WeiboAdapter extends BaseAdapter {

    private List<WeiboInfo> list;
    private Context ctx;
    private LoadImg loadImgHeadImg;
    private LoadImg loadImgMainImg;
    private boolean upFlag = false;
    private boolean downFlag = false;
    private WeiboApi weiboApi;


    private FinalBitmap finalBitmap;
    private KJBitmap kjBitmap;

    public WeiboAdapter(Context ctx, List<WeiboInfo> list, FinalBitmap finalBitmap, KJBitmap kjBitmap) {
        this.list = list;
        this.ctx = ctx;
        this.finalBitmap = finalBitmap;
        this.kjBitmap = kjBitmap;
        loadImgHeadImg = new LoadImg(ctx);
        loadImgMainImg = new LoadImg(ctx);
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        final Holder hold;
        weiboApi = new WeiboApi();
        final int number = arg0;
        if (arg1 == null) {
            System.out.println("为空：" + arg0);
            hold = new Holder();
            arg1 = View.inflate(ctx, R.layout.mylistview_item, null);
            hold.UserHead = (ImageView) arg1.findViewById(R.id.Item_UserHead);
            hold.UserName = (TextView) arg1.findViewById(R.id.Item_UserName);
            hold.MainText = (FaceTextView) arg1.findViewById(R.id.Item_MainText);
            hold.MainImg = (ImageView) arg1.findViewById(R.id.Item_MainImg);
            hold.MainImg1 = (ImageView) arg1.findViewById(R.id.Item_MainImg1);
            hold.MainImg2 = (ImageView) arg1.findViewById(R.id.Item_MainImg2);
            hold.imgViewList.add(hold.MainImg);
            hold.imgViewList.add(hold.MainImg1);
            hold.imgViewList.add(hold.MainImg2);
            hold.Up = (LinearLayout) arg1.findViewById(R.id.Item_Up);
            hold.Up_Img = (ImageView) arg1.findViewById(R.id.Item_Up_img);
            hold.Up_text = (TextView) arg1.findViewById(R.id.Like_text);
            hold.repostLinerLayout = (LinearLayout) arg1.findViewById(R.id.RepostWeibo);
            hold.repostText = (FaceTextView) arg1.findViewById(R.id.Repost_content);
            hold.repostName = (TextView) arg1.findViewById(R.id.Repost_name);
            hold.repostWeiboTime = (TextView) arg1.findViewById(R.id.Repost_time);
            hold.CommentNum = (TextView) arg1.findViewById(R.id.Item_CommentNum);
            hold.WeiboTop = (TextView) arg1.findViewById(R.id.Weibo_top);
            hold.ShareNum = (TextView) arg1.findViewById(R.id.Repost_Text);
            hold.Ctime = (TextView) arg1.findViewById(R.id.Item_Ctime);
            hold.Weibo_From = (TextView) arg1.findViewById(R.id.Weibo_from);
            hold.sendRepost = (LinearLayout) arg1.findViewById(R.id.SendRepostWeibo);
            hold.enterDetail = (LinearLayout) arg1.findViewById(R.id.enterDetail);
            hold.sendComment = (LinearLayout) arg1.findViewById(R.id.ListView_itemComment);
            hold.WeiboTop.setTag(list.get(arg0).getWid());
            hold.Up_Img.setTag(list.get(arg0).getWid());
            //默认设置不显示图片
            hold.imgViewList.get(0).setVisibility(View.GONE);
            hold.imgViewList.get(1).setVisibility(View.GONE);
            hold.imgViewList.get(2).setVisibility(View.GONE);
            hold.UserHead.setTag(list.get(arg0).getUser().getAvatar());
            hold.Up.setOnTouchListener(new TouchHelper(ctx, "#ededed", "#ffffff", "color"));
            hold.sendRepost.setOnTouchListener(new TouchHelper(ctx, "#ededed", "#ffffff", "color"));
            hold.sendComment.setOnTouchListener(new TouchHelper(ctx, "#ededed", "#ffffff", "color"));
            //hold.Up.setOnTouchListener(new TouchHelper(ctx,R.drawable.button_vote_active+"",R.drawable.button_vote_enable+"","image"));
            arg1.setTag(hold);
        } else {
            System.out.println("不为空：" + arg0);
            hold = (Holder) arg1.getTag();
            if (!list.get(arg0).getIs_supported()) {
                hold.upFlag = false;
            }
            //默认设置不显示图片
            hold.imgViewList.get(0).setVisibility(View.GONE);
            hold.imgViewList.get(1).setVisibility(View.GONE);
            hold.imgViewList.get(2).setVisibility(View.GONE);
        }

        hold.UserName.setText(list.get(arg0).getUser().getNickname());
        hold.MainText.setFaceText(list.get(arg0).getWcontent());
//        hold.LikeNum.setText(list.get(arg0).getQlike());
//        hold.Down_text.setText("-" + list.get(arg0).getQunlike());
        hold.CommentNum.setText(list.get(arg0).getRepost_count());
        hold.Ctime.setText(list.get(arg0).getCtime());
        hold.CommentNum.setText(list.get(arg0).getComment_count());
        hold.ShareNum.setText(list.get(arg0).getRepost_count());
        hold.Up_text.setText(list.get(arg0).getLikenum());
        if (list.get(arg0).getFrom().equals("")) {
            hold.Weibo_From.setText("来自:网站端");
        } else {
            hold.Weibo_From.setText("来自:" + list.get(arg0).getFrom());
        }
        if (hold.Up_Img.getTag().equals(list.get(arg0).getWid())) {
            if (list.get(arg0).getIs_supported()) {
                hold.Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, ctx));
                hold.upFlag = true;
            }
        } else {
            if (list.get(arg0).getIs_supported()) {
                hold.Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, ctx));
                hold.upFlag = true;
            } else {
                hold.Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart_grary, ctx));
                hold.upFlag = false;
            }
        }
        //hold.WeiboTop.setTag(list.get(arg0).getWid());
        if (hold.WeiboTop.getTag().equals(list.get(arg0).getWid())) {
            if (list.get(arg0).getIs_top() == 1) {
                hold.WeiboTop.setVisibility(View.VISIBLE);
            }
        } else {
            if (list.get(arg0).getIs_top() == 1) {
                hold.WeiboTop.setVisibility(View.VISIBLE);
            } else {
                hold.WeiboTop.setVisibility(View.GONE);
            }
        }

        //Log.e("ID", arg0 + "" + list.get(arg0).getType() + list.get(arg0).getWid());
        String weibotype = list.get(arg0).getType();
        if (weibotype.equalsIgnoreCase("repost")) {
            WeiboInfo repostWeibo = list.get(arg0).getRepostWeiboInfo();
            Log.d("12354", repostWeibo.getUser() + "");
            if (repostWeibo.getUser()==null){
                hold.repostName.setText("");
                hold.repostName.setTextColor(ctx.getResources().getColor(R.color.repostName));
                hold.repostWeiboTime.setText(".");
                hold.repostText.setFaceText("      原微博已删除");
                hold.repostLinerLayout.setVisibility(View.VISIBLE);
                hold.repostText.setVisibility(View.VISIBLE);
                hold.repostWeiboTime.setVisibility(View.VISIBLE);
                hold.repostName.setVisibility(View.VISIBLE);
            }else {
                hold.repostLinerLayout.setBackgroundColor(Color.parseColor("#F7F7F7"));
                hold.repostName.setText("@" + "" + repostWeibo.getUser().getNickname() + "：");
                hold.repostName.setTextColor(ctx.getResources().getColor(R.color.repostName));
                hold.repostWeiboTime.setText(repostWeibo.getCtime());
                hold.repostText.setFaceText(repostWeibo.getWcontent());
                hold.repostLinerLayout.setVisibility(View.VISIBLE);
                hold.repostText.setVisibility(View.VISIBLE);
                hold.repostWeiboTime.setVisibility(View.VISIBLE);
                hold.repostName.setVisibility(View.VISIBLE);

                String repostWeiboType = repostWeibo.getType();
                if (repostWeiboType != null) {
                    if (repostWeiboType.equalsIgnoreCase("image")) {
                        List<String> imglist = repostWeibo.getImgList();
                        if (imglist.size() >= 3) {
                            //setImgRec(hold,imglist,3);
                            hold.repostLinerLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < 3; i++) {
                                setWeiboImgVisiable(hold, 3);
                                hold.imgViewList.get(i).setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no, ctx));
                                loadWeiboImg(hold, hold.imgViewList.get(i), imglist.get(i), finalBitmap, kjBitmap);
                                Log.d("img>><><><>:", imglist.get(i));
                            }
                        } else {
                            // setImgRec(hold,imglist,imglist.size());
                            setWeiboImgVisiable(hold, imglist.size());
                            for (int i = 0; i < imglist.size(); i++) {
                                hold.imgViewList.get(i).setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no, ctx));
                                loadWeiboImg(hold, hold.imgViewList.get(i), imglist.get(i), finalBitmap, kjBitmap);
                                Log.d("img>><><><>:", imglist.get(i));
                            }
                        }
                    }
                }
            }
        } else if (weibotype.equalsIgnoreCase("image")) {
            hold.repostLinerLayout.setVisibility(View.VISIBLE);
            hold.repostLinerLayout.setBackgroundColor(Color.parseColor("#FDFDFD"));
            hold.repostText.setVisibility(View.GONE);
            hold.repostWeiboTime.setVisibility(View.GONE);
            hold.repostName.setVisibility(View.GONE);
            List<String> imglist = list.get(arg0).getImgList();
            if (imglist.size() >= 3) {
                //setImgRec(hold,imglist,3);
                setWeiboImgVisiable(hold, 3);
                for (int i = 0; i < 3; i++) {
                    hold.imgViewList.get(i).setVisibility(View.VISIBLE);
                    hold.imgViewList.get(i).setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no, ctx));
                    loadWeiboImg(hold, hold.imgViewList.get(i), imglist.get(i), finalBitmap, kjBitmap);
                    Log.d("img>><><><>:", imglist.get(i));
                }
            } else {
                // setImgRec(hold,imglist,imglist.size());
                setWeiboImgVisiable(hold, imglist.size());
                for (int i = 0; i < imglist.size(); i++) {
                    hold.imgViewList.get(i).setVisibility(View.VISIBLE);
                    hold.imgViewList.get(i).setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no, ctx));
                    loadWeiboImg(hold, hold.imgViewList.get(i), imglist.get(i), finalBitmap, kjBitmap);
                    Log.d("img>><><><>:", imglist.get(i));
                }
            }
        } else {
            hold.repostLinerLayout.setVisibility(View.GONE);
        }

        hold.sendRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Url.SESSIONID.equalsIgnoreCase("")) {
                    Intent intent = new Intent(ctx, UploadActivity.class);
                    Log.e("转发微博被点击啦啦啦啦啦", arg0+"");
                    intent.putExtra("weiboType", "repost");
                    if (list.get(number).getType().equals("repost")) {
                        if (list.get(number).getUser().equals("null")){
                            intent.putExtra("weibo_master", "");
                            intent.putExtra("weibo_content", "");
                            intent.putExtra("weibo_id", "");
                            intent.putExtra("source_id", "");
                        }else {
                            intent.putExtra("weibo_master", list.get(number).getUser().getNickname());
                            intent.putExtra("weibo_content", list.get(number).getWcontent());
                            intent.putExtra("weibo_id", list.get(number).getWid());
                            intent.putExtra("source_id", list.get(number).getRepostWeiboInfo().getWid());
                        }
                    } else {
                        intent.putExtra("weibo_id", list.get(number).getWid());
                        intent.putExtra("source_id", list.get(number).getWid());
                    }
                    ctx.startActivity(intent);

                } else {
                    Toast.makeText(ctx, "请先登入！", Toast.LENGTH_LONG).show();
                }

            }
        });
        hold.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseFunction.isLogin()) {
                    Intent intent = new Intent(ctx,
                            SendCommentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("weiboInfo", list.get(arg0));
                    intent.putExtra("weiboInfo", bundle);
                    intent.putExtra("commentType", 1);
                    ctx.startActivity(intent);
                } else {
                    ToastHelper.showToast("请先登录", ctx);
                }
            }
        });

        // 设置监听
        hold.Up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (BaseFunction.isLogin()) {
                    WeiboApi weiboApi = new WeiboApi();
                    weiboApi.setHandler(handler);
                    if (!hold.upFlag) {
                        hold.upFlag = true;
                        hold.Up_text.setText(Integer.parseInt(list.get(arg0).getLikenum()) + 1 + "");
                        hold.Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, ctx));
                        list.get(arg0).setIs_supported(true);
                        list.get(arg0).setLikenum(Integer.parseInt(list.get(arg0).getLikenum()) + 1 + "");
                        weiboApi.supportWeibo(list.get(arg0).getWid());
                    } else {
                        ToastHelper.showToast("点赞失败，重复点赞", ctx);
                    }
                } else {
                    Toast.makeText(ctx, "请先登陆", Toast.LENGTH_LONG).show();
                }
            }
        });

        hold.enterDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, WeiboDetailActivity.class);
                Bundle bund = new Bundle();
                Log.e("被点击啦啦啦啦啦", arg0+"");
                bund.putSerializable("WeiboInfo", list.get(arg0));
                intent.putExtra("value", bund);
                ctx.startActivity(intent);
            }
        });
        hold.MainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, WeiboDetailActivity.class);
                Bundle bund = new Bundle();
                Log.e("被点击啦啦啦啦啦", arg0+"");
                bund.putSerializable("WeiboInfo", list.get(arg0));
                intent.putExtra("value", bund);
                ctx.startActivity(intent);
            }
        });


        hold.UserHead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                weiboApi.goUserInfo(ctx, list.get(number).getUser().getUid());
            }
        });
        hold.MainImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startPhotoBrowser(number, 0);
            }
        });

        hold.MainImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhotoBrowser(number, 1);
            }
        });
        hold.MainImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhotoBrowser(number, 2);
            }
        });


        hold.UserHead.setImageResource(R.drawable.side_user_avatar);
        if (BaseFunction.fileExists(list.get(arg0).getUser().getAvatar())) {
            hold.UserHead.setTag(list.get(arg0).getUser().getAvatar());
            hold.UserHead.setImageBitmap(BitmapUtiles.localImgToBitmap(list.get(arg0).getUser().getAvatar(), 1));
        } else {
            BaseFunction.showImage(ctx, hold.UserHead, list.get(arg0).getUser().getAvatar(), loadImgHeadImg, Url.IMGTYPE_HEAD);
        }


        return arg1;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(ctx, "点赞成功", Toast.LENGTH_LONG).show();

            } else if (msg.what == 501) {
                Toast.makeText(ctx, "点赞失败，重复点赞", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void setWeiboImgVisiable(Holder holder, int num) {
        for (int i = 0; i < num; i++) {
            // holder.imgViewList.get(i).setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no,ctx));
            holder.imgViewList.get(i).setVisibility(View.VISIBLE);

        }
    }

    public void loadWeiboImg(Holder hold, final ImageView weiboImg, String url, FinalBitmap finalBitmap, KJBitmap kjBitmap) {
        String url2;
        if(url.startsWith("http")){
            url2=url;
        }else{
//            return (Url.USERHEADURL+"/"+url).replace("com///","com/").replace("com//","com/").replace("cn///","cn/").replace("cn//","cn/").replace("net///","net/").replace("net//","net/");
            url2= Url.USERHEADURL+"/"+url;
        }
        BaseFunction.showImage(ctx, weiboImg, url2, loadImgMainImg, Url.IMGTYPE_WEIBO);
    }

    private void startPhotoBrowser(int arg0, int index) {
        WeiboInfo weiboInfo = list.get(arg0);
        List list=new ArrayList();
        if (weiboInfo.getType().equalsIgnoreCase("image")) {
            Intent intent = new Intent(ctx, ImagePagerActivity.class);
            Bundle bundle = new Bundle();
            for(int i=0;i<weiboInfo.getImgList().size();i++){
                String str=weiboInfo.getImgList().get(i);
                if (str.startsWith("http")) {
                    list.add(str.substring(0, str.lastIndexOf("/") - 8));
                }else{
                    list.add(Url.HTTPURL+str);
                }
            }
            bundle.putStringArrayList("image_urls", (ArrayList<String>) list);
            bundle.putInt("image_index", index);
            intent.putExtras(bundle);
            ctx.startActivity(intent);
        }
        if (weiboInfo.getType().equalsIgnoreCase("repost")) {
            Intent intent = new Intent(ctx, ImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("image_urls", (ArrayList<String>) weiboInfo.getRepostWeiboInfo().getImgList());
            bundle.putInt("image_index", index);
            intent.putExtras(bundle);
            ctx.startActivity(intent);
        }
    }

    static class Holder {
        ImageView UserHead;
        TextView UserName;
        TextView   repostName, repostWeiboTime, WeiboTop, Weibo_From;
        FaceTextView MainText,repostText;
        ImageView MainImg, MainImg1, MainImg2;
        List<ImageView> imgViewList = new ArrayList<ImageView>();
        LinearLayout Up, enterDetail;
        ImageView Up_Img;
        TextView Up_text, ShareNum;
        LinearLayout repostLinerLayout, sendRepost, sendComment;
        TextView CommentNum;
        LinearLayout Share;
        ImageView Share_Img;
        TextView Ctime;

        boolean upFlag = false;
    }

}