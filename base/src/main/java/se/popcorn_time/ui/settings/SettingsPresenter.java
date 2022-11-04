package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import se.popcorn_time.model.config.Config;
import se.popcorn_time.model.config.IConfigUseCase;
import se.popcorn_time.model.settings.ISettingsUseCase;

public final class SettingsPresenter extends Presenter<ISettingsView> implements ISettingsPresenter {

    private final IConfigUseCase configUseCase;
    private final ISettingsUseCase settingsUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private OnLanguageViewState onLanguageViewState;
    private OnStartPageViewState onStartPageViewState;
    private OnPlayerHardwareAccelerationViewState onPlayerHardwareAccelerationViewState;
    private OnSubtitlesLanguageViewState onSubtitlesLanguageViewState;
    private OnSubtitlesFontSizeViewState onSubtitlesFontSizeViewState;
    private OnSubtitlesFontColorViewState onSubtitlesFontColorViewState;
    private OnDownloadsCheckVpnViewState onDownloadsCheckVpnViewState;
    private OnDownloadsWifiOnlyViewState onDownloadsWifiOnlyViewState;
    private OnDownloadsConnectionsLimitViewState onDownloadsConnectionsLimitViewState;
    private OnDownloadsDownloadSpeedViewState onDownloadsDownloadSpeedViewState;
    private OnDownloadsUploadSpeedViewState onDownloadsUploadSpeedViewState;
    private OnDownloadsCacheFolderViewState onDownloadsCacheFolderViewState;
    private OnDownloadsClearCacheFolderViewState onDownloadsClearCacheFolderViewState;
    private OnAboutSiteViewState onAboutSiteViewState;
    private OnAboutForumViewState onAboutForumViewState;

    private final String version;

    public SettingsPresenter(@NonNull IConfigUseCase configUseCase,
                             @NonNull ISettingsUseCase settingsUseCase,
                             @NonNull String version) {
        this.configUseCase = configUseCase;
        this.settingsUseCase = settingsUseCase;
        this.version = version;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        onLanguageViewState = new OnLanguageViewState(this, settingsUseCase.getLanguage());
        onStartPageViewState = new OnStartPageViewState(this, settingsUseCase.getStartPage());
        onPlayerHardwareAccelerationViewState = new OnPlayerHardwareAccelerationViewState(this, settingsUseCase.getPlayerHardwareAcceleration());
        onSubtitlesLanguageViewState = new OnSubtitlesLanguageViewState(this, settingsUseCase.getSubtitlesLanguage());
        onSubtitlesFontSizeViewState = new OnSubtitlesFontSizeViewState(this, settingsUseCase.getSubtitlesFontSize());
        onSubtitlesFontColorViewState = new OnSubtitlesFontColorViewState(this, settingsUseCase.getSubtitlesFontColor());
        final Boolean downloadsCheckVpn = settingsUseCase.isDownloadsCheckVpn();
        onDownloadsCheckVpnViewState = new OnDownloadsCheckVpnViewState(
                this,
                downloadsCheckVpn != null ? downloadsCheckVpn : configUseCase.getConfig().getVpnConfig().isCheckVpnOptionDefault(),
                configUseCase.getConfig().getVpnConfig().isCheckVpnOptionEnabled()
        );
        onDownloadsWifiOnlyViewState = new OnDownloadsWifiOnlyViewState(this, settingsUseCase.isDownloadsWifiOnly());
        onDownloadsConnectionsLimitViewState = new OnDownloadsConnectionsLimitViewState(this, settingsUseCase.getDownloadsConnectionsLimit());
        onDownloadsDownloadSpeedViewState = new OnDownloadsDownloadSpeedViewState(this, settingsUseCase.getDownloadsDownloadSpeed());
        onDownloadsUploadSpeedViewState = new OnDownloadsUploadSpeedViewState(this, settingsUseCase.getDownloadsUploadSpeed());
        onDownloadsCacheFolderViewState = new OnDownloadsCacheFolderViewState(this, settingsUseCase.getDownloadsCacheFolder());
        onDownloadsClearCacheFolderViewState = new OnDownloadsClearCacheFolderViewState(this, settingsUseCase.isDownloadsClearCacheFolder());
        onAboutSiteViewState = new OnAboutSiteViewState(this, configUseCase.getConfig().getSiteUrl());
        onAboutForumViewState = new OnAboutForumViewState(this, configUseCase.getConfig().getForumUrl());

        disposables.add(configUseCase.getConfigObservable().subscribe(new Consumer<Config>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Config config) throws Exception {
                if (settingsUseCase.isDownloadsCheckVpn() == null) {
                    onDownloadsCheckVpnViewState.setDownloadsCheckVpn(config.getVpnConfig().isCheckVpnOptionDefault());
                }
                onDownloadsCheckVpnViewState.setCheckVpnOptionEnabled(config.getVpnConfig().isCheckVpnOptionEnabled()).apply();
                onAboutSiteViewState.apply(config.getSiteUrl());
                onAboutForumViewState.apply(config.getForumUrl());
            }
        }));

        disposables.add(settingsUseCase.getLanguageObservable().subscribe(new Consumer<String>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull String language) throws Exception {
                onLanguageViewState.onLanguage(language);
            }
        }));
        disposables.add(settingsUseCase.getStartPageObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer startPage) throws Exception {
                onStartPageViewState.onStartPage(startPage);
            }
        }));
        disposables.add(settingsUseCase.getPlayerHardwareAccelerationObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer playerHardwareAcceleration) throws Exception {
                onPlayerHardwareAccelerationViewState.apply(playerHardwareAcceleration);
            }
        }));
        disposables.add(settingsUseCase.getSubtitlesLanguageObservable().subscribe(new Consumer<String>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull String subtitlesLanguage) throws Exception {
                onSubtitlesLanguageViewState.apply(subtitlesLanguage);
            }
        }));
        disposables.add(settingsUseCase.getSubtitlesFontSizeObservable().subscribe(new Consumer<Float>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Float subtitlesFontSize) throws Exception {
                onSubtitlesFontSizeViewState.apply(subtitlesFontSize);
            }
        }));
        disposables.add(settingsUseCase.getSubtitlesFontColorObservable().subscribe(new Consumer<String>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull String subtitlesFontColor) throws Exception {
                onSubtitlesFontColorViewState.apply(subtitlesFontColor);
            }
        }));
        disposables.add(settingsUseCase.getDownloadsCheckVpnObservable().subscribe(new Consumer<Boolean>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean downloadsCheckVpn) throws Exception {
                onDownloadsCheckVpnViewState.setDownloadsCheckVpn(downloadsCheckVpn).apply();
            }
        }));
        disposables.add(settingsUseCase.getDownloadsWifiOnlyObservable().subscribe(new Consumer<Boolean>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean downloadsWifiOnly) throws Exception {
                onDownloadsWifiOnlyViewState.apply(downloadsWifiOnly);
            }
        }));
        disposables.add(settingsUseCase.getDownloadsConnectionsLimitObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer downloadsConnectionsLimit) throws Exception {
                onDownloadsConnectionsLimitViewState.apply(downloadsConnectionsLimit);
            }
        }));
        disposables.add(settingsUseCase.getDownloadsDownloadSpeedObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer downloadsDownloadSpeed) throws Exception {
                onDownloadsDownloadSpeedViewState.apply(downloadsDownloadSpeed);
            }
        }));
        disposables.add(settingsUseCase.getDownloadsUploadSpeedObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer downloadsUploadSpeed) throws Exception {
                onDownloadsUploadSpeedViewState.apply(downloadsUploadSpeed);
            }
        }));
        disposables.add(settingsUseCase.getDownloadsCacheFolderObservable().subscribe(new Consumer<File>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull File downloadsCacheFolder) throws Exception {
                onDownloadsCacheFolderViewState.apply(downloadsCacheFolder);
            }
        }));
        disposables.add(settingsUseCase.getDownloadsClearCacheFolderObservable().subscribe(new Consumer<Boolean>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean downloadsClearCacheFolder) throws Exception {
                onDownloadsClearCacheFolderViewState.apply(downloadsClearCacheFolder);
            }
        }));
    }

    @Override
    protected void onAttach(@NonNull ISettingsView view) {
        super.onAttach(view);
        view.onLanguages(settingsUseCase.getLanguages());
        view.onStartPages(settingsUseCase.getStartPages());
        view.onPlayerHardwareAccelerations(settingsUseCase.getPlayerHardwareAccelerations());
        view.onSubtitlesLanguages(settingsUseCase.getSubtitlesLanguages());
        view.onSubtitlesFontSizes(settingsUseCase.getSubtitlesFontSizes());
        view.onSubtitlesFontColors(settingsUseCase.getSubtitlesFontColors());
        view.onDownloadsConnectionsLimits(settingsUseCase.getDownloadsMinConnectionsLimit(), settingsUseCase.getDownloadsMaxConnectionsLimit());
        view.onDownloadsDownloadSpeeds(settingsUseCase.getDownloadsDownloadSpeeds());
        view.onDownloadsUploadSpeeds(settingsUseCase.getDownloadsUploadSpeeds());
        view.onAboutVersion(version);
        onLanguageViewState.apply(view);
        onStartPageViewState.apply(view);
        onPlayerHardwareAccelerationViewState.apply(view);
        onSubtitlesLanguageViewState.apply(view);
        onSubtitlesFontSizeViewState.apply(view);
        onSubtitlesFontColorViewState.apply(view);
        onDownloadsCheckVpnViewState.apply(view);
        onDownloadsWifiOnlyViewState.apply(view);
        onDownloadsConnectionsLimitViewState.apply(view);
        onDownloadsDownloadSpeedViewState.apply(view);
        onDownloadsUploadSpeedViewState.apply(view);
        onDownloadsCacheFolderViewState.apply(view);
        onDownloadsClearCacheFolderViewState.apply(view);
        onAboutSiteViewState.apply(view);
        onAboutForumViewState.apply(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
        onLanguageViewState = null;
        onStartPageViewState = null;
        onPlayerHardwareAccelerationViewState = null;
        onSubtitlesLanguageViewState = null;
        onSubtitlesFontSizeViewState = null;
        onSubtitlesFontColorViewState = null;
        onDownloadsCheckVpnViewState = null;
        onDownloadsWifiOnlyViewState = null;
        onDownloadsConnectionsLimitViewState = null;
        onDownloadsDownloadSpeedViewState = null;
        onDownloadsUploadSpeedViewState = null;
        onDownloadsCacheFolderViewState = null;
        onDownloadsClearCacheFolderViewState = null;
        onAboutSiteViewState = null;
        onAboutForumViewState = null;
    }

    @Override
    public void setLanguage(@NonNull String language) {
        settingsUseCase.setLanguage(language);
    }

    @Override
    public void setStartPage(@NonNull Integer startPage) {
        settingsUseCase.setStartPage(startPage);
    }

    @Override
    public void setPlayerHardwareAcceleration(@NonNull Integer playerHardwareAcceleration) {
        settingsUseCase.setPlayerHardwareAcceleration(playerHardwareAcceleration);
    }

    @Override
    public void setSubtitlesLanguage(@NonNull String subtitlesLanguage) {
        settingsUseCase.setSubtitlesLanguage(subtitlesLanguage);
    }

    @Override
    public void setSubtitlesFontSize(@NonNull Float subtitlesFontSize) {
        settingsUseCase.setSubtitlesFontSize(subtitlesFontSize);
    }

    @Override
    public void setSubtitlesFontColor(@NonNull String subtitlesFontColor) {
        settingsUseCase.setSubtitlesFontColor(subtitlesFontColor);
    }

    @Override
    public void setDownloadsCheckVpn(@NonNull Boolean downloadsCheckVpn) {
        settingsUseCase.setDownloadsCheckVpn(downloadsCheckVpn);
    }

    @Override
    public void setDownloadsWifiOnly(@NonNull Boolean downloadsWifiOnly) {
        settingsUseCase.setDownloadsWifiOnly(downloadsWifiOnly);
    }

    @Override
    public void setDownloadsConnectionsLimit(@NonNull Integer downloadsConnectionsLimit) {
        settingsUseCase.setDownloadsConnectionsLimit(downloadsConnectionsLimit);
    }

    @Override
    public void setDownloadsDownloadSpeed(@NonNull Integer downloadsDownloadSpeed) {
        settingsUseCase.setDownloadsDownloadSpeed(downloadsDownloadSpeed);
    }

    @Override
    public void setDownloadsUploadSpeed(@NonNull Integer downloadsUploadSpeed) {
        settingsUseCase.setDownloadsUploadSpeed(downloadsUploadSpeed);
    }

    @Override
    public void setDownloadsCacheFolder(@NonNull File downloadsCacheFolder) {
        settingsUseCase.setDownloadsCacheFolder(downloadsCacheFolder);
    }

    @Override
    public void setDownloadsClearCacheFolder(@NonNull Boolean downloadsClearCacheFolder) {
        settingsUseCase.setDownloadsClearCacheFolder(downloadsClearCacheFolder);
    }
}
