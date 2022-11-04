package se.popcorn_time.base.subtitles;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import se.popcorn_time.base.R;

public final class SubtitlesFontColor {

    public static final String WHITE = "#ffffff";
    public static final String YELLOW = "#ffff00";

    private SubtitlesFontColor() {
    }

    @NonNull
    public static String getName(@NonNull Resources resources, @NonNull String subtitlesFontColor) {
        switch (subtitlesFontColor) {
            case WHITE:
                return resources.getString(R.string.white);
            case YELLOW:
                return resources.getString(R.string.yellow);
        }
        return subtitlesFontColor;
    }
}