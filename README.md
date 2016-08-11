 This quick example demonstrates:
  * MVP with Mosby 2.0
  * Retaining state (e.g. loading / showing content) across configuration changes (portrait->landscape) with Mosby ViewState
  * Retaining the Presenter across configuration changes
    * The Presenter may be in the middle of accessing data / calling an API

 Notes: In this demo the presenter just constructs an Observable that simulates an API call by waiting 10 seconds before emitting a data stream (of number words) to be presented in the UI.

 Next steps you could look into:
  * Injecting the Presenter into MainActivity with Dagger 2
    * Bonus points for creating an (Activity scoped) ActivityComponent that uses an ApplicationComponent as a dependency
  * Use Java 8 lambdas in the RX bits - either directly in Android Studio 2.1+ or the retrolambda module
  * Use [Butterknife](http://jakewharton.github.io/butterknife/) to bind the views
  * Look at how [EventBus](https://github.com/greenrobot/EventBus) and [Android Priority Job Queue](https://github.com/path/android-priority-jobqueue) may be used to decouple the Presenter from the task of data loading.