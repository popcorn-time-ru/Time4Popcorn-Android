package se.popcorn_time.model.messaging;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public final class MessagingUtils {

    private MessagingUtils() {
    }

    @Nullable
    public static IMessagingData.Action parse(@NonNull JSONObject json) {
        try {
            String actionName = json.getString("name");
            switch (actionName) {
                case OpenBrowserAction.NAME:
                    OpenBrowserAction action = new OpenBrowserAction();
                    action.setUrl(json.getString("url"));
                    return action;
                default:
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void action(@NonNull Context context, @NonNull IMessagingData.Action action) {
        if (action instanceof OpenBrowserAction) {
            openBrowser(context, (OpenBrowserAction) action);
        }
    }

    public static void openBrowser(@NonNull Context context, @NonNull OpenBrowserAction action) {
        if (TextUtils.isEmpty(action.getUrl())) {
            return;
        }
        String url = action.getUrl();
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
