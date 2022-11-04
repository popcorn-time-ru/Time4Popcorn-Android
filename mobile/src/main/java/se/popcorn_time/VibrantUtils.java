package se.popcorn_time;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

public final class VibrantUtils {

    private static int accentColor;

    private VibrantUtils() {
    }

    public static int getAccentColor() {
        return accentColor;
    }

    public static void setAccentColor(Bitmap bitmap, final int defaultColor) {
        final Palette palette = Palette.from(bitmap).generate();
        accentColor = palette.getVibrantColor(defaultColor);
        if (accentColor == defaultColor) {
            accentColor = palette.getMutedColor(defaultColor);
        }
    }
}
