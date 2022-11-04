package se.popcorn_time.ui.share;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

import se.popcorn_time.model.share.IShareData;

public final class OnShowShareDataViewState extends ViewState<IShareView> {

    private IShareData shareData;

    public OnShowShareDataViewState(@NonNull Presenter<IShareView> presenter, @NonNull IShareData shareData) {
        super(presenter);
        this.shareData = shareData;
    }

    public OnShowShareDataViewState setShareData(@NonNull IShareData shareData) {
        this.shareData = shareData;
        return this;
    }

    @Override
    public void apply(@NonNull IShareView view) {
        view.onShowShareData(shareData);
    }
}
