package se.popcorn_time.base.analytics;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import se.popcorn_time.base.utils.Logger;

public class Analytics {

    public interface Category {
        String UI = "ui";
        String CONTENT = "content";
    }

    public interface Event {
        String MENU_SHARE_BUTTON_IS_CLICKED = "menu-share-button-is-clicked";
        String SHARE_DIALOG_IS_SHOWN = "share-dialog-is-shown";
        String SHARE_DIALOG_IS_CANCELED = "share-dialog-is-canceled";
        String SHARE_DIALOG_SHARE_IS_CLICKED = "share-dialog-share-is-clicked";
        String SHARE_VIDEO_DIALOG_IS_SHOWN = "share-video-dialog-is-shown";
        String SHARE_VIDEO_DIALOG_IS_CANCELED = "share-video-dialog-is-canceled";
        String SHARE_VIDEO_DIALOG_SHARE_IS_CLICKED = "share-video-dialog-share-is-clicked";
        String SHARE_FOCUS_DIALOG_IS_SHOWN = "share-focus-dialog-is-shown";
        String SHARE_FOCUS_DIALOG_IS_CANCELED = "share-focus-dialog-is-canceled";
        String SHARE_FOCUS_DIALOG_SHARE_IS_CLICKED = "share-focus-dialog-share-is-clicked";

        String DOWNLOAD_START = "download_start";
        String WATCHING_START = "watching_start";
        String PROLONGED_WATCHING = "prolonged_watching";
        String FINISHING_WATCHING = "finishing_watching";
    }

    private static final Analytics INSTANCE = new Analytics();

    private GoogleAnalytics googleAnalytics;
    private Tracker tracker;

    private Analytics() {

    }

    public static void init(Context context, String trackerId, boolean dryRun) {
        if (TextUtils.isEmpty(trackerId)) {
            Logger.error("Analytics: Initialization error. Tracker id is empty.");
            return;
        }

        INSTANCE.googleAnalytics = GoogleAnalytics.getInstance(context);
        INSTANCE.googleAnalytics.setDryRun(dryRun);
        INSTANCE.googleAnalytics.setLocalDispatchPeriod(1800);
        INSTANCE.tracker = INSTANCE.googleAnalytics.newTracker(trackerId);
        INSTANCE.tracker.setAnonymizeIp(dryRun);
        INSTANCE.tracker.enableExceptionReporting(true);
        INSTANCE.tracker.enableAutoActivityTracking(true);
        INSTANCE.tracker.enableAdvertisingIdCollection(false);
        Logger.debug("Analytics: Initialization completed");
    }

    public static void event(String category, String action) {
        if (INSTANCE.tracker == null) {
            return;
        }
        INSTANCE.tracker.send(new HitBuilders.EventBuilder(category, action).build());
    }
}