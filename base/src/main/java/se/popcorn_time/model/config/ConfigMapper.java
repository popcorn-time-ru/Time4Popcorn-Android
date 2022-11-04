package se.popcorn_time.model.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import se.popcorn_time.utils.GsonUtils;

public final class ConfigMapper implements JsonDeserializer<Config> {

    private static final String KEY_CONFIG_URLS = "configDomains";
    private static final String KEY_UPDATER_URLS = "updaterDomains";
    private static final String KEY_SHARE_URLS = "shareDomains";
    private static final String KEY_ANALYTICS_ID = "analyticsTrackerId";
    private static final String KEY_CINEMA_URL = "apiDomain";
    private static final String KEY_ANIME_URL = "apiAnimeDomain";
    private static final String KEY_POSTER_URL = "posterDomain";
    private static final String KEY_SUBTITLES_URL = "subtitleDomain";
    private static final String KEY_SITE_URL = "appSite";
    private static final String KEY_FORUM_URL = "appForum";
    private static final String KEY_FACEBOOK_URL = "appFacebook";
    private static final String KEY_TWITTER_URL = "appTwitter";
    private static final String KEY_YOUTUBE_URL = "appYoutube";
    private static final String KEY_IMDB_URL = "imdbUrl";
    private static final String KEY_REFERRER_REGEX = "referrerRegex";

    @Override
    public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Config config = new Config();
        final JsonObject jsonConfig = (JsonObject) json;
        config.setUrls(context.<String[]>deserialize(jsonConfig.get(KEY_CONFIG_URLS), String[].class));
        config.setUpdaterUrls(context.<String[]>deserialize(jsonConfig.get(KEY_UPDATER_URLS), String[].class));
        config.setShareUrls(context.<String[]>deserialize(jsonConfig.get(KEY_SHARE_URLS), String[].class));
        config.setAnalyticsId(GsonUtils.getAsString(jsonConfig, KEY_ANALYTICS_ID));
        config.setCinemaUrl(GsonUtils.getAsString(jsonConfig, KEY_CINEMA_URL));
        config.setAnimeUrl(GsonUtils.getAsString(jsonConfig, KEY_ANIME_URL));
        config.setPosterUrl(GsonUtils.getAsString(jsonConfig, KEY_POSTER_URL));
        config.setSubtitlesUrl(GsonUtils.getAsString(jsonConfig, KEY_SUBTITLES_URL));
        config.setSiteUrl(GsonUtils.getAsString(jsonConfig, KEY_SITE_URL));
        config.setForumUrl(GsonUtils.getAsString(jsonConfig, KEY_FORUM_URL));
        config.setFacebookUrl(GsonUtils.getAsString(jsonConfig, KEY_FACEBOOK_URL));
        config.setTwitterUrl(GsonUtils.getAsString(jsonConfig, KEY_TWITTER_URL));
        config.setYoutubeUrl(GsonUtils.getAsString(jsonConfig, KEY_YOUTUBE_URL));
        config.setImdbUrl(GsonUtils.getAsString(jsonConfig, KEY_IMDB_URL));
        config.setReferrerRegex(GsonUtils.getAsString(jsonConfig, KEY_REFERRER_REGEX));
        config.setVpnConfig(context.<VpnConfig>deserialize(jsonConfig, VpnConfig.class));
        return config;
    }
}
