package se.popcorn_time.ui.content;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.IPresenter;

import se.popcorn_time.model.content.IContentProvider;

public interface IContentProviderPresenter extends IPresenter<IContentProviderView> {

    void setContentProvider(@NonNull IContentProvider contentProvider);
}
