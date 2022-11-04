package se.popcorn_time.mobile.ui.base;

public interface ContentLoadListener {

    public void showLoading();

    public void showError();

    public void retryLoad();
}