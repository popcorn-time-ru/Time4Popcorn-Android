package se.popcorn_time.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class InstallReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ((PopcornApplication) context.getApplicationContext()).onReferrerReceive(intent.getStringExtra("referrer"));
    }
}
