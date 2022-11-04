package com.player.cast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import eu.sesma.castania.castserver.NanoHTTPD;
import eu.sesma.castania.castserver.SimpleWebServer;

public final class WebServerService extends Service {
    public final static int DEFAULT_SERVER_PORT = 8080;

    public final static String EXTRA_HOST = "host";
    public final static String EXTRA_PORT = "port";
    public final static String EXTRA_ROOT_DIR = "root-dir";

    private NanoHTTPD server;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String host = intent.getStringExtra(EXTRA_HOST);
            int port = intent.getIntExtra(EXTRA_PORT, DEFAULT_SERVER_PORT);
            String rootDir = intent.getStringExtra(EXTRA_ROOT_DIR);
            if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(rootDir)) {
                if (server != null) {
                    server.stop();
                }
                server = new SimpleWebServer(host, port, new File(rootDir), false);
                try {
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public static String start(@NonNull Context context, @NonNull String host, int port, @NonNull String rootDir) {
        Intent intent = new Intent(context, WebServerService.class);
        intent.putExtra(EXTRA_HOST, host);
        intent.putExtra(EXTRA_PORT, port);
        intent.putExtra(EXTRA_ROOT_DIR, rootDir);
        context.startService(intent);
        return "http://" + host + ":" + port;
    }

    public static String start(@NonNull Context context, @NonNull String rootDir) throws UnknownHostException {
        return start(context, getLocalHost(context), DEFAULT_SERVER_PORT, rootDir);
    }

    public static void stop(@NonNull Context context) {
        context.stopService(new Intent(context, WebServerService.class));
    }

    @NonNull
    public static String getLocalHost(@NonNull Context context) throws UnknownHostException {
        int ipAddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress();
        byte[] ipAddressBytes = {
                (byte) (0xff & ipAddress),
                (byte) (0xff & (ipAddress >> 8)),
                (byte) (0xff & (ipAddress >> 16)),
                (byte) (0xff & (ipAddress >> 24))
        };
        return InetAddress.getByAddress(ipAddressBytes).getHostAddress();
    }
}