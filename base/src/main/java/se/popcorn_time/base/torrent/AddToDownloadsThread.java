package se.popcorn_time.base.torrent;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.frostwire.jlibtorrent.swig.torrent_status;

import java.util.concurrent.TimeUnit;

import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.utils.Logger;

public class AddToDownloadsThread implements Runnable {

    private Context context;
    private TorrentService service;
    private DownloadInfo info;

    public AddToDownloadsThread(Context context, TorrentService service, DownloadInfo info) {
        this.context = context;
        this.service = service;
        this.info = info;
    }

    @Override
    public void run() {
        String torrentFile;
        try {
            torrentFile = TorrentUtil.addTorrent(service, info);
        } catch (InterruptedException e) {
            Logger.error("AddToDownloadsThread: interrupt", e);
            return;
        }
        if (TextUtils.isEmpty(torrentFile)) {
            info.state = TorrentState.ERROR;
            Downloads.update(context, info);
            Logger.debug("AddToDownloadsThread: State error - " + info.title);
        } else {
            FileEntry videoFileEntry = TorrentUtil.setFilePriority(service, torrentFile, info.fileName, TorrentPriority.NORMAL);
            if (videoFileEntry == null) {
                Downloads.delete(context, info);
                return;
            }
            checking(torrentFile);
            String selection = Downloads._TORRENT_URL + "=\"" + info.torrentUrl + "\"";
            Cursor cursor = Downloads.query(context, null, selection, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    Downloads.populate(info, cursor);
                    if (TorrentUtil.saveMetadata(service, info, torrentFile) || info.size <= 0) {
                        info.size = videoFileEntry.size;
                        Downloads.update(context, info);
                    }
                    if (TorrentState.PAUSED == info.state) {
                        service.pauseTorrent(torrentFile);
                    }
                }
                cursor.close();
            } else {
                service.removeTorrent(torrentFile);
            }
        }
    }

    private void checking(String torrentFile) {
        boolean wait;
        do {
            int state = service.getTorrentState(torrentFile);
            wait = torrent_status.state_t.queued_for_checking.swigValue() == state
                    || torrent_status.state_t.checking_files.swigValue() == state
                    || torrent_status.state_t.checking_resume_data.swigValue() == state;
            if (wait) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    wait = false;
                }
            }
        } while (wait);
    }
}