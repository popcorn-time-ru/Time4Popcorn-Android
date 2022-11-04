package com.player.cast;

import android.support.annotation.NonNull;

public abstract class CastDeviceItem<Device> {

    private final Device mDevice;

    public CastDeviceItem(@NonNull Device device) {
        this.mDevice = device;
    }

    @NonNull
    public final Device getDevice() {
        return mDevice;
    }

    public abstract String getServiceName();

    public abstract String getDeviceName();
}