package se.popcorn_time.model.share;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface IShareRemoteRepository {

    void setUrl(@NonNull String url);

    @NonNull
    Observable<IShareData> getShare(int launchesAfterInstall,
                                    int launchesAfterLastShare,
                                    long timeCounterAfterLastShare,
                                    boolean wasSharedFromDialog,
                                    int launchesAfterShareDialog);

    @NonNull
    Observable<IShareData> getFocusShare(int launchesAfterInstall,
                                         int focusesAfterLastShare,
                                         long timeCounterAfterLastShare,
                                         boolean wasSharedFromDialog,
                                         int shareDialogsAfterLaunch,
                                         int launchesAfterShareDialog,
                                         int focusesAfterLaunch);

    @NonNull
    Observable<IVideoShareData> getVideoShare(@NonNull String imdb);
}
