package se.popcorn_time.model.vpn;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import se.popcorn_time.api.vpn.VpnClient;

public final class VpnUseCase implements IVpnUseCase {

    private final Subject<Collection<VpnClient>> vpnClientsSubject = PublishSubject.create();
    private final Map<String, VpnClient> vpnClients = new HashMap<>();

    private VpnClient activeClient;

    public VpnUseCase() {
    }

    @Override
    public boolean isVpnConnected() {
        return activeClient != null && VpnClient.STATUS_CONNECTED == activeClient.getStatus();
    }

    @Override
    public void clearVpnClients() {
        vpnClients.clear();
        vpnClientsSubject.onNext(vpnClients.values());
    }

    @Override
    public void setVpnClient(@NonNull VpnClient vpnClient) {
        vpnClients.put(vpnClient.getPackageName(), vpnClient);
        if (VpnClient.STATUS_CONNECTED == vpnClient.getStatus()) {
            if (activeClient == null) {
                activeClient = vpnClient;
            }
        } else if (VpnClient.STATUS_DISCONNECTED == vpnClient.getStatus()) {
            if (activeClient != null && activeClient.getPackageName().equals(vpnClient.getPackageName())) {
                activeClient = null;
            }
        }
        vpnClientsSubject.onNext(vpnClients.values());
    }

    @NonNull
    @Override
    public Collection<VpnClient> getVpnClients() {
        return vpnClients.values();
    }

    @NonNull
    @Override
    public Observable<Collection<VpnClient>> getVpnClientsObservable() {
        return vpnClientsSubject;
    }
}
