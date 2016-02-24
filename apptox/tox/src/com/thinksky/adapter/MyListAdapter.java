package com.thinksky.adapter;

import java.util.ArrayList;
import java.util.List;

import com.thinksky.anim3d.RoundBitmap;
import com.thinksky.tox.R;
import com.thinksky.info.WeiboInfo;
import com.tox.BaseFunction;
import com.tox.TouchHelper;
import com.tox.Url;
import com.thinksky.utils.LoadImg;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

public class MyListAdapter extends BaseAdapter {

    private List<WeiboInfo> list;
    private Context ctx;
    private LoadImg loadImgHeadImg;
    private LoadImg loadImgMainImg;
    private boolean upFlag = false;
    private boolean downFlag = false;
    private RoundBitmap roundBitmap = new RoundBitmap();

    private FinalBitmap finalBitmap;
    private KJBitmap kjBitmap;

    public MyListAdapter(Context ctx, List<WeiboInfo> list, FinalBitmap finalBitmap) {
        this.list = list;
        this.ctx = ctx;
        this.finalBitmap = finalBitmap;
        kjBitmap = KJBitmap.create();
        // 加载图片对象
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
        if (arg1 == null) {
            hold = new Holder();
            arg1 = View.inflate(ctx, R.layout.mylistview_item, null);
            hold.UserHead = (ImageView) arg1.findViewById(R.id.Item_UserHead);
            hold.UserName = (TextView) arg1.findViewById(R.id.Item_UserName);
            hold.MainText = (TextView) arg1.findViewById(R.id.Item_MainText);
            hold.MainImg = (ImageView) arg1.findViewById(R.id.Item_MainImg);
            hold.MainImg1 = (ImageView) arg1.findViewById(R.id.Item_MainImg1);
            hold.MainImg2 = (ImageView) arg1.findViewById(R.id.Item_MainImg2);
            hold.imgViewList.add(hold.MainImg);
            hold.imgViewList.add(hold.MainImg1);
            hold.imgViewList.add(hold.MainImg2);
            hold.Up = (LinearLayout) arg1.findViewById(R.id.Item_Up);
            hold.Up_Img = (ImageView) arg1.findViewById(R.id.Item_Up_img);
            hold.LikeNum = (TextView) arg1.findViewById(R.id.Like_text);
            hold.repostLinerLayout = (LinearLayout) arg1.findViewById(R.id.RepostWeibo);
            hold.repostText = (TextView) arg1.findViewById(R.id.Repost_content);
            hold.repostName = (TextView) arg1.findViewById(R.id.Repost_name);
            hold.repostWeiboTime = (TextView) arg1.findViewById(R.id.Repost_time);
            hold.sendRepostWeibo = (LinearLayout) arg1.findViewById(R.id.SendRepostWeibo);
            hold.ShareNum = (TextView) arg1.findViewById(R.id.Repost_Text);
            hold.CommentNum = (TextView) arg1.findViewById(R.id.Item_CommentNum);
            hold.sendRepostWeibo = (LinearLayout) arg1.findViewById(R.id.ListView_itemComment);
            hold.Ctime = (TextView) arg1.findViewById(R.id.Item_Ctime);
            hold.imgViewList.get(0).setVisibility(View.GONE);
            hold.imgViewList.get(1).setVisibility(View.GONE);
            hold.imgViewList.get(2).setVisibility(View.GONE);
            hold.Up.setOnTouchListener(new TouchHelper(ctx, "#ededed", "#ffffff", "color"));
            hold.sendRepostWeibo.setOnTouchListener(new TouchHelper(ctx, "#ededed", "#ffffff", "color"));
            hold.sendComment.setOnTouchListener(new TouchHelper(ctx, "#ededed", "#ffffff", "color"));
            arg1.setTag(hold);
        } else {
            hold = (Holder) arg1.getTag();
        }

        hold.UserName.setText(list.get(arg0).getUser().getNickname());
        hold.MainText.setText(list.get(arg0).getWcontent());
        //hold.LikeNum.setText(list.get(arg0).getQlike());
        //hold.Down_text.setText("-" + list.get(arg0).getQunlike());
        hold.CommentNum.setText(list.get(arg0).getRepost_count());
        hold.Ctime.setText(list.get(arg0).getCtime());
        hold.CommentNum.setText(list.get(arg0).getComment_count());
        hold.ShareNum.setText(list.get(arg0).getRepost_count());

        Log.e("ID", arg0 + "" + list.get(arg0).getType() + list.get(arg0).getWid());
        String weibotype = list.get(arg0).getType();
        if (weibotype.equalsIgnoreCase("repost")) {
            WeiboInfo repostWeibo = list.get(arg0).getRepostWeiboInfo();
            hold.repostLinerLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
            hold.repostName.setText(repostWeibo.getUser().getNickname());
            hold.repostWeiboTime.setText(repostWeibo.getCtime());
            hold.repostText.setText(repostWeibo.getWcontent());
            hold.repostLinerLayout.setVisibility(View.VISIBLE);
            hold.repostText.setVisibility(View.VISIBLE);
            hold.repostWeiboTime.setVisibility(View.VISIBLE);
            hold.repostName.setVisibility(View.VISIBLE);
            String repostWeiboType = repostWeibo.getType();
            if (repostWeiboType.equalsIgnoreCase("image")) {
                List<String> imglist = repostWeibo.getImgList();
                if (imglist.size() >= 3) {
                    //setImgRec(hold,imglist,3);
                    hold.repostLinerLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 3; i++) {
                        setWeiboImgVisiable(hold, 3);
                        loadWeiboImg(hold, hold.imgViewList.get(i), Url.USERHEADURL + "/" + imglist.get(i), finalBitmap);
                    }
                } else {
                    // setImgRec(hold,imglist,imglist.size());
                    setWeiboImgVisiable(hold, imglist.size());
                    for (int i = 0; i < imglist.size(); i++) {
                        loadWeiboImg(hold, hold.imgViewList.get(i), Url.USERHEADURL + "/" + imglist.get(i), finalBitmap);
                    }
                }
            }
        } else if (weibotype.equalsIgnoreCase("image")) {
            hold.repostLinerLayout.setVisibility(View.VISIBLE);
            hold.repostLinerLayout.setBackgroundColor(Color.parseColor("#F5F2F0"));
            hold.repostText.setVisibility(View.GONE);
            hold.repostWeiboTime.setVisibility(View.GONE);
            hold.repostName.setVisibility(View.GONE);
            List<String> imglist = list.get(arg0).getImgList();
            if (imglist.size() >= 3) {
                //setImgRec(hold,imglist,3);
                setWeiboImgVisiable(hold, 3);
                for (int i = 0; i < 3; i++) {
                    hold.imgViewList.get(i).setVisibility(View.VISIBLE);
                    loadWeiboImg(hold, hold.imgViewList.get(i), Url.USERHEADURL + "/" + imglist.get(i), finalBitmap);
                }
            } else {
                // setImgRec(hold,imglist,imglist.size());
                setWeiboImgVisiable(hold, imglist.size());
                for (int i = 0; i < imglist.size(); i++) {
                    hold.imgViewList.get(i).setVisibility(View.VISIBLE);
                    loadWeiboImg(hold, hold.imgViewList.get(i), Url.USERHEADURL + "/" + imglist.get(i), finalBitmap);
                }
            }
        } else {
            hold.repostLinerLayout.setVisibility(View.GONE);
        }


        // 设置监听
        hold.Up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                upFlag = true;
                downFlag = false;
                hold.Up.setBackgroundResource(R.drawable.button_vote_active);
                hold.Up_Img.setImageResource(R.drawable.icon_for_active);
                hold.LikeNum.setTextColor(Color.RED);
                hold.Up.setTag("0");
            }
        });


        hold.Share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(ctx, "分享被点击", Toast.LENGTH_LONG).show();
            }
        });
        hold.UserHead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(ctx, "点击发送小纸条", Toast.LENGTH_LONG).show();
            }
        });
        hold.MainImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(ctx, "查看大图被点击", Toast.LENGTH_LONG).show();
            }
        });

//		hold.Up.setTag(list.get(arg0).getQid());
        // Log.e("liuxiaowei", hold.Up.getTag().toString());
      /*  hold.Up.setBackgroundResource(R.drawable.button_vote_enable);
        hold.Up_Img.setImageResource(R.drawable.icon_against_enable);
        hold.LikeNum.setTextColor(Color.parseColor("#815F3D"));

        if (hold.Up.getTag().equals("0")) {
            hold.Up.setBackgroundResource(R.drawable.button_vote_active);
            hold.Up_Img.setImageResource(R.drawable.icon_for_active);
            hold.LikeNum.setTextColor(Color.RED);
        }*/
        hold.UserHead.setImageResource(R.drawable.side_user_avatar);
        BaseFunction.showImage(ctx, hold.UserHead, list.get(arg0).getUser().getAvatar(), loadImgHeadImg, Url.IMGTYPE_HEAD);

        return arg1;
    }

    private void setWeiboImgVisiable(Holder holder, int num) {
        for (int i = 0; i < num; i++) {
            holder.imgViewList.get(i).setVisibility(View.VISIBLE);

        }
    }


    public void loadWeiboImg(Holder hold, final ImageView weiboImg, final String url, FinalBitmap finalBitmap) {
        weiboImg.setTag(url);
        BaseFunction.showImage(ctx, weiboImg, url, loadImgMainImg, Url.IMGTYPE_WEIBO);
    }

    static class Holder {
        ImageView UserHead;
        TextView UserName;
        TextView MainText, ShareNum, repostText, repostName, repostWeiboTime;
        ImageView MainImg, MainImg1, MainImg2;
        List<ImageView> imgViewList = new ArrayList<ImageView>();
        LinearLayout Up;
        ImageView Up_Img;
        TextView LikeNum;
        LinearLayout Down, repostLinerLayout, sendRepostWeibo, sendComment;

        TextView CommentNum;
        LinearLayout Share;
        ImageView Share_Img;
        TextView Ctime;
    }

}
