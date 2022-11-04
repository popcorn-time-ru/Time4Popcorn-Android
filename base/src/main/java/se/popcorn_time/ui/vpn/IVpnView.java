package se.popcorn_time.ui.vpn;

import android.support.annotation.NonNull;

import java.util.Collection;

import se.popcorn_time.api.vpn.VpnClient;

public interface IVpnView {

    void onVpnClients(@NonNull Collection<VpnClient> vpnClients, @NonNull String connectOnStartVpnPackage);
}
