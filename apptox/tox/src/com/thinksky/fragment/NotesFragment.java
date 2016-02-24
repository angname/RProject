package com.thinksky.fragment;

import com.thinksky.tox.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotesFragment extends Fragment implements OnClickListener {

    private NotesFragmentCallBack mNotesFragmentCallBack;
    private View view;
    private Context ctx;
    private ImageView Menu;
    private TextView HomeNoValue;
    private LinearLayout load_progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_xiaozhitiao, null);
        ctx = view.getContext();
        initView();
        return view;
    }

    private void initView() {
        Menu = (ImageView) view.findViewById(R.id.Menu);
        HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);
        load_progressBar = (LinearLayout) view
                .findViewById(R.id.load_progressBar);
    }

    @Override
    public void onClick(View arg0) {

    }

    // private void createListModel() {
    // ListBottem.setVisibility(View.GONE);
    // mLinearLayout.setVisibility(View.GONE);
    // load_progressBar.setVisibility(View.VISIBLE);
    // loadflag = false;
    // mStart = 0;
    // mEnd = 5;
    // url = hotUrl + "start=" + mStart + "&end=" + mEnd;
    // ThreadPoolUtils.execute(new HttpGetThread(hand, url));
    // }

    // private class MainListOnItemClickListener implements OnItemClickListener
    // {
    // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    // long arg3) {
    // Intent intent = new Intent(ctx, AshamedDetailActivity.class);
    // Bundle bund = new Bundle();
    // bund.putSerializable("AshamedInfo", list.get(arg2 - 1));
    // intent.putExtra("value", bund);
    // startActivity(intent);
    // }
    // }

    public void setCallBack(NotesFragmentCallBack mNotesFragmentCallBack) {
        this.mNotesFragmentCallBack = mNotesFragmentCallBack;
    }

    public interface NotesFragmentCallBack {
        public void callback(int flag);
    }


}
