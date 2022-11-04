package se.popcorn_time.base.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Locale;

import se.popcorn_time.base.model.WatchInfo;

public final class History extends Table {

    public static final String NAME = "history";

    public static final String _IMDB = "_imdb";
    public static final String _SEASON = "_season";
    public static final String _EPISODE = "_episode";

    public static final Uri CONTENT_URI = getContentUri(NAME);

    private static final String FORMAT_SELECTION = _IMDB + "=\"%s\" AND " + _SEASON + "=%d AND " + _EPISODE + "=%d";

    public static String createTableQuery() {
        return "CREATE TABLE " + NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + _IMDB + " TEXT, "
                + _SEASON + " INTEGER, "
                + _EPISODE + " INTEGER)";
    }

    public static Uri insert(Context context, WatchInfo watchInfo) {
        return insert(context, watchInfo.imdb, watchInfo.season, watchInfo.episode);
    }

    public static Uri insert(Context context, String imdb, int season, int episode) {
        final ContentValues values = new ContentValues();
        values.put(_IMDB, imdb);
        values.put(_SEASON, season);
        values.put(_EPISODE, episode);
        return context.getContentResolver().insert(CONTENT_URI, values);
    }

    public static int delete(Context context, String imdb, int season, int episode) {
        return context.getContentResolver().delete(CONTENT_URI, String.format(Locale.ENGLISH, FORMAT_SELECTION, imdb, season, episode), null);
    }

    public static boolean isWatched(Context context, String imdb, int season, int episode) {
        final String selection = String.format(Locale.ENGLISH, FORMAT_SELECTION, imdb, season, episode);
        final Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, selection, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}
