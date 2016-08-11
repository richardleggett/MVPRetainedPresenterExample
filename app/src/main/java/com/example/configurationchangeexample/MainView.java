package com.example.configurationchangeexample;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * @see MainPresenter
 */
public interface MainView extends MvpView {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setData(Model model);
}
