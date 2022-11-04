package com.player.dialog;

import android.os.Bundle;

import com.connectsdk.core.Util;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.player.cast.CastDeviceItem;
import com.player.cast.CastDevicesDialog;
import com.player.cast.CastDevicesItemAdapter;

public final class ConnectableDevicesDialog extends CastDevicesDialog<ConnectableDevice> implements DiscoveryManagerListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCastDevicesItemAdapter = new CastDevicesItemAdapter<>(getActivity());

        DiscoveryManager.getInstance().addListener(ConnectableDevicesDialog.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DiscoveryManager.getInstance().removeListener(ConnectableDevicesDialog.this);
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, final ConnectableDevice device) {
        if (!device.isConnectable()) {
            return;
        }

        Util.runOnUI(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < mCastDevicesItemAdapter.getCount(); i++) {
                    CastDeviceItem<ConnectableDevice> castDeviceItem = mCastDevicesItemAdapter.getItem(i);

                    if (castDeviceItem.getDevice().getIpAddress().equals(device.getIpAddress())) {
                        mCastDevicesItemAdapter.remove(castDeviceItem);
                        mCastDevicesItemAdapter.insert(new ConnectableDeviceItem(device), i);
                        return;
                    }

                    String newDeviceName = device.getFriendlyName();
                    String dName = castDeviceItem.getDevice().getFriendlyName();

                    if (newDeviceName == null) {
                        newDeviceName = device.getModelName();
                    }

                    if (dName == null) {
                        dName = castDeviceItem.getDevice().getModelName();
                    }

                    if (newDeviceName.compareToIgnoreCase(dName) < 0) {
                        mCastDevicesItemAdapter.insert(new ConnectableDeviceItem(device), i);
                        return;
                    }
                }

                mCastDevicesItemAdapter.add(new ConnectableDeviceItem(device));
            }
        });
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        Util.runOnUI(new Runnable() {

            @Override
            public void run() {
                mCastDevicesItemAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, final ConnectableDevice device) {
        Util.runOnUI(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < mCastDevicesItemAdapter.getCount(); i++) {
                    CastDeviceItem<ConnectableDevice> castDeviceItem = mCastDevicesItemAdapter.getItem(i);
                    if (castDeviceItem.getDevice().getIpAddress().equals(device.getIpAddress())) {
                        mCastDevicesItemAdapter.remove(castDeviceItem);
                        break;
                    }

                    String newDeviceName = device.getFriendlyName();
                    String dName = castDeviceItem.getDevice().getFriendlyName();

                    if (newDeviceName == null) {
                        newDeviceName = device.getModelName();
                    }

                    if (dName == null) {
                        dName = castDeviceItem.getDevice().getModelName();
                    }

                    if (newDeviceName.compareToIgnoreCase(dName) < 0) {
                        mCastDevicesItemAdapter.remove(castDeviceItem);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        Util.runOnUI(new Runnable() {

            @Override
            public void run() {
                mCastDevicesItemAdapter.clear();
            }
        });
    }
}