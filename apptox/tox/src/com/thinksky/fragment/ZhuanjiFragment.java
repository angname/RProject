package com.thinksky.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinksky.tox.IssueActivity1;
import com.thinksky.tox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ZhuanjiFragment extends Fragment {
    private List<String> pictureListUrls;
    View rootView;
    ListView listView;
    private Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这里写
        rootView = inflater.inflate(R.layout.fragment_zhuanji, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
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
        RsenUrlUtil.execute(this.getActivity(), RsenUrlUtil.URL_ZJ, new RsenUrlUtil.OnHttpResultListener() {
            @Override
            public void onResult(boolean state, String result, JSONObject jsonObject) {
                if (state) {
                    ArrayList<ZjBean> beans = parseJson(jsonObject);
                    listView.setAdapter(new ZjAdapter(getActivity(), beans));
                }
            }
        });
    }

    public static ArrayList<ZjBean> parseJson(JSONObject object) {
        ArrayList<ZjBean> beans = new ArrayList<>();
        if (object != null) {
            try {
                JSONArray array = object.getJSONArray("list");
                for (int i = 0; i < array.length(); i++) {
                    ZjBean bean = new ZjBean();
                    JSONObject jsonObject = array.getJSONObject(i);
                    bean.title = jsonObject.getString("title");//title 赋值
                    //其他字段。。。赋值

                    // TODO: 2016/2/17

                    ArrayList<IssueBean> issueBeens = new ArrayList<>();
                    JSONArray issueList = jsonObject.getJSONArray("$IssueList");
                    for (int j = 0; j < issueList.length(); j++) {
                        JSONObject issueListJSONObject = issueList.getJSONObject(i);
                        IssueBean issueBean = new IssueBean();
                        issueBean.title = issueListJSONObject.getString("title");// issue title 赋值
                        issueBean.cover_url = issueListJSONObject.getString("cover_url");// issue cover_url 赋值
//                        issueBean.Row_id = issueListJSONObject.getString("Row_id");
                        //其他字段。。。赋值
                        // TODO: 2016/2/17

                        issueBeens.add(issueBean);
                    }

                    bean.IssueList = issueBeens;
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


    public static ZhuanjiFragment newInstance(String param1) {
        ZhuanjiFragment fragment = new ZhuanjiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static class ZjAdapter extends BaseAdapter {

        Context context;
        ArrayList<ZjBean> beans;

        public ZjAdapter(Context context, ArrayList<ZjBean> beans) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_zhuanji_adapter_item, parent, false);
                viewHolder = new BaikeFragment.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BaikeFragment.ViewHolder) convertView.getTag();
            }


            ZjBean bean = beans.get(position);
            ((TextView) viewHolder.itemView.findViewById(R.id.title)).setText(bean.title);
            ((GridView) viewHolder.itemView.findViewById(R.id.gridView)).setAdapter(new ZjGridAdapter(context, bean.IssueList));

            return convertView;
        }
    }

    public static class ZjGridAdapter extends BaseAdapter {

        Context context;
        ArrayList<IssueBean> beans;

        public ZjGridAdapter(Context context, ArrayList<IssueBean> beans) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_zhuanji_adapter_issue_item, parent, false);
                viewHolder = new BaikeFragment.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BaikeFragment.ViewHolder) convertView.getTag();
            }

            final IssueBean bean = beans.get(position);
            ((TextView) viewHolder.itemView.findViewById(R.id.title)).setText(bean.title);
            ImageLoader.getInstance().displayImage(RsenUrlUtil.URL_BASE + bean.cover_url,
                    (ImageView) viewHolder.itemView.findViewById(R.id.imageView));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("key", bean.Row_id);
                    Intent intent = new Intent(context, IssueActivity1.class);
                    context.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public static class ZjBean {
        //需要什么字段，自己添加


        public String title;
        public ArrayList<IssueBean> IssueList;
    }

    public static class IssueBean {
        public String title;
        public String cover_url;//需要加上 URL_BASE
        public String Row_id;
    }


}
