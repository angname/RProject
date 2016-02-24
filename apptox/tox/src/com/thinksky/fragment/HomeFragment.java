package com.thinksky.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksky.tox.R;

/**
 * Created by jiao on 2016/1/27.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    @Override
    public void onClick(View view) {

    }
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_home, null);
        initView();
        return view;}

    private void initView() {

    }
}
