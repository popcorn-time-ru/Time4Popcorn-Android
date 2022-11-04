package com.player.subtitles.loader;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.player.subtitles.Caption;
import com.player.subtitles.RemoveTagsFormatter;
import com.player.subtitles.SubtitlesException;
import com.player.subtitles.format.Format;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;

public abstract class SubtitlesLoader<Source> {

    private static final String UTF_8 = "UTF-8";
    private static final String MACCYRILLIC = "MACCYRILLIC";
    private static final String WINDOWS_1256 = "Windows-1256";

    private UniversalDetector detector = new UniversalDetector(null);
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    public SubtitlesLoader() {

    }

    public abstract void load(@NonNull Source source, @NonNull File saveFile) throws IOException, SubtitlesException, InterruptedException;

    protected final void save(@NonNull InputStream data, @NonNull File saveFile, @NonNull Format format) throws IOException, SubtitlesException, InterruptedException {
        detector.reset();
        int count;
        byte[] buffer = new byte[1024];
        while ((count = data.read(buffer)) > 0) {
            if (!detector.isDone()) {
                detector.handleData(buffer, 0, count);
            }
            output.write(buffer, 0, count);
        }
        detector.dataEnd();

        String subtitleEncoding = detector.getDetectedCharset();
        detector.reset();
        if (TextUtils.isEmpty(subtitleEncoding)) {
            subtitleEncoding = UTF_8;
        } else if (MACCYRILLIC.equals(subtitleEncoding)) {
            subtitleEncoding = WINDOWS_1256; // for arabic
        }

        byte[] subtitle_utf_8 = new String(output.toByteArray(), Charset.forName(subtitleEncoding)).getBytes(UTF_8);
        String subtitle = new String(subtitle_utf_8, Charset.forName(UTF_8));

        BufferedReader reader = new BufferedReader(new StringReader(subtitle));
        Map<Integer, Caption> subs = format.parse(reader, new RemoveTagsFormatter());
        reader.close();

        if (Thread.interrupted()) {
            throw new InterruptedException("Subtitles saving was interrupted");
        }

        if (!saveFile.getParentFile().exists() && !saveFile.getParentFile().mkdirs()) {
            throw new SubtitlesException("Cannot create subtitles dirs: " + saveFile.getAbsolutePath());
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
        try {
            format.write(writer, subs);
            writer.close();
        } catch (IOException ioe) {
            writer.close();
            throw ioe;
        }
    }
}