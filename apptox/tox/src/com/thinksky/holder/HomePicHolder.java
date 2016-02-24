package com.thinksky.holder;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.thinksky.imageload.ImageLoader;
import com.thinksky.tox.R;
import com.thinksky.utils.UIUtils;
import com.thinksky.widget.IndicatorView;

import java.util.List;


public class HomePicHolder extends BaseHolder<List<String>> {

	private LayoutParams rl;
	private ViewPager mViewPager;
	private List<String> appInfos;
	private IndicatorView indicatorView;
	private RelativeLayout.LayoutParams mParams;
	private AutoPlayTask autoPlayTask;

	@Override
	protected View initView() {

		// 初始化轮播图最外面的布局
		RelativeLayout mHeadView = new RelativeLayout(UIUtils.getContext());
		rl = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				UIUtils.getDimens(R.dimen.list_header_hight));
		mHeadView.setLayoutParams(rl);
		// 初始化轮播图，然后设置数据
		mViewPager = new ViewPager(UIUtils.getContext());

		mParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		ViewPageAdapter adapter = new ViewPageAdapter();

		mViewPager.setAdapter(adapter);

		// 初始化轮播图的宽和高
		mViewPager.setLayoutParams(mParams);
		// 初始化点
		indicatorView = new IndicatorView(UIUtils.getContext());
		// 设置点的背景
		indicatorView.setIndicatorDrawable(UIUtils
				.getDrawable(R.drawable.indicator));

		// new RelativeLayout(UIUtils.getContext());

		mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 设置到父控件的右边
		mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		// 设置到父控件的下边
		mParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		// 设置左上右下的距离
		mParams.setMargins(0, 0, 20, 20);
		// 设置点的位置从0开始
		indicatorView.setSelection(0);
		// 把宽和高丢到布局里面
		indicatorView.setLayoutParams(mParams);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				indicatorView.setSelection(arg0 % getData().size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		autoPlayTask = new AutoPlayTask();
		mViewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					autoPlayTask.stop();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					autoPlayTask.start();
				}
				return true;
			}
		});

		// 把轮播图和点加入到头文件里面。需要注意：先加载轮播图，然后再加载点
		mHeadView.addView(mViewPager);
		mHeadView.addView(indicatorView);
		return mHeadView;
	}

	private class AutoPlayTask implements Runnable {

		// 自动跳的时间
		private int auto_play_time = 2000;

		private boolean is_auto_play = false;

		@Override
		public void run() {
			if (is_auto_play) {
				UIUtils.removeCallbacks(this);
				mViewPager
						.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
				UIUtils.postDelayed(this, auto_play_time);
			}
		}

		public void start() {
			if (!is_auto_play) {
				is_auto_play = true;
				UIUtils.removeCallbacks(this);
				UIUtils.getHandler().postDelayed(this, auto_play_time);
			}
		}

		public void stop() {
			if (is_auto_play) {
				UIUtils.removeCallbacks(this);
				is_auto_play = false;
			}
		}
	}

	@Override
	public void refreshView() {
		appInfos = getData();
		// 一共有几个点
		indicatorView.setCount(appInfos.size());
		// 默认设置viewpager的位置
		mViewPager.setCurrentItem(5000, false);
		autoPlayTask.start();
	}

	private class ViewPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// appInfos.size()
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(UIUtils.getContext());
			// 注意：如果只要是从网络获取图片的话，就必须设置这个属性。
			ImageLoader.load(imageView,
					appInfos.get(position % getData().size()));
			imageView.setScaleType(ScaleType.FIT_XY);
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

}
