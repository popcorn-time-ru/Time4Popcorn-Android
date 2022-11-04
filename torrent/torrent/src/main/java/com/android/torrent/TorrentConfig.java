package com.android.torrent;

import android.support.annotation.NonNull;

public interface TorrentConfig {

    @NonNull
    String getUniqueId();

    @NonNull
    String getUserAgent();

    int getMajorVersion();

    int getMinorVersion();

    int getRevisionVersion();

    int getFirstListenPort();

    int getLastListenPort();

    long getMaximumDownloadSpeed();

    long getMaximumUploadSpeed();

    int getConnectionsLimit();
}