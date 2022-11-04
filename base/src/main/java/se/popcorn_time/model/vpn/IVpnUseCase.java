package se.popcorn_time.model.vpn;

import android.support.annotation.NonNull;

import java.util.Collection;

import io.reactivex.Observable;
import se.popcorn_time.api.vpn.VpnClient;

public interface IVpnUseCase {

    boolean isVpnConnected();

    void clearVpnClients();

    void setVpnClient(@NonNull VpnClient vpnClient);

    @NonNull
    Collection<VpnClient> getVpnClients();

    @NonNull
    Observable<Collection<VpnClient>> getVpnClientsObservable();
}
