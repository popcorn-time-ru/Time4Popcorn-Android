package com.android.torrent;

import android.support.annotation.NonNull;

public interface TorrentListener {

    void onTorrentAdded(@NonNull TorrentHandle handle);

    void onTorrentRemoved(@NonNull TorrentHandle handle);

    void onTorrentResumed(@NonNull TorrentHandle handle);

    void onTorrentPaused(@NonNull TorrentHandle handle);

    void onTorrentChecked(@NonNull TorrentHandle handle);

    void onTorrentFinished(@NonNull TorrentHandle handle);

    void onTorrentError(@NonNull TorrentHandle handle);

    void onMetadataReceived(@NonNull TorrentHandle handle);

    void onMetadataFailed(@NonNull TorrentHandle handle);

    void onPieceFinished(@NonNull TorrentHandle handle);

    void onBlockFinished(@NonNull TorrentHandle handle);

    void onSaveResumeData(@NonNull TorrentHandle handle);
}