package com.thinksky.tox;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.thinksky.redefine.MyScrollView;


public class ForumActivity3 extends ActionBarActivity implements MyScrollView.OnScrollListener {
    /**
     * 自定义的MyScrollView
     */
    private MyScrollView myScrollView;
    /**
     * 在MyScrollView里面的购买布局
     */
    private LinearLayout mBuyLayout;
    /**
     * 位于顶部的购买布局
     */
    private LinearLayout mTopBuyLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_activity3);
        myScrollView=(MyScrollView)findViewById(R.id.scrollView);
        mBuyLayout=(LinearLayout)findViewById(R.id.stick);
        mTopBuyLayout=(LinearLayout)findViewById(R.id.top_stick);
        myScrollView.setOnScrollListener(this);
        findViewById(R.id.parent_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onScroll(myScrollView.getScrollY());
                //Log.e("myScrollView.getScrollY()>>>>"+myScrollView.getScrollY(),"");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forum_activity3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScroll(int scrollY) {

        int mBuyLayout2ParentTop = Math.max(scrollY, mBuyLayout.getTop());

        Log.e("scrollY>>>>"+scrollY,"");
        Log.e("mBuyLayout.getTop()>>>>"+mBuyLayout.getTop(),"");
//        Log.e("mTopBuyLayout.getHeight()>>>>"+mTopBuyLayout.getHeight(),"mBuyLayout.getHeight()>>>>"+mBuyLayout.getHeight());
        mTopBuyLayout.layout(0, mBuyLayout2ParentTop, mTopBuyLayout.getWidth(), mBuyLayout2ParentTop + mTopBuyLayout.getHeight());
    }
}
