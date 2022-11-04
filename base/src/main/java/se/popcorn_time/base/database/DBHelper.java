package se.popcorn_time.base.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.database.tables.Favorites;
import se.popcorn_time.base.database.tables.History;
import se.popcorn_time.base.model.video.Anime;
import se.popcorn_time.base.model.video.Cinema;
import se.popcorn_time.base.torrent.TorrentState;
import se.popcorn_time.base.utils.Logger;

public class DBHelper extends SQLiteOpenHelper {

    private static final String NAME = "popcorn.db";
    private static final int VERSION_1 = 1001;
    private static final int VERSION_2 = 1002;
    private static final int VERSION_3 = 1003;
    private static final int VERSION_4 = 1004;
    private static final int VERSION_5 = 1005;
    private static final int VERSION_6 = 1006;
    private static final int VERSION_7 = 1007;
    private static final int CURRENT_VERSION = VERSION_7;

    public DBHelper(Context context) {
        super(context, NAME, null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Favorites.createTableQuery());
        db.execSQL(Downloads.createTableQuery());
        db.execSQL(History.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (VERSION_1 == oldVersion) {
            upgradeVersion_1(db, newVersion);
        } else if (VERSION_2 == oldVersion) {
            upgradeVersion_2(db, newVersion);
        } else if (VERSION_3 == oldVersion) {
            upgradeVersion_3(db, newVersion);
        } else if (VERSION_4 == oldVersion) {
            upgradeVersion_4(db, newVersion);
        } else if (VERSION_5 == oldVersion) {
            upgradeVersion_5(db, newVersion);
        } else if (VERSION_6 == oldVersion) {
            upgradeVersion_6(db, newVersion);
        }
    }

	/*
     * Upgrade version: 1
	 */

    private void upgradeVersion_1(SQLiteDatabase db, int newVersion) {
        if (VERSION_2 == newVersion) {
            upgradeVersion_1_to_2(db);
        } else if (VERSION_3 == newVersion) {
            upgradeVersion_1_to_3(db);
        } else if (VERSION_4 == newVersion) {
            upgradeVersion_1_to_4(db);
        } else if (VERSION_5 == newVersion) {
            upgradeVersion_1_to_5(db);
        } else if (VERSION_6 == newVersion) {
            upgradeVersion_1_to_6(db);
        } else if (VERSION_7 == newVersion) {
            upgradeVersion_1_to_7(db);
        }
    }

    private void upgradeVersion_1_to_2(SQLiteDatabase db) {
        db.execSQL(Downloads.createTableQuery());
        Logger.debug("Database upgrade: " + VERSION_1 + " to " + VERSION_2);
    }

    private void upgradeVersion_1_to_3(SQLiteDatabase db) {
        db.execSQL(Downloads.createTableQuery());
        Logger.debug("Database upgrade: " + VERSION_1 + " to " + VERSION_3);
    }

    private void upgradeVersion_1_to_4(SQLiteDatabase db) {
        db.execSQL(Downloads.createTableQuery());
        updateCinemaTypeColumn(db);
        Logger.debug("Database upgrade: " + VERSION_1 + " to " + VERSION_4);
    }

    private void upgradeVersion_1_to_5(SQLiteDatabase db) {
        db.execSQL(Downloads.createTableQuery());
        updateCinemaTypeColumn(db);
        Logger.debug("Database upgrade: " + VERSION_1 + " to " + VERSION_5);
    }

    private void upgradeVersion_1_to_6(SQLiteDatabase db) {
        db.execSQL(Downloads.createTableQuery());
        updateCinemaTypeColumn(db);
        Logger.debug("Database upgrade: " + VERSION_1 + " to " + VERSION_6);
    }

    private void upgradeVersion_1_to_7(SQLiteDatabase db) {
        db.execSQL(Downloads.createTableQuery());
        db.execSQL(History.createTableQuery());
        updateCinemaTypeColumn(db);
        Logger.debug("Database upgrade: " + VERSION_1 + " to " + VERSION_7);
    }

	/*
     * Upgrade version: 2
	 */

    private void upgradeVersion_2(SQLiteDatabase db, int newVersion) {
        if (VERSION_3 == newVersion) {
            upgradeVersion_2_to_3(db);
        } else if (VERSION_4 == newVersion) {
            upgradeVersion_2_to_4(db);
        } else if (VERSION_5 == newVersion) {
            upgradeVersion_2_to_5(db);
        } else if (VERSION_6 == newVersion) {
            upgradeVersion_2_to_6(db);
        } else if (VERSION_7 == newVersion) {
            upgradeVersion_2_to_7(db);
        }
    }

    private void upgradeVersion_2_to_3(SQLiteDatabase db) {
        addMagnetColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_2 + " to " + VERSION_3);
    }

    private void upgradeVersion_2_to_4(SQLiteDatabase db) {
        addMagnetColumnToDownloads(db);
        updateCinemaTypeColumn(db);
        Logger.debug("Database upgrade: " + VERSION_2 + " to " + VERSION_4);
    }

    private void upgradeVersion_2_to_5(SQLiteDatabase db) {
        addMagnetColumnToDownloads(db);
        updateCinemaTypeColumn(db);
        addSeasonAndEpisodeColumnsToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_2 + " to " + VERSION_5);
    }

    private void upgradeVersion_2_to_6(SQLiteDatabase db) {
        addMagnetColumnToDownloads(db);
        updateCinemaTypeColumn(db);
        addSeasonAndEpisodeColumnsToDownloads(db);
        addTorrentHashColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_2 + " to " + VERSION_6);
    }

    private void upgradeVersion_2_to_7(SQLiteDatabase db) {
        db.execSQL(History.createTableQuery());
        addMagnetColumnToDownloads(db);
        updateCinemaTypeColumn(db);
        addSeasonAndEpisodeColumnsToDownloads(db);
        addTorrentHashColumnToDownloads(db);
        addResumeDataColumnToDownloads(db);
        addReadyToWatchColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_2 + " to " + VERSION_7);
    }

    /*
     * Upgrade version: 3
	 */

    private void upgradeVersion_3(SQLiteDatabase db, int newVersion) {
        if (VERSION_4 == newVersion) {
            upgradeVersion_3_to_4(db);
        } else if (VERSION_5 == newVersion) {
            upgradeVersion_3_to_5(db);
        } else if (VERSION_6 == newVersion) {
            upgradeVersion_3_to_6(db);
        } else if (VERSION_7 == newVersion) {
            upgradeVersion_3_to_7(db);
        }
    }

    private void upgradeVersion_3_to_4(SQLiteDatabase db) {
        updateCinemaTypeColumn(db);
        Logger.debug("Database upgrade: " + VERSION_3 + " to " + VERSION_4);
    }

    private void upgradeVersion_3_to_5(SQLiteDatabase db) {
        updateCinemaTypeColumn(db);
        addSeasonAndEpisodeColumnsToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_3 + " to " + VERSION_5);
    }

    private void upgradeVersion_3_to_6(SQLiteDatabase db) {
        updateCinemaTypeColumn(db);
        addSeasonAndEpisodeColumnsToDownloads(db);
        addTorrentHashColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_3 + " to " + VERSION_6);
    }

    private void upgradeVersion_3_to_7(SQLiteDatabase db) {
        db.execSQL(History.createTableQuery());
        updateCinemaTypeColumn(db);
        addSeasonAndEpisodeColumnsToDownloads(db);
        addTorrentHashColumnToDownloads(db);
        addResumeDataColumnToDownloads(db);
        addReadyToWatchColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_3 + " to " + VERSION_7);
    }

    /*
     * Upgrade version: 4
	 */

    private void upgradeVersion_4(SQLiteDatabase db, int newVersion) {
        if (VERSION_5 == newVersion) {
            upgradeVersion_4_to_5(db);
        } else if (VERSION_6 == newVersion) {
            upgradeVersion_4_to_6(db);
        } else if (VERSION_7 == newVersion) {
            upgradeVersion_4_to_7(db);
        }
    }

    private void upgradeVersion_4_to_5(SQLiteDatabase db) {
        addSeasonAndEpisodeColumnsToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_4 + " to " + VERSION_5);
    }

    private void upgradeVersion_4_to_6(SQLiteDatabase db) {
        addSeasonAndEpisodeColumnsToDownloads(db);
        addTorrentHashColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_4 + " to " + VERSION_6);
    }

    private void upgradeVersion_4_to_7(SQLiteDatabase db) {
        db.execSQL(History.createTableQuery());
        addSeasonAndEpisodeColumnsToDownloads(db);
        addTorrentHashColumnToDownloads(db);
        addResumeDataColumnToDownloads(db);
        addReadyToWatchColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_4 + " to " + VERSION_7);
    }

    /*
    * Upgrade version: 5
    * */

    private void upgradeVersion_5(SQLiteDatabase db, int newVersion) {
        if (VERSION_6 == newVersion) {
            upgradeVersion_5_to_6(db);
        } else if (VERSION_7 == newVersion) {
            upgradeVersion_5_to_7(db);
        }
    }

    private void upgradeVersion_5_to_6(SQLiteDatabase db) {
        addTorrentHashColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_5 + " to " + VERSION_6);
    }

    private void upgradeVersion_5_to_7(SQLiteDatabase db) {
        db.execSQL(History.createTableQuery());
        addTorrentHashColumnToDownloads(db);
        addResumeDataColumnToDownloads(db);
        addReadyToWatchColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_5 + " to " + VERSION_7);
    }

     /*
    * Upgrade version: 6
    * */

    private void upgradeVersion_6(SQLiteDatabase db, int newVersion) {
        if (VERSION_7 == newVersion) {
            upgradeVersion_6_to_7(db);
        }
    }

    private void upgradeVersion_6_to_7(SQLiteDatabase db) {
        db.execSQL(History.createTableQuery());
        addResumeDataColumnToDownloads(db);
        addReadyToWatchColumnToDownloads(db);
        Logger.debug("Database upgrade: " + VERSION_6 + " to " + VERSION_7);
    }

    /*
    * Upgrade methods
    * */

    private void addMagnetColumnToDownloads(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Downloads.NAME + " ADD COLUMN " + Downloads._TORRENT_MAGNET + " TEXT");
    }

    private void updateCinemaTypeColumn(SQLiteDatabase db) {
        ContentValues favoritesMoviesValue = new ContentValues();
        favoritesMoviesValue.put(Favorites._TYPE, Cinema.TYPE_MOVIES);
        db.update(Favorites.NAME, favoritesMoviesValue, Favorites._TYPE + " = 'list'", null);

        ContentValues favoritesTvShowsValue = new ContentValues();
        favoritesTvShowsValue.put(Favorites._TYPE, Cinema.TYPE_TV_SHOWS);
        db.update(Favorites.NAME, favoritesTvShowsValue, Favorites._TYPE + " = 'shows'", null);

        ContentValues downloadsMoviesValue = new ContentValues();
        downloadsMoviesValue.put(Downloads._TYPE, Cinema.TYPE_MOVIES);
        db.update(Downloads.NAME, downloadsMoviesValue, Downloads._TYPE + " = 'list'", null);

        ContentValues downloadsTvShowsValue = new ContentValues();
        downloadsTvShowsValue.put(Downloads._TYPE, Cinema.TYPE_TV_SHOWS);
        db.update(Downloads.NAME, downloadsTvShowsValue, Downloads._TYPE + " = 'shows'", null);

        // update state column, 8 - old state paused value
        ContentValues state = new ContentValues();
        state.put(Downloads._STATE, TorrentState.PAUSED);
        db.update(Downloads.NAME, state, Downloads._STATE + " = 8", null);
    }

    private void addSeasonAndEpisodeColumnsToDownloads(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Downloads.NAME + " ADD COLUMN " + Downloads._SEASON + " INTEGER");
        db.execSQL("ALTER TABLE " + Downloads.NAME + " ADD COLUMN " + Downloads._EPISODE + " INTEGER");
        String[] columns = new String[]{Downloads._ID, Downloads._TYPE, "_summary"};
        Cursor cursor = db.query(Downloads.NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Pattern pattern = Pattern.compile("\\{\\{season\\}\\} ([0-9]+), \\{\\{episode\\}\\} ([0-9]+)");
                do {
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(Downloads._TYPE));
                    if (Cinema.TYPE_TV_SHOWS.equals(type) || Anime.TYPE_TV_SHOWS.equals(type)) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads._ID));
                        String summary = cursor.getString(cursor.getColumnIndexOrThrow("_summary"));
                        Matcher m = pattern.matcher(summary);
                        if (m.find() && m.groupCount() == 2) {
                            int season = Integer.parseInt(m.group(1));
                            int episode = Integer.parseInt(m.group(2));
                            ContentValues values = new ContentValues();
                            values.put(Downloads._SEASON, season);
                            values.put(Downloads._EPISODE, episode);
                            db.update(Downloads.NAME, values, Downloads._ID + " = " + id, null);
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        dropColumn(db, Downloads.NAME, Downloads.createTableQuery(), new String[]{"_summary", "_subtitles_data_url"});
    }

    private void addTorrentHashColumnToDownloads(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Downloads.NAME + " ADD COLUMN " + Downloads._TORRENT_HASH + " TEXT");
    }

    private void addResumeDataColumnToDownloads(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Downloads.NAME + " ADD COLUMN " + Downloads._RESUME_DATA + " BLOB");
    }

    private void addReadyToWatchColumnToDownloads(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Downloads.NAME + " ADD COLUMN " + Downloads._READY_TO_WATCH + " INTEGER");
    }

    private void dropColumn(SQLiteDatabase db, String tableName, String createTableQuery, String[] columnsToRemove) {
        final Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        final List<String> columnNames = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        cursor.close();
        columnNames.removeAll(Arrays.asList(columnsToRemove));
        final String columnsSeparated = TextUtils.join(",", columnNames);

        db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;");
        db.execSQL(createTableQuery);
        db.execSQL("INSERT INTO " + tableName + "(" + columnsSeparated + ") SELECT " + columnsSeparated + " FROM " + tableName + "_old;");
        db.execSQL("DROP TABLE " + tableName + "_old;");
    }
}