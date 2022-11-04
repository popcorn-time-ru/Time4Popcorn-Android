package se.popcorn_time.base.subtitles;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import se.popcorn_time.base.R;

public final class SubtitlesFontSize {

    public static final float EXTRA_SMALL = 0.7f;
    public static final float SMALL = 0.85f;
    public static final float NORMAL = 1f;
    public static final float LARGE = 1.25f;
    public static final float EXTRA_LARGE = 1.5f;

    private SubtitlesFontSize() {
    }

    @NonNull
    public static String getName(@NonNull Resources resources, @NonNull Float subtitlesFontSize) {
        if (EXTRA_SMALL == subtitlesFontSize) {
            return resources.getString(R.string.extra_small);
        } else if (SMALL == subtitlesFontSize) {
            return resources.getString(R.string.small);
        } else if (NORMAL == subtitlesFontSize) {
            return resources.getString(R.string.normal);
        } else if (LARGE == subtitlesFontSize) {
            return resources.getString(R.string.large);
        } else if (EXTRA_LARGE == subtitlesFontSize) {
            return resources.getString(R.string.extra_large);
        }
        return Float.toString(subtitlesFontSize);
    }
}