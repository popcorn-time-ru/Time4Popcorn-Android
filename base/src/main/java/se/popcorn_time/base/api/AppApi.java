package se.popcorn_time.base.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import se.popcorn_time.api.PopcornApi;
import se.popcorn_time.api.vpn.PopcornVpnApi;
import se.popcorn_time.api.vpn.VpnClient;

public final class AppApi extends PopcornApi {

    private AppApi() {
    }

    public static void start(@NonNull Context context) {
        send(context, ACTION_APP, TYPE_START, null, Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    }

    public static void stop(@NonNull Context context) {
        send(context, ACTION_APP, TYPE_STOP);
    }

    public static void getVpnStatus(@NonNull Context context) {
        send(context, ACTION_APP, PopcornVpnApi.TYPE_GET_VPN_STATUS, null, Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    }

    public static void connectVpn(@NonNull Context context, @NonNull VpnClient client) {
        Bundle extras = new Bundle();
        extras.putString(PopcornVpnApi.KEY_VPN_PACKAGE_NAME, client.getPackageName());
        send(context, ACTION_APP, PopcornVpnApi.TYPE_VPN_CONNECT, extras);
    }

    public static void disconnectVpn(@NonNull Context context, @NonNull VpnClient client) {
        Bundle extras = new Bundle();
        extras.putString(PopcornVpnApi.KEY_VPN_PACKAGE_NAME, client.getPackageName());
        send(context, ACTION_APP, PopcornVpnApi.TYPE_VPN_DISCONNECT, extras);
    }

    @Nullable
    static VpnClient getVpnClient(@NonNull Bundle extras) {
        return extras.getParcelable(PopcornVpnApi.KEY_VPN_CLIENT);
    }
}