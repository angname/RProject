package com.thinksky.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.tox.Url;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 小泽玛丽罗 on 2014/8/4 14:13.
 */
public class BitmapUtiles {
    /**
     * 禁止实例化
     */
    private BitmapUtiles() {

    }

    public static Bitmap localImgToBitmap(String path, int size) {

        String imgPath = path.substring(path.lastIndexOf("/", path.lastIndexOf("/") - 1) + 1, path.length());
        imgPath = Url.IMGLOCAL + imgPath.replaceAll("[/]","");
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (size != 0) {
            options.inSampleSize = size;
        } else {
            options.inSampleSize = 1;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        return bitmap;
    }

    public static String getUploadImgPath(String imgPath) {
        Bitmap bitmap = getSmallBitmap(imgPath);
        Log.e("bitmap>>>>>>>>>>>", bitmap.toString());

        int degree = readPictureDegree(imgPath);
        String temporaryFileName = saveBitampToFile(rotaingImageView(degree, bitmap), imgPath);
        Log.e("临时文件路径>>>>>>>>>>>", temporaryFileName);
        return temporaryFileName;
    }

    /**
     * 根据图片路径返回bitmap
     *
     * @param path
     * @param size
     * @return
     */
    public static Bitmap loadBitmap(String path, int size) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        File imageFile = new File(path);
        if (size != 0) {
            options.inSampleSize = size;
        } else {
            if (imageFile.length()/1024 > 1000 ) {
                options.inSampleSize = 4;
            }else if (imageFile.length()/1024 > 500 ) {
                options.inSampleSize = 2;
            }else if (imageFile.length()/1024 > 200 ) {
                options.inSampleSize = 1;
            }
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static void loadBitmapFitWindow(String path, int nWidth) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int width = bitmap.getWidth();

        if (width < nWidth) {

        }


    }

    /**
     * 在上传图片文件前把原图读出来后获得压缩后的bitmap进行保存,然后上传
     *
     * @param bitmap
     * @return
     */
    public static String saveBitampToFile(Bitmap bitmap, String path) {
        File file = new File(Url.UPLOADTEMPORARYPATH);
        file.mkdirs();
        String filePath = Url.UPLOADTEMPORARYPATH + path.substring(path.lastIndexOf("/") + 1, path.length());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (Exception e) {
            return "0";
        }
        return filePath;
    }


    /**
     * 根据资源ID返回bitmap
     *
     * @param bmpid
     * @param ctx
     * @return
     */
    public static Bitmap drawableTobitmap(int bmpid, Context ctx) {
        Resources res = ctx.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        return BitmapFactory.decodeResource(res, bmpid, options);
    }

    /**
     * 计算图片缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据图片路径获得图片并压缩后返回
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


    public static long getFileSize(List<String> imgPath) {
        long size = 0;
        long count = 0;

        try {
            for (int i = 0; i < imgPath.size(); i++) {
                String path = getUploadImgPath(imgPath.get(i));

                File file = new File(path);
                if (file.exists()) {
                    FileInputStream fileLen = null;
                    fileLen = new FileInputStream(file);
                    size = fileLen.available();
                    count = count + size;
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("文件不存在", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count / 1024;
    }



//自己写得一个方法
    public static long getFileSize_(String imgPath) {
        long size = 0;
        long count = 0;

        try {

                String path = getUploadImgPath(imgPath);

                File file = new File(path);
                if (file.exists()) {
                    FileInputStream fileLen = null;
                    fileLen = new FileInputStream(file);
                    size = fileLen.available();
                    count = count + size;
                }

        } catch (FileNotFoundException e) {
            Log.e("文件不存在", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count / 1024;
    }










    public static String getOnlyUploadImgPath(String path) {
        String name=path.substring(path.lastIndexOf("/") + 1, path.length());
        String realpath = Url.UPLOADTEMPORARYPATH + name;
        Log.d("realpath>>>>>>>",realpath);
        return realpath;

    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
    * 旋转图片
    * @param angle
    * @param bitmap
    * @return Bitmap
    */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}
