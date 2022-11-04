package se.popcorn_time.model.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public interface ISettingsRepository {

    @Nullable
    String getLanguage();

    void setLanguage(@NonNull String language);

    @NonNull
    Integer getStartPage();

    void setStartPage(@NonNull Integer startPage);

    @NonNull
    Integer getPlayerHardwareAcceleration();

    void setPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration);

    @NonNull
    String getSubtitlesLanguage();

    void setSubtitlesLanguage(@NonNull String subtitlesLanguage);

    @NonNull
    Float getSubtitlesFontSize();

    void setSubtitlesFontSize(@NonNull Float subtitlesFontSize);

    @NonNull
    String getSubtitlesFontColor();

    void setSubtitlesFontColor(@NonNull String subtitlesFontColor);

    @Nullable
    Boolean isDownloadsCheckVpn();

    void setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn);

    @NonNull
    Boolean isDownloadsWifiOnly();

    void setDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly);

    @NonNull
    Integer getDownloadsConnectionsLimit();

    void setDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit);

    @NonNull
    Integer getDownloadsDownloadSpeed();

    void setDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed);

    @NonNull
    Integer getDownloadsUploadSpeed();

    void setDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed);

    @Nullable
    File getDownloadsCacheFolder();

    void setDownloadsCacheFolder(@NonNull File downloadsCacheFolder);

    @NonNull
    Boolean isDownloadsClearCacheFolder();

    void setDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder);
}
