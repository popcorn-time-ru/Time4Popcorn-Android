package se.popcorn_time.mobile.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import se.popcorn_time.base.model.video.info.AnimeMoviesInfo;
import se.popcorn_time.base.model.video.info.AnimeTvShowsInfo;
import se.popcorn_time.base.model.video.info.CinemaMoviesInfo;
import se.popcorn_time.base.model.video.info.CinemaTvShowsInfo;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.MoviesInfo;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.TvShowsInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.filter.IFilter;
import se.popcorn_time.model.filter.IFilterItem;
import se.popcorn_time.utils.GsonUtils;

public final class ContentRepository {

    public static final String KEY_OS = "os";
    public static final String KEY_APPLICATION_ID = "app_id";
    public static final String KEY_VERSION = "ver";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_SORT_BY = "sort";
    public static final String KEY_QUALITY = "quality";
    public static final String KEY_KEYWORDS = "keywords";
    public static final String KEY_PAGE = "page";
    public static final String KEY_IMDB = "imdb";

    public static final String GENRE_NONE = "";
    public static final String GENRE_POPULAR = "all";
    public static final String GENRE_ACTION = "action";
    public static final String GENRE_ADVENTURE = "adventure";
    public static final String GENRE_ANIMATION = "animation";
    public static final String GENRE_BIOGRAPHY = "biography";
    public static final String GENRE_COMEDY = "comedy";
    public static final String GENRE_CRIME = "crime";
    public static final String GENRE_DOCUMENTARY = "documentary";
    public static final String GENRE_DRAMA = "drama";
    public static final String GENRE_FAMILY = "family";
    public static final String GENRE_FANTASY = "fantasy";
    public static final String GENRE_FILM_NOIR = "film-noir";
    public static final String GENRE_HISTORY = "history";
    public static final String GENRE_HORROR = "horror";
    public static final String GENRE_MUSIC = "music";
    public static final String GENRE_MUSICAL = "musical";
    public static final String GENRE_MYSTERY = "mystery";
    public static final String GENRE_ROMANCE = "romance";
    public static final String GENRE_SCI_FI = "sci-fi";
    public static final String GENRE_SHORT = "short";
    public static final String GENRE_SPORT = "sport";
    public static final String GENRE_THRILLER = "thriller";
    public static final String GENRE_WAR = "war";
    public static final String GENRE_WESTERN = "western";

    public static final String SORT_BY_POPULARITY = "seeds";
    public static final String SORT_BY_DATE_ADDED = "dateadded";
    public static final String SORT_BY_YEAR = "year";

    public static final String QUALITY_480p = "480p";
    public static final String QUALITY_720p = "720p";
    public static final String QUALITY_1080p = "1080p";
    public static final String QUALITY_3d = "3d";

    private final String os;
    private final String applicationId;
    private final String version;
    private final Api api;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(CinemaMoviesInfo.class, new MovieGsonMapper())
            .registerTypeAdapter(AnimeMoviesInfo.class, new MovieGsonMapper())
            .registerTypeAdapter(CinemaTvShowsInfo.class, new TVShowGsonMapper())
            .registerTypeAdapter(AnimeTvShowsInfo.class, new TVShowGsonMapper())
            .registerTypeAdapter(Episode.class, new EpisodeGsonMapper())
            .registerTypeAdapter(Torrent.class, new TorrentGsonMapper())
            .create();

    public ContentRepository(@NonNull String os, @NonNull String applicationId, @NonNull String version, @NonNull String url) {
        this.os = os;
        this.applicationId = applicationId;
        this.version = version;
        if (TextUtils.isEmpty(url)) {
            this.api = new Api() {

                @Override
                public Observable<Response<ResponseBody>> getVideos(@Path(value = "path") String path, @QueryMap Map<String, String> queries) {
                    return Observable.just(Response.success(ResponseBody.create(MediaType.parse("application/json"),"")));
                }

                @Override
                public Observable<Response<ResponseBody>> getSeasons(@QueryMap Map<String, String> queries) {
                    return Observable.just(Response.success(ResponseBody.create(MediaType.parse("application/json"),"")));
                }
            };
        } else {
            this.api = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .baseUrl(url)
                    .build()
                    .create(Api.class);
        }
    }

    @NonNull
    public <T extends MoviesInfo> Observable<List<T>> getMovies(@NonNull IFilter[] filters,
                                                                @Nullable String keywords,
                                                                int page,
                                                                @NonNull TypeToken<List<T>> typeToken) {
        return getVideos("list", getQueries(filters, keywords, page), typeToken);
    }

    @NonNull
    public <T extends TvShowsInfo> Observable<List<T>> getTVShows(@NonNull IFilter[] filters,
                                                                  @Nullable String keywords,
                                                                  int page,
                                                                  @NonNull TypeToken<List<T>> typeToken) {
        return getVideos("shows", getQueries(filters, keywords, page), typeToken);
    }

    @NonNull
    public Observable<ArrayList<Season>> getSeasons(@NonNull String imdb, @NonNull IFilter qualityFilter) {
        final Map<String, String> queries = getQueries(qualityFilter);
        queries.put(KEY_IMDB, imdb);
        return api.getSeasons(queries).observeOn(AndroidSchedulers.mainThread()).map(new SeasonsRxMapper(gson));
    }

    @NonNull
    private <T extends VideoInfo> Observable<List<T>> getVideos(String path, Map<String, String> queries, TypeToken<List<T>> typeToken) {
        return api.getVideos(path, queries)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new VideosRxMapper<>(gson, typeToken))
                .retry(2);
    }

    @NonNull
    private Map<String, String> getQueries(@NonNull IFilter[] filters, @Nullable String keywords, int page) {
        final Map<String, String> queries = getQueries(filters);
        if (!TextUtils.isEmpty(keywords)) {
            queries.put(KEY_KEYWORDS, keywords);
        }
        queries.put(KEY_PAGE, Integer.toString(page));
        return queries;
    }

    @NonNull
    private Map<String, String> getQueries(@NonNull IFilter... filters) {
        final Map<String, String> queries = new HashMap<>();
        queries.put(KEY_OS, os);
        queries.put(KEY_APPLICATION_ID, applicationId);
        queries.put(KEY_VERSION, version);
        for (IFilter filter : filters) {
            final StringBuilder builder = new StringBuilder();
            for (IFilterItem item : filter.getItems()) {
                if (filter.isChecked(item)) {
                    if (builder.length() > 0) {
                        builder.append(",");
                    }
                    builder.append(item.getValue());
                }
            }
            if (builder.length() != 0) {
                queries.put(filter.getName(), builder.toString());
            }
        }
        return queries;
    }

    private interface Api {

        @GET("{path}")
        Observable<Response<ResponseBody>> getVideos(@Path(value = "path") String path, @QueryMap Map<String, String> queries);

        @GET("show")
        Observable<Response<ResponseBody>> getSeasons(@QueryMap Map<String, String> queries);
    }

    private static final class VideosRxMapper<T extends VideoInfo> implements Function<Response<ResponseBody>, List<T>> {

        private final Gson gson;
        private final TypeToken<List<T>> typeToken;

        public VideosRxMapper(@NonNull Gson gson, @NonNull TypeToken<List<T>> typeToken) {
            this.gson = gson;
            this.typeToken = typeToken;
        }

        @Override
        public ArrayList<T> apply(@io.reactivex.annotations.NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
            if (responseBodyResponse.isSuccessful()) {
                return gson.fromJson(
                        new JsonParser().parse(responseBodyResponse.body().charStream()).getAsJsonObject().get("MovieList"),
                        typeToken.getType()
                );
            }
            throw new Exception(responseBodyResponse.errorBody().string());
        }
    }

    private static final class SeasonsRxMapper implements Function<Response<ResponseBody>, ArrayList<Season>> {

        private final Gson gson;

        public SeasonsRxMapper(@NonNull Gson gson) {
            this.gson = gson;
        }

        @Override
        public ArrayList<Season> apply(@io.reactivex.annotations.NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
            if (responseBodyResponse.isSuccessful()) {
                final JsonObject jsonObject = new JsonParser().parse(responseBodyResponse.body().charStream()).getAsJsonObject();
                final ArrayList<Season> seasons = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    final Season season = new Season();
                    season.setNumber(Integer.parseInt(entry.getKey()));
                    season.setEpisodes(gson.<List<Episode>>fromJson(entry.getValue(), new TypeToken<ArrayList<Episode>>() {}.getType()));
                    seasons.add(season);
                }
                return seasons;
            }
            throw new Exception(responseBodyResponse.errorBody().string());
        }
    }

    private static class VideoGsonMapper<T extends VideoInfo> implements JsonDeserializer<T> {

        private static final String KEY_TITLE = "title";
        private static final String KEY_YEAR = "year";
        private static final String KEY_RATING = "rating";
        private static final String KEY_DESCRIPTION = "description";
        private static final String KEY_ACTORS = "actors";
        private static final String KEY_POSTER_MEDIUM = "poster_med";
        private static final String KEY_POSTER_BIG = "poster_big";
        private static final String KEY_IMDB = "imdb";
        private static final String KEY_GENRES = "genres";
        private static final String KEY_TRAILER = "trailer";

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonVideo = (JsonObject) json;
            final T video;
            try {
                video = ((Class<T>) typeOfT).newInstance();
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
            video.setImdb(GsonUtils.getAsString(jsonVideo, KEY_IMDB));
            video.setTitle(GsonUtils.getAsString(jsonVideo, KEY_TITLE));
            video.setYear(GsonUtils.getAsInt(jsonVideo, KEY_YEAR));
            video.setRating(GsonUtils.getAsFloat(jsonVideo, KEY_RATING));
            video.setDescription(GsonUtils.getAsString(jsonVideo, KEY_DESCRIPTION));
            video.setActors(GsonUtils.getAsString(jsonVideo, KEY_ACTORS));
            video.setPoster(GsonUtils.getAsString(jsonVideo, KEY_POSTER_MEDIUM));
            video.setPosterBig(GsonUtils.getAsString(jsonVideo, KEY_POSTER_BIG));
            video.setTrailer(getTrailerUrl(GsonUtils.getAsString(jsonVideo, KEY_TRAILER)));
            video.setGenres(context.<String[]>deserialize(jsonVideo.get(KEY_GENRES), String[].class));
            return video;
        }

        @Nullable
        private String getTrailerUrl(@Nullable String trailer) {
            if (TextUtils.isEmpty(trailer)) {
                return null;
            }
            return "https://www.youtube.com/embed/" + trailer + "?autoplay=1";
        }
    }

    private static final class MovieGsonMapper extends VideoGsonMapper<MoviesInfo> {

        private static final String KEY_TORRENTS = "items";
        private static final String KEY_TORRENTS_LANG = "items_lang";

        private static final String KEY_TORRENT_LANG = "language";

        @Override
        public MoviesInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonMovie = (JsonObject) json;
            final MoviesInfo movie = super.deserialize(json, typeOfT, context);
            final Map<String, List<Torrent>> map = new LinkedHashMap<>();
            map.put("", context.<List<Torrent>>deserialize(jsonMovie.get(KEY_TORRENTS), new TypeToken<ArrayList<Torrent>>() {}.getType()));
            if (jsonMovie.has(KEY_TORRENTS_LANG)) {
                final JsonArray jsonTorrentsLang = jsonMovie.getAsJsonArray(KEY_TORRENTS_LANG);
                for (JsonElement jsonElement : jsonTorrentsLang) {
                    final String lang = jsonElement.getAsJsonObject().get(KEY_TORRENT_LANG).getAsString();
                    if (map.get(lang) == null) {
                        map.put(lang, new ArrayList<Torrent>());
                    }
                    map.get(lang).add(context.<Torrent>deserialize(jsonElement, Torrent.class));
                }
            }
            movie.setLangTorrents(map);
            return movie;
        }
    }

    private static final class TVShowGsonMapper extends VideoGsonMapper<TvShowsInfo> {
    }

    private static final class EpisodeGsonMapper implements JsonDeserializer<Episode> {

        private static final String KEY_NUMBER = "episode";
        private static final String KEY_TITLE = "title";
        private static final String KEY_DESCRIPTION = "synopsis";
        private static final String KEY_AIR_DATE = "air_time";
        private static final String KEY_TORRENTS = "items";
        private static final String KEY_TORRENTS_LANG = "items_lang";

        private static final String KEY_TORRENT_LANG = "language";

        @Override
        public Episode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonEpisode = (JsonObject) json;
            final Episode episode = new Episode();
            episode.setNumber(GsonUtils.getAsInt(jsonEpisode, KEY_NUMBER));
            episode.setTitle(GsonUtils.getAsString(jsonEpisode, KEY_TITLE));
            episode.setDescription(GsonUtils.getAsString(jsonEpisode, KEY_DESCRIPTION));
            final String airDate = GsonUtils.getAsString(jsonEpisode, KEY_AIR_DATE);
            episode.setAirDate("0".equals(airDate) ? null : airDate);
            final Map<String, List<Torrent>> map = new LinkedHashMap<>();
            map.put("", context.<List<Torrent>>deserialize(jsonEpisode.get(KEY_TORRENTS), new TypeToken<ArrayList<Torrent>>() {}.getType()));
            if (jsonEpisode.has(KEY_TORRENTS_LANG)) {
                final JsonArray jsonTorrentsLang = jsonEpisode.getAsJsonArray(KEY_TORRENTS_LANG);
                for (JsonElement jsonElement : jsonTorrentsLang) {
                    final String lang = jsonElement.getAsJsonObject().get(KEY_TORRENT_LANG).getAsString();
                    if (map.get(lang) == null) {
                        map.put(lang, new ArrayList<Torrent>());
                    }
                    map.get(lang).add(context.<Torrent>deserialize(jsonElement, Torrent.class));
                }
            }
            episode.setLangTorrents(map);
            return episode;
        }
    }

    private static final class TorrentGsonMapper implements JsonDeserializer<Torrent> {

        private static final String KEY_HASH = "id";
        private static final String KEY_URL = "torrent_url";
        private static final String KEY_MAGNET = "torrent_magnet";
        private static final String TORRENT_SEEDS = "torrent_seeds";
        private static final String TORRENT_PEERS = "torrent_peers";
        private static final String KEY_FILE = "file";
        private static final String KEY_QUALITY = "quality";
        private static final String KEY_SIZE = "size_bytes";

        @Override
        public Torrent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonTorrent = (JsonObject) json;
            final Torrent torrent = new Torrent();
            torrent.setHash(GsonUtils.getAsString(jsonTorrent, KEY_HASH));
            torrent.setUrl(GsonUtils.getAsString(jsonTorrent, KEY_URL));
            torrent.setMagnet(GsonUtils.getAsString(jsonTorrent, KEY_MAGNET));
            torrent.setFile(GsonUtils.getAsString(jsonTorrent, KEY_FILE));
            torrent.setQuality(GsonUtils.getAsString(jsonTorrent, KEY_QUALITY));
            torrent.setSize(GsonUtils.getAsLong(jsonTorrent, KEY_SIZE));
            torrent.setSeeds(GsonUtils.getAsInt(jsonTorrent, TORRENT_SEEDS));
            torrent.setPeers(GsonUtils.getAsInt(jsonTorrent, TORRENT_PEERS));
            return torrent;
        }
    }
}
