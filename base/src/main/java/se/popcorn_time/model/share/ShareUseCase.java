package se.popcorn_time.model.share;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.functions.Consumer;
import se.popcorn_time.base.prefs.Prefs;

public final class ShareUseCase implements IShareUseCase {

    public static final String WAS_SHARED_FROM_DIALOG = "was-shared-from-dialog";
    private static final String KEY_LAUNCHES_AFTER_INSTALL = "launches-after-install";
    private static final String KEY_LAST_SHARE_TIME = "last-share-time";
    private static final String KEY_LAUNCHES_AFTER_LAST_SHARE = "launches-after-last-share";
    private static final String KEY_FOCUSES_AFTER_LAST_SHARE = "focuses-after-last-share";
    private static final String KEY_LAUNCHES_AFTER_SHARE_DIALOG = "launches-after-share-dialog";

    @Nullable
    private Observer observer;

    @Nullable
    private Observer resumedObserver;

    private IShareData shareData;
    private IVideoShareData videoShareData;
    private boolean appBackground;

    private int shareDialogsAfterLaunch = 0;
    private int focusesAfterLaunch = 0;

    private final IShareRemoteRepository remoteRepository;
    private String[] urls;

    public ShareUseCase(@NonNull IShareRemoteRepository remoteRepository) {
        this.remoteRepository = remoteRepository;
    }

    @Override
    public void subscribe(@NonNull Observer observer) {
        this.observer = observer;
    }

    @Override
    public void unsubscribe(@NonNull Observer observer) {
        if (observer.equals(this.observer)) {
            this.observer = null;
        }
    }

    @Nullable
    @Override
    public IShareData getData() {
        return shareData;
    }

    @Nullable
    @Override
    public IVideoShareData getVideoShareData() {
        return videoShareData;
    }

    @Override
    public void setUrls(@NonNull String[] urls) {
        this.urls = urls;
        remoteRepository.setUrl(urls != null && urls.length > 0 ? urls[0] : null);
    }

    @Override
    public void share() {
        Prefs.getPopcornPrefs().put(KEY_LAUNCHES_AFTER_LAST_SHARE, 0);
        Prefs.getPopcornPrefs().put(KEY_FOCUSES_AFTER_LAST_SHARE, 0);
        Prefs.getPopcornPrefs().put(KEY_LAST_SHARE_TIME, System.currentTimeMillis());
    }

    @Override
    public void share(@NonNull String imdb, long length, long position) {
        if (null != videoShareData && videoShareData.isShow()) {
            if (observer != null && videoShareData.getRate() <= (double) position / (double) length) {
                observer.onShowShare(videoShareData);
            }
        }
    }

    @Override
    public void checkLaunchShare() {
        final int launchesAfterInstall = Prefs.getPopcornPrefs().get(KEY_LAUNCHES_AFTER_INSTALL, 0) + 1;
        final int launchesAfterLastShare = Prefs.getPopcornPrefs().get(KEY_LAUNCHES_AFTER_LAST_SHARE, 0) + 1;
        final long timeCounterAfterLastShare = System.currentTimeMillis() - Prefs.getPopcornPrefs().get(KEY_LAST_SHARE_TIME, 0);
        final boolean wasSharedFromDialog = Prefs.getPopcornPrefs().get(WAS_SHARED_FROM_DIALOG, false);
        final int launchesAfterShareDialog = Prefs.getPopcornPrefs().get(KEY_LAUNCHES_AFTER_SHARE_DIALOG, 0) + 1;

        Prefs.getPopcornPrefs().put(KEY_LAUNCHES_AFTER_INSTALL, launchesAfterInstall);
        Prefs.getPopcornPrefs().put(KEY_LAUNCHES_AFTER_LAST_SHARE, launchesAfterLastShare);
        Prefs.getPopcornPrefs().put(KEY_LAUNCHES_AFTER_SHARE_DIALOG, launchesAfterShareDialog);
        remoteRepository.getShare(
                launchesAfterInstall,
                launchesAfterLastShare,
                timeCounterAfterLastShare,
                wasSharedFromDialog,
                launchesAfterShareDialog
        ).subscribe(new Consumer<IShareData>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull IShareData shareData) throws Exception {
                ShareUseCase.this.shareData = shareData;
                if (shareData.isShow()) {
                    if (observer != null) {
                        shareDialogsAfterLaunch++;
                        Prefs.getPopcornPrefs().put(KEY_LAUNCHES_AFTER_SHARE_DIALOG, 0);
                        observer.onShowShare(shareData);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void loadVideoShare(@NonNull String imdb) {
        if (videoShareData != null && videoShareData.getImdb().equals(imdb)) {
            return;
        }
        remoteRepository.getVideoShare(imdb).subscribe(new Consumer<IVideoShareData>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull IVideoShareData videoShareData) throws Exception {
                ShareUseCase.this.videoShareData = videoShareData;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onAppBackground(boolean appBackground) {
        this.appBackground = appBackground;
    }

    @Override
    public void onViewResumed(@NonNull Observer observer) {
        this.resumedObserver = observer;
        if (!appBackground) {
            return;
        }
        appBackground = false;
        focusesAfterLaunch++;
        final int launchesAfterInstall = Prefs.getPopcornPrefs().get(KEY_LAUNCHES_AFTER_INSTALL, 0);
        final int focusesAfterLastShare = Prefs.getPopcornPrefs().get(KEY_FOCUSES_AFTER_LAST_SHARE, 0) + 1;
        final long timeCounterAfterLastShare = System.currentTimeMillis() - Prefs.getPopcornPrefs().get(KEY_LAST_SHARE_TIME, 0);
        final boolean wasSharedFromDialog = Prefs.getPopcornPrefs().get(WAS_SHARED_FROM_DIALOG, false);
        final int launchesAfterShareDialog = Prefs.getPopcornPrefs().get(KEY_LAUNCHES_AFTER_SHARE_DIALOG, 0);

        Prefs.getPopcornPrefs().put(KEY_FOCUSES_AFTER_LAST_SHARE, focusesAfterLastShare);

        remoteRepository.getFocusShare(
                launchesAfterInstall,
                focusesAfterLastShare,
                timeCounterAfterLastShare,
                wasSharedFromDialog,
                shareDialogsAfterLaunch,
                launchesAfterShareDialog,
                focusesAfterLaunch
        ).subscribe(new Consumer<IShareData>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull IShareData shareData) throws Exception {
                if (shareData.isShow()) {
                    if (resumedObserver != null) {
                        shareDialogsAfterLaunch++;
                        Prefs.getPopcornPrefs().put(KEY_LAUNCHES_AFTER_SHARE_DIALOG, 0);
                        resumedObserver.onShowShare(shareData);
                    }
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }
}
