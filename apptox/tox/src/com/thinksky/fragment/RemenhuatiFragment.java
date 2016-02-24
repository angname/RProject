package com.thinksky.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinksky.tox.R;


public class RemenhuatiFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;


    public static RemenhuatiFragment newInstance(String param1) {
        RemenhuatiFragment fragment = new RemenhuatiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public RemenhuatiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mall_mall, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);

        return view;
    }


}
