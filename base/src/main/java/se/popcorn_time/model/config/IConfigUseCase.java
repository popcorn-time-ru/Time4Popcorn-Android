package se.popcorn_time.model.config;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface IConfigUseCase {

    void getRemoteConfig();

    @NonNull
    Config getConfig();

    void setConfig(@NonNull Config config);

    @NonNull
    Observable<Config> getConfigObservable();
}
