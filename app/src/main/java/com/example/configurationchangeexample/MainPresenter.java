package com.example.configurationchangeexample;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * The Presenter retrieves data and co-ordinates the View, responding to user events where necessary
 */
public class MainPresenter extends MvpNullObjectBasePresenter<MainView> {
    public int ID;
    private Subscription subscription;
    private boolean simulateError;

    MainPresenter() {
        // when debugging you can use this to easily inspect the instance ID
        ID = new Random().nextInt();
    }

    public void loadData() {
        getView().showLoading();

        if(subscription != null && !subscription.isUnsubscribed()) {
            // cancel previous
            subscription.unsubscribe();
        }

        // create an Observable that will emit a stream of numbers
        subscription = Observable
                .just("one", "two", "three", "four", "five")
                // simulate a network delay
                .delay(10, TimeUnit.SECONDS)
                // do the work on the pre-defined io background Thread
                .subscribeOn(Schedulers.io())
                // handle onNext/onComplete/onError on Android UI thread
                .observeOn(AndroidSchedulers.mainThread())
                // allow us to intercept the stream to simulate an error
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (simulateError) {
                            throw OnErrorThrowable.from(new IOException("Network error"));
                        }
                    }
                })
                // transform the number string into a Model
                .map(new Func1<String, Model>() {
                    @Override
                    public Model call(String number) {
                        Model model = new Model();
                        model.number = number;
                        return model;
                    }
                })
                // start the stream (cold Observables require subscription to start)
                .subscribe(
                        // handle onNext
                        new Action1<Model>() {
                            @Override
                            public void call(Model model) {
                                getView().setData(model);
                                getView().showContent();
                            }
                        },
                        // handle onError
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                getView().showError(throwable);
                            }
                        });
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        // NOTE: don't forget the View is being re-created and attached even though we are not
    }

    /**
     * Called when Activity gets destroyed, so cancel running background task
     * @param retainPresenterInstance
     */
    @Override
    public void detachView(boolean retainPresenterInstance){
        super.detachView(retainPresenterInstance);
        if (!retainPresenterInstance){
            // if we decided not to retain the Presenter with setRetainInstance() in our View
            // here would be a good place to cancel any long running tasks or clean up references
        }
    }

    public void onCheckedSimulateErrorCheckbox(boolean simulateError) {
        this.simulateError = simulateError;
        loadData();
    }
}
