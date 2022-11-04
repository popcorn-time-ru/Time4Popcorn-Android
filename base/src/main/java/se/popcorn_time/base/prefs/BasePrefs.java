package se.popcorn_time.base.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public abstract class BasePrefs {

    private SharedPreferences prefs;

    public BasePrefs(Context context) {
        prefs = context.getSharedPreferences(getPrefsName(), Context.MODE_PRIVATE);
    }

    protected abstract String getPrefsName();

    public final boolean contains(String key) {
        return prefs.contains(key);
    }

    public final boolean remove(String key) {
        return prefs.edit().remove(key).commit();
    }

    public final void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public final void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /*
    * Put
    * */

    public final boolean put(String key, boolean value) {
        return prefs.edit().putBoolean(key, value).commit();
    }

    public final boolean put(String key, float value) {
        return prefs.edit().putFloat(key, value).commit();
    }

    public final boolean put(String key, int value) {
        return prefs.edit().putInt(key, value).commit();
    }

    public final boolean put(String key, long value) {
        return prefs.edit().putLong(key, value).commit();
    }

    public final boolean put(String key, String value) {
        return prefs.edit().putString(key, value).commit();
    }

    public final boolean put(String key, Set<String> values) {
        return prefs.edit().putStringSet(key, values).commit();
    }

    /*
    * Get
    * */

    public final boolean get(String key, boolean defaultValue) {
        try {
            return prefs.getBoolean(key, defaultValue);
        } catch (ClassCastException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public final float get(String key, float defaultValue) {
        try {
            return prefs.getFloat(key, defaultValue);
        } catch (ClassCastException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public final int get(String key, int defaultValue) {
        try {
            return prefs.getInt(key, defaultValue);
        } catch (ClassCastException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public final long get(String key, long defaultValue) {
        try {
            return prefs.getLong(key, defaultValue);
        } catch (ClassCastException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public final String get(String key, String defaultValue) {
        try {
            return prefs.getString(key, defaultValue);
        } catch (ClassCastException e) {
            put(key, defaultValue);
        }
        return defaultValue;
    }

    public final Set<String> get(String key, Set<String> defaultValues) {
        try {
            return prefs.getStringSet(key, defaultValues);
        } catch (ClassCastException e) {
            put(key, defaultValues);
        }
        return defaultValues;
    }
}