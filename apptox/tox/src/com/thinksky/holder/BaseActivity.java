package com.thinksky.holder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

	// 获取到前台进程的Activity
	private static Activity mForegroundActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		initView();
		initActionbar();
	}

	/**
	 * 初始化actionbar
	 */
	protected abstract void initActionbar();

	/**
	 * 初始化界面
	 */
	protected abstract void initView();

	/**
	 * 初始化数据
	 */
	protected abstract void init();

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mForegroundActivity = this;
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.mForegroundActivity = null;
	}

	public static Activity getForegroundActivity() {
		return mForegroundActivity;
	}
}
