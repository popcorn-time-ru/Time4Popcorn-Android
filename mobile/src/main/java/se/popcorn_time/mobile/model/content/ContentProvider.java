package se.popcorn_time.mobile.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.content.IContentProvider;
import se.popcorn_time.model.content.IDetailsProvider;
import se.popcorn_time.model.content.ISubtitlesProvider;
import se.popcorn_time.model.filter.IFilter;

public abstract class ContentProvider<T extends VideoInfo> implements IContentProvider {

    final ContentType<T> type;
    final ContentRepository repository;
    final IFilter[] filters;
    private final IDetailsProvider[] detailsProviders;
    private final ISubtitlesProvider subtitlesProvider;

    public ContentProvider(@NonNull ContentType<T> type,
                           @NonNull ContentRepository repository,
                           @NonNull IFilter[] filters,
                           @Nullable IDetailsProvider[] detailsProviders,
                           @Nullable ISubtitlesProvider subtitlesProvider) {
        this.type = type;
        this.repository = repository;
        this.filters = filters;
        this.detailsProviders = detailsProviders;
        this.subtitlesProvider = subtitlesProvider;
    }

    @NonNull
    @Override
    public String getType() {
        return type.getType();
    }

    @NonNull
    @Override
    public IFilter[] getFilters() {
        return filters;
    }

    @Nullable
    @Override
    public IDetailsProvider[] getDetailsProviders() {
        return detailsProviders;
    }

    @Nullable
    @Override
    public ISubtitlesProvider getSubtitlesProvider() {
        return subtitlesProvider;
    }

    abstract class ContentIterator implements Iterator<Observable<List<? extends VideoInfo>>>, Consumer<List<? extends VideoInfo>> {

        static final int DEFAULT_LIMIT = 75;

        private final String keywords;
        private final int limit;
        private int page;
        private boolean next;

        ContentIterator(@Nullable String keywords) {
            this(keywords, DEFAULT_LIMIT);
        }

        ContentIterator(@Nullable String keywords, int limit) {
            this(keywords, limit, 1);
        }

        ContentIterator(@Nullable String keywords, int limit, int page) {
            this.keywords = keywords;
            this.limit = limit;
            this.page = page;
            this.next = true;
        }

        @Override
        public final boolean hasNext() {
            return next;
        }

        @Override
        public final Observable<List<? extends VideoInfo>> next() {
            if (next) {
                return getVideos(keywords, page).doOnNext(ContentIterator.this);
            }
            return Observable.error(new NoSuchElementException());
        }

        @Override
        public final void accept(List<? extends VideoInfo> videos) throws Exception {
            next = limit == videos.size();
            page++;
        }

        @NonNull
        protected abstract Observable<List<? extends VideoInfo>> getVideos(@Nullable String keywords, int page);
    }
}
