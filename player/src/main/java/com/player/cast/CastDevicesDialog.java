package com.player.cast;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.player.R;

public class CastDevicesDialog<Device> extends DialogFragment {

    public interface CastDevicesListener<Device> {
        void onDeviceSelected(@NonNull Device device);
    }

    private final String KEY_ICON = "icon";
    private final String KEY_TITLE = "title";

    protected CastDevicesItemAdapter<Device> mCastDevicesItemAdapter;

    private int icon;
    private int title;
    private CastDevicesListener<Device> castDevicesListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        if (savedInstanceState != null) {
            icon = savedInstanceState.getInt(KEY_ICON, R.drawable.ic_mr_button_disconnected_dark);
            title = savedInstanceState.getInt(KEY_TITLE, R.string.mr_chooser_title);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ICON, icon);
        outState.putInt(KEY_TITLE, title);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setSingleChoiceItems(mCastDevicesItemAdapter, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (castDevicesListener != null && mCastDevicesItemAdapter != null) {
                    castDevicesListener.onDeviceSelected(mCastDevicesItemAdapter.getItem(which).getDevice());
                }
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, null);
        return builder.create();
    }

    public final void show(@NonNull FragmentManager manager, @Nullable CastDevicesListener<Device> listener, String tag) {
        this.show(manager, R.drawable.ic_mr_button_disconnected_dark, R.string.mr_chooser_title, listener, tag);
    }

    public final void show(@NonNull FragmentManager manager, @DrawableRes int icon, @StringRes int title, @Nullable CastDevicesListener<Device> listener, String tag) {
        if (!isAdded() && !manager.isDestroyed()) {
            this.icon = icon;
            this.title = title;
            this.castDevicesListener = listener;
            this.show(manager, tag);
        }
    }
}