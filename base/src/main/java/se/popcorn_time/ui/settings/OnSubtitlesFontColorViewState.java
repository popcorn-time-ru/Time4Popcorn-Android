package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnSubtitlesFontColorViewState extends ViewState<ISettingsView> {

    private String subtitlesFontColor;

    public OnSubtitlesFontColorViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull String subtitlesFontColor) {
        super(presenter);
        this.subtitlesFontColor = subtitlesFontColor;
    }

    public void apply(@NonNull String subtitlesFontColor) {
        this.subtitlesFontColor = subtitlesFontColor;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onSubtitlesFontColor(subtitlesFontColor);
    }
}
