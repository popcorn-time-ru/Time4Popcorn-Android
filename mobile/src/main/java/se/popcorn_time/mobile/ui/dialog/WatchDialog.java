package se.popcorn_time.mobile.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.player.MobilePlayerActivity;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import se.popcorn_time.base.analytics.Analytics;
import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.database.tables.History;
import se.popcorn_time.base.model.PlayerInfo;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.torrent.client.ClientConnectionListener;
import se.popcorn_time.base.torrent.client.WatchClient;
import se.popcorn_time.base.torrent.watch.WatchException;
import se.popcorn_time.base.torrent.watch.WatchListener;
import se.popcorn_time.base.torrent.watch.WatchProgress;
import se.popcorn_time.base.torrent.watch.WatchState;
import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.VLCPlayerActivity;
import se.popcorn_time.mobile.ui.base.PlayerBaseActivity;
import se.popcorn_time.model.config.VpnConfig;
import se.popcorn_time.model.share.IShareUseCase;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.vpn.IVpnView;

public class WatchDialog extends DialogFragment implements WatchListener {

    private ImageView vpnAlertIcon;
    private ImageView popcorn;
    private ViewGroup statusLayout;
    private ProgressBar statusProgress;
    private TextView statusInfo;
    private TextView statusPercent;
    private TextView statusSeedsPeers;

    private WatchInfo watchInfo;
    private WatchClient watchClient;
    private boolean prepared;
    private Animation popcornAnimation;
    private Animation alertIconAnimation;

    private String loadedSubtitlesPath;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("watch_info", watchInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullscreenDialog);
        setCancelable(true);

        if (savedInstanceState != null) {
            watchInfo = savedInstanceState.getParcelable("watch_info");
        }

        prepared = false;
        popcornAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.popcorn_prepare);
        alertIconAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.vpn_alert_icon_anim);

        watchClient = new WatchClient(getActivity());
        watchClient.setConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                if (!prepared) {
                    watchClient.startWatch(watchInfo, WatchDialog.this);
                    Analytics.event(Analytics.Category.CONTENT, Analytics.Event.DOWNLOAD_START);
                }
            }

            @Override
            public void onClientDisconnected() {

            }
        });
        if (watchInfo != null) {
            ((PopcornApplication) getActivity().getApplication()).getShareUseCase().loadVideoShare(watchInfo.imdb);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && KeyEvent.ACTION_UP == event.getAction()) {
                    close();
                    return true;
                }
                return false;
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_watch, container, false);
        final PopcornApplication app = (PopcornApplication) getActivity().getApplication();
        final String[] providers = app.getConfigUseCase().getConfig().getVpnConfig().getProviders();
        if (providers != null && providers.length > 0 && !app.getVpnUseCase().isVpnConnected()) {
            vpnAlertIcon = (ImageView) view.findViewById(R.id.vpn_alert_icon);
            final VpnConfig.Notice notice = app.getConfigUseCase().getConfig().getVpnConfig().getNotice();
            if (!TextUtils.isEmpty(notice.getIconUrl())) {
                Picasso.with(getContext()).load(notice.getIconUrl()).into(vpnAlertIcon);
            }
            ((TextView) view.findViewById(R.id.vpn_security_recommendation_title)).setText(Html.fromHtml(notice.getTitle()));
            ((TextView) view.findViewById(R.id.vpn_security_recommendation_message)).setText(Html.fromHtml(notice.getText()));
            final Button turnOnVpn = (Button) view.findViewById(R.id.turn_on_vpn);
            turnOnVpn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((IViewRouter) getActivity().getApplication()).onShowView(IVpnView.class);
                    close();
                }
            });
        } else {
            view.findViewById(R.id.vpn_security_recommendation).setVisibility(View.GONE);
        }

        popcorn = (ImageView) view.findViewById(R.id.prepare_popcorn);

        statusLayout = (ViewGroup) view.findViewById(R.id.prepare_status_layout);
        statusProgress = (ProgressBar) statusLayout.findViewById(R.id.prepare_status_progress);
        statusInfo = (TextView) statusLayout.findViewById(R.id.prepare_status_info);
        statusPercent = (TextView) statusLayout.findViewById(R.id.prepare_status_percent);
        statusSeedsPeers = (TextView) statusLayout.findViewById(R.id.prepare_status_seeds_peers);

        popcorn.setVisibility(View.GONE);
        updateStatus(0, 0, 0, 0, "0B/s");

        ImageButton closeBtn = (ImageButton) view.findViewById(R.id.prepare_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView image = (ImageView) view.findViewById(R.id.image);
        if (!TextUtils.isEmpty(watchInfo.posterUrl)) {
            Picasso.with(view.getContext()).load(watchInfo.posterUrl).into(image);
        } else {
            image.setImageResource(R.drawable.poster);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        watchClient.bind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (vpnAlertIcon != null) {
            vpnAlertIcon.startAnimation(alertIconAnimation);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vpnAlertIcon != null) {
            vpnAlertIcon.clearAnimation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        watchClient.removeWatchListener(WatchDialog.this);
        watchClient.unbind();
    }

    @Override
    public void onError(WatchException exception) {
        if (exception != null) {
            if (WatchState.LOAD_METADATA == exception.getState()) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.error_metadata, Toast.LENGTH_SHORT).show();
                }
            }
            Logger.error("State: " + exception.getState() + ", error: " + exception.getMessage());
        }
        stopAnim();
        dismiss();
    }

    @Override
    public void onMetadataLoad() {
        startAnim();
        loadedSubtitlesPath = null;
    }

    @Override
    public void onSubtitlesLoaded(String subPath) {
        loadedSubtitlesPath = subPath;
        Logger.debug("WatchDialog<onSubtitlesLoaded>: " + subPath);
    }

    @Override
    public void onDownloadStarted(String torrent) {
        Logger.debug("WatchDialog<onDownloadStarted>: " + torrent);
        stopAnim();
        prepared = false;
        if (statusLayout != null) {
            updateStatus(0, 0, 0, 0, "0B/s");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            final long length = data.getLongExtra(MobilePlayerActivity.EXTRA_LENGTH, 0);
            final long position = data.getLongExtra(MobilePlayerActivity.EXTRA_POSITION, 0);
            if (getActivity() != null) {
                final IShareUseCase shareUseCase = ((PopcornApplication) getActivity().getApplication()).getShareUseCase();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (watchInfo != null) {
                            shareUseCase.share(watchInfo.imdb, length, position);
                        }
                    }
                }, 1000);
            }
            close();
        }
    }

    @Override
    public void onVideoPrepared(String filePath) {
        Logger.debug("WatchDialog<onPrepareWatchCompleted>: " + filePath);
        if (getActivity() == null || getActivity().getBaseContext() == null) {
            return;
        }
        if (watchInfo != null) {
            if (watchInfo.isDownloads()) {
                Downloads.readyToWatch(getContext(), watchInfo.downloadsId);
            }
            if (!History.isWatched(getContext(), watchInfo.imdb, watchInfo.episode, watchInfo.season)) {
                History.insert(getContext(), watchInfo);
            }
        }
        Analytics.event(Analytics.Category.CONTENT, Analytics.Event.WATCHING_START);
        prepared = true;
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.loadedSubtitlesPath = loadedSubtitlesPath;
        PlayerBaseActivity.startForResult(
                WatchDialog.this,
                new Intent(getActivity(), VLCPlayerActivity.class),
                111,
                Uri.parse("file://" + filePath),
                playerInfo
        );
    }

    @Override
    public void onUpdateProgress(WatchProgress progress) {
        updateStatus(progress.total, progress.value, progress.seeds, progress.peers, progress.speed);
    }

    @Override
    public void onDownloadFinished() {
        Logger.debug("WatchDialog<onDownloadFinished>");
    }

    @Override
    public void onBufferingFinished() {

    }

    public void show(FragmentManager fm, WatchInfo watchInfo, String tag) {
        if (!isAdded()) {
            this.watchInfo = watchInfo;
            super.show(fm, tag);
        }
    }

    public void close() {
        watchClient.stopWatch();
        dismiss();
    }

    private void startAnim() {
        statusLayout.setVisibility(View.GONE);
        if (popcorn != null) {
            popcorn.setVisibility(View.VISIBLE);
            if (popcornAnimation != null) {
                popcorn.startAnimation(popcornAnimation);
            }
        }
    }

    private void stopAnim() {
        if (popcorn != null) {
            popcorn.setVisibility(View.GONE);
            popcorn.clearAnimation();
        }
    }

    private void updateStatus(int max, int progress, int seeds, int peers, String info) {
        popcorn.setVisibility(View.GONE);
        statusLayout.setVisibility(View.VISIBLE);
        statusProgress.setMax(max);
        statusProgress.setProgress(progress);
        statusInfo.setText(info);
        statusPercent.setText(String.format(Locale.ENGLISH, "%d%%", max > 0 ? (100 * progress / max) : 0));
        statusSeedsPeers.setText(String.format(Locale.ENGLISH, "%d/%d", seeds, peers));
    }
}