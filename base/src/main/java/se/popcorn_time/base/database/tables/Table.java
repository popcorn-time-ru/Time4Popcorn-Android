package se.popcorn_time.base.database.tables;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.Locale;

public abstract class Table implements BaseColumns {

    public static final String AUTHORITY = "dp.ws.popcorntime";

    public static final String BASE_DIR_TYPE = "vnd.android.cursor.dir/vnd.popcorn";
    public static final String BASE_ITEM_TYPE = "vnd.android.cursor.item/vnd.popcorn";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    @NonNull
    public static Uri getContentUri(@NonNull String tableName) {
        return BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();
    }

    @NonNull
    public static Uri getContentUri(@NonNull String tableName, long id) {
        return BASE_CONTENT_URI.buildUpon().appendPath(tableName).appendPath(Long.toString(id)).build();
    }

    @NonNull
    public static String getDirType(@NonNull String tableName) {
        return getType(BASE_DIR_TYPE, tableName);
    }

    @NonNull
    public static String getItemType(@NonNull String tableName) {
        return getType(BASE_ITEM_TYPE, tableName);
    }

    @NonNull
    private static String getType(@NonNull String baseType, @NonNull String tableName) {
        return String.format(Locale.ENGLISH, "%s.%s", baseType, tableName);
    }
}
