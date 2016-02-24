package com.thinksky.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksky.info.NewsListInfo;
import com.thinksky.tox.R;

import org.kymjs.aframe.bitmap.KJBitmap;

import java.util.ArrayList;

/**
 * 资讯列表适配器
 */
public class NewsListAdapter extends BaseAdapter {

    private ArrayList<NewsListInfo> newsListInfos;
    ViewHolder viewHolder;
    KJBitmap kjBitmap;
    private Context mContent;

    public NewsListAdapter(Context context,ArrayList<NewsListInfo> newsListInfos) {
        super();
        this.newsListInfos = newsListInfos;
        this.mContent = context;
    }

    @Override
    public int getCount() {
        return newsListInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return newsListInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(newsListInfos.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        kjBitmap = KJBitmap.create();
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContent).inflate(R.layout.news_info_item, parent, false);
            viewHolder.newsLogo = (ImageView)convertView.findViewById(R.id.news_logo);
            viewHolder.newsTitle = (TextView)convertView.findViewById(R.id.news_title);
            viewHolder.newsAuthor = (TextView)convertView.findViewById(R.id.news_author_name);
            viewHolder.newsCreateTime = (TextView)convertView.findViewById(R.id.news_create_time);
            viewHolder.newsDescription = (TextView)convertView.findViewById(R.id.news_description);
            viewHolder.newsViewCount = (TextView)convertView.findViewById(R.id.news_view_count);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        kjBitmap.display(viewHolder.newsLogo,newsListInfos.get(position).getCover(),280,260);
        viewHolder.newsTitle.setText(newsListInfos.get(position).getTitle());
        viewHolder.newsAuthor.setText(newsListInfos.get(position).getUser().getNickname());
        viewHolder.newsCreateTime.setText(newsListInfos.get(position).getCreate_time());
        viewHolder.newsDescription.setText(Html.fromHtml(newsListInfos.get(position).getDescription()));
        viewHolder.newsViewCount.setText(newsListInfos.get(position).getView());
        return convertView;
    }

    //控件缓存器
    private class ViewHolder{
        ImageView newsLogo;
        TextView newsTitle,newsAuthor,newsCreateTime,newsDescription,newsViewCount;
    }
}