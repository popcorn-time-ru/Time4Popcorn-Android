package se.popcorn_time.mobile.model.content;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
import retrofit2.http.Query;
import se.popcorn_time.base.model.video.info.CinemaMoviesInfo;
import se.popcorn_time.base.model.video.info.CinemaTvShowsInfo;
import se.popcorn_time.base.model.video.info.MoviesInfo;
import se.popcorn_time.base.model.video.info.TvShowsInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.content.IDetailsProvider;
import se.popcorn_time.utils.GsonUtils;

public final class TmdbProvider implements IDetailsProvider {

    private final Api api;
    private final String key;

    public TmdbProvider(@NonNull String url, @NonNull String key) {
        if (TextUtils.isEmpty(url)) {
            this.api = new Api() {
                @Override
                public Observable<Response<ResponseBody>> getMovieInfo(@Path("imdb") String imdb, @Query("api_key") String apiKey) {
                    return Observable.just(Response.success(ResponseBody.create(MediaType.parse("application/json"),"")));
                }

                @Override
                public Observable<Response<ResponseBody>> getMovieBackdrops(@Path("imdb") String imdb, @Query("api_key") String apiKey) {
                    return Observable.just(Response.success(ResponseBody.create(MediaType.parse("application/json"),"")));
                }

                @Override
                public Observable<Response<ResponseBody>> search(@Query(value = "query", encoded = true) String title, @Query("api_key") String apiKey) {
                    return Observable.just(Response.success(ResponseBody.create(MediaType.parse("application/json"),"")));
                }

                @Override
                public Observable<Response<ResponseBody>> getTVShowInfo(@Path("id") String id, @Query("api_key") String apiKey) {
                    return Observable.just(Response.success(ResponseBody.create(MediaType.parse("application/json"),"")));
                }

                @Override
                public Observable<Response<ResponseBody>> getTVShowBackdrops(@Path("id") String id, @Query("api_key") String apiKey) {
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
        this.key = key;
    }

    @Override
    public <T extends VideoInfo> boolean isDetailsExists(T videoInfo) {
        return videoInfo.getDurationMinutes() > 0 && videoInfo.getBackdrops() != null;
    }

    @NonNull
    @Override
    public Observable<? extends VideoInfo> getDetails(final VideoInfo videoInfo) {
        if (videoInfo instanceof CinemaMoviesInfo) {
            return Observable.merge(
                    api.getMovieInfo(videoInfo.getImdb(), key).map(new MoviesInfoRxMapper((CinemaMoviesInfo) videoInfo)),
                    api.getMovieBackdrops(videoInfo.getImdb(), key).map(new BackdropsRxMapper<>((CinemaMoviesInfo) videoInfo))
            ).observeOn(AndroidSchedulers.mainThread());
        } else if (videoInfo instanceof CinemaTvShowsInfo) {
            return api.search(videoInfo.getTitle(), key).observeOn(AndroidSchedulers.mainThread()).concatMap(new TVShowSearchRxMapper((CinemaTvShowsInfo) videoInfo));
        }
        return Observable.error(new IllegalArgumentException("Wrong video info type"));
    }

    private abstract class VideoInfoRxMapper<T extends VideoInfo> implements Function<Response<ResponseBody>, T> {

        final T videoInfo;

        VideoInfoRxMapper(T videoInfo) {
            this.videoInfo = videoInfo;
        }
    }

    private final class MoviesInfoRxMapper extends VideoInfoRxMapper<MoviesInfo> {

        MoviesInfoRxMapper(MoviesInfo videoInfo) {
            super(videoInfo);
        }

        @Override
        public MoviesInfo apply(@io.reactivex.annotations.NonNull Response<ResponseBody> response) throws Exception {
            if (response.isSuccessful()) {
                final JsonObject jsonInfo = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
                videoInfo.setDurationMinutes(GsonUtils.getAsInt(jsonInfo, "runtime"));
            }
            return videoInfo;
        }
    }

    private final class TvShowsInfoRxMapper extends VideoInfoRxMapper<TvShowsInfo> {

        TvShowsInfoRxMapper(TvShowsInfo videoInfo) {
            super(videoInfo);
        }

        @Override
        public TvShowsInfo apply(@io.reactivex.annotations.NonNull Response<ResponseBody> response) throws Exception {
            if (response.isSuccessful()) {
                final JsonObject jsonInfo = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
                final JsonArray jsonEpisodeRunTime = jsonInfo.getAsJsonArray("episode_run_time");
                if (jsonEpisodeRunTime.size() > 0) {
                    videoInfo.setDurationMinutes(jsonEpisodeRunTime.get(0).getAsInt());
                }
            }
            return videoInfo;
        }
    }

    private final class TVShowSearchRxMapper implements Function<Response<ResponseBody>, ObservableSource<TvShowsInfo>> {

        private final TvShowsInfo videoInfo;

        TVShowSearchRxMapper(TvShowsInfo videoInfo) {
            this.videoInfo = videoInfo;
        }

        @Override
        public ObservableSource<TvShowsInfo> apply(@io.reactivex.annotations.NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
            if (responseBodyResponse.isSuccessful()) {
                final JsonArray jsonResults = new JsonParser().parse(responseBodyResponse.body().charStream()).getAsJsonObject().getAsJsonArray("results");
                if (jsonResults.size() == 1) {
                    return getTvShowsInfo(jsonResults.get(0).getAsJsonObject());
                } else if (jsonResults.size() > 1) {
                    final int year = videoInfo.getYear();
                    if (year > 0) {
                        for (JsonElement jsonElement : jsonResults) {
                            final JsonObject jsonResult = jsonElement.getAsJsonObject();
                            final String date = GsonUtils.getAsString(jsonResult, "first_air_date");
                            if (!TextUtils.isEmpty(date)) {
                                final String[] splitDate = date.split("-");
                                if (splitDate.length == 3 && year == Integer.parseInt(splitDate[0])) {
                                    return getTvShowsInfo(jsonResult);
                                }
                            }
                        }
                    }
                    return getTvShowsInfo(jsonResults.get(0).getAsJsonObject());
                }
                return Observable.just(videoInfo);
            }
            throw new Exception(responseBodyResponse.errorBody().string());
        }

        private ObservableSource<TvShowsInfo> getTvShowsInfo(@NonNull JsonObject jsonObject) {
            final String id = GsonUtils.getAsString(jsonObject, "id");
            if (TextUtils.isEmpty(id)) {
                return Observable.just(videoInfo);
            }
            return Observable.merge(
                    api.getTVShowInfo(id, key).map(new TvShowsInfoRxMapper(videoInfo)),
                    api.getTVShowBackdrops(id, key).map(new BackdropsRxMapper<>(videoInfo))
            ).observeOn(AndroidSchedulers.mainThread());
        }
    }

    private final class BackdropsRxMapper<T extends VideoInfo> implements Function<Response<ResponseBody>, T> {

        private static final String FORMAT_BACKDROP_URL = "http://image.tmdb.org/t/p/w780%s";

        private final T videoInfo;

        private BackdropsRxMapper(T videoInfo) {
            this.videoInfo = videoInfo;
        }

        @Override
        public T apply(@io.reactivex.annotations.NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
            if (responseBodyResponse.isSuccessful()) {
                final JsonArray jsonBackdrops = new JsonParser().parse(responseBodyResponse.body().charStream()).getAsJsonObject().getAsJsonArray("backdrops");
                final String[] backdrops = new String[jsonBackdrops.size()];
                for (int i = 0; i < jsonBackdrops.size(); i++) {
                    backdrops[i] = String.format(Locale.ENGLISH, FORMAT_BACKDROP_URL, jsonBackdrops.get(i).getAsJsonObject().get("file_path").getAsString());
                }
                videoInfo.setBackdrops(backdrops);
                return videoInfo;
            }
            throw new Exception(responseBodyResponse.errorBody().string());
        }
    }

    private interface Api {

        @GET("3/movie/{imdb}")
        Observable<Response<ResponseBody>> getMovieInfo(@Path("imdb") String imdb, @Query("api_key") String apiKey);

        @GET("3/movie/{imdb}/images")
        Observable<Response<ResponseBody>> getMovieBackdrops(@Path("imdb") String imdb, @Query("api_key") String apiKey);

        @GET("3/search/tv")
        Observable<Response<ResponseBody>> search(@Query(value = "query", encoded = true) String title, @Query("api_key") String apiKey);

        @GET("3/tv/{id}")
        Observable<Response<ResponseBody>> getTVShowInfo(@Path("id") String id, @Query("api_key") String apiKey);

        @GET("3/tv/{id}/images")
        Observable<Response<ResponseBody>> getTVShowBackdrops(@Path("id") String id, @Query("api_key") String apiKey);
    }
}
