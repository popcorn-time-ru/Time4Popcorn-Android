package se.popcorn_time.ui.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import se.popcorn_time.mvp.Presenter;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import se.popcorn_time.model.content.IContentStatus;
import se.popcorn_time.model.content.IContentUseCase;

public final class ContentStatusPresenter extends Presenter<IContentStatusView> implements IContentStatusPresenter {

    private final IContentUseCase contentUseCase;

    private ContentStatusViewState contentStatusViewState;

    private Disposable disposable;

    public ContentStatusPresenter(@NonNull IContentUseCase contentUseCase) {
        this.contentUseCase = contentUseCase;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        contentStatusViewState = new ContentStatusViewState(ContentStatusPresenter.this, contentUseCase.getContentStatus());
        disposable = contentUseCase.getContentStatusObservable().subscribe(new Consumer<IContentStatus>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull IContentStatus contentStatus) throws Exception {
                contentStatusViewState.apply(contentStatus);
            }
        });
    }

    @Override
    protected void onAttach(@NonNull IContentStatusView view) {
        super.onAttach(view);
        view.onKeywords(contentUseCase.getKeywords());
        contentStatusViewState.apply(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        contentStatusViewState = null;
    }

    @Override
    public void setKeywords(@Nullable String keywords) {
        contentUseCase.setKeywords(keywords);
    }

    @Override
    public void getContent(boolean reset) {
        contentUseCase.getContent(reset);
    }
}
