package se.popcorn_time.base.prefs;

import android.content.Context;

public final class Prefs {

    private final static Prefs INSTANCE = new Prefs();

    private PopcornPrefs popcornPrefs;

    private Prefs() {

    }

    public static void init(Context context) {
        INSTANCE.popcornPrefs = new PopcornPrefs(context);
    }

    public static PopcornPrefs getPopcornPrefs() {
        return INSTANCE.popcornPrefs;
    }
}