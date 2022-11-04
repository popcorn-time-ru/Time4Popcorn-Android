package se.popcorn_time.base.torrent.watch;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class WatchHandler {

    public static final int ERROR = 1;
    public static final int METADATA_LOAD = 2;
    public static final int SUBTITLES_LOADED = 3;
    public static final int DOWNLOAD_STARTED = 4;
    public static final int VIDEO_PREPARED = 5;

    public static final int UPDATE_PROGRESS = 7;
    public static final int DOWNLOAD_FINISHED = 8;
    public static final int BUFFERING_FINISHED = 10;

    private List<WatchListener> listeners = new ArrayList<>();

    public WatchHandler() {

    }

    public List<WatchListener> getListeners() {
        return listeners;
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR:
                    if (msg.obj != null && msg.obj instanceof WatchException) {
                        for (WatchListener listener : listeners) {
                            listener.onError((WatchException) msg.obj);
                        }
                    }
                    break;
                case METADATA_LOAD:
                    for (WatchListener listener : listeners) {
                        listener.onMetadataLoad();
                    }
                    break;
                case SUBTITLES_LOADED:
                    if (msg.obj != null && msg.obj instanceof String) {
                        for (WatchListener listener : listeners) {
                            listener.onSubtitlesLoaded((String) msg.obj);
                        }
                    }
                    break;
                case DOWNLOAD_STARTED:
                    if (msg.obj != null && msg.obj instanceof String) {
                        for (WatchListener listener : listeners) {
                            listener.onDownloadStarted((String) msg.obj);
                        }
                    }
                    break;
                case VIDEO_PREPARED:
                    if (msg.obj != null && msg.obj instanceof String) {
                        for (WatchListener listener : listeners) {
                            listener.onVideoPrepared((String) msg.obj);
                        }
                    }
                    break;
                case UPDATE_PROGRESS:
                    if (msg.obj != null && msg.obj instanceof WatchProgress) {
                        for (WatchListener listener : listeners) {
                            listener.onUpdateProgress((WatchProgress) msg.obj);
                        }
                    }
                    break;
                case DOWNLOAD_FINISHED:
                    for (WatchListener listener : listeners) {
                        listener.onDownloadFinished();
                    }
                    break;

                case BUFFERING_FINISHED:
                    for (WatchListener listener : listeners) {
                        listener.onBufferingFinished();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    public void sendMessage(int what) {
        sendMessage(what, null);
    }

    public void sendMessage(int what, Object obj) {
        Message msg = handler.obtainMessage(what, obj);
        msg.sendToTarget();
    }

    public void removeMessages() {
        handler.removeCallbacksAndMessages(null);
    }
}