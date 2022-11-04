package se.popcorn_time.model.config;

import android.support.annotation.NonNull;

public final class StaticConfig {

    public static String APP_ID;

    private static Config config;

    private StaticConfig() {
    }

    public static void setConfig(@NonNull Config config) {
        StaticConfig.config = config;
    }

    @NonNull
    public static Config getConfig() {
        return config;
    }
}
