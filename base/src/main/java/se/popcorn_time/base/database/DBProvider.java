package se.popcorn_time.base.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.database.tables.Favorites;
import se.popcorn_time.base.database.tables.History;
import se.popcorn_time.base.database.tables.Table;

public class DBProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final SparseArray<String> TYPES = new SparseArray<>();
    private static final SparseArray<String> TABLES = new SparseArray<>();

    static {
        add(Favorites.NAME, 100, 101);
        add(Downloads.NAME, 200, 201);
        add(History.NAME, 300, 301);
    }

    private DBHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return TYPES.get(URI_MATCHER.match(uri));
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final String tableName = TABLES.get(URI_MATCHER.match(uri));
        if (tableName == null) {
            return null;
        }
        final Cursor cursor = mHelper.getReadableDatabase().query(tableName, projection, selection(uri, selection), selectionArgs, null, null, sortOrder);
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final String tableName = TABLES.get(URI_MATCHER.match(uri));
        if (tableName == null) {
            return null;
        }
        final long id = mHelper.getWritableDatabase().insert(tableName, null, values);
        if (id == -1) {
            return null;
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Table.getContentUri(tableName, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final String tableName = TABLES.get(URI_MATCHER.match(uri));
        if (tableName == null) {
            return 0;
        }
        final int count = mHelper.getWritableDatabase().delete(tableName, selection(uri, selection), selectionArgs);
        if (count > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final String tableName = TABLES.get(URI_MATCHER.match(uri));
        if (tableName == null) {
            return 0;
        }
        final int count = mHelper.getWritableDatabase().update(tableName, values, selection(uri, selection), selectionArgs);
        if (count > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    private static void add(@NonNull String tableName, int tableId, int itemId) {
        URI_MATCHER.addURI(Table.AUTHORITY, tableName, tableId);
        URI_MATCHER.addURI(Table.AUTHORITY, tableName + "/#", itemId);
        TYPES.put(tableId, Table.getDirType(tableName));
        TYPES.put(itemId, Table.getItemType(tableName));
        TABLES.put(tableId, tableName);
        TABLES.put(itemId, tableName);
    }

    @Nullable
    private String selection(@NonNull Uri uri, @Nullable String selection) {
        final String type = getType(uri);
        if (type != null && type.startsWith(Table.BASE_ITEM_TYPE)) {
            if (TextUtils.isEmpty(selection)) {
                selection = BaseColumns._ID + "=" + uri.getLastPathSegment();
            } else {
                selection += " AND " + BaseColumns._ID + "=" + uri.getLastPathSegment();
            }
        }
        return selection;
    }
}
