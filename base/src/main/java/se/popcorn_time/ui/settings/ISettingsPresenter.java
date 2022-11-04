package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.IPresenter;

import java.io.File;

public interface ISettingsPresenter extends IPresenter<ISettingsView> {

    void setLanguage(@NonNull String language);

    void setStartPage(@NonNull Integer startPage);

    void setPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration);

    void setSubtitlesLanguage(@NonNull String subtitlesLanguage);

    void setSubtitlesFontSize(@NonNull Float subtitlesFontSize);

    void setSubtitlesFontColor(@NonNull String subtitlesFontColor);

    void setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn);

    void setDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly);

    void setDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit);

    void setDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed);

    void setDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed);

    void setDownloadsCacheFolder(@NonNull File downloadsCacheFolder);

    void setDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder);
}
