package com.thinksky.tox;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.thinksky.adapter.GroupAdapter;
import com.thinksky.info.ImageBean;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ScanPhotoActivity extends Activity {
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> list = new ArrayList<ImageBean>();
    private final static int SCAN_OK = 1;
    private ProgressDialog mProgressDialog;
    private GroupAdapter adapter;
    private GridView mGroupGridView;
    private List<String> selectedImag = new ArrayList<String>();
    private FinalBitmap finalBitmap;
    private  List<String> lists;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    //关闭进度条
                    mProgressDialog.dismiss();

                    adapter = new GroupAdapter(ScanPhotoActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView, finalBitmap);
                    mGroupGridView.setAdapter(adapter);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_scan_main);
        finalBitmap = FinalBitmap.create(this);
        finalBitmap.configBitmapLoadThreadSize(5);
        finalBitmap.configMemoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 1024));
        finalBitmap.configLoadingImage(R.drawable.friends_sends_pictures_no);
        mGroupGridView = (GridView) findViewById(R.id.main_grid);
        getImages();
        mGroupGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());
                Intent mIntent = new Intent(ScanPhotoActivity.this, ShowImageActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
                startActivityForResult(mIntent, 10);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 999) {
            lists = data.getStringArrayListExtra("data");
            Log.e("size>>>", lists.size() + "");
            if (lists.size()>9){
                Toast.makeText(ScanPhotoActivity.this,"图片不能超过9张哟",Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lists.size(); i++) {
                selectedImag.add(lists.get(i));
                Log.e("选择图片", selectedImag.get(i));
            }
        }
       Back();
    }
    public void Back(){
        finalBitmap.onPause();
        finalBitmap.onDestroy();

        Intent data = new Intent();
        data.putStringArrayListExtra("data", (ArrayList<String>) selectedImag);
        setResult(99, data);
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        finalBitmap.onPause();
//        finalBitmap.onDestroy();
//
//        Intent data = new Intent();
//        data.putStringArrayListExtra("data", (ArrayList<String>) selectedImag);
//        setResult(99, data);
//        finish();
//    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        //显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ScanPhotoActivity.this.getContentResolver();

                //只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED
                );

                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();

                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }

                mCursor.close();
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();

    }


    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param mGruopMap
     * @return
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片

            list.add(mImageBean);
        }

        return list;

    }


}
