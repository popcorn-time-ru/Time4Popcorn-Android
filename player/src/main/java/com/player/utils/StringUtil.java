package com.player.utils;

public class StringUtil {

    public static final String TIME_FORMAT_2_DIGITS = "%02d:%02d:%02d"; // 00:00:00
    public static final String TIME_SRT_FORMAT = "%02d:%02d:%02d,%03d"; // 00:00:00,000
    public static final String TIME_VTT_FORMAT = "%02d:%02d:%02d.%03d"; // 00:00:00.000

    public static String millisToString(long time, String format) {
        if (time < 1000) {
            return String.format(format, 0, 0, 0, time);
        }
        long millis = time % 1000;
        time /= 1000;
        long sec = time % 60;
        time /= 60;
        long min = time % 60;
        time /= 60;
        return String.format(format, time, min, sec, millis);
    }
}