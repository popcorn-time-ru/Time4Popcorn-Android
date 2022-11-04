package se.popcorn_time.mobile.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import se.popcorn_time.mobile.R;
import se.popcorn_time.model.updater.Update;
import se.popcorn_time.ui.updater.IUpdateView;

public final class UpdateDialog extends DialogFragment implements IUpdateView {

    private static final String KEY_URI = "uri";
    private static final String KEY_VERSION = "app-version";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.application_name);
        builder.setMessage(getString(R.string.available_new_version)
                + " " + getArguments().getString(KEY_VERSION) + "\n"
                + getString(R.string.update_now));
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Uri uri = Uri.parse(getArguments().getString(KEY_URI));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.later, null);
        return builder.create();
    }

    public static boolean show(@NonNull FragmentManager manager, @NonNull String tag, @NonNull Update update) {
        DialogFragment fragment = (DialogFragment) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new UpdateDialog();
        }
        if (!fragment.isAdded()) {
            final Bundle data = new Bundle(2);
            data.putString(KEY_URI, update.getUrl());
            data.putString(KEY_VERSION, update.getVersion());
            fragment.setArguments(data);
            fragment.show(manager, tag);
            return true;
        }
        return false;
    }
}
