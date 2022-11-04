package com.android.torrent.libtorrent;

import android.support.annotation.NonNull;

import com.android.torrent.FileEntity;
import com.android.torrent.TorrentPriority;
import com.frostwire.jlibtorrent.swig.file_entry;

public final class LibTorrentFileEntity extends FileEntity<file_entry> {

    @NonNull
    private final String _savePath;

    public LibTorrentFileEntity(@NonNull String savePath, @NonNull file_entry file, @NonNull TorrentPriority priority, int index) {
        super(file, priority, index);
        _savePath = savePath;
    }

    @Override
    public long getSize() {
        return _file.getSize();
    }

    @Override
    public String getPath() {
        return _savePath + "/" + _file.getPath();
    }
}