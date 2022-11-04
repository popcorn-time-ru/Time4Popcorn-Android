package se.popcorn_time.base.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class DeviceUtils {

    private DeviceUtils() {
    }

    @Nullable
    public static String getMACAddress(@NonNull String interfaceName) {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                if (!networkInterface.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder builder = new StringBuilder();
                    for (byte b : mac) {
                        builder.append(String.format("%02X:", b));
                    }
                    if (builder.length() > 0) {
                        builder.deleteCharAt(builder.length() - 1);
                    }
                    return builder.toString();
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @NonNull
    public static String getDeviceId(@NonNull Context context) {
        final String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String macAddress = getMACAddress("wlan0");
        if (TextUtils.isEmpty(macAddress)) {
            macAddress = getMACAddress("eth0");
        }
        final String name = device_id
                + "-" + name_part(Build.SERIAL, device_id)
                + "-" + name_part(Build.BOARD, device_id)
                + "-" + (TextUtils.isEmpty(macAddress) ? device_id : macAddress);
        return UUID.nameUUIDFromBytes(name.getBytes()).toString().replaceAll("-", "");
    }

    private static String name_part(String part, String def) {
        if (TextUtils.isEmpty(part) || Build.UNKNOWN.equals(part)) {
            return def;
        }
        return part;
    }
}
