package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnAboutSiteViewState extends ViewState<ISettingsView> {

    private String siteUrl;

    public OnAboutSiteViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull String siteUrl) {
        super(presenter);
        this.siteUrl = siteUrl;
    }

    public void apply(@NonNull String siteUrl) {
        this.siteUrl = siteUrl;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onAboutSite(siteUrl);
    }
}
