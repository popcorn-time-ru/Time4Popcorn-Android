package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnStartPageViewState extends ViewState<ISettingsView> {

    private Integer startPage;

    public OnStartPageViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull Integer startPage) {
        super(presenter);
        this.startPage = startPage;
    }

    public void onStartPage(@NonNull Integer startPage) {
        this.startPage = startPage;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onStartPage(startPage);
    }
}
