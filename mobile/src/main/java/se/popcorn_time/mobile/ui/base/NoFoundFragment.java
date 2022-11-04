package se.popcorn_time.mobile.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.locale.LocaleFragment;

public class NoFoundFragment extends LocaleFragment {

    public static final String LABEL_ID_KEY = "label-id";

    private int labelId;
    private TextView label;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(LABEL_ID_KEY)) {
            labelId = getArguments().getInt(LABEL_ID_KEY);
        } else {
            labelId = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_found, container, false);
        label = (TextView) view;
        updateLocaleText();
        return view;
    }

    @Override
    public void updateLocaleText() {
        super.updateLocaleText();
        if (labelId > 0) {
            label.setText(labelId);
        } else {
            label.setText("No found");
        }
    }

    public static Bundle createArguments(int labelId) {
        Bundle args = new Bundle();
        args.putInt(LABEL_ID_KEY, labelId);
        return args;
    }
}