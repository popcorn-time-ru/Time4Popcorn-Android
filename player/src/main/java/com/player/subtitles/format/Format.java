package com.player.subtitles.format;

import com.player.subtitles.Caption;
import com.player.subtitles.SubtitlesException;
import com.player.subtitles.TextFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public interface Format {

    int BOM_UTF_8 = '\uFEFF';

    Map<Integer, Caption> parse(BufferedReader reader, TextFormatter formatter) throws IOException, SubtitlesException;

    void write(BufferedWriter writer, Map<Integer, Caption> captions) throws IOException;
}