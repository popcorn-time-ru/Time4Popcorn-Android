package se.popcorn_time.model.share;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IShareUseCase {

    interface Observer {

        void onShowShare(@NonNull IShareData data);
    }

    void subscribe(@NonNull Observer observer);

    void unsubscribe(@NonNull Observer observer);

    @Nullable
    IShareData getData();

    @Nullable
    IVideoShareData getVideoShareData();

    void setUrls(@NonNull String[] urls);

    void share();

    void share(@NonNull String imdb, long length, long position);

    void checkLaunchShare();

    void loadVideoShare(@NonNull String imdb);

    void onAppBackground(boolean appBackground);

    void onViewResumed(@NonNull Observer observer);
}
