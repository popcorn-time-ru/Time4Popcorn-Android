package se.popcorn_time.mobile.ui;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.player.PlayerControl;
import com.player.dialog.ListItemEntity;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.libvlc.util.VLCUtil;
import org.videolan.vlc.util.VLCOptions;

import java.util.ArrayList;
import java.util.List;

import se.popcorn_time.base.analytics.Analytics;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.base.PlayerBaseActivity;

public class VLCPlayerActivity extends PlayerBaseActivity implements IVLCVout.Callback {

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;

    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    private boolean mPlaybackStarted = false;
    private long lastPosition = 0;

    private AudioManager audioManager;
    private int maxVolume;
    private boolean mHasAudioFocus = false;

    private final Handler vlc_handler = new Handler(Looper.getMainLooper());

    private View.OnLayoutChangeListener playerLayoutChangeListener = new View.OnLayoutChangeListener() {

        private final Runnable r = new Runnable() {

            @Override
            public void run() {
                changeSurfaceLayout();
            }
        };

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                vlc_handler.removeCallbacks(r);
                vlc_handler.post(r);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!VLCUtil.hasCompatibleCPU(getBaseContext())) {
            Logger.error("VLCPlayerActivity: Compatible cpu error.");
            finish();
            return;
        }

        LibVLC.setOnNativeCrashListener(new LibVLC.OnNativeCrashListener() {
            @Override
            public void onNativeCrash() {
                Logger.error("VLCPlayerActivity: Native crash.");
                finish();
            }
        });

        libVLC = new LibVLC(VLCPlayerActivity.this, VLCOptions.getLibOptions());
//        libVLC.setOnHardwareAccelerationError(new LibVLC.HardwareAccelerationError() {
//            @Override
//            public void eventHardwareAccelerationError() {
//                Logger.error("VLCPlayerActivity: Hardware acceleration error.");
//            }
//        });
        mediaPlayer = new MediaPlayer(libVLC);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initAspectRatio();
        setPlayerControl(vlcAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            stopPlayback();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        audioManager = null;
        mediaPlayer.release();
        mediaPlayer = null;
        libVLC.release();
        libVLC = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (!AndroidUtil.isHoneycombOrLater()) {
            changeSurfaceLayout();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onChosenAspectRatio() {
        changeSurfaceLayout();
    }

    private PlayerControl vlcAction = new PlayerControl() {

        @Override
        public long getLength() {
            return mediaPlayer != null ? (int) mediaPlayer.getLength() : 0;
        }

        @Override
        public long getPosition() {
            return mediaPlayer != null ? (int) mediaPlayer.getTime() : 0;
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        }

        @Override
        public void play() {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        }

        @Override
        public void pause() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }

        @Override
        public void seek(long position) {
            if (mediaPlayer != null) {
                float length = mediaPlayer.getLength();
                if (length == 0f) {
                    mediaPlayer.setTime(position);
                } else {
                    mediaPlayer.setPosition(position / length);
                }
            }
        }

        @Override
        public void volumeUp() {
            if (audioManager != null) {
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                currentVolume = Math.min(currentVolume + 1, maxVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                showVolumeInfo(currentVolume, maxVolume);
            }
        }

        @Override
        public void volumeDown() {
            if (audioManager != null) {
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                currentVolume = Math.max(currentVolume - 1, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                showVolumeInfo(currentVolume, maxVolume);
            }
        }
    };

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0) {
            return;
        }
        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mSarNum = sarNum;
        mSarDen = sarDen;
        changeSurfaceLayout();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {
        Log.e(TAG, "Error: onHardwareAccelerationError");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeSurfaceLayout() {
        int sw = getWindow().getDecorView().getWidth();
        int sh = getWindow().getDecorView().getHeight();

        if (mediaPlayer != null) {
            mediaPlayer.getVLCVout().setWindowSize(sw, sh);
        }

        double dw = sw, dh = sh;
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Logger.info("VLCPlayerActivity<changeSurfaceLayout>: Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mSarDen == mSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double) mSarNum / mSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (currentAspectRatioItem != null ? currentAspectRatioItem.getValue() : SURFACE_BEST_FIT) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = dw / ar;
                break;
            case SURFACE_FIT_VERTICAL:
                dw = dh * ar;
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }

        // set display size
        ViewGroup.LayoutParams lp = playerSurfaceView.getLayoutParams();
        lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        playerSurfaceView.setLayoutParams(lp);
        subtitlesView.setLayoutParams(lp);
        if (subtitlesSurfaceView != null) {
            subtitlesSurfaceView.setLayoutParams(lp);
        }

        playerSurfaceView.invalidate();
        subtitlesView.invalidate();
        if (subtitlesSurfaceView != null) {
            subtitlesSurfaceView.invalidate();
        }
    }

    private void startPlayback() {
        if (mPlaybackStarted || mediaPlayer == null) {
            return;
        }

        final IVLCVout vlcVout = mediaPlayer.getVLCVout();
        vlcVout.setVideoView(playerSurfaceView);
        if (subtitlesSurfaceView != null && subtitlesSurfaceView.getVisibility() == View.VISIBLE) {
            vlcVout.setSubtitlesView(subtitlesSurfaceView);
        }
        vlcVout.addCallback(VLCPlayerActivity.this);
        vlcVout.attachViews();
        mediaPlayer.setVideoTrackEnabled(true);

        mPlaybackStarted = true;

        playerSurfaceView.addOnLayoutChangeListener(playerLayoutChangeListener);
        changeSurfaceLayout();
        mediaPlayer.setEventListener(mediaPlayerEventListener);
        final Media media = new Media(libVLC, Uri.fromFile(videoFile));
        VLCOptions.setMediaOptions(media, VLCOptions.MEDIA_VIDEO, ((PopcornApplication) getApplication()).getSettingsUseCase().getPlayerHardwareAcceleration());
        media.setEventListener(mediaEventListener);
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.setVideoTitleDisplay(MediaPlayer.Position.Disable, 0);
        mediaPlayer.play();
    }

    private void stopPlayback() {
        if (!mPlaybackStarted) {
            return;
        }
        mPlaybackStarted = false;
        lastPosition = vlcAction.getPosition() - 5000;
        if (lastPosition < 1000) {
            lastPosition = 0;
        }
        if (mediaPlayer != null) {
            final IVLCVout vlcVout = mediaPlayer.getVLCVout();
            vlcVout.detachViews();
            vlcVout.removeCallback(VLCPlayerActivity.this);
            mediaPlayer.stop();
        }
        changeAudioFocus(false);
        playerSurfaceView.removeOnLayoutChangeListener(playerLayoutChangeListener);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void changeAudioFocus(boolean acquire) {
        if (audioManager == null) {
            return;
        }
        if (acquire) {
            if (!mHasAudioFocus) {
                final int result = audioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    audioManager.setParameters("bgm_state=true");
                    mHasAudioFocus = true;
                }
            }
        } else {
            if (mHasAudioFocus) {
                audioManager.abandonAudioFocus(mAudioFocusListener);
                audioManager.setParameters("bgm_state=false");
                mHasAudioFocus = false;
            }
        }
    }

    private void initAspectRatio() {
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_BEST_FIT) {
            @Override
            public String getName() {
                return getString(R.string.surface_best_fit);
            }
        });
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_FIT_HORIZONTAL) {
            @Override
            public String getName() {
                return getString(R.string.surface_fit_horizontal);
            }
        });
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_FIT_VERTICAL) {
            @Override
            public String getName() {
                return getString(R.string.surface_fit_vertical);
            }
        });
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_FILL) {
            @Override
            public String getName() {
                return getString(R.string.surface_fill);
            }
        });
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_16_9) {
            @Override
            public String getName() {
                return "16:9";
            }
        });
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_4_3) {
            @Override
            public String getName() {
                return "4:3";
            }
        });
        ListItemEntity.addItemToList(aspectRatioItems, new AspectRatioItem(SURFACE_ORIGINAL) {
            @Override
            public String getName() {
                return getString(R.string.surface_original);
            }
        });
        currentAspectRatioItem = aspectRatioItems.get(0);
    }

    private void initAudioTracks() {
        MediaPlayer.TrackDescription[] audioTracks = mediaPlayer.getAudioTracks();
        int currentAudioTrack = mediaPlayer.getAudioTrack();
        List<AudioItem> audioItems = new ArrayList<>();
        AudioItem currentAudioItem = null;
        for (MediaPlayer.TrackDescription td : audioTracks) {
            AudioItem audioItem = new AudioItem(td.id, td.name) {
                @Override
                public void onItemChosen() {
                    super.onItemChosen();
                    mediaPlayer.setAudioTrack(getValue());
                }
            };
            audioItems.add(audioItem);
            if (currentAudioTrack == td.id) {
                currentAudioItem = audioItem;
            }
        }
        initAudioTracks(audioItems, currentAudioItem);
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = !AndroidUtil.isFroyoOrLater() ? null : new AudioManager.OnAudioFocusChangeListener() {

        private boolean mLossTransient = false;
        private boolean mLossTransientCanDuck = false;

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    changeAudioFocus(false);
                    final Media media = mediaPlayer.getMedia();
                    if (media != null) {
                        media.setEventListener(null);
                        mediaPlayer.setEventListener(null);
                        mediaPlayer.stop();
                        mediaPlayer.setMedia(null);
                        media.release();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mediaPlayer.isPlaying()) {
                        mLossTransient = true;
                        mediaPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(36);
                        mLossTransientCanDuck = true;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mLossTransientCanDuck) {
                        mediaPlayer.setVolume(100);
                        mLossTransientCanDuck = false;
                    }
                    if (mLossTransient) {
                        mediaPlayer.play();
                        mLossTransient = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private final Media.EventListener mediaEventListener = new Media.EventListener() {

        @Override
        public void onEvent(Media.Event event) {
            switch (event.type) {
                case Media.Event.ParsedChanged:
                    Logger.debug("VLCPlayerActivity: Media event - ParsedChanged");
                    mediaPlayer.setTime(lastPosition);
                    updateMediaLength((int) mediaPlayer.getLength());
                    mediaPlayer.setSpuTrack(-1);
                    initAudioTracks();
                    break;
                case Media.Event.MetaChanged:
                    break;
                default:
                    break;
            }
        }
    };

    private final MediaPlayer.EventListener mediaPlayerEventListener = new MediaPlayer.EventListener() {

        private float lastProgress = 0;

        @Override
        public void onEvent(MediaPlayer.Event event) {
            switch (event.type) {
                case MediaPlayer.Event.Playing:
                    Logger.debug("VLCPlayerActivity: MediaPlayer event - Playing");
                    changeAudioFocus(true);
                    if (vlcAction == getPlayerControl()) {
                        eventMediaPlaying();
                    } else {
                        vlcAction.pause();
                    }
                    break;
                case MediaPlayer.Event.Paused:
                    Logger.debug("VLCPlayerActivity: MediaPlayer event - Paused");
                    if (vlcAction == getPlayerControl()) {
                        eventMediaPaused();
                    }
                    break;
                case MediaPlayer.Event.Stopped:
                    Logger.debug("VLCPlayerActivity: MediaPlayer event - Stopped");
                    changeAudioFocus(false);
                    break;
                case MediaPlayer.Event.EndReached:
                    Logger.debug("VLCPlayerActivity: MediaPlayer event - EndReached");
                    changeAudioFocus(false);
                    setResultVideoStop(mediaPlayer.getLength(), mediaPlayer.getLength());
                    finish();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    Logger.error("VLCPlayerActivity: MediaPlayer event - EncounteredError");
                    break;
                case MediaPlayer.Event.TimeChanged:
                    final long position = event.getTimeChanged();
                    updateSubtitles(position);
                    final float progress = (float) position / vlcAction.getLength();
                    if (progress >= 0.2f && lastProgress < 0.2f) {
                        Analytics.event(Analytics.Category.CONTENT, Analytics.Event.PROLONGED_WATCHING);
                    } else if (progress >= 0.9f && lastProgress < 0.9f) {
                        Analytics.event(Analytics.Category.CONTENT, Analytics.Event.FINISHING_WATCHING);
                    }
                    if (progress > lastProgress) {
                        lastProgress = progress;
                    }
                    break;
                case MediaPlayer.Event.PositionChanged:
                    break;
                case MediaPlayer.Event.Vout:
                    break;
                case MediaPlayer.Event.ESAdded:
                    break;
                case MediaPlayer.Event.ESDeleted:
                    break;
                default:
                    break;
            }
        }
    };
}