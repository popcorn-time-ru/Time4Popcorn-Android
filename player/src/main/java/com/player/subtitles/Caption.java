package com.player.subtitles;

import android.support.annotation.NonNull;

public final class Caption {

    protected final int startMillis;
    protected final int endMillis;
    protected final String text;

    private Caption(int startMillis, int endMillis, String text) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.text = text;
    }

    public int getStartMillis() {
        return startMillis;
    }

    public int getEndMillis() {
        return endMillis;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public static final class Builder {

        private int startMillis;
        private int endMillis;
        private String text;

        public Builder() {

        }

        public Builder setStartMillis(int startMillis) {
            this.startMillis = startMillis;
            return Builder.this;
        }

        public Builder setEndMillis(int endMillis) {
            this.endMillis = endMillis;
            return Builder.this;
        }

        public Builder setText(@NonNull String text) {
            this.text = text;
            return Builder.this;
        }

        @NonNull
        public Caption build() {
            return new Caption(startMillis, endMillis, text);
        }
    }
}