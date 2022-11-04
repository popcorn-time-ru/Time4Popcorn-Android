package se.popcorn_time.mobile.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.locale.LocaleFragment;

public class NoConnectionFragment extends LocaleFragment {

    private TextView label;
    private Button retry;
    private ContentLoadListener mLoadListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_connection, container, false);
        label = (TextView) view.findViewById(R.id.no_connection_label);
        retry = (Button) view.findViewById(R.id.no_connection_retry);
        retry.setOnClickListener(retryListener);
        updateLocaleText();
        return view;
    }

    @Override
    public void updateLocaleText() {
        super.updateLocaleText();
        label.setText(R.string.no_connection);
        retry.setText(R.string.retry);
    }

    public void setLoadListener(ContentLoadListener listener) {
        mLoadListener = listener;
    }

    private OnClickListener retryListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mLoadListener != null) {
                mLoadListener.retryLoad();
            }
        }
    };
}