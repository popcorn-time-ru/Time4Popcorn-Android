package se.popcorn_time.base.torrent.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import se.popcorn_time.base.torrent.TorrentService;
import se.popcorn_time.base.torrent.TorrentUtil;

public class BaseClient {

    protected Context context;
    protected TorrentService torrentService;
    protected boolean bound = false;

    private ClientConnectionListener connectionListener;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            torrentService = ((TorrentService.TorrentBinder) service).getService();
            bound = true;
            if (connectionListener != null) {
                connectionListener.onClientConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            torrentService = null;
            if (connectionListener != null) {
                connectionListener.onClientDisconnected();
            }
        }
    };

    public BaseClient(Context context) {
        this.context = context;
    }

    public void setConnectionListener(ClientConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public final boolean bind() {
        return context.bindService(TorrentService.createIntent(context), connection, Context.BIND_AUTO_CREATE);
    }

    public final void unbind() {
        if (bound) {
            context.unbindService(connection);
            bound = false;
        }
    }

//    public final TorrentService getTorrentService() {
//        if (bound) {
//            return torrentService;
//        }
//        return null;
//    }

    /*
    * Session
    * */

    public void setConnectionsLimit(int connections) {
        if (bound) {
            torrentService.setConnectionsLimit(connections);
        }
    }

    public void setMaximumDownloadSpeed(int speed) {
        if (bound) {
            torrentService.setDownloadLimit(speed);
        }
    }

    public void setMaximumUploadSpeed(int speed) {
        if (bound) {
            torrentService.setUploadLimit(speed);
        }
    }

    /*
    * Torrent
    * */

//    public void addTorrent(String torrentFile, File saveDir) {
//        if (!bound) {
//            return;
//        }
//        torrentService.addTorrent(torrentFile, saveDir.getAbsolutePath());
//    }

    public void removeTorrent(String torrentFile) {
        if (!bound) {
            return;
        }
        torrentService.removeTorrent(torrentFile);
    }

    public void pauseTorrent(String torrentFile) {
        if (!bound) {
            return;
        }
        torrentService.pauseTorrent(torrentFile);
    }

    public void resumeTorrent(String torrentFile) {
        if (!bound) {
            return;
        }
        torrentService.resumeTorrent(torrentFile);
    }

//    public boolean hasMetadata(String torrentFile) {
//        return bound && torrentService.hasMetadata(torrentFile);
//    }

//    public boolean saveMetadata(String torrentFile, File metadataFile) {
//        if (!bound) {
//            return false;
//        }
//        return torrentService.saveMetadata(torrentFile, metadataFile);
//    }

//    public String getTorrentName(String torrentFile) {
//        if (!bound) {
//            return null;
//        }
//        return torrentService.getTorrentName(torrentFile);
//    }

//    public void setFilePriority(String torrentFile, String fileName) {
//        if (!bound) {
//            return;
//        }
//        torrentService.setFilePriority(torrentFile, fileName);
//    }

    public int getTorrentState(String torrentFile) {
        if (!bound) {
            return -1;
        }
        return torrentService.getTorrentState(torrentFile);
    }

    public String getTorrentSpeed(String torrentFile) {
        if (!bound) {
            return "0B/s";
        }
        return torrentService.getTorrentSpeed(torrentFile);
    }

    public int getDownloadSizeMb(String torrentFile) {
        if (!bound) {
            return 0;
        }
        return torrentService.getDownloadSizeMb(torrentFile);
    }

    public String getAvailableTorrentFile(String file, String url, String magnet) {
        if (!bound) {
            return null;
        }
        return TorrentUtil.getAvailableTorrentFile(torrentService, file, url, magnet);
    }
}