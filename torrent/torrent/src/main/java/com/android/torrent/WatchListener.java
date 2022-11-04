package com.android.torrent;

public interface WatchListener {

    void onProgress(int current, int total);

    void onPrepareSuccess();

    void onFinished();
}
