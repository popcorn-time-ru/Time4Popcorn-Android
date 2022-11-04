package se.popcorn_time.base.torrent;

public class Status {

    public String hash;
    public int seeds;
    public int peers;

    public Status() {

    }

    @Override
    public String toString() {
        return hash + ", s=" + seeds + ", p=" + peers;
    }
}
