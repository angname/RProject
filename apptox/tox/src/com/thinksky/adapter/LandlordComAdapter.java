package com.thinksky.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksky.info.Com2Com;
import com.thinksky.tox.R;
import com.thinksky.utils.LoadImg;
import com.tox.BaseFunction;
import com.tox.ForumApi;
import com.tox.Url;

import java.util.List;

/**
 * Created by Administrator on 2015/2/9 0009.
 */
public class LandlordComAdapter extends BaseAdapter {
    private Context ctx;
    private List<Com2Com> list;
    private LoadImg loadImg;
    private LandLordCallBack landLordCallBack;
    private ForumApi forumApi;

    public LandlordComAdapter(Context ctx, List<Com2Com> list,LandLordCallBack landLordCallBack) {
        this.ctx = ctx;
        this.loadImg = new LoadImg(ctx);
        this.list = list;
        this.landLordCallBack=landLordCallBack;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        forumApi = new ForumApi();
        if(convertView==null){
            holder=new Holder();
            convertView=View.inflate(ctx, R.layout.post_landlord_com_item,null);
            holder.comContent=(TextView)convertView.findViewById(R.id.landlord_content);
            holder.comTime=(TextView)convertView.findViewById(R.id.landlord_time);
            holder.userHead=(ImageView)convertView.findViewById(R.id.landlord_userhead);
            holder.userName=(TextView)convertView.findViewById(R.id.landlord_username);
            holder.landordLayout=(RelativeLayout)convertView.findViewById(R.id.landlord_layout);
            holder.userHead.setTag(list.get(position).getUserInfo().getAvatar());
            convertView.setTag(holder);
        }else {
            holder=(Holder)convertView.getTag();
        }
        holder.userName.setText(list.get(position).getUserInfo().getNickname());
        holder.comTime.setText(list.get(position).getcTime());
        holder.comContent.setText(list.get(position).getContent());
        if(holder.userHead.getTag().equals(list.get(position).getUserInfo().getAvatar())){
           BaseFunction.showImage(ctx,holder.userHead,list.get(position).getUserInfo().getAvatar(),loadImg, Url.IMGTYPE_HEAD);
        }else{
            BaseFunction.showImage(ctx,holder.userHead,list.get(position).getUserInfo().getAvatar(),loadImg,Url.IMGTYPE_HEAD);
        }
        holder.userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumApi.goUserInfo(ctx,list.get(position).getUserInfo().getUid());
            }
        });
        holder.landordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                landLordCallBack.callBack(list.get(position));
            }
        });

        return convertView;
    }

    public interface LandLordCallBack{
        public void callBack(Com2Com com);
    }

    static class Holder{
        ImageView userHead;
        TextView userName,comTime,comContent;
        RelativeLayout landordLayout;
    }
}
