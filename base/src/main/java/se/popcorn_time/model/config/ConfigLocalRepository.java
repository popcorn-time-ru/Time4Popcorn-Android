package se.popcorn_time.model.config;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigLocalRepository implements IConfigLocalRepository {

    private static final String KEY_CONFIG_URLS = "config-domains";
    private static final String KEY_UPDATER_URLS = "updater-domains";
    private static final String KEY_SHARE_URLS = "share-domains";
    private static final String KEY_ANALYTICS_ID = "analytics-tracker-id";
    private static final String KEY_CINEMA_URL = "api-domain";
    private static final String KEY_ANIME_URL = "api-anime-domain";
    private static final String KEY_POSTER_URL = "poster-domain";
    private static final String KEY_SUBTITLE_URL = "subtitles-domain";
    private static final String KEY_SITE_URL = "app-site";
    private static final String KEY_FORUM_URL = "app-forum";
    private static final String KEY_FACEBOOK_URL = "facebook-url";
    private static final String KEY_TWITTER_URL = "twitter-url";
    private static final String KEY_YOUTUBE_URL = "youtube-url";
    private static final String KEY_IMDB_URL = "imdb-url";
    private static final String KEY_REFERRER_REGEX = "referrer-regex";
    private static final String KEY_VPN_PROVIDERS = "vpn-providers";
    private static final String KEY_CHECK_VPN_OPTION_ENABLED = "show-check-vpn-option";
    private static final String KEY_CHECK_VPN_OPTION_DEFAULT = "check-vpn-connection";
    private static final String KEY_ALERT_ENABLED = "show-vpn-alert";
    private static final String KEY_ALERT = "vpn-alert";
    private static final String KEY_NOTICE = "vpn-notice";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(VpnConfig.Alert.class, new VpnConfigAlertMapper())
            .registerTypeAdapter(VpnConfig.Notice.class, new VpnConfigNoticeMapper())
            .create();

    private final SharedPreferences preferences;

    public ConfigLocalRepository(@NonNull SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public Config getConfig() {
        final Config config = new Config();
        config.setUrls(getUrls(preferences.getString(KEY_CONFIG_URLS, null)));
        config.setUpdaterUrls(getUrls(preferences.getString(KEY_UPDATER_URLS, null)));
        config.setShareUrls(getUrls(preferences.getString(KEY_SHARE_URLS, null)));
        config.setAnalyticsId(preferences.getString(KEY_ANALYTICS_ID, null));
        config.setCinemaUrl(preferences.getString(KEY_CINEMA_URL, null));
        config.setAnimeUrl(preferences.getString(KEY_ANIME_URL, null));
        config.setPosterUrl(preferences.getString(KEY_POSTER_URL, null));
        config.setSubtitlesUrl(preferences.getString(KEY_SUBTITLE_URL, null));
        config.setSiteUrl(preferences.getString(KEY_SITE_URL, null));
        config.setForumUrl(preferences.getString(KEY_FORUM_URL, null));
        config.setFacebookUrl(preferences.getString(KEY_FACEBOOK_URL, null));
        config.setTwitterUrl(preferences.getString(KEY_TWITTER_URL, null));
        config.setYoutubeUrl(preferences.getString(KEY_YOUTUBE_URL, null));
        config.setImdbUrl(preferences.getString(KEY_IMDB_URL, null));
        config.setReferrerRegex(preferences.getString(KEY_REFERRER_REGEX, null));
        config.setVpnConfig(getVpnConfig());
        return config;
    }

    @Override
    public void setConfig(@NonNull Config config) {
        preferences.edit()
                .putString(KEY_CONFIG_URLS, GSON.toJson(config.getUrls()))
                .putString(KEY_UPDATER_URLS, GSON.toJson(config.getUpdaterUrls()))
                .putString(KEY_SHARE_URLS, GSON.toJson(config.getShareUrls()))
                .putString(KEY_ANALYTICS_ID, config.getAnalyticsId())
                .putString(KEY_CINEMA_URL, config.getCinemaUrl())
                .putString(KEY_ANIME_URL, config.getAnimeUrl())
                .putString(KEY_POSTER_URL, config.getPosterUrl())
                .putString(KEY_SUBTITLE_URL, config.getSubtitlesUrl())
                .putString(KEY_SITE_URL, config.getSiteUrl())
                .putString(KEY_FORUM_URL, config.getForumUrl())
                .putString(KEY_FACEBOOK_URL, config.getFacebookUrl())
                .putString(KEY_TWITTER_URL, config.getTwitterUrl())
                .putString(KEY_YOUTUBE_URL, config.getYoutubeUrl())
                .putString(KEY_IMDB_URL, config.getImdbUrl())
                .putString(KEY_REFERRER_REGEX, config.getReferrerRegex())
                .putStringSet(KEY_VPN_PROVIDERS, new HashSet<>(Arrays.asList(config.getVpnConfig().getProviders())))
                .putBoolean(KEY_CHECK_VPN_OPTION_ENABLED, config.getVpnConfig().isCheckVpnOptionEnabled())
                .putBoolean(KEY_CHECK_VPN_OPTION_DEFAULT, config.getVpnConfig().isCheckVpnOptionDefault())
                .putBoolean(KEY_ALERT_ENABLED, config.getVpnConfig().isAlertEnabled())
                .putString(KEY_ALERT, GSON.toJson(config.getVpnConfig().getAlert()))
                .putString(KEY_NOTICE, GSON.toJson(config.getVpnConfig().getNotice()))
                .apply();
    }

    @Nullable
    private String[] getUrls(@Nullable String urls) {
        if (!TextUtils.isEmpty(urls)) {
            try {
                return GSON.fromJson(urls, String[].class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NonNull
    private VpnConfig getVpnConfig() {
        final VpnConfig vpnConfig = new VpnConfig();
        vpnConfig.setProviders(getVpnConfigProviders());
        vpnConfig.setCheckVpnOptionEnabled(preferences.getBoolean(KEY_CHECK_VPN_OPTION_ENABLED, false));
        vpnConfig.setCheckVpnOptionDefault(preferences.getBoolean(KEY_CHECK_VPN_OPTION_DEFAULT, true));
        vpnConfig.setAlertEnabled(preferences.getBoolean(KEY_ALERT_ENABLED, true));
        vpnConfig.setAlert(getVpnConfigAlert());
        vpnConfig.setNotice(getVpnConfigNotice());
        return vpnConfig;
    }

    @Nullable
    private String[] getVpnConfigProviders() {
        if (preferences.contains(KEY_VPN_PROVIDERS)) {
            final Set<String> providers = preferences.getStringSet(KEY_VPN_PROVIDERS, new HashSet<String>());
            return providers.toArray(new String[providers.size()]);
        }
        return null;
    }

    @Nullable
    private VpnConfig.Alert getVpnConfigAlert() {
        if (preferences.contains(KEY_ALERT)) {
            return GSON.fromJson(preferences.getString(KEY_ALERT, null), VpnConfig.Alert.class);
        }
        return null;
    }

    @Nullable
    private VpnConfig.Notice getVpnConfigNotice() {
        if (preferences.contains(KEY_NOTICE)) {
            return GSON.fromJson(preferences.getString(KEY_NOTICE, null), VpnConfig.Notice.class);
        }
        return null;
    }
}
