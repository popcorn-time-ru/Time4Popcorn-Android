package se.popcorn_time.base.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WatchInfo implements Parcelable {

    public long downloadsId = -1;
    public String imdb;
    public String type;
    public String watchDir;
    public String torrentFilePath;
    public String torrentUrl;
    public String torrentMagnet;
    public String fileName;
    public String posterUrl;
    public int season;
    public int episode;
    public byte[] resumeData;

    public WatchInfo() {

    }

    public WatchInfo(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            downloadsId = downloadInfo.id;
            imdb = downloadInfo.imdb;
            type = downloadInfo.type;
            watchDir = downloadInfo.directory.getAbsolutePath();
            torrentFilePath = downloadInfo.torrentFilePath;
            torrentUrl = downloadInfo.torrentUrl;
            torrentMagnet = downloadInfo.torrentMagnet;
            fileName = downloadInfo.fileName;
            posterUrl = downloadInfo.posterUrl;
            season = downloadInfo.season;
            episode = downloadInfo.episode;
            resumeData = downloadInfo.resumeData;
        }
    }

    private WatchInfo(Parcel parcel) {
        downloadsId = parcel.readLong();
        imdb = parcel.readString();
        type = parcel.readString();
        watchDir = parcel.readString();
        torrentFilePath = parcel.readString();
        torrentUrl = parcel.readString();
        torrentMagnet = parcel.readString();
        fileName = parcel.readString();
        posterUrl = parcel.readString();
        season = parcel.readInt();
        episode = parcel.readInt();
        resumeData = parcel.createByteArray();
    }

    public boolean isDownloads() {
        return downloadsId > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(downloadsId);
        parcel.writeString(imdb);
        parcel.writeString(type);
        parcel.writeString(watchDir);
        parcel.writeString(torrentFilePath);
        parcel.writeString(torrentUrl);
        parcel.writeString(torrentMagnet);
        parcel.writeString(fileName);
        parcel.writeString(posterUrl);
        parcel.writeInt(season);
        parcel.writeInt(episode);
        parcel.writeByteArray(resumeData);
    }

    public static final Creator<WatchInfo> CREATOR = new Creator<WatchInfo>() {

        @Override
        public WatchInfo createFromParcel(Parcel source) {
            return new WatchInfo(source);
        }

        @Override
        public WatchInfo[] newArray(int size) {
            return new WatchInfo[size];
        }
    };
}