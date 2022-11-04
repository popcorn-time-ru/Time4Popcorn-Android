package com.player.subtitles.format;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.player.subtitles.Caption;
import com.player.subtitles.SubtitlesException;
import com.player.subtitles.TextFormatter;
import com.player.utils.StringUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class SRTFormat implements Format {

    public static final String EXTENSION = "srt";

    private final String TIME_DELIMITER = "-->";

    private StringBuilder builder = new StringBuilder();

    @Override
    public Map<Integer, Caption> parse(@NonNull BufferedReader reader, TextFormatter formatter) throws IOException, SubtitlesException {
        reader.mark(Integer.MAX_VALUE);
        if (BOM_UTF_8 != reader.read()) {
            reader.reset();
        }

        Map<Integer, Caption> captions = new TreeMap<>();
        int index = 1;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(TIME_DELIMITER)) {
                String[] times = line.split(TIME_DELIMITER);
                if (times.length == 2) {
                    try {
                        Caption.Builder captionBuilder = new Caption.Builder()
                                .setStartMillis(parseTime(times[0]))
                                .setEndMillis(parseTime(times[1]));
                        builder.setLength(0);
                        while (true) {
                            line = reader.readLine();
                            if (TextUtils.isEmpty(line)) {
                                break;
                            } else {
                                if (builder.length() > 0) {
                                    builder.append("\n");
                                }
                                builder.append(line);
                            }
                        }
                        captionBuilder.setText(formatter != null ? formatter.format(builder.toString()) : builder.toString());
                        captions.put(index, captionBuilder.build());
                        index++;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return captions;
    }

    @Override
    public void write(@NonNull BufferedWriter writer, Map<Integer, Caption> captions) throws IOException {
        writer.write(BOM_UTF_8);
        for (Integer key : captions.keySet()) {
            writer.write(Integer.toString(key));
            writer.newLine();

            builder.setLength(0);
            builder.append(StringUtil.millisToString(captions.get(key).getStartMillis(), StringUtil.TIME_SRT_FORMAT));
            builder.append(" ");
            builder.append(TIME_DELIMITER);
            builder.append(" ");
            builder.append(StringUtil.millisToString(captions.get(key).getEndMillis(), StringUtil.TIME_SRT_FORMAT));
            writer.write(builder.toString());
            writer.newLine();

            writer.write(captions.get(key).getText());
            writer.newLine();
            writer.newLine();
        }
    }

    private int parseTime(String time) throws IllegalArgumentException {
        // 00:00:00,000
        time = time.trim();
        if (time.length() != 12) {
            throw new IllegalArgumentException("Wrong time length: " + time.length() + ", time: " + time);
        }
        int h = Integer.parseInt(time.substring(0, 2));
        int m = Integer.parseInt(time.substring(3, 5));
        int s = Integer.parseInt(time.substring(6, 8));
        int ms = Integer.parseInt(time.substring(9, 12));
        return h * 3600000 + m * 60000 + s * 1000 + ms;
    }
}