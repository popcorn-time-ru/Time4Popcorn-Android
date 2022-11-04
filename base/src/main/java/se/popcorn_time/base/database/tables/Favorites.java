package se.popcorn_time.base.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.popcorn_time.base.model.video.Anime;
import se.popcorn_time.base.model.video.Cinema;
import se.popcorn_time.base.model.video.info.AnimeMoviesInfo;
import se.popcorn_time.base.model.video.info.AnimeTvShowsInfo;
import se.popcorn_time.base.model.video.info.CinemaMoviesInfo;
import se.popcorn_time.base.model.video.info.CinemaTvShowsInfo;
import se.popcorn_time.base.model.video.info.MoviesInfo;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.utils.GsonUtils;

public final class Favorites extends Table {

    public static final String NAME = "favorites";

    public static final String _TYPE = "_type";
    public static final String _TITLE = "_title";
    public static final String _YEAR = "_year";
    public static final String _RATING = "_rating";
    public static final String _IMDB = "_imdb";
    public static final String _ACTORS = "_actors";
    public static final String _TRAILER = "_trailer";
    public static final String _DESCRIPTION = "_description";
    public static final String _POSTER_MEDIUM_URL = "_poster_medium_url";
    public static final String _POSTER_BIG_URL = "_poster_big_url";
    public static final String _TORRENTS_INFO = "_torrents_info";

    public static final Uri CONTENT_URI = getContentUri(NAME);

    public static String createTableQuery() {
        return "CREATE TABLE " + NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + _TYPE + " TEXT, "
                + _TITLE + " TEXT, "
                + _YEAR + " TEXT, "
                + _RATING + " REAL, "
                + _IMDB + " TEXT, "
                + _ACTORS + " TEXT, "
                + _TRAILER + " TEXT, "
                + _DESCRIPTION + " TEXT, "
                + _POSTER_MEDIUM_URL + " TEXT, "
                + _POSTER_BIG_URL + " TEXT, "
                + _TORRENTS_INFO + " TEXT, "
                + "UNIQUE (" + _IMDB + ") ON CONFLICT REPLACE)";
    }

    public static Cursor query(Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    public static Uri insert(Context context, VideoInfo info) {
        return context.getContentResolver().insert(CONTENT_URI, buildValues(info));
    }

    public static int update(Context context, VideoInfo info) {
        return context.getContentResolver().update(CONTENT_URI, buildValues(info), _IMDB + "=\"" + info.getImdb() + "\"", null);
    }

    public static int delete(Context context, VideoInfo info) {
        return context.getContentResolver().delete(CONTENT_URI, _IMDB + "=\"" + info.getImdb() + "\"", null);
    }

    public static boolean isFavorite(@NonNull Context context, @NonNull VideoInfo videoInfo) {
        boolean favorite = false;
        final Cursor cursor = query(context, null, Favorites._IMDB + "=\"" + videoInfo.getImdb() + "\"", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                update(context, videoInfo);
                favorite = true;
            }
            cursor.close();
        }
        return favorite;
    }

    private static ContentValues buildValues(VideoInfo videoInfo) {
        ContentValues values = new ContentValues();
        String type = videoInfo.getType();
        values.put(_TYPE, type);
        values.put(_TITLE, videoInfo.getTitle());
        values.put(_YEAR, videoInfo.getYear());
        values.put(_RATING, videoInfo.getRating());
        values.put(_IMDB, videoInfo.getImdb());
        values.put(_ACTORS, videoInfo.getActors());
        values.put(_TRAILER, videoInfo.getTrailer());
        values.put(_DESCRIPTION, videoInfo.getDescription());
        values.put(_POSTER_MEDIUM_URL, videoInfo.getPoster());
        values.put(_POSTER_BIG_URL, videoInfo.getPosterBig());
        switch (type) {
            case Cinema.TYPE_MOVIES:
            case Anime.TYPE_MOVIES:
                try {
                    values.put(_TORRENTS_INFO, torrentsToJson(((MoviesInfo) videoInfo).getLangTorrents()).toString());
                } catch (JSONException e) {
                    Logger.error("Favorites<buildValues>: Error", e);
                }
                break;
        }
        return values;
    }

    @Nullable
    public static VideoInfo create(Cursor cursor) throws JSONException, IllegalArgumentException {
        String type = cursor.getString(cursor.getColumnIndexOrThrow(_TYPE));
        switch (type) {
            case Cinema.TYPE_MOVIES:
                return createCinemaMovies(cursor);
            case Cinema.TYPE_TV_SHOWS:
                return createCinemaTvShows(cursor);
            case Anime.TYPE_MOVIES:
                return createAnimeMovies(cursor);
            case Anime.TYPE_TV_SHOWS:
                return createAnimeTvShows(cursor);
            default:
                Logger.error("Favorites: wrong video type - " + type);
                return null;
        }
    }

    @NonNull
    private static CinemaMoviesInfo createCinemaMovies(Cursor cursor) throws JSONException {
        CinemaMoviesInfo info = new CinemaMoviesInfo();
        populateVideoInfo(info, cursor);
        String json = cursor.getString(cursor.getColumnIndexOrThrow(_TORRENTS_INFO));
        if (!TextUtils.isEmpty(json)) {
            populateTorrents(info, new JsonParser().parse(json));
        }
        return info;
    }

    @NonNull
    private static CinemaTvShowsInfo createCinemaTvShows(Cursor cursor) throws JSONException {
        CinemaTvShowsInfo info = new CinemaTvShowsInfo();
        populateVideoInfo(info, cursor);
        return info;
    }

    @NonNull
    private static AnimeMoviesInfo createAnimeMovies(Cursor cursor) throws JSONException {
        AnimeMoviesInfo info = new AnimeMoviesInfo();
        populateVideoInfo(info, cursor);
        String json = cursor.getString(cursor.getColumnIndexOrThrow(_TORRENTS_INFO));
        if (!TextUtils.isEmpty(json)) {
            populateTorrents(info, new JsonParser().parse(json));
        }
        return info;
    }

    @NonNull
    private static AnimeTvShowsInfo createAnimeTvShows(Cursor cursor) throws JSONException {
        AnimeTvShowsInfo info = new AnimeTvShowsInfo();
        populateVideoInfo(info, cursor);
        return info;
    }

    private static void populateVideoInfo(VideoInfo videoInfo, Cursor cursor) {
        videoInfo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(_TITLE)));
        videoInfo.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(_YEAR))));
        videoInfo.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(_RATING)));
        videoInfo.setImdb(cursor.getString(cursor.getColumnIndexOrThrow(_IMDB)));
        videoInfo.setActors(cursor.getString(cursor.getColumnIndexOrThrow(_ACTORS)));
        videoInfo.setTrailer(cursor.getString(cursor.getColumnIndexOrThrow(_TRAILER)));
        videoInfo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(_DESCRIPTION)));
        videoInfo.setPoster(cursor.getString(cursor.getColumnIndexOrThrow(_POSTER_MEDIUM_URL)));
        videoInfo.setPosterBig(cursor.getString(cursor.getColumnIndexOrThrow(_POSTER_BIG_URL)));
    }

    private static final String KEY_TORRENT_HASH = "torrent_hash";
    private static final String KEY_TORRENT_URL = "torrent_url";
    private static final String KEY_TORRENT_MAGNET = "torrent_magnet";
    private static final String KEY_TORRENT_FILE = "file";
    private static final String KEY_TORRENT_QUALITY = "quality";
    private static final String KEY_TORRENT_SIZE = "size_bytes";
    private static final String KEY_TORRENT_SEEDS = "torrent_seeds";
    private static final String KEY_TORRENT_PEERS = "torrent_peers";

    private static void populateTorrents(MoviesInfo info, JsonElement jsonTorrents) throws JSONException {
        final Map<String, List<Torrent>> map = new LinkedHashMap<>();
        if (jsonTorrents.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : jsonTorrents.getAsJsonObject().entrySet()) {
                map.put(entry.getKey(), jsonToTorrents(entry.getValue().getAsJsonArray()));
            }
        } else if (jsonTorrents.isJsonArray()) {
            map.put("", jsonToTorrents(jsonTorrents.getAsJsonArray()));
        }
        info.setLangTorrents(map);
    }

    private static List<Torrent> jsonToTorrents(JsonArray jsonTorrents) {
        final List<Torrent> torrents = new ArrayList<>();
        for (JsonElement jsonElement : jsonTorrents) {
            final JsonObject jsonTorrent = jsonElement.getAsJsonObject();
            final Torrent torrent = new Torrent();
            torrent.setHash(GsonUtils.getAsString(jsonTorrent, KEY_TORRENT_HASH));
            torrent.setUrl(GsonUtils.getAsString(jsonTorrent, KEY_TORRENT_URL));
            torrent.setMagnet(GsonUtils.getAsString(jsonTorrent, KEY_TORRENT_MAGNET));
            torrent.setFile(GsonUtils.getAsString(jsonTorrent, KEY_TORRENT_FILE));
            torrent.setQuality(GsonUtils.getAsString(jsonTorrent, KEY_TORRENT_QUALITY));
            torrent.setSize(GsonUtils.getAsLong(jsonTorrent, KEY_TORRENT_SIZE));
            torrent.setSeeds(GsonUtils.getAsInt(jsonTorrent, KEY_TORRENT_SEEDS));
            torrent.setPeers(GsonUtils.getAsInt(jsonTorrent, KEY_TORRENT_PEERS));
            torrents.add(torrent);
        }
        return torrents;
    }

    @NonNull
    private static JsonElement torrentsToJson(Map<String, List<Torrent>> torrents) throws JSONException {
        final JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, List<Torrent>> entry : torrents.entrySet()) {
            final JsonArray jsonTorrents = new JsonArray();
            for (Torrent torrent : entry.getValue()) {
                JsonObject jsonTorrent = new JsonObject();
                jsonTorrent.addProperty(KEY_TORRENT_HASH, torrent.getHash());
                jsonTorrent.addProperty(KEY_TORRENT_URL, torrent.getUrl());
                jsonTorrent.addProperty(KEY_TORRENT_MAGNET, torrent.getMagnet());
                jsonTorrent.addProperty(KEY_TORRENT_FILE, torrent.getFile());
                jsonTorrent.addProperty(KEY_TORRENT_QUALITY, torrent.getQuality());
                jsonTorrent.addProperty(KEY_TORRENT_SIZE, torrent.getSize());
                jsonTorrent.addProperty(KEY_TORRENT_SEEDS, torrent.getSeeds());
                jsonTorrent.addProperty(KEY_TORRENT_PEERS, torrent.getPeers());
                jsonTorrents.add(jsonTorrent);
            }
            jsonObject.add(entry.getKey(), jsonTorrents);
        }
        return jsonObject;
    }
}