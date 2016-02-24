package com.thinksky.fragment;

/**
 * Created by jiao on 2016/2/23.
 */
public interface IDrawerPresenter {
    IDrawerPresenter getInstance();
    void dispatchEvent(int totalPages, int currentPage);
}
