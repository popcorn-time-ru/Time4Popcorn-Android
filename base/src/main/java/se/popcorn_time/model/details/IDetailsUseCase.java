package se.popcorn_time.model.details;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.ObservableChoiceProperty;
import se.popcorn_time.model.ObservableProperty;
import se.popcorn_time.model.subtitles.Subtitles;

public interface IDetailsUseCase {

    @NonNull
    ObservableProperty<VideoInfo> getVideoInfoProperty();

    @NonNull
    ObservableChoiceProperty<Season> getSeasonChoiceProperty();

    @NonNull
    ObservableChoiceProperty<Episode> getEpisodeChoiceProperty();

    @NonNull
    ObservableChoiceProperty<Map.Entry<String, List<Torrent>>> getDubbingChoiceProperty();

    @NonNull
    ObservableChoiceProperty<Torrent> getTorrentChoiceProperty();

    @NonNull
    ObservableChoiceProperty<Map.Entry<String, List<Subtitles>>> getLangSubtitlesChoiceProperty();

    @NonNull
    ObservableChoiceProperty<Subtitles> getSubtitlesChoiceProperty();

    @NonNull
    ObservableProperty<Subtitles> getCustomSubtitlesProperty();
}
