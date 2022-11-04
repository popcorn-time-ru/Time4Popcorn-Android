package com.player.dialog;

import android.support.annotation.NonNull;

import com.connectsdk.device.ConnectableDevice;
import com.player.cast.CastDeviceItem;

public final class ConnectableDeviceItem extends CastDeviceItem<ConnectableDevice> {

    public ConnectableDeviceItem(@NonNull ConnectableDevice connectableDevice) {
        super(connectableDevice);
    }

    @Override
    public String getServiceName() {
        return getDevice().getConnectedServiceNames();
    }

    @Override
    public String getDeviceName() {
        return getDevice().getFriendlyName();
    }
}