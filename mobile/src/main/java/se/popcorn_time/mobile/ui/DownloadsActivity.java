package se.popcorn_time.mobile.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import se.popcorn_time.base.database.tables.Downloads;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.torrent.client.DownloadsClient;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.adapter.DownloadsAdapter;
import se.popcorn_time.mobile.ui.dialog.FirebaseMessagingDialog;
import se.popcorn_time.mobile.ui.dialog.OptionDialog;
import se.popcorn_time.mobile.ui.dialog.ShareDialog;
import se.popcorn_time.model.messaging.IMessagingData;
import se.popcorn_time.model.messaging.IMessagingUseCase;
import se.popcorn_time.model.share.IShareData;
import se.popcorn_time.model.share.IShareUseCase;

public class DownloadsActivity extends UpdateActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        IShareUseCase.Observer,
        IMessagingUseCase.Observer {

    private static final int LOADER_ID = 120;
    public static final String VIDEO_URL = "video-url";

    private boolean init;
    private DownloadsClient downloadsClient;
    private DownloadsAdapter mDownloadsAdapter;
    private ListView downloadsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init = getIntent().hasExtra(VIDEO_URL);
        downloadsClient = new DownloadsClient(DownloadsActivity.this);

        setContentView(R.layout.view_downloads);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.downloads);

        mDownloadsAdapter = new DownloadsAdapter(DownloadsActivity.this, downloadsClient);
        downloadsList = (ListView) findViewById(R.id.downloads_list);
        downloadsList.setAdapter(mDownloadsAdapter);

        mDownloadsAdapter.updateLocaleText();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadsClient.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        downloadsClient.unbind();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.downloads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.downloads_resume_all:
                downloadsClient.downloadsResumeAll();
                return true;
            case R.id.downloads_pause_all:
                downloadsClient.downloadsPauseAll();
                return true;
            case R.id.downloads_remove_all:
                showRemoveAllDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Downloads.CONTENT_URI, null, null, null, Downloads._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mDownloadsAdapter.swapCursor(cursor);
        findViewById(R.id.empty).setVisibility(cursor != null && cursor.getCount() > 0 ? View.GONE : View.VISIBLE);
        if (cursor != null && init) {
            init = false;
            int position = 0;
            String url = getIntent().getStringExtra(VIDEO_URL);
            final DownloadInfo downloadInfo = new DownloadInfo();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                Downloads.populate(downloadInfo, cursor);
                if (url.equals(downloadInfo.torrentUrl)) {
                    position = i;
                    break;
                }
            }
            if (0 != position) {
                setSelection(position);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDownloadsAdapter.swapCursor(null);
    }

    private void setSelection(final int position) {
        downloadsList.post(new Runnable() {
            @Override
            public void run() {
                downloadsList.setSelection(position);
            }
        });
    }

    private void showRemoveAllDialog() {
        final String tag = "downloads_remove_all_dialog";
        OptionDialog dialog = (OptionDialog) getSupportFragmentManager().findFragmentByTag(tag);
        if (dialog == null) {
            dialog = new OptionDialog();
        }
        if (!dialog.isAdded()) {
            dialog.setListener(new OptionDialog.SimpleOptionListener() {

                @Override
                public boolean positiveShow() {
                    return true;
                }

                @Override
                public String positiveButtonText() {
                    return getString(android.R.string.ok);
                }

                @Override
                public void positiveAction() {
                    downloadsClient.downloadsRemoveAll();
                }

                @Override
                public boolean negativeShow() {
                    return true;
                }

                @Override
                public String negativeButtonText() {
                    return getString(android.R.string.cancel);
                }
            });
            dialog.setArguments(OptionDialog.createArguments(getString(R.string.remove_all), getString(R.string.downloads_remove_msg)));
            dialog.show(getSupportFragmentManager(), tag);
        }
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, DownloadsActivity.class));
    }

    @Override
    public void onShowShare(@NonNull IShareData data) {
        ShareDialog.open(getSupportFragmentManager(), "share_dialog");
    }

    @Override
    public void onShowMessagingDialog(@NonNull IMessagingData data) {
        FirebaseMessagingDialog.show(getSupportFragmentManager(), data, "firebase_messaging_dialog");
    }
}