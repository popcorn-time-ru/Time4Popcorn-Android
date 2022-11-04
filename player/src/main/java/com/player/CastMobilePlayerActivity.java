package com.player;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.core.SubtitleInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DLNAService;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.sessions.LaunchSession;
import com.player.cast.CastDevicesDialog;
import com.player.cast.WebServerService;
import com.player.dialog.ConnectableDeviceControlDialog;
import com.player.dialog.ConnectableDeviceItem;
import com.player.dialog.ConnectableDevicesDialog;
import com.player.subtitles.SubtitlesUtils;
import com.player.subtitles.format.VTTFormat;

import java.net.UnknownHostException;
import java.util.List;

public abstract class CastMobilePlayerActivity extends MobilePlayerActivity {

    private ConnectableDevice connectableDevice;
    private LaunchSession launchSession;
    private MediaControl mediaControl;

    private MenuItem castMenuItem;

    private PlayerControl cachedPlayerControl;
    private long duration = 0L;
    private long position = 0L;
    private float volume = 0.0f;
    private int subtitlesForegroundColor = Color.parseColor("#ffffff");
    private float subtitlesFontScale = 1f;
    private boolean isPlaying = false;

    private ServiceSubscription<VolumeControl.VolumeListener> volumeSubscription;
    private ServiceSubscription<MediaControl.PlayStateListener> playStateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        teardown();

        DiscoveryManager.init(getApplicationContext());

        CapabilityFilter videoFilter = new CapabilityFilter(
                MediaPlayer.Play_Video,
                MediaControl.Any
        );

        DiscoveryManager.getInstance().setCapabilityFilters(videoFilter);
        DiscoveryManager.getInstance().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        teardown();

        DiscoveryManager.getInstance().stop();
        DiscoveryManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (connectableDevice == null) {
            closeDeviceControlDialog();
        }
    }

    private void teardown() {
        if (volumeSubscription != null) {
            volumeSubscription.unsubscribe();
            volumeSubscription = null;
        }
        if (playStateSubscription != null) {
            playStateSubscription.unsubscribe();
            playStateSubscription = null;
        }

        ConnectableNotificationService.stop(getBaseContext());

        if (mediaControl != null) {
            mediaControl.stop(null);
            mediaControl = null;
        } else {
            if (connectableDevice != null && launchSession != null) {
                MediaPlayer mediaPlayer = getMediaPlayer(connectableDevice);
                if (mediaPlayer != null) {
                    mediaPlayer.closeMedia(launchSession, null);
                }
            }
        }

        if (launchSession != null) {
            launchSession = null;
        }
        isPlaying = false;

        closeDeviceControlDialog();
        if (connectableDevice != null) {
            if (connectableDevice.isConnected()) {
                connectableDevice.disconnect();
                connectableDevice.removeListener(deviceListener);
            }
            setConnectableDevice(null);
        }

        if (cachedPlayerControl != null) {
            cachedPlayerControl.seek(position);
            if (isActivityPaused()) {
                cachedPlayerControl.pause();
            } else {
                cachedPlayerControl.play();
            }
        }
        setPlayerControl(cachedPlayerControl);

        WebServerService.stop(getApplicationContext());
    }

    private void closeDeviceControlDialog() {
        if (!isActivityPaused()) {
            Fragment deviceControlDialog = getSupportFragmentManager().findFragmentByTag("connectable_device_control_dialog");
            if (deviceControlDialog != null && deviceControlDialog instanceof DialogFragment) {
                ((DialogFragment) deviceControlDialog).dismiss();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_cast, menu);
        castMenuItem = menu.findItem(R.id.cast_device_picker);
        setConnectableDevice(connectableDevice);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.cast_device_picker == item.getItemId()) {
            if (connectableDevice != null) {
                new ConnectableDeviceControlDialog().show(getSupportFragmentManager(), new ConnectableDeviceItem(connectableDevice), "connectable_device_control_dialog");
            } else {
                showConnectableDevicesDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onChosenWithoutSubtitles() {
        castMedia();
    }

    @Override
    protected void onChosenSubtitles() {
        castMedia();
    }

    @Override
    protected void setSubtitlesForeground(int color) {
        super.setSubtitlesForeground(color);
        subtitlesForegroundColor = color;
    }

    @Override
    protected void setSubtitlesFontScale(float scale) {
        super.setSubtitlesFontScale(scale);
        subtitlesFontScale = scale;
    }

    @Override
    protected long getPlayerPosition() {
        updatePosition();
        return super.getPlayerPosition();
    }

    private void showConnectableDevicesDialog() {
        new ConnectableDevicesDialog().show(getSupportFragmentManager(), new CastDevicesDialog.CastDevicesListener<ConnectableDevice>() {

            @Override
            public void onDeviceSelected(@NonNull ConnectableDevice connectableDevice) {
                connectableDevice.addListener(deviceListener);
                connectableDevice.connect();
            }
        }, "connectable_devices_dialog");
    }

    private void setConnectableDevice(@Nullable ConnectableDevice connectableDevice) {
        if (castMenuItem != null) {
            if (connectableDevice != null) {
                castMenuItem.setIcon(R.drawable.ic_mr_button_connected_22_dark);
            } else {
                castMenuItem.setIcon(R.drawable.ic_mr_button_disconnected_dark);
            }
        }
        this.connectableDevice = connectableDevice;
    }

    private void castMedia() {
        if (connectableDevice != null) {
            final MediaPlayer mediaPlayer = getMediaPlayer(connectableDevice);
            if (mediaPlayer == null) {
                return;
            }

            setPlayerControl(null);
            if (playStateSubscription != null) {
                playStateSubscription.unsubscribe();
            }
            mediaControl = null;
            isPlaying = false;
            eventMediaPaused();

            if (launchSession != null) {
                mediaPlayer.closeMedia(launchSession, new ResponseListener<Object>() {


                    @Override
                    public void onSuccess(Object object) {
                        playMedia(mediaPlayer, connectableDevice);
                    }

                    @Override
                    public void onError(ServiceCommandError error) {
                        teardown();
                    }
                });
                launchSession = null;
            } else {
                playMedia(mediaPlayer, connectableDevice);
            }
        }
    }

    private void playMedia(@NonNull MediaPlayer mediaPlayer, @NonNull final ConnectableDevice device) {
        try {
            int folderIndex = videoFile.getAbsolutePath().lastIndexOf('/');
            String rootDir = videoFile.getAbsolutePath().substring(0, folderIndex);
            String fileName = videoFile.getAbsolutePath().substring(folderIndex + 1);
            String host = WebServerService.start(getApplicationContext(), rootDir);

            MediaInfo.Builder mediaInfoBuilder = new MediaInfo.Builder(host + "/" + fileName, "video/*")
                    .setTitle(fileName);

            String subtitlesPath = null;
            String mimeType = null;
            if (device.hasCapability(MediaPlayer.Subtitle_SRT)) {
                subtitlesPath = subtitlesRenderer.getSubtitlesPath();
                mimeType = "text/srt";
            } else if (device.hasCapability(MediaPlayer.Subtitle_WebVTT)) {
                subtitlesPath = getVTTSubtitlesPath();
                mimeType = "text/vtt";
            }
            Log.d(TAG, "Subtitles path: " + subtitlesPath);
            if (!TextUtils.isEmpty(subtitlesPath)) {
                if (subtitlesPath.startsWith(rootDir)) {
                    subtitlesPath = subtitlesPath.substring(rootDir.length() + 1);
                }
                String url = host + "/" + subtitlesPath;
                Log.d(TAG, "Subtitles url: " + url);
                mediaInfoBuilder.setSubtitleInfo(new SubtitleInfo.Builder(url)
                        .setLabel(subtitlesPath)
                        .setMimeType(mimeType)
                        .setForegroundColor(subtitlesForegroundColor)
                        .setFontScale(subtitlesFontScale)
                        .build());
            }

            mediaPlayer.playMedia(mediaInfoBuilder.build(), false, new MediaPlayer.LaunchListener() {

                @Override
                public void onSuccess(MediaPlayer.MediaLaunchObject object) {
                    launchSession = object.launchSession;
                    mediaControl = object.mediaControl;
                    Log.d("pt_mobile", "CastMobilePlayerActivity<MediaControl>: " + mediaControl);

                    isPlaying = true;
                    eventMediaPlaying();
                    setPlayerControl(castPlayer);

                    if (mediaControl instanceof DeviceService) {
                        ConnectableNotificationService.start(getBaseContext(), device, (DeviceService) mediaControl, CastMobilePlayerActivity.this.getClass());
                    } else {
                        ConnectableNotificationService.start(getBaseContext(), device, null, CastMobilePlayerActivity.this.getClass());
                    }

                    playStateSubscription = mediaControl.subscribePlayState(new MediaControl.PlayStateListener() {

                        @Override
                        public void onSuccess(MediaControl.PlayStateStatus object) {
                            if (MediaControl.PlayStateStatus.Playing == object || MediaControl.PlayStateStatus.Buffering == object) {
                                isPlaying = true;
                                eventMediaPlaying();
                            } else {
                                isPlaying = false;
                                eventMediaPaused();
                                if (MediaControl.PlayStateStatus.Finished == object) {
                                    setResultVideoStop(duration, duration);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onError(ServiceCommandError error) {

                        }
                    });
                    mediaControl.getDuration(new MediaControl.DurationListener() {

                        @Override
                        public void onSuccess(Long object) {
                            duration = object;
                        }

                        @Override
                        public void onError(ServiceCommandError error) {

                        }
                    });
                    if (position > 1000) {
                        mediaControl.seek(position, null);
                    }
                }

                @Override
                public void onError(ServiceCommandError error) {
                    showCastErrorDialog();
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
            teardown();
        }
    }

    @Nullable
    private String getVTTSubtitlesPath() {
        String subtitlesPath = SubtitlesUtils.generateSubtitlePath(videoFile.getAbsolutePath(), VTTFormat.EXTENSION);
        if (writeCurrentSubtitles(new VTTFormat(), subtitlesPath)) {
            return subtitlesPath;
        }
        return null;
    }

    private void showCastErrorDialog() {
        teardown();
        ErrorDialog dialog = (ErrorDialog) getSupportFragmentManager().findFragmentByTag("cast_error_dialog");
        if (dialog == null) {
            dialog = new ErrorDialog();
        }
        if (!dialog.isAdded() && !isFinishing()) {
            dialog.show(getSupportFragmentManager(), "cast_error_dialog");
        }
    }

    public static final class ErrorDialog extends DialogFragment implements DialogInterface.OnClickListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle("Something went wrong");
            builder.setMessage("The video could not play on your streaming device.\nWould you like to try again?");
            builder.setPositiveButton("Yes", ErrorDialog.this);
            builder.setNegativeButton("No", ErrorDialog.this);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (getActivity() instanceof CastMobilePlayerActivity) {
                        ((CastMobilePlayerActivity) getActivity()).showConnectableDevicesDialog();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    }

    @Nullable
    private MediaPlayer getMediaPlayer(@NonNull ConnectableDevice connectableDevice) {
        for (DeviceService service : connectableDevice.getServices()) {
            if (service instanceof DLNAService) {
                return ((DLNAService) service).getMediaPlayer();
            }
        }
        return connectableDevice.getCapability(MediaPlayer.class);
    }

    @Nullable
    private VolumeControl getVolumeControl(@NonNull ConnectableDevice connectableDevice) {
        if (connectableDevice.hasCapabilities(VolumeControl.Any)) {
            return connectableDevice.getCapability(VolumeControl.class);
        }
        return null;
    }

    private void updatePosition() {
        if (mediaControl != null) {
            mediaControl.getPosition(new MediaControl.PositionListener() {

                @Override
                public void onSuccess(Long object) {
                    position = object;
                }

                @Override
                public void onError(ServiceCommandError error) {

                }
            });
        }
    }

    private final ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {

        @Override
        public void onDeviceReady(ConnectableDevice device) {
            setConnectableDevice(device);
            VolumeControl volumeControl = getVolumeControl(device);
            if (volumeControl != null) {
                volumeControl.getVolume(new VolumeControl.VolumeListener() {
                    @Override
                    public void onSuccess(Float object) {
                        volume = object;
                    }

                    @Override
                    public void onError(ServiceCommandError error) {

                    }
                });
                volumeSubscription = volumeControl.subscribeVolume(new VolumeControl.VolumeListener() {

                    @Override
                    public void onSuccess(Float object) {
                        volume = object;
                        showVolumeInfo((int) (object * 100), 100);
                    }

                    @Override
                    public void onError(ServiceCommandError error) {

                    }
                });
            }

            cachedPlayerControl = getPlayerControl();
            position = 0;
            if (cachedPlayerControl != null) {
                if (cachedPlayerControl.isPlaying()) {
                    cachedPlayerControl.pause();
                }
                position = cachedPlayerControl.getPosition();
            }

            castMedia();
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            teardown();
        }

        @Override
        public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {

        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

        }

        @Override
        public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
            showCastErrorDialog();
        }
    };

    private final PlayerControl castPlayer = new PlayerControl() {

        @Override
        public long getLength() {
            return duration;
        }

        @Override
        public long getPosition() {
            return position;
        }

        @Override
        public boolean isPlaying() {
            return isPlaying;
        }

        @Override
        public void play() {
            if (mediaControl != null) {
                mediaControl.play(null);
            }
        }

        @Override
        public void pause() {
            if (mediaControl != null) {
                mediaControl.pause(null);
            }
        }

        @Override
        public void seek(long position) {
            if (mediaControl != null) {
                mediaControl.seek(position, null);
            }
        }

        @Override
        public void volumeUp() {
            if (connectableDevice == null) {
                return;
            }
            VolumeControl volumeControl = getVolumeControl(connectableDevice);
            if (volumeControl != null) {
                volumeControl.setVolume(Math.min(volume + 0.02f, 1.0f), null);
            }
        }

        @Override
        public void volumeDown() {
            if (connectableDevice == null) {
                return;
            }
            VolumeControl volumeControl = getVolumeControl(connectableDevice);
            if (volumeControl != null) {
                volumeControl.setVolume(Math.max(volume - 0.02f, 0.0f), null);
            }
        }
    };
}