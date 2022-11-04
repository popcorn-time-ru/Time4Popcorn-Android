package se.popcorn_time.base.prefs;

import android.content.Context;

public class PopcornPrefs extends BasePrefs {

    public static final String UPDATE_APK_PATH = "update-apk-path";
    public static final String LAST_TORRENT = "last-torrent";
    public static final String ON_START_VPN_PACKAGE = "on-start-vpn-package";
    public static final String KEY_FULL_VERSION = "full-version";

    public PopcornPrefs(Context context) {
        super(context);
    }

    @Override
    protected String getPrefsName() {
        return "PopcornPreferences";
    }

    public boolean isFullVersion(boolean defaultValue) {
        return get(KEY_FULL_VERSION, defaultValue);
    }

    public void setFullVersion(boolean fullVersion) {
        put(KEY_FULL_VERSION, fullVersion);
    }
}