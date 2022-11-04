package se.popcorn_time.model.config;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public final class ConfigRemoteRepository implements IConfigRemoteRepository {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Config.class, new ConfigMapper())
            .registerTypeAdapter(VpnConfig.class, new VpnConfigMapper())
            .registerTypeAdapter(VpnConfig.Alert.class, new VpnConfigAlertMapper())
            .registerTypeAdapter(VpnConfig.Notice.class, new VpnConfigNoticeMapper())
            .create();

    @NonNull
    @Override
    public Observable<Config> getConfig(@NonNull String url) {
        try {
            return new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(GSON))
                    .baseUrl(url)
                    .build()
                    .create(Api.class)
                    .getConfig()
                    .observeOn(AndroidSchedulers.mainThread());
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    private interface Api {

        @GET("/v2")
        Observable<Config> getConfig();
    }
}
