package se.popcorn_time.base.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import se.popcorn_time.api.PopcornApi;
import se.popcorn_time.api.vpn.PopcornVpnApi;
import se.popcorn_time.api.vpn.VpnClient;
import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.utils.Logger;

public final class AppApiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            final String action = intent.getAction();
            final int type = PopcornApi.getActionType(intent.getExtras());
            Logger.debug("AppApiReceiver<onReceive>: " + action + "/" + type);
            if (PopcornApi.ACTION_VPN.equals(action) && PopcornVpnApi.TYPE_VPN_STATUS == type) {
                final VpnClient client = AppApi.getVpnClient(intent.getExtras());
                if (client != null) {
                    ((IPopcornApplication) context.getApplicationContext()).getVpnUseCase().setVpnClient(client);
                }
            }
        }
    }
}
