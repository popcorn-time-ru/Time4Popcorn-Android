package com.player.dialog;

import android.support.annotation.NonNull;

import com.connectsdk.device.ConnectableDevice;
import com.player.cast.CastDeviceControlDialog;

public final class ConnectableDeviceControlDialog extends CastDeviceControlDialog<ConnectableDevice> {

    @Override
    protected void onDeviceDisconnect(@NonNull ConnectableDevice connectableDevice) {
        connectableDevice.disconnect();
    }
}