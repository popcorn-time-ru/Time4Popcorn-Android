package se.popcorn_time.mvp;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class Presenter<T> implements IPresenter<T> {

    private final Set<T> views = Collections.newSetFromMap(new WeakHashMap<T, Boolean>());

    @Override
    public final void attach(@NonNull T view) {
        if (!views.contains(view)) {
            if (views.isEmpty()) {
                onCreate();
            }
            views.add(view);
            onAttach(view);
        }
    }

    @Override
    public final void detach(@NonNull T view) {
        if (views.contains(view)) {
            views.remove(view);
            onDetach(view);
            if (views.isEmpty()) {
                onDestroy();
            }
        }
    }

    @NonNull
    protected final Iterable<T> getViews() {
        return views;
    }

    protected final void apply(@NonNull IViewState<T> state) {
        for (T view : views) {
            state.apply(view);
        }
    }

    protected void onCreate() {
    }

    protected void onAttach(@NonNull T view) {
    }

    protected void onDetach(@NonNull T view) {
    }

    protected void onDestroy() {
    }
}
