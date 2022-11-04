package se.popcorn_time.base.torrent.client;

import android.content.Context;
import android.database.Cursor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.base.receiver.ConnectivityReceiver;
import se.popcorn_time.base.torrent.AddToDownloadsThread;
import se.popcorn_time.base.torrent.TorrentState;

public class MainClient extends BaseClient {

    final int DEFAULT_INIT_THREADS_COUNT = 3;

    private ExecutorService executorService;
    private boolean init;

    ClientConnectionListener connectionListener = new ClientConnectionListener() {
        @Override
        public void onClientConnected() {
            if (!init) {
//                setProxy(null);
                ConnectivityReceiver.checkWifiConnection(context);
                initDownloads();
                init = true;
            }
        }

        @Override
        public void onClientDisconnected() {

        }
    };

    public MainClient(Context context) {
        super(context);
        init = false;
        setConnectionListener(connectionListener);
    }

    public void exitFromApp() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        System.exit(0);
    }

    public void removeLastOnExit() {
        if (!bound) {
            return;
        }
        String last = Prefs.getPopcornPrefs().get(PopcornPrefs.LAST_TORRENT, "");
        if (!last.equals("")) {
            removeTorrent(last);
            Prefs.getPopcornPrefs().put(PopcornPrefs.LAST_TORRENT, "");
        }
    }

    private void initDownloads() {
        Cursor cursor = Downloads.query(context, null, null, null, Downloads._ID + " DESC");
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int threadsCount = cursor.getCount() >= DEFAULT_INIT_THREADS_COUNT ? DEFAULT_INIT_THREADS_COUNT : cursor.getCount();
                executorService = Executors.newFixedThreadPool(threadsCount);
                do {
                    final DownloadInfo info = new DownloadInfo();
                    Downloads.populate(info, cursor);
                    if (TorrentState.ERROR == info.state) {
                        info.state = TorrentState.DOWNLOADING;
                        Downloads.update(context, info);
                    }
                    executorService.execute(new AddToDownloadsThread(context, torrentService, info));
                } while (cursor.moveToNext());
                executorService.shutdown();
            }
            cursor.close();
        }
    }
}