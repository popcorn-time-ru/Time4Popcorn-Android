package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public interface ISettingsView {

    void onLanguages(@NonNull String[] languages);

    void onLanguage(@NonNull String language);

    void onStartPages(@NonNull Integer[] startPages);

    void onStartPage(@NonNull Integer startPage);

    void onPlayerHardwareAccelerations(@NonNull Integer[] playerHardwareAccelerations);

    void onPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration);

    void onSubtitlesLanguages(@NonNull String[] subtitlesLanguages);

    void onSubtitlesLanguage(@NonNull String subtitlesLanguage);

    void onSubtitlesFontSizes(@NonNull Float[] subtitlesFontSizes);

    void onSubtitlesFontSize(@NonNull Float subtitlesFontSize);

    void onSubtitlesFontColors(@NonNull String[] subtitlesFontColors);

    void onSubtitlesFontColor(@NonNull String subtitlesFontColor);

    void onDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn, @NonNull Boolean enabled);

    void onDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly);

    void onDownloadsConnectionsLimits(@NonNull Integer minDownloadsConnectionsLimit, @NonNull Integer maxDownloadsConnectionsLimit);

    void onDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit);

    void onDownloadsDownloadSpeeds(@NonNull Integer[] downloadsDownloadSpeeds);

    void onDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed);

    void onDownloadsUploadSpeeds(@NonNull Integer[] downloadsUploadSpeeds);

    void onDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed);

    void onDownloadsCacheFolder(@Nullable File downloadsCacheFolder);

    void onDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder);

    void onAboutSite(@NonNull String site);

    void onAboutForum(@NonNull String forum);

    void onAboutVersion(@NonNull String version);
}
