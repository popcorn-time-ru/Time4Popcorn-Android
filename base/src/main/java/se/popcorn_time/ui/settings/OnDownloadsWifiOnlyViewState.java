package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnDownloadsWifiOnlyViewState extends ViewState<ISettingsView> {

    private Boolean downloadsWifiOnly;

    public OnDownloadsWifiOnlyViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull Boolean downloadsWifiOnly) {
        super(presenter);
        this.downloadsWifiOnly = downloadsWifiOnly;
    }

    public void apply(@NonNull Boolean downloadsWifiOnly) {
        this.downloadsWifiOnly = downloadsWifiOnly;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onDownloadsWifiOnly(downloadsWifiOnly);
    }
}
