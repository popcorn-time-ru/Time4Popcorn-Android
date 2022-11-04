package se.popcorn_time.model.config;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ConfigUseCase implements IConfigUseCase {

    private final Subject<Config> configSubject = PublishSubject.create();

    private IConfigLocalRepository configLocalRepository;
    private IConfigRemoteRepository configRemoteRepository;

    private Config config;

    private int index;
    private Disposable disposable;

    public ConfigUseCase(@NonNull Config config,
                         @NonNull IConfigLocalRepository configLocalRepository,
                         @NonNull IConfigRemoteRepository configRemoteRepository) {
        this.config = merge(configLocalRepository.getConfig(), config);
        this.configLocalRepository = configLocalRepository;
        this.configRemoteRepository = configRemoteRepository;
    }

    @Override
    public void getRemoteConfig() {
        index = 0;
        getRemoteConfig(config.getUrls());
    }

    @NonNull
    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setConfig(@NonNull Config config) {
        this.config = config;
        configLocalRepository.setConfig(config);
        configSubject.onNext(config);
    }

    @NonNull
    @Override
    public Observable<Config> getConfigObservable() {
        return configSubject;
    }

    private Config merge(@NonNull Config config1, @NonNull Config config2) {
        if (config1.getUrls() == null) {
            config1.setUrls(config2.getUrls());
        }
        if (config1.getUpdaterUrls() == null) {
            config1.setUpdaterUrls(config2.getUpdaterUrls());
        }
        if (config1.getShareUrls() == null) {
            config1.setShareUrls(config2.getShareUrls());
        }
        if (TextUtils.isEmpty(config1.getAnalyticsId())) {
            config1.setAnalyticsId(config2.getAnalyticsId());
        }
        if (TextUtils.isEmpty(config1.getCinemaUrl())) {
            config1.setCinemaUrl(config2.getCinemaUrl());
        }
        if (TextUtils.isEmpty(config1.getAnimeUrl())) {
            config1.setAnimeUrl(config2.getAnimeUrl());
        }
        if (TextUtils.isEmpty(config1.getPosterUrl())) {
            config1.setPosterUrl(config2.getPosterUrl());
        }
        if (TextUtils.isEmpty(config1.getSubtitlesUrl())) {
            config1.setSubtitlesUrl(config2.getSubtitlesUrl());
        }
        if (TextUtils.isEmpty(config1.getSiteUrl())) {
            config1.setSiteUrl(config2.getSiteUrl());
        }
        if (TextUtils.isEmpty(config1.getForumUrl())) {
            config1.setForumUrl(config2.getForumUrl());
        }
        if (TextUtils.isEmpty(config1.getFacebookUrl())) {
            config1.setFacebookUrl(config2.getFacebookUrl());
        }
        if (TextUtils.isEmpty(config1.getTwitterUrl())) {
            config1.setTwitterUrl(config2.getTwitterUrl());
        }
        if (TextUtils.isEmpty(config1.getYoutubeUrl())) {
            config1.setYoutubeUrl(config2.getYoutubeUrl());
        }
        if (TextUtils.isEmpty(config1.getImdbUrl())) {
            config1.setImdbUrl(config2.getImdbUrl());
        }
        if (config1.getVpnConfig() == null) {
            config1.setVpnConfig(config2.getVpnConfig());
        } else {
            config1.setVpnConfig(merge(config1.getVpnConfig(), config2.getVpnConfig()));
        }
        return config1;
    }

    private VpnConfig merge(@NonNull VpnConfig vpnConfig1, @NonNull VpnConfig vpnConfig2) {
        if (vpnConfig1.getProviders() == null) {
            vpnConfig1.setProviders(vpnConfig2.getProviders());
        }
        if (vpnConfig1.getAlert() == null) {
            vpnConfig1.setAlert(vpnConfig2.getAlert());
        }
        if (vpnConfig1.getNotice() == null) {
            vpnConfig1.setNotice(vpnConfig2.getNotice());
        }
        return vpnConfig1;
    }

    private void getRemoteConfig(@NonNull String[] urls) {
        if (urls != null && index < urls.length) {
            final String url = urls[index];
            index++;
            getRemoteConfig(url);
        }
    }

    private void getRemoteConfig(@NonNull String url) {
        if (disposable != null && !disposable.isDisposed()) {
            return;
        }
        disposable = configRemoteRepository.getConfig(url).subscribe(new Consumer<Config>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Config config) throws Exception {
                setConfig(merge(config, ConfigUseCase.this.config));
            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                throwable.printStackTrace();
                getRemoteConfig(config.getUrls());
            }
        });
    }
}
