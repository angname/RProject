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

import com.alibaba.fastjson.JSON;
import com.thinksky.tox.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WendaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private TextView textView;

    View rootView;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这里写
        rootView = inflater.inflate(R.layout.fragment_wenda, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        RsenUrlUtil.execute(this.getActivity(),RsenUrlUtil.URL_WD, new RsenUrlUtil.OnHttpResultListener() {
            @Override
            public void onResult(boolean state, String result, JSONObject jsonObject) {
                if (state) {
//                    ArrayList<ZjBean> beans = parseJson(jsonObject);
//                    listView.setAdapter(new ZjAdapter(getActivity(), beans));
//                    WendaListAdapter

                    WendaBean wendaBean = JSON.parseObject(result, WendaBean.class);
                    listView.setAdapter(new WendaListAdapter(WendaFragment.this.getActivity(), wendaBean.getList()));
                }
            }
        });
    }


    public static WendaFragment newInstance(String param1) {
        WendaFragment fragment = new WendaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public WendaFragment() {
        // Required empty public constructor
    }

    public static class WendaListAdapter extends BaseAdapter {
        private ArrayList<WendaBean.ListEntity> datas;
        private Context context;

        public WendaListAdapter(Context context, ArrayList<WendaBean.ListEntity> datas) {
            this.datas = datas;
            this.context = context;
        }

        @Override
        public int getCount() {
            if (datas == null) {
                return 0;
            }
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BaikeFragment.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_wenda_adapter_item, parent, false);
                viewHolder = new BaikeFragment.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BaikeFragment.ViewHolder) convertView.getTag();
            }

            WendaBean.ListEntity listEntity = datas.get(position);

            /*其他控件信息，自己添加 id， 然后从 listEntity对象中获取数据，填充就行了*/
            ((TextView) viewHolder.itemView.findViewById(R.id.title)).setText(listEntity.getTitle());
            ((TextView) viewHolder.itemView.findViewById(R.id.content)).setText(listEntity.getContent());
            ((TextView) viewHolder.itemView.findViewById(R.id.nickname)).setText(listEntity.getUser().getNickname());
            ((TextView) viewHolder.itemView.findViewById(R.id.answer_num)).setText(listEntity.getAnswer_num());
            ((TextView) viewHolder.itemView.findViewById(R.id.creat_time)).setText(listEntity.getCreate_time());
            String s = listEntity.getBest_answer();
            if (s.equals("1")) {
                ((TextView) viewHolder.itemView.findViewById(R.id.best_answer)).setText("已解决");
            }
            if (s.equals("0")) {
                ((TextView) viewHolder.itemView.findViewById(R.id.best_answer)).setText("求助中");
            }
            /*点击事件响应*/
            viewHolder.itemView.findViewById(R.id.item_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RsenCommonActivity.showActivity(context, RsenCommonActivity.TYPE_QDETAIL, null);
                    //Toast.makeText(v.getContext(), "Click Me " + position, Toast.LENGTH_LONG).show();
                }
            });
            return convertView;
        }
    }

    public static class WendaBean {

        /**
         * error_code : 0
         * list : [{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月19日 11:23","description1":"<p>f&#39;f&#39;f&#39;f&#39;fffffffffffff&#39;f&#39;f&#39;f&#39;fffffffffffff&#39;f&#39;f&#39;f&#39;fffffffffffff&#39;f&#39;f&#39;f&#39;ffffffffffff<\/p>","good_question":"0","id":"15","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"f'f'f'f'ffffffffffff","uid":"100","update_time":"02月19日 11:23","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"david","real_nickname":"david","signature":"","uid":"100","username":"david"}},{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月19日 11:22","description1":"","good_question":"0","id":"14","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"手机端登录密码弹出输入盘只有数字aaa","uid":"100","update_time":"02月19日 11:22","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"david","real_nickname":"david","signature":"","uid":"100","username":"david"}},{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月19日 11:04","description1":"<p>手机端登录密码弹出输入盘只有数字手机端登录密码弹出输入盘只有数字手机端登录密码弹出输入盘只有数字手机端登录密码弹出输入盘只有数字手机端登录密码弹出输入盘只有数字<\/p>","good_question":"0","id":"13","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"手机端登录密码弹出输入盘只有数字","uid":"100","update_time":"02月19日 11:04","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"david","real_nickname":"david","signature":"","uid":"100","username":"david"}},{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月19日 10:55","description1":"","good_question":"0","id":"12","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"手机端登录密码弹出输入盘只有数字","uid":"100","update_time":"02月19日 10:55","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"david","real_nickname":"david","signature":"","uid":"100","username":"david"}},{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月15日 16:48","description1":"","good_question":"0","id":"11","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"测试789","uid":"1","update_time":"02月15日 16:48","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"admin","real_nickname":"admin","signature":"","uid":"1","username":"admin"}},{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月15日 16:41","description1":"","good_question":"0","id":"10","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"测试456","uid":"1","update_time":"02月15日 16:41","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"admin","real_nickname":"admin","signature":"","uid":"1","username":"admin"}},{"answer_num":"1","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月15日 16:40","description1":"","good_question":"0","id":"9","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"测试123","uid":"1","update_time":"02月15日 16:40","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"admin","real_nickname":"admin","signature":"","uid":"1","username":"admin"}},{"answer_num":"0","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月15日 16:39","description1":"","good_question":"0","id":"8","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"测试最佳答案","uid":"1","update_time":"02月15日 16:39","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"admin","real_nickname":"admin","signature":"","uid":"1","username":"admin"}},{"answer_num":"1","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月15日 16:25","description1":"","good_question":"0","id":"7","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"魟鱼饲养怎么会更好","uid":"1","update_time":"02月15日 16:26","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"admin","real_nickname":"admin","signature":"","uid":"1","username":"admin"}},{"answer_num":"1","best_answer":"0","category":"1","category_title":false,"content":"","cover_url":"/opensns/Public/images/nopic.png","create_time":"02月15日 10:32","description1":"","good_question":"0","id":"6","imgList":[],"is_recommend":"0","is_supported":"0","status":"1","support_count":"0","title":"测试问答带图","uid":"1","update_time":"02月15日 10:32","user":{"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"admin","real_nickname":"admin","signature":"","uid":"1","username":"admin"}}]
         * message : 返回成功
         * success : true
         */

        private int error_code;
        private String message;
        private boolean success;
        /**
         * answer_num : 0
         * best_answer : 0
         * category : 1
         * category_title : false
         * content :
         * cover_url : /opensns/Public/images/nopic.png
         * create_time : 02月19日 11:23
         * description1 : <p>f&#39;f&#39;f&#39;f&#39;fffffffffffff&#39;f&#39;f&#39;f&#39;fffffffffffff&#39;f&#39;f&#39;f&#39;fffffffffffff&#39;f&#39;f&#39;f&#39;ffffffffffff</p>
         * good_question : 0
         * id : 15
         * imgList : []
         * is_recommend : 0
         * is_supported : 0
         * status : 1
         * support_count : 0
         * title : f'f'f'f'ffffffffffff
         * uid : 100
         * update_time : 02月19日 11:23
         * user : {"avatar128":"/opensns/Public/images/default_avatar_128_128.jpg","avatar256":"/opensns/Public/images/default_avatar_256_256.jpg","avatar32":"/opensns/Public/images/default_avatar_32_32.jpg","avatar512":"/opensns/Public/images/default_avatar_512_512.jpg","avatar64":"/opensns/Public/images/default_avatar_64_64.jpg","nickname":"david","real_nickname":"david","signature":"","uid":"100","username":"david"}
         */

        private ArrayList<ListEntity> list;

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setList(ArrayList<ListEntity> list) {
            this.list = list;
        }

        public int getError_code() {
            return error_code;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }

        public ArrayList<ListEntity> getList() {
            return list;
        }

        public static class ListEntity {
            private String answer_num;
            private String best_answer;
            private String category;
            private boolean category_title;
            private String content;
            private String cover_url;
            private String create_time;
            private String description1;
            private String good_question;
            private String id;
            private String is_recommend;
            private String is_supported;
            private String status;
            private String support_count;
            private String title;
            private String uid;
            private String update_time;
            /**
             * avatar128 : /opensns/Public/images/default_avatar_128_128.jpg
             * avatar256 : /opensns/Public/images/default_avatar_256_256.jpg
             * avatar32 : /opensns/Public/images/default_avatar_32_32.jpg
             * avatar512 : /opensns/Public/images/default_avatar_512_512.jpg
             * avatar64 : /opensns/Public/images/default_avatar_64_64.jpg
             * nickname : david
             * real_nickname : david
             * signature :
             * uid : 100
             * username : david
             */

            private UserEntity user;
            private List<String> imgList;

            public void setAnswer_num(String answer_num) {
                this.answer_num = answer_num;
            }

            public void setBest_answer(String best_answer) {
                this.best_answer = best_answer;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public void setCategory_title(boolean category_title) {
                this.category_title = category_title;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setCover_url(String cover_url) {
                this.cover_url = cover_url;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public void setDescription1(String description1) {
                this.description1 = description1;
            }

            public void setGood_question(String good_question) {
                this.good_question = good_question;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setIs_recommend(String is_recommend) {
                this.is_recommend = is_recommend;
            }

            public void setIs_supported(String is_supported) {
                this.is_supported = is_supported;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setSupport_count(String support_count) {
                this.support_count = support_count;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }

            public void setUser(UserEntity user) {
                this.user = user;
            }

            public void setImgList(List<String> imgList) {
                this.imgList = imgList;
            }

            public String getAnswer_num() {
                return answer_num;
            }

            public String getBest_answer() {
                return best_answer;
            }

            public String getCategory() {
                return category;
            }

            public boolean isCategory_title() {
                return category_title;
            }

            public String getContent() {
                return content;
            }

            public String getCover_url() {
                return cover_url;
            }

            public String getCreate_time() {
                return create_time;
            }

            public String getDescription1() {
                return description1;
            }

            public String getGood_question() {
                return good_question;
            }

            public String getId() {
                return id;
            }

            public String getIs_recommend() {
                return is_recommend;
            }

            public String getIs_supported() {
                return is_supported;
            }

            public String getStatus() {
                return status;
            }

            public String getSupport_count() {
                return support_count;
            }

            public String getTitle() {
                return title;
            }

            public String getUid() {
                return uid;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public UserEntity getUser() {
                return user;
            }

            public List<?> getImgList() {
                return imgList;
            }

            public static class UserEntity {
                private String avatar128;
                private String avatar256;
                private String avatar32;
                private String avatar512;
                private String avatar64;
                private String nickname;
                private String real_nickname;
                private String signature;
                private String uid;
                private String username;

                public void setAvatar128(String avatar128) {
                    this.avatar128 = avatar128;
                }

                public void setAvatar256(String avatar256) {
                    this.avatar256 = avatar256;
                }

                public void setAvatar32(String avatar32) {
                    this.avatar32 = avatar32;
                }

                public void setAvatar512(String avatar512) {
                    this.avatar512 = avatar512;
                }

                public void setAvatar64(String avatar64) {
                    this.avatar64 = avatar64;
                }

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public void setReal_nickname(String real_nickname) {
                    this.real_nickname = real_nickname;
                }

                public void setSignature(String signature) {
                    this.signature = signature;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public void setUsername(String username) {
                    this.username = username;
                }

                public String getAvatar128() {
                    return avatar128;
                }

                public String getAvatar256() {
                    return avatar256;
                }

                public String getAvatar32() {
                    return avatar32;
                }

                public String getAvatar512() {
                    return avatar512;
                }

                public String getAvatar64() {
                    return avatar64;
                }

                public String getNickname() {
                    return nickname;
                }

                public String getReal_nickname() {
                    return real_nickname;
                }

                public String getSignature() {
                    return signature;
                }

                public String getUid() {
                    return uid;
                }

                public String getUsername() {
                    return username;
                }
            }
        }
    }


}
