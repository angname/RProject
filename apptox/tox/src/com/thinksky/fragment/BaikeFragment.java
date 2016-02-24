package com.thinksky.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thinksky.tox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BaikeFragment extends Fragment {

    View rootView;
    ListView myListView;
    private BaikeTwoFragment baikeTwoFragment;
    private static final String ARG_PARAM1 = "param1";

    private List<String> mDatas;

    public static BaikeFragment newInstance(String param1) {
        BaikeFragment fragment = new BaikeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_baike, container, false);
        myListView = (ListView) rootView.findViewById(R.id.MyListView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        RsenUrlUtil.execute(this.getActivity(),RsenUrlUtil.URL_BK, new RsenUrlUtil.OnHttpResultListener() {
            @Override
            public void onResult(boolean state,String result ,JSONObject jsonObject) {
                if (state) {
                    try {
                        final ArrayList<BkBean> beans = parseJson(jsonObject.getJSONArray("list"));
                        myListView.setAdapter(new BaikeAdapter(getActivity(), beans));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    public class BaikeAdapter extends BaseAdapter {
        ArrayList<BkBean> beans = new ArrayList<>();
        Context context;

        public BaikeAdapter(Context context, ArrayList<BkBean> beans) {
            this.context = context;
            this.beans = beans;
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_baike_adapter_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final BkBean bean = beans.get(position);
            ((TextView) viewHolder.itemView.findViewById(R.id.title_bkt)).setText(bean.getTitle());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", bean.getId());
                    FragmentTransaction transaction = fm.beginTransaction();
                    baikeTwoFragment = new BaikeTwoFragment();
//                    baikeTwoFragment=BaikeTwoFragment.newInstance("1");

                    baikeTwoFragment.setArguments(bundle);
                    transaction.add(R.id.content_bk, baikeTwoFragment);
                    transaction.commit();
                }
            });
            return convertView;
        }
    }

    public static class ViewHolder {
        public View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

    public static ArrayList<BkBean> parseJson(JSONArray array) {
        ArrayList<BkBean> beans = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject json = array.getJSONObject(i);
                    BkBean bean = new BkBean(
                            json.getString("id"),
                            json.getString("sort"),
                            json.getString("status"),
                            json.getString("title")
//                            json.getString("pid")
                    );
                    beans.add(bean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return beans;
    }


    public static class BkBean {
        /**
         * "id": "3",
         * "sort": "0",
         * "status": "1",
         * "title": "魟鱼"
         * "pid": "0",
         */
        public String id;
        public String sort;
        public String status;
        public String title;

//        public String pid;

        public String getId() {
            return id;
        }

        public String getSort() {
            return sort;
        }

        public String getStatus() {
            return status;
        }

        public String getTitle() {
            return title;
        }

        public BkBean(String id, String sort, String status, String title) {
            this.id = id;
            this.sort = sort;
            this.status = status;
            this.title = title;

//            this.pid = pid ;
        }
    }
}
