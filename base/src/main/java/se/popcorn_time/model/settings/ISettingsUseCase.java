package se.popcorn_time.model.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import io.reactivex.Observable;

public interface ISettingsUseCase {

    @NonNull
    String[] getLanguages();

    @Nullable
    String getLanguage();

    void setLanguage(@NonNull String language);

    @NonNull
    Observable<String> getLanguageObservable();

    @NonNull
    Integer[] getStartPages();

    @NonNull
    Integer getStartPage();

    void setStartPage(@NonNull Integer startPage);

    @NonNull
    Observable<Integer> getStartPageObservable();

    @NonNull
    Integer[] getPlayerHardwareAccelerations();

    @NonNull
    Integer getPlayerHardwareAcceleration();

    void setPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration);

    @NonNull
    Observable<Integer> getPlayerHardwareAccelerationObservable();

    @NonNull
    String[] getSubtitlesLanguages();

    @NonNull
    String getSubtitlesLanguage();

    void setSubtitlesLanguage(@NonNull String subtitlesLanguage);

    @NonNull
    Observable<String> getSubtitlesLanguageObservable();

    @NonNull
    Float[] getSubtitlesFontSizes();

    @NonNull
    Float getSubtitlesFontSize();

    void setSubtitlesFontSize(@NonNull Float subtitlesFontSize);

    @NonNull
    Observable<Float> getSubtitlesFontSizeObservable();

    @NonNull
    String[] getSubtitlesFontColors();

    @NonNull
    String getSubtitlesFontColor();

    void setSubtitlesFontColor(@NonNull String subtitlesFontColor);

    @NonNull
    Observable<String> getSubtitlesFontColorObservable();

    @Nullable
    Boolean isDownloadsCheckVpn();

    void setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn);

    @NonNull
    Observable<Boolean> getDownloadsCheckVpnObservable();

    @NonNull
    Boolean isDownloadsWifiOnly();

    void setDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly);

    @NonNull
    Observable<Boolean> getDownloadsWifiOnlyObservable();

    @NonNull
    Integer getDownloadsMinConnectionsLimit();

    @NonNull
    Integer getDownloadsMaxConnectionsLimit();

    @NonNull
    Integer getDownloadsConnectionsLimit();

    void setDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit);

    @NonNull
    Observable<Integer> getDownloadsConnectionsLimitObservable();

    @NonNull
    Integer[] getDownloadsDownloadSpeeds();

    @NonNull
    Integer getDownloadsDownloadSpeed();

    void setDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed);

    @NonNull
    Observable<Integer> getDownloadsDownloadSpeedObservable();

    @NonNull
    Integer[] getDownloadsUploadSpeeds();

    @NonNull
    Integer getDownloadsUploadSpeed();

    void setDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed);

    @NonNull
    Observable<Integer> getDownloadsUploadSpeedObservable();

    @Nullable
    File getDownloadsCacheFolder();

    void setDownloadsCacheFolder(@NonNull File downloadsCacheFolder);

    @NonNull
    Observable<File> getDownloadsCacheFolderObservable();

    @NonNull
    Boolean isDownloadsClearCacheFolder();

    void setDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder);

    @NonNull
    Observable<Boolean> getDownloadsClearCacheFolderObservable();
}
