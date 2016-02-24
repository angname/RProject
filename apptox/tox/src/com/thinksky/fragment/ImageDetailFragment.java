package com.thinksky.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.thinksky.tox.R;
import com.thinksky.utils.LoadImg;
import com.tox.BaseFunction;
import com.tox.ToastHelper;
import com.tox.Url;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private LinearLayout saveImage;
    private PhotoViewAttacher mAttacher;
    private Context ctx;
    private LoadImg loadImg;

    private KJBitmap kjBitmap = KJBitmap.create();

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        ctx = v.getContext();
        loadImg = new LoadImg(ctx);
        mImageView = (ImageView) v.findViewById(R.id.image);
        saveImage = (LinearLayout) v.findViewById(R.id.save_image);

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mImageView.getDrawable() != null) {
                    String saveImagePath = (String) mImageView.getTag();
                    Bitmap saveBitmap = mImageView.getDrawingCache();
                    File file = new File(Url.SAVEIMAGE);
                    file.mkdirs();
                    saveImagePath = saveImagePath.substring(saveImagePath.lastIndexOf("/", saveImagePath.lastIndexOf("/") - 1) + 1, saveImagePath.length());
                    saveImagePath = Url.SAVEIMAGE + saveImagePath.replaceAll("[/]", "");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(saveImagePath);
                        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        ToastHelper.showToast("保存到tox/SaveImage文件", ctx);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        progressBar.setVisibility(View.VISIBLE);
        super.onActivityCreated(savedInstanceState);
        final String largeImageUrl = mImageUrl.replace("_100_100", "");
        mImageView.setTag(largeImageUrl);
        BaseFunction.showImage(ctx, mImageView,mImageUrl, loadImg, Url.IMGTYPE_WEIBO);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 5000);
        if (BaseFunction.fileExists(largeImageUrl)) {
            BaseFunction.showImage(ctx, mImageView, largeImageUrl, loadImg, Url.IMGTYPE_BIG);
            progressBar.setVisibility(View.GONE);
        } else {

            final String url=(Url.USERHEADURL+largeImageUrl).replace("com//","com/").replace("cn//","cn/").replace("com///","com/");
            // ToastHelper.showToast(url,ctx);
            final Bitmap bitmap=loadImg.loadImage(mImageView,url,new LoadImg.ImageDownloadCallBack() {
                @Override
                public void onImageDownload(ImageView imageView, Bitmap bitmap) {
                    progressBar.setVisibility(View.GONE);
                    mImageView.setImageBitmap(bitmap);
                    mAttacher.update();
                    if(bitmap!=null){
                        BaseFunction.saveBitmapToLocal(bitmap,url,ctx,0);
                    }
                }
            });
          /*  FinalHttp finalHttp = new FinalHttp();
            HttpHandler httpHandler = finalHttp.download((Url.USERHEADURL + largeImageUrl).replace("com///","com/").replace("com//","com/"), Environment.getExternalStorageDirectory() + "/tox/" + largeImageUrl.substring(largeImageUrl.lastIndexOf("/") + 1, largeImageUrl.length()), new AjaxCallBack<File>() {
                @Override
                public void onStart() {
                    super.onStart();

                }

                @Override
                public void onSuccess(File file) {
                    super.onSuccess(file);

                    progressBar.setVisibility(View.GONE);
                    mImageView.setImageBitmap(BitmapUtiles.loadBitmap(file.getPath(), 2));
                    mAttacher.update();

                }

                @Override
                public void onLoading(long count, long current) {
                    super.onLoading(count, current);
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    ToastHelper.showToast(strMsg, ctx);
                    Log.e(strMsg, "");
                }
            });*/
        }

    }
}


