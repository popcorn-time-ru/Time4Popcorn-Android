package se.popcorn_time.base.torrent.watch;

public interface WatchListener {

    void onError(WatchException exception);

    void onMetadataLoad();

    void onSubtitlesLoaded(String subPath);

    void onDownloadStarted(String torrent);

    void onVideoPrepared(String filePath);

    void onUpdateProgress(WatchProgress progress);

    void onDownloadFinished();

    void onBufferingFinished();
}