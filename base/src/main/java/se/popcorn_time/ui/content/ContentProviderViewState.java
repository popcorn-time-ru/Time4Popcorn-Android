package se.popcorn_time.ui.content;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

import se.popcorn_time.model.content.IContentProvider;

public final class ContentProviderViewState extends ViewState<IContentProviderView> {

    private final IContentProvider[] contentProviders;
    private IContentProvider contentProvider;

    public ContentProviderViewState(@NonNull Presenter<IContentProviderView> presenter,
                                    @NonNull IContentProvider[] contentProviders,
                                    @NonNull IContentProvider contentProvider) {
        super(presenter);
        this.contentProviders = contentProviders;
        this.contentProvider = contentProvider;
    }

    public void apply(@NonNull IContentProvider contentProvider) {
        this.contentProvider = contentProvider;
        apply();
    }

    @Override
    public void apply(@NonNull IContentProviderView view) {
        view.onContentProvider(contentProviders, contentProvider);
    }
}
