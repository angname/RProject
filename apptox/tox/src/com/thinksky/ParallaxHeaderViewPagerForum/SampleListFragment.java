package com.thinksky.ParallaxHeaderViewPagerForum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thinksky.adapter.PostAdapter;
import com.thinksky.info.PostInfo;
import com.thinksky.tox.PostDetailActivity;
import com.thinksky.tox.R;

import net.tsz.afinal.FinalBitmap;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;
import java.util.List;

public class SampleListFragment extends ScrollTabHolderFragment implements OnScrollListener {

        private static final String ARG_POSITION = "position";
        private int mMinHeaderTranslation;
        private ListView mListView;
        private ArrayList<String> mListItems;
        private View mPlaceHolderView;
        private View mHeader;
        private View view;
        private int mPosition;
        private PostAdapter mAdapter;
        private Context ctx,forumCxt;
        private KJBitmap kjBitmap;
        private FinalBitmap finalBitmap;
        private SampListCallBack sampListCallBack;
        private  LinearLayout mEditBox;
        private int page=2;
        private List<PostInfo> mPostInfos=new ArrayList<PostInfo>();
        private Button ListBottem=null;
        private ProgressBar mAddMoreProgressBar;
        public boolean firstInitData=true;
        private boolean isInitView=false;
        public static Fragment newInstance(int position,SampListCallBack sampListCallBack,LinearLayout mEditBox) {
            SampleListFragment f = new SampleListFragment();
            f.setSampListCallBack(sampListCallBack,mEditBox);
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);

            return f;
        }

        private void setSampListCallBack(SampListCallBack sampListCallBack,LinearLayout mEditBox){
            this.sampListCallBack=sampListCallBack;
            this.mEditBox=mEditBox;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.e("onCreate()", "");
            super.onCreate(savedInstanceState);
            mPosition = getArguments().getInt(ARG_POSITION);
            mListItems = new ArrayList<String>();
            for (int i = 1; i <= 100; i++) {
                mListItems.add(i + ". item - currnet page: " + (mPosition + 1));
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.e("onCreateView()","");
            view = inflater.inflate(R.layout.fragment_list, null);
            ctx=view.getContext();
           /* finalBitmap = FinalBitmap.create(v.getContext());
            finalBitmap.configMemoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 1024));
            finalBitmap.configBitmapLoadThreadSize(30);
            kjBitmap = KJBitmap.create();
            List<PostInfo> postInfo=new ArrayList();
            mAdapter=new PostAdapter(postInfo,ctx,kjBitmap,finalBitmap);*/
            mListView = (ListView) view.findViewById(R.id.listView);

            View placeHolderView = inflater.inflate(R.layout.fake_header, mListView, false);
            mListView.addHeaderView(placeHolderView);
            /*mListView.setAdapter(mAdapter);*/
            return view;
        }

        public int getScrollY() {
            View c = mListView.getChildAt(0);
            if (c == null) {
                return 0;
            }

            int firstVisiblePosition = mListView.getFirstVisiblePosition();
            int top = c.getTop();

            int headerHeight = 0;
            if (firstVisiblePosition >= 1) {
                headerHeight = mPlaceHolderView.getHeight();
            }

            return -top + firstVisiblePosition * c.getHeight() + headerHeight;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.e("onActivityCreated()","");
            mListView.setOnScrollListener(this);
            finalBitmap = FinalBitmap.create(view.getContext());
            finalBitmap.configMemoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 1024));
            finalBitmap.configBitmapLoadThreadSize(30);
            kjBitmap = KJBitmap.create();
            List<PostInfo> postInfos=new ArrayList();
            PostInfo postInfo=new PostInfo();
            for (int i=0;i<=10;i++){
                postInfos.add(postInfo);
            }
            //设置底部加载
            ListBottem = new Button(ctx);
            if(isAdded()) {
                ListBottem.setBackgroundColor(getResources().getColor(R.color.forumAdd));
                ListBottem.setTextColor(getResources().getColor(R.color.black));
            }
            ListBottem.setText("点击加载更多");
//            ListBottem.setPadding(20,10,20,10);
            ListBottem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sampListCallBack.callback(19,page);
                    listBottom();
                }
            });
            mAddMoreProgressBar = new ProgressBar(ctx);
            mAddMoreProgressBar.setIndeterminate(false);
            if(isAdded()) {
                mAddMoreProgressBar.setBackgroundColor(getResources().getColor(R.color.forumAdd));
                mAddMoreProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bg));
            }
            mAddMoreProgressBar.setVisibility(View.GONE);
            /*mListView.addFooterView(ListBottem, null, false);
            ListBottem.setVisibility(View.VISIBLE);*/
            mAdapter=new PostAdapter(mPostInfos,ctx,mEditBox,kjBitmap,finalBitmap);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new MyListItemClickListener());
            isInitView=true;
            //mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item, android.R.id.text1, mListItems));
        }


        private class MyListItemClickListener implements AdapterView.OnItemClickListener{

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ToastHelper.showToast(position+"",ctx);
                if(position<=0)
                {
                   return;
                }
                Intent intent=new Intent(ctx, PostDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("postInfo",mPostInfos.get(position-1));
                intent.putExtra("post",bundle);
                startActivity(intent);
            }
        }

        @Override
        public void adjustScroll(int scrollHeight) {
            if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
                return;
            }
            mListView.setSelectionFromTop(1, scrollHeight);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mScrollTabHolder != null)
                mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
//            mEditBox.setVisibility(View.GONE);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // nothing
            if(scrollState==SCROLL_STATE_TOUCH_SCROLL){
                mEditBox.setVisibility(View.GONE);
            }
        }

        public interface SampListCallBack{
            public void callback(int flag,int page);
        }

        private void initView(){

            mListView.setOnScrollListener(this);
            finalBitmap = FinalBitmap.create(view.getContext());
            finalBitmap.configMemoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 1024));
            finalBitmap.configBitmapLoadThreadSize(30);
            kjBitmap = KJBitmap.create();
            List<PostInfo> postInfos=new ArrayList();
            PostInfo postInfo=new PostInfo();
            for (int i=0;i<=10;i++){
                postInfos.add(postInfo);
            }
            //设置底部加载
            ListBottem = new Button(ctx);
            if(isAdded()) {
                ListBottem.setBackgroundColor(getResources().getColor(R.color.forumAdd));
                ListBottem.setTextColor(getResources().getColor(R.color.black));
            }
            ListBottem.setText("点击加载更多");
//            ListBottem.setPadding(20,10,20,10);
            ListBottem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sampListCallBack.callback(19,page);
                    listBottom();
                }
            });
            mAddMoreProgressBar = new ProgressBar(ctx);
            mAddMoreProgressBar.setIndeterminate(false);
            if(isAdded()) {
                mAddMoreProgressBar.setBackgroundColor(getResources().getColor(R.color.forumAdd));
                mAddMoreProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bg));
            }
            mAddMoreProgressBar.setVisibility(View.GONE);
            /*mListView.addFooterView(ListBottem, null, false);
            ListBottem.setVisibility(View.VISIBLE);*/
            mAdapter=new PostAdapter(mPostInfos,ctx,mEditBox,kjBitmap,finalBitmap);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new MyListItemClickListener());
        }

        public void updatePosts(List<PostInfo> list,boolean addFlag){

            if(!isInitView){
                initView();
            }

            firstInitData=false;
            this.getArguments().getInt(ARG_POSITION);
            //ToastHelper.showToast("fragment postion"+this.getArguments().getInt(ARG_POSITION),ctx);
            if(!addFlag){
                mPostInfos.removeAll(mPostInfos);
                page =2;
            }else{
                page++;
            }
            for (PostInfo post:list){
                mPostInfos.add(post);
            }

            mAdapter.notifyDataSetChanged();

            if(list.size()<10 && list.size()>0){
                mListView.removeFooterView(mAddMoreProgressBar);
                mListView.removeFooterView(ListBottem);
                Button noBtn=new Button(ctx);
                noBtn.setText("没有更多了111");
                if(isAdded()) {
                    noBtn.setBackgroundColor(getResources().getColor(R.color.forumAdd));
                    noBtn.setTextColor(getResources().getColor(R.color.black));
                }
                noBtn.setPadding(20,10,20,10);
//                mListView.addFooterView(noBtn);

            }else if(list.size()>=10){
                mListView.removeFooterView(mAddMoreProgressBar);
                mListView.removeFooterView(ListBottem);
                mListView.addFooterView(ListBottem);
            }
            if(!addFlag){
                mListView.setSelection(0);
            }
        }

        private void listBottom() {
            mAddMoreProgressBar.setVisibility(View.VISIBLE);
            mListView.removeFooterView(ListBottem);
            mListView.addFooterView(mAddMoreProgressBar, null, false);
        }
}