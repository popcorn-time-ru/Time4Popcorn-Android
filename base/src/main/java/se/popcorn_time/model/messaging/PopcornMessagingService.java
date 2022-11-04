package se.popcorn_time.model.messaging;

import android.support.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.base.utils.Logger;

public final class PopcornMessagingService extends FirebaseMessagingService {

    private IMessagingUseCase firebaseMessagingUseCase;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseMessagingUseCase = ((IPopcornApplication) getApplication()).getMessagingUseCase();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (data == null) {
            return;
        }

        Logger.debug("========== PopcornMessagingService<data> ===============");
        for (String key : data.keySet()) {
            Logger.debug(key + ": " + data.get(key));
        }
        Logger.debug("======================================================== ");
        Logger.debug("PopcornMessagingService<getBody()> :" + remoteMessage.getNotification().getBody());
        Logger.debug("PopcornMessagingService<getTitle()> :" + remoteMessage.getNotification().getTitle());

        if (data.containsKey(KEY_NOTIFICATION)) {
            firebaseMessagingUseCase.show(buildNotificationData(data.get(KEY_NOTIFICATION), null, null));
        }
        if (data.containsKey(KEY_DIALOG)) {
            firebaseMessagingUseCase.show(buildDialogData(data.get(KEY_DIALOG), null, null));
        } else if (data.containsKey(KEY_DIALOG_HTML)) {
            firebaseMessagingUseCase.show(buildDialogHtmlData(data.get(KEY_DIALOG_HTML)));
        }
    }

    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_DIALOG = "dialog";
    public static final String KEY_DIALOG_HTML = "dialog_html";

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_ACTION = "action";

    private static final String KEY_DIALOG_HTML_URL = "url";

    /*
    {
        "title": "Title Notification",
        "message": "Test message notification",
        "action": {
            "name": "openBrowser",
            "url": "http://google.com"
        }
    }
    */
    @Nullable
    public static IMessagingNotificationData buildNotificationData(String data, String title, String message) {
        try {
            JSONObject json = new JSONObject(data);
            NotificationData notificationData = new NotificationData();
            notificationData.setTitle(json.has(KEY_TITLE) ? json.getString(KEY_TITLE) : title);
            notificationData.setMessage(json.has(KEY_MESSAGE) ? json.getString(KEY_MESSAGE) : message);
            notificationData.setAction(json.has(KEY_ACTION) ? MessagingUtils.parse(json.getJSONObject(KEY_ACTION)) : null);
            return notificationData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    {
        "title": "Title",
        "message": "Test message",
        "positiveButton": "Ok",
        "negativeButton": "Cancel",
        "action": {
            "name": "openBrowser",
            "url": "http://google.com"
        }
    }
    */
    @Nullable
    public static IMessagingDialogData buildDialogData(String data, String title, String message) {
        try {
            JSONObject json = new JSONObject(data);
            DialogData dialogData = new DialogData();
            dialogData.setTitle(json.has(KEY_TITLE) ? json.getString(KEY_TITLE) : title);
            dialogData.setMessage(json.has(KEY_MESSAGE) ? json.getString(KEY_MESSAGE) : message);
            dialogData.setPositiveButton(json.has(KEY_POSITIVE_BUTTON) ? json.getString(KEY_POSITIVE_BUTTON) : null);
            dialogData.setNegativeButton(json.has(KEY_NEGATIVE_BUTTON) ? json.getString(KEY_NEGATIVE_BUTTON) : null);
            dialogData.setAction(json.has(KEY_ACTION) ? MessagingUtils.parse(json.getJSONObject(KEY_ACTION)) : null);
            return dialogData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    {
        "url":"http://google.com"
    }
    */
    @Nullable
    public static IMessagingDialogHtmlData buildDialogHtmlData(String data) {
        try {
            JSONObject json = new JSONObject(data);
            DialogHtmlData dialogHtmlData = new DialogHtmlData();
            dialogHtmlData.setUrl(json.getString(KEY_DIALOG_HTML_URL));
            return dialogHtmlData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final class NotificationData implements IMessagingNotificationData {

        private String title;
        private String message;
        private Action action;

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public Action getAction() {
            return action;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setAction(Action action) {
            this.action = action;
        }
    }

    private static final class DialogData implements IMessagingDialogData {

        private String title;
        private String message;
        private String positiveButton;
        private String negativeButton;
        private Action action;

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getPositiveButton() {
            return positiveButton;
        }

        @Override
        public String getNegativeButton() {
            return negativeButton;
        }

        @Override
        public Action getAction() {
            return action;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setPositiveButton(String positiveButton) {
            this.positiveButton = positiveButton;
        }

        public void setNegativeButton(String negativeButton) {
            this.negativeButton = negativeButton;
        }

        public void setAction(Action action) {
            this.action = action;
        }
    }

    private static final class DialogHtmlData implements IMessagingDialogHtmlData {

        private String url;

        @Override
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
