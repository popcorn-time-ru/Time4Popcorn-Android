package se.popcorn_time.mobile.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.model.WatchInfo;
import se.popcorn_time.base.model.video.Anime;
import se.popcorn_time.base.model.video.Cinema;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.base.torrent.TorrentState;
import se.popcorn_time.base.torrent.client.DownloadsClient;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.dialog.OptionDialog;
import se.popcorn_time.mobile.ui.dialog.WatchDialog;

public class DownloadsAdapter extends CursorAdapter {

    private FragmentActivity activity;
    private DownloadsClient downloadsClient;

    private String seasonText;
    private String episodeText;
    private String finishedText;
    private String pausedText;
    private String errorText;
    private String checkingDataText;
    private String watchBtnText;

    public DownloadsAdapter(FragmentActivity activity, DownloadsClient downloadsClient) {
        super(activity, null, false);
        this.activity = activity;
        this.downloadsClient = downloadsClient;
    }

    public void updateLocaleText() {
        seasonText = activity.getString(R.string.season);
        episodeText = activity.getString(R.string.episode);
        finishedText = activity.getString(R.string.finished);
        pausedText = activity.getString(R.string.paused);
        errorText = activity.getString(R.string.error_metadata);
        checkingDataText = activity.getString(R.string.checking_data);
        watchBtnText = activity.getString(R.string.watch_it_now);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DownloadView downloadView = (DownloadView) view;
        final DownloadInfo info = new DownloadInfo();
        Downloads.populate(info, cursor);
        downloadView.populate(info);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new DownloadView(context);
    }

    private void showRemoveDialog(DownloadInfo info) {
        final String tag = "downloads_remove_dialog";
        OptionDialog dialog = (OptionDialog) activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (dialog == null) {
            dialog = new OptionDialog();
        }
        if (!dialog.isAdded()) {
            dialog.setListener(new RemoveListener(info));
            dialog.setArguments(OptionDialog.createArguments(activity.getString(R.string.remove), activity.getString(R.string.downloads_remove_msg)));
            dialog.show(activity.getSupportFragmentManager(), tag);
        }
    }

    private final class RemoveListener extends OptionDialog.SimpleOptionListener {

        private DownloadInfo info;

        public RemoveListener(DownloadInfo info) {
            this.info = info;
        }

        @Override
        public boolean positiveShow() {
            return true;
        }

        @Override
        public String positiveButtonText() {
            return activity.getString(android.R.string.ok);
        }

        @Override
        public void positiveAction() {
            downloadsClient.downloadsRemove(info);
        }

        @Override
        public boolean negativeShow() {
            return true;
        }

        @Override
        public String negativeButtonText() {
            return activity.getString(android.R.string.cancel);
        }
    }

    private class DownloadView extends LinearLayout {

        private final int STATE_WHAT = 1;
        private final int HANDLER_DELAY = 1000;

        private ImageView poster;
        private ImageButton posterAction;
        private TextView title;
        private TextView size;
        private TextView summary;
        private ProgressBar progress;
        private TextView status;
        private TextView progressPercentage;
        private Button watchNow;
        private ImageButton download;
        private ImageButton pause;
        private ImageButton remove;

        private DownloadInfo info;
        private int fileSizeMB;
        private int progressSizeMB;
        private String torrentFile;
        private String dots;

        public DownloadView(Context context) {
            super(context);
            init();
        }

        public DownloadView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DownloadView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            stateHandler.sendEmptyMessage(STATE_WHAT);
        }

        @Override
        protected void onDetachedFromWindow() {
            stateHandler.removeMessages(STATE_WHAT);
            super.onDetachedFromWindow();
        }

        public void populate(DownloadInfo info) {
            this.info = info;
            torrentFile = downloadsClient.getAvailableTorrentFile(info);
            dots = "";
            fileSizeMB = (int) (info.size / StorageUtil.SIZE_MB);

            Picasso.with(getContext()).load(info.posterUrl).placeholder(R.drawable.poster).into(poster);
            title.setText(info.title);
            size.setText(StorageUtil.getSizeText(info.size));
            String summaryTxt;
            switch (info.type) {
                case Cinema.TYPE_MOVIES:
                case Anime.TYPE_MOVIES:
                    summaryTxt = "";
                    break;
                case Cinema.TYPE_TV_SHOWS:
                case Anime.TYPE_TV_SHOWS:
                    summaryTxt = seasonText + " " + info.season + ", " + episodeText + " " + info.episode;
                    break;
                default:
                    summaryTxt = "Unknown type: " + info.type;
                    break;
            }
            summary.setText(summaryTxt);
            watchNow.setText(watchBtnText);
            watchNow.setOnClickListener(watchListener);
        }

        private void init() {
            inflate(getContext(), R.layout.view_item_download, DownloadView.this);
            poster = (ImageView) findViewById(R.id.poster);
            poster.setOnClickListener(posterActionListener);
            posterAction = (ImageButton) findViewById(R.id.btn_poster_action);
            posterAction.setOnClickListener(posterActionListener);
            title = (TextView) findViewById(R.id.title);
            size = (TextView) findViewById(R.id.size);
            summary = (TextView) findViewById(R.id.summary);
            progress = (ProgressBar) findViewById(R.id.progress);
            status = (TextView) findViewById(R.id.status);
            progressPercentage = (TextView) findViewById(R.id.percent);
            watchNow = (Button) findViewById(R.id.watch);
            download = (ImageButton) findViewById(R.id.download);
            download.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    downloadsClient.downloadsResume(info);
                }
            });
            pause = (ImageButton) findViewById(R.id.pause);
            pause.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    downloadsClient.downloadsPause(info);
                }
            });
            remove = (ImageButton) findViewById(R.id.remove);
            remove.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showRemoveDialog(info);
                }
            });
        }

        private void checkState() {
            if (TextUtils.isEmpty(torrentFile)) {
                torrentFile = downloadsClient.getAvailableTorrentFile(info);
            }
            updateProgress();
            if (TorrentState.DOWNLOADING == info.state) {
                if (TextUtils.isEmpty(torrentFile)) {
                    progress(-1);
                } else {
                    int state = downloadsClient.getTorrentState(torrentFile);
                    if (TorrentState.FINISHED == state || TorrentState.SEEDING == state) {
                        if (fileSizeMB > 0 && fileSizeMB <= progressSizeMB) {
                            info.state = TorrentState.FINISHED;
                            Downloads.update(activity, info);
                            progress(TorrentState.FINISHED);
                        } else {
                            progress(state);
                        }
                    } else {
                        progress(state);
                    }
                }
            } else if (TorrentState.FINISHED == info.state) {
                progress(TorrentState.FINISHED);
            } else if (TorrentState.PAUSED == info.state) {
                if (!TextUtils.isEmpty(torrentFile)) {
                    int state = downloadsClient.getTorrentState(torrentFile);
                    if (TorrentState.FINISHED == state || TorrentState.SEEDING == state) {
                        if (fileSizeMB > 0 && fileSizeMB <= progressSizeMB) {
                            info.state = TorrentState.FINISHED;
                            Downloads.update(activity, info);
                            progress(TorrentState.FINISHED);
                            return;
                        }
                    }
                }
                progress(TorrentState.PAUSED);
            } else if (TorrentState.ERROR == info.state) {
                progress(TorrentState.ERROR);
            } else {
                progress(-1);
            }
        }

        private void progress(int state) {
            if (TorrentState.DOWNLOADING == state) {
                posterAction.setVisibility(View.VISIBLE);
                posterAction.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_item_download_resume));
                status.setText(downloadsClient.getTorrentSpeed(torrentFile));
                watchNow.setActivated(info.readyToWatch);
                download.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            } else if (TorrentState.FINISHED == state) {
                posterAction.setVisibility(View.VISIBLE);
                posterAction.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_item_download_play));
                status.setText(finishedText);
                watchNow.setActivated(true);
                download.setVisibility(View.GONE);
                pause.setVisibility(View.GONE);
            } else if (TorrentState.PAUSED == state) {
                posterAction.setVisibility(View.VISIBLE);
                posterAction.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_item_download_pause));
                status.setText(pausedText);
                watchNow.setActivated(info.readyToWatch);
                download.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            } else if (TorrentState.ERROR == state) {
                posterAction.setVisibility(View.GONE);
                status.setText(errorText);
                watchNow.setActivated(false);
                download.setVisibility(View.GONE);
                pause.setVisibility(View.GONE);
            } else {
                posterAction.setVisibility(View.GONE);
                status.setText(checkingDataText + dots);
                watchNow.setActivated(false);
                download.setVisibility(View.GONE);
                pause.setVisibility(View.GONE);
                if (dots.length() >= 3) {
                    dots = "";
                } else {
                    dots += ".";
                }
            }
        }

        private void updateProgress() {
            if (TextUtils.isEmpty(torrentFile)) {
                progressSizeMB = 0;
            } else {
                progressSizeMB = downloadsClient.getDownloadSizeMb(torrentFile);
            }
            if (progressSizeMB > fileSizeMB) {
                fileSizeMB = progressSizeMB;
            }
            progress.setMax(fileSizeMB);
            progress.setProgress(progressSizeMB);
            int percentage = (int) (((double) progressSizeMB / (double) fileSizeMB) * 100);
            progressPercentage.setText(percentage + "%");
        }

        private void showWatchDialog(@NonNull WatchInfo watchInfo) {
            final String tag = "watch_view";
            WatchDialog dialog = (WatchDialog) activity.getSupportFragmentManager().findFragmentByTag(tag);
            if (dialog == null) {
                dialog = new WatchDialog();
            }
            if (!dialog.isAdded()) {
                dialog.show(activity.getSupportFragmentManager(), watchInfo, tag);
            }
        }

        private OnClickListener posterActionListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TorrentState.DOWNLOADING == info.state) {
                    showWatchDialog(new WatchInfo(info));
                } else if (TorrentState.PAUSED == info.state) {
                    downloadsClient.downloadsResume(info);
                } else if (TorrentState.FINISHED == info.state) {
                    showWatchDialog(new WatchInfo(info));
                }
            }
        };

        private OnClickListener watchListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                showWatchDialog(new WatchInfo(info));
            }
        };

        private Handler stateHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                checkState();
                stateHandler.sendEmptyMessageDelayed(STATE_WHAT, HANDLER_DELAY);
                return true;
            }
        });
    }
}
