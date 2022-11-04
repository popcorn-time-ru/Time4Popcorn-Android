package se.popcorn_time.mvp;

import android.support.annotation.NonNull;

public abstract class ValueViewState<T, V> implements IViewState<T> {

    private final V v;

    public ValueViewState(@NonNull V v) {
        this.v = v;
    }

    @Override
    public final void apply(@NonNull T view) {
        apply(view, v);
    }

    protected abstract void apply(@NonNull T view, @NonNull V v);
}
