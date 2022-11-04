package com.android.torrent.libtorrent;

import android.support.annotation.NonNull;
import android.util.Log;

import com.frostwire.jlibtorrent.AlertListener;
import com.frostwire.jlibtorrent.alerts.AddTorrentAlert;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert;
import com.frostwire.jlibtorrent.alerts.MetadataFailedAlert;
import com.frostwire.jlibtorrent.alerts.MetadataReceivedAlert;
import com.frostwire.jlibtorrent.alerts.PieceFinishedAlert;
import com.frostwire.jlibtorrent.alerts.SaveResumeDataAlert;
import com.frostwire.jlibtorrent.alerts.TorrentAddedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentAlert;
import com.frostwire.jlibtorrent.alerts.TorrentCheckedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentErrorAlert;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentPausedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentRemovedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentResumedAlert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

abstract class LibTorrentAlertListener implements AlertListener {

    private static final Map<String, Method> ALERT_METHODS = new HashMap<>();

    private static final int[] ALERT_TYPES = new int[]{
            AlertType.TORRENT_ADDED.getSwig(),
            AlertType.TORRENT_REMOVED.getSwig(),
            AlertType.TORRENT_RESUMED.getSwig(),
            AlertType.TORRENT_PAUSED.getSwig(),
            AlertType.TORRENT_CHECKED.getSwig(),
            AlertType.TORRENT_FINISHED.getSwig(),
            AlertType.TORRENT_ERROR.getSwig(),
            AlertType.ADD_TORRENT.getSwig(),
            AlertType.METADATA_RECEIVED.getSwig(),
            AlertType.METADATA_FAILED.getSwig(),
            AlertType.PIECE_FINISHED.getSwig(),
            AlertType.BLOCK_FINISHED.getSwig(),
            AlertType.SAVE_RESUME_DATA.getSwig()
    };

    static {
        for (Method method : LibTorrentAlertListener.class.getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && TorrentAlert.class.isAssignableFrom(parameterTypes[0])) {
                ALERT_METHODS.put(parameterTypes[0].getName(), method);
            }
        }
    }

    public LibTorrentAlertListener() {

    }

    @Override
    public int[] types() {
        return ALERT_TYPES;
    }

    @Override
    public final void alert(Alert<?> alert) {
        if (alert instanceof TorrentAlert && !(alert instanceof PieceFinishedAlert) && !(alert instanceof BlockFinishedAlert)) {
            Log.d("torrent", alert.toString());
        }
        Method method = ALERT_METHODS.get(alert.getClass().getName());
        if (method != null) {
            try {
                method.invoke(LibTorrentAlertListener.this, alert);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void onTorrentAdded(@NonNull TorrentAddedAlert alert);

    public abstract void onTorrentRemoved(@NonNull TorrentRemovedAlert alert);

    public abstract void onTorrentResumed(@NonNull TorrentResumedAlert alert);

    public abstract void onTorrentPaused(@NonNull TorrentPausedAlert alert);

    public abstract void onTorrentChecked(@NonNull TorrentCheckedAlert alert);

    public abstract void onTorrentFinished(@NonNull TorrentFinishedAlert alert);

    public abstract void onTorrentError(@NonNull TorrentErrorAlert alert);

    public abstract void onAddTorrent(@NonNull AddTorrentAlert alert);

    public abstract void onMetadataReceived(@NonNull MetadataReceivedAlert alert);

    public abstract void onMetadataFailed(@NonNull MetadataFailedAlert alert);

    public abstract void onPieceFinished(@NonNull PieceFinishedAlert alert);

    public abstract void onBlockFinished(@NonNull BlockFinishedAlert alert);

    public abstract void onSaveResumeData(@NonNull SaveResumeDataAlert alert);
}