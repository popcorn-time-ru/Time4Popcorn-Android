package se.popcorn_time.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.torrent.TorrentService;
import se.popcorn_time.base.utils.Logger;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (CONNECTIVITY_CHANGE.equals(action)) {
                checkWifiConnection(context);
            }
        }
    }

    public static void checkWifiConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            if (((IPopcornApplication) context.getApplicationContext()).getSettingsUseCase().isDownloadsWifiOnly()) {
                if (ConnectivityManager.TYPE_WIFI == activeNetwork.getType()) {
                    resumeTorrentService(context);
                } else {
                    pauseTorrentService(context);
                }
            } else {
                resumeTorrentService(context);
            }
            Logger.debug("ConnectivityReceiver: Network connected");
        } else {
            Logger.debug("ConnectivityReceiver: Network disconnected");
        }
    }

    private static void resumeTorrentService(Context context) {
        Intent intent = TorrentService.createIntent(context);
        intent.setAction(TorrentService.ACTION_RESUME);
        context.startService(intent);
    }

    private static void pauseTorrentService(Context context) {
        Intent intent = TorrentService.createIntent(context);
        intent.setAction(TorrentService.ACTION_PAUSE);
        context.startService(intent);
    }
}