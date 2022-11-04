package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnSubtitlesFontSizeViewState extends ViewState<ISettingsView> {

    private Float subtitlesFontSize;

    public OnSubtitlesFontSizeViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull Float subtitlesFontSize) {
        super(presenter);
        this.subtitlesFontSize = subtitlesFontSize;
    }

    public void apply(@NonNull Float subtitlesFontSize) {
        this.subtitlesFontSize = subtitlesFontSize;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onSubtitlesFontSize(subtitlesFontSize);
    }
}
