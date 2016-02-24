package com.thinksky.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thinksky.tox.R;

import org.json.JSONObject;

/**
 * 问答详情
 * Created by jiao on 2016/2/20.
 */
public class QuestionDetailFragment extends Fragment {

    public static QuestionDetailFragment newInstance() {
        QuestionDetailFragment fragment = new QuestionDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    View rootView;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这里写
        rootView = inflater.inflate(R.layout.fragment_question_detail, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        RsenUrlUtil.execute(RsenUrlUtil.URL_WD, new RsenUrlUtil.OnHttpResultListener() {
            @Override
            public void onResult(boolean state, String result, JSONObject jsonObject) {
//                if (state) {
//                    WendaBean wendaBean = JSON.parseObject(result, WendaBean.class);
//                    listView.setAdapter(new WendaListAdapter(WendaFragment.this.getActivity(), wendaBean.getList()));
//                }
            }
        });
    }


}
