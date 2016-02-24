package com.thinksky.tox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.fragment.PagerSlidingTabFragment;


public class ForumActivity extends FragmentActivity {
    PagerSlidingTabStrip tabs;
    ViewPager viewPager;
    String[] titles={"gggggg","qqqqqq","bbbbbb"};
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView(){
        setContentView(R.layout.activity_forum);
        dm=getResources().getDisplayMetrics();
        viewPager=(ViewPager)findViewById(R.id.pager);
        tabs=(PagerSlidingTabStrip)findViewById(R.id.tabs);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(),titles));
        tabs.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forum, menu);
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

    public class MyAdapter extends FragmentPagerAdapter{
        String[] _titles;
        public MyAdapter(FragmentManager fm,String[] titles) {
            super(fm);
            this._titles=titles;
        }

        @Override
        public int getCount() {
            return _titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return PagerSlidingTabFragment.newInstance(position);
        }
    }
}
