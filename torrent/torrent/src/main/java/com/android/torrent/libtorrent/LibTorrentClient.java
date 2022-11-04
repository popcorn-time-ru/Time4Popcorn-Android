package com.android.torrent.libtorrent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.torrent.FileEntity;
import com.android.torrent.TorrentClient;
import com.android.torrent.TorrentConfig;
import com.android.torrent.TorrentHandle;
import com.android.torrent.WatchListener;
import com.android.torrent.WatchTask;
import com.frostwire.jlibtorrent.AlertListener;
import com.frostwire.jlibtorrent.DHT;
import com.frostwire.jlibtorrent.Fingerprint;
import com.frostwire.jlibtorrent.Pair;
import com.frostwire.jlibtorrent.Session;
import com.frostwire.jlibtorrent.SessionSettings;
import com.frostwire.jlibtorrent.Vectors;
import com.frostwire.jlibtorrent.alerts.AddTorrentAlert;
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert;
import com.frostwire.jlibtorrent.alerts.MetadataFailedAlert;
import com.frostwire.jlibtorrent.alerts.MetadataReceivedAlert;
import com.frostwire.jlibtorrent.alerts.PieceFinishedAlert;
import com.frostwire.jlibtorrent.alerts.SaveResumeDataAlert;
import com.frostwire.jlibtorrent.alerts.TorrentAddedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentCheckedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentErrorAlert;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentPausedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentRemovedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentResumedAlert;
import com.frostwire.jlibtorrent.swig.add_torrent_params;
import com.frostwire.jlibtorrent.swig.error_code;
import com.frostwire.jlibtorrent.swig.lazy_entry;
import com.frostwire.jlibtorrent.swig.libtorrent;
import com.frostwire.jlibtorrent.swig.storage_mode_t;
import com.frostwire.jlibtorrent.swig.torrent_handle;
import com.frostwire.jlibtorrent.swig.torrent_handle_vector;
import com.frostwire.jlibtorrent.swig.torrent_info;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class LibTorrentClient extends TorrentClient<torrent_handle> {

    private Session session;
    private DHT dht;

    @Nullable
    protected WatchTask<LibTorrentHandle, LibTorrentFileEntity> watchTask;
    private final List<TorrentHandle> watchPausedTorrents = new ArrayList<>();

    public LibTorrentClient() {
        super();
    }

    public LibTorrentClient(@NonNull TorrentConfig config) {
        super(config);
    }

    @Override
    public void start() {
        Fingerprint fingerprint = new Fingerprint(
                config.getUniqueId(),
                config.getMajorVersion(),
                config.getMinorVersion(),
                config.getRevisionVersion(),
                0);
        session = new Session(
                fingerprint,
                new Pair<>(config.getFirstListenPort(), config.getLastListenPort()),
                "0.0.0.0");

        SessionSettings settings = session.getSettings();
        settings.setUserAgent(config.getUserAgent());
        settings.setMaxPeerlistSize(0);
        settings.setDownloadRateLimit((int) config.getMaximumDownloadSpeed());
        settings.setUploadRateLimit((int) config.getMaximumUploadSpeed());
        settings.setConnectionsLimit(config.getConnectionsLimit());
        session.setSettings(settings);

        if (session.isPaused()) {
            session.resume();
        }

        dht = new DHT(session);
        dht.start();

        session.addListener(alertListener);
    }

    @Override
    public void resume() {
        if (session != null) {
            session.resume();
        }
    }

    @Override
    public void pause() {
        if (session != null) {
            session.pause();
        }
    }

    @Override
    public void stop() {
        if (dht != null) {
            dht.stop();
        }
        if (session != null) {
            session.removeListener(alertListener);
            session.abort();
        }
    }

    @Nullable
    @Override
    public TorrentHandle add(@NonNull Uri uri, @NonNull File saveDir, @Nullable byte[] resumeData) {
        for (TorrentHandle handle : getTorrents()) {
            if (handle.getUri().equals(uri)) {
                return handle;
            }
        }

        add_torrent_params params;
        switch (uri.getScheme()) {
            case SCHEME_FILE:
                params = add_torrent_params.create_instance();
                params.setTi(new torrent_info(uri.getPath()));
                break;
            case SCHEME_HTTP:
                params = add_torrent_params.create_instance();
                params.setUrl(uri.toString());
                break;
            case SCHEME_HTTPS:
                TorrentHandle<torrent_handle> handle = new LibTorrentHandle(uri, saveDir, null);
                new LibTorrentAddTask(handle) {

                    @Override
                    protected void onMetadataLoaded(@NonNull TorrentHandle handle, @NonNull byte[] metadata) {
                        lazy_entry entry = new lazy_entry();
                        error_code error = new error_code();
                        int ret = lazy_entry.bdecode(Vectors.bytes2char_vector(metadata), entry, error);
                        if (ret != 0 || error.value() != 0) {
                            ((LibTorrentHandle) handle).setError(new Exception("Metadata cannot be parsed"));
                            return;
                        }
                        add_torrent_params params = add_torrent_params.create_instance();
                        params.setTi(new torrent_info(entry));
                        populate_torrent_params(params, handle.getSaveDir(), null);
                        ((LibTorrentHandle) handle).setTorrent(session.getSwig().add_torrent(params));
                    }

                    @Override
                    protected void onMetadataLoadError(@NonNull TorrentHandle handle) {
                        ((LibTorrentHandle) handle).setError(new Exception("Metadata don't loaded"));
                    }
                }.execute();
                return handle;
            case SCHEME_MAGNET:
                params = add_torrent_params.create_instance();
                error_code error = new error_code();
                libtorrent.parse_magnet_uri(uri.toString(), params, error);
                if (error.value() != 0) {
                    return null;
                }
                break;
            default:
                return null;
        }

        populate_torrent_params(params, saveDir, resumeData);
        torrent_handle th = session.getSwig().add_torrent(params);
        TorrentHandle<torrent_handle> handle = findTorrent(th);
        if (handle == null) {
            handle = new LibTorrentHandle(uri, saveDir, th);
            addTorrent(handle);
        }
        return handle;
    }

    private void populate_torrent_params(@NonNull add_torrent_params params, @NonNull File saveDir, @Nullable byte[] resumeData) {
        params.setSave_path(saveDir.getAbsolutePath());
        params.setStorage_mode(storage_mode_t.storage_mode_sparse);
        long flags = params.getFlags();
        flags &= ~add_torrent_params.flags_t.flag_auto_managed.swigValue();
        params.setFlags(flags);
        if (resumeData != null) {
            params.setResume_data(Vectors.bytes2char_vector(resumeData));
        }
    }

    @Override
    public boolean remove(@NonNull TorrentHandle entity, boolean removeFiles) {
        if (entity instanceof LibTorrentHandle) {
            final LibTorrentHandle handle = (LibTorrentHandle) entity;
            final torrent_handle_vector vector = session.getSwig().get_torrents();
            for (int i = 0; i < vector.size(); i++) {
                if (vector.get(i).op_eq(handle.getTorrent())) {
                    session.getSwig().remove_torrent(handle.getTorrent(), removeFiles ? Session.Options.DELETE_FILES.getSwig() : 0);
                    return true;
                }
            }
            removeTorrent(handle);
            onTorrentRemoved(handle);
            return true;
        }
        return false;
    }

    @Override
    public void startWatch(@NonNull TorrentHandle torrentEntity, @NonNull FileEntity fileEntity, @Nullable WatchListener listener) {
        if (watchTask != null) {
            return;
        }
        if (torrentEntity instanceof LibTorrentHandle && fileEntity instanceof LibTorrentFileEntity) {
            pauseBeforeWatch(torrentEntity);
            watchTask = new LibTorrentWatchTask((LibTorrentHandle) torrentEntity, (LibTorrentFileEntity) fileEntity, listener);
        }
    }

    @Override
    public void stopWatch() {
        if (watchTask != null) {
            resumeAfterWatch();
            watchTask.cancel();
            watchTask = null;
        }
    }

    @Override
    public void setMaximumDownloadSpeed(int speed) {
        if (session != null) {
            SessionSettings settings = session.getSettings();
            settings.setDownloadRateLimit(speed);
            session.setSettings(settings);
        }
    }

    @Override
    public int getMaximumDownloadSpeed() {
        if (session != null) {
            return session.getSettings().getDownloadRateLimit();
        }
        return 0;
    }

    @Override
    public void setMaximumUploadSpeed(int speed) {
        if (session != null) {
            SessionSettings settings = session.getSettings();
            settings.setUploadRateLimit(speed);
            session.setSettings(settings);
        }
    }

    @Override
    public int getMaximumUploadSpeed() {
        if (session != null) {
            return session.getSettings().getUploadRateLimit();
        }
        return 0;
    }

    @Override
    public void setConnectionsLimit(int limit) {
        if (session != null) {
            SessionSettings settings = session.getSettings();
            settings.setConnectionsLimit(limit);
            session.setSettings(settings);
        }
    }

    @Override
    public int getConnectionsLimit() {
        if (session != null) {
            return session.getSettings().getConnectionsLimit();
        }
        return 0;
    }

    private void pauseBeforeWatch(@NonNull TorrentHandle watchEntity) {
        watchPausedTorrents.clear();
        for (TorrentHandle entity : getTorrents()) {
            if (!watchEntity.equals(entity) && !entity.isFinished() && !entity.isPaused()) {
                watchPausedTorrents.add(entity);
                entity.pause();
            }
        }
    }

    private void resumeAfterWatch() {
        for (TorrentHandle entity : watchPausedTorrents) {
            entity.resume();
        }
        watchPausedTorrents.clear();
    }

    private final AlertListener alertListener = new LibTorrentAlertListener() {

        @Override
        public void onTorrentAdded(@NonNull TorrentAddedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onTorrentAdded(handle);
            }
        }

        @Override
        public void onTorrentRemoved(@NonNull TorrentRemovedAlert alert) {
            TorrentHandle<torrent_handle> handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                removeTorrent(handle);
                LibTorrentClient.this.onTorrentRemoved(handle);
            }
        }

        @Override
        public void onTorrentResumed(@NonNull TorrentResumedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onTorrentResumed(handle);
            }
        }

        @Override
        public void onTorrentPaused(@NonNull TorrentPausedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onTorrentPaused(handle);
            }
        }

        @Override
        public void onTorrentChecked(@NonNull TorrentCheckedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onTorrentChecked(handle);
            }
        }

        @Override
        public void onTorrentFinished(@NonNull TorrentFinishedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onTorrentFinished(handle);
            }
        }

        @Override
        public void onTorrentError(@NonNull TorrentErrorAlert alert) {
            TorrentHandle<torrent_handle> handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                ((LibTorrentHandle) handle).setError(new Exception(alert.getSwig().message()));
                LibTorrentClient.this.onTorrentError(handle);
            }
        }

        @Override
        public void onAddTorrent(@NonNull AddTorrentAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onTorrentAdded(handle);
            }
        }

        @Override
        public void onMetadataReceived(@NonNull MetadataReceivedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onMetadataReceived(handle);
            }
        }

        @Override
        public void onMetadataFailed(@NonNull MetadataFailedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onMetadataFailed(handle);
            }
        }

        @Override
        public void onPieceFinished(@NonNull PieceFinishedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                if (watchTask != null && watchTask.getTorrentEntity().equals(handle)) {
                    watchTask.onPieceFinished(alert.getPieceIndex());
                }
                LibTorrentClient.this.onPieceFinished(handle);
            }
        }

        @Override
        public void onBlockFinished(@NonNull BlockFinishedAlert alert) {
            TorrentHandle handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                LibTorrentClient.this.onBlockFinished(handle);
            }
        }

        @Override
        public void onSaveResumeData(@NonNull SaveResumeDataAlert alert) {
            TorrentHandle<torrent_handle> handle = findTorrent(alert.getSwig().getHandle());
            if (handle != null) {
                ((LibTorrentHandle) handle).setResumeData(alert.getResumeData().bencode());
                LibTorrentClient.this.onSaveResumeData(handle);
            }
        }
    };
}