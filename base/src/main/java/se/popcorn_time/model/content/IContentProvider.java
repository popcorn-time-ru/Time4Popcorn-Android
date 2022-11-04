package se.popcorn_time.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.filter.IFilter;

public interface IContentProvider {

    @NonNull
    String getType();

    @NonNull
    IFilter[] getFilters();

    @NonNull
    Iterator<Observable<List<? extends VideoInfo>>> getContentIterator(@Nullable String keywords);

    @Nullable
    IDetailsProvider[] getDetailsProviders();

    @Nullable
    ISubtitlesProvider getSubtitlesProvider();
}
