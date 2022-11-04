package se.popcorn_time.base.torrent.watch;

public class WatchProgress {

    public WatchState state;
    public int total;
    public int value;
    public int seeds;
    public int peers;
    public String speed;

    public WatchProgress() {
        this(WatchState.NONE, 0, 0, 0, 0, "0B/s");
    }

    public WatchProgress(WatchState state, int total, int value, int seeds, int peers, String speed) {
        this.state = state;
        this.total = total;
        this.value = value;
        this.seeds = seeds;
        this.peers = peers;
        this.speed = speed;
    }
}