package com.android.torrent.libtorrent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.torrent.TorrentAddTask;
import com.android.torrent.TorrentHandle;
import com.frostwire.jlibtorrent.swig.torrent_handle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

abstract class LibTorrentAddTask extends TorrentAddTask {

    protected LibTorrentAddTask(@NonNull TorrentHandle<torrent_handle> handle) {
        super(handle);
    }

    @Nullable
    @Override
    protected final byte[] loadMetadata(@NonNull Uri uri) {
        byte[] metadata = null;
        HttpURLConnection connection = null;
        ByteArrayOutputStream stream = null;
        try {
            connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
            connection.connect();
            int len;
            byte[] buffer = new byte[1024];
            stream = new ByteArrayOutputStream();
            while ((len = connection.getInputStream().read(buffer)) != -1) {
                stream.write(buffer, 0, len);
            }
            metadata = stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return metadata;
    }
}