package com.player.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.player.MobilePlayerActivity;
import com.player.R;
import com.player.base.NumberPickerDialogListener;

import java.util.ArrayList;
import java.util.List;

public class NumberPickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener{

    private String title;
    private int checkedItem;
    private int minValue;
    private int maxValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.number_picker_dialog_layout, null);
        builder.setView(v);
        builder.setTitle(title);
        builder.setNegativeButton(android.R.string.cancel, null);

        final NumberPicker np = (NumberPicker) v.findViewById(R.id.subtitle_settings_number_picker);
        List<String> displayValues = new ArrayList<>();
        float j = minValue;
        for(int i = minValue * (int)(1f/MobilePlayerActivity.SUBTITLE_SHIFT_STEP_IN_SEC);
            i < maxValue * (int)(1f/MobilePlayerActivity.SUBTITLE_SHIFT_STEP_IN_SEC) + 2; i++) {
            displayValues.add(Float.toString(j));
            j += MobilePlayerActivity.SUBTITLE_SHIFT_STEP_IN_SEC;
        }
        np.setDisplayedValues(displayValues.toArray(new String[displayValues.size()]));

        if(minValue < 0) {
            np.setMinValue(0);
            np.setMaxValue(maxValue * (int)(1f/MobilePlayerActivity.SUBTITLE_SHIFT_STEP_IN_SEC) + Math.abs(minValue) * (int)(1f/MobilePlayerActivity.SUBTITLE_SHIFT_STEP_IN_SEC));

        } else {
            np.setMinValue(minValue);
            np.setMaxValue(maxValue);
        }
        np.setValue(checkedItem);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NumberPickerDialogListener activity = (NumberPickerDialogListener) getActivity();
                activity.onReturnNumberPickerValue(title, np.getValue());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }


    public void show(FragmentManager fm, String title, int maxValue, int minValue, int checkedItem) {
        if (!isAdded() && !fm.isDestroyed()) {
            this.title = title;
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.checkedItem = checkedItem >= 0 && checkedItem <= maxValue * (int)(1f/MobilePlayerActivity.SUBTITLE_SHIFT_STEP_IN_SEC) * 2 + 1 ? checkedItem : -1;
            this.show(fm, "subtitle_settings_dialog_" + hashCode());
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        checkedItem = newVal;
    }
}