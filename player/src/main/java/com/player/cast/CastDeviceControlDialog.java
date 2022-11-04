package com.player.cast;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.player.R;

public abstract class CastDeviceControlDialog<Device> extends DialogFragment {

    private CastDeviceItem<Device> castDeviceItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(true);
        builder.setTitle(castDeviceItem.getDeviceName());

        View view = createView(castDeviceItem.getDevice());
        if (view != null) {
            builder.setView(view);
        }

        builder.setPositiveButton(R.string.mr_controller_disconnect, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (castDeviceItem != null) {
                    onDeviceDisconnect(castDeviceItem.getDevice());
                }
            }
        });

        return builder.create();
    }

    public final void show(@NonNull FragmentManager fm, @NonNull CastDeviceItem<Device> castDeviceItem, String tag) {
        if (!isAdded() && !fm.isDestroyed()) {
            this.castDeviceItem = castDeviceItem;
            this.show(fm, tag);
        }
    }

    @Nullable
    protected View createView(@NonNull Device device) {
        return null;
    }

    protected abstract void onDeviceDisconnect(@NonNull Device device);
}