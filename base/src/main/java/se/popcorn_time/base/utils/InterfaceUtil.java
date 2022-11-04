package se.popcorn_time.base.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Locale;

import se.popcorn_time.model.settings.ISettingsUseCase;

public class InterfaceUtil {

    private static final String DEFAULT_INTERFACE_LOCALE = "en";

    private static Locale appLocale;

    public static void init(@NonNull String[] languages, @NonNull ISettingsUseCase settingsUseCase) {
        if (TextUtils.isEmpty(settingsUseCase.getLanguage())) {
            final String locale = getInterfaceSupportedLocale(languages);
            settingsUseCase.setLanguage(locale);
            appLocale = createLocale(locale);
        } else {
            appLocale = createLocale(settingsUseCase.getLanguage());
        }
    }

    public static Locale getAppLocale() {
        return appLocale;
    }

    public static void changeAppLocale(String locale) {
        if (appLocale.getLanguage().equals(locale)) {
            return;
        }
        appLocale = createLocale(locale);
        Logger.debug("Change locale to: " + appLocale.toString());
    }

    private static Locale createLocale(String locale) {
        String[] args = locale.split("_");
        if (args.length == 2) {
            return new Locale(args[0], args[1]);
        }
        return new Locale(locale);
    }

    private static String getInterfaceSupportedLocale(@NonNull String[] languages) {
        String language = Locale.getDefault().getLanguage();
        String locale = Locale.getDefault().toString();
        for (String _locale : languages) {
            if (_locale.equals(language) || _locale.equals(locale)) {
                return _locale;
            }
        }
        return DEFAULT_INTERFACE_LOCALE;
    }
}