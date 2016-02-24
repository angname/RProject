package com.tox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络图片下载器
 * Created by Administrator on 2015/4/13 0013.
 */
public class ImageLoader {

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private static LruCache<String, Bitmap> mMemoryCache;

    /**
     * ImageLoader的实例。
     */
    private static ImageLoader mImageLoader;

    private ImageLoader() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 2;
        // 设置图片缓存大小为程序最大可用内存的1/2
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 获取ImageLoader的实例。
     *
     * @return ImageLoader的实例。
     */
    public static ImageLoader getInstance() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader();
        }
        return mImageLoader;
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     *质量压缩
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            Log.e("图片压缩率widthRatio>>",widthRatio+"");
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
     *
     * @param imageUrl 图片的URL地址
     * @param columnWidth 图片的显示宽度
     * @return 加载到内存的图片。
     */
    public Bitmap loadImage(String imageUrl,int columnWidth) {
        File imageFile = new File(getImagePath(imageUrl));
        if (!imageFile.exists()) {
            downloadImage(imageUrl, columnWidth);
        }
        if (imageUrl != null) {
            Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                    imageFile.getPath(), columnWidth);
            if (bitmap != null) {
                addBitmapToMemoryCache(imageUrl, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 将图片下载到SD卡缓存起来。
     *
     * @param imageUrl 图片的URL地址。
     *@param columnWidth 图片的显示宽度
     */
    public void downloadImage(String imageUrl,int columnWidth) {
        HttpURLConnection con = null;
        FileOutputStream fos;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File imageFile = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setDoInput(true);
            //get请求不需要
//                con.setDoOutput(true);
            bis = new BufferedInputStream(con.getInputStream());
            imageFile = new File(getImagePath(imageUrl));
            fos = new FileOutputStream(imageFile);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            int length;
            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (imageFile != null) {
            Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                    imageFile.getPath(), columnWidth);
            if (bitmap != null) {
                addBitmapToMemoryCache(imageUrl, bitmap);
            }
        }
    }

    /**
     * 获取图片的本地存储路径。
     *
     * @param imageUrl
     *            图片的URL地址。
     * @return 图片的本地存储路径。
     */
    public String getImagePath(String imageUrl) {
        String imageName;
        int lastSlashIndex;
        if (imageUrl.contains("?image")){
            imageName = imageUrl.substring(0,imageUrl.lastIndexOf("?image"));
            lastSlashIndex = imageName.lastIndexOf("/");
            imageName = imageName.substring(lastSlashIndex + 1);
        }else {
            lastSlashIndex = imageUrl.lastIndexOf("/");
            imageName = imageUrl.substring(lastSlashIndex + 1);
        }
        String imageDir = Environment.getExternalStorageDirectory()
                .getPath() + "/tox/photos/";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;
        Log.e("imagePath>>>", imagePath);
        return imagePath;
    }
}