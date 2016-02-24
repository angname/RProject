package com.thinksky.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.net.IsNet;
import com.thinksky.tox.IssueDetail;
import com.thinksky.tox.R;
import com.tox.ImageLoader;
import com.tox.IssueData;
import com.tox.ToastHelper;
import com.tox.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Created by hujiayu on 2015/3/24 0024.   原版专辑模块，暂时不用
 */
public class IssueFragment extends Fragment {

    // 每页要加载的图片数量
    public static final int PAGE_SIZE = 10;

    // 记录当前已加载到第几页
    private int page;
    // 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
    private boolean loadOnce;
    // MyScrollView下的直接子布局。
    LinearLayout scrollLayout;

    // MyScrollView布局的高度。
    private static int scrollViewHeight;

    // 记录上垂直方向的滚动距离。
    private static int lastScrollY = -1;

    // 记录所有界面上的图片，用以可以随时控制对图片的释放。
    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    Bundle screenID;
    // 每一列的宽度
    private int columnWidth;
    // 当前第一列的高度
    private int firstColumnHeight;
    // 当前第二列的高度
    private int secondColumnHeight;
    // 当前第三列的高度
    private int thirdColumnHeight;
    ScrollView scrollView;
    // 对图片进行管理的工具类
    private ImageLoader imageLoader;
    // 第一列的布局
    private LinearLayout firstColumn;
    // 第二列的布局
    private LinearLayout secondColumn;
    // 第三列的布局
    private LinearLayout thirdColumn;
    // 记录所有正在下载或等待下载的任务。
    private static Set<LoadImageTask> taskCollection;
    //记录是否已经加载到最后过一次
    boolean count = true;
    LinearLayout my_scroll_progress;
    //专辑图片信息集合

    Context context;
    ArrayList<Image> imageArrayList;
    private int index = 0;

    // 在Handler中进行图片可见性检查的判断，以及加载更多图片的操作。
//    private Handler handler = new Handler() {
//
//        public void handleMessage(Message msg) {
//            ScrollView myScrollView = (ScrollView) msg.obj;
//            int scrollY = myScrollView.getScrollY();
//            // 如果当前的滚动位置和上次相同，表示已停止滚动
//            if (scrollY == lastScrollY) {
//                // 当滚动的最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
//                if (scrollViewHeight <= scrollLayout.getHeight() && taskCollection.isEmpty()) {
//                    loadMoreImages();
//                }
////                checkVisibility();
//            }
//            else {
//                lastScrollY = scrollY;
//                Message message = new Message();
//                message.obj = myScrollView;
//                // 5毫秒后再次对滚动位置进行判断
//                handler.sendMessageDelayed(message, 5);
//            }
//        }
//    };

    private Image imagePicture;
    private int issue_id;
    private int pid;
    ArrayList<Image> imageArrayLists;

    private Handler handler = new Handler() {
        ArrayList<Image> imageArrayList;

        @Override
        @SuppressWarnings(value = {"unchecked"})
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageArrayList = (ArrayList<Image>) msg.obj;
            loadMoreImages(imageArrayList);
            my_scroll_progress.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (IsNet.IsConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    imageArrayLists = new ArrayList<Image>();
                    IssueData issueData = new IssueData();
                    try {
//                        HttpHelper.HttpResult result = HttpHelper.get("?s=" + Url.ISSUELIST);
//                        String josn = result.getString();
                        ArrayList<JSONObject> jsonList = issueData.getJson("?s=" + Url.ISSUELIST);
                        screenID = getArguments();
                        issue_id = Integer.parseInt(screenID.get("issue_id").toString());
                        pid = Integer.parseInt(screenID.get("pid").toString());
                        for (int i = 0; i < jsonList.size(); i++) {//每个2级分类的专辑图片
                            imagePicture = new Image();
                            JSONArray fistIssueIDArray = jsonList.get(i).optJSONArray("Modules_id");
                            if (fistIssueIDArray != null) {
                                JSONObject fistIssueIDObj = (JSONObject) fistIssueIDArray.get(0);
                                JSONObject jsonObj =  (JSONObject)jsonList.get(i);
                                if (issue_id == (jsonObj.getInt("issue_id"))) {
                                    imagePicture.setId(Integer.parseInt(jsonObj.getString("id")));
                                    imagePicture.setIssue_id(Integer.parseInt(jsonObj.getString("issue_id")));
                                    imagePicture.setTitle(jsonObj.getString("title"));
                                    imagePicture.setImage_url(issueData.getResourcesURL(jsonObj.getString("cover_url")));
                                    imageArrayLists.add(imagePicture);
                                } else if (pid == 0 && issue_id == fistIssueIDObj.getInt("id")) {       //全部分类
                                    imagePicture.setId(Integer.parseInt(jsonObj.getString("id")));
                                    imagePicture.setIssue_id(Integer.parseInt(jsonObj.getString("issue_id")));
                                    imagePicture.setTitle(jsonObj.getString("title"));
                                    imagePicture.setImage_url(issueData.getResourcesURL(jsonObj.getString("cover_url")));
                                    imageArrayLists.add(imagePicture);
                                }
                            }
                        }
                        Message message = new Message();
                        message.obj = imageArrayLists;
                        handler.sendMessage(message);
                    } catch (NullPointerException e) {
                        ToastHelper.showToast("获取数据失败，请重试", context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {
            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.issue_fragment_scroview, container, false);
        scrollView = (ScrollView) rootView.findViewById(R.id.my_scroll_view);
        my_scroll_progress = (LinearLayout) rootView.findViewById(R.id.my_scroll_progress);

        imageLoader = ImageLoader.getInstance();
        taskCollection = new HashSet<LoadImageTask>();
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        index++;
                        break;
                    default:
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
                    index = 0;
                    View view = ((ScrollView) v).getChildAt(0);
                    if (count && view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                        //加载数据代码
                        Toast.makeText(context, "正在加载...", Toast.LENGTH_SHORT).show();
                        Message message = new Message();
                        message.obj = imageArrayLists;
                        handler.sendMessage(message);
                    }
                }
                return false;
            }
        });
        if (!loadOnce) {
            scrollLayout = (LinearLayout) scrollView.getChildAt(0);
            firstColumn = (LinearLayout) scrollView.findViewById(R.id.first_column);
            secondColumn = (LinearLayout) scrollView.findViewById(R.id.second_column);
            thirdColumn = (LinearLayout) scrollView.findViewById(R.id.third_column);
            //瀑布流的列数
            DisplayMetrics metric = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
            int widthPixels = metric.widthPixels;
            columnWidth = widthPixels / 3;
            loadOnce = true;
        }
        return rootView;
    }

    //图片类
    private class Image {
        int issue_id;//父ID
        String title;//图片标题
        String image_url;//图片url
        int id;//图片ID

        public void setId(int id) {
            this.id = id;
        }

        public void setIssue_id(int issue_id) {
            this.issue_id = issue_id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getId() {
            return id;
        }

        public int getIssue_id() {
            return issue_id;
        }

        public String getTitle() {
            return title;
        }

        public String getImage_url() {
            return image_url;
        }
    }

    /**
     * 开始加载下一页的图片，每张图片都会开启一个异步线程去下载。
     */

    public void loadMoreImages(ArrayList<Image> imageArrayList) {

        if (hasSDCard()) {
            int startIndex = page * PAGE_SIZE;
            int endIndex = page * PAGE_SIZE + PAGE_SIZE;
            if (startIndex < imageArrayList.size()) {
                if (endIndex > imageArrayList.size()) {
                    endIndex = imageArrayList.size();
                }
                for (int i = startIndex; i < endIndex; i++) {
                    LoadImageTask task = new LoadImageTask(imageArrayList.get(i).getTitle(), imageArrayList.get(i).getId());
                    taskCollection.add(task);
                    task.execute(imageArrayList.get(i).getImage_url());
                }
                page++;
            } else if (count) {
                Toast.makeText(context, "已没有更多内容", Toast.LENGTH_SHORT).show();
                count = false;
            }
        } else {
            Toast.makeText(context, "未发现SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 遍历imageViewList中的每张图片，对图片的可见性进行检查，如果图片已经离开屏幕可见范围，则将图片替换成一张空图。
     */
    public void checkVisibility() {
        for (int i = 0; i < imageViewList.size(); i++) {
            ImageView imageView = imageViewList.get(i);
            int borderTop = (Integer) imageView.getTag(R.string.border_top);
            int borderBottom = (Integer) imageView
                    .getTag(R.string.border_bottom);
            if (borderBottom > scrollView.getScrollY()
                    && borderTop < scrollView.getScrollY() + scrollViewHeight) {
                String imageUrl = (String) imageView.getTag(R.string.image_url);
                Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    LoadImageTask task = new LoadImageTask(imageView);
                    task.execute(imageUrl);
                }
            } else {
                imageView.setImageResource(R.drawable.empty_photo);
            }
        }
    }

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    private boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 异步下载图片的任务。
     */
    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String mImageUrl;
        /**
         * 图片的ID
         */
        private int issueID;
        /**
         * 图片的title
         */
        private String title;
        /**
         * 可重复使用的ImageView
         */
        private ImageView mImageView;

        /**
         * 可重复使用的TextView
         */
        private TextView mTextView;

        public LoadImageTask() {
        }

        public LoadImageTask(String title, int issueID) {
            this.title = title;
            this.issueID = issueID;
        }

        /**
         * 将可重复使用的ImageView传入
         *
         * @param imageView
         */
        public LoadImageTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            mImageUrl = params[0];
            Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
            if (imageBitmap == null) {
                imageBitmap = imageLoader.loadImage(mImageUrl, columnWidth);
            }
            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                double ratio = bitmap.getWidth() / (columnWidth * 1.0);
                int scaledHeight = (int) (bitmap.getHeight() / ratio);
                addImage(bitmap, title, columnWidth, scaledHeight);
            }
            taskCollection.remove(this);
        }

        /**
         * 向ImageView中添加一张图片
         *
         * @param bitmap      待添加的图片
         * @param imageWidth  图片的宽度
         * @param imageHeight 图片的高度
         */
        private void addImage(Bitmap bitmap, String title, int imageWidth, int imageHeight) {
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    imageWidth, imageHeight);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    imageWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            if (mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                ImageView imageView = new ImageView(context);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                alphaAnimation.setDuration(1500);
                imageView.startAnimation(alphaAnimation);
                imageView.setLayoutParams(imageParams);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setTag(R.string.image_url, mImageUrl);
                //图片和文本组成一个LinearLayout
                LinearLayout layoutTemp = new LinearLayout(context);
                layoutTemp.setOrientation(LinearLayout.VERTICAL);
                layoutTemp.setBackgroundResource(R.drawable.issue_image_background);
                layoutTemp.setPadding(5, 10, 10, 10);
                layoutTemp.addView(imageView);
                imageViewList.add(imageView);

                //        //gridView列表项监听事件
                layoutTemp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle id_to = new Bundle();
                        id_to.putLong("id", issueID);
                        //                text.setText(getUrl()+"这是测试文字");
                        if (IsNet.IsConnect()) {
                            Intent intent = new Intent(context, IssueDetail.class);
                            intent.putExtras(id_to);
                            getActivity().startActivity(intent);
                        } else {
                            ToastHelper.showToast("网络不可用，请检查网络是否开启！", Url.context);
                        }
                    }
                });
                TextView textView = new TextView(context);
                textView.setLayoutParams(titleParams);
                textView.setPadding(5, 5, 5, 5);
                textView.setBackgroundColor(Color.parseColor("#F9F9F9"));
                textView.setTextColor(Color.parseColor("#666666"));
                textView.setText(title);
                layoutTemp.addView(textView);
                findColumnToAdd(imageView, imageHeight).addView(layoutTemp);
            }
        }

        /**
         * 找到此时应该添加图片的一列。原则就是对三列的高度进行判断，当前高度最小的一列就是应该添加的一列。
         *
         * @param imageView
         * @param imageHeight
         * @return 应该添加图片的一列
         */
        private LinearLayout findColumnToAdd(ImageView imageView,
                                             int imageHeight) {
            if (firstColumnHeight <= secondColumnHeight) {
                if (firstColumnHeight <= thirdColumnHeight) {
                    imageView.setTag(R.string.border_top, firstColumnHeight);
                    firstColumnHeight += imageHeight;
                    imageView.setTag(R.string.border_bottom, firstColumnHeight);
                    return firstColumn;
                }
                imageView.setTag(R.string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            } else {
                if (secondColumnHeight <= thirdColumnHeight) {
                    imageView.setTag(R.string.border_top, secondColumnHeight);
                    secondColumnHeight += imageHeight;
                    imageView
                            .setTag(R.string.border_bottom, secondColumnHeight);
                    return secondColumn;
                }
                imageView.setTag(R.string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            }
        }
    }
}