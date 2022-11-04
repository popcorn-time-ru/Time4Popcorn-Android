package se.popcorn_time.mvp;

import android.support.annotation.NonNull;

public abstract class ViewState<T> implements IViewState<T> {

    private final Presenter<T> presenter;

    public ViewState(@NonNull Presenter<T> presenter) {
        this.presenter = presenter;
    }

    public final void apply() {
        presenter.apply(ViewState.this);
    }
}
