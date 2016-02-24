package com.thinksky.tox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thinksky.net.IsNet;
import com.thinksky.utils.MyJson;
import com.tox.ToastHelper;
import com.tox.Url;
import com.tox.WeiboApi;
import com.zxing.activity.CallActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
private TextView tv_splash;
    protected final int SPLASH_TIME = 2000;
    private WeiboApi weiboApi = new WeiboApi();
    private SharedPreferences sp = null;
    private MyJson myJson = new MyJson();
    private int month, day;
    Timer mTimer;
    private Context mContext;
    AlertDialog.Builder alertDialog;
    AlertDialog dlg;
    private ViewPager mViewPager;

    private int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();

    }

    private void init() {
        setContentView(R.layout.activity_splash);
//        tv_splash = (TextView) findViewById(R.id.tv_splash);
        mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);

//        tv_splash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });



        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.whats1, null);
        View view2 = mLi.inflate(R.layout.whats2, null);
        View view3 = mLi.inflate(R.layout.whats3, null);
        View view4 = mLi.inflate(R.layout.whats4, null);



        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);


        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }


            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
    }

//    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
//        @Override
//        public void onPageSelected(int arg0) {
//            switch (arg0) {
//                case 0:
//                    mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//                    mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
//                    break;
//                case 1:
//                    mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//                    mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
//                    mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
//                    break;
//                case 2:
//                    mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//                    mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
//                    mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page));
//                    break;
//                case 3:
//                    mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
//
//                    mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
//                    break;
//
//            }
//            currIndex = arg0;
//            //animation.setFillAfter(true);
//            //animation.setDuration(300);
//            //mPageImg.startAnimation(animation);
//        }

//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
//    }

    public void startbutton(View v) {
        weiboApi.setHandler(handler);
        mContext = SplashActivity.this;
        SharedPreferences sp = getSharedPreferences("userInfo", 0);
        sp.edit().putString("version", "0.1.1").commit();
        Url.sp = sp;
        Url.context = getApplicationContext();
        if (IsNet.IsConnect()) {
//            is2getParameters();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //判断是否是首次登入
                    SharedPreferences startTimes = getSharedPreferences("Parameters", 1);
                    int times = startTimes.getInt("times", 1);

                    Log.e("TIME>>>>>>>>>>>>>>", times + "");
                    SharedPreferences.Editor editor = startTimes.edit();
                    if (times == 0) {
                        editor.putInt("times", 1);
                        editor.commit();
                        Log.e("AfterSave>>>>>>>>>>", startTimes.getInt("times", 0) + "");
                        Intent intent = new Intent(SplashActivity.this, CallActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else {
                        ++times;
                        editor.putInt("times", times);
                        editor.commit();
                        Log.e("AfterSave>>>>>>>>>>", startTimes.getInt("times", 0) + "");
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                }
            }, SPLASH_TIME);
        } else {
            alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("提示").setMessage("请开启数据网络！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SplashActivity.this.finish();
                }
            });
            dlg = alertDialog.create();
            dlg.show();
            //周期性的检测网络状况，如果开启则自动载入
            mTimer = new Timer(true);
            mTimer.schedule(new TimerTask() {
                public void run() {
                    if (IsNet.IsConnect()) {
                        new Handler(mContext.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //判断是否是首次登入
                                SharedPreferences startTimes = getSharedPreferences("Parameters", 1);
                                int times = startTimes.getInt("times", 1);

                                Log.e("TIME>>>>>>>>>>>>>>", times + "");
                                SharedPreferences.Editor editor = startTimes.edit();
                                if (times == 0) {
                                    editor.putInt("times", 1);
                                    editor.commit();
                                    Log.e("AfterSave>>>>>>>>>>", startTimes.getInt("times", 0) + "");
                                    Intent intent = new Intent(SplashActivity.this, CallActivity.class);
                                    startActivity(intent);
                                    if (dlg != null && dlg.isShowing()) {
                                        dlg.dismiss();
                                    }
                                    SplashActivity.this.finish();
                                    mTimer.cancel();
                                } else {
                                    ++times;
                                    editor.putInt("times", times);
                                    editor.commit();
                                    Log.e("AfterSave>>>>>>>>>>", startTimes.getInt("times", 0) + "");
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    if (dlg != null && dlg.isShowing()) {
                                        dlg.dismiss();
                                    }
                                    SplashActivity.this.finish();
                                    mTimer.cancel();
                                }
                            }
                        });

                    }
                }
            }, 2000, 1500);
        }
    }


    /*private void is2getParameters() {
        SharedPreferences sp = getSharedPreferences("Parameters", 0);
    Calendar calendar = Calendar.getInstance();
    month = calendar.get(Calendar.MONTH) + 1;
    day = calendar.get(Calendar.DAY_OF_MONTH);
    if (month == sp.getInt("month", 0) && day == sp.getInt("day", 0)) {
        Url.WEIBOWORDS = sp.getString("weiboWordsLimit", "200");
    } else {
        weiboApi.getWeiboWordsLimit();
    }
}*/

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                try {
                    JSONObject jsonObject = new JSONObject((String) msg.obj);
                    SharedPreferences sharedPreferences = getSharedPreferences("Parameters", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("weiboWordsLimit", jsonObject.getString("limit"));
                    editor.putInt("month", Calendar.getInstance().get(Calendar.MONTH) + 1);
                    editor.putInt("day", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    editor.commit();
                } catch (JSONException E) {

                }
            } else {
                ToastHelper.showToast("错误代码" + msg.what, SplashActivity.this);
            }

        }
    };

}
