package se.popcorn_time.base.torrent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.frostwire.jlibtorrent.Vectors;
import com.frostwire.jlibtorrent.swig.add_torrent_params;
import com.frostwire.jlibtorrent.swig.alert;
import com.frostwire.jlibtorrent.swig.alert_ptr_deque;
import com.frostwire.jlibtorrent.swig.create_torrent;
import com.frostwire.jlibtorrent.swig.error_code;
import com.frostwire.jlibtorrent.swig.file_entry;
import com.frostwire.jlibtorrent.swig.file_storage;
import com.frostwire.jlibtorrent.swig.fingerprint;
import com.frostwire.jlibtorrent.swig.int64_vector;
import com.frostwire.jlibtorrent.swig.int_int_pair;
import com.frostwire.jlibtorrent.swig.int_vector;
import com.frostwire.jlibtorrent.swig.libtorrent;
import com.frostwire.jlibtorrent.swig.piece_finished_alert;
import com.frostwire.jlibtorrent.swig.proxy_settings;
import com.frostwire.jlibtorrent.swig.save_resume_data_alert;
import com.frostwire.jlibtorrent.swig.session;
import com.frostwire.jlibtorrent.swig.session_settings;
import com.frostwire.jlibtorrent.swig.storage_mode_t;
import com.frostwire.jlibtorrent.swig.string_int_pair;
import com.frostwire.jlibtorrent.swig.time_duration;
import com.frostwire.jlibtorrent.swig.torrent_finished_alert;
import com.frostwire.jlibtorrent.swig.torrent_handle;
import com.frostwire.jlibtorrent.swig.torrent_info;
import com.frostwire.jlibtorrent.swig.torrent_paused_alert;
import com.frostwire.jlibtorrent.swig.torrent_status;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.torrent.watch.WatchListener;
import se.popcorn_time.base.torrent.watch.WatchTask;
import se.popcorn_time.base.utils.Logger;

public final class TorrentService extends Service {

    public static final int MINIMUM_SPEED = 100000;
    public static final int DEFAULT_DOWNLOAD_SPEED = 0;
    public static final int DEFAULT_UPLOAD_SPEED = MINIMUM_SPEED;

    public static final int MIN_CONNECTIONS_LIMIT = 10;
    public static final int MAX_CONNECTIONS_LIMIT = 200;
    public static final int DEFAULT_CONNECTIONS_LIMIT = MAX_CONNECTIONS_LIMIT;

    public static final String ACTION_RESUME = "action-resume";
    public static final String ACTION_PAUSE = "action-pause";
    public static final String ACTION_STOP_WATCH = "action-stop-watch";

    private final IBinder binder = new TorrentBinder();

    private session mSession;
    private HashMap<String, torrent_handle> torrents = new HashMap<>();

    private boolean running;

    public class TorrentBinder extends Binder {
        public TorrentService getService() {
            return TorrentService.this;
        }
    }

    public TorrentService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        mSession.abort();
        Logger.debug("TorrentService: stopped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartTorrent();

        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            switch (intent.getAction()) {
                case ACTION_RESUME:
                    resumeSession();
                    Logger.debug("TorrentService: Action resume session");
                    break;
                case ACTION_PAUSE:
                    pauseSession();
                    Logger.debug("TorrentService: Action pause session");
                    break;
                case ACTION_STOP_WATCH:
                    stopWatch();
                    Logger.debug("TorrentService: Action stop watch");
                    break;
                default:
                    break;
            }
        }

        return START_STICKY;
    }

    private void onStartTorrent() {
        if (mSession != null) {
            return;
        }

        int_int_pair listenPortRange = new int_int_pair(50321, 50331);
        int flags = session.session_flags_t.start_default_features.swigValue() | session.session_flags_t.add_default_plugins.swigValue();
        int alertMask = alert.category_t.all_categories.swigValue()
                & ~(alert.category_t.dht_notification.swigValue()
                + alert.category_t.debug_notification.swigValue()
                + alert.category_t.stats_notification.swigValue());

        mSession = new session(new fingerprint("UT", 2, 2, 1, 0), listenPortRange, "0.0.0.0", flags, alertMask);
        mSession.add_dht_router(new string_int_pair("router.bittorrent.com", 6881));
        mSession.add_dht_router(new string_int_pair("dht.transmissionbt.com", 6881));
        mSession.start_dht();

        session_settings sessionSettings = mSession.settings();
        sessionSettings.setUser_agent("BTWebClient/2210(25031)");
        sessionSettings.setAlways_send_user_agent(true);
        sessionSettings.setAnonymous_mode(true);
        sessionSettings.setAnnounce_to_all_trackers(true);
        sessionSettings.setAnnounce_to_all_tiers(true);
        sessionSettings.setPrefer_udp_trackers(false);
        sessionSettings.setMax_peerlist_size(0);
        sessionSettings.setDownload_rate_limit(((IPopcornApplication) getApplication()).getSettingsUseCase().getDownloadsDownloadSpeed());
        sessionSettings.setUpload_rate_limit(((IPopcornApplication) getApplication()).getSettingsUseCase().getDownloadsUploadSpeed());
        sessionSettings.setConnections_limit(((IPopcornApplication) getApplication()).getSettingsUseCase().getDownloadsConnectionsLimit());
        mSession.set_settings(sessionSettings);

        alertsLoop();

        mSession.resume();
        Logger.debug("TorrentService: Started");
    }

    private void alertsLoop() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                alert_ptr_deque deque = new alert_ptr_deque();

                time_duration max_wait = libtorrent.milliseconds(500);

                while (running) {
                    alert ptr = mSession.wait_for_alert(max_wait);
                    if (ptr != null) {
                        mSession.pop_alerts(deque);
                        long size = deque.size();
                        for (int i = 0; i < size; i++) {
                            final alert swigAlert = deque.getitem(i);
                            final int type = swigAlert.type();
                            if (type == save_resume_data_alert.alert_type) {
                                final save_resume_data_alert srd_alert = alert.cast_to_save_resume_data_alert(swigAlert);
                                Downloads.update(TorrentService.this, srd_alert.getHandle().info_hash().to_hex(), Vectors.char_vector2bytes(srd_alert.getResume_data().bencode()));
                            } else if (piece_finished_alert.alert_type == type
                                    || torrent_paused_alert.alert_type == type
                                    || torrent_finished_alert.alert_type == type) {
                                alert.cast_to_torrent_alert(swigAlert).getHandle().save_resume_data();
                            }
                        }
                        deque.clear();
                    }
                }
            }
        };

        Thread t = new Thread(r, "alertsLoop");
        t.setDaemon(true);
        t.start();
    }

    /*
    * Session
    * */

    public void pauseSession() {
        if (mSession == null) {
            return;
        }
        if (!mSession.is_paused()) {
            mSession.pause();
        }
    }

    public void resumeSession() {
        if (mSession == null) {
            return;
        }
        if (mSession.is_paused()) {
            mSession.resume();
        }
    }

//    public void abortSession() {
//        if (mSession == null) {
//            return;
//        }
//        mSession.abort();
//    }

    public void setProxy(proxy_settings.proxy_type type, String hostname, int port, String username, String password) {
        if (mSession == null) {
            return;
        }
        proxy_settings proxySettings = new proxy_settings();
        proxySettings.setType((short) type.swigValue());
        proxySettings.setHostname(hostname);
        proxySettings.setPort(port);
        proxySettings.setUsername(username);
        proxySettings.setPassword(password);
        mSession.set_proxy(proxySettings);
        Logger.debug("TorrentService<setProxy>: " + type);
    }

    public void setConnectionsLimit(int connections) {
        if (mSession == null) {
            return;
        }
        try {
            session_settings sessionSettings = mSession.settings();
            sessionSettings.setConnections_limit(connections);
            mSession.set_settings(sessionSettings);
            Logger.debug("setConnectionsLimit: " + connections);
        } catch (UnknownError ue) {
            Logger.error("TorrentService<setConnectionsLimit>: Error", ue);
        }
    }

    public void setDownloadLimit(int speed) {
        if (mSession == null) {
            return;
        }
        try {
            session_settings sessionSettings = mSession.settings();
            sessionSettings.setDownload_rate_limit(speed);
            mSession.set_settings(sessionSettings);
            Logger.debug("setDownloadLimit: " + speed);
        } catch (UnknownError ue) {
            Logger.error("TorrentService<setDownloadLimit>: Error", ue);
        }
    }

    public void setUploadLimit(int speed) {
        if (mSession == null) {
            return;
        }
        try {
            session_settings sessionSettings = mSession.settings();
            sessionSettings.setUpload_rate_limit(speed);
            mSession.set_settings(sessionSettings);
            Logger.debug("setUploadLimit: " + speed);
        } catch (UnknownError ue) {
            Logger.error("TorrentService<setUploadLimit>: Error", ue);
        }
    }

    /*
    * Torrent
    * */

    public void addTorrent(String key, String savePath, byte[] resumeData) {
        if (mSession == null) {
            return;
        }
        if (torrents.containsKey(key)) {
            Logger.info("Already have torrent: " + key);
            return;
        }
        try {
            add_torrent_params params = add_torrent_params.create_instance();
            if (key.startsWith("http")) {
                params.setUrl(key);
            } else if (key.startsWith("magnet")) {
                error_code ec = new error_code();
                libtorrent.parse_magnet_uri(key, params, ec);
                if (ec.value() != 0) {
                    Logger.error("Not valid magnet: " + key);
                    return;
                }
            } else {
                File torrentFile = new File(key);
                if (torrentFile.exists()) {
                    params.setTi(new torrent_info(key));
                } else {
                    Logger.error("Not supported metadata path: " + key);
                    return;
                }
            }
            params.setStorage_mode(storage_mode_t.storage_mode_sparse);
            params.setSave_path(savePath);
            long flags = params.getFlags();
            flags &= ~add_torrent_params.flags_t.flag_auto_managed.swigValue();
            params.setFlags(flags);
            if (resumeData != null && resumeData.length > 0) {
                params.setResume_data(Vectors.bytes2char_vector(resumeData));
            }
            torrent_handle th = mSession.add_torrent(params);
            if (th.is_valid()) {
                th.move_storage(savePath);
                if (!th.status().getAuto_managed()) {
                    th.auto_managed(true);
                }
                if (th.status().getPaused()) {
                    th.resume();
                }
                torrents.put(key, th);
                Logger.debug("Torrent added<" + torrents.size() + ">: " + key);
            } else {
                mSession.remove_torrent(th);
                Logger.debug("Torrent not added: " + key);
            }
        } catch (UnknownError ue) {
            Logger.error("TorrentService<addTorrent>: Error", ue);
        }
    }

    public boolean hasTorrent(String key) {
        return mSession != null && torrents.containsKey(key);
    }

    public void removeTorrent(String key) {
        if (hasTorrent(key)) {
            try {
                torrents.get(key).auto_managed(false);
                mSession.remove_torrent(torrents.get(key));
            } catch (UnknownError ue) {
                Logger.error("TorrentService<removeTorrent>: Error", ue);
            }
            torrents.remove(key);
            Logger.debug("Torrent removed<" + torrents.size() + ">: " + key);
        }
    }

    public void pauseTorrent(String key) {
        if (hasTorrent(key)) {
            if (isWatchingNow(key)) {
                watchTask.pauseTorrent();
            } else {
                torrents.get(key).auto_managed(false);
                torrents.get(key).pause();
                Logger.debug("Torrent paused: " + key);
            }
        }
    }

    public void resumeTorrent(String key) {
        if (hasTorrent(key)) {
            torrents.get(key).resume();
            torrents.get(key).auto_managed(true);
            Logger.debug("Torrent resumed: " + key);
        }
    }

    public boolean isTorrentPaused(String key) {
        return hasTorrent(key) && torrents.get(key).status().getPaused();
    }

    public int getTorrentState(String key) {
        if (hasTorrent(key)) {
            return torrents.get(key).status().getState().swigValue();
        }
        return -1;
    }

//    public void setSequentialDownload(String key, boolean sd) {
//        if (hasTorrent(key)) {
//            torrents.get(key).set_sequential_download(sd);
//        }
//    }

    public boolean hasMetadata(String key) {
        return hasTorrent(key) && torrents.get(key).status().getHas_metadata();
    }

    public Status getStatus(String key) {
        if (hasMetadata(key)) {
            torrent_status torrentStatus = torrents.get(key).status();
            Status status = new Status();
            status.hash = torrentStatus.getInfo_hash().to_hex();
            status.seeds = torrentStatus.getList_seeds();
            status.peers = torrentStatus.getList_peers() - status.seeds;
            return status;
        }
        return null;
    }

    public boolean saveMetadata(String key, File metadataFile) {
        if (hasMetadata(key)) {
            create_torrent createTorrent = new create_torrent(torrents.get(key).torrent_file());
            byte[] metadata = Vectors.char_vector2bytes(createTorrent.generate().bencode());
            try {
                FileUtils.writeByteArrayToFile(metadataFile, metadata);
                Logger.debug("Metadata write to: " + metadataFile.getAbsolutePath());
                return true;
            } catch (IOException e) {
                Logger.error("saveMetadata", e);
            }
        }
        return false;
    }

    public void saveResumeData(String key) {
        if (hasTorrent(key)) {
            torrents.get(key).save_resume_data();
        }
    }

    public String getTorrentName(String key) {
        if (hasMetadata(key)) {
            return torrents.get(key).torrent_file().name();
        }
        return null;
    }

    public List<FileEntry> getFiles(String key) {
        if (hasMetadata(key)) {
            ArrayList<FileEntry> files = new ArrayList<>();
            file_storage fileStorage = torrents.get(key).torrent_file().files();
            for (int i = 0; i < fileStorage.num_files(); i++) {
                file_entry fileEntry = fileStorage.at(i);
                files.add(new FileEntry(fileEntry.getPath(), fileEntry.getSize()));
            }
            return files;
        }
        return null;
    }

    public int[] getFilePriorities(String key) {
        if (hasMetadata(key)) {
            int_vector file_priorities = torrents.get(key).file_priorities();
            int[] priorities = new int[(int) file_priorities.size()];
            for (int i = 0; i < priorities.length; i++) {
                priorities[i] = file_priorities.get(i);
            }
            return priorities;
        }
        return null;
    }

    public void setFilePriorities(String key, int[] priorities) {
        if (hasMetadata(key)) {
            for (int i = 0; i < priorities.length; i++) {
                torrents.get(key).file_priority(i, priorities[i]);
            }
        }
    }

    public int[] getPiecePriorities(String key) {
        if (hasMetadata(key)) {
            int_vector piece_priorities = torrents.get(key).piece_priorities();
            int[] priorities = new int[(int) piece_priorities.size()];
            for (int i = 0; i < priorities.length; i++) {
                priorities[i] = piece_priorities.get(i);
            }
            return priorities;
        }
        return null;
    }

    // torrents.get(key).torrent_file().num_pieces() - call out of memory!!!!!

    public void setPiecePriorities(String key, int[] priorities) {
        if (hasMetadata(key)) {
            for (int i = 0; i < priorities.length; i++) {
                torrents.get(key).piece_priority(i, priorities[i]);
            }
        }
    }

    public void setPieceDeadline(String key, int index, int deadline) {
        if (hasMetadata(key)) {
            torrents.get(key).set_piece_deadline(index, deadline);
        }
    }

    public void clearPieceDeadlines(String key) {
        if (hasMetadata(key)) {
            torrents.get(key).clear_piece_deadlines();
        }
    }

    public boolean havePiece(String key, int piece) {
        return hasMetadata(key) && torrents.get(key).have_piece(piece);
    }

    public String getTorrentSpeed(String key) {
        if (hasMetadata(key)) {
            torrent_status.state_t state = torrents.get(key).status().getState();
            if (torrent_status.state_t.queued_for_checking != state
                    && torrent_status.state_t.checking_files != state
                    && torrent_status.state_t.checking_resume_data != state) {
                int rate = torrents.get(key).status().getDownload_payload_rate();
                if (rate >= 1000000) {
                    return String.format("%.2f", ((float) rate / StorageUtil.SIZE_MB)) + "MB/s";
                } else if (rate >= 1000) {
                    return String.format("%.2f", ((float) rate / StorageUtil.SIZE_KB)) + "KB/s";
                } else {
                    return rate + "B/s";
                }
            }
        }
        return "0B/s";
    }

    public int getDownloadSizeMb(String key) {
        if (hasMetadata(key)) {
            long size = 0;
            int64_vector fileProgress = new int64_vector();
            torrents.get(key).file_progress(fileProgress);
            for (int i = 0; i < fileProgress.size(); i++) {
                size += fileProgress.get(i);
            }
            if (size > 0) {
                return (int) (size / StorageUtil.SIZE_MB);
            }
        }
        return 0;
    }

    public void setPriority(String key, int priority) {
        if (hasMetadata(key)) {
            torrents.get(key).set_priority(priority);
        }
    }

    public int getPieceLength(String key) {
        if (hasMetadata(key)) {
            return torrents.get(key).torrent_file().piece_length();
        }
        return 0;
    }

    /*
    * Watch
    * */

    private WatchTask watchTask;

    public void startWatch(WatchInfo watchInfo, WatchListener listener) {
        if (isWatchAlive()) {
            Logger.info("Watch task already running!");
            watchTask.addWatchListener(listener);
        } else {
            watchTask = new WatchTask(TorrentService.this, watchInfo);
            watchTask.addWatchListener(listener);
            watchTask.start();
        }
    }

    public boolean addWatchListener(WatchListener listener) {
        if (watchTask != null) {
            watchTask.addWatchListener(listener);
        }
        return false;
    }

    public boolean removeWatchListener(WatchListener listener) {
        if (watchTask != null) {
            watchTask.removeWatchListener(listener);
        }
        return false;
    }

    public boolean seekWatch(float delta) {
        return isWatchAlive() && watchTask.seek(delta);
    }

    private boolean isWatchAlive() {
        return watchTask != null && watchTask.isAlive();
    }

    private boolean isWatchingNow(String torrentFile) {
        return isWatchAlive() && watchTask.isWatchingNow(torrentFile);
    }

    private void stopWatch() {
        if (watchTask != null) {
            watchTask.interrupt();
            watchTask = null;
        }
    }

    /*
    * Statics
    * */

    public static void start(Context context) {
        context.startService(createIntent(context));
    }

    public static void stop(Context context) {
        context.stopService(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, TorrentService.class);
    }
}