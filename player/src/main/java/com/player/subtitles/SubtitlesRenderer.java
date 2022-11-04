package com.player.subtitles;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.player.subtitles.format.SRTFormat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public final class SubtitlesRenderer {

    public static final String[] SUPPORTED_EXTENSIONS = new String[]{
            SRTFormat.EXTENSION
    };

    private TextView subtitlesView;
    private String subtitlesPath;
    private Map<Integer, Caption> captions;
    private int index;

    public SubtitlesRenderer() {

    }

    public void setSubtitlesView(TextView subtitlesView) {
        this.subtitlesView = subtitlesView;
        if (subtitlesView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            subtitlesView.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }
    }

    public Map<Integer, Caption> getCaptions() {
        return captions;
    }

    public void disable() {
        subtitlesPath = null;
        captions = null;
        if (subtitlesView != null) {
            subtitlesView.setVisibility(View.GONE);
        }
    }

    public void setSubtitlesColor(int color) {
        if (subtitlesView != null) {
            subtitlesView.setTextColor(color);
        }
    }

    public void setSubtitlesSize(float size) {
        if (subtitlesView != null) {
            subtitlesView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void setSubtitlesTrack(@NonNull String filePath, final TextFormatter formatter) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        subtitlesPath = filePath;
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                try {
                    captions = new SRTFormat().parse(new BufferedReader(new FileReader(subtitlesPath)), formatter);
                    index = 1;
                } catch (Exception e) {
                    subtitlesPath = null;
                    captions = null;
                    e.printStackTrace();
                }
            }
        });
    }

    @Nullable
    public String getSubtitlesPath() {
        return subtitlesPath;
    }

    public void onUpdate(long timeMillis) {
        if (captions != null && subtitlesView != null) {
            Caption caption;
            if (captions.containsKey(index)) {
                if (timeMillis >= captions.get(index).startMillis && timeMillis <= captions.get(index).endMillis) {
                    caption = captions.get(index);
                } else {
                    caption = searchCaption(timeMillis);
                }
            } else {
                caption = searchCaption(timeMillis);
            }
            if (caption != null) {
                String subtitles = caption.text;
                subtitlesView.setText(subtitles);
                subtitlesView.setVisibility(View.VISIBLE);
            } else {
                subtitlesView.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    private Caption searchCaption(long timeMillis) {
        for (Integer key : captions.keySet()) {
            if (timeMillis >= captions.get(key).startMillis && timeMillis <= captions.get(key).endMillis) {
                index = key;
                return captions.get(key);
            }
        }
        return null;
    }
}