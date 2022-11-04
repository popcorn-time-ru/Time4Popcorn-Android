package se.popcorn_time.model.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.MoviesInfo;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.TvShowsInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.ChoiceProperty;
import se.popcorn_time.model.ObservableChoiceProperty;
import se.popcorn_time.model.ObservableProperty;
import se.popcorn_time.model.Property;
import se.popcorn_time.model.subtitles.Subtitles;

public final class DetailsUseCase implements IDetailsUseCase {

    private final ObservableProperty<VideoInfo> videoInfoProperty = new ObservableProperty<>();
    private final ObservableChoiceProperty<Season> seasonChoiceProperty = new ObservableChoiceProperty<>();
    private final ObservableChoiceProperty<Episode> episodeChoiceProperty = new ObservableChoiceProperty<>();
    private final ObservableChoiceProperty<Map.Entry<String, List<Torrent>>> dubbingChoiceProperty = new ObservableChoiceProperty<>();
    private final ObservableChoiceProperty<Torrent> torrentChoiceProperty = new ObservableChoiceProperty<>();
    private final ObservableChoiceProperty<Map.Entry<String, List<Subtitles>>> langSubtitlesChoiceProperty = new ObservableChoiceProperty<>();
    private final ObservableChoiceProperty<Subtitles> subtitlesChoiceProperty = new ObservableChoiceProperty<>();
    private final ObservableProperty<Subtitles> customSubtitlesProperty = new ObservableProperty<>();

    public DetailsUseCase() {
        videoInfoProperty.getObservable().subscribe(new Consumer<Property<VideoInfo>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Property<VideoInfo> videoInfoProperty) throws Exception {
                final VideoInfo videoInfo = videoInfoProperty.getValue();
                if (videoInfo != null) {
                    if (videoInfo instanceof MoviesInfo) {
                        updateSeasonChoiceProperty(null);
                        updateDubbingChoiceProperty(((MoviesInfo) videoInfo).getLangTorrents());
                    } else if (videoInfo instanceof TvShowsInfo) {
                        updateSeasonChoiceProperty(((TvShowsInfo) videoInfo).getSeasons());
                    }
                } else {
                    updateSeasonChoiceProperty(null);
                }
            }
        });
        seasonChoiceProperty.getObservable().subscribe(new Consumer<ChoiceProperty<Season>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Season> property) throws Exception {
                updateEpisodeChoiceProperty(property.getItem() != null ? property.getItem().getEpisodes() : null);
            }
        });
        episodeChoiceProperty.getObservable().subscribe(new Consumer<ChoiceProperty<Episode>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Episode> property) throws Exception {
                updateDubbingChoiceProperty(property.getItem() != null ? property.getItem().getLangTorrents() : null);
            }
        });
        dubbingChoiceProperty.getObservable().subscribe(new Consumer<ChoiceProperty<Map.Entry<String, List<Torrent>>>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Map.Entry<String, List<Torrent>>> property) throws Exception {
                updateTorrentChoiceProperty(property.getItem() != null ? property.getItem().getValue() : null);
            }
        });
        torrentChoiceProperty.getObservable().subscribe(new Consumer<ChoiceProperty<Torrent>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Torrent> property) throws Exception {
                if (property.getItem() == null) {
                    langSubtitlesChoiceProperty.setItems(null);
                    customSubtitlesProperty.setValue(null);
                }
            }
        });
        langSubtitlesChoiceProperty.getObservable().subscribe(new Consumer<ChoiceProperty<Map.Entry<String, List<Subtitles>>>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Map.Entry<String, List<Subtitles>>> property) throws Exception {
                final Map.Entry<String, List<Subtitles>> subs = property.getItem();
                subtitlesChoiceProperty.setItems(subs != null ? subs.getValue().toArray(new Subtitles[subs.getValue().size()]) : null);
            }
        });
    }

    @NonNull
    @Override
    public ObservableProperty<VideoInfo> getVideoInfoProperty() {
        return videoInfoProperty;
    }

    @NonNull
    @Override
    public ObservableChoiceProperty<Season> getSeasonChoiceProperty() {
        return seasonChoiceProperty;
    }

    @NonNull
    @Override
    public ObservableChoiceProperty<Episode> getEpisodeChoiceProperty() {
        return episodeChoiceProperty;
    }

    @NonNull
    @Override
    public ObservableChoiceProperty<Map.Entry<String, List<Torrent>>> getDubbingChoiceProperty() {
        return dubbingChoiceProperty;
    }

    @NonNull
    @Override
    public ObservableChoiceProperty<Torrent> getTorrentChoiceProperty() {
        return torrentChoiceProperty;
    }

    @NonNull
    @Override
    public ObservableChoiceProperty<Map.Entry<String, List<Subtitles>>> getLangSubtitlesChoiceProperty() {
        return langSubtitlesChoiceProperty;
    }

    @NonNull
    @Override
    public ObservableChoiceProperty<Subtitles> getSubtitlesChoiceProperty() {
        return subtitlesChoiceProperty;
    }

    @NonNull
    @Override
    public ObservableProperty<Subtitles> getCustomSubtitlesProperty() {
        return customSubtitlesProperty;
    }

    private void updateSeasonChoiceProperty(@Nullable List<Season> seasons) {
        seasonChoiceProperty.setItems(seasons != null ? seasons.toArray(new Season[seasons.size()]) : null);
    }

    private void updateEpisodeChoiceProperty(@Nullable List<Episode> episodes) {
        episodeChoiceProperty.setItems(episodes != null ? episodes.toArray(new Episode[episodes.size()]) : null);
    }

    private void updateDubbingChoiceProperty(@Nullable Map<String, List<Torrent>> langTorrents) {
        dubbingChoiceProperty.setItems(langTorrents != null ? langTorrents.entrySet().toArray(new Map.Entry[langTorrents.entrySet().size()]) : null);
    }

    private void updateTorrentChoiceProperty(@Nullable List<Torrent> torrents) {
        torrentChoiceProperty.setItems(torrents != null ? torrents.toArray(new Torrent[torrents.size()]) : null);
    }
}
