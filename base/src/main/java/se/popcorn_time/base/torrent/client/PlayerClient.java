package se.popcorn_time.base.torrent.client;

import android.content.Context;

import se.popcorn_time.base.torrent.watch.WatchListener;

public class PlayerClient extends BaseClient {

    public PlayerClient(Context context) {
        super(context);
    }

    public boolean addWatchListener(WatchListener listener) {
        if (bound) {
            return torrentService.addWatchListener(listener);
        }
        return false;
    }

    public boolean removeWatchListener(WatchListener listener) {
        if (bound) {
            return torrentService.removeWatchListener(listener);
        }
        return false;
    }

    public boolean seek(float delta) {
        if (bound) {
           return torrentService.seekWatch(delta);
        }
        return false;
    }
}