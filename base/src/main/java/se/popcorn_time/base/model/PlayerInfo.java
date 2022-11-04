package se.popcorn_time.base.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerInfo implements Parcelable {

    public String loadedSubtitlesPath;

    public PlayerInfo() {

    }

    private PlayerInfo(Parcel source) {
        loadedSubtitlesPath = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(loadedSubtitlesPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel source) {
            return new PlayerInfo(source);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };
}