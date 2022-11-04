package com.android.torrent;

import android.support.annotation.NonNull;

public abstract class FileEntity<File> {

    @NonNull
    protected final File _file;

    protected final int _index;

    @NonNull
    private TorrentPriority _priority = TorrentPriority.NOT_DOWNLOAD;

    public FileEntity(@NonNull File file, @NonNull TorrentPriority priority, int index) {
        _file = file;
        _priority = priority;
        _index = index;
    }

    @NonNull
    public TorrentPriority getPriority() {
        return _priority;
    }

    public void setPriority(@NonNull TorrentPriority priority) {
        this._priority = priority;
    }

    public final int getIndex() {
        return _index;
    }

    public abstract long getSize();

    public abstract String getPath();
}