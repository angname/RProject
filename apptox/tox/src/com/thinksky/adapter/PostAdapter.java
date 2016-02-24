package com.thinksky.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksky.info.PostInfo;
import com.thinksky.tox.ImagePagerActivity;
import com.thinksky.tox.R;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;
import com.tox.BaseFunction;
import com.tox.ForumApi;
import com.tox.ToastHelper;
import com.tox.Url;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/1/22 0022.
 */
public class PostAdapter extends BaseAdapter  {

    private List<PostInfo> mList;
    private Context ctx,forumCtx;
    private ImageLoader mLoadImgHead;
    private KJBitmap kjBitmap;
    private FinalBitmap finalBitmap;
    private LoadImg loadImgHeadImg;
    private LoadImg loadImgMainImg;
    private boolean upFlag=false;
    private boolean showEditFlag=true;
    private LinearLayout mEditBox;
    private ForumApi forumApi=new ForumApi();

    public PostAdapter(List<PostInfo> mList, Context ctx,LinearLayout mEditBox, KJBitmap kjBitmap, FinalBitmap finalBitmap) {
        this.mList = mList;
        this.ctx = ctx;
        this.mEditBox=mEditBox;
        this.kjBitmap = kjBitmap;
        this.finalBitmap = finalBitmap;
        loadImgHeadImg = new LoadImg(ctx);
        loadImgMainImg = new LoadImg(ctx);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder hold;
        final int number=position;

        if(convertView==null){
            hold=new Holder();
            convertView=View.inflate(ctx, R.layout.post,null);
            hold.comCount=(TextView)convertView.findViewById(R.id.Post_comNum);
            hold.supportCount=(TextView)convertView.findViewById(R.id.Post_likeNum);
            hold.comImg=(ImageView)convertView.findViewById(R.id.Post_comImg);
            hold.supportImg=(ImageView)convertView.findViewById(R.id.Post_likeImg);
            hold.postContent=(TextView)convertView.findViewById(R.id.Post_MainText);
            hold.postFrom=(TextView)convertView.findViewById(R.id.Post_from);
            hold.postImg1=(ImageView)convertView.findViewById(R.id.Post_MainImg);
            hold.postImg2=(ImageView)convertView.findViewById(R.id.Post_MainImg1);
            hold.postImg3=(ImageView)convertView.findViewById(R.id.Post_MainImg2);
            hold.imgList.add(hold.postImg1);
            hold.imgList.add(hold.postImg2);
            hold.imgList.add(hold.postImg3);
            hold.userName=(TextView)convertView.findViewById(R.id.Post_UserName);
            hold.userHead=(ImageView)convertView.findViewById(R.id.Post_UserHead);
            hold.publicTime=(TextView)convertView.findViewById(R.id.Post_Ctime);
            hold.postTitle=(TextView)convertView.findViewById(R.id.Post_title);
            hold.supportImg.setTag(mList.get(position).getPostId());
            hold.comImg.setTag(mList.get(position).getPostId());
            hold.postImg1.setVisibility(View.GONE);
            hold.postImg2.setVisibility(View.GONE);
            hold.postImg3.setVisibility(View.GONE);
            convertView.setTag(hold);
        }else{
            Log.e("","");
            hold=(Holder)convertView.getTag();
            if(mList.get(position).getIs_support()==0){
                upFlag=false;
            }
            hold.postImg1.setVisibility(View.GONE);
            hold.postImg2.setVisibility(View.GONE);
            hold.postImg3.setVisibility(View.GONE);
        }

        hold.userName.setText(mList.get(position).getUserInfo().getNickname());
        hold.publicTime.setText(mList.get(position).getCreatTime());
        hold.postTitle.setText(mList.get(position).getPostTitle());
        if(mList.get(position).getPostContent().length()>30){
            hold.postContent.setText(mList.get(position).getPostContent().substring(0,30));
        }else {
            hold.postContent.setText(mList.get(position).getPostContent());
        }

        hold.userHead.setImageResource(R.drawable.side_user_avatar);
        hold.userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumApi.goUserInfo(ctx,mList.get(position).getUserInfo().getUid());
            }
        });
        hold.comCount.setText(mList.get(position).getReplyCount());
        hold.supportCount.setText(mList.get(position).getSupportCount());
        if (BaseFunction.fileExists(mList.get(position).getUserInfo().getAvatar())) {
            hold.userHead.setTag(mList.get(position).getUserInfo().getAvatar());
            hold.userHead.setImageBitmap(BitmapUtiles.localImgToBitmap(mList.get(position).getUserInfo().getAvatar(), 1));
        } else {
            BaseFunction.showImage(ctx, hold.userHead, mList.get(position).getUserInfo().getAvatar(), loadImgHeadImg, Url.IMGTYPE_HEAD);
        }

        if (hold.supportImg.getTag().equals(mList.get(position).getPostId())) {
            if (mList.get(position).getIs_support()==1) {
                //hold.Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, ctx));
                hold.upFlag = true;
            }else{
                hold.upFlag=false;
            }
        } else {
            if (mList.get(position).getIs_support()==1) {
               // hold.Up_Img.setImageBitmap(BitmapUtiles.drawableTobitmap(R.drawable.heart, ctx));
                hold.upFlag = true;
            } else {
                hold.upFlag = false;
            }
        }

        hold.supportImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BaseFunction.isLogin()){
                    if(!hold.upFlag){
                        hold.upFlag=true;
                        AnimationSet as=new AnimationSet(true);
                        ScaleAnimation scaleAnimation=new ScaleAnimation(0.5f,1.1f,0.5f,1.1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                        scaleAnimation.setDuration(500);
                        as.addAnimation(scaleAnimation);
                        hold.supportImg.startAnimation(as);
                        forumApi.setHandler(handler);
                        hold.supportCount.setText(Integer.parseInt(mList.get(position).getSupportCount())+1+"");
                        Log.d("post 点赞"+mList.get(position).getPostId(),"");
                        forumApi.supportPost(mList.get(position).getPostId());
                    }else{
                        ToastHelper.showToast("重复点赞",ctx);
                    }
                }else{
                    ToastHelper.showToast("请先登陆",ctx);
                }
            }
        });
        //显示图片
        setImVisible(mList.get(position).getImgList().size(), hold);
        if(mList.get(position).getImgList().size()>3){
            for ( int i=0;i<3;i++){
                BaseFunction.showImage(ctx,hold.imgList.get(i),mList.get(position).getImgList().get(i),loadImgMainImg,Url.IMGTYPE_WEIBO);
            }
        }else{
            for ( int i=0;i<mList.get(position).getImgList().size();i++){
                BaseFunction.showImage(ctx,hold.imgList.get(i),mList.get(position).getImgList().get(i),loadImgMainImg,Url.IMGTYPE_WEIBO);


            }
        }

        hold.postImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhotoBrowser(position,0);
            }
        });
        hold.postImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhotoBrowser(position,1);
            }
        });
        hold.postImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhotoBrowser(position,2);
            }
        });
        hold.comImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ToastHelper.showToast("click",ctx);
                Url.PostID=mList.get(position).getPostId();
                boolean b=mEditBox.isShown();
                if(mEditBox.isShown()){
                    mEditBox.setVisibility(View.GONE);
                    EditText editText=(EditText)mEditBox.findViewById(R.id.Forum_index_edittext);
                    editText.setText("");
                }else{
                    EditText editText=(EditText)mEditBox.findViewById(R.id.Forum_index_edittext);
                    editText.setText("");
                    mEditBox.setVisibility(View.VISIBLE);
                }
            }
        });

      /*  hold.postContent.setText(mList.get(position).getPostContent());
        hold.postFrom.setText(mList.get(position).getPostFrom());
        hold.postTitle.setText(mList.get(position).getPostTitle());
        hold.supportCount.setText(mList.get(position).getLikeNum());
        hold.publicTime.setText(mList.get(position).getPublicTime());
        hold.userName.setText(mList.get(position).getUserInfo().getNickname());*/
        return convertView;
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                ToastHelper.showToast("点赞成功",ctx);

            }else{
                ToastHelper.showToast("重复点赞"+msg.what,ctx);
            }
        }
    };

    private void setImVisible(int num,Holder holder){
        int count=0;
        if(num>3){
            count=3;
        }else{
            count=num;
        }
        for (int i=0;i<count;i++){
            holder.imgList.get(i).setVisibility(View.VISIBLE);

        }
    }


    private void startPhotoBrowser(int arg0, int index) {
        PostInfo postInfo=mList.get(arg0);


            Intent intent = new Intent(ctx, ImagePagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("image_urls", (ArrayList<String>) postInfo.getImgList());

            bundle.putInt("image_index", index);
            intent.putExtras(bundle);
            ctx.startActivity(intent);

    }
    static class Holder{
        ImageView userHead,postImg1,postImg2,postImg3,supportImg,comImg;
        TextView userName,publicTime,supportCount,comCount,postTitle,postContent,postFrom;
        boolean upFlag=false;
        List<ImageView> imgList=new ArrayList<ImageView>();
    }
}
