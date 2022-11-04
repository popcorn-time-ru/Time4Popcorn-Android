package com.player.subtitles;

public class RemoveTagsFormatter implements TextFormatter {

    @Override
    public String format(String text) {
        return text.replaceAll("<[^>]*>", "");
    }
}