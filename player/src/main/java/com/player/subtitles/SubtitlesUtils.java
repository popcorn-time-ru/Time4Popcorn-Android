package com.player.subtitles;

import android.text.TextUtils;
import android.util.Log;

import com.player.subtitles.loader.FileSubtitlesLoader;
import com.player.subtitles.loader.UrlSubtitlesLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SubtitlesUtils {

    public static final String WITHOUT_SUBTITLES = "without-subtitles";
    public static final String CUSTOM_SUBTITLES = "custom-subtitles";

    public static String generateSubtitlePath(String videoPath, String extension) {
        return videoPath.substring(0, videoPath.lastIndexOf(".") + 1) + extension;
    }

    public static void load(String subtitlesPath, String savePath) throws InterruptedException, SubtitlesException, IOException {
        if (TextUtils.isEmpty(subtitlesPath)) {
            throw new SubtitlesException("Empty subtitles path");
        }
        if (TextUtils.isEmpty(savePath)) {
            throw new SubtitlesException("Empty save path");
        }
        Log.d("pt_mobile", "SubtitlesUtils<load>: " + subtitlesPath);
        if (subtitlesPath.startsWith("http://") || subtitlesPath.startsWith("https://")) {
            new UrlSubtitlesLoader().load(new URL(subtitlesPath), new File(savePath));
        } else if (subtitlesPath.startsWith("file://")) {
            new FileSubtitlesLoader().load(new File(subtitlesPath.substring(7)), new File(savePath));
        } else {
            throw new SubtitlesException("Not supported subtitles path: " + subtitlesPath);
        }
    }
}