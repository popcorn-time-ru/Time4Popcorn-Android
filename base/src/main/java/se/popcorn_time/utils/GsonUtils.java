package se.popcorn_time.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class GsonUtils {

    private GsonUtils() {
    }

    @Nullable
    public static String getAsString(@NonNull JsonObject jsonObject, @NonNull String memberName) {
        return getAsString(jsonObject, memberName, null);
    }

    @Nullable
    public static String getAsString(@NonNull JsonObject jsonObject, @NonNull String memberName, @Nullable String defaultValue) {
        if (jsonObject.has(memberName)) {
            final JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement.isJsonNull()) {
                return null;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsString();
            }
        }
        return defaultValue;
    }

    public static long getAsLong(@NonNull JsonObject jsonObject, @NonNull String memberName) {
        return getAsLong(jsonObject, memberName, 0L);
    }

    public static long getAsLong(@NonNull JsonObject jsonObject, @NonNull String memberName, long defaultValue) {
        if (jsonObject.has(memberName)) {
            final JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement.isJsonNull()) {
                return 0L;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsLong();
            }
        }
        return defaultValue;
    }

    public static int getAsInt(@NonNull JsonObject jsonObject, @NonNull String memberName) {
        return getAsInt(jsonObject, memberName, 0);
    }

    public static int getAsInt(@NonNull JsonObject jsonObject, @NonNull String memberName, int defaultValue) {
        if (jsonObject.has(memberName)) {
            final JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement.isJsonNull()) {
                return 0;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsInt();
            }
        }
        return defaultValue;
    }

    public static boolean getAsBoolean(@NonNull JsonObject jsonObject, @NonNull String memberName) {
        return getAsBoolean(jsonObject, memberName, false);
    }

    public static boolean getAsBoolean(@NonNull JsonObject jsonObject, @NonNull String memberName, boolean defaultValue) {
        if (jsonObject.has(memberName)) {
            final JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement.isJsonNull()) {
                return false;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsBoolean();
            }
        }
        return defaultValue;
    }

    public static float getAsFloat(@NonNull JsonObject jsonObject, @NonNull String memberName) {
        return getAsFloat(jsonObject, memberName, 0f);
    }

    public static float getAsFloat(@NonNull JsonObject jsonObject, @NonNull String memberName, float defaultValue) {
        if (jsonObject.has(memberName)) {
            final JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement.isJsonNull()) {
                return 0f;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsFloat();
            }
        }
        return defaultValue;
    }

    public static double getAsDouble(@NonNull JsonObject jsonObject, @NonNull String memberName) {
        return getAsDouble(jsonObject, memberName, 0d);
    }

    public static double getAsDouble(@NonNull JsonObject jsonObject, @NonNull String memberName, double defaultValue) {
        if (jsonObject.has(memberName)) {
            final JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement.isJsonNull()) {
                return 0d;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsDouble();
            }
        }
        return defaultValue;
    }
}
