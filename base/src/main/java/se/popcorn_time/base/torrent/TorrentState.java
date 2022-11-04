package se.popcorn_time.base.torrent;

import com.frostwire.jlibtorrent.swig.torrent_status;

public class TorrentState {
    public static final int FINISHED = torrent_status.state_t.finished.swigValue();
    public static final int DOWNLOADING = torrent_status.state_t.downloading.swigValue();
    public static final int SEEDING = torrent_status.state_t.seeding.swigValue();
    public static final int PAUSED = 1001;
    public static final int ERROR = 1002;
}