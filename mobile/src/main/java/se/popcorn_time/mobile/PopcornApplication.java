package se.popcorn_time.mobile;

import android.app.Application;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.WebView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.analytics.Analytics;
import se.popcorn_time.base.api.AppApi;
import se.popcorn_time.base.model.video.Anime;
import se.popcorn_time.base.model.video.Cinema;
import se.popcorn_time.base.model.video.info.AnimeMoviesInfo;
import se.popcorn_time.base.model.video.info.AnimeTvShowsInfo;
import se.popcorn_time.base.model.video.info.CinemaMoviesInfo;
import se.popcorn_time.base.model.video.info.CinemaTvShowsInfo;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.base.prefs.Prefs;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.subtitles.SubtitlesFontColor;
import se.popcorn_time.base.subtitles.SubtitlesFontSize;
import se.popcorn_time.base.subtitles.SubtitlesLanguage;
import se.popcorn_time.base.torrent.TorrentService;
import se.popcorn_time.base.torrent.client.BaseClient;
import se.popcorn_time.base.utils.DeviceUtils;
import se.popcorn_time.base.utils.InterfaceUtil;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.base.utils.NetworkUtil;
import se.popcorn_time.mobile.model.content.ContentProviderView;
import se.popcorn_time.mobile.model.content.ContentRepository;
import se.popcorn_time.mobile.model.content.ContentType;
import se.popcorn_time.mobile.model.content.MoviesProvider;
import se.popcorn_time.mobile.model.content.SubtitlesProvider;
import se.popcorn_time.mobile.model.content.SubtitlesRepository;
import se.popcorn_time.mobile.model.content.TmdbProvider;
import se.popcorn_time.mobile.model.content.TvShowsProvider;
import se.popcorn_time.mobile.model.content.TvShowsSeasonsProvider;
import se.popcorn_time.mobile.model.filter.FilterItemView;
import se.popcorn_time.mobile.model.filter.FilterView;
import se.popcorn_time.mobile.model.settings.SettingsRepository;
import se.popcorn_time.mobile.ui.SettingsActivity;
import se.popcorn_time.mobile.ui.VpnActivity;
import se.popcorn_time.model.ChoiceProperty;
import se.popcorn_time.model.Property;
import se.popcorn_time.model.config.Config;
import se.popcorn_time.model.config.ConfigLocalRepository;
import se.popcorn_time.model.config.ConfigRemoteRepository;
import se.popcorn_time.model.config.ConfigUseCase;
import se.popcorn_time.model.config.IConfigUseCase;
import se.popcorn_time.model.config.StaticConfig;
import se.popcorn_time.model.config.VpnConfig;
import se.popcorn_time.model.content.ContentUseCase;
import se.popcorn_time.model.content.IContentProvider;
import se.popcorn_time.model.content.IContentUseCase;
import se.popcorn_time.model.content.IDetailsProvider;
import se.popcorn_time.model.content.ISubtitlesProvider;
import se.popcorn_time.model.details.DetailsUseCase;
import se.popcorn_time.model.details.IDetailsUseCase;
import se.popcorn_time.model.filter.FilterItem;
import se.popcorn_time.model.filter.IFilter;
import se.popcorn_time.model.messaging.IMessagingData;
import se.popcorn_time.model.messaging.IMessagingNotificationData;
import se.popcorn_time.model.messaging.IMessagingUseCase;
import se.popcorn_time.model.messaging.MessagingUseCase;
import se.popcorn_time.model.messaging.MessagingUtils;
import se.popcorn_time.model.settings.ISettingsUseCase;
import se.popcorn_time.model.settings.SettingsUseCase;
import se.popcorn_time.model.share.IShareUseCase;
import se.popcorn_time.model.share.ShareRemoteRepository;
import se.popcorn_time.model.share.ShareUseCase;
import se.popcorn_time.model.subtitles.Subtitles;
import se.popcorn_time.model.updater.IUpdaterRepository;
import se.popcorn_time.model.updater.IUpdaterUseCase;
import se.popcorn_time.model.updater.Update;
import se.popcorn_time.model.updater.UpdaterUseCase;
import se.popcorn_time.model.vpn.IVpnUseCase;
import se.popcorn_time.model.vpn.VpnUseCase;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.IBrowserView;
import se.popcorn_time.ui.ISystemShareView;
import se.popcorn_time.ui.locale.ILocalePresenter;
import se.popcorn_time.ui.locale.LocalePresenter;
import se.popcorn_time.ui.settings.ISettingsView;
import se.popcorn_time.ui.updater.IUpdateView;
import se.popcorn_time.ui.vpn.IVpnView;

public final class PopcornApplication extends Application implements IPopcornApplication,
        IViewRouter,
        IUseCaseManager,
        IMessagingUseCase.NotificationObserver {

    private static boolean fullVersion;

    public static boolean isFullVersion() {
        return fullVersion;
    }

    public static final String ACTION_NOTIFICATION = "se.popcorn_time.mobile.ACTION_NOTIFICATION";
    public static final String EXTRA_ACTION = "action";

    private static final String[] LANGUAGES = new String[]{
            Language.CODE_ENGLISH,
            Language.CODE_SPANISH,
            Language.CODE_DUTCH,
            Language.CODE_BRAZILIAN_PORTUGUESE,
            Language.CODE_RUSSIAN,
            Language.CODE_TURKISH,
            Language.CODE_ITALIAN
    };

    private static final Integer[] START_PAGES = new Integer[]{
            StartPage.PAGE_CINEMA_MOVIES,
            StartPage.PAGE_CINEMA_TV_SHOWS,
            StartPage.PAGE_ANIME_MOVIES,
            StartPage.PAGE_ANIME_TV_SHOWS
    };

    private static final Integer[] PLAYER_HARDWARE_ACCELERATIONS = new Integer[]{
            PlayerHardwareAcceleration.AUTOMATIC,
            PlayerHardwareAcceleration.DISABLED,
            PlayerHardwareAcceleration.DECODING,
            PlayerHardwareAcceleration.FULL
    };

    private static final Float[] SUBTITLES_FONT_SIZES = new Float[]{
            SubtitlesFontSize.EXTRA_SMALL,
            SubtitlesFontSize.SMALL,
            SubtitlesFontSize.NORMAL,
            SubtitlesFontSize.LARGE,
            SubtitlesFontSize.EXTRA_LARGE
    };

    private static final String[] SUBTITLES_FONT_COLORS = new String[]{
            SubtitlesFontColor.WHITE,
            SubtitlesFontColor.YELLOW
    };

    private final Integer[] SPEEDS = new Integer[]{
            0,
            TorrentService.MINIMUM_SPEED,
            2 * TorrentService.MINIMUM_SPEED,
            5 * TorrentService.MINIMUM_SPEED,
            10 * TorrentService.MINIMUM_SPEED,
            20 * TorrentService.MINIMUM_SPEED,
            50 * TorrentService.MINIMUM_SPEED,
            100 * TorrentService.MINIMUM_SPEED
    };

    private IShareUseCase shareUseCase;
    private IMessagingUseCase firebaseMessagingUseCase;
    private IVpnUseCase vpnUseCase;
    private IConfigUseCase configUseCase;
    private ISettingsUseCase settingsUseCase;
    private IUpdaterUseCase updaterUseCase;
    private IContentUseCase contentUseCase;

    private IDetailsUseCase detailsUseCase;

    private LocalePresenter localePresenter;

    private BaseClient baseClient;
    private Update update;

    private IViewRouter activeViewRouter;

    private Disposable detailsDisposable;
    private Disposable subtitlesDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getBaseContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Picasso.setSingletonInstance(new Picasso.Builder(PopcornApplication.this).downloader(new OkHttp3Downloader(PopcornApplication.this)).build());

        if (BuildConfig.DEBUG) {
            Logger.init("pt_mobile");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        if (!TextUtils.isEmpty(BuildConfig.FIREBASE_DEV_TOPIC)) {
            if ("dev".equals(BuildConfig.BUILD_TYPE)) {
                FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.FIREBASE_DEV_TOPIC);
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(BuildConfig.FIREBASE_DEV_TOPIC);
            }
        }

        Prefs.init(getBaseContext());
        fullVersion = Prefs.getPopcornPrefs().isFullVersion(BuildConfig.IS_APP_FULL);

        final SharedPreferences popcornPreferences = getSharedPreferences("PopcornPreferences", Context.MODE_PRIVATE);

        final String os = "ANDROID" + android.os.Build.VERSION.RELEASE.replace(".", "");
        final String version = String.format(Locale.ENGLISH, "%s.%d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        final String hardwareId = DeviceUtils.getDeviceId(PopcornApplication.this);

        baseClient = new BaseClient(PopcornApplication.this);
        baseClient.bind();

        shareUseCase = new ShareUseCase(new ShareRemoteRepository(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
        firebaseMessagingUseCase = new MessagingUseCase();
        firebaseMessagingUseCase.subscribe(PopcornApplication.this);

        final Config config = new Config();
        config.setUrls(BuildConfig.CONFIG_DOMAINS);
        config.setUpdaterUrls(BuildConfig.UPDATER_DOMAINS);
        config.setShareUrls(BuildConfig.SHARE_DOMAINS);
        config.setAnalyticsId(BuildConfig.ANALYTICS_TRACKER_ID);
        config.setCinemaUrl(BuildConfig.API_DOMAIN);
        config.setAnimeUrl(BuildConfig.API_ANIME_DOMAIN);
        config.setPosterUrl(BuildConfig.POSTER_DOMAIN);
        config.setSubtitlesUrl(BuildConfig.SUBTITLE_DOMAIN);
        config.setSiteUrl(BuildConfig.APP_SITE);
        config.setForumUrl(BuildConfig.APP_FORUM);
        config.setFacebookUrl(BuildConfig.APP_FACEBOOK);
        config.setTwitterUrl(BuildConfig.APP_TWITTER);
        config.setYoutubeUrl(BuildConfig.APP_YOUTUBE);
        config.setImdbUrl(BuildConfig.IMDB_URL);
        final VpnConfig vpnConfig = new VpnConfig();
        vpnConfig.setProviders(BuildConfig.VPN_PROVIDERS);
        vpnConfig.setAlert(new VpnConfig.Alert(
                "<b>YOU'RE BEING MONITORED!</b>",
                new VpnConfig.Alert.Text[]{
                        new VpnConfig.Alert.Text("<b>Streaming without a secure VPN connection will expose your identity and can get you in trouble</b>", 2),
                        new VpnConfig.Alert.Text("Please take 1 minute to activate the <b>built-in unlimited VPN</b> connection before streaming", 2)
                }
        ));
        vpnConfig.setNotice(new VpnConfig.Notice(getString(R.string.security_recommendation), getString(R.string.security_recommendation_msg)));
        config.setVpnConfig(vpnConfig);
        configUseCase = new ConfigUseCase(
                config,
                new ConfigLocalRepository(popcornPreferences),
                new ConfigRemoteRepository()
        );
        StaticConfig.APP_ID = BuildConfig.APP_ID;
        StaticConfig.setConfig(configUseCase.getConfig());

        final String[] SUBTITLES_LANGUAGES = getResources().getStringArray(se.popcorn_time.base.R.array.subtitles_name);

        settingsUseCase = new SettingsUseCase(
                LANGUAGES,
                START_PAGES,
                PLAYER_HARDWARE_ACCELERATIONS,
                SUBTITLES_LANGUAGES,
                SUBTITLES_FONT_SIZES,
                SUBTITLES_FONT_COLORS,
                TorrentService.MIN_CONNECTIONS_LIMIT,
                TorrentService.MAX_CONNECTIONS_LIMIT,
                SPEEDS,
                SPEEDS,
                new SettingsRepository(popcornPreferences)
        );

        vpnUseCase = new VpnUseCase();

        final File updateDir = getExternalFilesDir(null);
        updaterUseCase = new UpdaterUseCase(
                updateDir != null ? updateDir : getCacheDir(),
                (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE),
                new IUpdaterRepository() {

                    @NonNull
                    @Override
                    public Observable<Update> getUpdate(@NonNull String url) {
                        return Observable.just(new Update());
                    }
                }
        );

        final IFilter sortByFilter = new FilterView.Builder(R.string.sort_by, R.drawable.ic_sort, ContentRepository.KEY_SORT_BY, true)
                .add(new FilterItemView(R.string.popularity, new FilterItem(ContentRepository.SORT_BY_POPULARITY)), true)
                .add(new FilterItemView(R.string.date_added, new FilterItem(ContentRepository.SORT_BY_DATE_ADDED)), false)
                .add(new FilterItemView(R.string.year, new FilterItem(ContentRepository.SORT_BY_YEAR)), false)
                .create();

        final IFilter qualityFilter = new FilterView.Builder(R.string.quality, R.drawable.ic_quality, ContentRepository.KEY_QUALITY, false)
                .add(new FilterItem(ContentRepository.QUALITY_480p), true)
                .add(new FilterItem(ContentRepository.QUALITY_720p), true)
                .add(new FilterItem(ContentRepository.QUALITY_1080p), true)
                .create();

        final ContentRepository cinemaContentRepository = new ContentRepository(
                "ANDROID",
                StaticConfig.APP_ID,
                BuildConfig.VERSION_NAME,
                configUseCase.getConfig().getCinemaUrl()
        );

        final TmdbProvider tmdbProvider = new TmdbProvider(configUseCase.getConfig().getPosterUrl(), "");
        final SubtitlesRepository cinemaSubtitlesRepository = new SubtitlesRepository(configUseCase.getConfig().getSubtitlesUrl());

        final ContentRepository animeContentRepository = new ContentRepository(
                "ANDROID",
                StaticConfig.APP_ID,
                BuildConfig.VERSION_NAME,
                configUseCase.getConfig().getAnimeUrl()
        );

        final IContentProvider[] contentProviders = new IContentProvider[]{
                new ContentProviderView(
                        R.drawable.ic_cinema,
                        R.string.cinema,
                        R.string.movies,
                        new MoviesProvider<>(
                                new ContentType<>(Cinema.TYPE_MOVIES, new TypeToken<List<CinemaMoviesInfo>>() {}),
                                cinemaContentRepository,
                                new IFilter[]{createCinemaFilter(), sortByFilter, qualityFilter},
                                new IDetailsProvider[]{tmdbProvider},
                                new SubtitlesProvider(cinemaSubtitlesRepository)
                        )
                ),
                new ContentProviderView(
                        R.drawable.ic_cinema,
                        R.string.cinema,
                        R.string.tv_shows,
                        new TvShowsProvider<>(
                                new ContentType<>(Cinema.TYPE_TV_SHOWS, new TypeToken<List<CinemaTvShowsInfo>>() {}),
                                cinemaContentRepository,
                                new IFilter[]{createCinemaFilter(), sortByFilter, qualityFilter},
                                new IDetailsProvider[]{
                                        tmdbProvider,
                                        new TvShowsSeasonsProvider(cinemaContentRepository, qualityFilter)
                                },
                                new SubtitlesProvider(cinemaSubtitlesRepository)
                        )
                ),
                new ContentProviderView(
                        R.drawable.ic_anime,
                        R.string.anime,
                        R.string.movies,
                        new MoviesProvider<>(
                                new ContentType<>(Anime.TYPE_MOVIES, new TypeToken<List<AnimeMoviesInfo>>() {}),
                                animeContentRepository,
                                new IFilter[]{sortByFilter, qualityFilter},
                                null,
                                null
                        )
                ),
                new ContentProviderView(
                        R.drawable.ic_anime,
                        R.string.anime,
                        R.string.tv_shows,
                        new TvShowsProvider<>(
                                new ContentType<>(Anime.TYPE_TV_SHOWS, new TypeToken<List<AnimeTvShowsInfo>>() {}),
                                animeContentRepository,
                                new IFilter[]{sortByFilter, qualityFilter},
                                new IDetailsProvider[]{new TvShowsSeasonsProvider(animeContentRepository, qualityFilter)},
                                null
                        )
                )
        };
        contentUseCase = new ContentUseCase(contentProviders, initContentProvider(contentProviders));

        detailsUseCase = new DetailsUseCase();

        localePresenter = new LocalePresenter(settingsUseCase);

        shareUseCase.setUrls(configUseCase.getConfig().getShareUrls());

        if (!BuildConfig.DEBUG) {
            Analytics.init(getBaseContext(), configUseCase.getConfig().getAnalyticsId(), false);
            if (NetworkUtil.hasAvailableConnection(PopcornApplication.this)) {
                updaterUseCase.getUpdate(configUseCase.getConfig().getUpdaterUrls()).subscribe(new Consumer<Update>() {

                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Update update) throws Exception {
                        if (getActiveViewRouter() == null || !getActiveViewRouter().onShowView(IUpdateView.class, update)) {
                            PopcornApplication.this.update = update;
                        }
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
            }
        }
        InterfaceUtil.init(LANGUAGES, settingsUseCase);
        SubtitlesLanguage.init(getResources(), SUBTITLES_LANGUAGES, settingsUseCase);
        updateConfigurationLocale();
        StorageUtil.init(getBaseContext(), settingsUseCase);
        AppApi.start(PopcornApplication.this);
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                IMessagingData.Action action = intent.getParcelableExtra(EXTRA_ACTION);
                if (action != null) {
                    MessagingUtils.action(context, action);
                }
            }
        }, new IntentFilter(ACTION_NOTIFICATION));

        configUseCase.getConfigObservable().subscribe(new Consumer<Config>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Config config) throws Exception {
                Logger.debug("PopcornApplication<Config>: " + config);
                shareUseCase.setUrls(config.getShareUrls());
                StaticConfig.setConfig(config);
            }
        });
        configUseCase.getRemoteConfig();

        settingsUseCase.getLanguageObservable().subscribe(new Consumer<String>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull String language) throws Exception {
                InterfaceUtil.changeAppLocale(language);
                updateConfigurationLocale();
            }
        });
        settingsUseCase.getDownloadsConnectionsLimitObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer downloadsConnectionsLimit) throws Exception {
                baseClient.setConnectionsLimit(downloadsConnectionsLimit);
            }
        });
        settingsUseCase.getDownloadsDownloadSpeedObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer downloadsDownloadSpeed) throws Exception {
                baseClient.setMaximumDownloadSpeed(downloadsDownloadSpeed);
            }
        });
        settingsUseCase.getDownloadsUploadSpeedObservable().subscribe(new Consumer<Integer>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer downloadsUploadSpeed) throws Exception {
                baseClient.setMaximumUploadSpeed(downloadsUploadSpeed);
            }
        });
        settingsUseCase.getDownloadsCacheFolderObservable().subscribe(new Consumer<File>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull File downloadsCacheFolder) throws Exception {
                StorageUtil.setRootDir(downloadsCacheFolder);
            }
        });

        detailsUseCase.getVideoInfoProperty().getObservable().subscribe(new Consumer<Property<VideoInfo>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Property<VideoInfo> videoInfoProperty) throws Exception {
                final VideoInfo videoInfo = videoInfoProperty.getValue();
                if (videoInfo != null) {
                    if (detailsDisposable == null || detailsDisposable.isDisposed()) {
                        final IDetailsProvider[] providers = contentUseCase.getDetailsProviders(videoInfo);
                        if (providers != null && providers.length > 0) {
                            final List<Observable<? extends VideoInfo>> observables = new ArrayList<>();
                            for (IDetailsProvider provider : providers) {
                                if (!provider.isDetailsExists(videoInfo)) {
                                    observables.add(provider.getDetails(videoInfo));
                                }
                            }
                            detailsDisposable = Observable.merge(observables).subscribe(new Consumer<VideoInfo>() {

                                @Override
                                public void accept(@io.reactivex.annotations.NonNull VideoInfo videoInfo) throws Exception {
                                    detailsUseCase.getVideoInfoProperty().setValue(videoInfo);
                                }
                            }, new Consumer<Throwable>() {

                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });
                        }
                    }
                } else {
                    if (detailsDisposable != null) {
                        if (!detailsDisposable.isDisposed()) {
                            detailsDisposable.dispose();
                        }
                        detailsDisposable = null;
                    }
                }
            }
        });
        detailsUseCase.getTorrentChoiceProperty().getObservable().subscribe(new Consumer<ChoiceProperty<Torrent>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Torrent> property) throws Exception {
                if (subtitlesDisposable != null && !subtitlesDisposable.isDisposed()) {
                    subtitlesDisposable.dispose();
                    subtitlesDisposable = null;
                }
                final VideoInfo videoInfo = detailsUseCase.getVideoInfoProperty().getValue();
                final Torrent torrent = property.getItem();
                if (videoInfo != null && torrent != null) {
                    final ISubtitlesProvider subtitlesProvider = contentUseCase.getSubtitlesProvider(videoInfo);
                    if (subtitlesProvider != null) {
                        Logger.debug("load subs: " + torrent.getHash());
                        subtitlesDisposable = subtitlesProvider.getSubtitles(
                                videoInfo,
                                detailsUseCase.getSeasonChoiceProperty().getItem(),
                                detailsUseCase.getEpisodeChoiceProperty().getItem(),
                                torrent
                        ).subscribe(new Consumer<Map.Entry<String, List<Subtitles>>[]>() {

                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Map.Entry<String, List<Subtitles>>[] subtitles) throws Exception {
                                final String subLang = SubtitlesLanguage.getSubtitlesLanguage();
                                if (TextUtils.isEmpty(subLang)) {
                                    detailsUseCase.getLangSubtitlesChoiceProperty().setItems(subtitles, -1);
                                } else {
                                    int position = -1;
                                    for (int i = 0; i < subtitles.length; i++) {
                                        if (subLang.equals(SubtitlesLanguage.subtitlesIsoToName(subtitles[i].getKey()))) {
                                            position = i;
                                            break;
                                        }
                                    }
                                    detailsUseCase.getLangSubtitlesChoiceProperty().setItems(subtitles, position);
                                }
                            }
                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                                subtitlesDisposable = null;
                                throwable.printStackTrace();
                            }
                        }, new Action() {

                            @Override
                            public void run() throws Exception {
                                subtitlesDisposable = null;
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (TRIM_MEMORY_UI_HIDDEN == level) {
            shareUseCase.onAppBackground(true);
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateConfigurationLocale();
    }

    @Override
    public boolean onShowView(@NonNull Class<?> view, Object... args) {
        if (IVpnView.class == view) {
            startActivity(new Intent(PopcornApplication.this, VpnActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } else if (ISettingsView.class == view) {
            startActivity(new Intent(PopcornApplication.this, SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } else if (IBrowserView.class == view) {
            String url = (String) args[0];
            if (TextUtils.isEmpty(url)) {
                return false;
            }
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else if (ISystemShareView.class == view) {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            }
            intent.putExtra(Intent.EXTRA_TEXT, (String) args[0]);
            try {
                shareUseCase.share();
                startActivity(Intent.createChooser(intent, getApplicationInfo().loadLabel(getPackageManager())).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @NonNull
    public IShareUseCase getShareUseCase() {
        return shareUseCase;
    }

    @NonNull
    @Override
    public IMessagingUseCase getMessagingUseCase() {
        return firebaseMessagingUseCase;
    }

    @NonNull
    @Override
    public IVpnUseCase getVpnUseCase() {
        return vpnUseCase;
    }

    @NonNull
    @Override
    public IConfigUseCase getConfigUseCase() {
        return configUseCase;
    }

    @NonNull
    @Override
    public ISettingsUseCase getSettingsUseCase() {
        return settingsUseCase;
    }

    public ILocalePresenter getLocalePresenter() {
        return localePresenter;
    }

    @Override
    public void onShowMessagingNotification(@NonNull IMessagingNotificationData data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(PopcornApplication.this);
        builder.setSmallIcon(R.drawable.ic_notify_mascot);
        builder.setContentTitle(data.getTitle());
        builder.setContentText(data.getMessage());
        builder.setAutoCancel(true);
        Intent intent = new Intent(ACTION_NOTIFICATION);
        if (data.getAction() != null) {
            intent.putExtra(EXTRA_ACTION, data.getAction());
        }
        builder.setContentIntent(PendingIntent.getBroadcast(PopcornApplication.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1001, builder.build());
    }

    private void updateConfigurationLocale() {
        android.content.res.Configuration config = getResources().getConfiguration();
        config.locale = InterfaceUtil.getAppLocale();
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        SubtitlesLanguage.setWithoutSubtitlesText(getString(R.string.without_subtitle));
    }

    @NonNull
    @Override
    public IContentUseCase getContentUseCase() {
        return contentUseCase;
    }

    @NonNull
    @Override
    public IDetailsUseCase getDetailsUseCase() {
        return detailsUseCase;
    }

    @Nullable
    public final IViewRouter getActiveViewRouter() {
        return activeViewRouter;
    }

    public final void setActiveViewRouter(@Nullable IViewRouter activeViewRouter) {
        this.activeViewRouter = activeViewRouter;
        if (activeViewRouter != null) {
            onActiveViewRouter(activeViewRouter);
        }
    }

    void onReferrerReceive(String referrer) {
        final String regex = configUseCase.getConfig().getReferrerRegex();
        if (TextUtils.isEmpty(referrer) || TextUtils.isEmpty(regex)) {
            return;
        }
        if (referrer.matches(regex)) {
            fullVersion = true;
            Prefs.getPopcornPrefs().setFullVersion(true);
        }
    }

    private void onActiveViewRouter(@NonNull IViewRouter activeViewRouter) {
        if (update != null && activeViewRouter.onShowView(IUpdateView.class, update)) {
            update = null;
        }
    }

    private IFilter createCinemaFilter() {
        return new FilterView.Builder(R.string.genre, R.drawable.ic_genre, ContentRepository.KEY_GENRE, false)
                .add(new FilterItemView(R.string.popular, new FilterItem(ContentRepository.GENRE_POPULAR)), true)
                .add(new FilterItemView(R.string.action, new FilterItem(ContentRepository.GENRE_ACTION)), false)
                .add(new FilterItemView(R.string.adventure, new FilterItem(ContentRepository.GENRE_ADVENTURE)), false)
                .add(new FilterItemView(R.string.animation, new FilterItem(ContentRepository.GENRE_ANIMATION)), false)
                .add(new FilterItemView(R.string.biography, new FilterItem(ContentRepository.GENRE_BIOGRAPHY)), false)
                .add(new FilterItemView(R.string.comedy, new FilterItem(ContentRepository.GENRE_COMEDY)), false)
                .add(new FilterItemView(R.string.crime, new FilterItem(ContentRepository.GENRE_CRIME)), false)
                .add(new FilterItemView(R.string.documentary, new FilterItem(ContentRepository.GENRE_DOCUMENTARY)), false)
                .add(new FilterItemView(R.string.drama, new FilterItem(ContentRepository.GENRE_DRAMA)), false)
                .add(new FilterItemView(R.string.family, new FilterItem(ContentRepository.GENRE_FAMILY)), false)
                .add(new FilterItemView(R.string.fantasy, new FilterItem(ContentRepository.GENRE_FANTASY)), false)
                .add(new FilterItemView(R.string.film_noir, new FilterItem(ContentRepository.GENRE_FILM_NOIR)), false)
                .add(new FilterItemView(R.string.history, new FilterItem(ContentRepository.GENRE_HISTORY)), false)
                .add(new FilterItemView(R.string.horror, new FilterItem(ContentRepository.GENRE_HORROR)), false)
                .add(new FilterItemView(R.string.music, new FilterItem(ContentRepository.GENRE_MUSIC)), false)
                .add(new FilterItemView(R.string.musical, new FilterItem(ContentRepository.GENRE_MUSICAL)), false)
                .add(new FilterItemView(R.string.mystery, new FilterItem(ContentRepository.GENRE_MYSTERY)), false)
                .add(new FilterItemView(R.string.romance, new FilterItem(ContentRepository.GENRE_ROMANCE)), false)
                .add(new FilterItemView(R.string.sci_fi, new FilterItem(ContentRepository.GENRE_SCI_FI)), false)
                .add(new FilterItemView(R.string.short_, new FilterItem(ContentRepository.GENRE_SHORT)), false)
                .add(new FilterItemView(R.string.sport, new FilterItem(ContentRepository.GENRE_SPORT)), false)
                .add(new FilterItemView(R.string.thriller, new FilterItem(ContentRepository.GENRE_THRILLER)), false)
                .add(new FilterItemView(R.string.war, new FilterItem(ContentRepository.GENRE_WAR)), false)
                .add(new FilterItemView(R.string.western, new FilterItem(ContentRepository.GENRE_WESTERN)), false)
                .create();
    }

    @NonNull
    private IContentProvider initContentProvider(@NonNull IContentProvider[] contentProviders) {
        final int page = settingsUseCase.getStartPage();
        String type = null;
        if (StartPage.PAGE_CINEMA_MOVIES == page) {
            type = Cinema.TYPE_MOVIES;
        } else if (StartPage.PAGE_CINEMA_TV_SHOWS == page) {
            type = Cinema.TYPE_TV_SHOWS;
        } else if (StartPage.PAGE_ANIME_MOVIES == page) {
            type = Anime.TYPE_MOVIES;
        } else if (StartPage.PAGE_ANIME_TV_SHOWS == page) {
            type = Anime.TYPE_TV_SHOWS;
        }

        if (TextUtils.isEmpty(type)) {
            settingsUseCase.setStartPage(StartPage.DEFAULT_START_PAGE);
        } else {
            for (IContentProvider contentProvider : contentProviders) {
                if (type.equals(contentProvider.getType())) {
                    return contentProvider;
                }
            }
        }
        return initContentProvider(contentProviders);
    }
}
