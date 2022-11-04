package se.popcorn_time.mobile;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import se.popcorn_time.mobile.R;

public final class Language {

    public static final String CODE_ENGLISH = "en";
    public static final String CODE_SPANISH = "es";
    public static final String CODE_DUTCH = "nl";
    public static final String CODE_BRAZILIAN_PORTUGUESE = "pt_BR";
    public static final String CODE_RUSSIAN = "ru";
    public static final String CODE_TURKISH = "tr";
    public static final String CODE_ITALIAN = "it";

    private static final Map<String, Integer> NAMES = new HashMap<>();

    static {
        NAMES.put(CODE_ENGLISH, R.string.lang_english);
        NAMES.put(CODE_SPANISH, R.string.lang_spanish);
        NAMES.put(CODE_DUTCH, R.string.lang_dutch);
        NAMES.put(CODE_BRAZILIAN_PORTUGUESE, R.string.lang_brazilian_portuguese);
        NAMES.put(CODE_RUSSIAN, R.string.lang_russian);
        NAMES.put(CODE_TURKISH, R.string.lang_turkish);
        NAMES.put(CODE_ITALIAN, R.string.lang_italian);
    }

    private Language() {
    }

    public static String getName(@NonNull Resources resources, @NonNull String language) {
        if (NAMES.containsKey(language)) {
            return resources.getString(NAMES.get(language));
        }
        return language;
    }
}
