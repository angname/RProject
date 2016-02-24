package com.thinksky.tox;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nineoldandroids.view.ViewHelper;
import com.thinksky.fragment.HomeFragment;
import com.thinksky.fragment.IsseuFragment;
import com.thinksky.fragment.MallFragment;
import com.thinksky.fragment.WeiboFragment;
import com.thinksky.fragment.YlqFragment;
import com.thinksky.holder.BaseActivity;
import com.thinksky.qqsliding.adapter.LeftItemAdapter;
import com.thinksky.qqsliding.widget.DragLayout;

import java.util.ArrayList;

/**
 * Created by jiao on 2016/1/26.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {


    /**
     * 用于展示首页的Fragment
     */
    private HomeFragment homeFragment;

    /**
     * 用于展示动态的Fragment
     */
    private WeiboFragment weiboFragment;

    /**
     * 用于展示鱼医生的Fragment
     */
    private IsseuFragment issueFragment;

    /**
     * 用于展示鱼乐圈的Fragment
     */
    private YlqFragment ylqFragment;

    /**
     * 用于展示商城的Fragment
     */
    private MallFragment mallFragment;

    /**
     * 首页界面布局
     */
    private View home;

    /**
     * 动态界面布局
     */
    private View weibo;

    /**
     * 鱼乐圈界面布局
     */
    private View yulequan;

    /**
     * 鱼医生界面布局
     */
    private View yuyisheng;

    /**
     * 商城界面布局
     */
    private View mall;

    /**
     * 用于对Fragment进行管理
     */
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout; // 设置的是左侧的抽屉菜单
    private ListView mDrawerList;
    private ArrayList<String> menuLists;
    private ArrayAdapter<String> adapter;
    private ActionBarDrawerToggle mDrawerToggle;// actionBar打开关闭的
    private String mTitle;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//        initActionbar();
//        initView();
//    }

    private Toolbar toolbar;
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;
    private View mContent;
    private DragLayout dl;
    private ListView lv;
    protected void initActionbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        toolbar.setLogo(R.drawable.iconfont_gerenshezhi);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mContent = drawer_layout.getChildAt(0);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float scale = 1 - slideOffset;
                ViewHelper.setTranslationX(mContent,
                        drawerView.getMeasuredWidth() * (1 - scale));
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.drawable.iconfont_gerenshezhi);
        drawer_layout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(new LeftItemAdapter(this));
    }
    protected void initView() {
        setContentView(R.layout.activity_home);
//        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer_layout.setDrawerListener();

//        dl = (DragLayout) findViewById(R.id.dl);
//
//        lv = (ListView) findViewById(R.id.lv);
//        lv.setAdapter(new LeftItemAdapter(this));
       /* dl.setDragStateListener(new DragLayout.OnDragStatusListener() {
            @Override
            public void onClose() {
                Log.d("MainActivity", "关闭");
            }

            @Override
            public void onOpen() {
                Log.d("MainActivity", "打开");
            }

            @Override
            public void onDraging(float percent) {
                Log.d("MainActivity", "拖拽");
            }
        });*/
        home = findViewById(R.id.rb_shouye);
        weibo = findViewById(R.id.rb_weibo);
        yulequan = findViewById(R.id.rb_yulequan);
        yuyisheng = findViewById(R.id.rb_yuyisheng);
        mall = findViewById(R.id.rb_mall);
        mFragmentManager = MainActivity.this.getSupportFragmentManager();
        home.setOnClickListener(MainActivity.this);
        weibo.setOnClickListener(MainActivity.this);
        yulequan.setOnClickListener(MainActivity.this);
        yuyisheng.setOnClickListener(MainActivity.this);
        mall.setOnClickListener(MainActivity.this);
        setTabSelection(0);

//        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mContent = drawer_layout.getChildAt(0);
//        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//                float scale = 1 - slideOffset;
//                ViewHelper.setTranslationX(mContent,
//                        drawerView.getMeasuredWidth() * (1 - scale));
//            }
//        };
//        drawer_layout.setDrawerListener(drawerToggle);
//        drawerToggle.syncState();
    }
//public boolean onOptionsItemSe
    @Override
    protected void init() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_shouye:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.rb_weibo:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.rb_yulequan:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.rb_yuyisheng:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3);
//                Intent intent=new Intent(MainActivity.this,IssueActivity2.class);
//                startActivity(intent);
                break;
            case R.id.rb_mall:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(4);
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示首页，1表示动态，2表示鱼乐圈，3表示鱼医生，4表示商城。
     */
    private void setTabSelection(int index) {
//        // 每次选中之前先清楚掉上次的选中状态
//        clearSelection();
        // 开启一个Fragment事务
        mFragmentTransaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(mFragmentTransaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
//                home_image.setImageResource(R.drawable.message_selected);
//                messageText.setTextColor(Color.WH ITE);
                if (homeFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    homeFragment = new HomeFragment();
                    mFragmentTransaction.add(R.id.content, homeFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    mFragmentTransaction.show(homeFragment);
                }
                break;
            case 1:
                // 当点击了动态tab时，改变控件的图片和文字颜色
//                newsImage.setImageResource(R.drawable.news_selected);
//                newsText.setTextColor(Color.WHITE);
                if (weiboFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    weiboFragment = new WeiboFragment();
                    mFragmentTransaction.add(R.id.content, weiboFragment);
                } else {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    mFragmentTransaction.show(weiboFragment);
                }

                break;
            case 2:

                // 当点击了设置tab时，改变控件的图片和文字颜色
//                settingImage.setImageResource(R.drawable.setting_selected);
//                settingText.setTextColor(Color.WHITE);
                if (ylqFragment == null) {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    ylqFragment = new YlqFragment();
                    mFragmentTransaction.add(R.id.content, ylqFragment);
                } else {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    mFragmentTransaction.show(ylqFragment);
                }
                break;
            case 3:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
//                contactsImage.setImageResource(R.drawable.contacts_selected);
//                contactsText.setTextColor(Color.WHITE);
                if (issueFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    issueFragment = new IsseuFragment();
                    mFragmentTransaction.add(R.id.content, issueFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    mFragmentTransaction.show(issueFragment);
                }
                break;


            case 4:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
//                contactsImage.setImageResource(R.drawable.contacts_selected);
//                contactsText.setTextColor(Color.WHITE);
                if (mallFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    mallFragment = new MallFragment();
                    mFragmentTransaction.add(R.id.content, mallFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    mFragmentTransaction.show(mallFragment);
                }
                break;
        }
        mFragmentTransaction.commit();
    }

//    /**
//     * 清除掉所有的选中状态。
//     */
//    private void clearSelection() {
//        messageImage.setImageResource(R.drawable.message_unselected);
//        messageText.setTextColor(Color.parseColor("#82858b"));
//        contactsImage.setImageResource(R.drawable.contacts_unselected);
//        contactsText.setTextColor(Color.parseColor("#82858b"));
//        newsImage.setImageResource(R.drawable.news_unselected);
//        newsText.setTextColor(Color.parseColor("#82858b"));
//        settingImage.setImageResource(R.drawable.setting_unselected);
//        settingText.setTextColor(Color.parseColor("#82858b"));
//    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param mFragmentTransaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction mFragmentTransaction) {
        if (homeFragment != null) {
            mFragmentTransaction.hide(homeFragment);
        }
        if (weiboFragment != null) {
            mFragmentTransaction.hide(weiboFragment);
        }
        if (issueFragment != null) {
            mFragmentTransaction.hide(issueFragment);
        }
        if (ylqFragment != null) {
            mFragmentTransaction.hide(ylqFragment);
        }
        if (mallFragment != null) {
            mFragmentTransaction.hide(mallFragment);
        }
    }
}

