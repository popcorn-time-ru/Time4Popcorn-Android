package com.android.torrent;

import android.support.annotation.NonNull;

public class DefaultTorrentConfig implements TorrentConfig {

    @NonNull
    @Override
    public String getUniqueId() {
        return "TC";
    }

    @NonNull
    @Override
    public String getUserAgent() {
        return "TorrentClient/100(1)";
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getRevisionVersion() {
        return 0;
    }

    @Override
    public int getFirstListenPort() {
        return 12345;
    }

    @Override
    public int getLastListenPort() {
        return 12355;
    }

    @Override
    public long getMaximumDownloadSpeed() {
        return 0;
    }

    @Override
    public long getMaximumUploadSpeed() {
        return 0;
    }

    @Override
    public int getConnectionsLimit() {
        return 100;
    }
}