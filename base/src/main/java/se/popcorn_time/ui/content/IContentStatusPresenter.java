package se.popcorn_time.ui.content;

import android.support.annotation.Nullable;

import se.popcorn_time.mvp.IPresenter;

public interface IContentStatusPresenter extends IPresenter<IContentStatusView> {

    void setKeywords(@Nullable String keywords);

    void getContent(boolean reset);
}
