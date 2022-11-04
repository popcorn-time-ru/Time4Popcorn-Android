package com.android.torrent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public abstract class TorrentHandle<Torrent> {

    @NonNull
    protected final Uri uri;

    @Nullable
    protected Torrent torrent;

    protected TorrentHandle(@NonNull Uri uri, @Nullable Torrent torrent) {
        this.uri = uri;
        this.torrent = torrent;
    }

    @NonNull
    public final Uri getUri() {
        return uri;
    }

    @Nullable
    public final Torrent getTorrent() {
        return torrent;
    }

    @NonNull
    public abstract File getSaveDir();

    @NonNull
    public abstract String getName();

    @NonNull
    public abstract String getInfoHash();

    @Nullable
    public abstract byte[] getMetadata();

    @Nullable
    public abstract FileEntity getFile(int index);

    @Nullable
    public abstract FileEntity[] getFiles();

    public abstract void setFilePriority(int index, @NonNull TorrentPriority priority);

    @Nullable
    public abstract TorrentPriority[] getFilePriorities();

    @Nullable
    public abstract TorrentPriority[] getPiecePriorities();

    public abstract long getPieceSize();

    public abstract boolean havePiece(int index);

    public abstract int getDownloadRate();

    public abstract long getTotalSize();

    public abstract long getTotalDownloadedSize();

    public abstract float getProgress();

    public abstract boolean isPaused();

    public abstract boolean isFinished();

    public abstract void pause();

    public abstract void resume();

    public abstract void saveResumeData();

    @Nullable
    public abstract byte[] getResumeData();

    @Nullable
    public abstract Throwable getError();

    protected abstract boolean op_equals(@NonNull Torrent torrent);
}