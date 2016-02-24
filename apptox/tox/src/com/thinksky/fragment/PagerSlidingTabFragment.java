package com.thinksky.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thinksky.PagerSlidingTabStrip.PagerSlidingTabStrip;
import com.thinksky.tox.R;

/**
 * Created by Administrator on 2015/1/20 0020.
 */
public class PagerSlidingTabFragment extends Fragment {

    private static final String ARG_POSTION="position";
    private int position;
    public static PagerSlidingTabFragment newInstance(int position){
        PagerSlidingTabFragment f=new PagerSlidingTabFragment();
        Bundle b=new Bundle();
        b.putInt(ARG_POSTION, position);
        f.setArguments(b);
        return f;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position=getArguments().getInt(ARG_POSTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());
        TextView v = new TextView(getActivity());
        params.setMargins(margin, margin, margin, margin);
        v.setLayoutParams(params);
        v.setLayoutParams(params);
        v.setGravity(Gravity.CENTER);
        v.setBackgroundResource(R.drawable.background_card);
        v.setText("CARD " + (position + 1));
        fl.addView(v);
        return fl;

    }

}
