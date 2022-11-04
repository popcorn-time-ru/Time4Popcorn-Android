package se.popcorn_time.mobile.ui.locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class LocaleFragment extends Fragment implements LocaleListener {

    protected LocaleHelper mLocaleHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLocaleHelper = new LocaleHelper(getActivity(), LocaleFragment.this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocaleHelper.checkLanguage();
    }

    @Override
    public void updateLocaleText() {

    }
}