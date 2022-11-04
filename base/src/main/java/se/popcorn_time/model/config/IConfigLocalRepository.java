package se.popcorn_time.model.config;

import android.support.annotation.NonNull;

public interface IConfigLocalRepository {

    @NonNull
    Config getConfig();

    void setConfig(@NonNull Config config);
}
