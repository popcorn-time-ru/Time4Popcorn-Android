package se.popcorn_time.base.database;

import android.database.Cursor;

public final class DBUtils {

    private DBUtils() {
    }

    public static int getInt(Cursor cursor, String columnName, int defaultValue) {
        final int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getInt(columnIndex);
        }
        return defaultValue;
    }
}
