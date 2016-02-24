package com.thinksky.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksky.info.Com2Com;
import com.thinksky.info.PostComment;
import com.thinksky.info.PostInfo;
import com.thinksky.tox.LandLordActivity;
import com.thinksky.tox.R;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;
import com.tox.BaseFunction;
import com.tox.ForumApi;
import com.tox.Url;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.List;

/**
 * Created by Administrator on 2015/1/23 0023.
 */
public class PostCommentAdapter extends BaseAdapter {
    private Context ctx;
    private List<PostComment> postCommentList;
    private KJBitmap kjBitmap;
    private FinalBitmap finalBitmap;
    private LoadImg loadImg;
    private PostInfo postInfo;
    private PostDetailCallBack postDetailCallBack;
    private ForumApi forumApi;
    public PostCommentAdapter(Context ctx, List<PostComment> postCommentList, KJBitmap kjBitmap, FinalBitmap finalBitmap,PostInfo postInfo,PostDetailCallBack postDetailCallBack) {
        this.ctx = ctx;
        this.postCommentList = postCommentList;
        this.kjBitmap = kjBitmap;
        this.finalBitmap = finalBitmap;
        loadImg=new LoadImg(ctx);
        this.postInfo=postInfo;
        this.postDetailCallBack=postDetailCallBack;
    }

    @Override
    public int getCount() {
        return postCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return postCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        forumApi = new ForumApi();
        final int number=position;
        if(convertView ==null){

            holder=new Holder();
                convertView=View.inflate(ctx,R.layout.post_comment_item,null);
                holder.com1Content=(TextView)convertView.findViewById(R.id.Post_detail_comment2comment);
                holder.com2Content=(TextView)convertView.findViewById(R.id.Post_detail_comment2comment1);
                holder.moreCom=(TextView)convertView.findViewById(R.id.Post_more_com2com);
                holder.postComContent=(TextView)convertView.findViewById(R.id.Post_comContent);
                holder.userName=(TextView)convertView.findViewById(R.id.Post_commentUsername);
                holder.postComTime=(TextView)convertView.findViewById(R.id.Post_detail_comment_time_louceng);
                holder.userHead=(ImageView)convertView.findViewById(R.id.Post_comItem_UserHead);
                holder.photoLayout=(LinearLayout)convertView.findViewById(R.id.Post_comment_photoLayout);
                holder.com1Layout=(RelativeLayout)convertView.findViewById(R.id.com1_layout);
            holder.com2Layout=(RelativeLayout)convertView.findViewById(R.id.com2_layout);
            holder.com1Time=(TextView)convertView.findViewById(R.id.com1_time);
            holder.com2Time=(TextView)convertView.findViewById(R.id.com2_time);
            holder.com1Username=(TextView)convertView.findViewById(R.id.com1_username);
            holder.com2Username=(TextView)convertView.findViewById(R.id.com2_username);
            holder.com1_userHead=(ImageView)convertView.findViewById(R.id.com1_userHead);
            holder.com2_userHead=(ImageView)convertView.findViewById(R.id.com2_userHead);
            holder.com1Louzhu=(LinearLayout)convertView.findViewById(R.id.com1_louzhu);
            holder.com2Louzhu=(LinearLayout)convertView.findViewById(R.id.com2_louzhu);
            holder.mHalvomgLine=(LinearLayout)convertView.findViewById(R.id.Post_comment_halvingLine);
            holder.mFloatLayout=(LinearLayout)convertView.findViewById(R.id.Post_detail_com_floatLayout);
            holder.mFloatImgBtn=(ImageView)convertView.findViewById(R.id.Post_detail_floatComBtn);
            convertView.setTag(holder);
        }else {
            holder=(Holder)convertView.getTag();
        }
        if (holder.photoLayout.getChildAt(0) != null){
            holder.photoLayout.removeAllViews();
        }
        for(String imgUrl:postCommentList.get(position).getImgList()){
            holder.photoLayout.setVisibility(View.GONE);
            ImageView view=new ImageView(ctx);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,10);
            view.setLayoutParams(layoutParams);
            view.setImageResource(R.drawable.friends_sends_pictures_no);
            holder.photoLayout.addView(view);
            BaseFunction.showImage(ctx,view,imgUrl,loadImg,Url.IMGTYPE_WEIBO);
        }
        if(postCommentList.get(position).getImgList().size()>0){
            holder.photoLayout.setVisibility(View.VISIBLE);
        }else{
            holder.photoLayout.setVisibility(View.GONE);
        }

        holder.moreCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.userName.setText(postCommentList.get(position).getUserInfo().getNickname());
        holder.postComContent.setText(postCommentList.get(position).getComContent());

        holder.userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumApi.goUserInfo(ctx,postCommentList.get(position).getUserInfo().getUid());
            }
        });
        holder.userHead.setImageResource(R.drawable.side_user_avatar);
        if (BaseFunction.fileExists(postCommentList.get(position).getUserInfo().getAvatar())) {
            holder.userHead.setTag(postCommentList.get(position).getUserInfo().getAvatar());
            holder.userHead.setImageBitmap(BitmapUtiles.localImgToBitmap(postCommentList.get(position).getUserInfo().getAvatar(), 1));
        } else {
            BaseFunction.showImage(ctx, holder.userHead, postCommentList.get(position).getUserInfo().getAvatar(), loadImg, Url.IMGTYPE_HEAD);
        }
        setMoreComText(holder, position);
        holder.postComTime.setText("第"+(position+2)+"楼"+postCommentList.get(position).getcTime());

        holder.moreCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postCommentList.get(position).getCom2comList().size()>2){
                    enterLandlord(0,position);
                }
            }
        });
        holder.mFloatImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( holder.mFloatLayout.isShown()){
                    ScaleAnimation scaleAnimation = new ScaleAnimation(
                            1.0f, 0.0f,1.0f,1.0f,
                            Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(200);
                    holder.mFloatLayout.startAnimation(scaleAnimation);
                    holder.mFloatLayout.setVisibility(View.GONE);
                }else{
                   /* TranslateAnimation mShowAction=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    mShowAction.setDuration(500);*/
                    ScaleAnimation scaleAnimation = new ScaleAnimation(
                            0.0f, 1.0f,1.0f,1.0f,
                            Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(200);
                    holder.mFloatLayout.startAnimation(scaleAnimation);
                    holder.mFloatLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.com1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterLandlord(0,position);

            }
        });
        holder.com2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterLandlord(1,position);
            }
        });
        holder.mFloatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                postDetailCallBack.callback();
                Intent intent=new Intent(ctx,LandLordActivity.class);
                intent.putExtra("type",Url.Type_postCom);
                intent.putExtra("keyLock",true);
                Bundle bundle=new Bundle();
                bundle.putSerializable("comInfo",postCommentList.get(position));
                intent.putExtra("postComment",bundle);
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1.0f, 0.0f,1.0f,1.0f,
                        Animation.RELATIVE_TO_SELF,1f,
                        Animation.RELATIVE_TO_SELF,1f);
                scaleAnimation.setDuration(200);

                holder.mFloatLayout.startAnimation(scaleAnimation);
                holder.mFloatLayout.setVisibility(View.GONE);
                ctx.startActivity(intent);
            }
        });

        return convertView;
    }

    private void setMoreComText(Holder holder,int position){

        if(postCommentList.get(position).getCom2comList().size()>2){
           setCom(holder,position);
            int num=postCommentList.get(position).getCom2comList().size()-2;
            holder.moreCom.setVisibility(View.VISIBLE);
            holder.mHalvomgLine.setVisibility(View.VISIBLE);
            holder.moreCom.setText("查看更多"+num+"条回复");
        }else{
           switch (postCommentList.get(position).getCom2comList().size()){
               case 0:
                   holder.com1Layout.setVisibility(View.GONE);
                   holder.com2Layout.setVisibility(View.GONE);
                    holder.moreCom.setVisibility(View.GONE);
                   holder.mHalvomgLine.setVisibility(View.GONE);
                   break;
               case 1:
                   String text="";
                   holder.mHalvomgLine.setVisibility(View.VISIBLE);
                   Com2Com com2Com=postCommentList.get(position).getCom2comList().get(0);
                   holder.com1Layout.setVisibility(View.VISIBLE);
                   holder.com1Username.setText(com2Com.getUserInfo().getNickname());
                   holder.com1Time.setText(com2Com.getcTime());
                   holder.com1Content.setText(com2Com.getContent());
                   BaseFunction.showImage(ctx,holder.com1_userHead,com2Com.getUserInfo().getAvatar(),loadImg,Url.IMGTYPE_HEAD);
                    if(com2Com.getIsLandlord()){
                        holder.com1Louzhu.setVisibility(View.VISIBLE);
                    }
                   holder.com2Layout.setVisibility(View.GONE);
                   holder.moreCom.setVisibility(View.GONE);
                   break;
               case 2:
                   setCom(holder,position);
                   holder.moreCom.setVisibility(View.GONE);

                   break;
               default:
                   break;
           }
        }
    }

    private void enterLandlord(int id,int position){
        Intent intent=new Intent(ctx,LandLordActivity.class);
        Bundle bundle=new Bundle();
        if(id==0){
            bundle.putSerializable("comInfo",postCommentList.get(position));
            bundle.putSerializable("com2com",postCommentList.get(position).getCom2comList().get(0));
        }else{
            bundle.putSerializable("comInfo",postCommentList.get(position));
            bundle.putSerializable("com2com",postCommentList.get(position).getCom2comList().get(1));
        }
        intent.putExtra("type",Url.Type_landlord);
        intent.putExtra("postComment",bundle);
        ctx.startActivity(intent);
    }

    private void setCom(Holder holder,int position){
        final Com2Com com=postCommentList.get(position).getCom2comList().get(0);
        holder.com1Layout.setVisibility(View.VISIBLE);
        holder.mHalvomgLine.setVisibility(View.VISIBLE);
        holder.com1Username.setText(com.getUserInfo().getNickname());
        holder.com1Time.setText(com.getcTime());
        holder.com1Content.setText(com.getContent());
        BaseFunction.showImage(ctx, holder.com1_userHead, com.getUserInfo().getAvatar(), loadImg, Url.IMGTYPE_HEAD);
        holder.com1_userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumApi.goUserInfo(ctx,com.getUserInfo().getUid());
            }
        });
        if(com.getIsLandlord()){
            holder.com1Louzhu.setVisibility(View.VISIBLE);
        }
        final Com2Com com1=postCommentList.get(position).getCom2comList().get(1);
        holder.com2Layout.setVisibility(View.VISIBLE);
        holder.com2Username.setText(com1.getUserInfo().getNickname());
        holder.com2Time.setText(com1.getcTime());
        holder.com2Content.setText(com1.getContent());
        BaseFunction.showImage(ctx, holder.com2_userHead, com1.getUserInfo().getAvatar(), loadImg, Url.IMGTYPE_HEAD);
        holder.userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumApi.goUserInfo(ctx,com1.getUserInfo().getUid());
            }
        });
        if(com1.getIsLandlord()){
            holder.com2Louzhu.setVisibility(View.VISIBLE);
        }
    }

    public interface PostDetailCallBack{
        public void callback();
    }

    static class Holder{
        TextView userName,postComTime,postComContent,com1Content,com2Content,moreCom,com1Username,com2Username,com1Time,com2Time;
        ImageView userHead,com1_userHead,com2_userHead,mFloatImgBtn;
        LinearLayout photoLayout,com1Louzhu,com2Louzhu,mHalvomgLine,mFloatLayout;
        RelativeLayout com1Layout,com2Layout;
    }
}
