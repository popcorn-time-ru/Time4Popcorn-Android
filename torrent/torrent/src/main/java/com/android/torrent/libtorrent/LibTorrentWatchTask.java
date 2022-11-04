package com.android.torrent.libtorrent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.torrent.WatchListener;
import com.android.torrent.WatchTask;

public final class LibTorrentWatchTask extends WatchTask<LibTorrentHandle, LibTorrentFileEntity> {

    public LibTorrentWatchTask(@NonNull LibTorrentHandle torrentEntity, @NonNull LibTorrentFileEntity fileEntity, @Nullable WatchListener listener) {
        super(torrentEntity, fileEntity, listener);
    }

    @Override
    protected void update(int first, int last, int position, int window) {
        int count = 0;
        if (first < last) {
            for (int i = position; i <= last; i++) {
                if (!_torrentHandle.havePiece(i)) {
                    _torrentHandle.getTorrent().set_piece_deadline(i, i - first + 1);
                    count++;
                }
                if (count >= window) {
                    break;
                }
            }
        } else if (first > last) {
            for (int i = first; i >= last; i--) {
                if (!_torrentHandle.havePiece(i)) {
                    _torrentHandle.getTorrent().set_piece_deadline(i, first - i + 1);
                    count++;
                }
                if (count >= window) {
                    break;
                }
            }
        }
    }
}