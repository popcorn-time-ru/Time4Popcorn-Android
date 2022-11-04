package se.popcorn_time.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.filter.IFilter;

public interface IContentUseCase {

    @NonNull
    IContentProvider[] getContentProviders();

    @NonNull
    IContentProvider getContentProvider();

    void setContentProvider(@NonNull IContentProvider contentProvider);

    @NonNull
    Observable<IContentProvider> getContentProviderObservable();

    @NonNull
    IContentStatus getContentStatus();

    @NonNull
    Observable<IContentStatus> getContentStatusObservable();

    @NonNull
    Observable<IFilter> getFilterCheckedObservable();

    @Nullable
    String getKeywords();

    void setKeywords(@Nullable String keywords);

    void getContent(boolean reset);

    @Nullable
    IDetailsProvider[] getDetailsProviders(@NonNull VideoInfo videoInfo);

    @Nullable
    ISubtitlesProvider getSubtitlesProvider(@NonNull VideoInfo videoInfo);

    @Nullable
    ISubtitlesProvider getSubtitlesProvider(@NonNull WatchInfo watchInfo);
}
