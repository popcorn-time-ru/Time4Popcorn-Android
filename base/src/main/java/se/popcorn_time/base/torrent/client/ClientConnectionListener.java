package se.popcorn_time.base.torrent.client;

public interface ClientConnectionListener {

    public void onClientConnected();

    public void onClientDisconnected();
}