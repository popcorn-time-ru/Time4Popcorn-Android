package se.popcorn_time;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberPickerPreference extends DialogPreference {

    private int minValue;
    private int maxValue;
    private int value;

    private boolean valueSet;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerPreference(Context context) {
        super(context);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(value) : (int) defaultValue);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        final boolean changed = this.value != value;
        if (changed || !valueSet) {
            this.value = value;
            valueSet = true;
            persistInt(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    public static final class Dialog extends PreferenceDialogFragmentCompat {

        private static final String SAVE_MIN_VALUE = "NumberPickerPreference.Dialog.minValue";
        private static final String SAVE_MAX_VALUE = "NumberPickerPreference.Dialog.maxValue";
        private static final String SAVE_VALUE = "NumberPickerPreference.Dialog.value";

        private int minValue;
        private int maxValue;
        private int value;

        private NumberPicker numberPicker;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState == null) {
                minValue = getNumberPickerPreference().getMinValue();
                maxValue = getNumberPickerPreference().getMaxValue();
                value = getNumberPickerPreference().getValue();
            } else {
                minValue = savedInstanceState.getInt(SAVE_MIN_VALUE);
                maxValue = savedInstanceState.getInt(SAVE_MAX_VALUE);
                value = savedInstanceState.getInt(SAVE_VALUE);
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(SAVE_MIN_VALUE, minValue);
            outState.putInt(SAVE_MAX_VALUE, maxValue);
            outState.putInt(SAVE_VALUE, value);
        }

        @Override
        protected View onCreateDialogView(Context context) {
            numberPicker = new NumberPicker(context);
            numberPicker.setMinValue(minValue);
            numberPicker.setMaxValue(maxValue);
            numberPicker.setValue(value);
            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    value = newVal;
                }
            });
            return numberPicker;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            super.onClick(dialog, which);
            if (DialogInterface.BUTTON_POSITIVE == which) {
                for (int i = 0; i < numberPicker.getChildCount(); i++) {
                    final View view = numberPicker.getChildAt(i);
                    if (view instanceof TextView) {
                        view.clearFocus();
                        break;
                    }
                }
            }
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                if (getNumberPickerPreference().callChangeListener(value)) {
                    getNumberPickerPreference().setValue(value);
                }
            }
        }

        private NumberPickerPreference getNumberPickerPreference() {
            return (NumberPickerPreference) getPreference();
        }

        public static Dialog newInstance(String key) {
            final Dialog fragment = new Dialog();
            final Bundle b = new Bundle(1);
            b.putString(ARG_KEY, key);
            fragment.setArguments(b);
            return fragment;
        }
    }
}
