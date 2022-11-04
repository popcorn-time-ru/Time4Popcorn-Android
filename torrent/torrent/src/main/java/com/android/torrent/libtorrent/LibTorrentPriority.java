package com.android.torrent.libtorrent;

import android.support.annotation.NonNull;

import com.android.torrent.TorrentPriority;

public final class LibTorrentPriority {

    public static final int NOT_DOWNLOAD = 0;
    public static final int NORMAL = 1;
    public static final int HIGH = 7;

    public static int convertPriority(@NonNull TorrentPriority priority) {
        switch (priority) {
            case NOT_DOWNLOAD:
                return NOT_DOWNLOAD;
            case HIGH:
                return HIGH;
            case NORMAL:
            default:
                return NORMAL;
        }
    }

    @NonNull
    public static TorrentPriority convertPriority(int priority) {
        switch (priority) {
            case NOT_DOWNLOAD:
                return TorrentPriority.NOT_DOWNLOAD;
            case HIGH:
                return TorrentPriority.HIGH;
            case NORMAL:
            default:
                return TorrentPriority.NORMAL;
        }
    }
}