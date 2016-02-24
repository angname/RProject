package com.thinksky.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 * Created by jiao on 2016/2/18.
 */
public class BaikeTwoFragment extends Fragment {
    View rootView;
    ListView myListView;

    private static final String ARG_PARAM1 = "param1";


    public static BaikeTwoFragment newInstance(String param1) {
        BaikeTwoFragment fragment = new BaikeTwoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_baike_two, container, false);
        myListView = (ListView) rootView.findViewById(R.id.myListView);
        String id = getArguments().getString("key");
        RsenUrlUtil.execute(RsenUrlUtil.URL_BKT + id, new RsenUrlUtil.OnHttpResultListener() {
            @Override
            public void onResult(boolean state, String result, JSONObject jsonObject) {
                if (state) {
                    try {
                        ArrayList<BktBean> beans = parseJson(jsonObject.getJSONArray("list"));
                        myListView.setAdapter(new BKTAdapter(getActivity(), beans));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public class BKTAdapter extends BaseAdapter {
        ArrayList<BktBean> beans = new ArrayList<>();
        Context context;

        public BKTAdapter(Context context, ArrayList<BktBean> beans) {
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

            BktBean bean = beans.get(position);
            ((TextView) viewHolder.itemView.findViewById(R.id.title_bkt)).setText(bean.getTitle());

            return convertView;
        }
    }

    public static class ViewHolder {
        public View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

    public static ArrayList<BktBean> parseJson(JSONArray array) {
        ArrayList<BktBean> beans = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject json = array.getJSONObject(i);
                    BktBean bean = new BktBean(
                            json.getString("id"),
                            json.getString("sort"),
                            json.getString("status"),
                            json.getString("title"),
                            json.getString("category")
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


    public static class BktBean {
        /**
         * "category": "0",
         * "content": "<p>sdfad</p>",
         * "cover": "",
         * "create_time": "02月14日 10:04",
         * "dead_line": "2016-02-18 15:29",
         * "id": "6",
         * "sort": "0",
         * "status": "1",
         * "title": "1111",
         * <p/>
         * "uid": "1",
         */
        public String id;
        public String sort;
        public String status;
        public String title;
        public String category;

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

        public String getCategory() {
            return category;
        }


        public BktBean(String id, String sort, String status, String title, String category) {
            this.id = id;
            this.sort = sort;
            this.status = status;
            this.title = title;
            this.category = category;
//            this.pid = pid ;
        }
    }
}
