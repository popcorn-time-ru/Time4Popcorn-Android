package se.popcorn_time.ui.vpn;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;

import java.util.Collection;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import se.popcorn_time.api.vpn.VpnClient;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.model.vpn.IVpnUseCase;

public final class VpnPresenter extends Presenter<IVpnView> implements IVpnPresenter {

    private final IVpnUseCase vpnUseCase;

    private OnVpnClientsViewState onVpnClientsViewState;

    private Disposable disposable;

    public VpnPresenter(@NonNull IVpnUseCase vpnUseCase) {
        this.vpnUseCase = vpnUseCase;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        onVpnClientsViewState = new OnVpnClientsViewState(
                this,
                vpnUseCase.getVpnClients(),
                Prefs.getPopcornPrefs().get(PopcornPrefs.ON_START_VPN_PACKAGE, "")
        );
        disposable = vpnUseCase.getVpnClientsObservable().subscribe(new Consumer<Collection<VpnClient>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Collection<VpnClient> vpnClients) throws Exception {
                onVpnClientsViewState.apply(vpnClients);
            }
        });
    }

    @Override
    protected void onAttach(@NonNull IVpnView view) {
        super.onAttach(view);
        onVpnClientsViewState.apply(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        onVpnClientsViewState = null;
    }
}
