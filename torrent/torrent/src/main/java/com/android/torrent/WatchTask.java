package com.android.torrent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class WatchTask<TH extends TorrentHandle, FE extends FileEntity> {

    private static final long SIZE_MB = 1024L * 1024L;
    private static final int PIECES_WINDOW_MIN = 2;

    @NonNull
    protected final TH _torrentHandle;

    @NonNull
    protected final FE _fileEntity;

    @Nullable
    protected final WatchListener _listener;

    @NonNull
    private final TorrentPriority[] _filePriorities;
    private final int _firstPiece;
    private final int _lastPiece;
    private final int _pieceWindow;
    private final boolean _paused;

    private int _currentPosition;
    private boolean finished = false;

    @Nullable
    private Preparing _preparing;

    public WatchTask(@NonNull TH torrentHandle, @NonNull FE fileEntity, @Nullable WatchListener listener) {
        _torrentHandle = torrentHandle;
        _fileEntity = fileEntity;
        _listener = listener;
        _paused = torrentHandle.isPaused();

        _filePriorities = torrentHandle.getFilePriorities();
        for (int i = 0; i < _filePriorities.length; i++) {
            _torrentHandle.setFilePriority(i, i == _fileEntity.getIndex() ? TorrentPriority.NORMAL : TorrentPriority.NOT_DOWNLOAD);
        }

        int first = -1;
        int last = -1;
        TorrentPriority[] piecePriorities = torrentHandle.getPiecePriorities();
        for (int i = 0; i < piecePriorities.length; i++) {
            if (piecePriorities[i] != TorrentPriority.NOT_DOWNLOAD) {
                if (first == -1) {
                    first = i;
                }
            } else {
                if (first != -1 && last == -1) {
                    last = i - 1;
                }
            }
        }
        if (last == -1) {
            last = piecePriorities.length - 1;
        }
        _firstPiece = first;
        _lastPiece = last;

        int pieceWindow = (int) (10 * SIZE_MB / _torrentHandle.getPieceSize());
        if (pieceWindow < PIECES_WINDOW_MIN) {
            pieceWindow = PIECES_WINDOW_MIN;
        }
        _pieceWindow = pieceWindow;

        updatePosition(_firstPiece);
//            log("first : " + _firstPiece + ", last: " + _lastPiece);
//            log("position : " + _currentPosition);
//            log("window : " + _pieceWindow);
        if (finished) {
            if (_listener != null) {
                _listener.onProgress(1, 1);
                _listener.onPrepareSuccess();
                _listener.onFinished();
            }
        } else {
            _preparing = new Preparing(_pieceWindow, _pieceWindow);
            if (_paused) {
                _torrentHandle.resume();
            }
        }
    }

    @NonNull
    public final TH getTorrentEntity() {
        return _torrentHandle;
    }

    public final void cancel() {
        for (int i = 0; i < _filePriorities.length; i++) {
            _torrentHandle.setFilePriority(i, _filePriorities[i]);
        }
        if (_paused) {
            _torrentHandle.pause();
        }
    }

    public final boolean isFinished() {
        return finished;
    }

    public final void onPieceFinished(int pieceIndex) {
        if (finished) {
            return;
        }
        if (_preparing != null && !_preparing.isSuccess()) {
            _preparing.onPieceFinished(pieceIndex);
        }
        updatePosition(_currentPosition);
//        log("LibTorrentWatchTask<onPieceFinished>: " + pieceIndex + ",  current: " + _currentPosition);
        if (finished) {
            if (_listener != null) {
                _listener.onFinished();
            }
        } else {
            update(_firstPiece, _lastPiece, _currentPosition, _pieceWindow);
        }
    }

    protected abstract void update(int first, int last, int position, int window);

    private void updatePosition(int position) {
        for (int i = position; i <= _lastPiece; i++) {
            if (!_torrentHandle.havePiece(i)) {
                _currentPosition = i;
                return;
            }
        }
        finished = true;
    }

//    protected final void log(@NonNull String msg) {
//        Log.d("log_downloader", msg);
//    }

    private final class Preparing {

        private final boolean[] _beginRange;
        private final boolean[] _endRange;
        private final int _total;

        private int _progress;
        private boolean success;

        Preparing(int startRange, int endRange) {
            _beginRange = new boolean[startRange];
            _endRange = new boolean[endRange];
            _total = startRange + endRange;
            _progress = 0;
            for (int i = 0; i < _beginRange.length; i++) {
                if (_torrentHandle.havePiece(_firstPiece + i)) {
                    _beginRange[i] = true;
                    _progress += 1;
                }
            }
            for (int i = 0; i < _endRange.length; i++) {
                if (_torrentHandle.havePiece(_lastPiece - i)) {
                    _endRange[i] = true;
                    _progress += 1;
                }
            }
            if (_listener != null) {
                _listener.onProgress(_progress, _total);
            }
            update(_firstPiece, _lastPiece, _currentPosition, startRange);
            success = _progress >= _total;
            if (success) {
                if (_listener != null) {
                    _listener.onPrepareSuccess();
                }
            } else {
                update(_lastPiece, _lastPiece - endRange + 1, _lastPiece, endRange);
            }
        }

        public boolean isSuccess() {
            return success;
        }

        public void onPieceFinished(int piece) {
            if (piece >= _firstPiece && piece < _firstPiece + _beginRange.length) {
                int index = piece - _firstPiece;
                updateState(_beginRange, index);
            } else if (piece <= _lastPiece && piece > _lastPiece - _endRange.length) {
                int index = _lastPiece - piece;
                updateState(_endRange, index);
            }
            if (_listener != null && success) {
                _listener.onPrepareSuccess();
            }
        }

        private void updateState(boolean[] range, int index) {
            if (!range[index]) {
                range[index] = true;
                _progress += 1;
                if (_listener != null) {
                    _listener.onProgress(_progress, _total);
                }
                success = _progress >= _total;
            }
        }
    }
}