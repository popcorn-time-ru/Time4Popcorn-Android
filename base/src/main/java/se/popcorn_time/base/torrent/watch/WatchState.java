package se.popcorn_time.base.torrent.watch;

public enum WatchState {
    NONE,
    LOAD_METADATA,
    CHECK_FILE,
    LOAD_SUBTITLES,
    PREPARING_FOR_WATCH,
    SEQUENTIAL_DOWNLOAD,
    BUFFERING,
    FINISHED
}