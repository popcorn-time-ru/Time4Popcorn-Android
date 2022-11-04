package se.popcorn_time.mobile.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import se.popcorn_time.mobile.R;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.torrent.TorrentState;

public class DownloadsMoreDialog extends DialogFragment {

    public interface DownloadsMoreListener {

        void onDownloadsPause(DownloadInfo info);

        void onDownloadsResume(DownloadInfo info);

        void onDownloadsRemove(DownloadInfo info);

        void onDownloadsRetry(DownloadInfo info);
    }

    private DownloadInfo info;
    private DownloadsMoreListener listener;
    private String[] options;

    public DownloadsMoreDialog() {

    }

    public void setInfo(DownloadInfo info) {
        this.info = info;
    }

    public void setListener(DownloadsMoreListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        if (TorrentState.FINISHED == info.state) {
            options = new String[1];
            options[0] = getString(R.string.remove);
        } else if (TorrentState.PAUSED == info.state) {
            options = new String[2];
            options[0] = getString(R.string.resume);
            options[1] = getString(R.string.remove);
        } else if (TorrentState.DOWNLOADING == info.state) {
            options = new String[2];
            options[0] = getString(R.string.pause);
            options[1] = getString(R.string.remove);
        } else if (TorrentState.ERROR == info.state) {
            options = new String[2];
            options[0] = getString(R.string.retry);
            options[1] = getString(R.string.remove);
        } else {
            options = null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(info.title);
        if (options != null) {
            builder.setItems(options, optionsListener);
        }
        builder.setNeutralButton(android.R.string.cancel, null);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void pause() {
        if (listener != null) {
            listener.onDownloadsPause(info);
        }
    }

    private void resume() {
        if (listener != null) {
            listener.onDownloadsResume(info);
        }
    }

    private void remove() {
        if (listener != null) {
            listener.onDownloadsRemove(info);
        }
    }

    private void retry() {
        if (listener != null) {
            listener.onDownloadsRetry(info);
        }
    }

    private DialogInterface.OnClickListener optionsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (TorrentState.FINISHED == info.state) {
                if (0 == which) {
                    remove();
                }
            } else if (TorrentState.PAUSED == info.state) {
                if (0 == which) {
                    resume();
                } else if (1 == which) {
                    remove();
                }
            } else if (TorrentState.DOWNLOADING == info.state) {
                if (0 == which) {
                    pause();
                } else if (1 == which) {
                    remove();
                }
            } else if (TorrentState.ERROR == info.state) {
                if (0 == which) {
                    retry();
                } else if (1 == which) {
                    remove();
                }
            }
        }
    };
}