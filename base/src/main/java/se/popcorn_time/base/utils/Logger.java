package se.popcorn_time.base.utils;

import android.text.TextUtils;
import android.util.Log;

public class Logger {

    private static String log_tag;

    private Logger() {

    }

    public static void init(String tag) {
        log_tag = tag;
    }

    public static void info(String msg) {
        if (log_tag != null && !TextUtils.isEmpty(msg)) {
            Log.i(log_tag, msg);
        }
    }

    public static void info(String msg, Throwable tr) {
        if (log_tag != null && !TextUtils.isEmpty(msg)) {
            Log.i(log_tag, msg, tr);
        }
    }

    public static void debug(String msg) {
        if (log_tag != null && !TextUtils.isEmpty(msg)) {
            Log.d(log_tag, msg);
        }
    }

    public static void debug(String msg, Throwable tr) {
        if (log_tag != null && !TextUtils.isEmpty(msg)) {
            Log.d(log_tag, msg, tr);
        }
    }

    public static void error(String msg) {
        if (log_tag != null && !TextUtils.isEmpty(msg)) {
            Log.e(log_tag, msg);
        }
    }

    public static void error(String msg, Throwable tr) {
        if (log_tag != null && !TextUtils.isEmpty(msg)) {
            Log.e(log_tag, msg, tr);
        }
    }
}