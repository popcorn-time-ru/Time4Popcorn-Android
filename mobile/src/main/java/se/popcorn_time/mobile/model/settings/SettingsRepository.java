package se.popcorn_time.mobile.model.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.subtitles.SubtitlesFontColor;
import se.popcorn_time.base.subtitles.SubtitlesFontSize;
import se.popcorn_time.base.torrent.TorrentService;
import se.popcorn_time.mobile.PlayerHardwareAcceleration;
import se.popcorn_time.mobile.StartPage;
import se.popcorn_time.model.settings.ISettingsRepository;

public final class SettingsRepository implements ISettingsRepository {

    private static final String KEY_LANGUAGE = "app-locale";
    private static final String KEY_START_PAGE = "start-page";
    private static final String KEY_HARDWARE_ACCELERATION = "hardware-acceleration";
    private static final String KEY_SUBTITLES_LANGUAGE = "subtitle-language";
    private static final String KEY_SUBTITLES_FONT_SIZE = "subtitle-font-size";
    private static final String KEY_SUBTITLES_FONT_COLOR = "subtitle-font-color";
    private static final String KEY_CHECK_VPN = "check-vpn";
    private static final String KEY_WIFI_ONLY = "only-wifi-connection";
    private static final String KEY_CONNECTIONS_LIMIT = "connections-limit";
    private static final String KEY_DOWNLOAD_SPEED = "maximum-download-speed";
    private static final String KEY_UPLOAD_SPEED = "maximum-upload-speed";
    private static final String KEY_CACHE_FOLDER = "chache-folder-path";
    private static final String KEY_CLEAR_CACHE_FOLDER = "clear-on-exit";

    private final SharedPreferences preferences;

    public SettingsRepository(@NonNull SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Nullable
    @Override
    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, null);
    }

    @Override
    public void setLanguage(@NonNull String language) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply();
    }

    @NonNull
    @Override
    public Integer getStartPage() {
        return preferences.getInt(KEY_START_PAGE, StartPage.DEFAULT_START_PAGE);
    }

    @Override
    public void setStartPage(@NonNull Integer startPage) {
        preferences.edit().putInt(KEY_START_PAGE, startPage).apply();
    }

    @NonNull
    @Override
    public Integer getPlayerHardwareAcceleration() {
        return preferences.getInt(KEY_HARDWARE_ACCELERATION, PlayerHardwareAcceleration.AUTOMATIC);
    }

    @Override
    public void setPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration) {
        preferences.edit().putInt(KEY_HARDWARE_ACCELERATION, playerHardwareAcceleration).apply();
    }

    @NonNull
    @Override
    public String getSubtitlesLanguage() {
        return preferences.getString(KEY_SUBTITLES_LANGUAGE, null);
    }

    @Override
    public void setSubtitlesLanguage(@NonNull String subtitlesLanguage) {
        preferences.edit().putString(KEY_SUBTITLES_LANGUAGE, subtitlesLanguage).apply();
    }

    @NonNull
    @Override
    public Float getSubtitlesFontSize() {
        return preferences.getFloat(KEY_SUBTITLES_FONT_SIZE, SubtitlesFontSize.NORMAL);
    }

    @Override
    public void setSubtitlesFontSize(@NonNull Float subtitlesFontSize) {
        preferences.edit().putFloat(KEY_SUBTITLES_FONT_SIZE, subtitlesFontSize).apply();
    }

    @NonNull
    @Override
    public String getSubtitlesFontColor() {
        return preferences.getString(KEY_SUBTITLES_FONT_COLOR, SubtitlesFontColor.WHITE);
    }

    @Override
    public void setSubtitlesFontColor(@NonNull String subtitlesFontColor) {
        preferences.edit().putString(KEY_SUBTITLES_FONT_COLOR, subtitlesFontColor).apply();
    }

    @Nullable
    @Override
    public Boolean isDownloadsCheckVpn() {
        if (preferences.contains(KEY_CHECK_VPN)) {
            return preferences.getBoolean(KEY_CHECK_VPN, true);
        }
        return null;
    }

    @Override
    public void setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn) {
        preferences.edit().putBoolean(KEY_CHECK_VPN, downloadsCheckVpn).apply();
    }

    @NonNull
    @Override
    public Boolean isDownloadsWifiOnly() {
        return preferences.getBoolean(KEY_WIFI_ONLY, false);
    }

    @Override
    public void setDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly) {
        preferences.edit().putBoolean(KEY_WIFI_ONLY, downloadsWifiOnly).apply();
    }

    @NonNull
    @Override
    public Integer getDownloadsConnectionsLimit() {
        return preferences.getInt(KEY_CONNECTIONS_LIMIT, TorrentService.DEFAULT_CONNECTIONS_LIMIT);
    }

    @Override
    public void setDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit) {
        preferences.edit().putInt(KEY_CONNECTIONS_LIMIT, downloadsConnectionsLimit).apply();
    }

    @NonNull
    @Override
    public Integer getDownloadsDownloadSpeed() {
        return preferences.getInt(KEY_DOWNLOAD_SPEED, TorrentService.DEFAULT_DOWNLOAD_SPEED);
    }

    @Override
    public void setDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed) {
        preferences.edit().putInt(KEY_DOWNLOAD_SPEED, downloadsDownloadSpeed).apply();
    }

    @NonNull
    @Override
    public Integer getDownloadsUploadSpeed() {
        return preferences.getInt(KEY_UPLOAD_SPEED, TorrentService.DEFAULT_UPLOAD_SPEED);
    }

    @Override
    public void setDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed) {
        preferences.edit().putInt(KEY_UPLOAD_SPEED, downloadsUploadSpeed).apply();
    }

    @Nullable
    @Override
    public File getDownloadsCacheFolder() {
        final String path = preferences.getString(KEY_CACHE_FOLDER, null);
        return TextUtils.isEmpty(path) ? null : new File(path);
    }

    @Override
    public void setDownloadsCacheFolder(@NonNull File downloadsCacheFolder) {
        preferences.edit().putString(KEY_CACHE_FOLDER, downloadsCacheFolder.getAbsolutePath()).apply();
    }

    @NonNull
    @Override
    public Boolean isDownloadsClearCacheFolder() {
        return preferences.getBoolean(KEY_CLEAR_CACHE_FOLDER, StorageUtil.DEFAULT_CLEAR);
    }

    @Override
    public void setDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder) {
        preferences.edit().putBoolean(KEY_CLEAR_CACHE_FOLDER, downloadsClearCacheFolder).apply();
    }
}
