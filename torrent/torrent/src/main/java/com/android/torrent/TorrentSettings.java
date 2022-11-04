package com.android.torrent;

public interface TorrentSettings {

    void setMaximumDownloadSpeed(int speed);

    int getMaximumDownloadSpeed();

    void setMaximumUploadSpeed(int speed);

    int getMaximumUploadSpeed();

    void setConnectionsLimit(int limit);

    int getConnectionsLimit();
}