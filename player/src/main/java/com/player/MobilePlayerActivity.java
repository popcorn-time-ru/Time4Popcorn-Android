package com.player;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.player.base.NumberPickerDialogListener;
import com.player.dialog.FileChooserDialog;
import com.player.dialog.ListItemEntity;
import com.player.dialog.NumberPickerDialog;
import com.player.dialog.SingleChoiceDialog;
import com.player.dialog.SubtitleSettingsDialog;
import com.player.subtitles.SubtitlesRenderer;
import com.player.subtitles.SubtitlesUtils;
import com.player.subtitles.TextFormatter;
import com.player.subtitles.format.Format;
import com.player.subtitles.format.SRTFormat;
import com.player.utils.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class MobilePlayerActivity extends AppCompatActivity implements NumberPickerDialogListener {

    public static final String TAG = "pt_mobile";

    public static final String EXTRA_LENGTH = "length";
    public static final String EXTRA_POSITION = "position";

    final String BRIGHTNESS_PREF = "brightness";

    public static final String SUBTITLE_SETTINGS = "subtitle-settings";

    protected File videoFile;

    public static final float EXTRA_SMALL = 0.7f;
    public static final float SMALL = 0.85f;
    public static final float NORMAL = 1f;
    public static final float LARGE = 1.25f;
    public static final float EXTRA_LARGE = 1.5f;
    public static final float DEFAULT_SIZE = NORMAL;
    public static final Float[] SIZES = new Float[]{EXTRA_SMALL, SMALL, NORMAL, LARGE, EXTRA_LARGE};
    public static final String SUBTITLE_FONT_SIZE = "subtitle-font-size";
    public static final String SUBTITLE_PREFS_NAME = "PopcornPreferences";

    private List<Float> shiftDisplayValues = new ArrayList<>();
    public static final float SUBTITLE_SHIFT_STEP_IN_SEC = 0.5f;

    protected SurfaceView playerSurfaceView;
    protected SurfaceView subtitlesSurfaceView;
    protected TextView subtitlesView;
    private FrameLayout bufferingView;
    private TextView bufferingPercent;
    private Toolbar toolbar;
    private TextView info;
    private ViewGroup overlay;
    private TextView playerTime;
    private SeekBar playerSeekBar;
    private TextView playerLength;
    private ImageButton playerScreenLock;
    private ImageButton playerScreenRotation;
    private ImageButton playerPlay;
    private ImageButton playerSubtitles;
    private ImageButton playerAspectRatio;
    private MenuItem audioMenuItem;

    protected final SubtitlesRenderer subtitlesRenderer = new SubtitlesRenderer();

    protected List<LangSubtitleItem> langSubtitleItems = new ArrayList<>();
    protected LangSubtitleItem currentLangSubtitleItem;
    protected List<VariantSubtitleItem> variantSubtitleItems = new ArrayList<>();
    protected VariantSubtitleItem currentVariantSubtitleItem;
    protected List<AspectRatioItem> aspectRatioItems = new ArrayList<>();
    private List<AudioItem> audioItems = new ArrayList<>();
    private List<SubtitleSettingsItem> subtitleSettingsItems = new ArrayList<>();
    private List<SubtitleSettingsSizeItem> subtitleSettingsSizeItems = new ArrayList<>();
    protected SubtitleSettingsSizeItem currentSubtitleSettingsSizeItem;
    protected AspectRatioItem currentAspectRatioItem;
    private AudioItem currentAudioItem;
    private SubtitleSettingsItem currentSubtitleSettingsItem;

    private GestureDetectorCompat gestureDetector;
    private PlayerControl playerControl;
    private boolean screenLocked;
    private int mUiVisibility = -1;
    private float mBrightness;

    private FileChooserDialog customSubtitlesDialog;
    private SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog();
    private SubtitleSettingsDialog subtitleSettingsDialog = new SubtitleSettingsDialog();
    private NumberPickerDialog shiftNumberPickerDialog = new NumberPickerDialog();

    private LoadSubtitlesTask loadSubtitlesTask;
    private SharedPreferences sharedPreferences;

    private boolean isActivityPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getIntent().getData() != null) {
            videoFile = new File(getIntent().getData().getPath());
            if (!videoFile.exists()) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_mobile_player);

        playerSurfaceView = (SurfaceView) findViewById(R.id.player_surface_view);
//        subtitlesSurfaceView = (SurfaceView) findViewById(R.id.subtitles_surface_view);
        subtitlesView = (TextView) findViewById(R.id.subtitles_view);
        bufferingView = (FrameLayout) findViewById(R.id.buffering_view);
        bufferingPercent = (TextView) findViewById(R.id.buffering_percent);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        info = (TextView) findViewById(R.id.info);
        overlay = (ViewGroup) findViewById(R.id.overlay_view);
        playerTime = (TextView) findViewById(R.id.player_time);
        playerSeekBar = (SeekBar) findViewById(R.id.player_seek_bar);
        playerLength = (TextView) findViewById(R.id.player_length);
        playerScreenLock = (ImageButton) findViewById(R.id.player_screen_lock);
        playerScreenRotation = (ImageButton) findViewById(R.id.player_screen_rotation);
        playerPlay = (ImageButton) findViewById(R.id.player_play);
        playerSubtitles = (ImageButton) findViewById(R.id.player_subtitles);
        playerAspectRatio = (ImageButton) findViewById(R.id.player_aspect_ratio);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(videoFile.getName());
        }
        toolbar.setOnTouchListener(overlayTouchListener);
        overlay.setOnTouchListener(overlayTouchListener);
        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return screenLocked;
            }
        });
        playerSeekBar.setOnSeekBarChangeListener(seekBarListener);
        playerScreenLock.setOnClickListener(screenLockListener);
        playerScreenRotation.setOnClickListener(screenRotationListener);
        playerPlay.setOnClickListener(playListener);
        playerSubtitles.setOnClickListener(subtitlesListener);
        playerAspectRatio.setOnClickListener(aspectRatioListener);

        gestureDetector = new GestureDetectorCompat(getBaseContext(), gestureListener);
        gestureDetector.setOnDoubleTapListener(gestureDoubleTapListener);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == mUiVisibility) {
                    return;
                }
                if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !(View.VISIBLE == overlay.getVisibility()) && !isFinishing()) {
                    showOverlay(true);
                }
                mUiVisibility = visibility;
            }
        });

        updateMediaLength(0);
        updateMediaTime(0, false);
        setScreenLocked(false);
        hideBuffering();
        hideInfo(false);
        hideOverlay(false);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        mBrightness = sharedPreferences.getFloat(BRIGHTNESS_PREF, 0.6f);
        setBrightness(mBrightness, false);

        subtitlesRenderer.setSubtitlesView(subtitlesView);
        ListItemEntity.addItemToList(langSubtitleItems, withoutSubtitlesItem);
        ListItemEntity.addItemToList(langSubtitleItems, customSubtitlesItem);
        withoutSubtitlesItem.onItemChosen();

        subtitleSettingsItems.clear();
        ListItemEntity.addItemToList(subtitleSettingsItems, langSubtitlesSettingItem);
        ListItemEntity.addItemToList(subtitleSettingsItems, variantSubtitleSettingItem);
        ListItemEntity.addItemToList(subtitleSettingsItems, sizeSubtitleSettingItem);
        ListItemEntity.addItemToList(subtitleSettingsItems, shiftSubtitleSettingItem);
        currentSubtitleShift = DEFAULT_SUBTITLE_SHIFT;
        for (int i = 0; i < getResources().getStringArray(R.array.font_size_names).length; i++) {
            ListItemEntity.addItemToList(subtitleSettingsSizeItems, new SubtitleSettingsSizeItem(
                    getResources().getStringArray(R.array.font_size_names)[i],
                    getResources().getStringArray(R.array.font_size_names)[i]));
        }
        SharedPreferences sPref = getSharedPreferences(SUBTITLE_PREFS_NAME, MODE_PRIVATE);
        currentSubtitleSettingsSizeItem = subtitleSettingsSizeItems.get(Arrays.asList(SIZES).indexOf(sPref.getFloat(SUBTITLE_FONT_SIZE, DEFAULT_SIZE)));

        float j = SUBTITLE_MIN_SHIFT;
        for (int i = SUBTITLE_MIN_SHIFT * (int) (1f / SUBTITLE_SHIFT_STEP_IN_SEC);
             i < SUBTITLE_MAX_SHIFT * (int) (1f / SUBTITLE_SHIFT_STEP_IN_SEC) + 2; i++) {
            shiftDisplayValues.add(j);
            j += SUBTITLE_SHIFT_STEP_IN_SEC;
        }

    }

    @Override
    public void onReturnNumberPickerValue(String name, int value) {
        if (name.equals(getString(R.string.subtitle_settings_shift))) {
            currentSubtitleShift = (int) (shiftDisplayValues.get(value) * MILLISEC_IN_SEC);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_mobile, menu);
        audioMenuItem = menu.findItem(R.id.audio_menu_item);
        audioMenuItem.setVisible(false);
        audioMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                singleChoiceDialog.show(getSupportFragmentManager(), getString(R.string.track_audio), audioItems, currentAudioItem);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityPaused = true;
    }

    protected final boolean isActivityPaused() {
        return isActivityPaused;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private float x = -1f;
    private float y = -1f;
    private int touchAction;
    private long seekInitTime;
    private boolean seeking;
    private boolean seekingPause;
    private final int TOUCH_NONE = -1;
    private final int TOUCH_SEEK = 1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        if (screenLocked) {
            return false;
        }

        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);
        int min_screen_side = Math.min(screen.widthPixels, screen.heightPixels);
        float changed_x = 0;
        float changed_y = 0;
        if (x != -1 && y != -1) {
            changed_x = event.getRawX() - x;
            changed_y = event.getRawY() - y;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX();
                y = event.getRawY();
                touchAction = TOUCH_NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (TOUCH_SEEK != touchAction && (changed_x == 0 || Math.abs(changed_y / changed_x) > 2)) {
                    if (Math.abs(changed_y / min_screen_side) < 0.05) {
                        return false;
                    }
                    x = event.getRawX();
                    y = event.getRawY();
                    if ((int) x > (screen.widthPixels / 2)) {
                        //Right side
                        if (playerControl != null) {
                            if (changed_y < 0) {
                                playerControl.volumeUp();
                            } else {
                                playerControl.volumeDown();
                            }
                        }
                    } else {
                        //Left side
                        if (changed_y < 0) {
                            mBrightness = Math.round(Math.min(mBrightness + 0.1f, 1.0f) * 10) / 10.0f;
                            setBrightness(mBrightness, true);
                        } else {
                            mBrightness = Math.round(Math.max(mBrightness - 0.1f, 0.0f) * 10) / 10.0f;
                            setBrightness(mBrightness, true);
                        }
                    }
                } else {
                    onSeekTouch((changed_x / screen.xdpi) * 2.54f, false);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (TOUCH_SEEK == touchAction) {
                    onSeekTouch((changed_x / screen.xdpi) * 2.54f, true);
                }
                x = -1f;
                y = -1f;
                break;
        }

        return super.onTouchEvent(event);
    }

    private void onSeekTouch(float gestureSize, boolean seek) {
        if (playerControl == null) {
            return;
        }
        // No onSeek action if coef > 0.5 and gesturesize < 1cm
        if (Math.abs(gestureSize) < 1) {
            if (TOUCH_SEEK == touchAction) {
                showSeekInfo(0, seekInitTime, false);
                updateMediaTime(seekInitTime, false);
                if (seek) {
                    onSeek(seekInitTime);
                }
            }
            return;
        }

        if (TOUCH_NONE == touchAction) {
            touchAction = TOUCH_SEEK;
            seekInitTime = getPlayerPosition();
            onStartSeeking();
            if (View.VISIBLE != overlay.getVisibility()) {
                showOverlay(false);
            }
        } else if (TOUCH_SEEK != touchAction) {
            return;
        }

        long length = playerControl.getLength();

        // Size of the jump, 10 minutes max (600000), with a bi-cubic progression, for a 8cm gesture
        long jump = (long) (Math.signum(gestureSize) * ((600000 * Math.pow((gestureSize / 8), 4)) + 3000));

        // Adjust the jump
        if (jump > 0 && seekInitTime + jump > length) {
            jump = length - seekInitTime;
        }
        if (jump < 0 && seekInitTime + jump < 0) {
            jump = -seekInitTime;
        }

        //Jump !
        if (length > 0) {
            showSeekInfo(jump, seekInitTime, false);
            updateMediaTime(seekInitTime + jump, false);
            if (seek) {
                onSeek(seekInitTime + jump);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (screenLocked) {
            showInfo(getString(R.string.locked), true);
        } else {
            if (playerControl != null) {
                setResultVideoStop(playerControl.getLength(), playerControl.getPosition());
            } else {
                setResultVideoStop(0, 0);
            }
            super.onBackPressed();
        }
    }

    protected final void setResultVideoStop(long length, long position) {
        Intent data = new Intent();
        data.putExtra(EXTRA_LENGTH, length);
        data.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, data);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (KeyEvent.ACTION_DOWN == event.getAction() && playerControl != null) {
                    playerControl.volumeUp();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (KeyEvent.ACTION_DOWN == event.getAction() && playerControl != null) {
                    playerControl.volumeDown();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    protected abstract void onChosenAspectRatio();

    protected abstract void onChosenWithoutSubtitles();

    protected abstract void onChosenSubtitles();

    protected final void setPlayerControl(PlayerControl action) {
        playerControl = action;
    }

    @Nullable
    protected final PlayerControl getPlayerControl() {
        return playerControl;
    }

    protected void onStartSeeking() {
        seeking = true;
        if (playerControl != null && playerControl.isPlaying()) {
            playerControl.pause();
            seekingPause = true;
        }
    }

    protected void onStopSeeking() {
        seeking = false;
        if (seekingPause) {
            if (playerControl != null) {
                playerControl.play();
            }
            seekingPause = false;
        }
    }

    protected void onSeek(long position) {
        seek(position);
        restartHandlerAction(HIDE_INFO, 500);
        if (View.VISIBLE == overlay.getVisibility()) {
            updateTime(playerControl != null && playerControl.isPlaying());
            restartHideOverlay();
        }
    }

    protected void seek(long position) {
        if (playerControl != null) {
            playerControl.seek(position);
        }
        onStopSeeking();
    }

    protected long getPlayerPosition() {
        if (playerControl != null) {
            return playerControl.getPosition();
        }
        return 0;
    }

    protected void setSubtitlesForeground(int color) {
        subtitlesRenderer.setSubtitlesColor(color);
    }

    protected void setSubtitlesFontScale(float scale) {
        subtitlesRenderer.setSubtitlesSize(getResources().getDimension(R.dimen.subtitles_text_size) * scale);
    }

    protected void setSubtitlesTrack(String filePath, TextFormatter formatter) {
        subtitlesRenderer.setSubtitlesTrack(filePath, formatter);
        onChosenSubtitles();
    }

    protected void updateSubtitles(long timeMillis) {
        subtitlesRenderer.onUpdate(timeMillis - currentSubtitleShift);
    }

    protected boolean writeCurrentSubtitles(Format format, String subtitlesPath) {
        if (subtitlesRenderer.getCaptions() != null && subtitlesRenderer.getCaptions().size() > 0) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(subtitlesPath));
                format.write(writer, subtitlesRenderer.getCaptions());
                writer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    protected String[] getDenyFolderNamesForCustomSubtitlesDialog() {
        return null;
    }

    protected void findBesideVideoSubtitles() {
//        if (videoFile.getParentFile() != null && videoFile.getParentFile().isDirectory()) {
//            File[] files = videoFile.getParentFile().listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    return pathname.isFile() && pathname.getName().endsWith(SRTFormat.EXTENSION);
//                }
//            });
//            for (int i = 0; i < files.length; i++) {
//                ListItemEntity.addItemToList(subtitleItems, new SubtitleItem(files[i].getAbsolutePath(), "Track - " + (i + 1)) {
//                    @Override
//                    public void onItemChosen() {
//                        super.onItemChosen();
//                        Format format = null;
//                        if (getValue().endsWith(SRTFormat.EXTENSION)) {
//                            format = new SRTFormat();
//                        }
//                        if (format != null) {
//                            setSubtitlesTrack(getValue(), new RemoveTagsFormatter());
//                        }
//                    }
//                });
//            }
//        }
    }

    protected void initAudioTracks(List<AudioItem> items, AudioItem item) {
        audioItems.clear();
        if (items != null) {
            for (AudioItem audioItem : items) {
                ListItemEntity.addItemToList(audioItems, audioItem);
            }
        }
        currentAudioItem = item;
        if (audioItems.size() > 1) {
            audioMenuItem.setVisible(true);
        }
    }

    protected void showBuffering() {
        showView(bufferingView);
    }

    protected void hideBuffering() {
        hideView(bufferingView, false);
    }

    protected void updateBufferingProgress(int percent) {
        bufferingPercent.setText(percent + "%");
    }

    protected void showInfo(String text, boolean autoHide) {
        showView(info);
        info.setText(text);
        if (autoHide) {
            restartHandlerAction(HIDE_INFO, HIDE_INFO_DELAY);
        } else {
            disableHandlerAction(HIDE_INFO);
        }
    }

    protected void showSeekInfo(long jump, long currentTime, boolean autoHide) {
        String sign;
        if (jump > 1000) {
            sign = "+";
        } else if (jump < -1000) {
            sign = "-";
        } else {
            sign = "";
        }
        showInfo(String.format("%s%s (%s)",
                sign,
                StringUtil.millisToString(Math.abs(jump), StringUtil.TIME_FORMAT_2_DIGITS),
                StringUtil.millisToString(currentTime + jump, StringUtil.TIME_FORMAT_2_DIGITS)
        ), autoHide);
    }

    protected void showVolumeInfo(int value, int max) {
        showInfo(getString(R.string.volume) + ": " + (100 * value / max) + "%", true);
    }

    protected void hideInfo(boolean animate) {
        hideView(info, animate);
    }

    private void showView(View view) {
        view.clearAnimation();
        if (View.VISIBLE != view.getVisibility()) {
            view.setVisibility(View.VISIBLE);
        }
    }


    private void hideView(View view, boolean animate) {
        if (View.VISIBLE == view.getVisibility()) {
            view.setVisibility(View.GONE);
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.fade_out));
            }
        }
    }

    private void showOverlay(boolean autoHide) {
        setStatusBarVisibility(true);
        toolbar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        updateTime(playerControl != null && playerControl.isPlaying());
        if (autoHide) {
            restartHideOverlay();
        }
    }

    private void hideOverlay(boolean animate) {
        setStatusBarVisibility(false);
        toolbar.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        if (animate) {
            toolbar.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.fade_out));
            overlay.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.fade_out));
        }
        disableHandlerAction(UPDATE_TIME);
        disableHandlerAction(HIDE_OVERLAY);
    }

    private void setStatusBarVisibility(boolean visibility) {
        int uiOptions = 0;
        if (visibility) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                }
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    protected final void updateMediaLength(int length) {
        playerSeekBar.setMax(length);
        playerLength.setText(StringUtil.millisToString(length, StringUtil.TIME_FORMAT_2_DIGITS));
    }

    protected void updateMediaTime(long time, boolean fromUser) {
        if (!fromUser) {
            playerSeekBar.setProgress((int) time);
        }
        playerTime.setText(StringUtil.millisToString(time, StringUtil.TIME_FORMAT_2_DIGITS));
    }

    protected void updateMediaProgress(long progress) {
        playerSeekBar.setSecondaryProgress((int) progress);
    }

    private void setScreenLocked(boolean lock) {
        screenLocked = lock;
        if (screenLocked) {
            playerScreenLock.setImageResource(R.drawable.ic_lock_white_36dp);
            playerScreenRotation.setVisibility(View.INVISIBLE);
            playerPlay.setVisibility(View.INVISIBLE);
            playerSubtitles.setVisibility(View.INVISIBLE);
            playerAspectRatio.setVisibility(View.INVISIBLE);
            showInfo(getString(R.string.locked), true);
        } else {
            playerScreenLock.setImageResource(R.drawable.ic_lock_open_white_36dp);
            playerScreenRotation.setVisibility(View.VISIBLE);
            playerPlay.setVisibility(View.VISIBLE);
            playerSubtitles.setVisibility(View.VISIBLE);
            playerAspectRatio.setVisibility(View.VISIBLE);
            showInfo(getString(R.string.unlocked), true);
        }
    }

    private void onPlayPause() {
        if (playerControl != null) {
            if (playerControl.isPlaying()) {
                playerControl.pause();
            } else {
                playerControl.play();
            }
        }
    }

    protected void setBrightness(float brightness, boolean inform) {
        if (brightness < 0.0f) {
            brightness = 0.0f;
        }
        if (brightness > 1.0f) {
            brightness = 1.0f;
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);
        sharedPreferences.edit().putFloat(BRIGHTNESS_PREF, brightness).apply();
        if (inform) {
            showInfo(getString(R.string.brightness) + ": " + (int) (brightness * 100) + "%", true);
        }
    }

    protected void loadSubtitles(String path) {
        if (loadSubtitlesTask != null && AsyncTask.Status.FINISHED != loadSubtitlesTask.getStatus()) {
            loadSubtitlesTask.cancel(true);
        }
        loadSubtitlesTask = new LoadSubtitlesTask();
        loadSubtitlesTask.execute(path);
    }

    private LangSubtitleItem withoutSubtitlesItem = new LangSubtitleItem(SubtitlesUtils.WITHOUT_SUBTITLES) {

        @Override
        public String getName() {
            return getString(R.string.without_subtitle);
        }

        @Override
        public void onItemChosen() {
            super.onItemChosen();
            variantSubtitleItems.clear();
            variantSubtitleItems.add(new VariantSubtitleItem(null, "None"));
            variantSubtitleItems.get(0).onItemChosen();
            subtitlesRenderer.disable();
            onChosenWithoutSubtitles();
        }
    };

    private LangSubtitleItem customSubtitlesItem = new LangSubtitleItem(SubtitlesUtils.CUSTOM_SUBTITLES) {

        @Override
        public String getName() {
            return getString(R.string.custom_subtitle);
        }

        @Override
        public void onItemChosen() {
            if (customSubtitlesDialog == null) {
                customSubtitlesDialog = new FileChooserDialog();
            }
            if (!customSubtitlesDialog.isAdded()) {
                customSubtitlesDialog.setTitle(R.string.select_subtitle);
                customSubtitlesDialog.setChooserListener(customSubtitlesDialogListener);
                customSubtitlesDialog.setAcceptExtensions(SubtitlesRenderer.SUPPORTED_EXTENSIONS);
                customSubtitlesDialog.setDenyFolderNames(getDenyFolderNamesForCustomSubtitlesDialog());
                customSubtitlesDialog.show(getSupportFragmentManager(), Environment.getExternalStorageDirectory());
            }
        }
    };

    private SubtitleSettingsItem langSubtitlesSettingItem = new SubtitleSettingsItem(SUBTITLE_SETTINGS) {

        @Override
        public String getName() {
            return getString(R.string.subtitle_settings_language);
        }

        @Override
        public CharSequence getControlText() {
            if (currentLangSubtitleItem != null) {
                return getColoredControlText(super.getControlText(), currentLangSubtitleItem.getName());
            }
            return super.getControlText();
        }

        @Override
        public void onItemChosen() {
            super.onItemChosen();
            if (!seeking) {
                singleChoiceDialog.show(getSupportFragmentManager(), getName(), langSubtitleItems, currentLangSubtitleItem);
                restartHideOverlay();
            }
        }
    };

    private SubtitleSettingsItem variantSubtitleSettingItem = new SubtitleSettingsItem(SUBTITLE_SETTINGS) {

        public String getName() {
            return getString(R.string.subtitle_file);
        }

        @Override
        public CharSequence getControlText() {
            if (currentVariantSubtitleItem != null) {
                return getColoredControlText(super.getControlText(), currentVariantSubtitleItem.getName());
            }
            return super.getControlText();
        }

        @Override
        public void onItemChosen() {
            super.onItemChosen();
            if (!seeking) {
                singleChoiceDialog.show(getSupportFragmentManager(), getName(), variantSubtitleItems, currentVariantSubtitleItem);
                restartHideOverlay();
            }
        }
    };

    private SubtitleSettingsItem sizeSubtitleSettingItem = new SubtitleSettingsItem(SUBTITLE_SETTINGS) {

        public String getName() {
            return getString(R.string.subtitle_settings_size);
        }

        @Override
        public CharSequence getControlText() {
            if (currentSubtitleSettingsSizeItem != null) {
                return getColoredControlText(super.getControlText(), currentSubtitleSettingsSizeItem.getName());
            }
            return super.getControlText();
        }

        @Override
        public void onItemChosen() {
            super.onItemChosen();
            singleChoiceDialog.show(getSupportFragmentManager(), getString(R.string.subtitle_settings_size), subtitleSettingsSizeItems, currentSubtitleSettingsSizeItem);
            restartHideOverlay();
        }
    };

    private static final int DEFAULT_SUBTITLE_SHIFT = 0;   //sec
    private static final int SUBTITLE_MAX_SHIFT = 300;
    private static final int SUBTITLE_MIN_SHIFT = -300;
    private static final int MILLISEC_IN_SEC = 1000;
    private int currentSubtitleShift;

    private SubtitleSettingsItem shiftSubtitleSettingItem = new SubtitleSettingsItem(SUBTITLE_SETTINGS) {

        public String getName() {
            return getString(R.string.subtitle_settings_shift);
        }

        @Override
        public CharSequence getControlText() {
            return getColoredControlText(super.getControlText(), String.format(Locale.ENGLISH, "%.1f sec", (float) currentSubtitleShift / MILLISEC_IN_SEC));
        }

        @Override
        public void onItemChosen() {
            super.onItemChosen();
            shiftNumberPickerDialog.show(getSupportFragmentManager(),
                    getString(R.string.subtitle_settings_shift),
                    SUBTITLE_MAX_SHIFT,
                    SUBTITLE_MIN_SHIFT,
                    shiftDisplayValues.indexOf(new Float((float) currentSubtitleShift / (float) MILLISEC_IN_SEC)));
            restartHideOverlay();
        }
    };

    /*
    * Events
    * */

    protected void eventMediaPlaying() {
        playerSurfaceView.setKeepScreenOn(true);
        playerPlay.setImageResource(R.drawable.ic_pause_white_36dp);
        if (View.VISIBLE == overlay.getVisibility()) {
            updateTime(true);
            restartHideOverlay();
        }
    }

    protected void eventMediaPaused() {
        if (!seeking) {
            playerSurfaceView.setKeepScreenOn(false);
            playerPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        }
        disableHandlerAction(HIDE_OVERLAY);
        disableHandlerAction(UPDATE_TIME);
    }

    /*
    * Listeners
    * */

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    private GestureDetector.OnDoubleTapListener gestureDoubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (View.VISIBLE == overlay.getVisibility()) {
                hideOverlay(false);
            } else {
                showOverlay(true);
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!screenLocked) {
                onPlayPause();
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    };

    private View.OnTouchListener overlayTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    restartHideOverlay();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener screenLockListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setScreenLocked(!screenLocked);
            if (!seeking) {
                restartHideOverlay();
            }
        }
    };

    private View.OnClickListener screenRotationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (seeking) {
                return;
            }
            if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE != getRequestedOrientation()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            restartHideOverlay();
        }
    };

    private View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (seeking) {
                return;
            }
            onPlayPause();
        }
    };

    private View.OnClickListener subtitlesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (seeking) {
                return;
            }
            subtitleSettingsDialog.show(getSupportFragmentManager(), getString(R.string.subtitles), subtitleSettingsItems, currentSubtitleSettingsItem);
            restartHideOverlay();
        }
    };

    private View.OnClickListener aspectRatioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (seeking) {
                return;
            }
            singleChoiceDialog.show(getSupportFragmentManager(), getString(R.string.aspect_ratio), aspectRatioItems, currentAspectRatioItem);
            restartHideOverlay();
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {

        private long startPosition;
        private int progress;
        private boolean fromUser;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!screenLocked) {
                this.progress = progress;
                this.fromUser = fromUser;
                if (fromUser && playerControl != null) {
                    showSeekInfo(progress - startPosition, startPosition, false);
                }
                updateMediaTime(progress, fromUser);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            progress = -1;
            fromUser = false;
            if (!screenLocked) {
                startPosition = getPlayerPosition();
                onStartSeeking();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (progress != -1 && fromUser) {
                onSeek(progress);
            }
        }
    };

    private FileChooserDialog.OnChooserListener customSubtitlesDialogListener = new FileChooserDialog.OnChooserListener() {

        @Override
        public void onChooserSelected(File file) {
            currentLangSubtitleItem = customSubtitlesItem;
            variantSubtitleItems.clear();
            variantSubtitleItems.add(new VariantSubtitleItem(Uri.fromFile(file).toString(), file.getName()) {

                @Override
                public void onItemChosen() {
                    super.onItemChosen();
                    loadSubtitles(getValue());
                }
            });
            variantSubtitleItems.get(0).onItemChosen();
        }

        @Override
        public void onChooserCancel() {

        }
    };

    /*
    * Handler
    * */

    private final int UPDATE_TIME = 1;
    private final int HIDE_OVERLAY = 2;
    private final int HIDE_INFO = 3;

    final long UPDATE_TIME_DELAY = 1000;
    final long HIDE_OVERLAY_DELAY = 4000;
    final long HIDE_INFO_DELAY = 1500;

    private void updateTime(boolean autoUpdate) {
        updateMediaTime(getPlayerPosition(), false);
        disableHandlerAction(UPDATE_TIME);
        if (autoUpdate) {
            handler.sendEmptyMessageDelayed(UPDATE_TIME, UPDATE_TIME_DELAY);
        }
    }

    private void restartHideOverlay() {
        if (playerControl != null && playerControl.isPlaying()) {
            restartHandlerAction(HIDE_OVERLAY, HIDE_OVERLAY_DELAY);
        }
    }

    private void restartHandlerAction(int what, long delay) {
        disableHandlerAction(what);
        handler.sendEmptyMessageDelayed(what, delay);
    }

    private void disableHandlerAction(int what) {
        handler.removeMessages(what);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TIME:
//                    Log.d(TAG, "Auto update time");
                    updateTime(true);
                    return true;
                case HIDE_OVERLAY:
                    hideOverlay(true);
                    return true;
                case HIDE_INFO:
                    hideInfo(true);
                    return true;
                default:
                    return true;
            }
        }
    });

    private CharSequence getColoredControlText(CharSequence name, CharSequence value) {
        name = TextUtils.concat(name, ": ");
        final SpannableString text = new SpannableString(TextUtils.concat(name, value));
        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        text.setSpan(new ForegroundColorSpan(typedValue.data), name.length(), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    /*
    * Classes
    * */

    private class LoadSubtitlesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            subtitlesRenderer.disable();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String subtitlesPath = SubtitlesUtils.generateSubtitlePath(videoFile.getAbsolutePath(), SRTFormat.EXTENSION);
                SubtitlesUtils.load(params[0], subtitlesPath);
                return subtitlesPath;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("pt_mobile", "MobilePlayerActivity<LoadSubtitlesTask>: Error", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String path) {
            if (!TextUtils.isEmpty(path)) {
                setSubtitlesTrack(path, null);
            }
        }
    }

    protected abstract class AspectRatioItem extends ListItemEntity<Integer> {

        public AspectRatioItem(Integer value) {
            super(value);
        }

        @Override
        public void onItemChosen() {
            currentAspectRatioItem = this;
            onChosenAspectRatio();
        }
    }

    protected class LangSubtitleItem extends ListItemEntity<String> {

        public LangSubtitleItem(String value) {
            super(value);
        }

        public LangSubtitleItem(String value, String name) {
            super(value, name);
        }

        @Override
        public void onItemChosen() {
            currentLangSubtitleItem = this;
        }
    }

    protected class VariantSubtitleItem extends ListItemEntity<String> {

        public VariantSubtitleItem(String value) {
            super(value);
        }

        public VariantSubtitleItem(String value, String name) {
            super(value, name);
        }

        @Override
        public void onItemChosen() {
            currentVariantSubtitleItem = this;
        }
    }

    protected class SubtitleSettingsItem extends ListItemEntity<String> {

        public SubtitleSettingsItem(String value) {
            super(value);
        }

        @Override
        public void onItemChosen() {
            currentSubtitleSettingsItem = this;
        }
    }

    protected class SubtitleSettingsSizeItem extends ListItemEntity<String> {

        public SubtitleSettingsSizeItem(String value, String name) {
            super(value, name);
        }

        @Override
        public void onItemChosen() {
            currentSubtitleSettingsSizeItem = this;
            setSubtitlesFontScale(SIZES[getPosition()]);
        }
    }

    protected class AudioItem extends ListItemEntity<Integer> {

        public AudioItem(Integer value, String name) {
            super(value, name);
        }

        @Override
        public void onItemChosen() {
            currentAudioItem = this;
        }
    }

    /*
    * Start
    * */

    public static void startForResult(Fragment fragment, Intent intent, int requestCode, Uri uri) {
        intent.setDataAndType(uri, "video/*");
        fragment.startActivityForResult(intent, requestCode);
    }
}