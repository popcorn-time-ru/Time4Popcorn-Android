package se.popcorn_time.model.updater;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.base.utils.Logger;

public final class UpdaterUseCase implements IUpdaterUseCase {

    private final File dir;
    private final DownloadManager downloadManager;
    private final IUpdaterRepository repository;

    public UpdaterUseCase(@NonNull File dir, @NonNull DownloadManager downloadManager, @NonNull IUpdaterRepository repository) {
        this.dir = dir;
        this.downloadManager = downloadManager;
        this.repository = repository;
    }

    @NonNull
    @Override
    public Observable<Update> getUpdate(@NonNull String[] urls) {
        return getUpdate(new LinkedList<>(Arrays.asList(urls)));
    }

    @NonNull
    private Observable<Update> getUpdate(@NonNull Queue<String> urls) {
        if (urls.isEmpty()) {
            return Observable.empty();
        }
        return repository.getUpdate(urls.poll())
                .onErrorResumeNext(new ErrorFunction(urls))
                .concatMap(new Function<Update, ObservableSource<? extends Update>>() {

                    @Override
                    public ObservableSource<? extends Update> apply(@io.reactivex.annotations.NonNull Update update) throws Exception {
                        if (TextUtils.isEmpty(update.getUrl()) || TextUtils.isEmpty(update.getVersion())) {
                            return currentVersion();
                        } else {
                            return newVersion(update);
                        }
                    }
                });
    }

    private void deleteApk(File file) {
        if (file != null && file.exists() && file.delete()) {
            Logger.debug("UpdaterService<deleteApk>: " + file.getAbsolutePath());
        }
        Prefs.getPopcornPrefs().put(PopcornPrefs.UPDATE_APK_PATH, "");
    }

    @NonNull
    private Observable<Update> currentVersion() {
        Logger.debug("UpdaterService: Current version");
        String filePath = Prefs.getPopcornPrefs().get(PopcornPrefs.UPDATE_APK_PATH, "");
        if (!"".equals(filePath)) {
            deleteApk(new File(filePath));
        }
        return Observable.empty();
    }

    @NonNull
    private Observable<Update> newVersion(@NonNull Update update) {
        Logger.debug("UpdaterService: New version " + update.getVersion());
        String filePath = Prefs.getPopcornPrefs().get(PopcornPrefs.UPDATE_APK_PATH, "");
        final File file = new File(dir, "popcorntime-" + update.getVersion() + ".apk");
        Logger.debug(file.getAbsolutePath());
        if (file.exists() && file.getAbsolutePath().equals(filePath)) {
            return downloadComplete(update, file);
        } else {
            deleteApk(file);
            return download(update, file);
        }
    }

    @NonNull
    private Observable<Update> download(Update update, File file) {
        return Observable.create(new DownloadObservable(update, file))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private final class DownloadObservable implements ObservableOnSubscribe<Update> {

        private final Update update;
        private final File file;

        private DownloadObservable(Update update, File file) {
            this.update = update;
            this.file = file;
        }

        @Override
        public void subscribe(ObservableEmitter<Update> e) throws Exception {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(update.getUrl()));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setDestinationUri(Uri.parse("file://" + file.getAbsolutePath()));
            request.setVisibleInDownloadsUi(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            long downloadId = -1;
            try {
                downloadId = downloadManager.enqueue(request);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (downloadId == -1) {
                downloadURL(e, update, file);
            } else {
                downloadManager(e, update, file, downloadId);
            }
        }
    }

    private void downloadURL(ObservableEmitter<Update> emitter, Update update, File file) {
        InputStream input = null;
        OutputStream output = null;
        while (true) {
            try {
                URL url = new URL(update.getUrl());
                URLConnection connection = url.openConnection();
                connection.connect();
                input = new BufferedInputStream(connection.getInputStream());
                output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    if (emitter.isDisposed()) {
                        break;
                    }
                    if (count != 0) {
                        output.write(data, 0, count);
                    }
                }
                if (emitter.isDisposed()) {
                    deleteApk(file);
                    break;
                }
                output.flush();
                downloadComplete(emitter, update, file);
                break;
            } catch (Exception e) {
                Logger.error("UpdaterService<download>: Error", e);
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception ex) {
                    // close exception
                }
            }
        }
    }

    private void downloadManager(ObservableEmitter<Update> emitter, Update update, File file, long downloadId) {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ex) {
                // interrupt
            } finally {
                if (emitter.isDisposed()) {
                    downloadManager.remove(downloadId);
                }
            }
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
            if (cursor != null) {
                int status = DownloadManager.STATUS_FAILED;
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                }
                cursor.close();
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        downloadComplete(emitter, update, file);
                        return;
                    case DownloadManager.STATUS_FAILED:
                        return;
                    default:
                        break;
                }
            }
        }
    }

    @NonNull
    private Observable<Update> downloadComplete(Update update, File file) {
        Prefs.getPopcornPrefs().put(PopcornPrefs.UPDATE_APK_PATH, file.getAbsolutePath());
        update.setUrl("file://" + file.getAbsolutePath());
        return Observable.just(update);
    }

    private void downloadComplete(ObservableEmitter<Update> emitter, Update update, File file) {
        Prefs.getPopcornPrefs().put(PopcornPrefs.UPDATE_APK_PATH, file.getAbsolutePath());
        update.setUrl("file://" + file.getAbsolutePath());
        emitter.onNext(update);
        emitter.onComplete();
    }

    private final class ErrorFunction implements Function<Throwable, ObservableSource<Update>> {

        private final Queue<String> urls;

        ErrorFunction(@NonNull Queue<String> urls) {
            this.urls = urls;
        }

        @Override
        public ObservableSource<Update> apply(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
            return getUpdate(urls);
        }
    }
}
