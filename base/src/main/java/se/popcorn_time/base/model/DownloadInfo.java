package se.popcorn_time.base.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class DownloadInfo implements Parcelable {

    public long id;
    public String type;
    public String imdb;
    public String torrentUrl;
    public String torrentMagnet;
    public String fileName;
    public String posterUrl;
    public String title;
    public File directory;
    public String torrentFilePath;
    public int state;
    public long size;
    public int season;
    public int episode;
    public String torrentHash;
    public byte[] resumeData;
    public boolean readyToWatch;

    public DownloadInfo() {
    }

    private DownloadInfo(Parcel parcel) {
        id = parcel.readLong();
        type = parcel.readString();
        imdb = parcel.readString();
        torrentUrl = parcel.readString();
        torrentMagnet = parcel.readString();
        fileName = parcel.readString();
        posterUrl = parcel.readString();
        title = parcel.readString();
        directory = new File(parcel.readString());
        torrentFilePath = parcel.readString();
        state = parcel.readInt();
        size = parcel.readLong();
        season = parcel.readInt();
        episode = parcel.readInt();
        torrentHash = parcel.readString();
        resumeData = parcel.createByteArray();
        readyToWatch = parcel.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(type);
        parcel.writeString(imdb);
        parcel.writeString(torrentUrl);
        parcel.writeString(torrentMagnet);
        parcel.writeString(fileName);
        parcel.writeString(posterUrl);
        parcel.writeString(title);
        parcel.writeString(directory.getAbsolutePath());
        parcel.writeString(torrentFilePath);
        parcel.writeInt(state);
        parcel.writeLong(size);
        parcel.writeInt(season);
        parcel.writeInt(episode);
        parcel.writeString(torrentHash);
        parcel.writeByteArray(resumeData);
        parcel.writeByte((byte) (readyToWatch ? 1 : 0));
    }

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {

        public DownloadInfo createFromParcel(Parcel in) {
            return new DownloadInfo(in);
        }

        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };
}