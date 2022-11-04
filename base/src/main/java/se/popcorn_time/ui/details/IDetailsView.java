package se.popcorn_time.ui.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;

public interface IDetailsView<T extends VideoInfo> {

    void onVideoInfo(@NonNull T videoInfo);

    void onDubbing(@Nullable String[] languages, int position);

    void onTorrents(@Nullable Torrent[] torrents, int position);

    void onLangSubtitles(@Nullable String[] languages, int position);
}
