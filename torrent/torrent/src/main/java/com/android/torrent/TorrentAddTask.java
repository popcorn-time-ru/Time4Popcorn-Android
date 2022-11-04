package com.android.torrent;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class TorrentAddTask extends AsyncTask<Void, Void, Void> {

    @NonNull
    private final TorrentHandle handle;

    public TorrentAddTask(@NonNull TorrentHandle handle) {
        this.handle = handle;
    }

    @Override
    protected final Void doInBackground(Void... params) {
        byte[] metadata = loadMetadata(handle.getUri());
        if (metadata == null || metadata.length == 0) {
            onMetadataLoadError(handle);
        } else {
            onMetadataLoaded(handle, metadata);
        }
        return null;
    }

    @Nullable
    protected abstract byte[] loadMetadata(@NonNull Uri uri);

    protected abstract void onMetadataLoaded(@NonNull TorrentHandle handle, @NonNull byte[] metadata);

    protected abstract void onMetadataLoadError(@NonNull TorrentHandle handle);
}