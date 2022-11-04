package com.player.cast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class CastDeviceReceiver extends BroadcastReceiver {

    public static final String ACTION_PLAY = "com.player.cast.action.PLAY";
    public static final String ACTION_PAUSE = "com.player.cast.action.PAUSE";
    public static final String ACTION_DISCONNECT = "com.player.cast.action.DISCONNECT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_PLAY:
                play();
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_DISCONNECT:
                disconnect();
                break;
            default:
                break;
        }
    }

    protected abstract void play();

    protected abstract void pause();

    protected abstract void disconnect();

    public final void register(@NonNull Context context, @Nullable IntentFilter filter) {
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction(CastDeviceReceiver.ACTION_PLAY);
            filter.addAction(CastDeviceReceiver.ACTION_PAUSE);
            filter.addAction(CastDeviceReceiver.ACTION_DISCONNECT);
        }
        context.registerReceiver(CastDeviceReceiver.this, filter);
    }

    public final void unregister(@NonNull Context context) {
        context.unregisterReceiver(CastDeviceReceiver.this);
    }
}