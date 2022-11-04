package se.popcorn_time.ui.share;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;

import se.popcorn_time.base.analytics.Analytics;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.model.share.IShareData;
import se.popcorn_time.model.share.IShareUseCase;
import se.popcorn_time.model.share.ShareUseCase;

public final class SharePresenter extends Presenter<IShareView> implements ISharePresenter {

    private final IShareUseCase shareUseCase;

    private OnShowShareDataViewState onShowShareDataViewState;

    public SharePresenter(@NonNull IShareUseCase shareUseCase) {
        this.shareUseCase = shareUseCase;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        switch (shareUseCase.getData().getType()) {
            case IShareData.TYPE_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_DIALOG_IS_SHOWN);
                break;
            case IShareData.TYPE_VIDEO_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_VIDEO_DIALOG_IS_SHOWN);
                break;
            case IShareData.TYPE_FOCUS_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_FOCUS_DIALOG_IS_SHOWN);
                break;
        }
        onShowShareDataViewState = new OnShowShareDataViewState(SharePresenter.this, shareUseCase.getData());
    }

    @Override
    protected void onAttach(@NonNull IShareView view) {
        super.onAttach(view);
        onShowShareDataViewState.apply(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void share() {
        Prefs.getPopcornPrefs().put(ShareUseCase.WAS_SHARED_FROM_DIALOG, true);
        switch (shareUseCase.getData().getType()) {
            case IShareData.TYPE_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_DIALOG_SHARE_IS_CLICKED);
                break;
            case IShareData.TYPE_VIDEO_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_VIDEO_DIALOG_SHARE_IS_CLICKED);
                break;
            case IShareData.TYPE_FOCUS_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_FOCUS_DIALOG_SHARE_IS_CLICKED);
                break;
        }
    }

    @Override
    public void cancel() {
        switch (shareUseCase.getData().getType()) {
            case IShareData.TYPE_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_DIALOG_IS_CANCELED);
                break;
            case IShareData.TYPE_VIDEO_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_VIDEO_DIALOG_IS_CANCELED);
                break;
            case IShareData.TYPE_FOCUS_SHARE:
                Analytics.event(Analytics.Category.UI, Analytics.Event.SHARE_FOCUS_DIALOG_IS_CANCELED);
                break;
        }
    }
}
