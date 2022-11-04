package com.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.player.cast.CastDeviceReceiver;

public class ConnectableNotificationService extends Service {

    public static final String ACTION_START = "com.player.action.START";

    public static final String EXTRA_IP_ADDRESS = "ip-address";
    public static final String EXTRA_SERVICE_NAME = "service-name";
    public static final String EXTRA_NOTIFICATION_CONTENT_CLASS_NAME = "notification-content-class-name";

    private ConnectableDevice device;
    private MediaControl control;
    private String notificationContentClassName;

    private ServiceSubscription<MediaControl.PlayStateListener> playStateSubscription;

    @Override
    public void onCreate() {
        super.onCreate();

        connectableReceiver.register(ConnectableNotificationService.this, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    String ip = intent.getStringExtra(EXTRA_IP_ADDRESS);
                    device = DiscoveryManager.getInstance().getCompatibleDevices().get(ip);
                    if (device != null) {
                        if (intent.hasExtra(EXTRA_SERVICE_NAME)) {
                            DeviceService service = device.getServiceByName(intent.getStringExtra(EXTRA_SERVICE_NAME));
                            if (service != null) {
                                control = service.getAPI(MediaControl.class);
                            }
                        } else {
                            control = device.getCapability(MediaControl.class);
                        }
                        if (control != null) {
                            playStateSubscription = control.subscribePlayState(playStateListener);
                        }
                        Log.d("pt_mobile", "ConnectableNotificationService<MediaControl>: " + control);
                    }
                    notificationContentClassName = intent.getStringExtra(EXTRA_NOTIFICATION_CONTENT_CLASS_NAME);
                    break;
                default:
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (playStateSubscription != null) {
            playStateSubscription.unsubscribe();
            playStateSubscription = null;
        }

        if (device != null) {
            device.disconnect();
            device = null;
        }

        connectableReceiver.unregister(ConnectableNotificationService.this);

        closeNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(boolean playing) {
        if (device == null) {
            return;
        }

        try {
            android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(ConnectableNotificationService.this)
                    .setSmallIcon(R.drawable.ic_mr_button_connected_22_dark)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon))
                    .setContentTitle(device.getFriendlyName())
                    .setContentText(device.getConnectedServiceNames())

                    .setShowWhen(false)
                    .setOngoing(true);

            builder.setColor(Color.parseColor("#1565C0"));

            if (!TextUtils.isEmpty(notificationContentClassName)) {
                Intent uiIntent = new Intent();
                uiIntent.setComponent(new ComponentName(getBaseContext(), notificationContentClassName));
                PendingIntent uiPendingIntent = PendingIntent.getActivity(ConnectableNotificationService.this,
                        0,
                        uiIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(uiPendingIntent);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }

            if (playing) {
                PendingIntent pauseIntent = PendingIntent.getBroadcast(ConnectableNotificationService.this,
                        1,
                        new Intent(CastDeviceReceiver.ACTION_PAUSE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(R.drawable.ic_pause_white_36dp, null, pauseIntent);
            } else {
                PendingIntent playIntent = PendingIntent.getBroadcast(ConnectableNotificationService.this,
                        1,
                        new Intent(CastDeviceReceiver.ACTION_PLAY),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(R.drawable.ic_play_arrow_white_36dp, null, playIntent);
            }
            PendingIntent disconnectIntent = PendingIntent.getBroadcast(ConnectableNotificationService.this,
                    2,
                    new Intent(CastDeviceReceiver.ACTION_DISCONNECT),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_close_white_36dp, null, disconnectIntent);

            NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
            style.setShowActionsInCompactView(0, 1);
            builder.setStyle(style);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1112, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1112);
    }

    private final CastDeviceReceiver connectableReceiver = new CastDeviceReceiver() {

        @Override
        protected void play() {
            if (control != null) {
                control.play(null);
            }
        }

        @Override
        protected void pause() {
            if (control != null) {
                control.pause(null);
            }
        }

        @Override
        protected void disconnect() {
            stopSelf();
        }
    };

    private final MediaControl.PlayStateListener playStateListener = new MediaControl.PlayStateListener() {

        @Override
        public void onSuccess(MediaControl.PlayStateStatus object) {
            if (MediaControl.PlayStateStatus.Playing == object || MediaControl.PlayStateStatus.Buffering == object) {
                showNotification(true);
            } else {
                showNotification(false);
            }
        }

        @Override
        public void onError(ServiceCommandError error) {

        }
    };

    public static void start(@NonNull Context context, @NonNull ConnectableDevice connectableDevice, @Nullable DeviceService service,
                             @Nullable Class<? extends CastMobilePlayerActivity> cls) {
        Intent intent = new Intent(context, ConnectableNotificationService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_IP_ADDRESS, connectableDevice.getIpAddress());
        if (service != null) {
            intent.putExtra(EXTRA_SERVICE_NAME, service.getServiceName());
        }
        if (cls != null) {
            intent.putExtra(EXTRA_NOTIFICATION_CONTENT_CLASS_NAME, cls.getName());
        }
        context.startService(intent);
    }

    public static void stop(@NonNull Context context) {
        context.stopService(new Intent(context, ConnectableNotificationService.class));
    }
}