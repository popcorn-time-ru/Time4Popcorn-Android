package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnLanguageViewState extends ViewState<ISettingsView> {

    private String language;

    public OnLanguageViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull String language) {
        super(presenter);
        this.language = language;
    }

    public void onLanguage(@NonNull String language) {
        this.language = language;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onLanguage(language);
    }
}