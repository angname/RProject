package com.thinksky.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.thinksky.holder.BaseHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class MyBaseAdapter<T> extends BaseAdapter {

	public MyBaseAdapter(ArrayList<T> mDatas) {
		setData(mDatas);
	}

	public List<T> mDatas;
	private BaseHolder holder;

	public void setData(ArrayList<T> mDatas) {
		this.mDatas = mDatas;
	}

	public List<T> getData() {
		return mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			holder = (BaseHolder) convertView.getTag();
			System.out
					.println("convertView == nullconvertView == nullconvertView == null");
		} else {
			holder = getHolder();
		}
		holder.setDate(mDatas.get(position));
		return holder.getRootView();
	}

	protected abstract BaseHolder getHolder();

}
