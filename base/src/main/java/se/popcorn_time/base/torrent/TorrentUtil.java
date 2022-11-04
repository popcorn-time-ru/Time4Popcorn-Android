package se.popcorn_time.base.torrent;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.utils.Logger;

public final class TorrentUtil {

    private TorrentUtil() {

    }

    public static String getAvailableTorrentFile(TorrentService service, String file, String url, String magnet) {
        if (service == null) {
            return null;
        }
        if (service.hasMetadata(file)) {
            return file;
        } else if (service.hasMetadata(url)) {
            return url;
        } else if (service.hasMetadata(magnet)) {
            return magnet;
        }
        return null;
    }

    public static boolean saveMetadata(TorrentService service, DownloadInfo info, String torrentFile) {
        File metadataFile;
        if (TextUtils.isEmpty(info.torrentFilePath)) {
            String fileName = service.getTorrentName(torrentFile);
            if (TextUtils.isEmpty(fileName)) {
                fileName = info.directory.getAbsolutePath();
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }
            info.torrentFilePath = info.directory.getAbsolutePath() + "/" + fileName + ".torrent";
        }
        metadataFile = new File(info.torrentFilePath);
        return !metadataFile.exists() && service.saveMetadata(torrentFile, metadataFile);
    }

    public static String addTorrent(TorrentService service, DownloadInfo info) throws InterruptedException {
        return addTorrent(service, info.directory.getAbsolutePath(), info.torrentFilePath, info.torrentUrl, info.torrentMagnet, info.resumeData);
    }

    public static String addTorrent(TorrentService service, String savePath, String file, String url, String magnet, byte[] resumeData) throws InterruptedException {
        if (TextUtils.isEmpty(savePath)) {
            Logger.error("addTorrent: Save path is empty.");
            return null;
        }

        String availableTorrent = getAvailableTorrentFile(service, file, url, magnet);
        if (!TextUtils.isEmpty(availableTorrent)) {
            return availableTorrent;
        }

//        Logger.debug("----------------- Add Torrent -----------------");
//        Logger.debug(file);
//        Logger.debug(url);
//        Logger.debug(magnet);
//        Logger.debug("-----------------------------------------------");

        long timeout = 200;
        if (isTorrentAdded(service, savePath, file, resumeData, timeout)) {
            return file;
        }
        timeout = TextUtils.isEmpty(magnet) ? 60000 : 15000;
        if (isTorrentAdded(service, savePath, url, resumeData, timeout)) {
            return url;
        }
        timeout = 120000;
        if (isTorrentAdded(service, savePath, magnet, resumeData, timeout)) {
            return magnet;
        }

        return null;
    }

    private static boolean isTorrentAdded(TorrentService service, String savePath, String metadataPath, byte[] resumeData, long timeout) throws InterruptedException {
        if (service == null || TextUtils.isEmpty(metadataPath)) {
            return false;
        }

        long time = timeout > 1000 ? 1000 : timeout;
        long currentTime = 0;
        service.addTorrent(metadataPath, savePath, resumeData);
        try {
            do {
                TimeUnit.MILLISECONDS.sleep(time);
                currentTime += time;
                if (service.hasMetadata(metadataPath)) {
                    return true;
                }
            } while (currentTime < timeout);
        } catch (InterruptedException ie) {
            service.removeTorrent(metadataPath);
            throw ie;
        }
        service.removeTorrent(metadataPath);

        return false;
    }

    @Nullable
    public static FileEntry setFilePriority(TorrentService service, String torrent, String fileName, int priority) {
        List<FileEntry> files = service.getFiles(torrent);
        if (files != null && files.size() > 0) {
            int fileIndex = 0;
            FileEntry fileEntry = files.get(fileIndex);
            if (TextUtils.isEmpty(fileName)) {
                for (int i = 0; i < files.size(); i++) {
                    if (fileEntry.size < files.get(i).size) {
                        fileIndex = i;
                        fileEntry = files.get(i);
                    }
                }
            } else {
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i).path.endsWith(fileName)) {
                        fileIndex = i;
                        fileEntry = files.get(i);
                        break;
                    }
                }
            }

            int[] priorities = service.getFilePriorities(torrent);
            if (priorities != null && priorities.length > 0) {
                for (int i = 0; i < priorities.length; i++) {
                    if (fileIndex == i) {
                        priorities[i] = priority;
                    } else {
                        priorities[i] = TorrentPriority.NOT_DOWNLOADED;
                    }
                }
                service.setFilePriorities(torrent, priorities);
            }

            Logger.debug("Set file priority to: " + fileEntry.toString());
            return fileEntry;
        }
        return null;
    }
}