package se.popcorn_time.mobile.ui.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.player.CastMobilePlayerActivity;
import com.player.MobilePlayerActivity;
import com.player.dialog.ListItemEntity;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.base.model.PlayerInfo;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.subtitles.SubtitlesLanguage;
import se.popcorn_time.base.torrent.client.ClientConnectionListener;
import se.popcorn_time.base.torrent.client.PlayerClient;
import se.popcorn_time.base.torrent.watch.BaseWatchListener;
import se.popcorn_time.base.torrent.watch.WatchListener;
import se.popcorn_time.base.torrent.watch.WatchProgress;
import se.popcorn_time.base.torrent.watch.WatchState;
import se.popcorn_time.base.utils.InterfaceUtil;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.model.ChoiceProperty;
import se.popcorn_time.model.details.IDetailsUseCase;
import se.popcorn_time.model.settings.ISettingsUseCase;
import se.popcorn_time.model.share.IVideoShareData;
import se.popcorn_time.model.subtitles.Subtitles;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.ISystemShareView;

public abstract class PlayerBaseActivity extends CastMobilePlayerActivity {

    private static final String EXTRA_PLAYER_INFO = "player-info";

    private final int DEFAULT_BUFFERING_PERCENT = 5;

    protected PlayerInfo playerInfo;
    private long seekPosition;
    private long downloadedProgress;

    private PlayerClient playerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Popcorn_Player_Classic);
        super.onCreate(savedInstanceState);

        final ISettingsUseCase settingsUseCase = ((PopcornApplication) getApplication()).getSettingsUseCase();
        setSubtitlesForeground(Color.parseColor(settingsUseCase.getSubtitlesFontColor()));
        setSubtitlesFontScale(settingsUseCase.getSubtitlesFontSize());

        playerClient = new PlayerClient(PlayerBaseActivity.this);
        playerClient.setConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                playerClient.addWatchListener(playerWatchListener);
                seekPosition = -1;
                downloadedProgress = 0;
            }

            @Override
            public void onClientDisconnected() {

            }
        });

        playerInfo = getIntent().getParcelableExtra(EXTRA_PLAYER_INFO);

        final IDetailsUseCase detailsUseCase = ((IUseCaseManager) getApplication()).getDetailsUseCase();
        onLangSubtitles(detailsUseCase.getLangSubtitlesChoiceProperty());
        final Subtitles customSubtitles = detailsUseCase.getCustomSubtitlesProperty().getValue();
        if (customSubtitles != null) {
            onCustomSubtitles(customSubtitles);
        } else {
            onVariantSubtitles(detailsUseCase.getSubtitlesChoiceProperty());
        }
        detailsUseCase.getSubtitlesChoiceProperty().getObservable().subscribe(new Consumer<ChoiceProperty<Subtitles>>() {

            @Override
            public void accept(@NonNull ChoiceProperty<Subtitles> property) throws Exception {
                onVariantSubtitles(property);
            }
        });

//            findBesideVideoSubtitles();
//            if (subtitleItems.size() > 2) {
//                subtitleItems.get(2).onItemChosen();
//            }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration config = getResources().getConfiguration();
        config.locale = InterfaceUtil.getAppLocale();
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerClient.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerClient.removeWatchListener(playerWatchListener);
        playerClient.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (R.id.share == item.getItemId()) {
            final IVideoShareData videoShareData = ((PopcornApplication) this.getApplication()).getShareUseCase().getVideoShareData();
            if (videoShareData != null) {
                ((IViewRouter) getApplication()).onShowView(ISystemShareView.class, videoShareData.getText());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStartSeeking() {
        super.onStartSeeking();
        seekPosition = -1;
    }

    @Override
    protected void onStopSeeking() {
        super.onStopSeeking();
        seekPosition = -1;
    }

    @Override
    protected void onSeek(long position) {
//        if (getPlayerControl() != null && playerClient.seek((float) position / getPlayerControl().getLength())) {
//            seekPosition = position;
//            hideInfo(false);
//            showBuffering();
//            updateBufferingProgress(DEFAULT_BUFFERING_PERCENT);
//            updateMediaTime(position, false);
//        } else {
//            hideBuffering();
//            super.onSeek(position);
//        }
//        updateMediaProgress(position);

        if (downloadedProgress == 0 || position < downloadedProgress) {
            super.onSeek(position);
        } else {
            hideInfo(false);
            onStopSeeking();
        }
    }

    @Override
    protected long getPlayerPosition() {
        if (seekPosition != -1) {
            return seekPosition;
        }
        return super.getPlayerPosition();
    }

    @Override
    protected String[] getDenyFolderNamesForCustomSubtitlesDialog() {
        return new String[]{
                StorageUtil.ROOT_FOLDER_NAME
        };
    }

    private WatchListener playerWatchListener = new BaseWatchListener() {
        @Override
        public void onUpdateProgress(WatchProgress progress) {
            if (progress == null) {
                return;
            }
            if (WatchState.SEQUENTIAL_DOWNLOAD == progress.state) {
                downloadedProgress = getPlayerControl() != null ? (long) ((double) progress.value / progress.total * getPlayerControl().getLength()) : 0;
                updateMediaProgress(downloadedProgress);
            } else if (WatchState.BUFFERING == progress.state) {
                int percent = progress.value * 100 / progress.total;
                if (percent < DEFAULT_BUFFERING_PERCENT) {
                    percent = DEFAULT_BUFFERING_PERCENT;
                } else if (percent > 100) {
                    percent = 100;
                }
                updateBufferingProgress(percent);
            }
        }

        @Override
        public void onDownloadFinished() {
            updateMediaProgress(getPlayerControl() != null ? getPlayerControl().getLength() : 0);
        }

        @Override
        public void onBufferingFinished() {
            if (seekPosition != -1) {
                hideBuffering();
                seek(seekPosition);
            }
        }
    };

    private void onLangSubtitles(final ChoiceProperty<Map.Entry<String, List<Subtitles>>> property) {
        final Map.Entry<String, List<Subtitles>>[] langSubtitles = property.getItems();
        if (langSubtitles != null && langSubtitles.length > 0) {
            for (Map.Entry<String, List<Subtitles>> entry : langSubtitles) {
                ListItemEntity.addItemToList(langSubtitleItems, new LangSubtitleItem(entry.getKey()) {

                    @Override
                    public String getName() {
                        return SubtitlesLanguage.subtitlesNameToNative(SubtitlesLanguage.subtitlesIsoToName(getValue()));
                    }

                    @Override
                    public void onItemChosen() {
                        super.onItemChosen();
                        property.setPosition(getPosition() - 2);
                    }
                });
            }
            if (property.getPosition() >= 0) {
                currentLangSubtitleItem = langSubtitleItems.get(property.getPosition() + 2); //+2 mean no subs and custom subs
            }
        } else {
            currentLangSubtitleItem = langSubtitleItems.get(0); // without subs
        }
    }

    private void onCustomSubtitles(Subtitles subtitles) {
        currentLangSubtitleItem = langSubtitleItems.get(1);
        final String url = subtitles.getUrl();
        variantSubtitleItems.clear();
        ListItemEntity.addItemToList(variantSubtitleItems, new VariantSubtitleItem(url, url.substring(url.lastIndexOf(File.separator) + 1)) {

            @Override
            public void onItemChosen() {
                super.onItemChosen();
                loadSubtitles(getValue());
            }
        });
        variantSubtitleItems.get(0).onItemChosen();
    }

    private void onVariantSubtitles(ChoiceProperty<Subtitles> property) {
        variantSubtitleItems.clear();
        final Subtitles[] subtitles = property.getItems();
        if (subtitles != null && subtitles.length > 0) {
            for (int i = 0; i < subtitles.length; i++) {
                ListItemEntity.addItemToList(variantSubtitleItems, new VariantSubtitleItem(subtitles[i].getUrl(), currentLangSubtitleItem.getName() + " #" + (i + 1)) {

                    @Override
                    public void onItemChosen() {
                        super.onItemChosen();
                        loadSubtitles(getValue());
                    }
                });
            }
            if (property.getPosition() >= 0) {
                variantSubtitleItems.get(property.getPosition()).onItemChosen();
            }
        } else {
            ListItemEntity.addItemToList(variantSubtitleItems, new VariantSubtitleItem(null, "None"));
            variantSubtitleItems.get(0).onItemChosen();
        }
    }

    /*
    * Start
    * */

    public static void startForResult(Fragment fragment, Intent intent, int requestCode, Uri uri, PlayerInfo playerInfo) {
        intent.putExtra(EXTRA_PLAYER_INFO, playerInfo);
        MobilePlayerActivity.startForResult(fragment, intent, requestCode, uri);
    }


}