package com.example.configurationchangeexample;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

/**
 * See README.md for description
 */
public class MainActivity extends MvpViewStateActivity<MainView, MainPresenter>
        implements MainView {

    private TextView textView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ensure that the Presenter survives Activity configuration (e.g. orientation) change
        // this also works for Fragments
        // NOTE: to manually do this you could use a retained headless Fragment to store the Presenter
        // or onRetainCustomNonConfigurationInstance(), but the former is preferred by Google
        setRetainInstance(true);

        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // toggle simulate error mode
        ((CheckBox)findViewById(R.id.errorCheckBox)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        getPresenter().onCheckedSimulateErrorCheckbox(checked);
                    }
                }
        );

        // see #onNewViewStateInstance() for the kick-off of loadData()
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void showLoading() {
        getMainViewState().state = MainViewState.STATE_SHOW_LOADING;
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        getMainViewState().state = MainViewState.STATE_SHOW_CONTENT;
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        getMainViewState().state = MainViewState.STATE_SHOW_ERROR;
        getMainViewState().exception = e;
        progressBar.setVisibility(View.GONE);
        // NOTE: really we should listen for when we have shown the error and return to another state
        // otherwise on rotation we'll show the error again.
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Model model) {
        getMainViewState().loadedModel = model;
        if(model != null) {
            textView.setText(model.number);
        } else {
            textView.setText("");
        }
    }

    /**
     * Will be called when no view state exists yet and one needs to be created
     * NOTE: This is because we extend MvpViewStateActivity
     */
    @Override
    public void onNewViewStateInstance() {
        getPresenter().loadData();
    }

    @Override
    @NonNull
    public ViewState<MainView> createViewState() {
        return new MainViewState();
    }

    /**
     * @return Cast ViewState to MainViewState
     */
    public MainViewState getMainViewState() {
        return (MainViewState) getViewState();
    }

    /**
     * Allows us to restore state after a configuration (e.g. orientation) change
     */
    class MainViewState implements ViewState<MainView> {
        /**
         * Used to indicate that loading is currently displayed by the View
         */
        public static final int STATE_SHOW_LOADING = 0;
        /**
         * Used to indicate that content is currently displayed by the View
         */
        public static final int STATE_SHOW_CONTENT = 1;
        /**
         * Used to indicate that the error message is currently displayed by the view
         */
        public static final int STATE_SHOW_ERROR = -1;

        public int state;
        public Throwable exception;
        public Model loadedModel;

        @Override
        public void apply(MainView view, boolean retained) {
            if(state==STATE_SHOW_ERROR) {
                view.showError(exception);
            } else if(state==STATE_SHOW_LOADING) {
                view.showLoading();
            } else if(state==STATE_SHOW_CONTENT) {
                view.setData(loadedModel);
                view.showContent();
            }
        }
    }
}
