package com.android.torrent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class TorrentClient<Torrent> implements TorrentSettings, TorrentListener {

    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";
    public static final String SCHEME_MAGNET = "magnet";

    protected final TorrentConfig config;

    private final Set<TorrentHandle<Torrent>> torrents = new HashSet<>();
    private final Set<TorrentListener> listeners = new HashSet<>();

    public TorrentClient() {
        this(new DefaultTorrentConfig());
    }

    public TorrentClient(@NonNull TorrentConfig config) {
        this.config = config;
    }

    public abstract void start();

    public abstract void resume();

    public abstract void pause();

    public abstract void stop();

    @Nullable
    public abstract TorrentHandle add(@NonNull Uri uri, @NonNull File saveDir, @Nullable byte[] resumeData);

    public abstract boolean remove(@NonNull TorrentHandle handle, boolean removeFiles);

    public abstract void startWatch(@NonNull TorrentHandle handle, @NonNull FileEntity fileEntity, @Nullable WatchListener listener);

    public abstract void stopWatch();

    public final void addListener(@NonNull TorrentListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(@NonNull TorrentListener listener) {
        listeners.remove(listener);
    }

    @NonNull
    public final Collection<TorrentHandle<Torrent>> getTorrents() {
        return torrents;
    }

    @Override
    public final void onTorrentAdded(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentAdded(handle);
        }
    }

    @Override
    public final void onTorrentRemoved(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentRemoved(handle);
        }
    }

    @Override
    public final void onTorrentResumed(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentResumed(handle);
        }
    }

    @Override
    public final void onTorrentPaused(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentPaused(handle);
        }
    }

    @Override
    public final void onTorrentChecked(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentChecked(handle);
        }
    }

    @Override
    public final void onTorrentFinished(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentFinished(handle);
        }
    }

    @Override
    public final void onTorrentError(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onTorrentError(handle);
        }
    }

    @Override
    public final void onMetadataReceived(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onMetadataReceived(handle);
        }
    }

    @Override
    public final void onMetadataFailed(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onMetadataFailed(handle);
        }
    }

    @Override
    public final void onPieceFinished(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onPieceFinished(handle);
        }
    }

    @Override
    public final void onBlockFinished(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onBlockFinished(handle);
        }
    }

    @Override
    public final void onSaveResumeData(@NonNull TorrentHandle handle) {
        for (TorrentListener listener : listeners) {
            listener.onSaveResumeData(handle);
        }
    }

    @Nullable
    protected final TorrentHandle<Torrent> findTorrent(@NonNull Torrent torrent) {
        for (TorrentHandle<Torrent> handle : torrents) {
            if (handle.op_equals(torrent)) {
                return handle;
            }
        }
        return null;
    }

    protected final void addTorrent(@NonNull TorrentHandle<Torrent> handle) {
        torrents.add(handle);
    }

    protected final void removeTorrent(@NonNull TorrentHandle<Torrent> handle) {
        torrents.remove(handle);
    }
}