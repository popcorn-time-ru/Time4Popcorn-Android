package com.android.torrent.libtorrent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.torrent.FileEntity;
import com.android.torrent.TorrentHandle;
import com.android.torrent.TorrentPriority;
import com.frostwire.jlibtorrent.Vectors;
import com.frostwire.jlibtorrent.swig.create_torrent;
import com.frostwire.jlibtorrent.swig.int_vector;
import com.frostwire.jlibtorrent.swig.torrent_handle;

import java.io.File;

public final class LibTorrentHandle extends TorrentHandle<torrent_handle> {

    @NonNull
    private final File saveDir;

    @Nullable
    private byte[] resumeData;

    @Nullable
    private Throwable error;

    protected LibTorrentHandle(@NonNull Uri uri, @NonNull File saveDir, @Nullable torrent_handle handle) {
        super(uri, handle);
        this.saveDir = saveDir;
    }

    @NonNull
    @Override
    public File getSaveDir() {
        return saveDir;
    }

    @NonNull
    @Override
    public String getName() {
        return torrent != null && torrent.torrent_file() != null ? torrent.torrent_file().name() : uri.toString();
    }

    @NonNull
    @Override
    public String getInfoHash() {
        return torrent != null ? torrent.info_hash().to_hex() : "0";
    }

    @Nullable
    @Override
    public byte[] getMetadata() {
        if (torrent != null && torrent.status() != null && torrent.status().getHas_metadata()) {
            return Vectors.char_vector2bytes(new create_torrent(torrent.torrent_file()).generate().bencode());
        }
        return null;
    }

    @Nullable
    @Override
    public FileEntity getFile(int index) {
        if (torrent != null && torrent.torrent_file() != null && index >= 0 && index < torrent.torrent_file().num_files()) {
            return new LibTorrentFileEntity(
                    torrent.status().getSave_path(),
                    torrent.torrent_file().file_at(index),
                    LibTorrentPriority.convertPriority(torrent.file_priorities().get(index)),
                    index
            );
        }
        return null;
    }

    @Nullable
    @Override
    public FileEntity[] getFiles() {
        if (torrent != null && torrent.torrent_file() != null) {
            FileEntity[] files = new FileEntity[torrent.torrent_file().num_files()];
            for (int i = 0; i < files.length; i++) {
                files[i] = new LibTorrentFileEntity(
                        torrent.status().getSave_path(),
                        torrent.torrent_file().file_at(i),
                        LibTorrentPriority.convertPriority(torrent.file_priorities().get(i)),
                        i
                );
            }
            return files;
        }
        return null;
    }

    @Override
    public void setFilePriority(int index, @NonNull TorrentPriority priority) {
        if (torrent != null) {
            torrent.file_priority(index, LibTorrentPriority.convertPriority(priority));
        }
    }

    @Nullable
    @Override
    public TorrentPriority[] getFilePriorities() {
        return torrent != null ? getPriority(torrent.file_priorities()) : null;
    }

    @Nullable
    @Override
    public TorrentPriority[] getPiecePriorities() {
        return torrent != null ? getPriority(torrent.piece_priorities()) : null;
    }

    @Override
    public long getPieceSize() {
        return torrent != null ? torrent.torrent_file().piece_size(0) : 0;
    }

    @Override
    public boolean havePiece(int index) {
        return torrent != null && torrent.have_piece(index);
    }

    @Override
    public int getDownloadRate() {
        return torrent != null ? torrent.status().getDownload_payload_rate() : 0;
    }

    @Override
    public long getTotalSize() {
        return torrent != null ? torrent.status().getTotal_wanted() : 0;
    }

    @Override
    public long getTotalDownloadedSize() {
        return torrent != null ? torrent.status().getTotal_wanted_done() : 0;
    }

    @Override
    public float getProgress() {
        return torrent != null ? torrent.status().getProgress() : 0;
    }

    @Override
    public boolean isPaused() {
        return torrent != null && torrent.status().getPaused();
    }

    @Override
    public boolean isFinished() {
        return torrent != null && torrent.status().getIs_finished();
    }

    @Override
    public void pause() {
        if (torrent != null) {
            torrent.pause();
        }
    }

    @Override
    public void resume() {
        if (torrent != null) {
            torrent.resume();
        }
    }

    @Override
    public void saveResumeData() {
        if (torrent != null) {
            torrent.save_resume_data();
        }
    }

    @Nullable
    @Override
    public byte[] getResumeData() {
        return resumeData;
    }

    @Nullable
    @Override
    public Throwable getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LibTorrentHandle) {
            torrent_handle th = ((LibTorrentHandle) o).getTorrent();
            return th != null && op_equals(th);
        }
        return false;
    }

    @Override
    protected boolean op_equals(@NonNull torrent_handle torrent_handle) {
        return torrent != null && torrent.op_eq(torrent_handle);
    }

    protected void setTorrent(@NonNull torrent_handle torrent) {
        this.torrent = torrent;
    }

    protected void setResumeData(@NonNull byte[] data) {
        this.resumeData = data;
    }

    protected void setError(@NonNull Throwable error) {
        this.error = error;
    }

    @NonNull
    private TorrentPriority[] getPriority(@NonNull int_vector priorities) {
        TorrentPriority[] torrentPriorities = new TorrentPriority[(int) priorities.size()];
        for (int i = 0; i < torrentPriorities.length; i++) {
            torrentPriorities[i] = LibTorrentPriority.convertPriority(priorities.get(i));
        }
        return torrentPriorities;
    }
}