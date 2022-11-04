package se.popcorn_time.mobile.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.player.subtitles.format.SRTFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.model.video.Cinema;
import se.popcorn_time.base.model.video.info.CinemaMoviesInfo;
import se.popcorn_time.base.model.video.info.CinemaTvShowsInfo;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.content.ISubtitlesProvider;
import se.popcorn_time.model.subtitles.Subtitles;
import se.popcorn_time.utils.GsonUtils;

public final class SubtitlesProvider implements ISubtitlesProvider {

    private final SubtitlesRepository repository;

    public SubtitlesProvider(@NonNull SubtitlesRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public Observable<Map.Entry<String, List<Subtitles>>[]> getSubtitles(@NonNull VideoInfo videoInfo,
                                                                         @Nullable Season season,
                                                                         @Nullable Episode episode,
                                                                         @Nullable Torrent torrent) {
        if (videoInfo instanceof CinemaMoviesInfo) {
            return repository.getMovieSubtitles(videoInfo.getImdb(), torrent != null ? torrent.getHash() : null)
                    .map(new SubtitlesRxMapper());
        } else if (videoInfo instanceof CinemaTvShowsInfo) {
            return repository.getTVShowSubtitles(
                    videoInfo.getImdb(),
                    season != null ? Integer.toString(season.getNumber()) : "",
                    episode != null ? Integer.toString(episode.getNumber()) : "",
                    torrent != null ? torrent.getHash() : null
            ).map(new SubtitlesRxMapper());
        }
        return Observable.error(new IllegalArgumentException("Wrong video info type"));
    }

    @NonNull
    @Override
    public Observable<Map.Entry<String, List<Subtitles>>[]> getSubtitles(@NonNull WatchInfo watchInfo) {
        switch (watchInfo.type) {
            case Cinema.TYPE_MOVIES:
                return repository.getMovieSubtitles(watchInfo.imdb, null)
                        .map(new SubtitlesRxMapper());
            case Cinema.TYPE_TV_SHOWS:
                return repository.getTVShowSubtitles(
                        watchInfo.imdb,
                        Integer.toString(watchInfo.season),
                        Integer.toString(watchInfo.episode),
                        null
                ).map(new SubtitlesRxMapper());
        }
        return Observable.error(new IllegalArgumentException("Wrong video info type"));
    }

    private static final class SubtitlesRxMapper implements Function<String, Map.Entry<String, List<Subtitles>>[]> {

        private static final String KEY_URL = "url";
        private static final String KEY_FORMAT = "format";
        private static final String KEY_DELAY = "delay";

        @Override
        public Map.Entry<String, List<Subtitles>>[] apply(@io.reactivex.annotations.NonNull String response) throws Exception {
            final JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();
            final JsonObject jsonSubtitles = jsonResponse.getAsJsonObject("subs");
            final Map<String, List<Subtitles>> langSubtitles = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonSubtitles.entrySet()) {
                final JsonArray subInfos = entry.getValue().getAsJsonArray();
                final List<Subtitles> subtitles = new ArrayList<>();
                for (JsonElement jsonElement : subInfos) {
                    final JsonObject jsonInfo = jsonElement.getAsJsonObject();
                    final String format = GsonUtils.getAsString(jsonInfo, KEY_FORMAT);
                    if (SRTFormat.EXTENSION.equals(format)) {
                        final Subtitles sub = new Subtitles();
                        sub.setUrl(GsonUtils.getAsString(jsonInfo, KEY_URL));
                        sub.setDelay(GsonUtils.getAsInt(jsonInfo, KEY_DELAY));
                        subtitles.add(sub);
                    }
                }
                langSubtitles.put(entry.getKey(), subtitles);
            }
            return langSubtitles.entrySet().toArray(new Map.Entry[langSubtitles.entrySet().size()]);
        }
    }
}
