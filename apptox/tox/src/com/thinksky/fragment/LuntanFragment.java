package com.thinksky.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksky.tox.ForumActivity2;
import com.thinksky.tox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LuntanFragment extends Fragment {
    private static final String TAG = "";
    private List<String> pictureListUrls;
    View rootView;
    ListView listView;
    private Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //这里写
        rootView = inflater.inflate(R.layout.fragment_luntan, container, false);
        listView = (ListView) rootView.findViewById(R.id.myListView);
        ctx = rootView.getContext();
//        if (pictureListUrls != null) {
//            HomePicHolder homePicHolder = new HomePicHolder();
//            homePicHolder.setDate(pictureListUrls);
//            rootView.add
//        }

//        listView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getContext(), IssueDetail.class);
//                startActivity(intent);
//            }
//        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        RsenUrlUtil.execute(this.getActivity(), RsenUrlUtil.URL_LT, new RsenUrlUtil.OnHttpResultListener() {
            @Override
            public void onResult(boolean state, String result, JSONObject jsonObject) {
                if (state) {
                    ArrayList<LTBean> beans = parseJson(jsonObject);
                    listView.setAdapter(new LTAdapter(getActivity(), beans));
                }
            }
        });
    }

    public static ArrayList<LTBean> parseJson(JSONObject object) {
        ArrayList<LTBean> beans = new ArrayList<>();
        if (object != null) {
            try {
                JSONArray array = object.getJSONArray("list");
                for (int i = 0; i < array.length(); i++) {
                    LTBean bean = new LTBean();
                    JSONObject jsonObject = array.getJSONObject(i);
                    bean.title = jsonObject.getString("title");//title 赋值
                    //其他字段。。。赋值

                    // TODO: 2016/2/17

                    ArrayList<ForumBean> forumBeens = new ArrayList<>();
                    JSONArray issueList = jsonObject.getJSONArray("forums");
                    for (int j = 0; j < issueList.length(); j++) {
                        JSONObject issueListJSONObject = issueList.getJSONObject(i);
                        ForumBean issueBean = new ForumBean();
                        issueBean.title = issueListJSONObject.getString("title");// issue title 赋值
                        issueBean.logo = issueListJSONObject.getString("logo");// issue cover_url 赋值
                        issueBean.total_count = issueListJSONObject.getString("total_count");//total_count赋值
                        //其他字段。。。赋值
                        // TODO: 2016/2/17

                        forumBeens.add(issueBean);
                    }

                    bean.forumList = forumBeens;
                    beans.add(bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return beans;
    }

    private ArrayList<String> mDatas;

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;


    public static LuntanFragment newInstance(String param1) {
        LuntanFragment fragment = new LuntanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


//    @Override
//    public IDrawerPresenter getInstance() {
//        return this;
//    }
//
//    @Override
//    public void dispatchEvent(int totalPages, int currentPage) {
//        Log.v(TAG, "~~~~dispatchEvent currentPage:" + currentPage);
//        Message msg = Message.obtain();
//        msg.what = MSG_DRAWER_UPDATE_PAGE_LAYOUT;
//        msg.arg1 = totalPages;
//        msg.arg2 = currentPage;
//        handler.sendMessage(msg);
//    }
//
//    protected class PageItemImageView extends ImageView {
//        public PageItemImageView(Context context) {
//            super(context);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.icon_page_normal);
//            this.setImageBitmap(bitmap);
//        }
//
//        public void updateDrawerPageLayout(int total_pages, int sel_page) {
//            Log.e(TAG, "~~~updateBooksPageLayout total_pages:" + total_pages + ",sel_page:" + sel_page);
//            layout_pagenumber.removeAllViews();
//            if (total_pages <= 0 || sel_page < 0 || sel_page >= total_pages) {
//                Log.e(TAG, "total_pages or sel_page is outofrange.");
//                return;
//            }
//            for (int i = 0; i < total_pages; i++) {
//                if (i != 0) {
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                    params.setMargins(5, 0, 0, 0);
//                    layout_pagenumber.addView(new PageItemImageView(this), params);
//                } else {
//                    layout_pagenumber.addView(new PageItemImageView(this));
//                }
//            }
//            PageItemImageView selItem = (PageItemImageView) layout_pagenumber.getChildAt(sel_page);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_page_selected);
//            selItem.setImageBitmap(bitmap);
//        }
//    }


    public static class LTAdapter extends BaseAdapter {

        Context context;
        ArrayList<LTBean> beans;

        public LTAdapter(Context context, ArrayList<LTBean> beans) {
            this.context = context;
            this.beans = beans;
        }

        @Override
        public int getCount() {
            if (beans == null) {
                return 0;
            }
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaikeFragment.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_luntan_adapter_item, parent, false);
                viewHolder = new BaikeFragment.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BaikeFragment.ViewHolder) convertView.getTag();
            }


            LTBean bean = beans.get(position);
            ((TextView) viewHolder.itemView.findViewById(R.id.title)).setText(bean.title);
            ((GridView) viewHolder.itemView.findViewById(R.id.gridView))
                    .setAdapter(new LTGridAdapter(context, bean.forumList));
            ((ViewPager) viewHolder.itemView.findViewById(R.id.viewPager))
                    .setAdapter(new ForumsPagerAdapter(context, bean.forumList));
            return convertView;
        }
    }

    /*forums 模块适配器*/
    public static class ForumsPagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<ForumBean> beans;
        int pagerSize = 4;//每个页面显示item的数量
        List<GridView> gridViewList;

        public ForumsPagerAdapter(Context context, ArrayList<ForumBean> beans) {
            this.context = context;
            this.beans = beans;

            gridViewList = new ArrayList<>();
            for (int i = 0; i < getCount(); i++) {
                GridView gridView = new GridView(context);
                gridView.setNumColumns(2);//固定2列，任意修改界面会错乱；
                gridViewList.add(gridView);
            }
        }

        @Override
        public int getCount() {
            if (beans == null) {
                return 0;
            }
            return (beans.size() % pagerSize) == 0 ?
                    beans.size() / pagerSize :
                    beans.size() / pagerSize + 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = gridViewList.get(position);
            gridView.setAdapter(new LTSubGridAdapter(context, getBeansWidthPosition(position)));

            container.addView(gridView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
//            container.setBackgroundColor(Color.RED);
            return gridView;
        }

        private ArrayList<ForumBean> getBeansWidthPosition(int position) {
            ArrayList<ForumBean> beans = new ArrayList<>();
            for (int i = position; i < position + pagerSize; i++) {
                if (i<this.beans.size()) {
                beans.add(this.beans.get(i));
                }
            }
            return beans;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView(gridViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.hashCode() == view.hashCode();
        }
    }

    public static class LTSubGridAdapter extends BaseAdapter {

        Context context;
        ArrayList<ForumBean> beans;
        List<View> itemViewList;

        public LTSubGridAdapter(Context context, ArrayList<ForumBean> beans) {
            this.context = context;
            this.beans = beans;

            itemViewList = new ArrayList<>();
            for (int i = 0; i < getCount(); i++) {
                LTSubViewHolder subViewHolder = createItemLayout();
                subViewHolder.rootLayout.setTag(subViewHolder);
                itemViewList.add(subViewHolder.rootLayout);
            }
        }

        //动态创建布局
        private LTSubViewHolder createItemLayout() {
            LTSubViewHolder viewHolder = new LTSubViewHolder();
            //
            viewHolder.rootLayout = new LinearLayout(context);
            viewHolder.userLogo = new ImageView(context);
            viewHolder.titleView = new TextView(context);
            viewHolder.countView = new TextView(context);

            viewHolder.rootLayout.setOrientation(LinearLayout.VERTICAL);
            viewHolder.rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            viewHolder.rootLayout.addView(viewHolder.userLogo, new LinearLayout.LayoutParams(170, 108));
            viewHolder.rootLayout.addView(viewHolder.titleView,new LinearLayout.LayoutParams(-2, -2));
            viewHolder.rootLayout.addView(viewHolder.countView,new LinearLayout.LayoutParams(-2, -2));

            return viewHolder;
        }

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = itemViewList.get(position);
            ForumBean bean = beans.get(position);
            LTSubViewHolder viewHolder = (LTSubViewHolder) view.getTag();
            ImageLoader.getInstance().displayImage(RsenUrlUtil.URL_BASE+bean.logo,
                    viewHolder.userLogo);
            viewHolder.titleView.setText(bean.title);
            viewHolder.countView.setText("帖数："+bean.total_count);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ForumActivity2.class);
                    context.startActivity(intent);
                }
            });
            return view;
        }
    }

    public static class LTSubViewHolder {
        public LinearLayout rootLayout;
        public ImageView userLogo;
        public TextView titleView;
        public TextView countView;
    }

    public static class LTGridAdapter extends BaseAdapter {

        Context context;
        ArrayList<ForumBean> beans;

        public LTGridAdapter(Context context, ArrayList<ForumBean> beans) {
            this.context = context;
            this.beans = beans;
        }

        @Override
        public int getCount() {
            if (beans == null) {
                return 0;
            }
            return beans.size() > 3 ? 3 : beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaikeFragment.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_luntan_item, parent, false);
                viewHolder = new BaikeFragment.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BaikeFragment.ViewHolder) convertView.getTag();
            }

            final ForumBean bean = beans.get(position);
            ((TextView) viewHolder.itemView.findViewById(R.id.title)).setText(bean.title);
            ImageLoader.getInstance().displayImage(RsenUrlUtil.URL_BASE + bean.logo,
                    (ImageView) viewHolder.itemView.findViewById(R.id.imageView));
            ((TextView) viewHolder.itemView.findViewById(R.id.title_tsh)).setText(bean.total_count);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
////                    Bundle bundle = new Bundle();
////                    bundle.putString("key", bean.);
//                    Intent intent = new Intent(context, IssueActivity1.class);
//                    context.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public static class LTBean {
        //需要什么字段，自己添加
        /**
         * "id": "9",
         * "pid": "0",
         * "sort": "1",
         * "status": "1",
         * "title": "龙鱼版块"
         */

        public String title;
        public ArrayList<ForumBean> forumList;
    }

    public static class ForumBean {
        /**
         * "allow_user_group": "1",
         * "background": "/opensns/Public/App/images/default_head.jpg",
         * "create_time": "1456125779",
         * "description": "",
         * "id": "13",
         * "last_reply_time": "1456210685",
         * "logo": "/opensns/Uploads/Picture/2016-02-22/56caae2b1df8d_128_128.jpg",
         * "post_count": "2",
         * "sort": "0",
         * "status": "1",
         * "title": "魟鱼版块三",
         * "topic_count": "2",
         * "total_count": 7,
         * "type_id": "10"
         */
        public String title;
        public String logo;//需要加上 URL_BASE
        public String total_count;


    }


}