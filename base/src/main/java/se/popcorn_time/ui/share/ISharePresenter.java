package se.popcorn_time.ui.share;

import se.popcorn_time.mvp.IPresenter;

public interface ISharePresenter extends IPresenter<IShareView> {

    void share();

    void cancel();
}
