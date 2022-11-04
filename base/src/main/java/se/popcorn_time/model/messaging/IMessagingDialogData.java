package se.popcorn_time.model.messaging;

public interface IMessagingDialogData extends IMessagingData {

    String getTitle();

    String getMessage();

    String getPositiveButton();

    String getNegativeButton();

    Action getAction();
}
