package se.popcorn_time.base.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.Locale;

import se.popcorn_time.base.database.DBUtils;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.model.video.info.Torrent;

public final class Downloads extends Table {

    public static final String NAME = "downloads";

    public static final String _TYPE = "_type";
    public static final String _IMDB = "_imdb";
    public static final String _TORRENT_URL = "_torrent_url";
    public static final String _TORRENT_MAGNET = "_torrent_magnet";
    public static final String _FILE_NAME = "_file_name";
    public static final String _POSTER_URL = "_poster_url";
    public static final String _TITLE = "_title";
    public static final String _DIRECTORY = "_directory";
    public static final String _TORRENT_FILE_PATH = "_torrent_file_path";
    public static final String _STATE = "_state";
    public static final String _SIZE = "_size";
    public static final String _SEASON = "_season";
    public static final String _EPISODE = "_episode";
    public static final String _TORRENT_HASH = "_torrent_hash";
    public static final String _RESUME_DATA = "_resume_data";
    public static final String _READY_TO_WATCH = "_ready_to_watch";

    public static final Uri CONTENT_URI = getContentUri(NAME);

    private static final String FORMAT_TORRENT_HASH_SELECTION = Downloads._TORRENT_HASH + "=\"%s\" COLLATE NOCASE";
    private static final String FORMAT_VIDEO_DOWNLOAD_SELECTION = FORMAT_TORRENT_HASH_SELECTION + " OR ( " + Downloads._FILE_NAME + "=\"%s\" AND " + Downloads._SIZE + "=%d )";
    private static final String FORMAT_TV_SHOW_DOWNLOAD_SELECTION = "( " + FORMAT_VIDEO_DOWNLOAD_SELECTION + " ) AND " + Downloads._SEASON + "=%d AND " + Downloads._EPISODE + "=%d";

    public static String createTableQuery() {
        return "CREATE TABLE " + NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + _TYPE + " TEXT, "
                + _IMDB + " TEXT, "
                + _TORRENT_URL + " TEXT, "
                + _TORRENT_MAGNET + " TEXT, "
                + _FILE_NAME + " TEXT, "
                + _POSTER_URL + " TEXT, "
                + _TITLE + " TEXT, "
                + _DIRECTORY + " TEXT, "
                + _TORRENT_FILE_PATH + " TEXT, "
                + _STATE + " INTEGER, "
                + _SIZE + " INTEGER, "
                + _SEASON + " INTEGER, "
                + _EPISODE + " INTEGER, "
                + _TORRENT_HASH + " TEXT, "
                + _RESUME_DATA + " BLOB, "
                + _READY_TO_WATCH + " INTEGER)";
    }

    public static Cursor query(Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    public static Uri insert(Context context, DownloadInfo info) {
        return context.getContentResolver().insert(CONTENT_URI, buildValues(info));
    }

    public static int update(Context context, DownloadInfo info) {
        return context.getContentResolver().update(getContentUri(NAME, info.id), buildValues(info), null, null);
    }

    public static int update(Context context, String hash, byte[] resumeData) {
        final ContentValues values = new ContentValues();
        values.put(_RESUME_DATA, resumeData);
        return context.getContentResolver().update(CONTENT_URI, values, String.format(Locale.ENGLISH, FORMAT_TORRENT_HASH_SELECTION, hash), null);
    }

    public static int readyToWatch(Context context, long id) {
        final ContentValues values = new ContentValues();
        values.put(_READY_TO_WATCH, true);
        return context.getContentResolver().update(getContentUri(NAME, id), values, null, null);
    }

    public static int delete(Context context, DownloadInfo info) {
        return context.getContentResolver().delete(getContentUri(NAME, info.id), null, null);
    }

    public static boolean isDownloads(@NonNull Context context, @NonNull Torrent torrent, int season, int episode) {
        final String selection;
        if (!TextUtils.isEmpty(torrent.getHash()) && !TextUtils.isEmpty(torrent.getFile()) && torrent.getSize() > 0) {
            if (season >= 0 && episode >= 0) {
                selection = String.format(Locale.ENGLISH, FORMAT_TV_SHOW_DOWNLOAD_SELECTION, torrent.getHash(), torrent.getFile(), torrent.getSize(), season, episode);
            } else {
                selection = String.format(Locale.ENGLISH, FORMAT_VIDEO_DOWNLOAD_SELECTION, torrent.getHash(), torrent.getFile(), torrent.getSize());
            }
        } else {
            selection = Downloads._TORRENT_URL + "=\"" + torrent.getUrl() + "\" OR " + Downloads._TORRENT_MAGNET + "=\"" + torrent.getMagnet() + "\"";
        }
        final Cursor cursor = Downloads.query(context, null, selection, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static void populate(DownloadInfo info, Cursor cursor) {
        try {
            info.id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            info.type = cursor.getString(cursor.getColumnIndexOrThrow(_TYPE));
            info.imdb = cursor.getString(cursor.getColumnIndexOrThrow(_IMDB));
            info.torrentUrl = cursor.getString(cursor.getColumnIndexOrThrow(_TORRENT_URL));
            info.torrentMagnet = cursor.getString(cursor.getColumnIndexOrThrow(_TORRENT_MAGNET));
            info.fileName = cursor.getString(cursor.getColumnIndexOrThrow(_FILE_NAME));
            info.posterUrl = cursor.getString(cursor.getColumnIndexOrThrow(_POSTER_URL));
            info.title = cursor.getString(cursor.getColumnIndexOrThrow(_TITLE));
            info.directory = new File(cursor.getString(cursor.getColumnIndexOrThrow(_DIRECTORY)));
            info.torrentFilePath = cursor.getString(cursor.getColumnIndexOrThrow(_TORRENT_FILE_PATH));
            info.state = cursor.getInt(cursor.getColumnIndexOrThrow(_STATE));
            info.size = cursor.getLong(cursor.getColumnIndexOrThrow(_SIZE));
            info.season = cursor.getInt(cursor.getColumnIndexOrThrow(_SEASON));
            info.episode = cursor.getInt(cursor.getColumnIndexOrThrow(_EPISODE));
            info.torrentHash = cursor.getString(cursor.getColumnIndexOrThrow(_TORRENT_HASH));
            info.resumeData = cursor.getBlob(cursor.getColumnIndexOrThrow(_RESUME_DATA));
            info.readyToWatch = DBUtils.getInt(cursor, _READY_TO_WATCH, 0) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ContentValues buildValues(DownloadInfo info) {
        ContentValues values = new ContentValues();
        values.put(_TYPE, info.type);
        values.put(_IMDB, info.imdb);
        values.put(_TORRENT_URL, info.torrentUrl);
        values.put(_TORRENT_MAGNET, info.torrentMagnet);
        values.put(_FILE_NAME, info.fileName);
        values.put(_POSTER_URL, info.posterUrl);
        values.put(_TITLE, info.title);
        values.put(_DIRECTORY, info.directory.getAbsolutePath());
        values.put(_TORRENT_FILE_PATH, info.torrentFilePath);
        values.put(_STATE, info.state);
        values.put(_SIZE, info.size);
        values.put(_SEASON, info.season);
        values.put(_EPISODE, info.episode);
        values.put(_TORRENT_HASH, info.torrentHash);
        values.put(_RESUME_DATA, info.resumeData);
        return values;
    }
}