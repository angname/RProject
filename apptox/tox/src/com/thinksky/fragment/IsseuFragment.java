package com.thinksky.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksky.tox.R;
import com.thinksky.tox.SegmentControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiao on 2016/1/27.
 */
public class IsseuFragment extends Fragment implements View.OnClickListener {
    private SegmentControl mSegmentControl2;
    private ViewPager mPager;
    private static final String ARG_PARAM1 = "param1";
    @Override
    public void onClick(View view) {

    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_yuyisheng, null);
        initView();
        return view;
    }
//交给你了
    private void initView() {
        mSegmentControl2 = (SegmentControl) view.findViewById(R.id.segment_control2);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        List<Fragment> fragments = new ArrayList<>();
//        WendaFragment wendaFragment = new WendaFragment();
//        ZhuanjiFragment zhuanjiFragment = new ZhuanjiFragment();
//        BaikeFragment baikeFragment = new BaikeFragment();
//        IssueFragment issueFragment = new IssueFragment();
        fragments.add(WendaFragment.newInstance("label1"));
        fragments.add(ZhuanjiFragment.newInstance("label2"));
//        fragments.add(IssueFragment.newInstance("问答"));
        fragments.add(BaikeFragment.newInstance("label3"));//我就写这一个Fragment。
        mPager.setAdapter(new PagerAdapter(getChildFragmentManager(), fragments));
        mSegmentControl2.setSelectedTextColor(getResources().getColor(android.R.color.black));
        mSegmentControl2.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                mPager.setCurrentItem(index);
            }
        });
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mSegmentControl2.setCurrentIndex(position);
                if (position % 2 != 0) {
                    mSegmentControl2.setSelectedBackgroundColors(getResources().getColor(android.R.color.darker_gray));
                } else {
                    mSegmentControl2.setSelectedBackgroundColors(0xff009688);
                }
            }
        });
//        FragmentTransaction mFragmentTransaction = mFragmentManager
//                .beginTransaction();
//        mFragmentTransaction.replace(R.id.wenda, zhuanjiFragment);
//        mFragmentTransaction.commit();
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
