package se.popcorn_time.ui.vpn;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

import java.util.Collection;

import se.popcorn_time.api.vpn.VpnClient;

public final class OnVpnClientsViewState extends ViewState<IVpnView> {

    private Collection<VpnClient> vpnClients;
    private String connectOnStartVpnPackage;

    public OnVpnClientsViewState(@NonNull Presenter<IVpnView> presenter, @NonNull Collection<VpnClient> vpnClients, @NonNull String connectOnStartVpnPackage) {
        super(presenter);
        this.vpnClients = vpnClients;
        this.connectOnStartVpnPackage = connectOnStartVpnPackage;
    }

    public void apply(@NonNull Collection<VpnClient> vpnClients) {
        this.vpnClients = vpnClients;
        apply();
    }

    public void apply(@NonNull String connectOnStartVpnPackage) {
        this.connectOnStartVpnPackage = connectOnStartVpnPackage;
        apply();
    }

    @Override
    public void apply(@NonNull IVpnView view) {
        view.onVpnClients(vpnClients, connectOnStartVpnPackage);
    }
}
