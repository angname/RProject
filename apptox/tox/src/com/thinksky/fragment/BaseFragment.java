package com.thinksky.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksky.utils.UIUtils;
import com.thinksky.utils.ViewUtils;
import com.thinksky.widget.LoadingPager;
import com.thinksky.widget.LoadingPager.LoadResult;

import java.util.List;

;

public abstract class BaseFragment extends Fragment {

    private LoadingPager mContentView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mContentView) {
            mContentView = new LoadingPager(UIUtils.getContext()) {
                @Override
                protected LoadResult load() {
                    return BaseFragment.this.Load();
                }

                @Override
                protected View createLoadedView() {
                    return BaseFragment.this.createLoadedView();
                }
            };
        } else {
            ViewUtils.removeSelfFromParent(mContentView);
        }
        return mContentView;
    }

    protected abstract View createLoadedView();

    protected abstract LoadResult Load();

    // 展示具体的页面
    public void show() {
        if (null != mContentView) {
            mContentView.show();
        }
    }

    // 检查服务器返回的数据情况
    protected LoadResult check(Object obj) {
        if (null == obj) {
            return LoadResult.ERROR;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.size() == 0) {
                return LoadResult.EMPTY;
            }
        }
        return LoadResult.SUCCESS;
    }
}
