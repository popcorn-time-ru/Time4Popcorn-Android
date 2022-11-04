package se.popcorn_time.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.subtitles.Subtitles;

public interface ISubtitlesProvider {

    @NonNull
    Observable<Map.Entry<String, List<Subtitles>>[]> getSubtitles(@NonNull VideoInfo videoInfo,
                                                                @Nullable Season season,
                                                                @Nullable Episode episode,
                                                                @Nullable Torrent torrent);

    @NonNull
    Observable<Map.Entry<String, List<Subtitles>>[]> getSubtitles(@NonNull WatchInfo watchInfo);
}
