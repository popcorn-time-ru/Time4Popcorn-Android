package se.popcorn_time.mobile.model.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
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
import retrofit2.http.QueryMap;

public final class SubtitlesRepository {

    private final Api api;

    public SubtitlesRepository(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            this.api = new Api() {
                @Override
                public Observable<Response<ResponseBody>> getSubtitles(@QueryMap Map<String, String> query) {
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
    public Observable<String> getMovieSubtitles(@NonNull final String imdb, @Nullable String hash) {
        return getSubtitles(new HashMap<String, String>(), imdb, hash);
    }

    @NonNull
    public Observable<String> getTVShowSubtitles(@NonNull final String imdb, @NonNull String season, @NonNull String episode, @Nullable String hash) {
        final Map<String, String> query = new HashMap<>();
        query.put("s", season);
        query.put("ep", episode);
        return getSubtitles(query, imdb, hash);
    }

    @NonNull
    private Observable<String> getSubtitles(@NonNull Map<String, String> query, @NonNull final String imdb, @Nullable String hash) {
        query.put("imdb", imdb);
        if (!TextUtils.isEmpty(hash)) {
            query.put("hash", hash);
        }
        return api.getSubtitles(query).observeOn(AndroidSchedulers.mainThread()).map(new SubtitlesRxMapper());
    }

    private interface Api {

        @GET("list")
        Observable<Response<ResponseBody>> getSubtitles(@QueryMap Map<String, String> query);
    }

    private final class SubtitlesRxMapper implements Function<Response<ResponseBody>, String> {

        @Override
        public String apply(@io.reactivex.annotations.NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
            if (responseBodyResponse.isSuccessful()) {
                return responseBodyResponse.body().string();
            }
            throw new Exception(responseBodyResponse.errorBody().string());
        }
    }
}
