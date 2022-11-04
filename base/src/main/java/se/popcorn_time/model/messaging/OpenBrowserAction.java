package se.popcorn_time.model.messaging;

import android.os.Parcel;

public final class OpenBrowserAction implements IMessagingData.Action {

    public static final String NAME = "openBrowser";

    private String url;

    public OpenBrowserAction() {

    }

    private OpenBrowserAction(Parcel source) {
        this.url = source.readString();
    }

    @Override
    public String name() {
        return NAME;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
    }

    public static final Creator<OpenBrowserAction> CREATOR = new Creator<OpenBrowserAction>() {

        @Override
        public OpenBrowserAction createFromParcel(Parcel parcel) {
            return new OpenBrowserAction(parcel);
        }

        @Override
        public OpenBrowserAction[] newArray(int i) {
            return new OpenBrowserAction[i];
        }
    };
}
