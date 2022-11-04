package se.popcorn_time.model.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public final class SettingsUseCase implements ISettingsUseCase {

    private final Subject<String> languageSubject = PublishSubject.create();
    private final Subject<Integer> startPageSubject = PublishSubject.create();
    private final Subject<Integer> playerHardwareAccelerationSubject = PublishSubject.create();
    private final Subject<String> subtitlesLanguageSubject = PublishSubject.create();
    private final Subject<Float> subtitlesFontSizeSubject = PublishSubject.create();
    private final Subject<String> subtitlesFontColorSubject = PublishSubject.create();
    private final Subject<Boolean> downloadsCheckVpnSubject = PublishSubject.create();
    private final Subject<Boolean> downloadsWifiOnlySubject = PublishSubject.create();
    private final Subject<Integer> downloadsConnectionsLimitSubject = PublishSubject.create();
    private final Subject<Integer> downloadsDownloadSpeedSubject = PublishSubject.create();
    private final Subject<Integer> downloadsUploadSpeedSubject = PublishSubject.create();
    private final Subject<File> downloadsCacheFolderSubject = PublishSubject.create();
    private final Subject<Boolean> downloadsClearCacheFolderSubject = PublishSubject.create();

    private final String[] languages;
    private final Integer[] startPages;
    private final Integer[] playerHardwareAccelerations;
    private final String[] subtitlesLanguages;
    private final Float[] subtitlesFontSizes;
    private final String[] subtitlesFontColors;
    private final Integer downloadsMinConnectionsLimit;
    private final Integer downloadsMaxConnectionsLimit;
    private final Integer[] downloadsDownloadSpeeds;
    private final Integer[] downloadsUploadSpeeds;
    private final ISettingsRepository repository;

    private String language;
    private Integer startPage;
    private Integer playerHardwareAcceleration;
    private String subtitlesLanguage;
    private Float subtitlesFontSize;
    private String subtitlesFontColor;
    private Boolean downloadsCheckVpn;
    private Boolean downloadsWifiOnly;
    private Integer downloadsConnectionsLimit;
    private Integer downloadsDownloadSpeed;
    private Integer downloadsUploadSpeed;
    private File downloadsCacheFolder;
    private Boolean downloadsClearCacheFolder;

    public SettingsUseCase(@NonNull String[] languages,
                           @NonNull Integer[] startPages,
                           @NonNull Integer[] playerHardwareAccelerations,
                           @NonNull String[] subtitlesLanguages,
                           @NonNull Float[] subtitlesFontSizes,
                           @NonNull String[] subtitlesFontColors,
                           @NonNull Integer downloadsMinConnectionsLimit,
                           @NonNull Integer downloadsMaxConnectionsLimit,
                           @NonNull Integer[] downloadsDownloadSpeeds,
                           @NonNull Integer[] downloadsUploadSpeeds,
                           @NonNull ISettingsRepository repository) {
        this.languages = languages;
        this.startPages = startPages;
        this.playerHardwareAccelerations = playerHardwareAccelerations;
        this.subtitlesLanguages = subtitlesLanguages;
        this.subtitlesFontSizes = subtitlesFontSizes;
        this.subtitlesFontColors = subtitlesFontColors;
        this.downloadsMinConnectionsLimit = downloadsMinConnectionsLimit;
        this.downloadsMaxConnectionsLimit = downloadsMaxConnectionsLimit;
        this.downloadsDownloadSpeeds = downloadsDownloadSpeeds;
        this.downloadsUploadSpeeds = downloadsUploadSpeeds;
        this.repository = repository;
        this.language = repository.getLanguage();
        this.startPage = repository.getStartPage();
        this.playerHardwareAcceleration = repository.getPlayerHardwareAcceleration();
        this.subtitlesLanguage = repository.getSubtitlesLanguage();
        this.subtitlesFontSize = repository.getSubtitlesFontSize();
        this.subtitlesFontColor = repository.getSubtitlesFontColor();
        this.downloadsCheckVpn = repository.isDownloadsCheckVpn();
        this.downloadsWifiOnly = repository.isDownloadsWifiOnly();
        this.downloadsConnectionsLimit = repository.getDownloadsConnectionsLimit();
        this.downloadsDownloadSpeed = repository.getDownloadsDownloadSpeed();
        this.downloadsUploadSpeed = repository.getDownloadsUploadSpeed();
        this.downloadsCacheFolder = repository.getDownloadsCacheFolder();
        this.downloadsClearCacheFolder = repository.isDownloadsClearCacheFolder();
    }

    @NonNull
    @Override
    public String[] getLanguages() {
        return languages;
    }

    @Nullable
    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(@NonNull String language) {
        this.language = language;
        repository.setLanguage(language);
        languageSubject.onNext(language);
    }

    @NonNull
    @Override
    public Observable<String> getLanguageObservable() {
        return languageSubject;
    }

    @NonNull
    @Override
    public Integer[] getStartPages() {
        return startPages;
    }

    @NonNull
    @Override
    public Integer getStartPage() {
        return startPage;
    }

    @Override
    public void setStartPage(@NonNull Integer startPage) {
        this.startPage = startPage;
        repository.setStartPage(startPage);
        startPageSubject.onNext(startPage);
    }

    @NonNull
    @Override
    public Observable<Integer> getStartPageObservable() {
        return startPageSubject;
    }

    @NonNull
    @Override
    public Integer[] getPlayerHardwareAccelerations() {
        return playerHardwareAccelerations;
    }

    @NonNull
    @Override
    public Integer getPlayerHardwareAcceleration() {
        return playerHardwareAcceleration;
    }

    @Override
    public void setPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration) {
        this.playerHardwareAcceleration = playerHardwareAcceleration;
        repository.setPlayerHardwareAcceleration(playerHardwareAcceleration);
        playerHardwareAccelerationSubject.onNext(playerHardwareAcceleration);
    }

    @NonNull
    @Override
    public Observable<Integer> getPlayerHardwareAccelerationObservable() {
        return playerHardwareAccelerationSubject;
    }

    @NonNull
    @Override
    public String[] getSubtitlesLanguages() {
        return subtitlesLanguages;
    }

    @NonNull
    @Override
    public String getSubtitlesLanguage() {
        return subtitlesLanguage;
    }

    @Override
    public void setSubtitlesLanguage(@NonNull String subtitlesLanguage) {
        this.subtitlesLanguage = subtitlesLanguage;
        repository.setSubtitlesLanguage(subtitlesLanguage);
        subtitlesLanguageSubject.onNext(subtitlesLanguage);
    }

    @NonNull
    @Override
    public Observable<String> getSubtitlesLanguageObservable() {
        return subtitlesLanguageSubject;
    }

    @NonNull
    @Override
    public Float[] getSubtitlesFontSizes() {
        return subtitlesFontSizes;
    }

    @NonNull
    @Override
    public Float getSubtitlesFontSize() {
        return subtitlesFontSize;
    }

    @Override
    public void setSubtitlesFontSize(@NonNull Float subtitlesFontSize) {
        this.subtitlesFontSize = subtitlesFontSize;
        repository.setSubtitlesFontSize(subtitlesFontSize);
        subtitlesFontSizeSubject.onNext(subtitlesFontSize);
    }

    @NonNull
    @Override
    public Observable<Float> getSubtitlesFontSizeObservable() {
        return subtitlesFontSizeSubject;
    }

    @NonNull
    @Override
    public String[] getSubtitlesFontColors() {
        return subtitlesFontColors;
    }

    @NonNull
    @Override
    public String getSubtitlesFontColor() {
        return subtitlesFontColor;
    }

    @Override
    public void setSubtitlesFontColor(@NonNull String subtitlesFontColor) {
        this.subtitlesFontColor = subtitlesFontColor;
        repository.setSubtitlesFontColor(subtitlesFontColor);
        subtitlesFontColorSubject.onNext(subtitlesFontColor);
    }

    @NonNull
    @Override
    public Observable<String> getSubtitlesFontColorObservable() {
        return subtitlesFontColorSubject;
    }

    @Nullable
    @Override
    public Boolean isDownloadsCheckVpn() {
        return downloadsCheckVpn;
    }

    @Override
    public void setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn) {
        this.downloadsCheckVpn = downloadsCheckVpn;
        repository.setDownloadsCheckVpn(downloadsCheckVpn);
        downloadsCheckVpnSubject.onNext(downloadsCheckVpn);
    }

    @NonNull
    @Override
    public Observable<Boolean> getDownloadsCheckVpnObservable() {
        return downloadsCheckVpnSubject;
    }

    @NonNull
    @Override
    public Boolean isDownloadsWifiOnly() {
        return downloadsWifiOnly;
    }

    @Override
    public void setDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly) {
        this.downloadsWifiOnly = downloadsWifiOnly;
        repository.setDownloadsWifiOnly(downloadsWifiOnly);
        downloadsWifiOnlySubject.onNext(downloadsWifiOnly);
    }

    @NonNull
    @Override
    public Observable<Boolean> getDownloadsWifiOnlyObservable() {
        return downloadsWifiOnlySubject;
    }

    @NonNull
    @Override
    public Integer getDownloadsMinConnectionsLimit() {
        return downloadsMinConnectionsLimit;
    }

    @NonNull
    @Override
    public Integer getDownloadsMaxConnectionsLimit() {
        return downloadsMaxConnectionsLimit;
    }

    @NonNull
    @Override
    public Integer getDownloadsConnectionsLimit() {
        return downloadsConnectionsLimit;
    }

    @Override
    public void setDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit) {
        this.downloadsConnectionsLimit = downloadsConnectionsLimit;
        repository.setDownloadsConnectionsLimit(downloadsConnectionsLimit);
        downloadsConnectionsLimitSubject.onNext(downloadsConnectionsLimit);
    }

    @NonNull
    @Override
    public Observable<Integer> getDownloadsConnectionsLimitObservable() {
        return downloadsConnectionsLimitSubject;
    }

    @NonNull
    @Override
    public Integer[] getDownloadsDownloadSpeeds() {
        return downloadsDownloadSpeeds;
    }

    @NonNull
    @Override
    public Integer getDownloadsDownloadSpeed() {
        return downloadsDownloadSpeed;
    }

    @Override
    public void setDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed) {
        this.downloadsDownloadSpeed = downloadsDownloadSpeed;
        repository.setDownloadsDownloadSpeed(downloadsDownloadSpeed);
        downloadsDownloadSpeedSubject.onNext(downloadsDownloadSpeed);
    }

    @NonNull
    @Override
    public Observable<Integer> getDownloadsDownloadSpeedObservable() {
        return downloadsDownloadSpeedSubject;
    }

    @NonNull
    @Override
    public Integer[] getDownloadsUploadSpeeds() {
        return downloadsUploadSpeeds;
    }

    @NonNull
    @Override
    public Integer getDownloadsUploadSpeed() {
        return downloadsUploadSpeed;
    }

    @Override
    public void setDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed) {
        this.downloadsUploadSpeed = downloadsUploadSpeed;
        repository.setDownloadsUploadSpeed(downloadsUploadSpeed);
        downloadsUploadSpeedSubject.onNext(downloadsUploadSpeed);
    }

    @NonNull
    @Override
    public Observable<Integer> getDownloadsUploadSpeedObservable() {
        return downloadsUploadSpeedSubject;
    }

    @Nullable
    @Override
    public File getDownloadsCacheFolder() {
        return downloadsCacheFolder;
    }

    @Override
    public void setDownloadsCacheFolder(@NonNull File downloadsCacheFolder) {
        this.downloadsCacheFolder = downloadsCacheFolder;
        repository.setDownloadsCacheFolder(downloadsCacheFolder);
        downloadsCacheFolderSubject.onNext(downloadsCacheFolder);
    }

    @NonNull
    @Override
    public Observable<File> getDownloadsCacheFolderObservable() {
        return downloadsCacheFolderSubject;
    }

    @NonNull
    @Override
    public Boolean isDownloadsClearCacheFolder() {
        return downloadsClearCacheFolder;
    }

    @Override
    public void setDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder) {
        this.downloadsClearCacheFolder = downloadsClearCacheFolder;
        repository.setDownloadsClearCacheFolder(downloadsClearCacheFolder);
        downloadsClearCacheFolderSubject.onNext(downloadsClearCacheFolder);
    }

    @NonNull
    @Override
    public Observable<Boolean> getDownloadsClearCacheFolderObservable() {
        return downloadsClearCacheFolderSubject;
    }
}
