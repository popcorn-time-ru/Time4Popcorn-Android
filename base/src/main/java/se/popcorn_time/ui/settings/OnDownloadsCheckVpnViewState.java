package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnDownloadsCheckVpnViewState extends ViewState<ISettingsView> {

    private Boolean downloadsCheckVpn;
    private Boolean checkVpnOptionEnabled;

    public OnDownloadsCheckVpnViewState(@NonNull Presenter<ISettingsView> presenter,
                                        @NonNull Boolean downloadsCheckVpn,
                                        @NonNull Boolean checkVpnOptionEnabled) {
        super(presenter);
        this.downloadsCheckVpn = downloadsCheckVpn;
        this.checkVpnOptionEnabled = checkVpnOptionEnabled;
    }

    public OnDownloadsCheckVpnViewState setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn) {
        this.downloadsCheckVpn = downloadsCheckVpn;
        return this;
    }

    public OnDownloadsCheckVpnViewState setCheckVpnOptionEnabled(@NonNull Boolean checkVpnOptionEnabled) {
        this.checkVpnOptionEnabled = checkVpnOptionEnabled;
        return this;
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onDownloadsCheckVpn(downloadsCheckVpn, checkVpnOptionEnabled);
    }
}
