package se.popcorn_time.ui.content;

import android.support.annotation.NonNull;

import se.popcorn_time.model.content.IContentProvider;
import se.popcorn_time.model.filter.IFilter;

public interface IContentProviderView {

    void onContentProvider(@NonNull IContentProvider[] contentProviders, @NonNull IContentProvider contentProvider);

    void onContentFilterChecked(@NonNull IFilter filter);
}
