package se.popcorn_time.model.messaging;

public interface IMessagingNotificationData extends IMessagingData {

    String getTitle();

    String getMessage();

    Action getAction();
}
