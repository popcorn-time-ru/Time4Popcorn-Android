package se.popcorn_time.model.messaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MessagingUseCase implements IMessagingUseCase {

    @Nullable
    private IMessagingUseCase.Observer observer;

    @Nullable
    private IMessagingUseCase.NotificationObserver notificationObserver;

    @Nullable
    private IMessagingData messagingData;

    private boolean messagingDataNotShown = false;

    public MessagingUseCase() {
    }

    @Override
    public IMessagingData getData() {
        return messagingData;
    }

    @Override
    public void subscribe(@NonNull Observer observer) {
        this.observer = observer;
        if (messagingDataNotShown && messagingData != null) {
            show(messagingData);
        }
    }

    @Override
    public void unsubscribe(@NonNull Observer observer) {
        if (observer.equals(this.observer)) {
            this.observer = null;
        }
    }

    @Override
    public void subscribe(@NonNull NotificationObserver observer) {
        this.notificationObserver = observer;
    }

    @Override
    public void show(@Nullable IMessagingData data) {
        this.messagingData = data;
        if (data instanceof IMessagingDialogData ||
                data instanceof IMessagingDialogHtmlData) {
            if (observer != null) {
                messagingDataNotShown = false;
                observer.onShowMessagingDialog(data);
            } else {
                messagingDataNotShown = true;
            }
        } else if (data instanceof IMessagingNotificationData) {
            if (notificationObserver != null) {
                notificationObserver.onShowMessagingNotification((IMessagingNotificationData) data);
            }
        }
    }
}
