package com.thinksky.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksky.tox.R;
import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;
import com.tox.BaseFunction;
import com.tox.Url;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.ui.widget.HorizontalListView;
import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/2/3 0003.
 */
public class PhotoAdapter extends BaseAdapter {
    private KJBitmap kjBitmap;
    private List<String> imgUrl=new ArrayList<String>();
    private Context ctx;
    private FinalBitmap finalBitmap;
    private LoadImg loadImg;
    public PhotoAdapter(Context ctx,FinalBitmap finalBitmap,KJBitmap kjBitmap,List<String> list) {
        this.kjBitmap=kjBitmap;
        this.finalBitmap=finalBitmap;
        this.imgUrl=list;

        this.ctx=ctx;
        loadImg=new LoadImg(ctx);
    }

    @Override
    public int getCount() {
        return imgUrl.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  Holder holder;
        if(convertView==null){
            holder=new Holder();
            convertView=View.inflate(ctx, R.layout.photo_item,null);
            holder.imageView=(ImageView)convertView.findViewById(R.id.Photo_item);
            holder.imageView.setTag(imgUrl.get(position));
            holder.delImg=(ImageView)convertView.findViewById(R.id.delImg);
            holder.delImg.setAlpha(100);
            convertView.setTag(holder);

        }else{
            holder=(Holder)convertView.getTag();
        }
        if(holder.imageView.getTag().equals(imgUrl.get(position))){
           holder.imageView.setImageBitmap(BitmapUtiles.loadBitmap(imgUrl.get(position),4));
        }else {
            holder.imageView.setTag(imgUrl.get(position));
            holder.imageView.setImageBitmap(BitmapUtiles.loadBitmap(imgUrl.get(position),4));
        }

        return convertView;
    }

    static class Holder{
        ImageView imageView,delImg;
    }



}
