package se.popcorn_time.base.torrent.watch;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.prefs.PopcornPrefs;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.subtitles.SubtitlesLanguage;
import se.popcorn_time.base.torrent.FileEntry;
import se.popcorn_time.base.torrent.Status;
import se.popcorn_time.base.torrent.TorrentPriority;
import se.popcorn_time.base.torrent.TorrentService;
import se.popcorn_time.base.torrent.TorrentUtil;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.model.content.ISubtitlesProvider;
import se.popcorn_time.model.details.IDetailsUseCase;
import se.popcorn_time.model.subtitles.Subtitles;

public class WatchTask extends Thread {

    protected final int DEFAULT_PREPARE_COUNT = 5;
    protected final int MIN_PREPARE_COUNT = 3;
    protected final int MAX_ACTIVE_COUNT = 20;

    private final int FIRST_PIECE = 0;
    private final int LAST_PIECE = 1;

    private WatchHandler handler = new WatchHandler();

    private TorrentService service;
    private WatchInfo watchInfo;

    private WatchState state;
    private String torrentFile;
    private boolean torrentPaused;

    private int[] filePriorities;
    private int[] fileBoundary;
    private int filePieceCount;

    private int activePieceCount;
    private int currentPiece;
    private int seekBeginPiece;
    private int seekEndPiece;
    private int seekTotalPieceCount;

    public WatchTask(TorrentService service, WatchInfo watchInfo) {
        this.service = service;
        this.watchInfo = watchInfo;
    }

    @Override
    public void run() {
        try {
            Logger.debug("WatchTask: Started");
            state = WatchState.LOAD_METADATA;
            handler.sendMessage(WatchHandler.METADATA_LOAD);
            torrentFile = loadMetadata(service, watchInfo);
            torrentPaused = resumeTorrent(service, watchInfo, torrentFile);

            state = WatchState.CHECK_FILE;
            FileEntry fileEntry = TorrentUtil.setFilePriority(service, torrentFile, watchInfo.fileName, TorrentPriority.MAXIMUM);
            if (fileEntry == null) {
                throw new WatchException("File is missing.", state);
            }
            String videoPath = watchInfo.watchDir + File.separator + fileEntry.path;

            filePriorities = service.getPiecePriorities(torrentFile);
            if (filePriorities == null) {
                throw new WatchException("File priorities is null.", state);
            }
            fileBoundary = getFileBoundary(filePriorities);
            filePieceCount = fileBoundary[LAST_PIECE] - fileBoundary[FIRST_PIECE] + 1;
            Logger.debug("Piece count: " + filePieceCount + ", first piece: " + fileBoundary[FIRST_PIECE] + ", last piece: " + fileBoundary[LAST_PIECE]);
            int prepareCount = getPreparePieceCount(service, torrentFile, 10 * StorageUtil.SIZE_MB, filePieceCount, 2);
            int totalPrepareCount = prepareCount * 2;
            Logger.debug("Prepare count: " + prepareCount + ", total: " + totalPrepareCount);
            if (prepareCount > MAX_ACTIVE_COUNT) {
                activePieceCount = MAX_ACTIVE_COUNT;
            } else {
                activePieceCount = prepareCount;
            }
            Logger.debug("Active count: " + activePieceCount);

            state = WatchState.LOAD_SUBTITLES;
            String subPath = loadSubtitles(service, watchInfo, videoPath);
            if (!TextUtils.isEmpty(subPath)) {
                handler.sendMessage(WatchHandler.SUBTITLES_LOADED, subPath);
            }

            handler.sendMessage(WatchHandler.DOWNLOAD_STARTED, torrentFile);
            state = WatchState.PREPARING_FOR_WATCH;
            int currentPieceFromFileEnd = fileBoundary[LAST_PIECE] - prepareCount + 1;
            while (WatchState.FINISHED != state) {
                synchronized (this) {
                    switch (state) {
                        case PREPARING_FOR_WATCH:
                            updateDownload(currentPieceFromFileEnd, fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE], filePriorities, activePieceCount, 4);
                            int count = downloadedPiecesCount(currentPieceFromFileEnd, fileBoundary[LAST_PIECE]);
                            currentPiece = updateDownload(fileBoundary[FIRST_PIECE], fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE], filePriorities, activePieceCount, 0);
                            count += downloadedPiecesCount(fileBoundary[FIRST_PIECE], fileBoundary[FIRST_PIECE] + prepareCount - 1);
                            updateProgress(state, totalPrepareCount, count);
                            if (count >= totalPrepareCount) {
                                handler.sendMessage(WatchHandler.VIDEO_PREPARED, videoPath);
                                state = WatchState.SEQUENTIAL_DOWNLOAD;
                            }
                            break;
                        case SEQUENTIAL_DOWNLOAD:
                            currentPiece = updateDownload(currentPiece, fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE], filePriorities, activePieceCount, 0);
                            updateProgress(state, filePieceCount, currentPiece - fileBoundary[FIRST_PIECE]);
                            if (fileBoundary[LAST_PIECE] <= currentPiece) {
                                Logger.debug("WatchTask: Finished");
                                state = WatchState.FINISHED;
                                handler.sendMessage(WatchHandler.DOWNLOAD_FINISHED);
                            }
                            break;
                        case BUFFERING:
                            currentPiece = updateDownload(currentPiece, fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE], filePriorities, activePieceCount, 4);
                            updateProgress(state, seekTotalPieceCount, downloadedPiecesCount(seekBeginPiece, seekEndPiece));
                            if (currentPiece >= seekEndPiece) {
                                state = WatchState.SEQUENTIAL_DOWNLOAD;
                                handler.sendMessage(WatchHandler.BUFFERING_FINISHED);
                            }
                            break;
                        default:
                            Logger.error("WatchTask wrong state: " + state);
                            currentPiece = fileBoundary[FIRST_PIECE];
                            state = WatchState.SEQUENTIAL_DOWNLOAD;
                            break;
                    }
                }
                if (WatchState.FINISHED != state) {
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
            }
        } catch (InterruptedException ex) {
            Logger.error("WatchTask: Interrupted", ex);
        } catch (WatchException e) {
            onTaskStopped(torrentFile);
            if (watchInfo != null && !watchInfo.isDownloads() && !TextUtils.isEmpty(torrentFile)) {
                service.removeTorrent(torrentFile);
            }
            handler.sendMessage(WatchHandler.ERROR, e);
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        onTaskStopped(torrentFile);
    }

    public boolean isWatchingNow(String torrentFile) {
        return !TextUtils.isEmpty(this.torrentFile) && this.torrentFile.equals(torrentFile);
    }

    public void pauseTorrent() {
        this.torrentPaused = true;
    }

    public boolean addWatchListener(WatchListener listener) {
        return listener != null && !handler.getListeners().contains(listener) && handler.getListeners().add(listener);
    }

    public boolean removeWatchListener(WatchListener listener) {
        return listener != null && handler.getListeners().remove(listener);
    }

    public boolean seek(float delta) {
        synchronized (this) {
            if (WatchState.FINISHED != state) {
                int seekPiece = (int) (delta * filePieceCount);
                seekBeginPiece = seekPiece - activePieceCount;
                seekEndPiece = seekPiece + activePieceCount;
                seekTotalPieceCount = 2 * activePieceCount + 1;
                Logger.debug("WatchTask<seek>: Seek index=" + seekPiece + ", range: " + seekBeginPiece + " - " + seekEndPiece);
                clearPiecePriorities(filePriorities, fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE]);
                service.clearPieceDeadlines(torrentFile);
                // activePieceCount=6, seekPiece=140, 134 / 140 / 146 = 13 pieces
                for (int i = seekBeginPiece; i <= seekEndPiece; i++) {
                    if (i > 0 && i < filePriorities.length && !service.havePiece(torrentFile, i)) {
                        state = WatchState.BUFFERING;
                        currentPiece = updateDownload(i, fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE], filePriorities, activePieceCount, 4);
                        updateProgress(state, seekTotalPieceCount, downloadedPiecesCount(seekBeginPiece, seekEndPiece));
                        return true;
                    }
                }
                state = WatchState.SEQUENTIAL_DOWNLOAD;
                currentPiece = updateDownload(seekPiece, fileBoundary[FIRST_PIECE], fileBoundary[LAST_PIECE], filePriorities, activePieceCount, 0);
                updateProgress(state, filePieceCount, currentPiece - fileBoundary[FIRST_PIECE]);
            }
            return false;
        }
    }

    @NonNull
    protected String loadMetadata(TorrentService service, WatchInfo info) throws InterruptedException, WatchException {
        if (info == null) {
            throw new WatchException("Watch info is null.", WatchState.LOAD_METADATA);
        }
        String torrent = TorrentUtil.addTorrent(service, info.watchDir, info.torrentFilePath, info.torrentUrl, info.torrentMagnet, info.resumeData);
        if (TextUtils.isEmpty(torrent)) {
            throw new WatchException("Metadata don't loaded.", WatchState.LOAD_METADATA);
        }
        if (!info.isDownloads()) {
            Prefs.getPopcornPrefs().put(PopcornPrefs.LAST_TORRENT, torrent);
        }
        return torrent;
    }

    protected boolean resumeTorrent(TorrentService service, WatchInfo info, String torrent) {
        if (!TextUtils.isEmpty(torrent) && service.isTorrentPaused(torrent)) {
            service.resumeTorrent(torrent);
            return true;
        } else {
            return !info.isDownloads();
        }
    }

    @NonNull
    protected int[] getFileBoundary(int[] priorities) throws WatchException {
        int[] boundary = new int[]{-1, -1};
        for (int i = 0; i < priorities.length; i++) {
            if (priorities[i] != TorrentPriority.NOT_DOWNLOADED) {
                if (boundary[FIRST_PIECE] == -1) {
                    boundary[FIRST_PIECE] = i;
                }
                priorities[i] = TorrentPriority.NOT_DOWNLOADED;
            } else {
                if (boundary[FIRST_PIECE] != -1 && boundary[LAST_PIECE] == -1) {
                    boundary[LAST_PIECE] = i - 1;
                }
            }
        }
        if (boundary[FIRST_PIECE] == -1) {
            throw new WatchException("File first piece is missing.", WatchState.CHECK_FILE);
        }
        if (boundary[LAST_PIECE] == -1) {
            boundary[LAST_PIECE] = priorities.length - 1;
        }
        return boundary;
    }

    protected int getPreparePieceCount(TorrentService service, String torrent, long activeSizeByte, int totalPieceCount, int activeMultiplier) throws WatchException {
        int prepare = DEFAULT_PREPARE_COUNT;
        int pieceLength = service.getPieceLength(torrent);
        Logger.debug("Piece size: " + pieceLength + ", active size: " + activeSizeByte);
        if (pieceLength > 0) {
            prepare = (int) (activeSizeByte / pieceLength);
            if (prepare < MIN_PREPARE_COUNT) {
                prepare = MIN_PREPARE_COUNT;
            }
        }
        if (prepare * activeMultiplier > totalPieceCount) {
            throw new WatchException("Small amount of pieces.", WatchState.CHECK_FILE);
        }
        return prepare;
    }

    protected String loadSubtitles(TorrentService service, WatchInfo info, String videoPath) throws InterruptedException {
        final IDetailsUseCase detailsUseCase = ((IUseCaseManager) service.getApplication()).getDetailsUseCase();
        if (detailsUseCase.getLangSubtitlesChoiceProperty().getItems() == null) {
            final ISubtitlesProvider provider = ((IUseCaseManager) service.getApplication()).getContentUseCase().getSubtitlesProvider(info);
            if (provider != null) {
                final Disposable disposable = provider.getSubtitles(info).subscribe(new Consumer<Map.Entry<String, List<Subtitles>>[]>() {

                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Map.Entry<String, List<Subtitles>>[] subtitles) throws Exception {
                        final String subLang = SubtitlesLanguage.getSubtitlesLanguage();
                        if (TextUtils.isEmpty(subLang)) {
                            detailsUseCase.getLangSubtitlesChoiceProperty().setItems(subtitles, -1);
                        } else {
                            int position = -1;
                            for (int i = 0; i < subtitles.length; i++) {
                                if (subLang.equals(SubtitlesLanguage.subtitlesIsoToName(subtitles[i].getKey()))) {
                                    position = i;
                                    break;
                                }
                            }
                            detailsUseCase.getLangSubtitlesChoiceProperty().setItems(subtitles, position);
                        }
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        synchronized (WatchTask.this) {
                            WatchTask.this.notify();
                        }
                    }
                }, new Action() {

                    @Override
                    public void run() throws Exception {
                        synchronized (WatchTask.this) {
                            WatchTask.this.notify();
                        }
                    }
                });
                if (Thread.interrupted()) {
                    disposable.dispose();
                    throw new InterruptedException("Load subtitles list was interrupted");
                }
                synchronized (WatchTask.this) {
                    WatchTask.this.wait();
                }
            }
        }
        return null;
    }

    private int updateDownload(int currentPiece, int firstPiece, int lastPiece, int[] priorities, int activeCount, int deadline) {
        int count = 0;
        int newIndex = -1;
        if (currentPiece < 0) {
            currentPiece = 0;
        }
        if (lastPiece >= priorities.length) {
            lastPiece = priorities.length - 1;
        }
//        String zzz = "| ";
        for (int i = currentPiece; i <= lastPiece; i++) {
            if (!service.havePiece(torrentFile, i)) {
                if (newIndex == -1) {
                    newIndex = i;
                }
                if (TorrentPriority.NOT_DOWNLOADED == priorities[i]) {
                    priorities[i] = TorrentPriority.MAXIMUM;
                    int _deadline = deadline > 0 ? (i - currentPiece + 1) * deadline : (i - firstPiece + 1) * 8;
//                    Logger.debug("Deadline<" + i + ">: " + _deadline);
                    service.setPieceDeadline(torrentFile, i, _deadline);
                }
//                zzz += i + " | ";
                count++;
            }
            if (count >= activeCount) {
                break;
            }
        }
//        Logger.debug(zzz);
        if (newIndex != -1) {
            currentPiece = newIndex;
        } else {
            currentPiece = lastPiece;
        }
        return currentPiece;
    }

    private int downloadedPiecesCount(int beginPiece, int endPiece) {
        int count = 0;
        for (int i = beginPiece; i <= endPiece; i++) {
            if (service.havePiece(torrentFile, i)) {
                count += 1;
            }
        }
        return count;
    }

    private void updateProgress(WatchState state, int total, int value) {
        final Status status = service.getStatus(torrentFile);
        handler.sendMessage(
                WatchHandler.UPDATE_PROGRESS,
                new WatchProgress(state, total, value, status != null ? status.seeds : 0, status != null ? status.peers : 0, service.getTorrentSpeed(torrentFile))
        );
    }

    private void onTaskStopped(String torrent) {
        handler.removeMessages();
        if (!TextUtils.isEmpty(torrent) && service != null) {
            service.setPriority(torrent, TorrentPriority.NORMAL);
            service.clearPieceDeadlines(torrent);
            if (torrentPaused) {
                service.pauseTorrent(torrent);
            }
        }
        Logger.debug("WatchTask: Stopped");
    }

    private void clearPiecePriorities(int[] priorities, int fPiece, int lPiece) {
        for (int i = fPiece; i <= lPiece; i++) {
            if (i > 0 && i < priorities.length) {
                priorities[i] = TorrentPriority.NOT_DOWNLOADED;
            }
        }
    }
}