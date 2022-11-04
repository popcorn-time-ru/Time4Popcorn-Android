package se.popcorn_time.base.subtitles;

import android.content.res.Resources;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;

import se.popcorn_time.base.R;
import se.popcorn_time.model.settings.ISettingsUseCase;

public final class SubtitlesLanguage {

    private static final String ISO_BRAZILIAN_PORTUGUESE = "pt_br";
    private static final String ISO_HEBREW = "iw";
    private static final String ISO_HINDI = "hi";

    private static final String NAME_BRAZILIAN_PORTUGUESE = "brazilian-portuguese";
    private static final String NAME_HEBREW = "hebrew";
    private static final String NAME_HINDI = "hindi";

    private static final String NATIVE_BRAZILIAN_PORTUGUESE = "Brazilian Portuguese";
    private static final String NATIVE_HEBREW = "עברית";
    private static final String NATIVE_HINDI = "हिन्दी";

    public static final String DEFAULT_SUBTITLE_LANGUAGE = "";
    public static final int POSITION_WITHOUT_SUBTITLES = 0;

    private static ISettingsUseCase settingsUseCase;

    private static String[] subtitlesName;
    private static String[] subtitlesNative;
    private static String[] subtitlesIso;

    private static HashMap<String, String> subtitlesNativeByName;
    private static HashMap<String, String> subtitlesNameByIso;
    private static HashMap<String, String> subtitlesIsoByName;

    private SubtitlesLanguage() {
    }

    public static void init(Resources resources, String[] subtitlesName, ISettingsUseCase settingsUseCase) {
        SubtitlesLanguage.subtitlesName = subtitlesName;
        subtitlesNative = resources.getStringArray(R.array.subtitles_native);
        subtitlesIso = resources.getStringArray(R.array.subtitles_iso);

        SubtitlesLanguage.settingsUseCase = settingsUseCase;

        subtitlesNativeByName = new HashMap<>();
        int count = subtitlesName.length <= subtitlesNative.length ? subtitlesName.length : subtitlesNative.length;
        for (int i = 1; i < count; i++) {
            subtitlesNativeByName.put(subtitlesName[i], subtitlesNative[i]);
        }
        subtitlesNativeByName.put(NAME_BRAZILIAN_PORTUGUESE, NATIVE_BRAZILIAN_PORTUGUESE);
        subtitlesNativeByName.put(NAME_HEBREW, NATIVE_HEBREW);
        subtitlesNativeByName.put(NAME_HINDI, NATIVE_HINDI);

        subtitlesNameByIso = new HashMap<>();
        count = subtitlesIso.length <= subtitlesName.length ? subtitlesIso.length : subtitlesName.length;
        for (int i = 1; i < count; i++) {
            subtitlesNameByIso.put(subtitlesIso[i], subtitlesName[i]);
        }
        subtitlesNameByIso.put(ISO_BRAZILIAN_PORTUGUESE, NAME_BRAZILIAN_PORTUGUESE);
        subtitlesNameByIso.put(ISO_HEBREW, NAME_HEBREW);
        subtitlesNameByIso.put(ISO_HINDI, NAME_HINDI);

        subtitlesIsoByName = new HashMap<>();
        count = subtitlesName.length <= subtitlesNative.length ? subtitlesName.length : subtitlesNative.length;
        for (int i = 1; i < count; i++) {
            subtitlesIsoByName.put(subtitlesName[i], subtitlesIso[i]);
        }
        subtitlesIsoByName.put(NAME_BRAZILIAN_PORTUGUESE, ISO_BRAZILIAN_PORTUGUESE);
        subtitlesIsoByName.put(NAME_HEBREW, ISO_HEBREW);
        subtitlesIsoByName.put(NAME_HINDI, ISO_HINDI);

        if (settingsUseCase.getSubtitlesLanguage() == null) {
            settingsUseCase.setSubtitlesLanguage(getDefaultSubtitlesLanguage());
        }
    }

    private static String getDefaultSubtitlesLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (subtitlesNameByIso.containsKey(language)) {
            return subtitlesNameByIso.get(language);
        }
        return DEFAULT_SUBTITLE_LANGUAGE;
    }

    public static String getSubtitlesLanguage() {
        return settingsUseCase.getSubtitlesLanguage();
    }

    public static void setWithoutSubtitlesText(String text) {
        subtitlesNative[POSITION_WITHOUT_SUBTITLES] = text;
    }

    public static String[] getSubtitlesName() {
        return subtitlesName;
    }

    public static String[] getSubtitlesNative() {
        return subtitlesNative;
    }

    public static String subtitlesNameToNative(String name) {
        if (TextUtils.isEmpty(name)) {
            return subtitlesNative[POSITION_WITHOUT_SUBTITLES];
        }
        name = name.toLowerCase();
        if (subtitlesNativeByName.containsKey(name)) {
            return subtitlesNativeByName.get(name);
        }
        return name;
    }

    public static String subtitlesIsoToName(String iso) {
        iso = iso.toLowerCase();
        if (subtitlesNameByIso.containsKey(iso)) {
            return subtitlesNameByIso.get(iso);
        }
        return iso;
    }
}