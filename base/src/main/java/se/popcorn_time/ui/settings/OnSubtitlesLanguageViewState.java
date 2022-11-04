package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnSubtitlesLanguageViewState extends ViewState<ISettingsView> {

    private String subtitlesLanguage;

    public OnSubtitlesLanguageViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull String subtitlesLanguage) {
        super(presenter);
        this.subtitlesLanguage = subtitlesLanguage;
    }

    public void apply(@NonNull String subtitlesLanguage) {
        this.subtitlesLanguage = subtitlesLanguage;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onSubtitlesLanguage(subtitlesLanguage);
    }
}
