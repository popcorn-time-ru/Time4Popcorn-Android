package se.popcorn_time.base.torrent.watch;

public class WatchException extends Exception {

    private WatchState state;

    public WatchException(String message, WatchState state) {
        super(message);
        this.state = state;
    }

    public WatchState getState() {
        return state;
    }
}