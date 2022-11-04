package se.popcorn_time.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.filter.IFilter;

public final class ContentUseCase implements IContentUseCase, IContentStatus, Observer<List<? extends VideoInfo>>, IFilter.OnCheckedListener {

    private final Subject<IContentProvider> contentProviderSubject = PublishSubject.create();
    private final Subject<IContentStatus> contentStatusSubject = PublishSubject.create();
    private final Subject<IFilter> filterCheckedSubject = PublishSubject.create();
    private final List<VideoInfo> list = new ArrayList<>();

    private final IContentProvider[] contentProviders;

    private IContentProvider contentProvider;
    private Iterator<Observable<List<? extends VideoInfo>>> iterator;
    private Disposable disposable;
    private Throwable error;
    private String keywords;

    public ContentUseCase(@NonNull IContentProvider[] contentProviders, @NonNull IContentProvider contentProvider) {
        this.contentProviders = contentProviders;
        setContentProvider(contentProvider);
    }

    @NonNull
    @Override
    public IContentProvider[] getContentProviders() {
        return contentProviders;
    }

    @NonNull
    @Override
    public IContentProvider getContentProvider() {
        return contentProvider;
    }

    @Override
    public void setContentProvider(@NonNull IContentProvider contentProvider) {
        this.contentProvider = contentProvider;
        for (IFilter filter : this.contentProvider.getFilters()) {
            filter.setOnCheckedListener(null);
        }
        for (IFilter filter : contentProvider.getFilters()) {
            filter.setOnCheckedListener(ContentUseCase.this);
        }
        contentProviderSubject.onNext(contentProvider);
        getContent(true);
    }

    @NonNull
    @Override
    public Observable<IContentProvider> getContentProviderObservable() {
        return contentProviderSubject;
    }

    @NonNull
    @Override
    public IContentStatus getContentStatus() {
        return ContentUseCase.this;
    }

    @NonNull
    @Override
    public Observable<IContentStatus> getContentStatusObservable() {
        return contentStatusSubject;
    }

    @NonNull
    @Override
    public Observable<IFilter> getFilterCheckedObservable() {
        return filterCheckedSubject;
    }

    @Nullable
    @Override
    public String getKeywords() {
        return keywords;
    }

    @Override
    public void setKeywords(@Nullable String keywords) {
        if ((this.keywords == null && keywords == null) || (this.keywords != null && keywords != null && this.keywords.equals(keywords))) {
            return;
        }
        this.keywords = keywords;
        getContent(true);
    }

    @Override
    public void getContent(boolean reset) {
        if (reset) {
            list.clear();
            iterator = contentProvider.getContentIterator(keywords);
        }
        if (iterator.hasNext()) {
            iterator.next().subscribe(ContentUseCase.this);
        }
    }

    @Nullable
    @Override
    public IDetailsProvider[] getDetailsProviders(@NonNull VideoInfo videoInfo) {
        final IContentProvider contentProvider = getContentProvider(videoInfo.getType());
        if (contentProvider != null) {
            return contentProvider.getDetailsProviders();
        }
        return null;
    }

    @Nullable
    @Override
    public ISubtitlesProvider getSubtitlesProvider(@NonNull VideoInfo videoInfo) {
        final IContentProvider contentProvider = getContentProvider(videoInfo.getType());
        if (contentProvider != null) {
            return contentProvider.getSubtitlesProvider();
        }
        return null;
    }

    @Nullable
    @Override
    public ISubtitlesProvider getSubtitlesProvider(@NonNull WatchInfo watchInfo) {
        final IContentProvider contentProvider = getContentProvider(watchInfo.type);
        if (contentProvider != null) {
            return contentProvider.getSubtitlesProvider();
        }
        return null;
    }

    @Override
    public boolean isLoading() {
        return disposable != null && !disposable.isDisposed();
    }

    @Override
    public Throwable getError() {
        return error;
    }

    @Override
    public List<VideoInfo> getList() {
        return list;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
        error = null;
        contentStatusSubject.onNext(ContentUseCase.this);
    }

    @Override
    public void onNext(List<? extends VideoInfo> videoInfos) {
        list.addAll(videoInfos);
        contentStatusSubject.onNext(ContentUseCase.this);
    }

    @Override
    public void onError(Throwable error) {
        this.error = error;
        disposable = null;
        contentStatusSubject.onNext(ContentUseCase.this);
    }

    @Override
    public void onComplete() {
        disposable = null;
        contentStatusSubject.onNext(ContentUseCase.this);
    }

    @Override
    public void onFilterChecked(@NonNull IFilter filter) {
        filterCheckedSubject.onNext(filter);
        getContent(true);
    }

    @Nullable
    private IContentProvider getContentProvider(@NonNull String type) {
        for (IContentProvider contentProvider : contentProviders) {
            if (type.equals(contentProvider.getType())) {
                return contentProvider;
            }
        }
        return null;
    }
}
