package se.popcorn_time.model.messaging;

import android.os.Parcelable;

public interface IMessagingData {

    interface Action extends Parcelable {

        String name();
    }
}
