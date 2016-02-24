package com.tox;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.thinksky.utils.BitmapUtiles;
import com.thinksky.utils.LoadImg;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 下载显示及缓存图片
 * Created by Administrator on 2014/7/24.
 */
public class BaseFunction {
    public static KJBitmap kjBitmap = KJBitmap.create();

    private BaseFunction() {

    }

    /**
     * @param bitmap
     * @param imageUrl 图片url
     * @param context
     * @author 小泽玛丽罗
     * @deprecated 将图片的bitmap保存到本地
     */
    public static boolean saveBitmapToLocal(Bitmap bitmap, String imageUrl, Context context, int type) {
        File file = new File(Url.IMGLOCAL);
        file.mkdirs();
        String ss=devide(imageUrl);
        String fileName = (ss.substring(ss.lastIndexOf("/", ss.lastIndexOf("/") - 1) + 1, ss.length())).replace("/", "");
        fileName = Url.IMGLOCAL + fileName.replaceAll("[/]","");
        Log.d("fileName>>>>>>",fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.e("已将图片保存到", fileName);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param imageUrl
     * @return
     * @author 小泽玛丽罗
     * @deprecated 判断图片在本地是否存在
     */
    public static boolean fileExists(String imageUrl) {

        String fileUrl = imageUrl.substring(imageUrl.lastIndexOf("/",imageUrl.lastIndexOf("/") - 1) + 1, imageUrl.length());
        fileUrl = Url.IMGLOCAL + fileUrl.replaceAll("[/]","");
        Log.d("fleUrl",fileUrl);
        try {
            File file = new File(fileUrl);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void imageLoadToLocal(ImageView imgView,   String imgurl, LoadImg loadImg, Context context, final int type) {
        Log.e("下载图片路径>>>>>>>>>>>>", imgurl);
        final String img=imgurl;
        final Bitmap bitmap = loadImg.loadImage(imgView,imgurl, new LoadImg.ImageDownloadCallBack() {
            @Override
            public void onImageDownload(ImageView imageView, Bitmap bitmap) {

                if (imageView.getTag() != null && imageView.getTag().equals(img)) {
                    Log.e("【imageViewTag", imageView.getTag().toString());
                    Log.e("图片路径", img + "】");
                    if (type == Url.IMGTYPE_HEAD) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageBitmap(bitmap);
                    }
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
        if (bitmap != null) {
            saveBitmapToLocal(bitmap, imgurl, context, type);
            imgView.setImageBitmap(bitmap);
            imgView.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param ctx
     * @param linearLayout
     * @param imgUrl
     * @param loadImg
     */
    public static void setLayoutBackGround(ImageView imageView,Context ctx, final LinearLayout linearLayout,String imgUrl,LoadImg loadImg){
        String imgPath = imgUrl.substring(imgUrl.lastIndexOf("/", imgUrl.lastIndexOf("/") - 1) + 1, imgUrl.length());
        imgPath = Url.IMGLOCAL + imgPath.replaceAll("[/]","");
        if(fileExists(imgUrl) && !imgUrl.equals("")){
            Drawable drawable=Drawable.createFromPath(imgPath);
            linearLayout.setBackgroundDrawable(drawable);
        }else{
            final Bitmap bitmap = loadImg.loadImage(imageView,replaceUrl(imgUrl), new LoadImg.ImageDownloadCallBack() {
                @Override
                public void onImageDownload(ImageView imageView, Bitmap bitmap) {

                        Drawable d=new BitmapDrawable(bitmap);
                        linearLayout.setBackgroundDrawable(d);

                }
            });
            if (bitmap!=null){
                saveBitmapToLocal(bitmap,imgUrl,ctx,2);
            }
        }
    }
    /**
     * @param context   activity的环境变量
     * @param imageView 要设置图片的imageview
     * @param imgUrl    图片的网络路径
     * @param loadImg   下载图片的实例对象
     * @param type      图片显示类型，分头像和微博图片
     * @author 小泽玛丽罗
     */
    public static void showImage(Context context, ImageView imageView, String imgUrl, LoadImg loadImg, int type) {

        String imgPath = imgUrl.substring(imgUrl.lastIndexOf("/",imgUrl.lastIndexOf("/") - 1) + 1, imgUrl.length());
        imgPath = Url.IMGLOCAL + imgPath.replaceAll("[/]","");
        imageView.setTag(imgPath);
        //判断图片是否存在，不存在就下载
        if (fileExists(imgUrl) /*&& !imgUrl.equals("")*/) {
            Log.d("是否存在",imgUrl);
            if (imageView.getTag().equals(imgPath)) {
                imageView.setTag(imgPath);
                Log.d("imgUrl>>>",imgUrl);
            }

            //String imgPath=Url.IMGLOCAL+ imgUrl.substring(imgUrl.lastIndexOf("/")+1,imgUrl.length());
                    /*BitmapFactory.Options options=new BitmapFactory.Options();
                    if(type==Url.IMGTYPE_HEAD)
                        options.inSampleSize=2;
                    Bitmap bitmap=BitmapFactory.decodeFile(imgPath,options);*/
            if (type == Url.IMGTYPE_HEAD) {
                Log.e("头像", imgPath);
                kjBitmap.display(imageView, imgPath,128,128);
                Log.d("imgPath>>>1", imgPath);
                //finalBitmap.display(imageView,imgPath,40,40,BitmapUtiles.drawableTobitmap(R.drawable.side_user_avatar,context),BitmapUtiles.drawableTobitmap(R.drawable.side_user_avatar,context));
//                        imageView.setImageBitmap(roundBitmap.getRoundedCornerBitmap(bitmap));
                //imageView.setVisibility(View.VISIBLE);
            } else if (type == Url.IMGTYPE_BIG) {
                Log.e("非头像大", imgPath);
                imageView.setImageBitmap(BitmapUtiles.loadBitmap(imgPath, 2));
                //kjBitmap.display(imageView,imgPath,100,100);//,BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no,context),BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no,context));
//                        imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            } else if(type==Url.IMGTYPE_WEIBO){

                Log.e("非头像", imgUrl);
//                imageView.setImageBitmap(BitmapUtiles.loadBitmap(imgPath, 1));
                kjBitmap.display(imageView,imgUrl);//,BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no,context),BitmapUtiles.drawableTobitmap(R.drawable.friends_sends_pictures_no,context));
//                        imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }
        } else {
            String url=replaceUrl(imgUrl);
            imageView.setTag(url);
            //对url进行判断是否是以http://开头

            imageLoadToLocal(imageView, url, loadImg, context, type);
        }
    }

    /**
     *dui图片的url进行组装替换
     */
    public static String replaceUrl(String url){
        if(url.startsWith("http")){
            return url;
        }else{
//            return (Url.USERHEADURL+"/"+url).replace("com///","com/").replace("com//","com/").replace("cn///","cn/").replace("cn//","cn/").replace("net///","net/").replace("net//","net/");
            return Url.USERHEADURL+"/"+url;
        }

    }
    /**
     * 判断是否登入
     *
     * @return
     */
    public static boolean isLogin() {
        if (!Url.SESSIONID.equals("")) {
            Log.e(">>>>>>>", "已经登入");
            return true;
        }
        return false;
    }

    public static String TimeStamp2Date(String timestampString, String form) {

        String date = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(Long.parseLong(timestampString) * 1000));
        Log.e("时间", date);
        return date;
    }


    public static String getTimeNow() {
        Date date = new Date();
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        String dateNoew = simpleDateFormat.format(date);
        if (w < 0) {
            w = 0;
        }
        return dateNoew + weekDays[w];
    }

    public static String getSharepreference(String key,Context ctx,String sp_name){
        SharedPreferences sp=ctx.getSharedPreferences(sp_name, 0);
        return sp.getString(key,"");
    }

    public static void putSharepreference(String key,String value,Context ctx,String sp_name){
        SharedPreferences sp=ctx.getSharedPreferences(sp_name,0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    //判断图片路径是否已是选中的图片
    public static boolean isExistsInList(String path, List<String> pathList) {
        if (pathList == null || pathList.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < pathList.size(); i++) {
                if (path.equalsIgnoreCase(pathList.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String devide(String imageUrl) {
        String s=null;
        if(imageUrl.startsWith("http://upload")){
            if (imageUrl.indexOf(".png")!=-1){
                return imageUrl.substring(0,(imageUrl.indexOf(".png")+4));
            }
            if((imageUrl.indexOf(".jpg"))==-1){
                return imageUrl.substring(0,(imageUrl.indexOf(".jpeg")+5));
            }else {
                s = imageUrl.substring(0, (imageUrl.indexOf(".jpg") + 4));
                return s;
            }
        }
        if(imageUrl.startsWith("http://img")){
            s = imageUrl.substring(imageUrl.lastIndexOf("/"),imageUrl.length());
            return s;
        }
        return imageUrl;
    }

    public static void addListBottom(ListView listView,String text){

    }
}
